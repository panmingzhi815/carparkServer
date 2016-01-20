package com.dongluhitec.card.hardware;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private Logger LOGGER = LoggerFactory.getLogger(HardwareService.class);
	public static HardwareService service = null;
	private static MessageService messageService = null;
	private ConnectFuture cf = null;
	private NioSocketConnector connector;
	private static CarparkSetting cs;
	private final long checkConnectorSecond = 3;
	private NioSocketAcceptor acceptor;
	private final int PORT = 9124;
	private long lastDownloadTime = 0;
	private long lastDownloadad = 0;
	private static boolean isPlayVoice = false;
	
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
		startListne();
	}
	
	private void startListne(){
		try {
			acceptor = new NioSocketAcceptor();

			acceptor.getFilterChain().addLast("logger", new LoggingFilter());
			//指定编码过滤器 
			TextLineCodecFactory lineCodec=new TextLineCodecFactory(Charset.forName("UTF-8"));
			lineCodec.setDecoderMaxLineLength(1024*1024); //1M  
			lineCodec.setEncoderMaxLineLength(1024*1024); //1M  
			acceptor.getFilterChain().addLast("codec",new ProtocolCodecFilter(lineCodec));  //行文本解析   
			acceptor.setHandler(new listenHandler());
			
			acceptor.bind(new InetSocketAddress(PORT));
			LOGGER.info("监听服务开始，端口：{}",PORT);
		} catch (Exception e) {
			e.printStackTrace();
			CommonUI.error("错误", "开始监听服务器失败!");
		}
	}
	
	private void startLogging(){
		Timer timer = new Timer("check web connector");
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try{
					List<Device> deviceList = cs.getDeviceList();
					for (Device device : deviceList) {
						LOGGER.debug("开始轮询设备:{}",device.getName());
						final String address = device.getArea();
						long start = System.currentTimeMillis();
						try{
							if(isPlayVoice == true){
								HardwareUtil.controlSpeed(start, 300);
								isPlayVoice = false;
							}
							if (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - lastDownloadTime) > 10){
								messageService.setDateTime(device,new Date());
								lastDownloadTime = System.currentTimeMillis();
								LOGGER.info("下载时间:{}到设备:{}",new Date(),device);
								Uninterruptibles.sleepUninterruptibly(200, TimeUnit.MILLISECONDS);
							}
							if (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - lastDownloadad) > 10){
								messageService.setAD(device,cs.getAd());
								lastDownloadad = System.currentTimeMillis();
								LOGGER.info("下载广告:{}到设备:{}",cs.getAd(),device);
								Uninterruptibles.sleepUninterruptibly(200, TimeUnit.MILLISECONDS);
							}
							ListenableFuture<CarparkNowRecord> carparkReadNowRecord = messageService.carparkReadNowRecord(device);
							CarparkNowRecord carparkNowRecord = carparkReadNowRecord.get(5000,TimeUnit.MILLISECONDS);
							if(carparkNowRecord != null){
								HardwareUtil.sendCardNO(cf.getSession(), carparkNowRecord.getCardID(),carparkNowRecord.getReaderID()+"", device.getName());
								HardwareUtil.controlSpeed(start, 3000);
							}
							device.setArea("255.1");
							messageService.carparkReadNowRecord(device).get(5000,TimeUnit.MILLISECONDS);
							EventBusUtil.post(new EventInfo(EventType.硬件通讯正常, "硬件通讯恢复正常"));
						}catch(Exception e){
							LOGGER.error("采集实时记录时发生错误",e);
							EventBusUtil.post(new EventInfo(EventType.硬件通讯异常, "当前主机与停车场硬件设备通讯时发生异常,请检查"));
						}finally{
							HardwareUtil.controlSpeed(start, 400);
							device.setArea(address);
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
			TextLineCodecFactory lineCodec=new TextLineCodecFactory(Charset.forName("UTF-8"));
			lineCodec.setDecoderMaxLineLength(1024*1024); //1M  
			lineCodec.setEncoderMaxLineLength(1024*1024); //1M  
			connector.getFilterChain().addLast("codec",new ProtocolCodecFilter(lineCodec));  //行文本解析   
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
	
	private boolean isSendDevice = false;
	private void checkWebConnector(){
		Timer timer = new Timer("check web connector");
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try{
					if(cf.getSession().isConnected()){
						EventBusUtil.post(new EventInfo(EventType.外接服务通讯正常, "外接服务通讯恢复正常"));
						if(isSendDevice == false){
							HardwareUtil.sendDeviceInfo(cf.getSession(), cs);
							isSendDevice = true;
						}
						return;
					}
					cf = connector.connect(new InetSocketAddress(cs.getIp(), Integer.parseInt(cs.getPort())));
					boolean awaitUninterruptibly = cf.awaitUninterruptibly(5,TimeUnit.SECONDS);
					if(!awaitUninterruptibly){
						EventBusUtil.post(new EventInfo(EventType.外接服务通讯异常, "当前主机与对接服务通讯失败,3秒后会自动重联"));
						isSendDevice = false;
						return;
					}
					
				}catch(Exception e){
					isSendDevice = false;
					cf = connector.connect(new InetSocketAddress(cs.getIp(), Integer.parseInt(cs.getPort())));
					cf.awaitUninterruptibly(500,TimeUnit.MILLISECONDS);
					EventBusUtil.post(new EventInfo(EventType.外接服务通讯异常, "当前主机与对接服务通讯失败,3秒后会自动重联"));
				}
			}
		},5000,5000);
	}
	
	class listenHandler extends IoHandlerAdapter{

		@Override
		public void messageReceived(final IoSession session, Object message) throws Exception {
			String checkSubpackage = HardwareUtil.checkSubpackage(session, message);
			if(checkSubpackage == null){
				return;
			}
			
			WebMessage wm = new WebMessage(checkSubpackage);
			
			final Document dom = DocumentHelper.parseText(wm.getContent());
			final Element rootElement = dom.getRootElement();
			
			if(wm.getType() == WebMessageType.成功) {
				HardwareUtil.responseResult(session,dom);
			}
			
			if(wm.getType() == WebMessageType.设备控制){
				new Thread(new Runnable() {
					@Override
					public void run() {
						try{
							isPlayVoice = true;
							Element controlElement = rootElement.element("control");
							Element element = rootElement.element("device");
							
							String deviceName = element.element("deviceName").getTextTrim();
							String gate = controlElement.element("gate").getTextTrim();
							String Insidevoice,Outsidevoice,InsideScreen,OutsideScreen,InsideScreenAndVoiceData,OutsideScreenAndVoiceData;
							if(controlElement.element("insideVoice") == null){
								Insidevoice = controlElement.element("InsideVoice").getTextTrim();
								Outsidevoice = controlElement.element("OutsideVoice").getTextTrim();
								InsideScreen = controlElement.element("InsideScreen").getTextTrim();
								OutsideScreen = controlElement.element("OutsideScreen").getTextTrim();
								InsideScreenAndVoiceData = controlElement.element("InsideScreenAndVoiceData").getTextTrim();
								OutsideScreenAndVoiceData = controlElement.element("OutsideScreenAndVoiceData").getTextTrim();
							}else{
								Insidevoice = controlElement.element("insideVoice").getTextTrim();
								Outsidevoice = controlElement.element("outsideVoice").getTextTrim();
								InsideScreen = controlElement.element("insideScreen").getTextTrim();
								OutsideScreen = controlElement.element("outsideScreen").getTextTrim();
								InsideScreenAndVoiceData = controlElement.element("insideScreenAndVoiceData").getTextTrim();
								OutsideScreenAndVoiceData = controlElement.element("outsideScreenAndVoiceData").getTextTrim();
							}
							
							Device device = cs.getDeviceByName(deviceName);
							if(device == null){
								return;
							}
							if(InsideScreen.equals("true")){
								int voice = Insidevoice.equals("false")==true ? 1 : 9;
								ListenableFuture<Boolean> carparkScreenVoiceDoor = messageService.carparkScreenVoiceDoor(device, 1, voice, 0, OpenDoorEnum.parse(gate), InsideScreenAndVoiceData);
								Boolean boolean1 = carparkScreenVoiceDoor.get();
								if(boolean1 == null){
									carparkScreenVoiceDoor = messageService.carparkScreenVoiceDoor(device, 1, voice, 0, OpenDoorEnum.parse(gate), InsideScreenAndVoiceData);
									carparkScreenVoiceDoor.get();
								}
								gate = "false";
							}
							if(OutsideScreen.equals("true")){
								int voice = Outsidevoice.equals("false")==true ? 1 : 9;
								ListenableFuture<Boolean> carparkScreenVoiceDoor = messageService.carparkScreenVoiceDoor(device, 2, voice, 0, OpenDoorEnum.parse(gate), OutsideScreenAndVoiceData);
								Boolean boolean1 = carparkScreenVoiceDoor.get();
								if(boolean1 == null){
									carparkScreenVoiceDoor = messageService.carparkScreenVoiceDoor(device, 2, voice, 0, OpenDoorEnum.parse(gate), OutsideScreenAndVoiceData);
									carparkScreenVoiceDoor.get();
								}
							}
							HardwareUtil.responseDeviceControl(session,dom);		
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}).start();
			}
		}

		@Override
		public void messageSent(IoSession session, Object message) throws Exception {
			// TODO Auto-generated method stub
			super.messageSent(session, message);
		}
		
	}
		
	class AcceptorMessageHandler extends IoHandlerAdapter {

		
		@Override
		public void sessionCreated(IoSession session) throws Exception {
			super.sessionCreated(session);
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
							isPlayVoice = true;
							Element controlElement = rootElement.element("control");
							Element element = rootElement.element("device");
							
							String deviceName = element.element("deviceName").getTextTrim();
							String gate = controlElement.element("gate").getTextTrim();

							String Insidevoice,Outsidevoice,InsideScreen,OutsideScreen,InsideScreenAndVoiceData,OutsideScreenAndVoiceData;
							if(controlElement.element("insideVoice") == null){
								Insidevoice = controlElement.element("Insidevoice").getTextTrim();
								Outsidevoice = controlElement.element("Outsidevoice").getTextTrim();
								InsideScreen = controlElement.element("InsideScreen").getTextTrim();
								OutsideScreen = controlElement.element("OutsideScreen").getTextTrim();
								InsideScreenAndVoiceData = controlElement.element("InsideScreenAndVoiceData").getTextTrim();
								OutsideScreenAndVoiceData = controlElement.element("OutsideScreenAndVoiceData").getTextTrim();
							}else{
								Insidevoice = controlElement.element("insideVoice").getTextTrim();
								Outsidevoice = controlElement.element("outsideVoice").getTextTrim();
								InsideScreen = controlElement.element("insideScreen").getTextTrim();
								OutsideScreen = controlElement.element("outsideScreen").getTextTrim();
								InsideScreenAndVoiceData = controlElement.element("insideScreenAndVoiceData").getTextTrim();
								OutsideScreenAndVoiceData = controlElement.element("outsideScreenAndVoiceData").getTextTrim();
							}
							
							Device device = cs.getDeviceByName(deviceName);
							if(device == null){
								return;
							}
							if(InsideScreen.equals("true")){
								int voice = Insidevoice.equals("false")==true ? 1 : 9;
								ListenableFuture<Boolean> carparkScreenVoiceDoor = messageService.carparkScreenVoiceDoor(device, 1, voice, 0, OpenDoorEnum.parse(gate), InsideScreenAndVoiceData);
								Boolean boolean1 = carparkScreenVoiceDoor.get();
								if(boolean1 == null){
									carparkScreenVoiceDoor = messageService.carparkScreenVoiceDoor(device, 1, voice, 0, OpenDoorEnum.parse(gate), InsideScreenAndVoiceData);
									carparkScreenVoiceDoor.get();
								}
								gate = "false";
							}
							if(OutsideScreen.equals("true")){
								int voice = Outsidevoice.equals("false")==true ? 1 : 9;
								ListenableFuture<Boolean> carparkScreenVoiceDoor = messageService.carparkScreenVoiceDoor(device, 2, voice, 0, OpenDoorEnum.parse(gate), OutsideScreenAndVoiceData);
								Boolean boolean1 = carparkScreenVoiceDoor.get();
								if(boolean1 == null){
									carparkScreenVoiceDoor = messageService.carparkScreenVoiceDoor(device, 2, voice, 0, OpenDoorEnum.parse(gate), OutsideScreenAndVoiceData);
									carparkScreenVoiceDoor.get();
								}
							}
							HardwareUtil.responseDeviceControl(session,dom);		
						}catch(Exception e){
							e.printStackTrace();
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
			cause.printStackTrace();
		}

	}

}
