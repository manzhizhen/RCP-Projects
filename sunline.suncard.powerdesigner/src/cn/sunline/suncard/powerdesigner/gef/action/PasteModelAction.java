/* 文件名：     PasteModelAction.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	粘贴模型的Action，可以实现Ctrl+V粘贴剪切板中的模型
 * 修改人：     易振强
 * 修改时间：2011-11-21
 * 修改内容：创     建
 */
package cn.sunline.suncard.powerdesigner.gef.action;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import cn.sunline.suncard.powerdesigner.gef.command.PasteAsShortcutCommand;
import cn.sunline.suncard.powerdesigner.gef.command.PasteModelCommand;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.sde.baseplugin.Activator;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 文件名：     PasteModelAction.java
 * 版权：         Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	粘贴模型的Action，可以实现Ctrl+V粘贴剪切板中的模型
 * 修改人：     易振强
 * 修改时间：2011-11-21
 * 修改内容：创     建
 */

public class PasteModelAction extends SelectionAction {

	public PasteModelAction(IWorkbenchPart part) {
		super(part);
	}

	/**
	 *  初始化Action
	 */
	@Override
	protected void init() {
		super.init();
		setId(ActionFactory.PASTE.getId());
		setText(I18nUtil.getMessage("PASTE_TABLE"));
		ISharedImages sharedImages = PlatformUI.getWorkbench()
				.getSharedImages();
		setHoverImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		setDisabledImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
		setEnabled(true);
	}

	/**
	 * 设定该Action是否启动
	 */
	@Override
	protected boolean calculateEnabled() {
		Command command = new PasteModelCommand();
		return command.canExecute();
	}

	/**
	 *  执行PasteNodeCommand()
	 */
	@Override
	public void run() {
		Command command = new PasteModelCommand();
		execute(command);
	}
	
    @Override
    public ImageDescriptor getImageDescriptor() {
		ImageDescriptor descriptor = null;
		
		descriptor = AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IDmImageKey.COLUMN_PASTE);
			
		return descriptor;
    }
}
