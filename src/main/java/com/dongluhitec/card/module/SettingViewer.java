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
		lblIp.setBounds(10, 22, 54, 12);
		lblIp.setText("IP地址:");
		
		text_ip = new Text(group, SWT.BORDER);
		text_ip.setText("192.168.1.1");
		text_ip.setBounds(70, 19, 105, 18);
		
		Label label = new Label(group, SWT.NONE);
		label.setAlignment(SWT.RIGHT);
		label.setBounds(191, 22, 49, 12);
		label.setText("端口:");
		
		text_port = new Text(group, SWT.BORDER);
		text_port.setText("10001");
		text_port.setBounds(245, 19, 91, 18);
		
		Group group_1 = new Group(this, SWT.NONE);
		group_1.setText("监控设备");
		group_1.setBounds(10, 63, 473, 279);
		
		Composite composite = new Composite(group_1, SWT.BORDER);
		composite.setBounds(10, 20, 452, 87);
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setAlignment(SWT.RIGHT);
		label_1.setBounds(0, 37, 60, 12);
		label_1.setText("通讯类型:");
		
		combo_deviceType = new Combo(composite, SWT.READ_ONLY);
		combo_deviceType.setItems(new String[] {"COM", "TCP"});
		combo_deviceType.setBounds(60, 33, 105, 20);
		combo_deviceType.select(0);
		
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setAlignment(SWT.RIGHT);
		label_2.setBounds(0, 60, 60, 12);
		label_2.setText("设备地址:");
		
		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setAlignment(SWT.RIGHT);
		label_3.setBounds(171, 35, 60, 16);
		label_3.setText("设备区控:");
		
		text_deviceArea = new Text(composite, SWT.BORDER);
		text_deviceArea.setText("1.1");
		text_deviceArea.setBounds(235, 34, 91, 18);
		
		Button button = new Button(composite, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String deviceType = combo_deviceType.getText().trim();
				String deviceAddress = combo_deviceAddress.getText().trim();
				String deviceArea = text_deviceArea.getText().trim();
				String deviceName = text_deviceName.getText().trim();
				String deviceInoutType = combo_inoutType.getText().trim();
				if(Strings.isNullOrEmpty(deviceType) || 
				Strings.isNullOrEmpty(deviceAddress) || 
				Strings.isNullOrEmpty(deviceArea) || 
				Strings.isNullOrEmpty(deviceName) ||
				Strings.isNullOrEmpty(deviceInoutType)){
					return;
				}

				TableItem ti = new TableItem(table, SWT.BORDER);
				ti.setText(new String[]{deviceName,deviceType,deviceAddress,deviceArea,deviceInoutType});
			}
		});
		button.setImage(ImageUtil.getImg("add_16.ico"));
		button.setBounds(341, 32, 72, 22);
		button.setText("添加");
		
		Label label_4 = new Label(composite, SWT.NONE);
		label_4.setAlignment(SWT.RIGHT);
		label_4.setBounds(0, 14, 60, 12);
		label_4.setText("设备名称:");
		
		text_deviceName = new Text(composite, SWT.BORDER);
		text_deviceName.setBounds(60, 11, 105, 18);
		
		combo_deviceAddress = new Combo(composite, SWT.NONE);
		combo_deviceAddress.setBounds(60, 57, 105, 20);
		
		table = new Table(group_1, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 113, 453, 156);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tableColumn_3 = new TableColumn(table, SWT.NONE);
		tableColumn_3.setWidth(100);
		tableColumn_3.setText("设备名称");
		
		TableColumn tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setWidth(69);
		tableColumn_1.setText("设备类型");
		
		TableColumn tableColumn_2 = new TableColumn(table, SWT.NONE);
		tableColumn_2.setWidth(95);
		tableColumn_2.setText("设备地址");
		
		TableColumn tableColumn_4 = new TableColumn(table, SWT.NONE);
		tableColumn_4.setWidth(74);
		tableColumn_4.setText("设备区控");
		
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(100);
		tableColumn.setText("进出类型");
		
		Button button_1 = new Button(this, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CarparkSetting cs = new CarparkSetting();
				cs.setIp(text_ip.getText().trim());
				cs.setPort(text_port.getText().trim());
				
				TableItem[] items = table.getItems();
				for (TableItem tableItem : items) {
					Device device = new Device();
					device.setName(tableItem.getText(0));
					device.setType(tableItem.getText(1));
					device.setAddress(tableItem.getText(2));
					device.setArea(tableItem.getText(3));
					device.setInoutType(tableItem.getText(4));
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
		button_1.setBounds(210, 348, 72, 22);
		button_1.setText("保存");
		createContents();
		
		initCOMCombo(combo_deviceAddress);
		
		Label label_5 = new Label(composite, SWT.NONE);
		label_5.setBounds(177, 14, 54, 12);
		label_5.setText("进出类型:");
		
		combo_inoutType = new Combo(composite, SWT.READ_ONLY);
		combo_inoutType.setItems(new String[] {"进口", "出口"});
		combo_inoutType.setBounds(235, 10, 91, 20);
		combo_inoutType.select(0);
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
		
		List<Device> deviceList = readData.getDeviceList();
		for (Device device : deviceList) {
			TableItem ti = new TableItem(table, SWT.BORDER);
			String deviceName = device.getName();
			String deviceType = device.getType();
			String deviceAddress = device.getAddress();
			String deviceArea = device.getArea();
			String inoutType = device.getInoutType();
			ti.setText(new String[]{deviceName,deviceType,deviceAddress,deviceArea,inoutType});
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
		setSize(502, 414);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
