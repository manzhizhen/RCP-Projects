/*
 * 文件名：DepartManagerHandler.java
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
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import cn.sunline.suncard.sde.bs.entity.BsDepartment;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.tree.FunctionTree;
import cn.sunline.suncard.sde.bs.ui.views.DepartManagerViewPart;
import cn.sunline.suncard.sde.bs.ui.views.FunctionTreeViewPart;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
/**
 *  Handler所有类，所有动作的操作类
 *  “部门管理”的Handler
 * @author    周兵
 * @version   1.0 2001-09-21
 * @see       
 * @since     1.0 
 */
public class DepartManagerHandler extends AbstractHandler {
	Log log = LogManager.getLogger(DepartManagerHandler.class.getName());

	/**
	 * handler类执行方法
	 * @param ExcutionEvent event
	 * @return Object 
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage  page = window.getActivePage();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		//得到功能树中的TreeViewer
		IViewPart viewPart = null;
		try {
			viewPart = window.getActivePage().showView(FunctionTreeViewPart.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		ISelectionProvider selectionProvider = viewPart.getSite().getSelectionProvider();
		TreeViewer treeViewer = (TreeViewer) selectionProvider;

		try {
			page.showView(DepartManagerViewPart.ID);
			//展开功能树
			treeViewer.expandToLevel(new FunctionTree(I18nUtil.getMessage("DEPART_MANAGER"), BsDepartment.class), 2);
		} catch (PartInitException e) {
			e.printStackTrace();
			log.error("刷新展开树"+e.getMessage());
		}
		return null;
	}

}
