package com.dongluhitec.card.hardware.message;

import com.dongluhitec.card.hardware.message.util.Bytenize;
import com.dongluhitec.card.hardware.message.util.Utils;


/**
 * 发送或者接受的一条消息。
 *
 * @author wudong
 */
public class Message<T extends MessageBody> implements Bytenize {

	public Message(MessageHeader header, T body) {
		this.header = header;
		this.body = body;
	}

	final private MessageHeader header;
	final private T body;

	public MessageHeader getHeader() {
		return this.header;
	}

	public T getBody() {
		return this.body;
	}

	@Override
	public byte[] toBytes() {
		int dataLength = this.header.getDataLength();
		int totalMsgLength = dataLength
				+ MessageConstance.MESSAGE_HEADER_LENGTH
				+ MessageConstance.MESSAGE_DATA_EXTRA_LENGTH;
		byte[] bb = new byte[totalMsgLength];
		byte[] hb = this.header.toBytes();
		System.arraycopy(hb, 0, bb, 0, MessageConstance.MESSAGE_HEADER_LENGTH);

		int etxIndex = MessageConstance.MESSAGE_HEADER_LENGTH
				+ MessageConstance.MESSAGE_DATA + dataLength;
		bb[etxIndex] = MessageConstance.MESSAGE_DATA_ETX_VALUE;

		bb[MessageConstance.MESSAGE_HEADER_LENGTH
				+ MessageConstance.MESSAGE_DATA_STX] = MessageConstance.MESSAGE_DATA_STX_VALUE;

		byte[] bytes = this.body.toBytes();

		System.arraycopy(bytes, 0, bb, MessageConstance.MESSAGE_HEADER_LENGTH
				+ MessageConstance.MESSAGE_DATA, dataLength);

		byte bcc = Utils.BCC(bb, 0, bb.length - 1);
		bb[bb.length - 1] = bcc;
		return bb;
	}

	@Override
	public String toString() {
		return "header=" + this.header + "body=" + this.body;
	}


}
