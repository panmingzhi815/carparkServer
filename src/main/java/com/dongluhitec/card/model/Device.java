package com.dongluhitec.card.model;

import java.io.Serializable;

import com.google.common.base.Objects;

public class Device implements Serializable{
	
	private static final long serialVersionUID = 6507921821879355543L;
	
	private String name;
	private String type;
	private String address;
	private String area;
	private String inoutType;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	
	public String getInoutType() {
		return inoutType;
	}
	public void setInoutType(String inoutType) {
		this.inoutType = inoutType;
	}
	@Override
	public String toString() {
		return "Device [name=" + name + ", type=" + type + ", address=" + address + ", area=" + area + ", inoutType=" + inoutType + "]";
	}
	
	
}
