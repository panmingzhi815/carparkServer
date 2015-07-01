package com.dongluhitec.card.plate;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * 信路威车牌一体识别仪
 * @author panmingzhi815
 * @date 2014-12-16
 */
public interface XinlutongNativeInterface extends Library {

	/**
	 * 初始化车牌设备
	 * @param szIP ip地址
	 * @param szApiVer 附加命令
	 * @return
	 */
	public Pointer HVAPI_OpenEx(String szIP, String szApiVer);

	/**
	 * 关闭回调
	 * @param hHandle 初始化HVAPI_OpenEx函数时返回的句柄
	 * @return 指针
	 */
	public Pointer HVAPI_CloseEx(Pointer hHandle);

	/**
	 * 发送XML命令设置设备参数及获取设备参数信息
	 * @param hHandle 初始化HVAPI_OpenEx函数时返回的句柄
	 * @param szCmd 命令字符串
	 * @param szRetBuf 执行结果信息
	 * @param nBufLen 执行结果信息缓存长度
	 * @param pnRetLen 实际执行结果信息长度
	 * @return
	 */
	public int HVAPI_ExecCmdEx(Pointer hHandle,String szCmd,Pointer szRetBuf,int nBufLen,int pnRetLen);

	/**
	 * 设置回调函数
	 * @param hHandle 初始化HVAPI_OpenEx函数时返回的句柄
	 * @param pFunc 要设置的固调
	 * @param pUserData 中间需传输的自定义指针
	 * @param iVideoID 视频编号
	 * @param iCallBackType 要设置的回调类型
	 * @param szConnCmd 连接参数
	 * @return 整数
	 */
	public int HVAPI_SetCallBackEx(Pointer hHandle, Callback pFunc, Pointer pUserData, int iVideoID, int iCallBackType, String szConnCmd);

	/**
	 * 将YUV图像转换为bitmap
	 * @param pbPicData 图片数据指针
	 * @param wWidth 图片宽度
	 * @param wHeight 图片高度
	 * @param bitmapPointer 缓存bitmap数据指针
	 * @param bitmapCache 缓存bitmap数据指针长度
	 * @return 整数
	 */
	public int HVAPIUTILS_SmallImageToBitmapEx(Pointer pbPicData,int wWidth, int wHeight,Pointer bitmapPointer,IntByReference bitmapCache);

	/**
	 * 车牌回调类型，上传车牌号码及车牌附加信息数据
	 */
	public static interface PLATE_NO_CALLBACK extends Callback {
		int invoke(Pointer pUserData, int dwCarID, String pcPlateNo, String pcAppendInfo, int dwRecordType, long dw64TimeMS);
	}
	
	/**
	 * 车牌大图回调类型，上传识别结果大图数据。
	 */
	public static interface HVAPI_CALLBACK_RECORD_BIGIMAGE extends Callback {
		int invoke(Pointer pUserData, int dwCarID, int wImageType, int wWidth, int wHeight, Pointer pbPicData, int dwImgDataLen, int dwRecordType, long dwTimeMS);
	}

	/**
	 * 车牌小图回调类型，上传识别结果小图数据
	 */
	public static interface HVAPI_CALLBACK_RECORD_SMALLIMAGE extends Callback {
		int invoke(Pointer pUserData, int dwCarID,int wWidth, int wHeight, Pointer pbPicData, int dwImgDataLen, int dwRecordType, long dwTimeMS);
	}

	/**
	 * 结束回调函数
	 */
	public static interface CARINFO_END_CALLBACK extends Callback {
		int invoke (Pointer pUserData, int dwCarID);
	}

}
