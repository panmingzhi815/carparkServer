package com.dongluhitec.card.hardware.message;

/**
 * 在一个消息中通讯的方向，一个消息或者是发送的消息，或者是接受的消息。
 *
 * @author wudong
 */
public enum CommDirectionEnum {
	Request, Response;

	private static final String STRING_RESPONSE = "接受";
	private static final String STRING_REQUEST = "发送";

	@Override
	public String toString() {
		switch (this) {
			case Request:
				return CommDirectionEnum.STRING_REQUEST;
			case Response:
				return CommDirectionEnum.STRING_RESPONSE;
			default:
				return "";

		}
	}

	;

}
