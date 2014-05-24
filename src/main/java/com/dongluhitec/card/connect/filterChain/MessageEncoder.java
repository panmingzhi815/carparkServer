package com.dongluhitec.card.connect.filterChain;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.dongluhitec.card.connect.Message;

public class MessageEncoder implements ProtocolEncoder {
	
	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		Message<?> request = (Message<?>) message;
		byte[] bytes = request.toBytes();

		IoBuffer buffer = IoBuffer.allocate(bytes.length, false);
		buffer.put(bytes);
		buffer.flip();
		
		out.write(buffer);
	}

	@Override
	public void dispose(IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}

}
