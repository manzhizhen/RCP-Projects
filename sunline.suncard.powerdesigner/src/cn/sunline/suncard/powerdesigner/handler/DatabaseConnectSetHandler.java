/* 文件名：     DatabaseConnectSetHandler.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-19
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.sql.dialog.ConnectionDialog;

/**
 * 数据库连接设置
 * @author  Manzhizhen
 * @version 1.0, 2012-12-19
 * @see 
 * @since 1.0
 */
public class DatabaseConnectSetHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		ConnectionDialog jdbcDialog = new ConnectionDialog(window.getShell());
		jdbcDialog.open();
		
		return null;
	}

}
