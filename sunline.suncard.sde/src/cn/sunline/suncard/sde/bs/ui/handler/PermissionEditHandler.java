/*
 * 文件名：PermissionEditHandler.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：权限设置
 * 修改人：heyong
 * 修改时间：2011-9-25
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.tree.FunctionTree;
import cn.sunline.suncard.sde.bs.ui.dailogs.PermissionSetDialog;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 
 * 权限设置
 * @author heyong
 * @version 1.0, 2011-9-25
 * @see 
 * @since 1.0
 */
public class PermissionEditHandler extends AbstractHandler {
	
	/**
	 * handler类执行方法
	 * @param ExcutionEvent event
	 * @return Object 
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		//得到当前选中的所有对象
		StructuredSelection selection = (StructuredSelection) HandlerUtil.getCurrentSelection(event);
		//如果没有选中记录，则弹出对话框
		if (selection.getFirstElement() == null || selection.getFirstElement() instanceof FunctionTree){
			MessageDialog.openInformation(window.getShell(), I18nUtil.getMessage("info"), 
					I18nUtil.getMessage("selectedit"));
		}else{
			PermissionSetDialog dialog = new PermissionSetDialog(window.getShell(), (BsUser)selection.getFirstElement());
			dialog.open();
		}
		return null;
	}

}
