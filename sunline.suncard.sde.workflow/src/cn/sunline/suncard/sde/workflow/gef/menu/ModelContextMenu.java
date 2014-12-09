/* 文件名：     ModelContextMenu.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	模型的上下文菜单
 * 修改人：     易振强
 * 修改时间：2011-12-10
 * 修改内容：创     建
 */
package cn.sunline.suncard.sde.workflow.gef.menu;

import java.util.List;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorPart;

import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.workflow.gef.action.SaveToImageAction;

/**
 * 模型的上下文菜单
 * @author  易振强
 * @version 1.0, 2011-12-10
 * @see 
 * @since 1.0
 */
public class ModelContextMenu extends ContextMenuProvider {
	
	private ActionRegistry actionRegistry;

	/**
	 * @param viewer
	 */
	public ModelContextMenu(EditPartViewer viewer, ActionRegistry actionRegistry) {
		super(viewer);
		this.actionRegistry = actionRegistry;
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {
        GEFActionConstants.addStandardActionGroups(menu);
        initMenu(menu); 
        
        menu.update();
	}
	
	private IAction getAction(String actionId) {
        return actionRegistry.getAction(actionId);
    }
	
	
	private void initMenu(IMenuManager menu) {
//		IStructuredSelection iSelection = (IStructuredSelection) getViewer().getSelection();
//		getAction(ActionFactory.DELETE.getId()).setText(I18nUtil.getMessage("ACTION_DEL"));
//		Object obj = iSelection.getFirstElement();
		
		menu.appendToGroup(GEFActionConstants.GROUP_REST, getAction(SaveToImageAction.ID));
		
	}
}
