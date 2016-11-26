package org.sirius.core.eventbus;

/**
 * 配置事件
 * @author liwei
 * 2016年11月25日
 */
public class ConfigEvent<T> {
	
	private EventType eventType;
	
	/** 事件源 */
	private T source;

	public ConfigEvent(EventType eventType, T source) {
		super();
		this.eventType = eventType;
		this.source = source;
	}

	public EventType getEventType() {
		return eventType;
	}

	public T getSource() {
		return source;
	}

}
