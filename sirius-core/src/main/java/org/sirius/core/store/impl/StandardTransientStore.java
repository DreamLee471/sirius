package org.sirius.core.store.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.sirius.core.eventbus.ConfigEvent;
import org.sirius.core.eventbus.EventBus;
import org.sirius.core.eventbus.EventHandler;
import org.sirius.core.eventbus.EventType;
import org.sirius.core.eventbus.filters.TrueFilter;
import org.sirius.core.store.PersistentStore;
import org.sirius.core.store.TransientStore;
import org.sirius.domain.Config;

/**
 * 默认的瞬时存储
 * @author liwei
 * 2016年11月27日
 */
public class StandardTransientStore implements TransientStore,EventHandler<Config> {
	
	private PersistentStore persistentStore;
	
	private Map<ConfigKey,Config> configs = new ConcurrentHashMap<StandardTransientStore.ConfigKey, Config>(1024);

	public StandardTransientStore(PersistentStore persistentStore) {
		this.persistentStore = persistentStore;
		if(persistentStore != null){
			EventBus.addEventHandler(new TrueFilter(), this);
		}
	}

	@Override
	public void store(Config config) {
		Config oldConfig = configs.put(ConfigKey.of(config), config);
		EventBus.dispatch(new ConfigEvent<Config>(oldConfig==null?EventType.ADD:EventType.MODIFIED, config));
	}

	@Override
	public int update(Config config) {
		Config oldConfig = configs.put(ConfigKey.of(config), config);
		EventBus.dispatch(new ConfigEvent<Config>(oldConfig==null?EventType.ADD:EventType.MODIFIED, config));
		return 1;
	}

	@Override
	public Config delete(String namespace, String name) {
		Config removed = configs.remove(new ConfigKey(namespace,name));
		EventBus.dispatch(new ConfigEvent<Config>(EventType.REMOVE, removed));
		return removed;
	}

	@Override
	public Config get(String namespace, String name) {
		return configs.get(new ConfigKey(namespace,name));
	}

	@Override
	public boolean isAttachPersistent() {
		return persistentStore != null;
	}

	@Override
	public PersistentStore getPersistentStore() {
		return persistentStore;
	}
	
	
	public final static class ConfigKey{
		private String namespace;
		private String name;
		public ConfigKey(String namespace, String name) {
			super();
			this.namespace = namespace;
			this.name = name;
		}
		public String getNamespace() {
			return namespace;
		}
		public String getName() {
			return name;
		}
		
		
		public static ConfigKey of(Config config){
			return new ConfigKey(config.getNamespace(),config.getName());
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ConfigKey other = (ConfigKey) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (namespace == null) {
				if (other.namespace != null)
					return false;
			} else if (!namespace.equals(other.namespace))
				return false;
			return true;
		}
	}


	@Override
	public void handler(ConfigEvent<Config> e) {
		switch(e.getEventType()){
			case ADD:
				getPersistentStore().store(e.getSource());
				break;
			case MODIFIED:
				getPersistentStore().update(e.getSource());
				break;
			case REMOVE:
				getPersistentStore().delete(e.getSource().getNamespace(),e.getSource().getName());
				break;
		}
	}

}
