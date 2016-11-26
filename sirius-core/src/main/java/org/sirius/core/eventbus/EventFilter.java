package org.sirius.core.eventbus;

public interface EventFilter <T>{
	
	public boolean filter(ConfigEvent<T> event);
	

}
