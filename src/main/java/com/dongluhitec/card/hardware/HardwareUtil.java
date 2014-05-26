package com.dongluhitec.card.hardware;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.dongluhitec.card.EncryptException;
import com.dongluhitec.card.RSAUtils;
import com.dongluhitec.card.model.Device;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.Uninterruptibles;

public class HardwareUtil {

	private static final String MSGKEY = "message_prefix";

	private static IoSession currentSession;
	public static String he_publicKey;

	public static String checkSubpackage(IoSession session, Object message) {
		String msg = ((String) message).trim();
		if (msg.startsWith("<dongluCarpark")) {
			session.setAttribute(MSGKEY, "");
		}
		// 在此处对数据的分包进行拼接
		String oldValue = (String) session.getAttribute(MSGKEY);
		session.setAttribute(MSGKEY, oldValue + msg);
		// 如果数据已经完整,早返回整个数据
		if (msg.endsWith("</dongluCarpark>")) {
			return (String) session.getAttribute(MSGKEY);
		}
		// 数据不完整,则返回空
		return null;
	}

	public static void responsePublicKey(IoSession session, String message) {
		try {
			Document dom = DocumentHelper.parseText(message);
			Element element = dom.getRootElement().element("publicKey");
			he_publicKey = element.getStringValue();
			element.setText(RSAUtils.getPublicKeyString());
			session.write(dom.getRootElement().asXML());
		} catch (Exception e) {
			throw new EncryptException("客户端响应公钥失败", e);
		}
	}

	public static void responsePublicKey_server(IoSession session,
			String message) {
		try {
			Document dom = DocumentHelper.parseText(message);
			Element element = dom.getRootElement().element("publicKey");
			he_publicKey = element.getStringValue();
		} catch (Exception e) {
			throw new EncryptException("服务端响应公钥失败", e);
		}
	}

	public static String responseSwipeCardInfo(IoSession session, Document dom,
			Document dom2) {
		try{
			Element rootElement = dom.getRootElement();
			String deviceName = rootElement.element("device").element("deviceName")
					.getText();

			dom2.getRootElement().addElement("device").addElement("deviceName")
					.setText(deviceName);
			writeMsg(session, dom2.getRootElement().asXML());
			return dom2.getRootElement().asXML();
		}catch(Exception e){
			throw new EncryptException("响应刷卡", e);
		}
	}
	

	public static void sendCardNO(IoSession session,String cardNO,String readerID,String deviceName) {
		if(Strings.isNullOrEmpty(he_publicKey)){
			return;
		}
		try{
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("dongluCarpark");
			root.addAttribute("type", "swipeCard");
			
			Element deviceElement = root.addElement("device");
			deviceElement.addElement("deviceName").setText(deviceName);
			
			root.addElement("cardSerialNumber").setText(cardNO);
			root.addElement("CardReaderID").setText(readerID);
			HardwareUtil.writeMsg(session, document.getRootElement().asXML());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static String responseDeviceControl(IoSession session, Document dom) {
		try{
			String value = "<dongluCarpark type=\"result\"><result>true</result></dongluCarpark>";
			writeMsg(session, value);
			return value;
		}catch(Exception e){
			throw new EncryptException("响应设备控制失败", e);
		}
	}

	public static String formatDateTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
	
public static void sendDeviceInfo(IoSession session,List<Device> deviceList) {
		
		try {
			Document dom = DocumentHelper.createDocument();
			Element rootElement = dom.addElement("dongluCarpark");
			rootElement.addAttribute("type", "deviceInfo");
			Element station = rootElement.addElement("station");
			station.addElement("account").setText("donglu");
			station.addElement("password").setText( "liuhanzhong");
			station.addElement("stationName").setText( "前门岗亭");
			station.addElement("stationIP").setText( HardwareUtil.getLocalIP());
			station.addElement("stationTime").setText( HardwareUtil.formatDateTime(new Date()));
			
			for (Device device : deviceList) {
				Element monitor = rootElement.addElement("monitor");;
				Element deviceElement = monitor.addElement("device");
				deviceElement.addElement("deviceName").setText(device.getName());
				deviceElement.addElement("deviceInOutType").setText(device.getType().equals("进口") == true ? "in":"out");
				deviceElement.addElement("deviceDisplayAndVoiceInside").setText("false");
				deviceElement.addElement("deviceDisplayAndVoiceOutside").setText("true");
				deviceElement.addElement("deviceDisplaySupportChinese").setText("true");
			}
			
			HardwareUtil.writeMsg(session, dom.getRootElement().asXML());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getLocalIP() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostAddress().toString();
		} catch (Exception e) {
			return null;
		}

	}

	public static void writeMsg(IoSession ioSession, String msg) {
		try {
			ioSession.write(encode(msg));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String decode(String msg) {
		try{
			int indexOf = msg.indexOf(">")+1;
			String subStr = msg.substring(indexOf, msg.length()-16);
			
			String decrypt = RSAUtils.decrypt(subStr, RSAUtils.getPrivateKey());
			String replace = msg.replace(subStr, decrypt);
			return replace;
		}catch(Exception e){
			throw new EncryptException("解密失败", e);
		}
	}

	public static String encode(String msg) {
		try{
			int indexOf = msg.indexOf(">")+1;
			String subStr = msg.substring(indexOf, msg.length()-16);
			String encrypt = RSAUtils.encrypt(subStr, RSAUtils.getPublicKey(he_publicKey));
			String replace = msg.replace(subStr,encrypt);
			return replace;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static void changeDecretKey(IoSession ioSession) {
		try{
			Document document = DocumentHelper.createDocument();
			Element dongluCarpark = document.addElement("dongluCarpark");
			dongluCarpark.addAttribute("type", "publicKey");
			Element publicKey = dongluCarpark.addElement("publicKey");
			publicKey.setText(RSAUtils.getPublicKeyString());
			ioSession.write(document.getRootElement().asXML());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void controlSpeed(long start, long speed){
        long used = Math.abs(start - System.currentTimeMillis());
        if(used < speed){
            Uninterruptibles.sleepUninterruptibly(speed - used, TimeUnit.MILLISECONDS);
        }
    }
}
