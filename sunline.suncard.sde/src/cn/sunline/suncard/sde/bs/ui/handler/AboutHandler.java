/*
 * 文件名：AboutHandler.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述： Handler所有类，所有动作的操作类
 * 修改人： 周兵
 * 修改时间：2001-09-21
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.bs.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import cn.sunline.suncard.sde.bs.ui.dailogs.AboutDialog;
import cn.sunline.suncard.sde.bs.ui.dailogs.RoleManagerDialog;

/**
 *  Handler所有类，所有动作的操作类
 *  “关于”的Handler
 * @author    周兵
 * @version   1.0 2001-09-21
 * @see       
 * @since     1.0 
 */
public class AboutHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		AboutDialog dialog = new AboutDialog(window.getShell());
		dialog.open();
		return null;
	}

}
