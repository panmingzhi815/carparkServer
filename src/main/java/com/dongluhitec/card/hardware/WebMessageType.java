package com.dongluhitec.card.hardware;

public enum WebMessageType {

	交换密钥("00"),设备信息("01"),发送卡号("02"),设备控制("03"),成功("04");
	
	private String code;
	WebMessageType(String code){
		this.code = code;
	}
	
	public String toString(){
		return code;
	}

	public static WebMessageType parse(CharSequence subSequence) {
		WebMessageType[] values = WebMessageType.values();
		for (WebMessageType messageType : values) {
			if(messageType.toString().equals(subSequence)){
				return messageType;
			}
		}
		return null;
	}
}
