package org.sirius.core.eventbus.filters;

import org.sirius.core.eventbus.ConfigEvent;
import org.sirius.core.eventbus.EventFilter;
import org.sirius.domain.Config;

public class ConfigFilter implements EventFilter<Config> {
	
	private String namespace;
	
	private String name;
	
	public ConfigFilter(String namespace, String name) {
		this.namespace = namespace;
		this.name = name;
	}


	@Override
	public boolean filter(ConfigEvent<Config> event) {
		return event.getSource().getName().equals(name) && event.getSource().getNamespace().equals(namespace);
	}

}
