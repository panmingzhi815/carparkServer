package com.dongluhitec.card.connect;

import com.dongluhitec.card.connect.exception.DongluInvalidMessageException;
import com.dongluhitec.card.connect.util.BCDAddressAdaptor;
import com.dongluhitec.card.connect.util.ByteUtils;
import com.dongluhitec.card.connect.util.SerialDeviceAddress;

public class MessageHeader implements Bytenize {
	private SerialDeviceAddress serialDeviceAddress;
	private DirectonType directonType;
	private byte functionCode;
	private int dataLength;

	public MessageHeader(SerialDeviceAddress serialDeviceAddress, DirectonType directonType, byte functionCode, int dataLength) {
		super();
		this.serialDeviceAddress = serialDeviceAddress;
		this.directonType = directonType;
		this.functionCode = functionCode;
		this.dataLength = dataLength;
	}

	public MessageHeader(byte[] bb, int start) throws DongluInvalidMessageException {
		this.checkHeader(bb, start);
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public int getDataLength() {
		return this.dataLength;
	}

	public byte getFunctionCode() {
		return this.functionCode;
	}

	public void setFunctionCode(byte functionCode) {
		this.functionCode = functionCode;
	}

	public SerialDeviceAddress getSerialDeviceAddress() {
		return serialDeviceAddress;
	}

	public void setSerialDeviceAddress(SerialDeviceAddress serialDeviceAddress) {
		this.serialDeviceAddress = serialDeviceAddress;
	}

	public DirectonType getDirectonType() {
		return directonType;
	}

	public void setDirectonType(DirectonType directonType) {
		this.directonType = directonType;
	}

	/**
	 * 检查消息头是否合法, 并初始化消息头对象。
	 * 
	 * @param header2
	 * @return
	 * @throws DongluInvalidMessageException
	 */
	private void checkHeader(byte[] header2, int start) throws DongluInvalidMessageException {
		byte soh = header2[start + MessageConstance.MESSAGE_HEADER_SOH];
		if (soh != MessageConstance.MESSAGE_HEADER_SOH_VALUE)
			throw new DongluInvalidMessageException("消息头格式不合法: SOH=[" + ByteUtils.byteArrayToHexString(header2) + "]");

		byte dir = header2[start + MessageConstance.MESSAGE_HEADER_PT];
		if (dir == 'W') {
			this.directonType = DirectonType.请求;
		} else if (dir == 'w') {
			this.directonType = DirectonType.响应;
		} else
			throw new DongluInvalidMessageException("消息头通讯方向不合法: DIR=[" + ByteUtils.byteToHexString(dir) + "]");

		try {
			this.serialDeviceAddress = new BCDAddressAdaptor(header2, start + MessageConstance.MESSAGE_HEADER_MID_ADDR).getAddress();
		} catch (IllegalArgumentException e) {
			throw new DongluInvalidMessageException("消息头地址定义不合法: " + e.getMessage());
		}

		this.functionCode = header2[start + MessageConstance.MESSAGE_HEADER_FUNCCODE];
	}

	@Override
	public byte[] toBytes() {
		byte[] header = new byte[MessageConstance.MESSAGE_HEADER_LENGTH];
		header[MessageConstance.MESSAGE_HEADER_SOH] = MessageConstance.MESSAGE_HEADER_SOH_VALUE;
		header[MessageConstance.MESSAGE_HEADER_PT] = (byte) (this.directonType == DirectonType.请求 ? 'W' : 'w');

		byte[] bytes2 = new byte[4];
		bytes2[1] = (byte)this.serialDeviceAddress.getFirstAddrPart();
		bytes2[3] = (byte)this.serialDeviceAddress.getSecondAddrPart();
//		byte[] bytes2 = new BCDAddressAdaptor(this.serialDeviceAddress).getBytes();
		System.arraycopy(bytes2, 0, header, MessageConstance.MESSAGE_HEADER_MID_ADDR, bytes2.length);

		header[MessageConstance.MESSAGE_HEADER_FUNCCODE] = this.functionCode;
		return header;
	}

	@Override
	public String toString() {
		return "Header [Addr.=" + this.serialDeviceAddress + ", Dir.=" + this.directonType + ", FC=" + ByteUtils.byteToHexString(this.functionCode) + ", length=" + this.dataLength + "]";
	}

}
