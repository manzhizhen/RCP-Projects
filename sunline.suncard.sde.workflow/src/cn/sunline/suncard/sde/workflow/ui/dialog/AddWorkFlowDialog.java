/* 文件名：     AddWorkFlowDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-28
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.ui.dialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.sde.workflow.action.WorkFlowActionGroup;
import cn.sunline.suncard.sde.workflow.common.DmConstants;
import cn.sunline.suncard.sde.workflow.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.sde.workflow.file.SwitchObjectAndFile;
import cn.sunline.suncard.sde.workflow.gef.ui.editor.WorkFlowEditor;
import cn.sunline.suncard.sde.workflow.gef.ui.editor.WorkFlowEditorInput;
import cn.sunline.suncard.sde.workflow.model.WorkFlowTreeNode;
import cn.sunline.suncard.sde.workflow.tree.WorkFlowTreeViewPart;
import cn.sunline.suncard.sde.workflow.tree.factory.TreeContent;
import cn.sunline.suncard.sde.workflow.tree.factory.TreeViewComposite;
import cn.sunline.suncard.sde.workflow.ui.dialog.factory.TitleAreaDialogMould;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 新增工作流的对话框
 * @author  易振强
 * @version 1.0, 2012-3-28
 * @see 
 * @since 1.0
 */
public class AddWorkFlowDialog extends TitleAreaDialogMould{
	private Composite composite;
	private Text idText;
	private Text nameText;
	private Text descText;
	private TreeViewComposite treeComposite;
	
	private String flag;
	
	private WorkFlowTreeNode node;
	
	Log logger = LogManager.getLogger(AddWorkFlowDialog.class);
	
	public AddWorkFlowDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(509, 336);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		if(WorkFlowActionGroup.ATTRI_FLAG.equals(flag)) {
			newShell.setText(I18nUtil.getMessage("ACTION_ATTRI"));
		} else {
			newShell.setText(I18nUtil.getMessage("NEW_WORKFLOW"));
		}
		
		super.configureShell(newShell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		if(WorkFlowActionGroup.ATTRI_FLAG.equals(flag)) {
			setTitle(I18nUtil.getMessage("ACTION_ATTRI"));
		} else {
			setTitle(I18nUtil.getMessage("NEW_WORKFLOW"));
		}
		
		Control control = super.createDialogArea(parent);
		composite = super.getMouldComposite();
		
		createControl();	// 创建控件
		
		try {
			initControlValue();	// 初始化控件的值
		} catch (PartInitException e) {
			logger.error("打开或显示工作流树失败！" +  e.getMessage());
			e.printStackTrace();
		}		
		
		return control;
	}

	/**
	 * 创建对话框控件
	 */
	private void createControl() {
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.right = new FormAttachment(0, 70);
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText(I18nUtil.getMessage("WORKFLOW_ID") + ":");
		
		idText = new Text(composite, SWT.BORDER);
		FormData fd_idText = new FormData();
		fd_idText.left = new FormAttachment(lblNewLabel, 5);
		fd_idText.right = new FormAttachment(50, 0);
		fd_idText.top = new FormAttachment(0, 7);
		idText.setLayoutData(fd_idText);
		
		idText.setEditable(false);
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		fd_lblNewLabel_1.top = new FormAttachment(lblNewLabel, 15, SWT.BOTTOM);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText(I18nUtil.getMessage("WORKFLOW_NAME") + ":");
		
		nameText = new Text(composite, SWT.BORDER);
		FormData fd_nameText = new FormData();
		fd_nameText.right = new FormAttachment(50, 0);
		fd_nameText.left = new FormAttachment(lblNewLabel_1, 5);
		fd_nameText.top = new FormAttachment(idText, 9);
		nameText.setLayoutData(fd_nameText);
		nameText.setFocus();
		
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.top = new FormAttachment(lblNewLabel_1, 15, SWT.BOTTOM);
		fd_lblNewLabel_2.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		lblNewLabel_2.setText(I18nUtil.getMessage("WORKFLOW_DESC") + ":");
		
		descText = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		FormData fd_descText = new FormData();
		fd_descText.bottom = new FormAttachment(nameText, 109, SWT.BOTTOM);
		fd_descText.top = new FormAttachment(nameText, 9);
		fd_descText.left = new FormAttachment(lblNewLabel_2, 5);
		fd_descText.right = new FormAttachment(100, -5);
		descText.setLayoutData(fd_descText);
	}

	/**
	 * 初始化控件的值
	 * @throws PartInitException 
	 */
	private void initControlValue() throws PartInitException {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if(page == null) {
			logger.error("无法找到活跃的WorkbenchPage！");
			return ;
		}  
		
		WorkFlowTreeViewPart view = null;
		
		IViewReference[] refs = page.getViewReferences();
		for(IViewReference ref : refs) {
			if(WorkFlowTreeViewPart.ID.equals(ref.getId())) {
				view = (WorkFlowTreeViewPart) ref.getView(true);
				break;
			}
		}
		
		if(view == null) {
			view = (WorkFlowTreeViewPart) page.showView(WorkFlowTreeViewPart.ID);
		}
		
		treeComposite = view.getTreeViewComposite();
		
		// 如果只是修改工作流属性，则只要赋值就行
		if(WorkFlowActionGroup.ATTRI_FLAG.equals(flag) && node != null) {
			idText.setText(node.getId() == null ? "" : node.getId());
			nameText.setText(node.getName() == null ? "" : node.getName());
			descText.setText(node.getDesc() == null ? "" : node.getDesc());
			
			return ;
		}
		
		TreeContent fatherWorkFlow = treeComposite.getRootContent();
		
		if(fatherWorkFlow == null || fatherWorkFlow.getChildrenList().size() == 0) {
			idText.setText(DmConstants.WORK_FLOW_ID + "00000001");
			
		} else {
			Long idNum = new Long(1);
			List<TreeContent> childList = fatherWorkFlow.getChildrenList();
			List<Long> idList = new ArrayList<Long>();
			
			for(TreeContent treeChild : childList) {
				String id = ((WorkFlowTreeNode)treeChild.getObj()).getId();
				idList.add(new Long(id.substring(2)));
			}
			
			while(idList.contains(idNum)) {
				idNum = new Long(idNum.longValue() + 1);
			}
			
			idText.setText(DmConstants.WORK_FLOW_ID + "00000000".substring(idNum.toString().length()) + idNum.toString());
			
		}
	}

