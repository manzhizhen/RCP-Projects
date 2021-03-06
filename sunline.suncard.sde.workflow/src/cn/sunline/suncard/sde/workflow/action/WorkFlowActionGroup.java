/* 文件名：     WorkFlowActionGroup.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-26
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
import cn.sunline.suncard.sde.workflow.gef.model.WorkFlowModel;
import cn.sunline.suncard.sde.workflow.model.WorkFlowTreeNode;
import cn.sunline.suncard.sde.workflow.tree.factory.TreeContent;


/**
 * 工作流的ActionGroup
 * @author  易振强
 * @version 1.0, 2012-3-26
 * @see 
 * @since 1.0
 */

public class WorkFlowActionGroup extends ActionGroup{
	private TreeViewer treeViewer;
	
	public final static String ADD_FLAG = "ADD";
	public final static String MODIFY_FLAG = "MODIFY";
	public final static String DELETE_FLAG = "DELETE";
	public final static String VIEW_FLAG = "VIEW";
	public final static String EXPORT_FLAG = "EXPORT";
	public final static String ATTRI_FLAG = "ATTRI";
	public final static String IMPORT_FLAG = "IMPORT";
	
	private IAction workFlowAddAction;
	private IAction workFlowDelAction;
	private IAction workFlowModifyAction;
	private IAction workFlowViewAction;
	private IAction workFlowExportAction;
	private IAction workFlowAttriAction;
	private IAction workFlowImportAction;
	
	public WorkFlowActionGroup(TreeViewer treeViewer) {
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
				manager.add(workFlowAddAction);
				manager.add(workFlowImportAction);	
				
				Object obj = getCurrentSelection();
				if(obj instanceof TreeContent) {
					TreeContent treeContent = (TreeContent) obj;
					if(treeContent.getObj() instanceof WorkFlowTreeNode && !DmConstants.
							WORK_FLOW_TREE_ROOT_ID.equals(treeContent.getId())) {
						manager.add(workFlowViewAction);
						manager.add(workFlowModifyAction);
						manager.add(workFlowDelAction);
						manager.add(workFlowExportAction);
						manager.add(workFlowAttriAction);
					}
				}
				
//				manager.add(workFlowImportAction);
			}
		});
		
		Tree tree = treeViewer.getTree();
		Menu treeMenu = menuManager.createContextMenu(tree);
		tree.setMenu(treeMenu);
		
		super.fillContextMenu(menu);
	}
	
	public void initAction() {
		workFlowAddAction = new WorkFlowAction(ADD_FLAG, null);
		workFlowModifyAction = new WorkFlowAction(MODIFY_FLAG, treeViewer);
		workFlowDelAction = new WorkFlowAction(DELETE_FLAG, treeViewer);
		workFlowViewAction = new WorkFlowAction(VIEW_FLAG, treeViewer);
		workFlowExportAction = new WorkFlowAction(EXPORT_FLAG, treeViewer);
		workFlowAttriAction = new WorkFlowAction(ATTRI_FLAG, treeViewer);
		workFlowImportAction = new WorkFlowAction(IMPORT_FLAG, treeViewer);
	}
	
	private Object getCurrentSelection() {
		IStructuredSelection iSelection = (IStructuredSelection) treeViewer.getSelection();
		Object element = iSelection.getFirstElement();
		return element;
	}
}
