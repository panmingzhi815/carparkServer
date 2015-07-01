package com.dongluhitec.card.plate;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class XinlutongCallback {

	public interface XinlutongResult {
		void invok(String ip,int channel,String plateNO,byte[] bigImage,byte[] smallImage);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(XinlutongCallback.class);
	private final int BITMAPSIZE = 150*1024;
	private byte[] smallImageBytes;
	private byte[] bigImageBytes;
	private String plateStr;
	private int plateChannel;

	private XinlutongNativeInterface hvDevice;
	private final XinlutongResult xinlutongResult;
	private final String ip;

	public XinlutongCallback(XinlutongResult xinlutongResult, String ip, XinlutongNativeInterface hvDevice) {
		this.xinlutongResult = xinlutongResult;
		this.ip = ip;
		this.hvDevice = hvDevice;
	}

	public XinlutongNativeInterface.HVAPI_CALLBACK_RECORD_SMALLIMAGE smallImage = new XinlutongNativeInterface.HVAPI_CALLBACK_RECORD_SMALLIMAGE() {

		@Override
		public int invoke(Pointer pUserData, int dwCarID,int wWidth, int wHeight, Pointer pbPicData, int dwImgDataLen, int dwRecordType, long dwTimeMS) {
			LOGGER.debug("{}开始小图回调",ip);
			
			Memory memory = new Memory(BITMAPSIZE);
			IntByReference ibr = new IntByReference(BITMAPSIZE);
			Pointer share = memory.share(0, BITMAPSIZE);

			hvDevice.HVAPIUTILS_SmallImageToBitmapEx(pbPicData,wWidth,wHeight,share,ibr);
			smallImageBytes = share.getByteArray(0, BITMAPSIZE);

			memory.clear();
			return 0;
		}
	};

	public XinlutongNativeInterface.HVAPI_CALLBACK_RECORD_BIGIMAGE bigImage = new XinlutongNativeInterface.HVAPI_CALLBACK_RECORD_BIGIMAGE() {
		
		@Override
		public int invoke(Pointer pUserData, int dwCarID, int wImageType, int wWidth, int wHeight, Pointer pbPicData, int dwImgDataLen, int dwRecordType, long dwTimeMS) {
			LOGGER.debug("{}开始大图回调",ip);
			bigImageBytes = pbPicData.getByteArray(0, dwImgDataLen);
			return 0;
		}
	};
	
	public XinlutongNativeInterface.PLATE_NO_CALLBACK plateNO = new XinlutongNativeInterface.PLATE_NO_CALLBACK() {
		
		@Override
		public int invoke(Pointer pUserData, int dwCarID, String pcPlateNo, String pcAppendInfo, int dwRecordType, long dw64TimeMS) {
			LOGGER.debug("{}开始车牌回调",ip);
			plateStr = pcPlateNo.equals("无车牌")==true ? "" : pcPlateNo.substring(1);
			try {
				Document dom = DocumentHelper.parseText(pcAppendInfo);
				String channel = dom.getRootElement().element("ResultSet").element("Result").element("VideoID").attributeValue("value");
				plateChannel = Integer.parseInt(channel);
			} catch (Exception e) {
				LOGGER.debug("解析回调详细信息异常,通道默认为0");
			}
			return 0;
		}
	};
	
	public XinlutongNativeInterface.CARINFO_END_CALLBACK end = new XinlutongNativeInterface.CARINFO_END_CALLBACK(){
		@Override
		public int invoke(Pointer pUserData, int dwCarID) {
			LOGGER.debug("{}开始结束回调,通道{},车牌{}",ip, plateChannel,plateStr);
			try{
				xinlutongResult.invok(ip, plateChannel,plateStr, bigImageBytes, smallImageBytes);
				plateStr = "";
				bigImageBytes = null;
				smallImageBytes = null;
			}catch(Exception e){
				LOGGER.error("信路威底层车牌广播失败",e);
			}
			return 0;
		}
	};
	
}
