package com.dongluhitec.card.hardware.message;

import com.dongluhitec.card.hardware.message.util.BCDAddressAdaptor;
import com.dongluhitec.card.hardware.message.util.ByteUtils;
import com.dongluhitec.card.hardware.message.util.Bytenize;
import com.dongluhitec.card.hardware.message.util.SerialDeviceAddress;


/**
 * 消息头.
 *
 * @author wudong
 */
public class MessageHeader implements Bytenize {

    private SerialDeviceAddress deviceAddress;
    private CommDirectionEnum direction;
    private byte functionCode;
    private int dataLength;

    public MessageHeader() {
    }

    public MessageHeader(byte[] bb, int start)
            throws DongluInvalidMessageException {
        this.checkHeader(bb, start);
    }

    public SerialDeviceAddress getDeviceAddress() {
        return this.deviceAddress;
    }

    public void setDeviceAddress(SerialDeviceAddress deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    public int getDataLength() {
        return this.dataLength;
    }

    public CommDirectionEnum getDirection() {
        return this.direction;
    }

    public void setDirection(CommDirectionEnum direction) {
        this.direction = direction;
    }

    public byte getFunctionCode() {
        return this.functionCode;
    }

    public void setFunctionCode(byte functionCode) {
        this.functionCode = functionCode;
    }

    /**
     * 检查消息头是否合法, 并初始化消息头对象。
     *
     * @param header2
     * @return
     * @throws DongluInvalidMessageException
     */
    private void checkHeader(byte[] header2, int start)
            throws DongluInvalidMessageException {
        byte soh = header2[start + MessageConstance.MESSAGE_HEADER_SOH];
        if (soh != MessageConstance.MESSAGE_HEADER_SOH_VALUE)
            throw new DongluInvalidMessageException("消息头格式不合法: SOH=[" + ByteUtils.byteArrayToHexString(header2) + "]");

        byte dir = header2[start + MessageConstance.MESSAGE_HEADER_PT];
        if (dir == 'W') {
            this.direction = CommDirectionEnum.Request;
        } else if (dir == 'w') {
            this.direction = CommDirectionEnum.Response;
        } else
            throw new DongluInvalidMessageException("消息头通讯方向不合法: DIR=[" + ByteUtils.byteToHexString(dir) + "]");

        // initialize from the header.
        try {
            this.deviceAddress = new BCDAddressAdaptor(header2, start
                    + MessageConstance.MESSAGE_HEADER_MID_ADDR).getAddress();
        } catch (IllegalArgumentException e) {
            throw new DongluInvalidMessageException("消息头地址定义不合法: " + e.getMessage());
        }

        // other header parameter.
        this.functionCode = header2[start
                + MessageConstance.MESSAGE_HEADER_FUNCCODE];
    }

    @Override
    public byte[] toBytes() {
        byte[] header = new byte[MessageConstance.MESSAGE_HEADER_LENGTH];
        header[MessageConstance.MESSAGE_HEADER_SOH] = MessageConstance.MESSAGE_HEADER_SOH_VALUE;
        header[MessageConstance.MESSAGE_HEADER_PT] = (byte) (this.direction == CommDirectionEnum.Request ? 'W'
                : 'w');

        byte[] bytes2 = new BCDAddressAdaptor(this.deviceAddress).getBytes();
        System.arraycopy(bytes2, 0, header,
                MessageConstance.MESSAGE_HEADER_MID_ADDR, bytes2.length);

        header[MessageConstance.MESSAGE_HEADER_FUNCCODE] = this.functionCode;
        return header;
    }

    @Override
    public String toString() {
        return "Header [Addr.=" + this.deviceAddress + ", Dir.="
                + this.direction + ", FC="
                + ByteUtils.byteToHexString(this.functionCode) + ", length="
                + this.dataLength + "]";
    }

}
