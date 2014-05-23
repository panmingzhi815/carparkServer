package com.dongluhitec.card.hardware;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.serial.SerialAddress;
import org.apache.mina.transport.serial.SerialAddress.DataBits;
import org.apache.mina.transport.serial.SerialAddress.FlowControl;
import org.apache.mina.transport.serial.SerialAddress.Parity;
import org.apache.mina.transport.serial.SerialAddress.StopBits;
import org.apache.mina.transport.serial.SerialConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.dongluhitec.card.CommonUI;
import com.dongluhitec.card.hardware.message.body.VoiceBody.Property;
import com.dongluhitec.card.hardware.message.util.ByteUtils;
import com.dongluhitec.card.hardware.message.util.MessageUtil;
import com.dongluhitec.card.model.CarparkSetting;
import com.dongluhitec.card.model.Device;
import com.jamierf.rxtx.RXTXLoader;

public class HardwareService {
	
	public static HardwareService service = null;
	private NioSocketConnector connector;
	private CarparkSetting cs;
	private ConnectFuture cf;
	private final long checkConnectorSecond = 3;
	//指定通讯所花的总共时长,打开端口占1/2,写数据占1/4,读数据占1/4
	private final long sendMessageTimeLength = 300;
	private IoConnector deviceConnector;
	private ConnectFuture deviceConnect;
	private Map<Device, SocketAddress> socketAddressMap = new HashMap<Device, SocketAddress>();
	
	private HardwareService(){};
	
	public static HardwareService getInstance(){
		if(service == null){
			service = new HardwareService();
			try {
				RXTXLoader.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return service;
	}
	
	public void start(){
		if(cs != null){
			startWebConnector();
		}
	}
	
	public void startLogging(){
		Timer timer = new Timer("check web connector");
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try{
					List<Device> deviceList = cs.getDeviceList();
					for (Device device : deviceList) {
						sendMesssage(device, "");
					}
				}catch(Exception e){}
			}
		},1000,checkConnectorSecond * 1000);
	}
	
	public void startWebConnector(){
		try {
			connector = new NioSocketConnector();

			connector.getFilterChain().addLast("logger", new LoggingFilter());
			//指定编码过滤器 
			connector.getFilterChain().addLast( "codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
			connector.setHandler(new AcceptorMessageHandler());
			// Set connect timeout.
			connector.setConnectTimeoutCheckInterval(30);
			// 连结到服务器:
			cf = connector.connect(new InetSocketAddress(cs.getIp(), Integer.parseInt(cs.getPort())));
			cf.awaitUninterruptibly(5,TimeUnit.SECONDS);
			
			checkWebConnector();
		} catch (Exception e) {
			CommonUI.error("错误", "连接失败");
		}
	}
	
	private void checkWebConnector(){
		Timer timer = new Timer("check web connector");
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try{
					if(cf.isConnected()){
						return;
					}
					cf = connector.connect(new InetSocketAddress(cs.getIp(), Integer.parseInt(cs.getPort())));
					cf.awaitUninterruptibly(5,TimeUnit.SECONDS);
				}catch(Exception e){}
			}
		},5000,checkConnectorSecond * 1000);
	}
	
	public Object sendMesssage(Device device,Object message){
		if(deviceConnector == null){
			deviceConnector = new SerialConnector();

			deviceConnector.getFilterChain().addLast("logger", new LoggingFilter());
			//指定编码过滤器 
			deviceConnector.getFilterChain().addLast( "codec", new ProtocolCodecFilter(new DemuxingProtocolCodecFactory()));
			deviceConnector.setHandler(new AcceptorMessageHandler());
			// Set connect timeout.
			deviceConnector.setConnectTimeoutMillis(1000);
		}
		// 连结到服务器:
		deviceConnect = deviceConnector.connect(getSocketAddress(device));
		boolean awaitUninterruptibly2 = deviceConnect.awaitUninterruptibly(1000);
		if(awaitUninterruptibly2 == false){
			throw new RuntimeException("打开连接失败");
		}
		IoSession session = deviceConnect.getSession();
		session.setAttribute("device", device);
		WriteFuture write = session.write(message);
		write.awaitUninterruptibly(sendMessageTimeLength/4,TimeUnit.MILLISECONDS);
		session.getConfig().setUseReadOperation(true);
		ReadFuture read = session.read();
		boolean awaitUninterruptibly = read.awaitUninterruptibly(sendMessageTimeLength/4,TimeUnit.MILLISECONDS);
		if(awaitUninterruptibly == false){
			session.close(false);
			return null;
		}
		return read.getMessage();
	}
	
	private SocketAddress getSocketAddress(Device device){
		SocketAddress socketAddress = socketAddressMap.get(device);
		if(socketAddress != null){
			return socketAddress;
		}
		String address = device.getAddress();
		String type = device.getType();

		if(type.equals("COM")){
			socketAddress = new SerialAddress(address,9600 ,DataBits.DATABITS_8, StopBits.BITS_1, Parity.NONE, FlowControl.NONE);
		}else if(type.equals("TCP")){
			String[] split = address.split(":");
			socketAddress = new InetSocketAddress(split[0], Integer.parseInt(split[1]));
		}
		
		socketAddressMap.put(device, socketAddress);
		return socketAddress;
	}
	
	class AcceptorMessageHandler extends IoHandlerAdapter {

		@Override
		public void sessionCreated(IoSession session) throws Exception {
			super.sessionCreated(session);
		}

		@Override
		public void messageReceived(IoSession session, Object message)
				throws Exception {
			String checkSubpackage = HardwareUtil.checkSubpackage(session, message);
			if(checkSubpackage == null){
				return;
			}
			if(checkSubpackage.startsWith("publicKey", 21)){
				HardwareUtil.responsePublicKey_server(session,checkSubpackage);
				return;
			}
			
			final Document dom = DocumentHelper.parseText(HardwareUtil.decode(checkSubpackage));
			Element rootElement = dom.getRootElement();
			
			if(rootElement.attributeValue("type").equals("deviceControl")){
				String responseDeviceControl = HardwareUtil.responseDeviceControl(session,dom);
			}
			
			if(rootElement.attributeValue("type").equals("result")){
				
			}
		}

		@Override
		public void messageSent(IoSession session, Object message)
				throws Exception {
			
			super.messageSent(session, message);
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			super.sessionClosed(session);
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause)
				throws Exception {
			super.exceptionCaught(session, cause);
		}

	}
	
	public static void main(String[] args) {
		HardwareService instance = HardwareService.getInstance();
		Device device = new Device();
		device.setAddress("192.168.1.170:10001");
		device.setArea("1.1");
		device.setType("TCP");
		device.setName("123456");
		device.setInoutType("出口");
		byte[] bytes = MessageUtil.createVoice(device, Property.欢迎光临_请入场停车).toBytes();		
		System.out.println(ByteUtils.byteArrayToHexString(bytes));
		instance.sendMesssage(device, bytes);
	}
}
