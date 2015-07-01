package com.dongluhitec.card.hardware;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import com.dongluhitec.card.plate.XinlutongCallback.XinlutongResult;
import com.dongluhitec.card.plate.XinlutongJNAImpl;
import com.dongluhitec.card.util.EventBusUtil;
import com.dongluhitec.card.util.EventInfo;
import com.dongluhitec.card.util.EventInfo.EventType;
import com.google.common.util.concurrent.ListenableFuture;

public class HardwareService {
	private Logger LOGGER = LoggerFactory.getLogger(HardwareService.class);
	public static HardwareService service = null;
	private static MessageService messageService = null;
	private ConnectFuture cf = null;
	private NioSocketConnector connector;
	private static CarparkSetting cs;
	private final long checkConnectorSecond = 3;
	private NioSocketAcceptor acceptor;
	private ExecutorService newSingleThreadExecutor;
	private final int PORT = 9124;
	private XinlutongResult xlr;
	
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
		if(newSingleThreadExecutor == null){
			newSingleThreadExecutor = Executors.newSingleThreadExecutor();
		}
		startWebConnector();
		checkDateTime();
		startLogging();
		startListne();
		startPlateMonitor();
	}
	
	private void startPlateMonitor() {
		final String plateDeviceip = cs.getPlateDeviceip();
		final String name = cs.getDeviceList().get(0).getName();
		if(plateDeviceip != null && !plateDeviceip.trim().isEmpty()){
			if(xlr == null){
    			xlr = new XinlutongResult() {
    				@Override
    				public void invok(String ip, int channel, String plateNO, byte[] bigImage, byte[] smallImage) {
    					HardwareUtil.setPlateInfo(cf.getSession(),name,ip,plateNO,bigImage,smallImage);
    				}
    			};
			}
			new XinlutongJNAImpl().openEx(plateDeviceip, xlr);
		}
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
						long start = System.currentTimeMillis();
						try{
							if(isPlayVoice == true){
								HardwareUtil.controlSpeed(start, 300);
								isPlayVoice = false;
							}
							ListenableFuture<CarparkNowRecord> carparkReadNowRecord = messageService.carparkReadNowRecord(device);
							CarparkNowRecord carparkNowRecord = carparkReadNowRecord.get();
							if(carparkNowRecord != null){
								HardwareUtil.sendCardNO(cf.getSession(), carparkNowRecord.getCardID(),carparkNowRecord.getReaderID()+"", device.getName());
								HardwareUtil.controlSpeed(start, 3000);
							}
							EventBusUtil.post(new EventInfo(EventType.硬件通讯正常, "硬件通讯恢复正常"));
						}catch(Exception e){
							EventBusUtil.post(new EventInfo(EventType.硬件通讯异常, "当前主机与停车场硬件设备通讯时发生异常,请检查"));
						}finally{
							HardwareUtil.controlSpeed(start, 400);
						}
					}
				}catch(Exception e){}
			}
		},5000,100);
	}
	
	private void checkDateTime(){
		Timer timer = new Timer("check date time");
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try{
					List<Device> deviceList = cs.getDeviceList();
					for (Device device : deviceList) {
						Date date = new Date();
						LOGGER.debug("开始设置设备{}时间:{}",device.getName(),date);
						
						long start = System.currentTimeMillis();
						try{
							if(isPlayVoice == true){
								HardwareUtil.controlSpeed(start, 10000);
							}
							messageService.setDateTime(device, date);
							EventBusUtil.post(new EventInfo(EventType.硬件通讯正常, "硬件通讯恢复正常"));
						}catch(Exception e){
							EventBusUtil.post(new EventInfo(EventType.硬件通讯异常, "当前主机与停车场硬件设备通讯时发生异常,请检查"));
						}finally{
							HardwareUtil.controlSpeed(start, 400);
						}
					}
				}catch(Exception e){}
			}
		},3000,1000*60*30);
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
			connector.setHandler(new listenHandler());
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
		},5000,100);
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
			
			if(wm.getType() == WebMessageType.成功){
				HardwareUtil.responseResult(session,dom);
				return;
			}
			
			if(wm.getType() == WebMessageType.广告){
				newSingleThreadExecutor.submit(new Runnable() {
					
					@Override
					public void run() {
						try{
							String deviceName = rootElement.element("device").element("deviceName").getTextTrim();
							String ad = rootElement.element("ad").getTextTrim();
							
							Device device = cs.getDeviceByName(deviceName);
							ListenableFuture<Boolean> setAD = messageService.setAD(device, ad);
							setAD.get();
							
							HardwareUtil.responseDeviceControl(session,dom);	
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
				
			}
			
			if(wm.getType() == WebMessageType.设备控制){
				newSingleThreadExecutor.submit(new Runnable() {
					
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
				});
			}
		}

		@Override
		public void messageSent(IoSession session, Object message) throws Exception {
			// TODO Auto-generated method stub
			super.messageSent(session, message);
		}
		
	}

}
