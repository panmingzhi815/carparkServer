package com.dongluhitec.card.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.dongluhitec.card.CommonUI;
import com.dongluhitec.card.model.CarparkSetting;
import com.google.common.io.Closer;



public class SettingPresenter {
	
	private final String dataFilePath = "donglu.data";
	
	private SettingViewer settingViewer;
	
	public SettingPresenter(SettingViewer settingViewer){
		this.settingViewer = settingViewer;
	}
	
	
	public void writeData(CarparkSetting cs) {
		Closer closer = Closer.create();
		try{
			FileOutputStream fos = new FileOutputStream(dataFilePath);
			FileOutputStream register = closer.register(fos);
			ObjectOutputStream oos = new ObjectOutputStream(register);
			ObjectOutputStream register2 = closer.register(oos);
			register2.writeObject(cs);
			CommonUI.info("提示", "操作成功!");
		}catch(Exception e){
			e.printStackTrace();
			CommonUI.error("错误", "保存失败");
		}finally{
			try {
				closer.close();
			} catch (IOException e) {}
		}
	}
	
	public CarparkSetting readData(){
		File file = new File(dataFilePath);
		if(!file.exists()){
			return null;
		}
		Closer closer = Closer.create();
		try{
			FileInputStream fis = new FileInputStream(dataFilePath);
			FileInputStream register = closer.register(fis);
			ObjectInputStream ois = new ObjectInputStream(register);
			ObjectInputStream register2 = closer.register(ois);
			CarparkSetting cs = (CarparkSetting)register2.readObject();
			return cs;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			try {
				closer.close();
			} catch (IOException e) {}
		}
	}
	
	
}
