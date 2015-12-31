package com.dongluhitec.card;

import image.ImageUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.dongluhitec.card.hardware.HardwareService;
import com.dongluhitec.card.module.SettingViewer;
import com.dongluhitec.card.util.EventBusUtil;
import com.dongluhitec.card.util.EventInfo;
import com.google.common.eventbus.Subscribe;
import com.jamierf.rxtx.RXTXLoader;

public class CarparkApp {

	private long lastDeviceSuccess;
	private long lastComputerSuccess;
	private boolean isDeviceFaile = false;
	private boolean isComputerFaile = false;

	public static void main(String[] args) throws URISyntaxException {
		
		try {
			RXTXLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("系统托盘BySwt");

		CarparkApp carparkApp = new CarparkApp(shell);
		carparkApp.createContent();

		shell.setBounds(0, 0, 0, 0);
		shell.open();
		shell.setVisible(false);
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		System.exit(1);
	}

	private Shell shell;
	private HardwareService hardwareService;
	private TrayItem trayItem;
	private ToolTip tip;

	public CarparkApp(Shell shell) {
		this.shell = shell;
	}

	public void createContent() {
		EventBusUtil.register(this);
		final Tray tray = shell.getDisplay().getSystemTray();
		if (tray == null) {
			CommonUI.error("错误", "当前系统不支持本软件的托盘监控");
			System.exit(1);
		}
		trayItem = new TrayItem(tray, SWT.NONE);
		trayItem.setToolTipText("东陆停车场岗停硬件底层");
		final Menu menu = new Menu(shell, SWT.POP_UP);

		MenuItem mi_password = new MenuItem(menu, SWT.PUSH);
		mi_password.setText("修改密码");
		mi_password.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				CommonUI.changePassword();
			}
		});

		MenuItem mi_monitorDevice = new MenuItem(menu, SWT.PUSH);
		mi_monitorDevice.setText("监控设备");
		mi_monitorDevice.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				SettingViewer.main(null);
			}
		});

		MenuItem mi_exit = new MenuItem(menu, SWT.PUSH);
		mi_exit.setText("退出");
		mi_exit.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				trayItem.dispose();
				System.exit(1);
			}
		});

		trayItem.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				menu.setVisible(true);
			}
		});

		trayItem.setImage(ImageUtil.getImg("hardware_logging_24.ico"));

		new Thread(new Runnable() {
			@Override
			public void run() {
				hardwareService = HardwareService.getInstance();
				hardwareService.start();
				createTip("提示","深圳市东陆高新停车场系统启动");
			}
		}).start();
	}

	@Subscribe
	public void listenTrayInfo(final EventInfo event) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				switch (event.getEventType()) {
				case 硬件通讯异常:
					if (!isDeviceFaile){
						if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastDeviceSuccess) > 8){
							trayItem.setImage(ImageUtil.getImg("hardware_error_24.ico"));
							createTip(event.getEventType().name(),(String)event.getObj());
						}

					}
					isDeviceFaile = true;
					break;
				case 外接服务通讯异常:
					if(!isComputerFaile) {
						if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastComputerSuccess) > 8){
							trayItem.setImage(ImageUtil.getImg("hardware_server_warn.ico"));
							createTip(event.getEventType().name(), (String) event.getObj());
						}
					}
					isComputerFaile = true;
					break;
				case 外接服务通讯正常:
					if(isComputerFaile) {
						if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastComputerSuccess) > 5) {
							trayItem.setImage(ImageUtil.getImg("hardware_logging_24.ico"));
							createTip(event.getEventType().name(), (String) event.getObj());
						}
					}
					lastComputerSuccess = System.currentTimeMillis();
					isComputerFaile = false;
					break;
				case 硬件通讯正常:
					if(isDeviceFaile) {
						if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastDeviceSuccess) > 5){
							trayItem.setImage(ImageUtil.getImg("hardware_logging_24.ico"));
							createTip(event.getEventType().name(), (String) event.getObj());
						}
					}
					lastDeviceSuccess = System.currentTimeMillis();
					isDeviceFaile = false;
					break;
				default:
					break;
				}
			}
		});
	}
	
	public void createTip(final String title,final String content){
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (tip != null) {
					tip.dispose();
				}
				tip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_INFORMATION);
				trayItem.setToolTip(tip);
				tip.setMessage(content);
				tip.setText(title);
				tip.setVisible(true);
				tip.setAutoHide(true);
			}
		});
	}

}
