/* 文件名：     IndexManagerHandler.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-2
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.ui.dialog.IndexAllShowDialog;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 索引管理的Handler
 * @author  Manzhizhen
 * @version 1.0, 2013-1-2
 * @see 
 * @since 1.0
 */
public class IndexManagerHandler extends AbstractHandler{
	private Log logger = LogManager.getLogger(NewHandler.class.getName());
	private IndexAllShowDialog allShowDialog;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		
			 allShowDialog = new IndexAllShowDialog(window.getShell());
			
			 allShowDialog.open();

		return null;
	}

}
