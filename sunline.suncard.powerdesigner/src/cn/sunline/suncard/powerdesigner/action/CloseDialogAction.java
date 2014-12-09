/* 文件名：     CloseDialogAction.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-16
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import cn.sunline.suncard.powerdesigner.Activator;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;

/**
 * 关闭对话框Action
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-12-16
 * @see
 * @since 1.0
 */
public class CloseDialogAction extends Action {
	private ListViewer windowsListViewer;

	public void setWindowsListViewer(ListViewer windowsListViewer) {
		this.windowsListViewer = windowsListViewer;
	}

	public CloseDialogAction(ListViewer windowsListViewer) {
		this.windowsListViewer = windowsListViewer;
		setText("关闭");
	}

	@Override
	public void run() {
		if (windowsListViewer != null) {
			IStructuredSelection select = (IStructuredSelection) windowsListViewer
					.getSelection();
			if (!select.isEmpty()) {
				Dialog dialog = (Dialog) select.getFirstElement();
				if (dialog != null && dialog.getShell() != null
						&& !dialog.getShell().isDisposed()) {
					dialog.close();
				}
			}

		}

		super.run();
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
				IDmImageKey.A_CLOSE);
	}

}
