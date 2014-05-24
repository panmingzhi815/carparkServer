package com.dongluhitec.card.module;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.graphics.Rectangle;


public class WorkSpace {
	 private Shell mainwindowShell = null; 
	    private HookSysTray sysTray=null;
	    /**
	    * @param args
	    */
	    public static void main(String[] args) {
	        WorkSpace thisClass = new WorkSpace();
	        thisClass.openmain();
	    }

	    /**
	    * This method initializes sShell
	    */
	    private void createSShell() {
	        mainwindowShell = new Shell();
	        mainwindowShell.setText("主窗口");
	        mainwindowShell.setSize(new Point(260, 200));
	        mainwindowShell.setLayout(new GridLayout());    
	        //设置窗口在屏幕上的初始位置，在屏幕的中间显示主窗口 
	        Monitor primary = mainwindowShell.getMonitor();
	        Rectangle bounds = primary.getBounds();
	        Rectangle rect =mainwindowShell.getBounds();
	        // 获取屏幕高度(screenH)和宽度(screenW)
	        // int screenH = bounds.height;
	        // int screenW = bounds.width;
	        // System.out.println("屏幕的分辨率为："+screenW+"*"+screenH);
	        int x = bounds.x + (bounds.width - rect.width) / 2;
	        int y = bounds.y + (bounds.height - rect.height) / 2;
	        if (x < 0)
	            x = 0;
	        if (y < 0)
	            y = 0;
	        //定位对象窗口坐标
	        mainwindowShell.setLocation(x, y);
	        //关闭主窗口
	        mainwindowShell
	        .addShellListener(new org.eclipse.swt.events.ShellAdapter() {
	            public void shellClosed(org.eclipse.swt.events.ShellEvent e) {
	                //重载关闭对话框方法    
	                closeWindow();
	            }
	        });     
	        
	    }
	    
	    public void openmain(){    
	        Display display = Display.getDefault();        
	        createSShell();
	        
	        //显示系统托盘
	        sysTray=new HookSysTray();
	        sysTray.createSysTray(mainwindowShell);
	        //显示主窗口
	        mainwindowShell.open();
	        
	        // 当窗体未被关闭时执行循环体内的代码
	        while (!mainwindowShell.isDisposed()) {
	            // 如果未发生事件，通过sleep方法进行监视事件队列
	            if (!display.readAndDispatch())
	                display.sleep();
	        }    
	            
	        
	    }
	    
	    public void closeWindow(){
	        sysTray.trayDispose();// 释放托盘及其相关资源
//	        Display display =mainwindowShell.getDisplay();
//	        display.dispose();// 释放底层的资源            
	        System.exit(1);//退出主程序
	    }
}
