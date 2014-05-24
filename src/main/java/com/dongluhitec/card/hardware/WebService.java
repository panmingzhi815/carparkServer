package com.dongluhitec.card.hardware;

import java.util.List;

import com.dongluhitec.card.model.Device;

public interface WebService {
	
	public void sendSecretKey();
	
	public void responseSecretKey();
	
	public void sendDeviceInfo(List<Device> deviceList);
	
	public void responseResult();
	
	public void sendCardID(String cardID);
	
	public void responseControl();

}