	/**
	 * 获得新的工作流ID
	 */
	public static String getNewWorkFlowId(TreeViewComposite treeComposite) {
		TreeContent fatherWorkFlow = treeComposite.getRootContent();
		
		if(fatherWorkFlow == null || fatherWorkFlow.getChildrenList().size() == 0) {
			return DmConstants.WORK_FLOW_ID + "00000001";
			
		} else {
			Long idNum = new Long(1);
			List<TreeContent> childList = fatherWorkFlow.getChildrenList();
			List<Long> idList = new ArrayList<Long>();
			
			for(TreeContent treeChild : childList) {
				String id = ((WorkFlowTreeNode)treeChild.getObj()).getId();
				idList.add(new Long(id.substring(2)));
			}
			
			while(idList.contains(idNum)) {
				idNum = new Long(idNum.longValue() + 1);
			}
			
			return DmConstants.WORK_FLOW_ID + "00000000".substring(idNum.toString().length()) + idNum.toString();
		}
	}
	

	@Override
	protected void okPressed() {
		// 如果只是修改工作流属性，则不打开对应editor了
		if(WorkFlowActionGroup.ATTRI_FLAG.equals(flag) && node != null) {
			node.setId(idText.getText());
			node.setName(nameText.getText().trim());
			node.setDesc(descText.getText().trim());

			try {
				treeComposite.updateNode(node.getId(), node.getName() + "-" + node.getDesc(), node);
			} catch (CanNotFoundNodeIDException e) {
				logger.error("更新树节点失败！");
				e.printStackTrace();
				
				return ;
			}
			
			try {
				SwitchObjectAndFile.SaveWorkFlowToFile(node);
			} catch (IOException e1) {
				logger.error("将对象保存为文件失败！" + e1.getMessage());
				e1.printStackTrace();
				
				return ;
			}
			
			// 更改打开的Editor的名字
			IEditorReference[] refers = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
			for(IEditorReference refer : refers) {
				IEditorPart editorPart = refer.getEditor(true);
				if(editorPart instanceof WorkFlowEditor) {
					WorkFlowEditor workFlowEditor = (WorkFlowEditor) editorPart;
					if(node.equals(workFlowEditor.getTreeNode())) {
						workFlowEditor.setPartName(node.getName());
					}
				}
			}
			
			super.okPressed();
			return ;
			
		} else {
			WorkFlowTreeNode treeNode = new WorkFlowTreeNode();
			treeNode.setId(idText.getText());
			treeNode.setName(nameText.getText().trim());
			treeNode.setDesc(descText.getText().trim());
			
			
			try {
				SwitchObjectAndFile.SaveWorkFlowToFile(treeNode);
			} catch (IOException e1) {
				logger.error("将对象保存为文件失败！" + e1.getMessage());
				e1.printStackTrace();
				
				return ;
			}
			
			if(treeComposite == null) {
				setMessage(I18nUtil.getMessage("UPDATE_TREE_FAIL"));
				logger.error("找不到工作流树，更新树失败！");
				super.okPressed();
			}
			
			try {
				treeComposite.addNode(DmConstants.WORK_FLOW_TREE_ROOT_ID, treeNode.getId(),
						treeNode.getName() + "-" + treeNode.getDesc(), treeNode);
			} catch (CanNotFoundNodeIDException e1) {
				setMessage("未知的父节点ID！  " + e1.getMessage());
				e1.printStackTrace();
				
				return ;
			}
			
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				
				WorkFlowEditorInput input = new WorkFlowEditorInput();
				input.setState(WorkFlowActionGroup.MODIFY_FLAG);
				input.setName(treeNode.getName() + "-" + I18nUtil.getMessage("MODIFY_EDITOR"));
				input.setTreeModel(treeNode);
				
				WorkFlowEditor workFlowEditor = (WorkFlowEditor) page.openEditor(
						input, WorkFlowEditor.ID);
				
			} catch (PartInitException e) {
				MessageDialog.openError(page.getWorkbenchWindow().getShell(), 
						I18nUtil.getMessage("ERROR"), I18nUtil.getMessage("VIEWPART_ERROR"));
				
				// 写日志
				logger.error(I18nUtil.getMessage("VIEWPART_ERROR") + " - " +
						e.getMessage());
				
				e.printStackTrace();
			}
		}
		
		super.okPressed();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("CANCEL"), false);
	}
	
	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setNode(WorkFlowTreeNode node) {
		this.node = node;
	}
	
}
