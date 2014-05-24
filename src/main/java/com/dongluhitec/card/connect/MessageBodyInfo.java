package com.dongluhitec.card.connect;


public class MessageBodyInfo {
	final private int bodyLength;
	final private Class<? extends MessageBody> bodyClass;
	final private boolean needResponse;

	public MessageBodyInfo(int length, Class<? extends MessageBody> bodyClass) {
		this.bodyLength = length;
		this.bodyClass = bodyClass;
		this.needResponse = true;
	}

	public MessageBodyInfo(int length, Class<? extends MessageBody> bodyClass,
	                       boolean needResponse) {
		this.bodyLength = length;
		this.bodyClass = bodyClass;
		this.needResponse = needResponse;
	}

	public boolean isNeedResponse() {
		return this.needResponse;
	}

	public int getRequestLength() {
		return this.bodyLength;
	}

	public Class<? extends MessageBody> getResponseClass() {
		return this.bodyClass;
	}
}
