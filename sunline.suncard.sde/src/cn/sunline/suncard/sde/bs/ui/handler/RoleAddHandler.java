/*
 * 文件名：RoleAddHandler.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述： 角色添加动作类
 * 修改人： heyong
 * 修改时间：2011-09-24
 * 修改内容：新增
 * 
 * 修改人：heyong
 * 修改时间：2011-09-26
 * 修改内容：实现角色添加功能
*/
package cn.sunline.suncard.sde.bs.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import cn.sunline.suncard.sde.bs.biz.BsRoleBiz;
import cn.sunline.suncard.sde.bs.entity.BsRole;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.tree.FunctionTree;
import cn.sunline.suncard.sde.bs.ui.dailogs.RoleManagerDialog;
import cn.sunline.suncard.sde.bs.ui.views.FunctionTreeViewPart;
import cn.sunline.suncard.sde.bs.ui.views.RoleManagerViewPart;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 角色添加动作类
 * 当点击角色管理视图中的+按钮时，执行此类的execute()方法
 * @author heyong
 * @version 1.0, 2011-9-24
 * @since 1.0
 */
public class RoleAddHandler extends AbstractHandler {
	Log log = LogManager.getLogger(RoleAddHandler.class.getName());
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
		//强制转化为带来复选框的CheckboxTableViewer对象
		CheckboxTableViewer tableViewer = (CheckboxTableViewer)isp;
		//构造用户管理对话框
		RoleManagerDialog dialog = new RoleManagerDialog(window.getShell(), null);
		dialog.open();
		//如果对话框确定按钮
		if (dialog.getReturnCode() == Window.OK){
			//返回对话框构建的实体类对象
			BsRole role = dialog.getResult();
			BsRoleBiz biz = new BsRoleBiz();
			biz.insert(role);
			//将新添加的记录添加到TableViewer，并选中新添加的记录
			tableViewer.add(role);
			//取消所有选择
			tableViewer.setAllChecked(false);
			tableViewer.setSelection(new StructuredSelection(role));
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
				log.error("刷新工具栏刷新并展开相关功能树"+e.getMessage());
			}
		}
		return null;
	}

}
