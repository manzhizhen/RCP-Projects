/* 文件名：     PasteAsShortcutAction.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-10
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.action;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import cn.sunline.suncard.powerdesigner.Activator;
import cn.sunline.suncard.powerdesigner.gef.command.PasteAsShortcutCommand;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 粘贴为快捷方式的Action
 * @author  Manzhizhen
 * @version 1.0, 2012-12-10
 * @see 
 * @since 1.0
 */
public class PasteAsShortcutAction extends SelectionAction {
	public static String ID = "sunline.suncard.powerdesigner.commands.saveasshortcut";
	
	public PasteAsShortcutAction(IWorkbenchPart part) {
		super(part);
		setId(ID);
		setText(I18nUtil.getMessage("PASTE_AS_SHORTCUT"));
		ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID
				, IDmImageKey.PASTE_16);
		setHoverImageDescriptor(imageDescriptor);
		setImageDescriptor(imageDescriptor);
		setDisabledImageDescriptor(imageDescriptor);
	}

	@Override
	public void run() {
		new PasteAsShortcutCommand().execute();
	}
	
	@Override
	protected boolean calculateEnabled() {
		return true;
	}

}
