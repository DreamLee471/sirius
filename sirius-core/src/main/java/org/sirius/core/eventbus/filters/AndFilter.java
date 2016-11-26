package org.sirius.core.eventbus.filters;

import org.sirius.core.eventbus.ConfigEvent;
import org.sirius.core.eventbus.EventFilter;
import org.sirius.domain.Config;

import com.sun.tools.javac.util.Assert;

public class AndFilter implements EventFilter<Config> {
	
	private EventFilter<Config> first;
	
	private EventFilter<Config> second;
	
	public AndFilter(EventFilter<Config> first, EventFilter<Config> second) {
		Assert.checkNonNull(first);
		Assert.checkNonNull(second);
		this.first = first;
		this.second = second;
	}


	@Override
	public boolean filter(ConfigEvent<Config> event) {
		return first.filter(event) && second.filter(event);
	}

}
