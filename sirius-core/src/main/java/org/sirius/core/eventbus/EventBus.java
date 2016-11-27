package org.sirius.core.eventbus;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.sirius.domain.Config;

public class EventBus {

	private static final Map<EventFilter<Config>, EventHandler<Config>> HANDLERS = new ConcurrentHashMap<>();

	public static void addEventHandler(EventFilter<Config> filter, EventHandler<Config> handler) {
		HANDLERS.put(filter, handler);
	}

	public static void dispatch(ConfigEvent<Config> e) {
		//FIXME 使用forkjoin框架优化
		for (Entry<EventFilter<Config>, EventHandler<Config>> entry : HANDLERS.entrySet()) {
			if(entry.getKey().filter(e)){
				entry.getValue().handler(e);
			}
		}
	}

}
