/* 文件名：     ActionTreeAction.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-9
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.workflow.Activator;
import cn.sunline.suncard.sde.workflow.common.DmConstants;
import cn.sunline.suncard.sde.workflow.gef.ui.editor.WorkFlowEditor;
import cn.sunline.suncard.sde.workflow.model.ActionTreeNode;
import cn.sunline.suncard.sde.workflow.model.WorkFlowTreeNode;
import cn.sunline.suncard.sde.workflow.resource.IDmImageKey;
import cn.sunline.suncard.sde.workflow.tree.factory.TreeContent;
import cn.sunline.suncard.sde.workflow.ui.editor.ActionEditor;
import cn.sunline.suncard.sde.workflow.ui.editor.ActionEditorInput;

/**
 * Action树的Action
 * @author  易振强
 * @version 1.0, 2012-4-9
 * @see 
 * @since 1.0
 */
public class ActionTreeAction extends Action{
	Log logger = LogManager.getLogger(ActionTreeAction.class);
	private TreeViewer treeViewer;	// 树视图
	private String editorState;		// 要打开的Editor的状态
	
	private ActionEditor actionEditor;
	
	public ActionTreeAction(String editorState, TreeViewer treeViewer) {
		this.editorState = editorState;
		this.treeViewer = treeViewer;
		
		if(WorkFlowActionGroup.ADD_FLAG.equals(editorState)) {
			setText(I18nUtil.getMessage("ACTION_ADD"));
			
		} else if(WorkFlowActionGroup.DELETE_FLAG.equals(editorState)) {
			setText(I18nUtil.getMessage("ACTION_DELETE"));
			
		} else if(WorkFlowActionGroup.MODIFY_FLAG.equals(editorState)) {
			setText(I18nUtil.getMessage("ACTION_MODIFY"));
			
		} else if(WorkFlowActionGroup.VIEW_FLAG.equals(editorState)) {
			setText(I18nUtil.getMessage("ACTION_VIEW"));
			
		}
	}
	
	@Override
	public void run() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		if(page == null) {
			logger.error("找不到活跃的WorkbenchPage，无法打开Editor！");
			return ;
		}
		
		if(WorkFlowActionGroup.ADD_FLAG.equals(editorState)) {
			addRun(page);
			
		} else if(WorkFlowActionGroup.DELETE_FLAG.equals(editorState)) {
			deleteRun(page);
			
		} else if(WorkFlowActionGroup.MODIFY_FLAG.equals(editorState)) {
			modifyRun(page);
			
		} else if(WorkFlowActionGroup.VIEW_FLAG.equals(editorState)) {
			viewRun(page);
			
		}
	}
	
	/**
	 * 查看Action
	 */
	private void viewRun(IWorkbenchPage page) {
		try {
			ActionTreeNode treeNode = getSelect();
			
			if(treeNode == null) {
				return ;
			}
			
			ActionEditorInput input = new ActionEditorInput();
			input.setFlag(ActionTreeActionGroup.VIEW_FLAG);
			input.setName(treeNode.getName() + "-" + I18nUtil.getMessage("VIEW_EDITOR"));
			input.setNode(treeNode);
			
			ActionEditor actionEditor = (ActionEditor) page.openEditor(
					input, ActionEditor.ID);
			actionEditor.initData();
			
		} catch (PartInitException e) {
			// 写日志
			logger.error("查看工作流失败!");
			
			e.printStackTrace();
		}
	}

	/**
	 * 删除Action
	 */
	private void deleteRun(IWorkbenchPage page) {
		try {
			ActionTreeNode treeNode = getSelect();
			
			if(treeNode == null) {
				return ;
			}
			
			ActionEditorInput input = new ActionEditorInput();
			input.setFlag(ActionTreeActionGroup.DELETE_FLAG);
			input.setName(treeNode.getName() + "-" + I18nUtil.getMessage("DEL_EDITOR"));
			input.setNode(treeNode);
			
			ActionEditor actionEditor = (ActionEditor) page.openEditor(
					input, ActionEditor.ID);
			actionEditor.initData();
			
		} catch (PartInitException e) {
			// 写日志
			logger.error("修改工作流失败!");
			
			e.printStackTrace();
		}
	}

	/**
	 * 新增一个Action
	 */
	private void addRun(IWorkbenchPage page) {
		try {
			ActionTreeNode treeNode = new ActionTreeNode();
			
			ActionEditorInput input = new ActionEditorInput();
			input.setFlag(ActionTreeActionGroup.ADD_FLAG);
			input.setName(I18nUtil.getMessage("NEW_OBJECT"));
			input.setNode(treeNode);
			
			ActionEditor actionEditor = (ActionEditor) page.openEditor(
					input, ActionEditor.ID);
			actionEditor.initData();
			
		} catch (PartInitException e) {
			// 写日志
			logger.error("修改工作流失败!");
			
			e.printStackTrace();
		}
	}
	
	/**
	 * 新增一个Action
	 */
	private void modifyRun(IWorkbenchPage page) {
		try {
			ActionTreeNode treeNode = getSelect();
			
			if(treeNode == null || treeNode.getName() == null) {
				return ;
			}
			
			ActionEditorInput input = new ActionEditorInput();
			input.setFlag(ActionTreeActionGroup.MODIFY_FLAG);
			input.setName(treeNode.getName() + "-" + I18nUtil.getMessage("MODIFY_EDITOR"));
			input.setNode(treeNode);
			
			ActionEditor actionEditor = (ActionEditor) page.openEditor(
					input, ActionEditor.ID);
			actionEditor.initData();
			
		} catch (PartInitException e) {
			// 写日志
			logger.error("修改工作流失败!");
			
			e.printStackTrace();
		}
	}
	
	public ActionTreeNode getSelect() {
		StructuredSelection selection = (StructuredSelection) treeViewer.getSelection();
		TreeContent select = (TreeContent) selection.getFirstElement();
		
		if(select == null) {
			return null;
		}
		
		ActionTreeNode treeNode = (ActionTreeNode) select.getObj();
		
		if(treeNode == null) {
			return null;
		}
		
		return treeNode;
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		ImageDescriptor descriptor = null;
		
		if(editorState == ActionTreeActionGroup.ADD_FLAG) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_NEW_IMAGE);
				
		} else if (editorState == ActionTreeActionGroup.DELETE_FLAG) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_DELETE_IMAGE);
				
		} else if (editorState == ActionTreeActionGroup.MODIFY_FLAG) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_MODIFY_IMAGE);
				
		} else if (editorState == ActionTreeActionGroup.VIEW_FLAG) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_VIEW_IMAGE);
				
		} 
		
		return descriptor;
	}

}
