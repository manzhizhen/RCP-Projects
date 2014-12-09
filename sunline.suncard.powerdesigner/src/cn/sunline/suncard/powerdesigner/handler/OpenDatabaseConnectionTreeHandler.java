/* 文件名：     OpenDatabaseConnectionTreeHandler.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-14
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.sql.view.ConnectionTreeView;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 打开数据库连接树的Handler
 * @author  Manzhizhen
 * @version 1.0, 2013-1-14
 * @see 
 * @since 1.0
 */
public class OpenDatabaseConnectionTreeHandler extends AbstractHandler{
	private Log logger = LogManager.getLogger(OpenDatabaseConnectionTreeHandler.class.getName());
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		if(page == null) {
			logger.debug("无法找到活跃的WorkbenchPage，无法打开数据库连接树！");
			return null;
		}
		
		try {
			page.showView(ConnectionTreeView.ID);
		} catch (PartInitException e) {
			logger.debug("展现数据库连接树失败！" + e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}

}
