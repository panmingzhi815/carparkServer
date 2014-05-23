package com.dongluhitec.card.connect;

import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.serial.SerialConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.dongluhitec.card.hardware.message.Message;

public class MessageTransport {
	public static enum TransportType{
		TCP,COM
	}
	
	private final String address;
	private final TransportType transportType;
	
	private IoConnector ioConnector;
	
	public MessageTransport(String address,TransportType transportType){
		this.address = address;
		this.transportType = transportType;
	}
	
	public void open(){
		if(ioConnector == null){
			if(transportType == TransportType.COM){
				ioConnector = new SerialConnector();
			}else{
				ioConnector = new NioSocketConnector();
			}
			ioConnector.getFilterChain().addLast("logger", new LoggingFilter());
			ioConnector.getFilterChain().addLast( "codec", new ProtocolCodecFilter(new DemuxingProtocolCodecFactory()));
			// Set connect timeout.
			ioConnector.setConnectTimeoutMillis(1000);
		}
		
	}
	
	public void close(){
		
	}
	
	public Message<?> sendMessage(Message<?> message){
		return null;
	}
	
	public void sendMessageNoReturn(Message<?> message){
		
	}

}
