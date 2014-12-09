/* 文件名：     CreateDBSetHandler.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-6
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.handler;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.ui.dialog.DatabaseGenerationSetDialog;

/**
 * 生成数据库的设置
 * @author  Manzhizhen
 * @version 1.0, 2012-12-6
 * @see 
 * @since 1.0
 */
public class CreateDBSetHandler extends AbstractHandler{
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		DatabaseGenerationSetDialog databaseGenerationSetDialog = new DatabaseGenerationSetDialog(window.getShell());
		databaseGenerationSetDialog.open();
		
		return null;
	}

}
