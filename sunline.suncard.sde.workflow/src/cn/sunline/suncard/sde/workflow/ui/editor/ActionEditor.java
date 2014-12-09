/* 文件名：     ActionEditor.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-9
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.ui.editor;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.workflow.action.ActionTreeActionGroup;
import cn.sunline.suncard.sde.workflow.common.DmConstants;
import cn.sunline.suncard.sde.workflow.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.sde.workflow.file.SwitchObjectAndFile;
import cn.sunline.suncard.sde.workflow.gef.ui.editor.WorkFlowEditorInput;
import cn.sunline.suncard.sde.workflow.model.ActionTreeNode;
import cn.sunline.suncard.sde.workflow.tree.ActionTreeViewPart;
import cn.sunline.suncard.sde.workflow.tree.WorkFlowTreeViewPart;
import cn.sunline.suncard.sde.workflow.tree.factory.TreeViewComposite;

/**
 * Action的Editor
 * @author  易振强
 * @version 1.0, 2012-4-9
 * @see 
 * @since 1.0
 */
public class ActionEditor extends EditorPart{
	public final static String ID = "cn.sunline.suncard.sde.dm.ui.editor.ActionEditor";
	private Text nameText;
	private Text descText;
	
	private String flag;
	
	private Button okButton;
	private Button cancelButton;
	private Label messageLabel;
	private Label lblNewLabel;
	
	private TreeViewComposite treeComposite;
	private ActionTreeNode treeNode;
	
	private Log logger = LogManager.getLogger(ActionEditor.class.getName());

	public ActionEditor() {
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName());
		
