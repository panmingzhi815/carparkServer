package com.dongluhitec.card.connect;

public class MessageConstance {

    public final static int MESSAGE_HEADER_LENGTH = 7;
    public final static int MESSAGE_HEADER_MID_ADDR = 2;
    public final static int MESSAGE_HEADER_SID_ADDR = 4;
    public final static int MESSAGE_HEADER_FUNCCODE = 6;
    public final static int MESSAGE_HEADER_SOH = 0;
    public final static int MESSAGE_HEADER_SOH_VALUE = 0X01;
    public final static int MESSAGE_HEADER_PT = 1;
    public final static int MESSAGE_DATA_EXTRA_LENGTH = 3;
    public final static int MESSAGE_DATA_STX = 0;
    public final static int MESSAGE_DATA = 1;
    public final static int MESSAGE_DATA_STX_VALUE = 0X02;
    public final static int MESSAGE_DATA_ETX_VALUE = 0X03;
    
    public static final byte Message_ReadNowRecord = (byte) 0XAC;
    
    public static final byte Message_PrintStr = (byte) 0XA9;

    public static final byte Message_Voice = (byte) 0XAB;
}
