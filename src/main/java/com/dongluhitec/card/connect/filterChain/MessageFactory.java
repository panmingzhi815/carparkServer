package com.dongluhitec.card.connect.filterChain;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import com.dongluhitec.card.connect.MessageTypeEnum;
import com.dongluhitec.card.connect.exception.DongluHWException;

public class MessageFactory implements ProtocolCodecFactory{
	
	private ProtocolEncoder protocolEncoder;
	private ProtocolDecoder protocolDecoder;
	private AbstractMessageRegister messageRegister;
	
	public MessageFactory(MessageTypeEnum messageTypeEnum){
		initCoder(messageTypeEnum);
	}
	
	private void initCoder(MessageTypeEnum messageTypeEnum) {
		switch (messageTypeEnum) {
			case 停车场:
				messageRegister = new CarparkMessageRegisterImpl();
				break;
			default:
				throw new DongluHWException("未注册当前协议的编码器"+messageTypeEnum);
		}
		protocolEncoder = new MessageEncoder();
		protocolDecoder = new MessageDecoder(messageRegister);
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return protocolEncoder;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return protocolDecoder;
	}

}
