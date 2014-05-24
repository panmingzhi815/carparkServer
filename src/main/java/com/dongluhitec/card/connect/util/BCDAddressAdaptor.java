package com.dongluhitec.card.connect.util;




public class BCDAddressAdaptor {

	final private SerialDeviceAddress address;

	public BCDAddressAdaptor(SerialDeviceAddress address) {
		this.address = address;
	}

	public BCDAddressAdaptor(byte[] bytes, int start) {
		int high_1 = Utils.BCDConvertFromByteToInt(bytes[start + 1]);
		int low_1 = Utils.BCDConvertFromByteToInt(bytes[start + 3]);
		this.address = new SerialDeviceAddress();
		this.address.setAddress(high_1, low_1);
	}

	public byte[] getBytes() {
		byte[] b = new byte[4];
		int firstAddrPart = this.address.getFirstAddrPart();
		int secondAddrPart = this.address.getSecondAddrPart();
		b[1] = Utils.BCDConvertFromIntToByte(firstAddrPart);
		b[3] = Utils.BCDConvertFromIntToByte(secondAddrPart);
		return b;
	}

	public SerialDeviceAddress getAddress() {
		return this.address;
	}

}
