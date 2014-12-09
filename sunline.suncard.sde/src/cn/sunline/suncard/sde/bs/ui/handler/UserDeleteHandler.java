/*
 * 文件名：UserDeleteHandler.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述： Handler所有类，所有动作的操作类
 * 修改人： 周兵
 * 修改时间：2001-09-21
 * 修改内容：新增
 * 
 * 修改人：heyong
 * 修改时间：2011-09-24
 * 修改内容：实现删除功能
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

import cn.sunline.suncard.sde.bs.biz.BsUserBiz;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.tree.FunctionTree;
import cn.sunline.suncard.sde.bs.ui.views.FunctionTreeViewPart;
import cn.sunline.suncard.sde.bs.ui.views.UserManagerViewPart;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * Handler所有类，所有动作的操作类 “用户删除”的Handler
 * 
 * @author 周兵
 * @version 1.0 2001-09-21
 * @see
 * @since 1.0
 */
public class UserDeleteHandler extends AbstractHandler {
	Log log = LogManager.getLogger(UserDeleteHandler.class.getName());

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

			BsUserBiz biz = new BsUserBiz();
			ArrayList<BsUser> users = new ArrayList<BsUser>();
			for (int i = 0; i < objects.length; i++) {
				BsUser user = (BsUser) objects[i];
				users.add(user);
			}
			biz.delete(users);
			tableViewer.remove(objects);
			//刷新工具栏
			try {
				UserManagerViewPart viewPart = (UserManagerViewPart) window.getActivePage().showView(UserManagerViewPart.ID);
				viewPart.getActionGroup().changeActionEnabled();
			} catch (PartInitException e) {
				e.printStackTrace();
				log.error("刷新工具栏"+e.getMessage());
			}
			//刷新并展开相关功能树
			try {
				FunctionTreeViewPart viewPart = (FunctionTreeViewPart) window.getActivePage().showView(FunctionTreeViewPart.ID);
				viewPart.refresh(new FunctionTree(I18nUtil.getMessage("USER_MANAGER"), BsUser.class));
			} catch (PartInitException e) {
				e.printStackTrace();
				log.error("刷新并展开相关功能树"+e.getMessage());
			}
		}
		return null;
	}
}
