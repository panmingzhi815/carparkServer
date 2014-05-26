package com.dongluhitec.card.model;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

public class CarparkSetting implements Serializable{
	private static final long serialVersionUID = 1268792761972568870L;
	
	private String password = "123456";
	
	private String ip;
	private String port;
	
	private List<Device> deviceList = Lists.newArrayList();
	
	public Device getDeviceByName(String deviceName){
		for (Device device : deviceList) {
			if(device.getName().equals(deviceName)){
				return device;
			}
		}
		return null;
	}

	public String getIp() {
		if(ip == null){
			return "192.168.1.1";
		}
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		if(port == null){
			return "9123";
		}
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public List<Device> getDeviceList() {
		return deviceList;
	}

	public void setDeviceList(List<Device> deviceList) {
		this.deviceList = deviceList;
	}

	public String getPassword() {
		if(password == null) return "123456";
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
