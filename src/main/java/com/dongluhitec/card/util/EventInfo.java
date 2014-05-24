package com.dongluhitec.card.util;


public class EventInfo {
	public static enum EventType{
		硬件通讯异常,外接服务通讯异常,硬件通讯正常,外接服务通讯正常
	}
	
	EventType eventType;
	Object obj;
	
	public EventInfo(EventType eventType, Object obj) {
		this.eventType = eventType;
		this.obj = obj;
	}
	public EventType getEventType() {
		return eventType;
	}
	public Object getObj() {
		return obj;
	}
}
