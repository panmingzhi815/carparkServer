package com.dongluhitec.card.connect.filterChain;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.dongluhitec.card.connect.DirectonType;
import com.dongluhitec.card.connect.Message;
import com.dongluhitec.card.connect.MessageBody;
import com.dongluhitec.card.connect.MessageConstance;
import com.dongluhitec.card.connect.MessageHeader;

public class MessageDecoder extends CumulativeProtocolDecoder{
	
	private final String header = "MessageDecoder.header";

	private AbstractMessageRegister messageRegister;

	public MessageDecoder(AbstractMessageRegister messageRegister) {
		this.messageRegister = messageRegister;
	}

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		MessageHeader mh = (MessageHeader)session.getAttribute(header);
		if(mh == null){
			int remaining = in.remaining();
			if(remaining <= MessageConstance.MESSAGE_HEADER_LENGTH){
				return false;
			}
			byte[] bb = new byte[MessageConstance.MESSAGE_HEADER_LENGTH];
			in.get(bb);
			mh = messageRegister.constructHeader(bb, 0);
			session.setAttribute(header, mh);
			if (in.remaining() >= mh.getDataLength() + 3) {
				this.readBodyAndWrite(mh, in, out, session);
				return true;
			} else
				return false;
		}
		
		if (in.remaining() >= mh.getDataLength() + 3) {
			this.readBodyAndWrite(mh, in, out, session);
			return true;
		} else
			return false;
	}
	

	private void readBodyAndWrite(MessageHeader mh, IoBuffer in,
	                              ProtocolDecoderOutput out, IoSession session)
			 {

		byte[] bb = new byte[mh.getDataLength() + 3];
		in.get(bb);

		byte[] b = new byte[mh.getDataLength()];
		System.arraycopy(bb, 1, b, 0, b.length);

		MessageBody body = this.messageRegister.createMessageBody(
				mh.getFunctionCode(), mh.getDirectonType());
		body.initContent(b);

		Message<?> message = new Message(mh, body);
		out.write(message);
	}

}
