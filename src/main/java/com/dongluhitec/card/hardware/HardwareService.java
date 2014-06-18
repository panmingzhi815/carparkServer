package com.dongluhitec.card.hardware;

import java.awt.TrayIcon.MessageType;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.dongluhitec.card.CommonUI;
import com.dongluhitec.card.connect.body.OpenDoorEnum;
import com.dongluhitec.card.hardware.impl.MessageServiceImpl;
import com.dongluhitec.card.model.CarparkNowRecord;
import com.dongluhitec.card.model.CarparkSetting;
import com.dongluhitec.card.model.Device;
import com.dongluhitec.card.util.EventBusUtil;
import com.dongluhitec.card.util.EventInfo;
import com.dongluhitec.card.util.EventInfo.EventType;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Uninterruptibles;

public class HardwareService {
	
	public static HardwareService service = null;
	private static MessageService messageService = null;
	private ConnectFuture cf = null;
	private NioSocketConnector connector;
	private static CarparkSetting cs;
	private final long checkConnectorSecond = 3;
	
	private HardwareService(){};
	
	public static HardwareService getInstance(){
		if(service == null){
			service = new HardwareService();
			messageService = new MessageServiceImpl();
			cs = HardwareConfig.readData();
		}
		return service;
	}
	
	public void start(){
		if(cs == null){
			return;
		}
		startWebConnector();
		startLogging();
	}
	
	private void startLogging(){
		Timer timer = new Timer("check web connector");
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try{
					List<Device> deviceList = cs.getDeviceList();
					for (Device device : deviceList) {
						long start = System.currentTimeMillis();
						try{
							ListenableFuture<CarparkNowRecord> carparkReadNowRecord = messageService.carparkReadNowRecord(device);
							CarparkNowRecord carparkNowRecord = carparkReadNowRecord.get();
							if(carparkNowRecord != null){
								HardwareUtil.sendCardNO(cf.getSession(), carparkNowRecord.getCardID(),carparkNowRecord.getReaderID()+"", device.getName());
							}
							EventBusUtil.post(new EventInfo(EventType.硬件通讯正常, "硬件通讯恢复正常"));
						}catch(Exception e){
							EventBusUtil.post(new EventInfo(EventType.硬件通讯异常, "当前主机与停车场硬件设备通讯时发生异常,请检查"));
						}finally{
							HardwareUtil.controlSpeed(start, 100);
						}
					}
				}catch(Exception e){}
			}
		},1,100);
	}
	
	private void startWebConnector(){
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
					if(cf.getSession().isConnected()){
						EventBusUtil.post(new EventInfo(EventType.外接服务通讯正常, "外接服务通讯恢复正常"));
						return;
					}
					cf = connector.connect(new InetSocketAddress(cs.getIp(), Integer.parseInt(cs.getPort())));
					boolean awaitUninterruptibly = cf.awaitUninterruptibly(5,TimeUnit.SECONDS);
					if(!awaitUninterruptibly){
						EventBusUtil.post(new EventInfo(EventType.外接服务通讯异常, "当前主机与对接服务通讯失败,3秒后会自动重联"));
						return;
					}
					
				}catch(Exception e){
					cf = connector.connect(new InetSocketAddress(cs.getIp(), Integer.parseInt(cs.getPort())));
					cf.awaitUninterruptibly(5,TimeUnit.SECONDS);
					EventBusUtil.post(new EventInfo(EventType.外接服务通讯异常, "当前主机与对接服务通讯失败,3秒后会自动重联"));
				}
			}
		},5000,1000);
	}
		
	class AcceptorMessageHandler extends IoHandlerAdapter {

		
		@Override
		public void sessionCreated(IoSession session) throws Exception {
			super.sessionCreated(session);
			HardwareUtil.sendDeviceInfo(session, cs);
		}

		@Override
		public void messageReceived(final IoSession session, Object message)
				throws Exception {
			String checkSubpackage = HardwareUtil.checkSubpackage(session, message);
			if(checkSubpackage == null){
				return;
			}
			
			WebMessage wm = new WebMessage(checkSubpackage);
			
			final Document dom = DocumentHelper.parseText(wm.getContent());
			final Element rootElement = dom.getRootElement();
			
			if(wm.getType() == WebMessageType.成功){
				HardwareUtil.responseResult(session,dom);
			}
			
			if(wm.getType() == WebMessageType.设备控制){
				new Thread(new Runnable() {
					@Override
					public void run() {
						try{
							Element controlElement = rootElement.element("control");
							Element element = rootElement.element("device");
							
							String deviceName = element.element("deviceName").getTextTrim();
							String gate = controlElement.element("gate").getTextTrim();
							String Insidevoice = controlElement.element("insideVoice").getTextTrim();
							String Outsidevoice = controlElement.element("outsideVoice").getTextTrim();
							String InsideScreen = controlElement.element("insideScreen").getTextTrim();
							String OutsideScreen = controlElement.element("outsideScreen").getTextTrim();
							String InsideScreenAndVoiceData = controlElement.element("insideScreenAndVoiceData").getTextTrim();
							String OutsideScreenAndVoiceData = controlElement.element("outsideScreenAndVoiceData").getTextTrim();
							
							Device device = cs.getDeviceByName(deviceName);
							if(device == null){
								return;
							}
							if(InsideScreen.equals("true")){
								int voice = Insidevoice.equals("false")==true ? 1 : 10;
								messageService.carparkScreenVoiceDoor(device, 1, voice, 0, 0, InsideScreenAndVoiceData);
								Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
							}
							if(OutsideScreen.equals("true")){
								int voice = Outsidevoice.equals("false")==true ? 1 : 10;
								messageService.carparkScreenVoiceDoor(device, 2, voice, 0, 0, OutsideScreenAndVoiceData);
								Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
							}
							if(!gate.equals("false")){							
								messageService.carparkOpenDoor(device, OpenDoorEnum.parse(OpenDoorEnum.parse(gate)));
							}
							
							HardwareUtil.responseDeviceControl(session,dom);		
						}catch(Exception e){
							
						}
					}
				}).start();
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

}
