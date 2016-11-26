package org.sirius.core.eventbus.filters;

import org.sirius.core.eventbus.ConfigEvent;
import org.sirius.core.eventbus.EventFilter;
import org.sirius.core.eventbus.EventType;
import org.sirius.domain.Config;

import com.sun.tools.javac.util.Assert;

public class ConfigEventTypeFilter implements EventFilter<Config> {
	
	
	private EventType targetType;
	
	public ConfigEventTypeFilter(EventType targetType) {
		Assert.checkNonNull(targetType);
		this.targetType = targetType;
	}

	@Override
	public boolean filter(ConfigEvent<Config> event) {
		return targetType.equals(event.getEventType());
	}

}
