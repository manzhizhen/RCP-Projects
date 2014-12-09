/* 文件名：     CopyModelAction.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	复制模型的Action，可以实现Ctrl+C复制选中的模型
 * 修改人：     易振强
 * 修改时间：2011-11-21
 * 修改内容：创     建
 */
package cn.sunline.suncard.powerdesigner.gef.action;



import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import cn.sunline.suncard.powerdesigner.gef.command.CopyModelCommand;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.sde.baseplugin.Activator;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 文件名：     CopyModelAction.java
 * 版权：         Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	复制模型的Action，可以实现Ctrl+C复制选中的模型
 * 修改人：     易振强
 * 修改时间：2011-11-21
 * 修改内容：创     建
 */
public class CopyModelAction extends SelectionAction {

	public CopyModelAction(IWorkbenchPart part) {
		super(part);
	}

	/**
	 *  初始化Action
	 */
	@Override
    protected void init() {
        super.init();
        setId(ActionFactory.COPY.getId());
        setText(I18nUtil.getMessage("ACTION_COPY"));
        ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
        setHoverImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
        setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
        setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
    }

	/**
	 * 设定该Action是否启动
	 */
    @Override
    protected boolean calculateEnabled() {
        IStructuredSelection selection = (IStructuredSelection) this.getSelection();
        if (selection == null) {
            return false;
        }
        
        Object obj = selection.getFirstElement();
        if (obj instanceof DatabaseDiagramEditor) {
            return true;
        }
        
        return false;
    }

    /**
     * run方法中调用CopyNodeCommand，通过getSelectedObjects())将选取的内容作为参数传入
     */
    @Override
    public void run() {
        super.run();
        Command command = new CopyModelCommand(getSelectedObjects());
        command.execute();
    }
    
    @Override
    public boolean isEnabled() {
    	return true;
    }
    
    @Override
    public ImageDescriptor getImageDescriptor() {
		ImageDescriptor descriptor = null;
		
		descriptor = AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IDmImageKey.COLUMN_COPY);
			
		return descriptor;
    }
    
}
