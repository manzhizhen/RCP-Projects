/* 文件名：     CloseDialogActionGroup.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-16
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.actions.ActionGroup;

import cn.sunline.suncard.powerdesigner.resource.DmConstants;

/**
 * 关闭对话框的ActionGroup
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-12-16
 * @see
 * @since 1.0
 */
public class CloseDialogActionGroup extends ActionGroup {
	private ListViewer windowsListViewer;
	private IAction closeDialogAction;

	public CloseDialogActionGroup(ListViewer windowsListViewer) {
		this.windowsListViewer = windowsListViewer;
	}

	public void fillContextMenu(IMenuManager mgr) {
		initAction();
		MenuManager menuManager = (MenuManager) mgr;
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				Object obj = getCurrentSelection();
				if(obj instanceof Dialog) {
					manager.add(closeDialogAction);
				}
				
			}
		});

		List list = windowsListViewer.getList();
		Menu menu = menuManager.createContextMenu(list);
		list.setMenu(menu);
	}
	
	/**
	 * 初始化Action
	 */
	private void initAction() {
		closeDialogAction = new CloseDialogAction(windowsListViewer);
	}
	
	private Object getCurrentSelection() {
		IStructuredSelection iSelection = (IStructuredSelection) windowsListViewer.getSelection();
		Object element = iSelection.getFirstElement();
		return element;
	}

}
