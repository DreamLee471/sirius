package org.sirius.core.pull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.sirius.core.eventbus.ConfigEvent;
import org.sirius.core.eventbus.EventBus;
import org.sirius.core.eventbus.EventHandler;
import org.sirius.core.eventbus.filters.TrueFilter;
import org.sirius.core.store.impl.StandardTransientStore;
import org.sirius.domain.Config;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @author liwei
 * 2016年11月27日
 */
public class PullCore {
	
	private static final Map<PullKey,List<ConfigPullTask>> LONGPULLS = new ConcurrentHashMap<>(1024);
	
	private static ScheduledExecutorService scheduleService;
	
	private static final Map<String,Config> MD5_STORE_MAP = new ConcurrentHashMap<String, Config>();
	
	private static StandardTransientStore store = new StandardTransientStore(null);
	
	static{
		EventBus.addEventHandler(new TrueFilter(), new ConfigPullHandler());
		
		scheduleService = Executors.newScheduledThreadPool(1);
	}
	
	
	
	public static void store(Config config){
		store.store(config);
	}
	
	
	public static void addLongPull(HttpServletRequest request,HttpServletResponse resp){
		
		String namespace = request.getParameter("namespace");
		String name = request.getParameter("name");
		String md5 = request.getParameter(Constansts.MD5_KEY);
		
		String timeout = request.getHeader(Constansts.TIMEOUT_KEY);
		if(StringUtils.isEmpty(timeout)){
			timeout = "30";
		}
		
		if(!StringUtils.isEmpty(md5)){//无md5值，表示第一次请求
			Config config = store.get(namespace, name);
			if(config != null){
				try {
					resp.getOutputStream().write(JSON.toJSONString(config).getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}else{
				PullKey key = new PullKey(md5, PullKeyType.MD5);
				scheduleService.schedule(new ConfigPullTask(request.startAsync(), key, Long.valueOf(timeout)), 0, TimeUnit.MILLISECONDS);
			}
		}else{
			PullKey key = new PullKey(namespace+"_"+name, PullKeyType.NAMESPACE_NAME);
			scheduleService.schedule(new ConfigPullTask(request.startAsync(), key, Long.valueOf(timeout)), 0, TimeUnit.MILLISECONDS);
		}
		
	}
	
	public static final class ConfigPullHandler implements EventHandler<Config>{

		@Override
		public void handler(ConfigEvent<Config> e) {
			Config config = e.getSource();
			List<ConfigPullTask> tasks = filterContext(config);
			if(tasks == null) return;
			switch(e.getEventType()){
				case ADD:
				case MODIFIED:
					MD5_STORE_MAP.put(config.getMd5(), config);
					try {
						for(ConfigPullTask task : tasks){
							task.send(config);
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				case REMOVE:
					MD5_STORE_MAP.remove(config.getMd5());
					try {
						for(ConfigPullTask task : tasks){
							task.send(config);
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
			}
		}
		
		private List<ConfigPullTask> filterContext(Config config){
			List<ConfigPullTask> tasks = LONGPULLS.remove(new PullKey(config.getMd5(), PullKeyType.MD5));
			if(tasks == null){
				tasks = LONGPULLS.remove(new PullKey(config.getNamespace()+"_"+config.getName(), PullKeyType.NAMESPACE_NAME));
			}
			return tasks;
		}
		
	}
	
	
	
	public static final class ConfigPullTask implements Runnable{
		
		private AsyncContext asyncContext;
		
		private PullKey key;
		
		private long timeout;
		
		private String uuid;
		
		private Future<Void> timeoutTask;
		
		public ConfigPullTask(AsyncContext asyncContext,PullKey key, long timeout) {
			super();
			this.asyncContext = asyncContext;
			this.key = key;
			this.timeout = timeout;
			this.uuid = UUID.randomUUID().toString();//每个任务生成唯一的uuid
		}
		
		
		public void send(Config config) throws IOException{
			if(timeoutTask != null){
				timeoutTask.cancel(true);
			}
			asyncContext.getResponse().getOutputStream().write(JSON.toJSONString(config).getBytes());
			asyncContext.complete();
		}


		@Override
		public void run() {
			asyncContext.setTimeout(0);
			scheduleService.schedule(new Runnable() {
				
				@Override
				public void run() {
					List<ConfigPullTask> tasks = LONGPULLS.get(key);
					if(tasks != null){
						
						for(Iterator<ConfigPullTask> iter = tasks.iterator();iter.hasNext();) {
							if(iter.next().uuid == uuid){
								iter.remove();
							}
						}
					}
					asyncContext.complete();
				}
			}, timeout, TimeUnit.SECONDS);
			if(!LONGPULLS.containsKey(key)){
				LONGPULLS.put(key, new ArrayList<>());
			}
			LONGPULLS.get(key).add(this);
		}
		
	}
	
	
	public static void destory(){
		scheduleService.shutdownNow();
	}
	

}
