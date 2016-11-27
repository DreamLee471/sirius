package org.sirius.core.store;

/**
 * 非持久化的存储
 * @author liwei
 * 2016年11月27日
 */
public interface TransientStore extends IStore{
	
	/**
	 * 是否关联了持久化存储
	 * @return
	 */
	public boolean isAttachPersistent();
	
	
	/**
	 * 获得持久化存储实例
	 * @return
	 */
	public PersistentStore getPersistentStore();
	

}
