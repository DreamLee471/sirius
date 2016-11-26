package org.sirius.core.eventbus;

public interface EventHandler<T> {
	
	public void handler(ConfigEvent<T> e);

}
