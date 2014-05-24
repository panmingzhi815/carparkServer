package org.dongluhitec.card.message;

import org.junit.Assert;
import org.junit.Test;

import com.dongluhitec.card.connect.body.CarparkNowRecordBody;
import com.dongluhitec.card.connect.util.ByteUtils;

public class CarparkNowRecordBodyTest {
	
	private byte[] arr = ByteUtils.hexStringToByteArray("01 02 BC 2D 31 E9 00 00 00 00 00 80 00 00 00 01 04 02 03");
	
	@Test
	public void initContentTest(){
		String result = "[01 02 F2 23 BE 46 00 00 00 00 00 00 00 00 00 00 00 00 00]";
		CarparkNowRecordBody cnr = new CarparkNowRecordBody();
		cnr.setHasRecord(true);
		cnr.setReaderID(2);
		cnr.setCardID("46BE23F2");
		byte[] bytes = cnr.toBytes();
		Assert.assertEquals(result, ByteUtils.byteArrayToHexString(bytes));
		
		cnr.initContent(bytes);
		Assert.assertEquals(true, cnr.isHasRecord());
		Assert.assertEquals(2, cnr.getReaderID());
		Assert.assertEquals("46BE23F2", cnr.getCardID());
		
		CarparkNowRecordBody cnr2 = new CarparkNowRecordBody();
		cnr2.setHasRecord(true);
		cnr2.setReaderID(2);
		cnr2.setCardID("46BE23F2");
		Assert.assertArrayEquals(cnr2.toBytes(), bytes);
		Assert.assertArrayEquals(cnr2.toBytes(), cnr.toBytes());
	}

}
