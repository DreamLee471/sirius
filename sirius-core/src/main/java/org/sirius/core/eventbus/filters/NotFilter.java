package org.sirius.core.eventbus.filters;

import org.sirius.core.eventbus.ConfigEvent;
import org.sirius.core.eventbus.EventFilter;
import org.sirius.domain.Config;

import com.sun.tools.javac.util.Assert;

public class NotFilter implements EventFilter<Config> {

	private EventFilter<Config> f;
	
	public NotFilter(EventFilter<Config> f) {
		Assert.checkNonNull(f);
		this.f = f;
	}

	@Override
	public boolean filter(ConfigEvent<Config> event) {
		return !f.filter(event);
	}

}
