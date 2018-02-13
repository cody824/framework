package com.noknown.framework.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author guodong
 * @date 2018/2/13
 */
public class CommonEvent extends ApplicationEvent {

	private String type;

	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the object on which the event initially occurred (never {@code null})
	 */
	public CommonEvent(Object source) {
		super(source);
		type = source.toString();
	}

	public CommonEvent(Object source, String type) {
		super(source);
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public CommonEvent setType(String type) {
		this.type = type;
		return this;
	}
}
