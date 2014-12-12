package com.dongluhitec.card.connect.body;

import java.util.Stack;

import com.dongluhitec.card.connect.MessageBody;
import com.dongluhitec.card.connect.util.ByteUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.primitives.UnsignedBytes;

public class CardIDBody implements MessageBody {
	public static final int LENGTH = 8; // 8个字节对应了16个十六进制的数字
	public static final byte NOCARD_BYTE = UnsignedBytes.checkedCast(0XFF);

	// indicate no cardid can be read.
	private boolean noCardIDRead = true;
	private String cardID = "";

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dongluhitec.card.servlet.message.body.ICardIDMessage#getCardID()
     */

	public String getCardID() {
		return this.noCardIDRead ? "" : this.cardID;
	}

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dongluhitec.card.servlet.message.body.ICardIDMessage#isNoCardIDRead
     * ()
     */

	public boolean isNoCardIDRead() {
		return this.noCardIDRead;
	}

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dongluhitec.card.servlet.message.body.ICardIDMessage#setCardID(java
     * .lang.String)
     */

	public void setCardID(String cardID) {
		Preconditions.checkArgument(cardID.length() <= 16,
				"Card ID has to be a 8 bit Hex String");
		// check card iD here to see if it is a hex string.
		// TODO
		if (!Strings.isNullOrEmpty(cardID)) {
			this.noCardIDRead = false;
			this.cardID = cardID;
		} else {
			this.noCardIDRead = true;
		}
	}

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dongluhitec.card.servlet.message.body.ICardIDMessage#setNoCardIDRead
     * (boolean)
     */

	public void setNoCardIDRead() {
		this.cardID = "";
		this.noCardIDRead = true;
	}

	@Override
	public void initContent(byte[] array) {
		assert array.length == CardIDBody.LENGTH;

		if (this.isNoCardReadBytes(array)) {
			this.noCardIDRead = true;
		} else {
			this.noCardIDRead = false;
			String byteArrayToHexString = ByteUtils
					.byteArrayToHexStringNoFormat(array);
			String s = reverseByByte(byteArrayToHexString);
			String s2 = removeLeadingZero(s);
			this.setCardID(s2);
		}
	}

	private String reverseByByte(String s) {
		Stack<String> sss = new Stack<String>();

		for (int i = 0; i < s.length(); i = i + 2) {
			String substring = s.substring(i, i + 2);
			sss.push(substring);
		}

		StringBuilder sb = new StringBuilder();
		while (!sss.isEmpty()) {
			sb.append(sss.pop());
		}
		return sb.toString();
	}

	private String removeLeadingZero(String s) {
		int firstNoZero = 0;
		for (int i = 0; i < s.length() - 8; i++) {
			if (s.charAt(i) == '0') {
				firstNoZero = i+1;
				continue;
			}
			break;
		}
		if (firstNoZero == 0)
			return s;
		else
			return s.substring(firstNoZero);
	}

	private boolean isNoCardReadBytes(byte[] array) {
		for (byte b : array) {
			if (b != CardIDBody.NOCARD_BYTE)
				return false;
		}
		return true;
	}

	@Override
	public byte[] toBytes() {
		byte[] array = new byte[CardIDBody.LENGTH];

		if (noCardIDRead) {
			for (int i = 0; i < CardIDBody.LENGTH; i++) {
				array[i] = CardIDBody.NOCARD_BYTE;
			}
		} else {
			String cardId = cardID;
			if (cardID.length() % 2 != 0) {
				cardId = "0" + cardID;
			}

			for (int i = cardId.length(), ai = 0; i > 1; i -= 2, ai++) {
				String substring = cardId.substring(i - 2, i);
				array[ai] = ByteUtils.hexStringToByte(substring);
			}
		}
		return array;
	}

	@Override
	public String toString() {
		if (this.noCardIDRead)
			return "卡内码: 无";
		else
			return "卡内码: " + this.cardID;
	}
	
	public static void main(String[] args){
		String s = "";
		
		int firstNoZero = 0;
		for (int i = 0; i < s.length() - 8; i++) {
			if (s.charAt(i) == '0') {
				firstNoZero = i+1;
				continue;
			}
			break;
		}
		if (firstNoZero == 0)
			System.out.println(s);
		else
			System.out.println(s.substring(firstNoZero));
	}
}
