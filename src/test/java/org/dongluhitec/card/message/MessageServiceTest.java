package org.dongluhitec.card.message;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dongluhitec.card.connect.body.OpenDoorEnum;
import com.dongluhitec.card.hardware.MessageService;
import com.dongluhitec.card.hardware.impl.MessageServiceImpl;
import com.dongluhitec.card.model.CarparkNowRecord;
import com.dongluhitec.card.model.Device;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Uninterruptibles;

public class MessageServiceTest {
	
	private static MessageService messageService;
	private static Device device;
	
	@BeforeClass
	public static void before(){
		messageService = new MessageServiceImpl();
		device = new Device();
		device.setAddress("COM1");
		device.setArea("1.1");
		device.setInoutType("进口");
		device.setName("测试设备");
		device.setType("COM");
	}
	
	@Test
	public void sendOpenDoor() throws InterruptedException, ExecutionException{
		ListenableFuture<Boolean> carparkOpenDoor1 = messageService.carparkOpenDoor(device, OpenDoorEnum.抬闸);
		carparkOpenDoor1.get();
		Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
		ListenableFuture<Boolean> carparkOpenDoor2 = messageService.carparkOpenDoor(device, OpenDoorEnum.落闸);
		carparkOpenDoor2.get();
		Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
		ListenableFuture<Boolean> carparkOpenDoor3 = messageService.carparkOpenDoor(device, OpenDoorEnum.暂停);
		carparkOpenDoor3.get();
	}
	
	@Test
	public void sendReadNowRecord() throws InterruptedException, ExecutionException{
		ListenableFuture<CarparkNowRecord> carparkReadNowRecord = messageService.carparkReadNowRecord(device);
		carparkReadNowRecord.get();
		Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
	}
	
	@Test
	public void sendScreenVoiceDoor() throws InterruptedException, ExecutionException{
		{
			int screenID = 1;
			int voice = 0;
			int door = 3;
			int font = 0;
			String text = "欢迎光临";
			ListenableFuture<Boolean> carparkScreenVoiceDoor = messageService.carparkScreenVoiceDoor(device, screenID, voice, font, door, text);
			carparkScreenVoiceDoor.get();
			
		}
		Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
		{
			int screenID = 1;
			int voice = 0;
			int door = 3;
			int font = 0;
			String text = "请入场停车";
			ListenableFuture<Boolean> carparkScreenVoiceDoor = messageService.carparkScreenVoiceDoor(device, screenID, voice, font, door, text);
			carparkScreenVoiceDoor.get();
		}
	}
	
}