		flag = ((ActionEditorInput)input).getFlag();
		treeNode = ((ActionEditorInput)input).getNode();
		
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FormLayout());
		creantControl(parent);
		
		

	}
	
	/**
	 * 创建控件
	 */
	private void creantControl(Composite parent) {
		lblNewLabel = new Label(parent, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText(I18nUtil.getMessage("ACTION_NAME") + ":");
		
		nameText = new Text(parent, SWT.BORDER);
		FormData fd_nameText = new FormData();
		fd_nameText.top = new FormAttachment(lblNewLabel, -3, SWT.TOP);
		fd_nameText.left = new FormAttachment(lblNewLabel, 7, SWT.RIGHT);
		fd_nameText.right = new FormAttachment(50, 0);
		nameText.setLayoutData(fd_nameText);
		
		Label lblNewLabel_1 = new Label(parent, SWT.NONE);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.top = new FormAttachment(lblNewLabel, 18);
		fd_lblNewLabel_1.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText(I18nUtil.getMessage("ACTION_DESC") + ":");
		
		descText = new Text(parent, SWT.BORDER);
		FormData fd_descText = new FormData();
		fd_descText.right = new FormAttachment(100, -10);
		fd_descText.top = new FormAttachment(nameText, 9);
		fd_descText.left = new FormAttachment(lblNewLabel_1, 7);
		descText.setLayoutData(fd_descText);
		
		cancelButton = new Button(parent, SWT.NONE);
		FormData fd_cancelButton = new FormData();
		fd_cancelButton.top = new FormAttachment(descText, 6);
		fd_cancelButton.right = new FormAttachment(descText, 0, SWT.RIGHT);
		fd_cancelButton.width = 80;
		cancelButton.setLayoutData(fd_cancelButton);
		cancelButton.setText(I18nUtil.getMessage("ACTION_CLOSE"));
		
		okButton = new Button(parent, SWT.NONE);
		FormData fd_okButton = new FormData();
		fd_okButton.bottom = new FormAttachment(cancelButton, 0, SWT.BOTTOM);
		fd_okButton.right = new FormAttachment(cancelButton, -6);
		fd_okButton.width = 80;
		okButton.setLayoutData(fd_okButton);
		
		messageLabel = new Label(parent, SWT.NONE);
		FormData fd_messageLabel = new FormData();
		fd_messageLabel.bottom = new FormAttachment(cancelButton, 0, SWT.BOTTOM);
		fd_messageLabel.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_messageLabel.right = new FormAttachment(okButton, -5, SWT.LEFT);
		messageLabel.setLayoutData(fd_messageLabel);
	}

	/**
	 * 初始化控件的值
	 */
	public void initData() {
		closeEvent();
		
		if(ActionTreeActionGroup.ADD_FLAG.equals(flag)) {
			okButton.setText(I18nUtil.getMessage("ACTION_ADD"));
			addEvent();
			
		} else if(ActionTreeActionGroup.MODIFY_FLAG.equals(flag)) {
			okButton.setText(I18nUtil.getMessage("ACTION_MODIFY"));
			modifyEvent();
			nameText.setEditable(false);
			
			if(treeNode == null) {
				return ;
			}
			
			nameText.setText(treeNode.getName() + "");
			descText.setText(treeNode.getDesc() + "");
			
		} else if(ActionTreeActionGroup.DELETE_FLAG.equals(flag)) {
			okButton.setText(I18nUtil.getMessage("ACTION_DELETE"));
			delEvent();
			nameText.setEditable(false);
			descText.setEditable(false);
			
			if(treeNode == null) {
				return ;
			}
			
			nameText.setText(treeNode.getName() + "");
			descText.setText(treeNode.getDesc() + "");
			
		} else if(ActionTreeActionGroup.VIEW_FLAG.equals(flag)) {
			okButton.setVisible(false);
			nameText.setEditable(false);
			descText.setEditable(false);
			
			if(treeNode == null) {
				return ;
			}
			
			nameText.setText(treeNode.getName() + "");
			descText.setText(treeNode.getDesc() + "");
		}
		
	}
	
	/**
	 * 删除Action
	 */
	private void delEvent() {
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				delAction();
			}
		});
	}
	
	private void delAction() {
		if(!MessageDialog.openConfirm(getSite().getShell(), I18nUtil.getMessage("MESSAGE"), I18nUtil.getMessage("SURE_DEL_ACTION"))) {
			return ;
		}
		
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		if(page == null) {
			logger.error("没找到活跃的WorkbenchPage！");
			return ;
		}
		
		try {
			// 关闭相关的Editor
			IEditorReference[] editorParts = page.getEditorReferences();
			
			for(IEditorReference editorPart : editorParts) {
				IEditorInput tempInput = editorPart.getEditorInput();
				if(tempInput instanceof ActionEditorInput) {
					if(treeNode == ((ActionEditorInput)tempInput).getNode()) {
						page.closeEditor(editorPart.getEditor(false), false);
					}
				}
			}
			
			// 删除目录下的文件
			String id = treeNode.getName();
			File file = new File(DmConstants.ACTION_DATA_FILE_PATH);	
			if(file.isDirectory()) {
				File[] files = file.listFiles();
				for(File allFile : files) {
					if(allFile.getName().contains(id)) {
						allFile.delete();
						break;
					}
				}
			}
			
			// 更新树
			IWorkbenchPart part = page.findView(ActionTreeViewPart.ID);
			if (part != null) {
				ActionTreeViewPart treeView = (ActionTreeViewPart) part;
				treeView.getTreeViewComposite().removeNode(treeNode.getName());
				
				// 更新静态列表
				WorkFlowTreeViewPart.allActionList.remove(treeNode);
			}
			
			
			closeOtherEditor();
			
		} catch (Exception e) {
			logger.error("删除Action失败！" + e.getMessage());
			MessageDialog.openError(page.getActivePart().getSite().getShell(), 
					I18nUtil.getMessage("MESSAGE"), I18nUtil.getMessage("DEL_ACTION_FAIL"));
		}
		
	}

	/**
	 * 修改Action
	 */
	private void modifyEvent() {
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				modifyAction();
			}
		});
	}

	/**
	 * 新增Action
	 */
	private void addEvent() {
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addAction();
			}
		});
	}
	
	private void closeEvent() {
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				closeEditorPart();
			}
		});
	}
	
	/**
	 *  关闭EditorPart
	 */
	public void closeEditorPart() {
		this.getSite().getPage().closeEditor(this, true);
//		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(this, true);
	}
	
	private void modifyAction() {
		if(!checkData() || treeNode == null) {
			return ;
		}	
		
		treeNode.setName(nameText.getText().trim());
		treeNode.setDesc(descText.getText().trim());
		
		try {
			SwitchObjectAndFile.SaveActionToFile(treeNode);
		} catch (IOException e1) {
			logger.error("将对象保存为文件失败！" + e1.getMessage());
			e1.printStackTrace();
			
			return ;
		}

		IViewReference[] viewParts = PlatformUI.getWorkbench().
				getActiveWorkbenchWindow().getActivePage().getViewReferences();
		
		boolean flag = true;
		for(IViewReference view : viewParts) {
			// 如果当时添加时树没被关闭，则向树中添加节点
			if(ActionTreeViewPart.ID.equals(view.getId())) {
				treeComposite = ((ActionTreeViewPart)view.getView(true)).getTreeViewComposite();
				flag = false;
				break;
			}
		}
		
		// 如果树已经被关闭，则直接打开树，打开树时节点自动更新。
		if(flag) {
			try {
				ActionTreeViewPart viewPart = (ActionTreeViewPart) PlatformUI.getWorkbench().
				getActiveWorkbenchWindow().getActivePage().showView(ActionTreeViewPart.ID);
			} catch (PartInitException e) {
				messageLabel.setText(I18nUtil.getMessage("OPEN_ACTION_TREE_FAIL"));
				logger.error("打开Action树失败！");
				e.printStackTrace();
			}
			return ;
		}
		
		
		if(treeComposite == null) {
			messageLabel.setText(I18nUtil.getMessage("UPDATE_TREE_FAIL"));
			logger.error("找不到Action树，更新树失败！");
			return ;
		}
		
		try {
			treeComposite.updateNode(treeNode.getName(), treeNode.getName() + "-" + treeNode.getDesc(), treeNode);
		} catch (CanNotFoundNodeIDException e) {
			logger.error("更新Action失败，找不到对应树节点ID：" + treeNode.getName());
			e.printStackTrace();
			return ;
		}
		
		messageLabel.setText(I18nUtil.getMessage("MODIFY_SUCCESS"));
		
		// 更新静态列表
//		ActionTreeNode delNode = null;
//		for(ActionTreeNode node : WorkFlowTreeViewPart.allActionList) {
//			if(node.getName().equals(treeNode.getName())) {
//				delNode = node;
//				break;
//			}
//		}
//		
//		WorkFlowTreeViewPart.allActionList.remove(delNode);
//		WorkFlowTreeViewPart.allActionList.add(treeNode);
		
		
	}
	
	private void addAction() {
		if(!checkData()) {
			return ;
		}
		
		ActionTreeNode node = new ActionTreeNode();
		node.setName(nameText.getText().trim());
		node.setDesc(descText.getText().trim());
		
		try {
			SwitchObjectAndFile.SaveActionToFile(node);
		} catch (IOException e1) {
			logger.error("将对象保存为文件失败！" + e1.getMessage());
			e1.printStackTrace();
			
			return ;
		}
		
		IViewReference[] viewParts = PlatformUI.getWorkbench().
				getActiveWorkbenchWindow().getActivePage().getViewReferences();
		
		boolean flag = true;
		for(IViewReference view : viewParts) {
			// 如果当时添加时树没被关闭，则向树中添加节点
			if(ActionTreeViewPart.ID.equals(view.getId())) {
				treeComposite = ((ActionTreeViewPart)view.getView(true)).getTreeViewComposite();
				flag = false;
				break;
			}
		}
		
		// 如果树已经被关闭，则直接打开树，打开树时节点自动更新。
		if(flag) {
			try {
				ActionTreeViewPart viewPart = (ActionTreeViewPart) PlatformUI.getWorkbench().
				getActiveWorkbenchWindow().getActivePage().showView(ActionTreeViewPart.ID);
			} catch (PartInitException e) {
				messageLabel.setText(I18nUtil.getMessage("OPEN_ACTION_TREE_FAIL"));
				logger.error("打开Action树失败！");
				e.printStackTrace();
			}
			return ;
		}
		
		
		if(treeComposite == null) {
			messageLabel.setText(I18nUtil.getMessage("UPDATE_TREE_FAIL"));
			logger.error("找不到Action树，更新树失败！");
			return ;
		}
		
		try {
			treeComposite.addNode(DmConstants.ACTION_TREE_ROOT_ID, node.getName(), node.getName() + "-" + node.getDesc(), node);
		} catch (CanNotFoundNodeIDException e) {
			logger.error("添加节点更新树失败，找不到对应父节点ID：" + DmConstants.ACTION_TREE_ROOT_ID);
			e.printStackTrace();
		}
		
		WorkFlowTreeViewPart.allActionList.add(node);
		
		getSite().getPage().closeEditor(this, true);
	}
	
	/**
	 * 关闭原因代码相关的Editor
	 */
	public void closeOtherEditor() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		IEditorReference[] editorParts = page.getEditorReferences();
		
		for(IEditorReference editorPart : editorParts) {
			try {
				IEditorInput tempInput = editorPart.getEditorInput();
				if(tempInput instanceof ActionEditorInput) {
					if(treeNode == ((ActionEditorInput)tempInput).getNode()) {
						page.closeEditor(editorPart.getEditor(false), false);
					}
				}
				
			} catch (PartInitException e) {
				logger.error("关闭Action相关EditorPart出错！" + "-" + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 校验数据的正确性
	 */
	private boolean checkData() {
		if("".equals(nameText.getText().trim())) {
			messageLabel.setText(I18nUtil.getMessage("ACTION_NAME_NOT_EMPTY"));
			
			return false;
		}
		
		if("".equals(descText.getText().trim())) {
			messageLabel.setText(I18nUtil.getMessage("ACTION_DESC_NOT_EMPTY"));
			
			return false;
		}
		
		if(ActionTreeActionGroup.ADD_FLAG.equals(flag)) {
			// 检查Action的id是否重复
			for(ActionTreeNode treeNode : WorkFlowTreeViewPart.allActionList) {
				if(treeNode.getName().equals(nameText.getText().trim())) {
					messageLabel.setText(I18nUtil.getMessage("ACTION_ID_IS_USEED"));
					return false;
				}
			}
		}
		
		messageLabel.setText("");
		return true;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}
