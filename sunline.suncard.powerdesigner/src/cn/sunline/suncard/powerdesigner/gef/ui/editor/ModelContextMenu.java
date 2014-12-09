/* 文件名：     ModelContextMenu.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	模型的上下文菜单
 * 修改人：     易振强
 * 修改时间：2011-12-10
 * 修改内容：创     建
 */
package cn.sunline.suncard.powerdesigner.gef.ui.editor;

import java.util.List;

import javax.swing.text.DefaultEditorKit.PasteAction;

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

import cn.sunline.suncard.powerdesigner.gef.action.PasteAsShortcutAction;
import cn.sunline.suncard.powerdesigner.gef.action.PasteModelAction;
import cn.sunline.suncard.powerdesigner.gef.editpart.DatabaseDiagramModelEditPart;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractGefModel;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

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
		IStructuredSelection iSelection = (IStructuredSelection) getViewer().getSelection();
//		getAction(ActionFactory.DELETE.getId()).setText(I18nUtil.getMessage("DELETE"));
		
		Object obj = iSelection.getFirstElement();
		if(iSelection.size() == 1) {
			if(obj instanceof AbstractConnectionEditPart) {
				menu.appendToGroup(GEFActionConstants.GROUP_REST, getAction(ActionFactory.DELETE.getId()));
				
			} else if(obj instanceof DatabaseDiagramModelEditPart){
				menu.appendToGroup(GEFActionConstants.GROUP_PRINT, getAction(PasteAsShortcutAction.ID));	
				menu.appendToGroup(GEFActionConstants.GROUP_PRINT, getAction(ActionFactory.PASTE.getId()));	
			}

		} else if(iSelection.size() > 1) {
			List<Object> list = iSelection.toList();
			
			for(Object selectionObj : list) {
				if(selectionObj instanceof NodeEditPart) {
					menu.appendToGroup(GEFActionConstants.GROUP_COPY, getAction(ActionFactory.COPY.getId()));
					menu.appendToGroup(GEFActionConstants.GROUP_REST, getAction(ActionFactory.DELETE.getId()));
					return ;
				}
			}
			
			menu.appendToGroup(GEFActionConstants.GROUP_REST, getAction(ActionFactory.DELETE.getId()));
		} 
		
		EditorPart part = (EditorPart) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
	}
}
