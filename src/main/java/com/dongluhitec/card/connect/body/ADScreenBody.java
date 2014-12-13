package com.dongluhitec.card.connect.body;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dongluhitec.card.connect.MessageBody;
import com.dongluhitec.card.connect.exception.DongluHWException;
import com.dongluhitec.card.connect.exception.DongluInvalidMessageException;

public class ADScreenBody implements MessageBody {

	public static final int LENGTH = 80;
	
	final private int BIT_OF_TEXT = 0;
	final private int BIT_OF_TEXT_LENGTH = 80;

	private String text;

	@Override
	public void initContent(byte[] bytes) throws DongluInvalidMessageException {
		assert bytes.length == ADScreenBody.LENGTH;

		byte[] array = new byte[BIT_OF_TEXT_LENGTH];
		System.arraycopy(bytes, BIT_OF_TEXT, array, 0, BIT_OF_TEXT_LENGTH);
		try {
			text = new String(array, "gb2312").trim();
		} catch (UnsupportedEncodingException e) {
			throw new DongluHWException("转换显示字体失败");
		}
	}

	@Override
	public byte[] toBytes() {
		byte[] result = new byte[ADScreenBody.LENGTH];
		for(int i=0;i<result.length;i++){
			result[i] = (byte)0xFF;
		}
		
		int bit_of_text = BIT_OF_TEXT;
		for(int i=0;i<text.length();i++){
			String charc = text.substring(i, i+1);
			boolean checkIsLettel = checkIsLettel(charc);
			if(checkIsLettel){
				result[bit_of_text] = (byte)charc.charAt(0);
				bit_of_text++;
				result[bit_of_text] = (byte)0x00;
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

	public String getText() {
		return text;
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
