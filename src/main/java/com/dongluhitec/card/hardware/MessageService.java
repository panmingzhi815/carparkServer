package com.dongluhitec.card.hardware;

import com.dongluhitec.card.connect.body.OpenDoorEnum;
import com.dongluhitec.card.model.CarparkNowRecord;
import com.dongluhitec.card.model.Device;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Date;

public interface MessageService {

	/**
	 * 开门
	 * @param device
	 * @param openDoorEnum
	 * @return
	 */
	ListenableFuture<Boolean> carparkOpenDoor(Device device, OpenDoorEnum openDoorEnum);

	/**
	 * 读当前记录
	 * @param device
	 * @return
	 */
	ListenableFuture<CarparkNowRecord> carparkReadNowRecord(Device device);

	/**
	 * 控制显示屏,音量,字体
	 * @param device
	 * @param screenID
	 * @param voice
	 * @param font
	 * @param door
	 * @param text
	 * @return
	 */
	ListenableFuture<Boolean> carparkScreenVoiceDoor(Device device, int screenID, int voice, int font, int door, String text);

	/**
	 * 下发设备时间
	 * @param device 操作设备
	 * @param date 时间
	 */
	void setDateTime(Device device, Date date);

}
