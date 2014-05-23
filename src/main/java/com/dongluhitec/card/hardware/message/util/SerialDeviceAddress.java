package com.dongluhitec.card.hardware.message.util;


public class SerialDeviceAddress{

	private String address;

	public String getAddress() {
		return this.address;
	}


	public static final char DILIMITER = '.';

	public static SerialDeviceAddress createAddress(int i, int j) {
		SerialDeviceAddress serialDeviceAddress = new SerialDeviceAddress();
		serialDeviceAddress.setAddress(i, j);
		return serialDeviceAddress;
	}

	public SerialDeviceAddress() {
		this.setAddress(1, 1);
	}

	public int getFirstAddrPart() {
		String addr = this.getAddress();
		int indexOf = this.getAddress().indexOf(SerialDeviceAddress.DILIMITER);
		String substring = addr.substring(0, indexOf);
		return Integer.parseInt(substring);
	}

	public int getSecondAddrPart() {
		String addr = this.getAddress();
		int indexOf = this.getAddress().indexOf(SerialDeviceAddress.DILIMITER);
		String substring = addr.substring(indexOf + 1);
		return Integer.parseInt(substring);
	}

	public void setAddress(int first, int second) {
		this.setAddress(Integer.toString(first) + SerialDeviceAddress.DILIMITER
				+ Integer.toString(second));
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
