package com.dongluhitec.card.util;

import com.google.common.eventbus.EventBus;

public class EventBusUtil {
	
	private static EventBus eventBus = new EventBus("硬件底层客户端事件监听器");
	
	public static void register(Object obj){
		eventBus.register(obj);
	}
	
	public static void post(EventInfo event){
		eventBus.post(event);
	}
	
}
