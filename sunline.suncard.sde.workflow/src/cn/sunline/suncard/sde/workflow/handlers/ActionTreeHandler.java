/* 文件名：     ActionTreeHandler.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-9
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.workflow.tree.ActionTreeViewPart;
import cn.sunline.suncard.sde.workflow.tree.WorkFlowTreeViewPart;

/**
 * Action
 * @author  易振强
 * @version 1.0, 2012-4-9
 * @see 
 * @since 1.0
 */
public class ActionTreeHandler extends AbstractHandler{
	private Log logger = LogManager.getLogger(ActionTreeHandler.class.getName());
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if(page != null) {
			try {
				WorkFlowTreeHandler.hideOtherViewPart(page);
				page.showView(WorkFlowTreeViewPart.ID);
				page.showView(ActionTreeViewPart.ID);
				
			} catch (PartInitException e) {
				logger.error("打开Action树失败：" + e.getMessage());
				e.printStackTrace();
			}
		} else {
			logger.error("没有活跃的WorkbenchPage!无法打开树!");
		}
		
		return null;
	}
	
}
