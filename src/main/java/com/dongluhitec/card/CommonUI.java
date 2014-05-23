package com.dongluhitec.card;

import gnu.io.CommPortIdentifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.dongluhitec.card.model.CarparkSetting;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.Closer;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.jamierf.rxtx.RXTXLoader;

@SuppressWarnings("unchecked")
public class CommonUI {
	
	private static String password = "123456";
	
	private static ListeningExecutorService uiHelper = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1));
	
	static{
		try{
			RXTXLoader.load();
		}catch(Exception e){
			System.out.println("加载串口驱动失败");
			e.printStackTrace();
		}
	}
	
	public static void error(String title, String text) {
		Shell shell = Display.getDefault().getActiveShell();
		MessageBox messageBox = new MessageBox(shell, SWT.OK | SWT.CANCEL | SWT.ICON_ERROR);
		messageBox.setText(title);
		messageBox.setMessage(text);
		messageBox.open();
	}

	public static void info(String title, String text) {
		Shell shell = Display.getDefault().getActiveShell();
		MessageBox messageBox = new MessageBox(shell, SWT.OK | SWT.CANCEL | SWT.ICON_INFORMATION);
		messageBox.setText(title);
		messageBox.setMessage(text);
		messageBox.open();
	}
	
	public static boolean confirmPassword(){
		Shell shell = Display.getDefault().getActiveShell();
		InputDialog inputDialog = new InputDialog(shell,"验证","请输入密码","",new IInputValidator() {
			@Override
			public String isValid(String arg0) {
				if(Strings.isNullOrEmpty(arg0)){
					return "密码不能为空";
				}
				if(arg0.length() < 6 || arg0.length() > 20){
					return "密码长度为6-20位";
				}
				return null;
			}
		});
		
		if(inputDialog.open() == InputDialog.OK){
			String value = inputDialog.getValue();
			if(value.equals(password)){
				return true;
			}
			return confirmPassword();
		}else{			
			return false;
		}
	}

	public static ListenableFuture<List<String>> getComList() throws IOException {
		
		ListenableFuture<List<String>> submit = uiHelper.submit(new Callable<List<String>>() {
			@Override
			public List<String> call() throws Exception {
				List<String> resultList = Lists.newArrayList();
				Enumeration<CommPortIdentifier> portIdentifiers = CommPortIdentifier.getPortIdentifiers();
				while (portIdentifiers.hasMoreElements()) {
					CommPortIdentifier nextElement = portIdentifiers.nextElement();
					resultList.add(nextElement.getName());
				}
				return resultList;
			}
		});
		return submit;
	}

	public ListeningExecutorService getUIHelper(){
		return uiHelper;
	}
}
