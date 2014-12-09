/*
 * 文件名：UserDeleteHandler.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述： Handler所有类，所有动作的操作类
 * 修改人： heyong
 * 修改时间：2011-10-08
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
import org.eclipse.ui.handlers.HandlerUtil;

import cn.sunline.suncard.sde.bs.biz.BsUserBiz;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 密码重置handler类
 * 当点击用户管理工具栏中的"密码重置"按钮时，执行此类中的execute方法
 * @author heyong
 * @version 1.0 2011-10-08
 * @see
 * @since 1.0
 */
public class PasswdResetHandler extends AbstractHandler {
	Log log = LogManager.getLogger(PasswdResetHandler.class.getName());

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
					I18nUtil.getMessage("selectpasswdreset"));
			return null;
		}
		// 弹出确认删除对话框，如果点击OK
		if (MessageDialog.openConfirm(window.getShell(),
				I18nUtil.getMessage("CONFIRMINFO"),
				I18nUtil.getMessage("confirmpasswdreset"))) {
			// 得到所有被选中的记录
			Object[] objects = tableViewer.getCheckedElements();

			BsUserBiz biz = new BsUserBiz();
			ArrayList<BsUser> users = new ArrayList<BsUser>();
			for (int i = 0; i < objects.length; i++) {
				BsUser user = (BsUser) objects[i];
				users.add(user);
			}
			String newpassword = biz.passwdReset(users);
			//提示成功，并回显密码
			MessageDialog.openInformation(window.getShell(), I18nUtil.getMessage("info"),
					I18nUtil.getMessage("passwdresetsuccess", new Object[]{newpassword}));
			log.info("密码重置");
		}
		return null;
	}
}
