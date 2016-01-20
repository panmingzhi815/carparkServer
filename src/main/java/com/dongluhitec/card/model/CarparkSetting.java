package com.dongluhitec.card.model;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

public class CarparkSetting implements Serializable{
	private static final long serialVersionUID = 1268792761972568870L;
	
	private String password = "123456";
	
	private String ip;
	private String port;
	private String ad;
	private String stationName;
	
	private List<Device> deviceList = Lists.newArrayList();
	
	public Device getDeviceByName(String deviceName){
		for (Device device : deviceList) {
			if(device.getName().equals(deviceName)){
				return device;
			}
		}
		return null;
	}

	public String getStationName() {
		if(stationName == null){
			return "岗亭1";
		}
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
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

	public String getAd() {
		if(ad == null){
			return "深圳市东陆高新";
		}
		return ad;
	}

	public void setAd(String ad) {
		this.ad = ad;
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
