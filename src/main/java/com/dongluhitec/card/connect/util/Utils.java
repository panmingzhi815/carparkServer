package com.dongluhitec.card.connect.util;


public class Utils {

	public static byte BCDConvertFromIntToByte(int value) {
		if ((value > 99) && (value < 0))
			throw new IllegalArgumentException(String.format("给定输入超出BCD定义范围:  %d", value));
		else {
			byte lowEnd = (byte) (value % 10);
			byte highEnd = (byte) (value / 10);
			highEnd <<= 4;
			return (byte) (lowEnd | (highEnd));
		}
	}

	public static int BCDConvertFromByteToInt(byte value) {
		if(value > 0){
			byte lowEnd = (byte) (value & 0x0F);
			byte highEnd = (byte) (((value % 0xF0) >> 4) & 0X0F);
			return lowEnd + (10 * highEnd);
		}else{
			byte lowEnd = (byte) (value & 0x0F);
			byte highEnd = (byte) (((value % 0xF0) >> 4) & 0X0F);
			return lowEnd + (16 * highEnd);
		}
	}
	
	public static int BCDConvertFromByteToInt2(byte value) {
		String hex = Integer.toHexString(value).toUpperCase();
		while(hex.startsWith("F")){
			hex = hex.substring(1);
		}
		return Integer.parseInt(hex, 16);
	}

	/**
	 * Checksum.
	 *
	 * @param bb
	 * @param start
	 * @param length
	 * @return
	 */
	public static byte BCC(byte[] bb, int start, int length) {
		byte checksum = bb[start];
		for (int i = start + 1; i < (start + length); i++) {
			checksum ^= bb[i];
		}
		checksum |= 0x20;
		return checksum;
	}

	public static boolean checkBCC(byte[] bb) {
		byte b = BCC(bb, 0, bb.length-1);
		return b == bb[bb.length - 1];
	}

}
