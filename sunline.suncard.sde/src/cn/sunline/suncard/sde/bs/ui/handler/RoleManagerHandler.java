/*
 * 文件名：PartManagerHandler.java
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

import cn.sunline.suncard.sde.bs.entity.BsRole;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.tree.FunctionTree;
import cn.sunline.suncard.sde.bs.ui.resource.IAppconstans;
import cn.sunline.suncard.sde.bs.ui.views.FunctionTreeViewPart;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 *  Handler所有类，所有动作的操作类
 *  “角色管理”的Handler
 * @author    周兵
 * @version   1.0 2001-09-21
 * @see       
 * @since     1.0 
 */
public class RoleManagerHandler extends AbstractHandler {
	Log log = LogManager.getLogger(RoleManagerHandler.class.getName());

	/**
	 * handler类执行方法
	 * @param ExcutionEvent event
	 * @return Object 
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		//得到功能树中的TreeViewer
		IViewPart viewPart = null;
		try {
			viewPart = window.getActivePage().showView(FunctionTreeViewPart.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
			log.error("得到功能树中的TreeViewer"+e.getMessage());
		}
		ISelectionProvider selectionProvider = viewPart.getSite().getSelectionProvider();
		TreeViewer treeViewer = (TreeViewer) selectionProvider;
		
		try {
			page.showView(IAppconstans.ROLE_MANAGER_VIEW_PART_ID);
			//展开功能树
			treeViewer.expandToLevel(new FunctionTree(I18nUtil.getMessage("ROLE_MANAGER"), BsRole.class), 2);
		} catch (PartInitException e) {
			e.printStackTrace();
			log.error("展开功能树"+e.getMessage());
		}
		return null;
	}

}
