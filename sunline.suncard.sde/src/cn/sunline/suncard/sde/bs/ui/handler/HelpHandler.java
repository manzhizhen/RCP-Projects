/*
 * 文件名：HelpHandler.java
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 *  Handler所有类，所有动作的操作类
 *  “帮助”的Handler
 * @author    周兵
 * @version   1.0 2001-09-21
 * @see       
 * @since     1.0 
 */
public class HelpHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"suncardsde",
				"Hello, help");
		return null;
	}

}
