/* 文件名：     SaveToImage.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-2-21
 * 修改内容：创     建
 */
package cn.sunline.suncard.sde.workflow.gef.action;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.workflow.Activator;
import cn.sunline.suncard.sde.workflow.gef.command.SaveToImageCommand;
import cn.sunline.suncard.sde.workflow.gef.ui.editor.WorkFlowEditor;
import cn.sunline.suncard.sde.workflow.resource.IDmImageKey;

/**
 * 将策略保存为图片
 * @author  易振强
 * @version 1.0, 2012-2-21
 * @see 
 * @since 1.0
 */
public class SaveToImageAction  extends SelectionAction{
	public final static String ID = "sunline.suncard.sde.dm.commands.SaveToImageCommand";
	
	private Log logger = LogManager.getLogger(SaveToImageAction.class.getName());
	
	private WorkFlowEditor workFlowEditor;
	
	/**
	 * @param part
	 */
	public SaveToImageAction(IWorkbenchPart part) {
		super(part);
		
		if(part instanceof WorkFlowEditor) {
			workFlowEditor = (WorkFlowEditor) part;
		}
		
		setId(ID);
		setText(I18nUtil.getMessage("ACTION_EXPORT"));
		
		ImageDescriptor descriptor = AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IDmImageKey.A_EXPORT_IMAGE);
		setImageDescriptor(descriptor);
	}

	@Override
	public void run() {
		SaveToImageCommand saveToImageCommand = new SaveToImageCommand(workFlowEditor);
		
		if(saveToImageCommand != null) {
			saveToImageCommand.execute();
		}
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

}
