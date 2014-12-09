/* 文件名：     OpenSqlExecuteEditorAction.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-15
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.sql.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import cn.sunline.suncard.powerdesigner.Activator;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.sql.editor.SqlEditorInput;
import cn.sunline.suncard.powerdesigner.sql.editor.SqlExecuteEditor;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 打开SQL执行Editor的Action
 * @author  Manzhizhen
 * @version 1.0, 2013-1-15
 * @see 
 * @since 1.0
 */
public class OpenSqlExecuteEditorAction extends Action implements IViewActionDelegate{
	private static Log logger = LogManager.getLogger(OpenSqlExecuteEditorAction.class
			.getName());
	
	public OpenSqlExecuteEditorAction() {
		setId("sunline.suncard.powerdesigner.actions.opensqlexecuteeditor");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator
				.PLUGIN_ID, IDmImageKey.SQL_EXECUTE));
	}
	
	@Override
	public void run() {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.openEditor(new SqlEditorInput(), SqlExecuteEditor.ID);
		} catch (PartInitException e) {
			logger.error("打开SqlExecuteEditor失败！" + e.getMessage());
			e.printStackTrace();
		}
		super.run();
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void init(IViewPart view) {
	}

	@Override
	public void run(IAction action) {
	}

}
