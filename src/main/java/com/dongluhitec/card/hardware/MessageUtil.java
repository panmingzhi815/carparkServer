package com.dongluhitec.card.hardware;

import java.util.HashMap;
import java.util.Map;

import com.dongluhitec.card.connect.DirectonType;
import com.dongluhitec.card.connect.Message;
import com.dongluhitec.card.connect.MessageBody;
import com.dongluhitec.card.connect.MessageConstance;
import com.dongluhitec.card.connect.MessageHeader;
import com.dongluhitec.card.connect.body.EmptyBody;
import com.dongluhitec.card.connect.body.OpenDoorEnum;
import com.dongluhitec.card.connect.body.ScreenVoiceDoorBody;
import com.dongluhitec.card.connect.body.SimpleBody;
import com.dongluhitec.card.connect.body.VoiceBody;
import com.dongluhitec.card.connect.util.SerialDeviceAddress;
import com.dongluhitec.card.model.Device;

public class MessageUtil {
	
	private static Map<String,Message<MessageBody>> commandMap = new HashMap<String,Message<MessageBody>>();
	
	public static Message<?> createOpenDoorMsg(Device device,OpenDoorEnum openDoorEnum) {
		String key = new StringBuffer().append(device.toString()).append(openDoorEnum).append("createOpenDoorMsg").toString();
		Message<MessageBody> message = commandMap.get(key);
		if(message != null){
			return message;
		}
		
		SerialDeviceAddress serialDeviceAddress = new SerialDeviceAddress();
		serialDeviceAddress.setAddress(device.getArea());
		MessageHeader mh = new MessageHeader(serialDeviceAddress,DirectonType.请求,MessageConstance.Message_OpenDoor,SimpleBody.LENGTH);
		SimpleBody sb = new SimpleBody();
		sb.setSimpleBody((byte)openDoorEnum.getI());
		
		Message<MessageBody> msg = new Message<MessageBody>(mh, sb);
		commandMap.put(key, msg);
		return msg;
	}

	public static Message<?> createReadNowRecordMsg(Device device) {
		String key = new StringBuffer().append(device.toString()).append("createReadNowRecordMsg").toString();
		Message<MessageBody> message = commandMap.get(key);
		if(message != null){
			return message;
		}
		
		SerialDeviceAddress serialDeviceAddress = new SerialDeviceAddress();
		serialDeviceAddress.setAddress(device.getArea());
		MessageHeader mh = new MessageHeader(serialDeviceAddress,DirectonType.请求,MessageConstance.Message_ReadNowRecord,EmptyBody.LENGTH);
		EmptyBody sb = new EmptyBody();
		Message<MessageBody> msg = new Message<MessageBody>(mh, sb);
		commandMap.put(key, msg);
		return msg;
	}
	
	public static Message<?> createScreenVoiceDoorMsg(Device device, int screenID, int voice, int font, int door, String text) {
		String key = new StringBuffer().append(device.toString()).append(screenID).append(voice).append(font).append(door).append(text).append("createScreenVoiceDoorMsg").toString();
		Message<MessageBody> message = commandMap.get(key);
		if(message != null){
			return message;
		}
		
		SerialDeviceAddress serialDeviceAddress = new SerialDeviceAddress();
		serialDeviceAddress.setAddress(device.getArea());
		MessageHeader mh = new MessageHeader(serialDeviceAddress,DirectonType.请求,MessageConstance.Message_ScreenVoiceDoor,ScreenVoiceDoorBody.LENGTH);
		ScreenVoiceDoorBody sb = new ScreenVoiceDoorBody();
		sb.setDoor(door);
		sb.setFont(font);
		sb.setScreenID(screenID);
		sb.setVoice(voice);
		sb.setText(text);
		
		Message<MessageBody> msg = new Message<MessageBody>(mh, sb);
		commandMap.put(key, msg);
		return msg;
	}

}
