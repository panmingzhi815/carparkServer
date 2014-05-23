package com.dongluhitec.card;

import image.ImageUtil;

import java.net.URISyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import com.dongluhitec.card.module.SettingViewer;

public class CarparkApp {
	
	public static void main(String[] args) throws URISyntaxException {
		
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
	
	public CarparkApp(Shell shell){
		this.shell = shell;
	}
	
	public void createContent(){
		final Tray tray = shell.getDisplay().getSystemTray();
		if (tray == null) {
			CommonUI.error("错误", "当前系统不支持本软件的托盘监控");
			System.exit(1);
		}
		
		final TrayItem trayItem = new TrayItem(tray, SWT.NONE);
		trayItem.setToolTipText("东陆停车场岗停硬件底层");
	
		final Menu menu = new Menu (shell, SWT.POP_UP);

		MenuItem mi_password = new MenuItem (menu, SWT.PUSH);
		mi_password.setText ("修改密码");
		mi_password.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				
			}
		});
		
		MenuItem mi_monitorDevice = new MenuItem (menu, SWT.PUSH);
		mi_monitorDevice.setText ("监控设备");
		mi_monitorDevice.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				SettingViewer.main(null);
			}
		});
		
		MenuItem mi_exit = new MenuItem (menu, SWT.PUSH);
		mi_exit.setText ("退出");
		mi_exit.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				trayItem.dispose();
				System.exit(1);
			}
		});
		
		trayItem.addListener (SWT.MenuDetect, new Listener () {
			public void handleEvent (Event event) {
				menu.setVisible (true);
			}
		});
		
		Image image = ImageUtil.getImg("hardware_logging_24.ico");
		trayItem.setImage(image);
	}
	
}

