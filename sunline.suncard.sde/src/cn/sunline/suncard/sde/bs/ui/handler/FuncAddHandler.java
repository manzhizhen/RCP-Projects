/*
 * 文件名：UserEditHandler.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述： Handler所有类，所有动作的操作类
 * 修改人： 周兵
 * 修改时间：2001-09-21
 * 修改内容：新增
 * 
 * 修改人：heyong
 * 修改时间：2011-09-26
 * 修改内容：实现功能添加功能
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

import cn.sunline.suncard.sde.bs.biz.BsFunctionBiz;
import cn.sunline.suncard.sde.bs.entity.BsFunction;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.tree.FunctionTree;
import cn.sunline.suncard.sde.bs.ui.dailogs.FuncManagerDialog;
import cn.sunline.suncard.sde.bs.ui.views.FuncManagerViewPart;
import cn.sunline.suncard.sde.bs.ui.views.FunctionTreeViewPart;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 *  Handler所有类，所有动作的操作类
 *  “用户编辑”的Handler
 * @author    周兵
 * @version   1.0 2001-09-21
 * @see       
 * @since     1.0 
 */
public class FuncAddHandler extends AbstractHandler {
	Log log = LogManager.getLogger(FuncAddHandler.class.getName());

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
		//构造功能管理对话框
		FuncManagerDialog dialog = new FuncManagerDialog(window.getShell(), null);
		dialog.open();
		//如果对话框确定按钮
		if (dialog.getReturnCode() == Window.OK){
			//返回对话框构建的实体类对象
			BsFunction function = dialog.getResult();
			BsFunctionBiz biz = new BsFunctionBiz();
			biz.insert(function);
			//将新添加的记录添加到TableViewer，并选中新添加的记录
			tableViewer.add(function);
			//取消选择
			tableViewer.setAllChecked(false);
			tableViewer.setSelection(new StructuredSelection(function));
			//刷新工具栏
			try {
				FuncManagerViewPart viewPart = (FuncManagerViewPart) window.getActivePage().showView(FuncManagerViewPart.ID);
				viewPart.getActionGroup().changeActionEnabled();
			} catch (PartInitException e) {
				e.printStackTrace();
			}
			//刷新并展开相关功能树
			try {
				FunctionTreeViewPart viewPart = (FunctionTreeViewPart) window.getActivePage().showView(FunctionTreeViewPart.ID);
				viewPart.refresh(new FunctionTree(I18nUtil.getMessage("FUNCTION_MANAGER"), BsFunction.class));
			} catch (PartInitException e) {
				e.printStackTrace();
				log.error("刷新展开树"+e.getMessage());
			}
		}
		return null;
	}

}
