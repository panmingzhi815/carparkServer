package com.dongluhitec.card.connect;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dongluhitec.card.connect.util.ByteUtils;

@SuppressWarnings("rawtypes")
public class MessageHandler extends IoHandlerAdapter {
	
	private Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);

	@Override
	public void sessionClosed(IoSession session) throws Exception {
//		LOGGER.debug("连接己关闭:{}",session.getRemoteAddress().toString());
		super.sessionClosed(session);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
//		LOGGER.debug("连接发生异常:{},异常信息:{}",session.getRemoteAddress().toString(),cause);
		cause.printStackTrace();
		super.exceptionCaught(session, cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		System.out.println(ByteUtils.byteArrayToHexString(((Message)message).toBytes()));
//		LOGGER.debug("收到消息:{},{}",session.getRemoteAddress().toString(),ByteUtils.byteArrayToHexString(((Message)message).toBytes()));
		super.messageReceived(session, message);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		System.out.println(ByteUtils.byteArrayToHexString(((Message)message).toBytes()));
//		LOGGER.debug("发送消息:{},{}",session.getRemoteAddress().toString(),ByteUtils.byteArrayToHexString(((Message)message).toBytes()));
		super.messageSent(session, message);
	}

}
