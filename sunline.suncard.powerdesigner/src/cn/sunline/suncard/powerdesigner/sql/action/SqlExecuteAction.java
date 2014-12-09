/* 文件名：     SqlExecuteAction.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-2-16
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.sql.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.sql.model.ConnectionModel;
import cn.sunline.suncard.powerdesigner.sql.view.ConnectionTreeView;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 执行SQL脚本的Action
 * @author  Manzhizhen
 * @version 1.0, 2013-2-16
 * @see 
 * @since 1.0
 */
public class SqlExecuteAction implements IWorkbenchWindowActionDelegate{
	public final static String ID = "sunline.suncard.powerdesigner.actions.executesql";
	
	private static Log logger = LogManager.getLogger(SqlExecuteAction.class);
	
	@Override
	public void run(IAction action) {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		if(page == null) {
			logger.debug("无法找到活跃的WorkbenchPage，无法打开数据库连接树！");
			return ;
		}
		
		ConnectionTreeView connectionTreeView = (ConnectionTreeView) page
				.findView(ConnectionTreeView.ID);
		
		if(connectionTreeView == null) {
			logger.debug("无法找到ConnectionTreeView,执行SQL命令失败!");
		}
		
		ConnectionModel connectionModel = connectionTreeView.getSelectConnectionModel();
		if(connectionModel == null) {
			return ;
		}
		
		
	} 

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		
	}

}
