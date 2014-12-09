/*
 * 文件名：
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：heyong
 * 修改时间：2011-10-17
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import cn.sunline.suncard.sde.bs.tree.FunctionTreeFactory;

public class RefreshHandler extends AbstractHandler {


	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		ISelectionProvider isp = window.getActivePage().getActivePart().getSite().getSelectionProvider();
		TreeViewer tv = (TreeViewer) isp;
		tv.setInput(FunctionTreeFactory.createTreeData());
		return null;
	}

}
