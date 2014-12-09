/* 文件名：     ActionTreeActionGroup.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-9
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.actions.ActionGroup;

import cn.sunline.suncard.sde.workflow.common.DmConstants;
import cn.sunline.suncard.sde.workflow.model.ActionTreeNode;
import cn.sunline.suncard.sde.workflow.tree.factory.TreeContent;


/**
 * 描述
 * @author  易振强
 * @version 1.0, 2012-4-9
 * @see 
 * @since 1.0
 */
public class ActionTreeActionGroup extends ActionGroup{
	private TreeViewer treeViewer;
	
	public final static String ADD_FLAG = "ADD";
	public final static String MODIFY_FLAG = "MODIFY";
	public final static String DELETE_FLAG = "DELETE";
	public final static String VIEW_FLAG = "VIEW";
	
	private IAction actionAddAction;
	private IAction actionDelAction;
	private IAction actionModifyAction;
	private IAction actionViewAction;
	
	public ActionTreeActionGroup(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}
	
	@Override
	public void fillContextMenu(IMenuManager menu) {
		initAction();	// 初始化Action
		
		MenuManager menuManager = (MenuManager) menu;
		menuManager.setRemoveAllWhenShown(true);
		
		menuManager.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(actionAddAction);
				
				Object obj = getCurrentSelection();
				if(obj instanceof TreeContent) {
					TreeContent treeContent = (TreeContent) obj;
					if(treeContent.getObj() instanceof ActionTreeNode && !DmConstants.ACTION_TREE_ROOT_ID.
							equals(treeContent.getId())) {
						manager.add(actionViewAction);
						manager.add(actionModifyAction);
						manager.add(actionDelAction);
						
					}
				}
				
//				manager.add(actionImportAction);
			}
		});
		
		Tree tree = treeViewer.getTree();
		Menu treeMenu = menuManager.createContextMenu(tree);
		tree.setMenu(treeMenu);
		
		super.fillContextMenu(menu);
	}
	
	public void initAction() {
		actionAddAction = new ActionTreeAction(ADD_FLAG, null);
		actionModifyAction = new ActionTreeAction(MODIFY_FLAG, treeViewer);
		actionDelAction = new ActionTreeAction(DELETE_FLAG, treeViewer);
		actionViewAction = new ActionTreeAction(VIEW_FLAG, treeViewer);
	}
	
	private Object getCurrentSelection() {
		IStructuredSelection iSelection = (IStructuredSelection) treeViewer.getSelection();
		Object element = iSelection.getFirstElement();
		return element;
	}

}
