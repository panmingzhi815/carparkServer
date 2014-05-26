package com.dongluhitec.card.connect;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.serial.SerialAddress;
import org.apache.mina.transport.serial.SerialAddress.DataBits;
import org.apache.mina.transport.serial.SerialAddress.FlowControl;
import org.apache.mina.transport.serial.SerialAddress.Parity;
import org.apache.mina.transport.serial.SerialAddress.StopBits;
import org.apache.mina.transport.serial.SerialConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.dongluhitec.card.connect.exception.DongluHWException;
import com.dongluhitec.card.connect.filterChain.MessageFactory;

public class MessageTransport {
	public static enum TransportType {
		TCP, COM;
	}

	private final String address;
	private final TransportType transportType;

	private IoConnector ioConnector;
	private SocketAddress socketAddress;
	private ConnectFuture connect;
	private IoSession session;

	public MessageTransport(String address, TransportType transportType) {
		this.address = address;
		this.transportType = transportType;
	}

	public void open() {
		if (ioConnector == null) {
			IoConnector ioConnector = null;
			if (transportType == TransportType.COM) {
				ioConnector = new SerialConnector();
			} else {
				ioConnector = new NioSocketConnector();
			}
			ioConnector.getFilterChain().addLast("logger", new LoggingFilter());
			ioConnector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MessageFactory(MessageTypeEnum.停车场)));
			ioConnector.setHandler(new MessageHandler());
			ioConnector.setConnectTimeoutMillis(1000);
			this.ioConnector = ioConnector;
		}
		
		ConnectFuture connect = ioConnector.connect(getSocketAddress());
        boolean connected = connect.awaitUninterruptibly(200);

        if (!connected) {
            throw new DongluHWException("连接超时");
        }

        this.session = connect.getSession();
	}

	public void close() {
		if (this.session != null) {
            CloseFuture close = this.session.close(true);
            close.awaitUninterruptibly();
            this.session = null;
        }
	}

	private SocketAddress getSocketAddress() {
		if (socketAddress != null) {
			return socketAddress;
		}
		switch (this.transportType) {
		case TCP:
			String[] split = address.split(":");
			return new InetSocketAddress(split[0], Integer.parseInt(split[1]));
		case COM:
			return new SerialAddress(address, 9600, DataBits.DATABITS_8, StopBits.BITS_1, Parity.NONE, FlowControl.NONE);
		default:
			throw new DongluHWException("不支持的通讯类型:" + transportType);
		}
	}

	public synchronized Message<?> sendMessage(Message<?> message) {
		try {
			open();

			WriteFuture write = session.write(message);
			boolean awaitUninterruptibly2 = write.awaitUninterruptibly(100);
			if(!awaitUninterruptibly2){
				throw new DongluHWException("发送消息超时");
			}

			session.getConfig().setUseReadOperation(true);
			ReadFuture read = session.read();
			boolean awaitUninterruptibly = read.awaitUninterruptibly(100);
			if(!awaitUninterruptibly){
				throw new DongluHWException("等待消息超时");
			}
			Message<?> readMsg = (Message<?>) read.getMessage();
			session.getConfig().setUseReadOperation(false);
			
			return readMsg;
		}finally {
			close();
		}

	}

	public synchronized void sendMessageNoReturn(Message<?> message) {
		try {
			open();

			IoSession session = connect.getSession();
			WriteFuture write = session.write(message);
			write.awaitUninterruptibly(100);
		}finally {
			close();
		}

	}

}
