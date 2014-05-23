package com.dongluhitec.card.hardware.message.body;

import com.dongluhitec.card.hardware.message.MessageBody;



public class EmptyBody implements MessageBody {

	public static int DATA_LENGTH = 0;

	@Override
	public void initContent(byte[] array) {
	}

	@Override
	public byte[] toBytes() {
		return new byte[EmptyBody.DATA_LENGTH];
	}

	@Override
	public String toString() {
		return "空消息体";
	}

}
