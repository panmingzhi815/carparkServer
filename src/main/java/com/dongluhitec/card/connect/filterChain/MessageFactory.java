package com.dongluhitec.card.connect.filterChain;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import com.dongluhitec.card.connect.DongluInvalidMessageException;

public class MessageFactory implements ProtocolCodecFactory{
	private final String FUNCTIONCODE = "functionCode";
	private final String DEVICEADDRESS = "deviceAddress";
	private final String DIRECTIONTYPE = "directionType";
	
	private ProtocolEncoder protocolEncoder = new MessageEncoder();
	private ProtocolDecoder protocolDecoder = new MessageDecoder();

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		checkAttribute(session);
		return protocolEncoder;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return protocolDecoder;
	}
	
	public void checkAttribute(IoSession session){
		Object attribute = session.getAttribute(FUNCTIONCODE);
		if(attribute == null){
			throw new DongluInvalidMessageException("未在会话中找到功能码");
		}
		Object attribute2 = session.getAttribute(DEVICEADDRESS);
		if(attribute2 == null){
			throw new DongluInvalidMessageException("未在会话中找到设备地址");
		}
		Object attribute3 = session.getAttribute(DIRECTIONTYPE);
		if(attribute3 == null){
			throw new DongluInvalidMessageException("未在会话中找到消息发送方向,w/W");
		}
	}

}
