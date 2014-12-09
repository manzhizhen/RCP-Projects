/* 文件名：     ProjectModelDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-31
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog.modelattri;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cn.sunline.suncard.powerdesigner.command.UpdateProjectModelCommand;
import cn.sunline.suncard.powerdesigner.db.DatabaseTypeConfigXml;
import cn.sunline.suncard.powerdesigner.model.ProjectModel;
import cn.sunline.suncard.powerdesigner.model.db.DatabaseTypeModel;
import cn.sunline.suncard.powerdesigner.provider.DatabaseTypeLabelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.tree.ProjectDefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProjectTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * 项目模型对话框
 * @author  Manzhizhen
 * @version 1.0, 2012-12-31
 * @see 
 * @since 1.0
 */
public class ProjectModelDialog extends TitleAreaDialog{
	private Text idText;
	private Text nameText;
	private Text noteText;
	private Composite composite;
	private String flag;
	private ProjectTreeViewPart projectTreeViewPart;
	private TreeContent projectTreeContent;
	private ComboViewer comboViewer;
	private Combo databaseTypeCombo;
	
	private Button btnUpdateTable;
	private Button btnUpdateSql;
	private Button btnUpdateProcess;
	private Button btnUpdateCode;
	private Button btnUpdateDoc;
	
	public ProjectModelDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		// 项目模型对话框
		newShell.setText(I18nUtil.getMessage("PROJECTMODEL_DIALOG"));
		setDefaultImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.PROJECT_16));
		super.configureShell(newShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(550, 520);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		// 项目群模型属性
		setTitle(I18nUtil.getMessage("PROJECTMODEL_ATTRI"));
		setMessage(I18nUtil.getMessage("PROJECTMODEL_ATTRI"));
		setTitleImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.PROJECT_64));
		
		Control control = super.createDialogArea(parent);
		
		composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new FormLayout());
		
		createControl();
		initControlValue();
		createEvent();
		
		return control;
	}
	
	private void createEvent() {
		// TODO Auto-generated method stub
		
	}

	private void initControlValue() {
		if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			if(projectTreeContent == null || !(projectTreeContent.getObj() instanceof ProjectModel)) {
				setErrorMessage("传入的数据不完整或错误，无法初始化数据！");
				return ;
			}
			
			ProjectModel projectModel = (ProjectModel) projectTreeContent.getObj();
			
			nameText.setText(projectModel.getName() == null ? "" : projectModel.getName());
			noteText.setText(projectModel.getNote() == null ? "" : projectModel.getNote());
			
			comboViewer.setContentProvider(new ArrayContentProvider());
			comboViewer.setLabelProvider(new DatabaseTypeLabelProvider());
			comboViewer.setInput(new DatabaseTypeConfigXml().getAllDatabaseType());
			comboViewer.setSelection(new StructuredSelection(projectModel.getDatabaseTypeModel()));
			
			// 如果该项目已经导入过产品模块，则不允许修改数据库类型
			if(projectModel.isAlreadyImport()) {
				databaseTypeCombo.setEnabled(false);
			}
			
			// 赋值可编辑性
			btnUpdateCode.setSelection(projectModel.isCanModifyCode());
			btnUpdateDoc.setSelection(projectModel.isCanModifyDoc());
			btnUpdateProcess.setSelection(projectModel.isCanModifyStored());
			btnUpdateSql.setSelection(projectModel.isCanModifySql());
			btnUpdateTable.setSelection(projectModel.isCanModifyTable());
		}
	}
	
	/**
	 * 检查数据正确性
	 * @return
	 */
	private boolean checkData() {
		if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			if(projectTreeContent == null || !(projectTreeContent.getObj() instanceof ProjectModel)) {
				setErrorMessage("传入的数据不完整或错误，无法校验数据！");
				return false;
			}
			
			String str = nameText.getText().trim();
			if(str.isEmpty()) {
				setErrorMessage("项目名称不能为空！");
				return false;
			}
			
			IStructuredSelection select = (IStructuredSelection)comboViewer.getSelection();
			if(select.isEmpty()) {
				setErrorMessage("请选择一种数据库类型！");
				return false;
			}
			
		}
		
		setErrorMessage(null);
		setMessage(I18nUtil.getMessage("PROJECTMODEL_ATTRI"));
		
		return true;
	}

	@Override
	protected void okPressed() {
		if(!checkData()) {
			return ;
		}
		
		UpdateProjectModelCommand command = new UpdateProjectModelCommand();
		command.setProjectTreeViewPart(projectTreeViewPart);
		command.setFlag(flag);
		
		if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			command.setProjectTreeContent(projectTreeContent);
			command.setNewName(nameText.getText().trim());
			command.setNewNote(noteText.getText().trim());
			command.setDatabaseTypeModel((DatabaseTypeModel) ((IStructuredSelection)comboViewer.getSelection()).getFirstElement());
			
			command.setCanModifyCode(btnUpdateCode.getSelection());
			command.setCanModifyDoc(btnUpdateDoc.getSelection());
			command.setCanModifySql(btnUpdateSql.getSelection());
			command.setCanModifyStored(btnUpdateProcess.getSelection());
			command.setCanModifyTable(btnUpdateTable.getSelection());
			
			IStructuredSelection select = (IStructuredSelection) comboViewer.getSelection();
			command.setDatabaseTypeModel((DatabaseTypeModel) select.getFirstElement());
			
			CommandStack commandStack = ProjectDefaultViewPart.getCommandStackFromProjectModel(
					(ProjectModel) projectTreeContent.getObj());
			if(commandStack != null) {
				commandStack.execute(command);
			}
		}
		
		super.okPressed();
	}
	
	private void createControl() {
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.left = new FormAttachment(0);
		fd_lblNewLabel_1.width = 70;
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("项目名称:");
		nameText = new Text(composite, SWT.BORDER);
		fd_lblNewLabel_1.top = new FormAttachment(nameText, 3, SWT.TOP);
		FormData fd_nameText = new FormData();
		fd_nameText.right = new FormAttachment(100, -5);
		fd_nameText.top = new FormAttachment(idText, 10);
		fd_nameText.left = new FormAttachment(lblNewLabel_1, 6);
		nameText.setLayoutData(fd_nameText);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.left = new FormAttachment(lblNewLabel_1, 0, SWT.LEFT);
		fd_lblNewLabel.width = 70;
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("数据库类型:");
		comboViewer = new ComboViewer(composite, SWT.NONE | SWT.READ_ONLY);
		databaseTypeCombo = comboViewer.getCombo();
		fd_lblNewLabel.top = new FormAttachment(databaseTypeCombo, 3, SWT.TOP);
		FormData fd_databaseTypeCombo = new FormData();
		fd_databaseTypeCombo.right = new FormAttachment(50);
		fd_databaseTypeCombo.top = new FormAttachment(nameText, 12);
		fd_databaseTypeCombo.left = new FormAttachment(nameText, 0, SWT.LEFT);
		databaseTypeCombo.setLayoutData(fd_databaseTypeCombo);
		
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.left = new FormAttachment(lblNewLabel_1, 0, SWT.LEFT);
		fd_lblNewLabel_2.width = 70;
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		lblNewLabel_2.setText("备注:");
		noteText = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		fd_lblNewLabel_2.top = new FormAttachment(noteText, 3, SWT.TOP);
		FormData fd_noteText = new FormData();
		fd_noteText.height = 150;
//		fd_noteText.bottom = new FormAttachment(100, -10);
		fd_noteText.top = new FormAttachment(databaseTypeCombo, 12);
		fd_noteText.right = new FormAttachment(100, -6);
		fd_noteText.left = new FormAttachment(lblNewLabel_2, 6);
		noteText.setLayoutData(fd_noteText);
		
		Group groupEditable = new Group(composite, SWT.NONE);
		groupEditable.setText("可编辑性");
		FormData fd_groupEditable = new FormData();
		fd_groupEditable.top = new FormAttachment(noteText, 12);
		fd_groupEditable.bottom = new FormAttachment(100, -10);
		fd_groupEditable.right = new FormAttachment(100, -6);
		fd_groupEditable.left = new FormAttachment(0, 6);
		groupEditable.setLayoutData(fd_groupEditable);
		groupEditable.setLayout(new FormLayout());
		
		btnUpdateTable = new Button(groupEditable, SWT.CHECK);
		btnUpdateTable.setText("允许修改表数据");
		FormData fd_btnUpdateTable = new FormData();
		fd_btnUpdateTable.top = new FormAttachment(0, 6);
		fd_btnUpdateTable.left = new FormAttachment(0, 6);
		btnUpdateTable.setLayoutData(fd_btnUpdateTable);
		
		btnUpdateSql = new Button(groupEditable, SWT.CHECK);
		btnUpdateSql.setText("允许修改SQL脚本");
		FormData fd_btnUpdateSql = new FormData();
		fd_btnUpdateSql.top = new FormAttachment(btnUpdateTable, 6);
		fd_btnUpdateSql.left = new FormAttachment(0, 6);
		btnUpdateSql.setLayoutData(fd_btnUpdateSql);
		
		btnUpdateProcess = new Button(groupEditable, SWT.CHECK);
		btnUpdateProcess.setText("允许修改存储过程");
		FormData fd_btnUpdateProcess = new FormData();
		fd_btnUpdateProcess.top = new FormAttachment(btnUpdateSql, 6);
		fd_btnUpdateProcess.left = new FormAttachment(0, 6);
		btnUpdateProcess.setLayoutData(fd_btnUpdateProcess);
		
		btnUpdateCode = new Button(groupEditable, SWT.CHECK);
		btnUpdateCode.setText("允许修改程序代码");
		FormData fd_btnUpdateCode = new FormData();
		fd_btnUpdateCode.top = new FormAttachment(btnUpdateProcess, 6);
		fd_btnUpdateCode.left = new FormAttachment(0, 6);
		btnUpdateCode.setLayoutData(fd_btnUpdateCode);
		
		btnUpdateDoc = new Button(groupEditable, SWT.CHECK);
		btnUpdateDoc.setText("允许修改文档");
		FormData fd_btnUpdateDoc = new FormData();
		fd_btnUpdateDoc.top = new FormAttachment(btnUpdateCode, 6);
		fd_btnUpdateDoc.left = new FormAttachment(0, 6);
		btnUpdateDoc.setLayoutData(fd_btnUpdateDoc);
		
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

	public void setProjectTreeViewPart(ProjectTreeViewPart projectTreeViewPart) {
		this.projectTreeViewPart = projectTreeViewPart;
	}

	public void setProjectTreeContent(TreeContent projectTreeContent) {
		this.projectTreeContent = projectTreeContent;
	}
}
