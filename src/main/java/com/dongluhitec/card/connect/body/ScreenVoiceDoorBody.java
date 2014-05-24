package com.dongluhitec.card.connect.body;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dongluhitec.card.connect.MessageBody;
import com.dongluhitec.card.connect.exception.DongluHWException;
import com.dongluhitec.card.connect.exception.DongluInvalidMessageException;
import com.dongluhitec.card.connect.util.ByteUtils;

public class ScreenVoiceDoorBody implements MessageBody {

	public static final int LENGTH = 84;

	final private int BIT_OF_SCREENID = 0;
	final private int BIT_OF_SCREENID_LENGTH = 1;

	final private int BIT_OF_VOICE = 1;
	final private int BIT_OF_VOICE_LENGTH = 1;

	final private int BIT_OF_FONT = 2;
	final private int BIT_OF_FONT_LENGTH = 1;

	final private int BIT_OF_DOOR = 3;
	final private int BIT_OF_DOOR_LENGTH = 1;

	final private int BIT_OF_TEXT = 4;
	final private int BIT_OF_TEXT_LENGTH = 80;

	private int screenID = 1;
	private int voice = 0;
	private int font = 0;
	private int door = 0;
	private String text;

	@Override
	public void initContent(byte[] bytes) throws DongluInvalidMessageException {
		assert bytes.length == ScreenVoiceDoorBody.LENGTH;

		screenID = bytes[BIT_OF_SCREENID];
		voice = bytes[BIT_OF_VOICE];
		font = bytes[BIT_OF_FONT];
		door = bytes[BIT_OF_DOOR];

		byte[] array = new byte[BIT_OF_TEXT_LENGTH];
		System.arraycopy(bytes, BIT_OF_TEXT, array, 0, BIT_OF_TEXT_LENGTH);
		try {
			text = new String(array, "gb2312");
		} catch (UnsupportedEncodingException e) {
			throw new DongluHWException("转换显示字体失败");
		}
	}

	@Override
	public byte[] toBytes() {
		byte[] result = new byte[ScreenVoiceDoorBody.LENGTH];
		for(int i=0;i<result.length;i++){
			result[i] = (byte)0xFF;
		}

		result[BIT_OF_SCREENID] = (byte) screenID;
		result[BIT_OF_VOICE] = (byte) voice;
		result[BIT_OF_FONT] = (byte) font;
		result[BIT_OF_DOOR] = (byte) door;
		
		int bit_of_text = BIT_OF_TEXT;
		for(int i=0;i<text.length();i++){
			String charc = text.substring(i, i+1);
			boolean checkIsLettel = checkIsLettel(charc);
			if(checkIsLettel){
				result[bit_of_text] = 0;
				bit_of_text++;
				result[bit_of_text] = (byte)charc.charAt(0);
				bit_of_text++;
			}else{
				try {
					byte[] array = charc.getBytes("gb2312");
					System.arraycopy(array, 0, result, bit_of_text, array.length);
					bit_of_text += 2;
				} catch (UnsupportedEncodingException e) {
					throw new DongluHWException("转换显示字体失败");
				}
			}
		}
		return result;
	}

	public int getScreenID() {
		return screenID;
	}

	public int getVoice() {
		return voice;
	}

	public int getFont() {
		return font;
	}

	public int getDoor() {
		return door;
	}

	public String getText() {
		return text;
	}

	public void setScreenID(int screenID) {
		this.screenID = screenID;
	}

	public void setVoice(int voice) {
		this.voice = voice;
	}

	public void setFont(int font) {
		this.font = font;
	}

	public void setDoor(int door) {
		this.door = door;
	}

	public void setText(String text) {
		this.text = text;
	}

	public static boolean checkIsLettel(String userName) {
		if (userName.equals(" ") || userName.equals(",") || userName.equals(".")) {
			return true;
		}
		String regex = "([a-z]|[A-Z]|[0-9])+";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(userName);
		return m.matches();
	}
}
