package org.dongluhitec.card.message;

import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;

import com.dongluhitec.card.connect.body.ADScreenBody;
import com.dongluhitec.card.connect.body.CarparkNowRecordBody;
import com.dongluhitec.card.connect.util.ByteUtils;

public class ADScreenBodyTest {
	
	@Test
	public void initContentTest() throws UnsupportedEncodingException{
		byte[] bytes = new byte[80];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte)0xFF;
		}
		bytes[0] = (byte)0x53;
		bytes[1] = (byte)0x00;
		bytes[2] = (byte)0x59;
		bytes[3] = (byte)0x00;
		
		ADScreenBody adsb = new ADScreenBody();
		adsb.initContent(bytes);
		
		//Assert.assertEquals("S Y", adsb.getText());
	}
	
	@Test
	public void toBytes() throws UnsupportedEncodingException{
		byte[] bytes = new byte[80];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte)0xFF;
		}
		bytes[0] = (byte)0x53;
		bytes[1] = (byte)0x00;
		bytes[2] = (byte)0x59;
		bytes[3] = (byte)0x00;
		
		ADScreenBody adsb = new ADScreenBody();
		adsb.setText("SY");
		
		Assert.assertArrayEquals(bytes, adsb.toBytes());
	}

}
