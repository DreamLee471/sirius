package org.sirius.core.pull;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;

import org.sirius.core.eventbus.ConfigEvent;
import org.sirius.core.eventbus.EventBus;
import org.sirius.core.eventbus.EventHandler;
import org.sirius.core.eventbus.filters.TrueFilter;
import org.sirius.domain.Config;

public class PullCore {
	
	public static final Map<PullKey,AsyncContext> LONGPULLS = new ConcurrentHashMap<>(1024);
	
	static{
		EventBus.addEventHandler(new TrueFilter(), new ConfigPullHandler());
	}
	
	public static void addLongPull(PullKey key,HttpServletRequest request){
		LONGPULLS.put(key, request.startAsync());
	}
	
	public static final class ConfigPullHandler implements EventHandler<Config>{

		@Override
		public void handler(ConfigEvent<Config> e) {
			Config config = e.getSource();
			AsyncContext asyncContext = filterContext(config);
			if(asyncContext == null) return;
			switch(e.getEventType()){
				case ADD:
				case MODIFIED:
					try {
						asyncContext.getResponse().getOutputStream().write(config.getContent().getBytes());
						asyncContext.complete();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				case REMOVE:
					try {
						asyncContext.getResponse().getOutputStream().write("".getBytes());
						asyncContext.complete();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
			}
		}
		
		private AsyncContext filterContext(Config config){
			AsyncContext asyncContext = LONGPULLS.remove(new PullKey(config.getMd5(), PullKeyType.MD5));
			if(asyncContext == null){
				asyncContext = LONGPULLS.remove(new PullKey(config.getNamespace()+"_"+config.getName(), PullKeyType.NAMESPACE_NAME));
			}
			return asyncContext;
		}
		
	}

}
