package com.dongluhitec.card.hardware;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.dongluhitec.card.connect.DirectonType;
import com.dongluhitec.card.connect.Message;
import com.dongluhitec.card.connect.MessageBody;
import com.dongluhitec.card.connect.MessageConstance;
import com.dongluhitec.card.connect.MessageHeader;
import com.dongluhitec.card.connect.body.*;
import com.dongluhitec.card.connect.util.ByteUtils;
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

	public static Message<?> createReadNextRecordMsg(Device device) {
		String key = new StringBuffer().append(device.toString()).append("createReadNextRecordMsg").toString();
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

	public static Message<?> createSetDateTime(Device device, Date date) {
		SetDateTimeBody setDateTimeBody = new SetDateTimeBody();
		setDateTimeBody.setDate(date);

		SerialDeviceAddress serialDeviceAddress = new SerialDeviceAddress();
		serialDeviceAddress.setAddress(device.getArea());
		MessageHeader mh = new MessageHeader(serialDeviceAddress,DirectonType.请求,MessageConstance.Message_SetTime,SetDateTimeBody.LENGTH);

		return new Message<MessageBody>(mh, setDateTimeBody);
	}

	public static void main(String[] args) {
		Device device = new Device();
		device.setAddress("COM1");
		device.setArea("255.1");
		Message<?> readNowRecordMsg = createReadNowRecordMsg(device);
		System.out.println(ByteUtils.byteArrayToHexString(readNowRecordMsg.toBytes()));
	}

	public static Message<?> createADScreenMsg(Device device, String adStr) {
		String key = device.toString() + adStr;
		Message<MessageBody> message = commandMap.get(key);
		if(message != null){
			return message;
		}
		ADScreenBody adScreenBody = new ADScreenBody();
		adScreenBody.setText(adStr);

		SerialDeviceAddress serialDeviceAddress = new SerialDeviceAddress();
		serialDeviceAddress.setAddress(device.getArea());
		MessageHeader mh = new MessageHeader(serialDeviceAddress,DirectonType.请求,MessageConstance.Message_AD,ADScreenBody.LENGTH);

		return new Message<MessageBody>(mh, adScreenBody);
	}

}
