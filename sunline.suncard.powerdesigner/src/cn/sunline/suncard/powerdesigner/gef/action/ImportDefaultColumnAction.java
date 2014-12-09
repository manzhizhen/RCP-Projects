/* 文件名：     ImportDefaultColumnAction.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-11
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.action;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import cn.sunline.suncard.powerdesigner.Activator;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 导入表格默认字段的Action
 * @author  Manzhizhen
 * @version 1.0, 2013-1-11
 * @see 
 * @since 1.0
 */
public class ImportDefaultColumnAction extends SelectionAction {
	public static String ID = "sunline.suncard.powerdesigner.commands.importdefaultcolumn";

	public ImportDefaultColumnAction(IWorkbenchPart part) {
		super(part);
		setId(ID);
		setText(I18nUtil.getMessage("IMPORT_DEFAULT_COLUMN"));
		ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, IDmImageKey.A_IMPORT_IMAGE);
		setHoverImageDescriptor(imageDescriptor);
		setImageDescriptor(imageDescriptor);
		setDisabledImageDescriptor(imageDescriptor);
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

}
