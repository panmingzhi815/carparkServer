package com.dongluhitec.card.connect;

import com.dongluhitec.card.connect.exception.DongluHWException;

public enum DirectonType {
	请求, 响应;
	public static DirectonType parse(String name){
		DirectonType[] values = DirectonType.values();
		for(DirectonType dt : values){
			if(dt.name().equals(name)){
				return dt;
			}
		}
		throw new DongluHWException("未知的方向类型,解析失败");
	}
}
