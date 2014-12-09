/*
 * 文件名：OpenAction.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述： 动作按钮的普通类
 * 修改人： 周兵
 * 修改时间：2001-09-21
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.bs.ui.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import cn.sunline.suncard.sde.bs.ui.resource.IAppconstans;
import cn.sunline.suncard.sde.bs.ui.resource.IImageKey;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 *  动作按钮的普通类
 *  动作按钮，将其单独拿出，便于以后管理
 * @author    周兵
 * @version   1.0 2001-09-21
 * @see       
 * @since     1.0 
 */
public class OpenAction extends Action {
	
	private IWorkbenchWindow window;
	

	public OpenAction(IWorkbenchWindow window){
		this.window = window;
		setId(IAppconstans.OPEN_ACTION_ID);
		setText(I18nUtil.getMessage("open"));
		setAccelerator(SWT.CTRL+'O');
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IAppconstans.APPLICATION_ID,
				IImageKey.OPEN_ACTION));
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
			FileDialog fileDialog = new FileDialog(window.getShell(), SWT.OPEN);
			fileDialog.open();

	}

}
