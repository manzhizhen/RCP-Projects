/*
 * 文件名：DepartEditHandler.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：部门修改操作类
 * 修改人： heyong
 * 修改时间：2011-09-25
 * 修改内容：新增
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

import cn.sunline.suncard.sde.bs.biz.BsDepartmentBiz;
import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsDepartment;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.tree.FunctionTree;
import cn.sunline.suncard.sde.bs.ui.dailogs.DepartManagerDialog;
import cn.sunline.suncard.sde.bs.ui.views.FunctionTreeViewPart;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 部门修改操作类
 * 当点击部门管理视图页面的修钮时，执行此类中的execute()方法
 * @author heyong
 * @version 1.0, 2011-9-25
 * @see 
 * @since 1.0
 */
public class DepartEditHandler extends AbstractHandler {
	Log log = LogManager.getLogger(DepartEditHandler.class.getName());

	/**
	 * handler类执行方法
	 * @param ExcutionEvent event
	 * @return Object 
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
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
			BsDepartment department = (BsDepartment) tableViewer.getCheckedElements()[0];
			//构造角色管理对话框，将当前选择的记录作为参数传给构造函数
			DepartManagerDialog dialog = new DepartManagerDialog(window.getShell(), department);
			dialog.open();
			//如果对话框确定按钮
			if(dialog.getReturnCode() == Window.OK){
				//返回对话框构建的实体类对象
				department = dialog.getResult();
				//设置更新信息
				department.setModiDate(new Timestamp(new Date().getTime()));
				BsUser user = (BsUser) Context.getSessionMap().get(Constants.CURRENT_USER);
				department.setModiUser(user.getId().getUserId());
				department.setVersion(department.getVersion()==null? 1 : department.getVersion()+1);
				BsDepartmentBiz biz = new BsDepartmentBiz();
				biz.update(department);
				//更新TableViewer的记录，并选中当前修改的记录
				tableViewer.update(department, null);
				//刷新并展开相关功能树
				try {
					FunctionTreeViewPart viewPart = (FunctionTreeViewPart) window.getActivePage().showView(FunctionTreeViewPart.ID);
					viewPart.refresh(new FunctionTree(I18nUtil.getMessage("DEPART_MANAGER"), BsDepartment.class));
				} catch (PartInitException e) {
					e.printStackTrace();
					log.error("刷新展开树"+e.getMessage());
				}
			}
		}
		return null;
	}

}
