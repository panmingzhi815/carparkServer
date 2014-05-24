package com.dongluhitec.card.connect.filterChain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.dongluhitec.card.connect.DirectonType;
import com.dongluhitec.card.connect.Message;
import com.dongluhitec.card.connect.MessageBody;
import com.dongluhitec.card.connect.MessageBodyInfo;
import com.dongluhitec.card.connect.MessageHeader;
import com.dongluhitec.card.connect.exception.DongluHWException;
import com.dongluhitec.card.connect.exception.DongluInvalidMessageException;
import com.dongluhitec.card.connect.util.SerialDeviceAddress;

public abstract class AbstractMessageRegister {
	
	public static Map<String,MessageHeader> headerMap = new HashMap<String,MessageHeader>();

	public Map<Byte, MessageBodyInfo> requestMap = new HashMap<Byte, MessageBodyInfo>();
	public Map<Byte, MessageBodyInfo> responseMap = new HashMap<Byte, MessageBodyInfo>();

	public AbstractMessageRegister() {
		registerRequestBody(requestMap);
		registerResponseBody(responseMap);
	}

	public MessageBodyInfo getMessageBody(byte code, DirectonType directonType) {
		MessageBodyInfo msg = null;
		if (directonType == DirectonType.请求) {
			msg = requestMap.get(code);
		} else {
			msg = responseMap.get(code);
		}
		if (msg == null) {
			throw new DongluHWException("未注册该消息类型" + code);
		}
		return msg;
	}

	public abstract void registerRequestBody(Map<Byte, MessageBodyInfo> requestMap);

	public abstract void registerResponseBody(Map<Byte, MessageBodyInfo> responseMap);

	public MessageBodyInfo getMessageBodyInfo(byte funcCode, DirectonType comm) {
		MessageBodyInfo info = this.getBodyInfo(funcCode, comm);
		return info;
	}

	public MessageBody createMessageBody(byte funcCode, DirectonType com) {
		MessageBodyInfo info = this.getBodyInfo(funcCode, com);
		try {
			if (info != null)
				return info.getResponseClass().newInstance();
			else
				throw new IllegalArgumentException("无法找到对应的消息体，请检查输入参数");
		} catch (IllegalAccessException e) {
			throw new RuntimeException("无法创建消息体对象", e);
		} catch (InstantiationException e) {
			throw new RuntimeException("无法创建消息体对象", e);
		}
	}

	public int getDataLength(byte funcCode, DirectonType com) {
		MessageBodyInfo info = this.getBodyInfo(funcCode, com);
		if (info != null)
			return info.getRequestLength();
		else
			throw new RuntimeException();
	}

	private MessageBodyInfo getBodyInfo(byte funcCode, DirectonType com) {
		MessageBodyInfo info = null;
		if (com == DirectonType.请求) {
			info = this.requestMap.get(funcCode);
		} else {
			info = this.responseMap.get(funcCode);
		}
		return info;
	}

	public MessageBody createMessageBodyFromBytes(byte[] dataBuffer, int start, byte funcCode, DirectonType direction) throws DongluInvalidMessageException {
		MessageBody body = this.createMessageBody(funcCode, direction);
		int dataLength = this.getDataLength(funcCode, direction);
		byte[] array = Arrays.copyOfRange(dataBuffer, start, start + dataLength);
		body.initContent(array);
		return body;
	}

	public Message<?> createMessage(byte funcCode, DirectonType dir) {
		MessageBody body = this.createMessageBody(funcCode, dir);
		MessageHeader head = new MessageHeader(new SerialDeviceAddress(), dir, funcCode, this.getDataLength(funcCode, dir));
		Message<?> message = new Message(head, body);
		return message;
	}

	public MessageHeader constructHeader(byte[] bb, int start) throws DongluInvalidMessageException {
		String createKeyWithBytes = createKeyWithBytes(bb);
		MessageHeader messageHeader = headerMap.get(createKeyWithBytes);
		if(messageHeader != null){
			return messageHeader;
		}
		
		MessageHeader head = new MessageHeader(bb, start);
		byte functionCode = head.getFunctionCode();
		DirectonType direction = head.getDirectonType();
		head.setDataLength(this.getDataLength(functionCode, direction));
		headerMap.put(createKeyWithBytes, head);
		return head;
	}
	
	public String createKeyWithBytes(byte[] bb){
		StringBuffer stringBuffer = new StringBuffer();
		for(byte b:bb){
			stringBuffer.append(b);
		}
		return stringBuffer.toString();
	}

}
