package com.dongluhitec.card.connect.body;

import java.io.UnsupportedEncodingException;

import com.dongluhitec.card.connect.MessageBody;
import com.dongluhitec.card.connect.exception.DongluInvalidMessageException;
import com.dongluhitec.card.connect.util.ByteUtils;

public class PrintStrBody implements MessageBody {

	public static final int LENGTH = 59;
	public static final int BIT_OFF_PRINTLOCATION = 0;
	public static final int BIT_OFF_PRINTSTYLE = 1;
	public static final int BIT_OFF_PLATECODE_START= 2;

	//进口标识
	public static final byte VALUE_OFF_PRINTLOCATION1 = 0x01;
	//出口标识
	public static final byte VALUE_OFF_PRINTLOCATION2 = 0x02;
	//显示方式,默认用滚屏
	public static final byte VALUE_OFF_PRINTSTYLE = 0x20;
	//结束标志
	public static final byte value_OFF_END = 0x0D;
	
	private String printStr;
	private LPRInOutType lprInOutType;
	
	@Override
	public void initContent(byte[] bytes) throws DongluInvalidMessageException {
		if(bytes[BIT_OFF_PRINTLOCATION] == VALUE_OFF_PRINTLOCATION1){
			setLprInOutType(LPRInOutType.进口);
		}else{
			setLprInOutType(LPRInOutType.出口);
		}
		
		int position = 0;
		for(int i=0;i<bytes.length;i++){
			if(bytes[i] == value_OFF_END){
				position = i;
			}
		}
		byte[] array = new byte[position+1];
		System.arraycopy(bytes, BIT_OFF_PRINTSTYLE, array, 0,position+1);
		try {
			printStr = new String(array,"gb2312");
		} catch (UnsupportedEncodingException e) {
			throw new DongluInvalidMessageException("转换字幕信息失败");
		}
	}

	@Override
	public byte[] toBytes() {
		byte[] result = new byte[PrintStrBody.LENGTH];
		String encodeChinese =ByteUtils.encodeChinese(printStr);
		if (encodeChinese.length() % 2 != 0) {
			encodeChinese = "0" + printStr;
		}

		for (int i = 0, ai = BIT_OFF_PLATECODE_START; i < encodeChinese.length(); i += 2, ai++) {
			String substring = encodeChinese.substring(i , i+2);
			result[ai] = ByteUtils.hexStringToByte(substring);
		}
		result[BIT_OFF_PRINTLOCATION] = getLprInOutTypeByte();
		result[BIT_OFF_PRINTSTYLE] = VALUE_OFF_PRINTSTYLE;
		result[encodeChinese.length()/2 + 2] = value_OFF_END;
		
		
		return result;
	}
	
	public void setPrintStr(String printStr) {
		this.printStr = printStr;
	}

	public String getPrintStr() {
		return printStr;
	}

	public LPRInOutType getLprInOutType() {
		return lprInOutType;
	}
	
	public byte getLprInOutTypeByte() {
		if(lprInOutType == LPRInOutType.出口){
			return VALUE_OFF_PRINTLOCATION2;
		}else{
			return VALUE_OFF_PRINTLOCATION1;
		}
	}

	public void setLprInOutType(LPRInOutType lprInOutType) {
		this.lprInOutType = lprInOutType;
	}
	
}
