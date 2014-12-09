/*
 * 文件名：RoleDeleteHandler.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述： 角色删除动作类
 * 修改人： heyong
 * 修改时间：2011-09-24
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.handler;

import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import cn.sunline.suncard.sde.bs.biz.BsRoleBiz;
import cn.sunline.suncard.sde.bs.entity.BsRole;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.tree.FunctionTree;
import cn.sunline.suncard.sde.bs.ui.views.FunctionTreeViewPart;
import cn.sunline.suncard.sde.bs.ui.views.RoleManagerViewPart;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 角色删除动作类 当选择角色管理中的删除按钮时，执行此类的execute()方法，执行删除操作
 * 
 * @author heyong
 * @version 1.0, 2011-9-24
 * @see
 * @since 1.0
 */
public class RoleDeleteHandler extends AbstractHandler {
	Log log = LogManager.getLogger(RoleDeleteHandler.class.getName());

	/**
	 * handler类执行方法
	 * 
	 * @param ExcutionEvent
	 *            event
	 * @return Object
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		// 得到按钮当前活动页面的选择提供者对象，此时，需要在相应的页面设置到选择提供者对象，这里是将TableViwer设置为选择提供者
		ISelectionProvider isp = window.getActivePage().getActivePart()
				.getSite().getSelectionProvider();
		// 强制转化为带来复选框的TableViwer对象
		CheckboxTableViewer tableViewer = (CheckboxTableViewer) isp;
		// 检查当前是否勾选记录，如果没有勾选，弹出信息对象框
		if (tableViewer.getCheckedElements() == null
				|| tableViewer.getCheckedElements().length == 0) {
			MessageDialog.openInformation(window.getShell(),
					I18nUtil.getMessage("info"),
					I18nUtil.getMessage("selectdelete"));
			return null;
		}
		// 弹出确认删除对话框，如果点击OK
		if (MessageDialog.openConfirm(window.getShell(),
				I18nUtil.getMessage("CONFIRMINFO"),
				I18nUtil.getMessage("CONFIRMDELETE"))) {
			// 得到所有被选中的记录
			Object[] objects = tableViewer.getCheckedElements();

			BsRoleBiz biz = new BsRoleBiz();
			ArrayList<BsRole> roles = new ArrayList<BsRole>();
			for (int i = 0; i < objects.length; i++) {
				BsRole role = (BsRole) objects[i];
				roles.add(role);
			}
			biz.delete(roles);
			//刷新列表
			tableViewer.remove(objects);
			//刷新工具栏
			try {
				RoleManagerViewPart viewPart = (RoleManagerViewPart) window.getActivePage().showView(RoleManagerViewPart.ID);
				viewPart.getActionGroup().changeActionEnabled();
			} catch (PartInitException e) {
				e.printStackTrace();
				log.error("刷新工具栏"+e.getMessage());
			}
			//刷新并展开相关功能树
			try {
				FunctionTreeViewPart viewPart = (FunctionTreeViewPart) window.getActivePage().showView(FunctionTreeViewPart.ID);
				viewPart.refresh(new FunctionTree(I18nUtil.getMessage("ROLE_MANAGER"), BsRole.class));
			} catch (PartInitException e) {
				e.printStackTrace();
				log.error("刷新并展开相关功能树"+e.getMessage());
			}
		}
		return null;
	}

}
