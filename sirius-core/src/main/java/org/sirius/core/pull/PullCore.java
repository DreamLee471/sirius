package org.sirius.core.pull;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sirius.core.eventbus.ConfigEvent;
import org.sirius.core.eventbus.EventBus;
import org.sirius.core.eventbus.EventHandler;
import org.sirius.core.eventbus.filters.TrueFilter;
import org.sirius.core.store.impl.StandardTransientStore;
import org.sirius.domain.Config;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;

/**
 * 
 * @author liwei
 * 2016年11月27日
 */
public class PullCore {
	
	private static final Map<PullKey,ConfigPullTask> LONGPULLS = new ConcurrentHashMap<>(1024);
	
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
			ConfigPullTask task = filterContext(config);
			if(task == null) return;
			switch(e.getEventType()){
				case ADD:
				case MODIFIED:
					MD5_STORE_MAP.put(config.getMd5(), config);
					try {
						task.send(config);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				case REMOVE:
					MD5_STORE_MAP.remove(config.getMd5());
					try {
						task.send(config);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
			}
		}
		
		private ConfigPullTask filterContext(Config config){
			ConfigPullTask task = LONGPULLS.remove(new PullKey(config.getMd5(), PullKeyType.MD5));
			if(task == null){
				task = LONGPULLS.remove(new PullKey(config.getNamespace()+"_"+config.getName(), PullKeyType.NAMESPACE_NAME));
			}
			return task;
		}
		
	}
	
	
	
	public static final class ConfigPullTask implements Runnable{
		
		private AsyncContext asyncContext;
		
		private PullKey key;
		
		private long timeout;
		
		private Future<Void> timeoutTask;
		
		public ConfigPullTask(AsyncContext asyncContext,PullKey key, long timeout) {
			super();
			this.asyncContext = asyncContext;
			this.key = key;
			this.timeout = timeout;
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
					LONGPULLS.remove(key);
					asyncContext.complete();
				}
			}, timeout, TimeUnit.SECONDS);
			ConfigPullTask oldTask = LONGPULLS.put(key, this);
			//防止将后加入的任务取消，先将之前的超时任务取消掉
			if(oldTask != null){
				oldTask.timeoutTask.cancel(true);
			}
		}
		
	}
	
	
	public static void destory(){
		scheduleService.shutdownNow();
	}
	

}
