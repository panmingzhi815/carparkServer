package com.dongluhitec.card.connect.body;

import com.dongluhitec.card.connect.exception.DongluHWException;

public enum OpenDoorEnum {
	抬闸(1), 落闸(2),暂停(3);
	
	private int i;
	OpenDoorEnum(int i){
		this.i = i;
	}
	
	public int getI() {
		return i;
	}

	public static OpenDoorEnum parse(int i){
		OpenDoorEnum[] values = OpenDoorEnum.values();
		for(OpenDoorEnum ode : values){
			if(ode.getI() == i){
				return ode;
			}
		}
		throw new DongluHWException("开闸类型解析失败");
	}
	
	public static int parse(String chars){
		if(chars.equals("up")){
			return 1;
		}else if(chars.equals("down")){
			return 2;
		}else{
			return 3;
		}
	}
}
