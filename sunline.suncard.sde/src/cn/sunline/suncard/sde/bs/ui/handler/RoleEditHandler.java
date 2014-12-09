/*
 * 文件名：RoleEditHandler.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：功能修改操作类
 * 修改人： 周兵
 * 修改时间：2001-09-21
 * 修改内容：新增
 * 
 * 修改人：heyong
 * 修改时间：2011-09-26
 * 修改内容：实现角色修改功能
*/
package cn.sunline.suncard.sde.bs.ui.handler;

import java.sql.Timestamp;
import java.util.Date;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import cn.sunline.suncard.sde.bs.biz.BsRoleBiz;
import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsRole;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.tree.FunctionTree;
import cn.sunline.suncard.sde.bs.ui.dailogs.RoleManagerDialog;
import cn.sunline.suncard.sde.bs.ui.views.FunctionTreeViewPart;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 功能修改操作类
 * 当点击角色管理视图页面的修钮时，执行此类中的execute()方法
 * @author heyong
 * @version 1.0, 2011-9-24
 * @see 
 * @since 1.0
 */
public class RoleEditHandler extends AbstractHandler {
	Log log = LogManager.getLogger(RoleEditHandler.class.getName());

	/**
	 * 角色修改handler类
	 * @param ExecuteionEvent event
	 * @return Object 
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		//得到按钮当前活动页面的选择提供者对象，此时，需要在相应的页面设置到选择提供者对象，这里是将TableViwer设置为选择提供者
		ISelectionProvider isp = window.getActivePage().getActivePart().getSite().getSelectionProvider();
		//强制转化为带来复选框的TableViwer对象
		CheckboxTableViewer tableViewer = (CheckboxTableViewer)isp;
		//检查当前是否勾选记录，如果没有勾选或勾选超过一个，弹出信息对象框
		if (tableViewer.getCheckedElements() == null || tableViewer.getCheckedElements().length == 0){
			MessageDialog.openInformation(window.getShell(), I18nUtil.getMessage("info"), 
					I18nUtil.getMessage("selectedit"));
		}else if (tableViewer.getCheckedElements().length > 1){
			MessageDialog.openInformation(window.getShell(), I18nUtil.getMessage("info"), 
					I18nUtil.getMessage("selectoneedit"));
		}else {
			//得到当前被选中的记录
			BsRole role = (BsRole) tableViewer.getCheckedElements()[0];
			//构造角色管理对话框，将当前选择的记录作为参数传给构造函数
			RoleManagerDialog dialog = new RoleManagerDialog(window.getShell(), role);
			dialog.open();
			//如果对话框确定按钮
			if (dialog.getReturnCode() == Window.OK){
				//返回对话框构建的实体类对象
				role = dialog.getResult();
				//设置更新信息
				role.setModiDate(new Timestamp(new Date().getTime()));
				BsUser user = (BsUser) Context.getSessionMap().get(Constants.CURRENT_USER);
				role.setModiUser(user.getId().getUserId());
				//将版本设置当前版本+1
				role.setVersion(role.getVersion()==null? 1 : role.getVersion()+1);
				BsRoleBiz biz = new BsRoleBiz();
				biz.update(role);
				//更新TableViewer的记录，并选中当前修改的记录
				tableViewer.update(role, null);
				//刷新并展开相关功能树
				try {
					FunctionTreeViewPart viewPart = (FunctionTreeViewPart) window.getActivePage().showView(FunctionTreeViewPart.ID);
					viewPart.refresh(new FunctionTree(I18nUtil.getMessage("ROLE_MANAGER"), BsRole.class));
				} catch (PartInitException e) {
					e.printStackTrace();
					log.error("刷新并展开相关功能树"+e.getMessage());
				}
			}
		}
		return null;
	}

}
