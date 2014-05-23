package com.dongluhitec.card.hardware.message.util;

import java.util.HashMap;
import java.util.Map;

import com.dongluhitec.card.hardware.message.CommDirectionEnum;
import com.dongluhitec.card.hardware.message.Message;
import com.dongluhitec.card.hardware.message.MessageBody;
import com.dongluhitec.card.hardware.message.MessageConstance;
import com.dongluhitec.card.hardware.message.MessageHeader;
import com.dongluhitec.card.hardware.message.body.EmptyBody;
import com.dongluhitec.card.hardware.message.body.LPRInOutType;
import com.dongluhitec.card.hardware.message.body.PrintStrBody;
import com.dongluhitec.card.hardware.message.body.VoiceBody;
import com.dongluhitec.card.hardware.message.body.VoiceBody.Property;
import com.dongluhitec.card.model.Device;

public class MessageUtil {
	
	private static Map<String,Message<MessageBody>> commandMap = new HashMap<String,Message<MessageBody>>();
	
	public static Message<MessageBody> createReadCurrent(Device device){
		String key = device.toString()+".createReadCurrent";
		Message<MessageBody> message = commandMap.get(key);
		if(message != null){
			return message;
		}
		SerialDeviceAddress serialDeviceAddress = new SerialDeviceAddress();
		serialDeviceAddress.setAddress(device.getArea());
		
		MessageHeader mh = new MessageHeader();
		mh.setDataLength(EmptyBody.DATA_LENGTH);
		mh.setDeviceAddress(new SerialDeviceAddress());
		mh.setDirection(CommDirectionEnum.Request);
		mh.setFunctionCode(MessageConstance.Message_ReadNowRecord);
		
		MessageBody mg = new EmptyBody();
		
		Message<MessageBody> msg = new Message<MessageBody>(mh, mg);
		commandMap.put(key, msg);
		return msg;
	}
	
	public static Message<MessageBody> createPrintStr(Device device,String content){
		String key = device.toString()+".createPrintStr";
		Message<MessageBody> message = commandMap.get(key);
		if(message != null){
			return message;
		}
		SerialDeviceAddress serialDeviceAddress = new SerialDeviceAddress();
		serialDeviceAddress.setAddress(device.getArea());
		
		MessageHeader mh = new MessageHeader();
		mh.setDataLength(PrintStrBody.LENGTH);
		mh.setDeviceAddress(new SerialDeviceAddress());
		mh.setDirection(CommDirectionEnum.Request);
		mh.setFunctionCode(MessageConstance.Message_PrintStr);
		
		PrintStrBody mg = new PrintStrBody();
		mg.setLprInOutType(LPRInOutType.valueOf(device.getInoutType()));
		mg.setPrintStr(content);
		
		Message<MessageBody> msg = new Message<MessageBody>(mh, mg);
		commandMap.put(key, msg);
		return msg;
	}
	
	public static Message<MessageBody> createVoice(Device device,Property p){
		String key = device.toString()+".createVoice";
		Message<MessageBody> message = commandMap.get(key);
		if(message != null){
			return message;
		}
		SerialDeviceAddress serialDeviceAddress = new SerialDeviceAddress();
		serialDeviceAddress.setAddress(device.getArea());
		
		MessageHeader mh = new MessageHeader();
		mh.setDataLength(VoiceBody.LENGTH);
		mh.setDeviceAddress(new SerialDeviceAddress());
		mh.setDirection(CommDirectionEnum.Request);
		mh.setFunctionCode(MessageConstance.Message_Voice);
		
		VoiceBody mg = new VoiceBody();
		mg.setSendVoice(p);
		
		Message<MessageBody> msg = new Message<MessageBody>(mh, mg);
		commandMap.put(key, msg);
		return msg;
	}

}
