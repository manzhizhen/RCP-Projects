/* 文件名：     WindowsManagerHandler.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-13
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.ui.dialog.WindowsManagerDialog;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 窗口管理功能
 * @author  Manzhizhen
 * @version 1.0, 2012-12-13
 * @see 
 * @since 1.0
 */
public class WindowsManagerHandler extends AbstractHandler{
	private Log logger = LogManager.getLogger(WindowsManagerHandler.class.getName());

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if(window == null ) {
			logger.error("找不到活跃的WorkbenchWindow，WindowsManagerHandler执行失败！");
			return null;
		}
		
		if(WindowsManagerDialog.getWindowsManagerDialog() == null) {
			WindowsManagerDialog dialog = new WindowsManagerDialog(window.getShell());
			dialog.open();
		} else {
			WindowsManagerDialog.getWindowsManagerDialog().getShell().setMinimized(false);
			WindowsManagerDialog.getWindowsManagerDialog().getShell().setActive();
		}
		
		return null;
	}



}
