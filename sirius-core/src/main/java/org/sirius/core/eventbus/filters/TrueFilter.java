package org.sirius.core.eventbus.filters;

import org.sirius.core.eventbus.ConfigEvent;
import org.sirius.core.eventbus.EventFilter;
import org.sirius.domain.Config;

public class TrueFilter implements EventFilter<Config> {

	@Override
	public boolean filter(ConfigEvent<Config> event) {
		return true;
	}

}
