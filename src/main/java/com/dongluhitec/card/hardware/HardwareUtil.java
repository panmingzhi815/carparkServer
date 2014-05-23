package com.dongluhitec.card.hardware;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.mina.core.session.IoSession;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.dongluhitec.card.EncryptException;
import com.dongluhitec.card.RSAUtils;
import com.google.common.base.Strings;

public class HardwareUtil {

	private static final String MSGKEY = "message_prefix";

	private static IoSession currentSession;
	private static String deviceName;
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

	public static String responseDeviceInfo(IoSession session, Document dom) {
		try{
			currentSession = session;
			deviceName = dom.getRootElement().element("monitor").element("device")
					.element("deviceName").getText();

			String value = "<dongluCarpark type=\"result\"><result>true</result></dongluCarpark>";
			writeMsg(session, value);
			return value;
		}catch(Exception e){
			throw new EncryptException("响应设备信息失败", e);
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

	public static String responseDeviceControl(IoSession session, Document dom) {
		try{
			String value = "<dongluCarpark type=\"result\"><result>true</result></dongluCarpark>";
			writeMsg(session, value);
			return value;
		}catch(Exception e){
			throw new EncryptException("响应设备控制失败", e);
		}
	}

	public static void requestDeviceControl(Document state2Xml) {
		try{
			if (currentSession == null || currentSession.isConnected() == false
					|| Strings.isNullOrEmpty(deviceName)) {
				return;
			}
			Element rootElement = state2Xml.getRootElement();
			Element deviceElement = rootElement.addElement("device");
			deviceElement.addElement("deviceName").setText(deviceName);
			writeMsg(currentSession, rootElement.asXML());
		}catch(Exception e){
			throw new EncryptException("请求设备控制失败", e);
		}
	}

	public static String formatDateTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
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
			System.out.println("decode str:"+msg);
			int indexOf = msg.indexOf(">")+1;
			String subStr = msg.substring(indexOf, msg.length()-16);
			
			String decrypt = RSAUtils.decrypt(subStr, RSAUtils.getPrivateKey());
			String replace = msg.replace(subStr, decrypt);
			System.out.println("decoded str:"+msg);
			return replace;
		}catch(Exception e){
			throw new EncryptException("解密失败", e);
		}
	}

	public static String encode(String msg) {
		try{
			System.out.println("encode str:"+msg);
			int indexOf = msg.indexOf(">")+1;
			String subStr = msg.substring(indexOf, msg.length()-16);
			String encrypt = RSAUtils.encrypt(subStr, RSAUtils.getPublicKey(he_publicKey));
			String replace = msg.replace(subStr,encrypt);
			System.out.println("encoded str:"+replace);
			return replace;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		String a = "123";
		String b = a.replace("1", "1235");
		System.out.println(b);
	}

}
