package com.dongluhitec.card.module;

import image.ImageUtil;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.xml.ws.Dispatch;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dongluhitec.card.CommonUI;
import com.dongluhitec.card.model.CarparkSetting;
import com.dongluhitec.card.model.Device;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class SettingViewer extends Shell {
	
	private Logger LOGGER = LoggerFactory.getLogger(SettingViewer.class);
	
	private SettingPresenter presenter;
	
	private Text text_ip;
	private Text text_port;
	private Text text_deviceArea;
	private Table table;
	private Text text_deviceName;
	private Combo combo_deviceType;
	private Combo combo_deviceAddress;

	private Combo combo_inoutType;

	private Combo combo_supportChinese;

	private Combo combo_supportIn;

	private Combo combo_supportOut;
	private Text text;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		Display display = Display.getDefault();
		SettingViewer shell = new SettingViewer();
		
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		shell.dispose();
	}

	/**
	 * Create the shell.
	 * @param display
	 */
	public SettingViewer() {
		presenter = new SettingPresenter(this);
		Group group = new Group(this, SWT.NONE);
		group.setText("外接服务设置");
		group.setBounds(10, 10, 471, 47);
		
		Label lblIp = new Label(group, SWT.NONE);
		lblIp.setAlignment(SWT.RIGHT);
		lblIp.setBounds(26, 22, 54, 12);
		lblIp.setText("IP地址:");
		
		text_ip = new Text(group, SWT.BORDER);
		text_ip.setText("192.168.1.1");
		text_ip.setBounds(86, 19, 105, 18);
		
		Label label = new Label(group, SWT.NONE);
		label.setAlignment(SWT.RIGHT);
		label.setBounds(207, 22, 49, 12);
		label.setText("端口:");
		
		text_port = new Text(group, SWT.BORDER);
		text_port.setText("10001");
		text_port.setBounds(261, 19, 91, 18);
		
		Group group_1 = new Group(this, SWT.NONE);
		group_1.setText("监控设备");
		group_1.setBounds(10, 63, 473, 379);
		
		Composite composite = new Composite(group_1, SWT.BORDER);
		composite.setBounds(9, 68, 452, 110);
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setAlignment(SWT.RIGHT);
		label_1.setBounds(10, 35, 60, 12);
		label_1.setText("通讯类型:");
		
		combo_deviceType = new Combo(composite, SWT.READ_ONLY);
		combo_deviceType.setItems(new String[] {"COM", "TCP"});
		combo_deviceType.setBounds(76, 31, 105, 20);
		combo_deviceType.select(0);
		
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setAlignment(SWT.RIGHT);
		label_2.setBounds(10, 57, 60, 12);
		label_2.setText("通讯地址:");
		
		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setAlignment(SWT.RIGHT);
		label_3.setBounds(195, 33, 60, 16);
		label_3.setText("设备地址:");
		
		text_deviceArea = new Text(composite, SWT.BORDER);
		text_deviceArea.setText("1.1");
		text_deviceArea.setBounds(259, 32, 91, 18);
		
		Button button = new Button(composite, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String deviceType = combo_deviceType.getText().trim();
				String deviceAddress = combo_deviceAddress.getText().trim();
				String deviceArea = text_deviceArea.getText().trim();
				String deviceName = text_deviceName.getText().trim();
				String deviceInoutType = combo_inoutType.getText().trim();
				String chineseSupport = combo_supportChinese.getText().trim();
				String insideSupport = combo_supportIn.getText().trim();
				String outSideSupport = combo_supportOut.getText().trim();
				
				if(Strings.isNullOrEmpty(deviceType) || 
				Strings.isNullOrEmpty(deviceAddress) || 
				Strings.isNullOrEmpty(deviceArea) || 
				Strings.isNullOrEmpty(deviceName) ||
				Strings.isNullOrEmpty(deviceInoutType)){
					return;
				}

				TableItem ti = new TableItem(table, SWT.BORDER);
				ti.setText(new String[]{deviceName,deviceType,deviceAddress,deviceArea,deviceInoutType,chineseSupport,insideSupport,outSideSupport});
			}
		});
		button.setImage(ImageUtil.getImg("add_16.ico"));
		button.setBounds(366, 47, 72, 22);
		button.setText("添加");
		
		Label label_4 = new Label(composite, SWT.NONE);
		label_4.setAlignment(SWT.RIGHT);
		label_4.setBounds(10, 13, 60, 12);
		label_4.setText("设备名称:");
		
		text_deviceName = new Text(composite, SWT.BORDER);
		text_deviceName.setBounds(76, 10, 105, 18);
		
		combo_deviceAddress = new Combo(composite, SWT.NONE);
		combo_deviceAddress.setBounds(76, 53, 105, 20);
		
		table = new Table(group_1, SWT.BORDER | SWT.FULL_SELECTION);
		table.setToolTipText("岗亭名称");
		table.setBounds(9, 183, 453, 188);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tableColumn_3 = new TableColumn(table, SWT.NONE);
		tableColumn_3.setWidth(100);
		tableColumn_3.setText("设备名称");
		
		TableColumn tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setWidth(69);
		tableColumn_1.setText("通讯类型");
		
		TableColumn tableColumn_2 = new TableColumn(table, SWT.NONE);
		tableColumn_2.setWidth(95);
		tableColumn_2.setText("通讯地址");
		
		TableColumn tableColumn_4 = new TableColumn(table, SWT.NONE);
		tableColumn_4.setWidth(74);
		tableColumn_4.setText("设备地址");
		
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(100);
		tableColumn.setText("进出类型");
		
		TableColumn tableColumn5 = new TableColumn(table, SWT.NONE);
		tableColumn5.setWidth(100);
		tableColumn5.setText("中文支持");
		
		TableColumn tableColumn6 = new TableColumn(table, SWT.NONE);
		tableColumn6.setWidth(100);
		tableColumn6.setText("内置音显");
		
		TableColumn tableColumn7 = new TableColumn(table, SWT.NONE);
		tableColumn7.setWidth(100);
		tableColumn7.setText("外置音显");
		
		Button button_1 = new Button(this, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CarparkSetting cs = new CarparkSetting();
				cs.setIp(text_ip.getText().trim());
				cs.setPort(text_port.getText().trim());
				cs.setStationName(text.getText().trim());
				
				TableItem[] items = table.getItems();
				for (TableItem tableItem : items) {
					Device device = new Device();
					device.setName(tableItem.getText(0));
					device.setType(tableItem.getText(1));
					device.setAddress(tableItem.getText(2));
					device.setArea(tableItem.getText(3));
					device.setInoutType(tableItem.getText(4));
					device.setSupportChinese(tableItem.getText(5));
					device.setSupportInsideVoice(tableItem.getText(6));
					device.setSupportOutsideVoice(tableItem.getText(7));
					
					cs.getDeviceList().add(device);
				}
				
				if(Strings.isNullOrEmpty(cs.getIp()) || Strings.isNullOrEmpty(cs.getPort())){
					CommonUI.error("错误", "外接服务信息不完整");
					return;
				}
				
				if(cs.getDeviceList().isEmpty()){
					CommonUI.error("错误", "未添加任何设备");
					return;
				}
				
				if(!CommonUI.confirmPassword()){
					return;
				}
				
				presenter.writeData(cs);
			}
		});
		button_1.setImage(ImageUtil.getImg("save_16.ico"));
		button_1.setBounds(209, 449, 72, 22);
		button_1.setText("保存");
		createContents();
		
		initCOMCombo(combo_deviceAddress);
		
		Label label_5 = new Label(composite, SWT.NONE);
		label_5.setBounds(201, 13, 54, 12);
		label_5.setText("进出类型:");
		
		combo_inoutType = new Combo(composite, SWT.READ_ONLY);
		combo_inoutType.setItems(new String[] {"进口", "出口"});
		combo_inoutType.setBounds(259, 9, 91, 20);
		combo_inoutType.select(0);
		
		Label label_6 = new Label(composite, SWT.NONE);
		label_6.setText("中文支持:");
		label_6.setAlignment(SWT.RIGHT);
		label_6.setBounds(195, 55, 60, 16);
		
		combo_supportChinese = new Combo(composite, SWT.READ_ONLY);
		combo_supportChinese.setItems(new String[] {"支持", "不支持"});
		combo_supportChinese.setBounds(259, 53, 91, 20);
		combo_supportChinese.select(0);
		
		Label label_7 = new Label(composite, SWT.NONE);
		label_7.setToolTipText("是否支持内置声音播报与显示功能");
		label_7.setText("内置音显:");
		label_7.setAlignment(SWT.RIGHT);
		label_7.setBounds(10, 80, 60, 12);
		
		combo_supportIn = new Combo(composite, SWT.NONE);
		combo_supportIn.setToolTipText("是否支持内置声音播报与显示功能");
		combo_supportIn.setItems(new String[] {"支持", "不支持"});
		combo_supportIn.setBounds(76, 76, 105, 20);
		combo_supportIn.select(0);
		
		Label label_8 = new Label(composite, SWT.NONE);
		label_8.setToolTipText("是否支持外置声音播报与显示功能");
		label_8.setText("外置音显:");
		label_8.setAlignment(SWT.RIGHT);
		label_8.setBounds(195, 78, 60, 16);
		
		combo_supportOut = new Combo(composite, SWT.READ_ONLY);
		combo_supportOut.setToolTipText("是否支持内置声音播报与显示功能");
		combo_supportOut.setItems(new String[] {"支持", "不支持"});
		combo_supportOut.setBounds(259, 76, 91, 20);
		combo_supportOut.select(0);
		
		Composite composite_1 = new Composite(group_1, SWT.BORDER);
		composite_1.setBounds(9, 27, 452, 32);
		
		Label label_9 = new Label(composite_1, SWT.NONE);
		label_9.setBounds(20, 10, 54, 12);
		label_9.setText("岗亭名称:");
		
		text = new Text(composite_1, SWT.BORDER);
		text.setBounds(74, 7, 273, 18);
		initView();
		
		table.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				TableItem[] selection = table.getSelection();
				if(selection == null || selection.length == 0){
					return;
				}
				table.remove(table.getSelectionIndex());
			}
		});
	}

	private void initView() {
		CarparkSetting readData = presenter.readData();
		if(readData == null){
			return;
		}
		text_ip.setText(readData.getIp());
		text_port.setText(readData.getPort());
		text.setText(readData.getStationName());
		
		List<Device> deviceList = readData.getDeviceList();
		for (Device device : deviceList) {
			TableItem ti = new TableItem(table, SWT.BORDER);
			String deviceName = device.getName();
			String deviceType = device.getType();
			String deviceAddress = device.getAddress();
			String deviceArea = device.getArea();
			String inoutType = device.getInoutType();
			String supportChinese = device.getSupportChinese();
			String supportInsideVoice = device.getSupportInsideVoice();
			String supportOutsideVoice = device.getSupportOutsideVoice();
			
			ti.setText(new String[]{deviceName,deviceType,deviceAddress,deviceArea,inoutType,supportChinese,supportInsideVoice,supportOutsideVoice});
		}
	}

	private void initCOMCombo(final Combo combo_1) {
		try{
			ListenableFuture<List<String>> comList = CommonUI.getComList();
			Futures.addCallback(comList, new FutureCallback<List<String>>() {
				@Override
				public void onFailure(Throwable arg0) {
					LOGGER.error("读取本机串口失败", arg0);
					CommonUI.error("错误", "读取本机串口失败");
				}

				@Override
				public void onSuccess(final List<String> arg0) {
					LOGGER.info("读取本机串口:{}", arg0);
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							combo_1.setItems(arg0.toArray(new String[arg0.size()]));
							combo_1.select(0);
						}
					});
				}
			});
		}catch(Exception e){
			e.printStackTrace();
			CommonUI.error("错误", "读取本机串口失败");
		}
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("东陆停车场");
		setSize(502, 512);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
