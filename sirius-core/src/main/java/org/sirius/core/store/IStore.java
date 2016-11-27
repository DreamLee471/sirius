package org.sirius.core.store;

import org.sirius.domain.Config;

public interface IStore {
	
	
	public void store(Config config);
	
	
	public int update(Config config);
	
	
	public Config delete(String namespace,String name);
	
	
	public Config get(String namespace,String name);
	

}
