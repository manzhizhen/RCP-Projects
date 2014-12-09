/* 文件名：     ProjectGroupModelDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-29
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog.modelattri;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.powerdesigner.command.UpdateProjectGroupModelCommand;
import cn.sunline.suncard.powerdesigner.model.ProjectGroupModel;
import cn.sunline.suncard.powerdesigner.model.ProjectSpaceModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.tree.ProjectDefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProjectTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;

/**
 * 项目群模型对话框
 * @author  Manzhizhen
 * @version 1.0, 2012-12-29
 * @see 
 * @since 1.0
 */
public class ProjectGroupModelDialog extends TitleAreaDialog{
	private Text idText;
	private Text nameText;
	private Text noteText;
	private Composite composite;
	
	private String flag;
	private ProjectTreeViewPart projectTreeViewPart;
	private TreeContent projectSpaceTreeContent;
	private TreeContent projectGroupTreeContent;
	private Log logger = LogManager.getLogger(ProjectGroupModelDialog.class.getName());
	
	public ProjectGroupModelDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		// 项目群模型对话框
		newShell.setText(I18nUtil.getMessage("PROJECTGROUPMODEL_DIALOG"));
		setDefaultImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.PROJECT_GROUP_IMAGE_64));
		super.configureShell(newShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(550, 358);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		// 项目群模型属性
		setTitle(I18nUtil.getMessage("PROJECTGROUPMODEL_ATTRI"));
		setMessage(I18nUtil.getMessage("PROJECTGROUPMODEL_ATTRI"));
		setTitleImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.PROJECT_GROUP_IMAGE_64));
		
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
			if(projectGroupTreeContent == null || !(projectGroupTreeContent.getObj() instanceof ProjectGroupModel)) {
				setErrorMessage("传入的数据不完整或错误，无法初始化数据！");
				return ;
			}
			
			ProjectGroupModel projectGroupModel = (ProjectGroupModel) projectGroupTreeContent.getObj();
			
			idText.setText(projectGroupModel.getId() == null ? "" : projectGroupModel.getId());
			nameText.setText(projectGroupModel.getName() == null ? "" : projectGroupModel.getName());
			noteText.setText(projectGroupModel.getNote() == null ? "" : projectGroupModel.getNote());
			
			idText.setEditable(false);
		}
		
	}
	
	@Override
	protected void okPressed() {
		if(!checkData()) {
			return ;
		}
		
		UpdateProjectGroupModelCommand command = new UpdateProjectGroupModelCommand();
		command.setProjectTreeViewPart(projectTreeViewPart);
		command.setFlag(flag);
		if(DmConstants.COMMAND_ADD.equals(flag)) {
			command.setProjectSpaceTreeContent(projectSpaceTreeContent);
			
			ProjectGroupModel projectGroupModel = new ProjectGroupModel();
			projectGroupModel.setId(idText.getText().trim());
			projectGroupModel.setName(nameText.getText().trim());
			projectGroupModel.setNote(noteText.getText().trim());
			
			command.setProjectGroupModel(projectGroupModel);
			
			command.execute();
		} else if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			command.setProjectGroupTreeContent(projectGroupTreeContent);
			command.setNewName(nameText.getText().trim());
			command.setNewNote(noteText.getText().trim());
			
			command.execute();
		}
		
		super.okPressed();
	}

	/**
	 * 检查数据正确性
	 * @return
	 */
	private boolean checkData() {
		if(DmConstants.COMMAND_ADD.equals(flag)) {
			if(projectSpaceTreeContent == null || !(projectSpaceTreeContent.getObj() 
					instanceof ProjectSpaceModel)) {
				setErrorMessage("传入的数据不完整或错误，无法校验数据！");
				return false;
			}
			
			String str = idText.getText().trim();
			if(str.isEmpty()) {
				setErrorMessage("ID不能为空！");
				return false;
			}
			
			// 一个项目空间中的项目群ID必须唯一
			if(ProjectSpaceModel.getProjectGroupModelMap().keySet().contains(str)) {
				setErrorMessage("此ID已经被使用，请重新输入！");
				return false;
			}
			
			str = nameText.getText().trim();
			if(str.isEmpty()) {
				setErrorMessage("项目群名称不能为空！");
				return false;
			}
		} else if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			if(projectGroupTreeContent == null || !(projectGroupTreeContent.getObj() instanceof ProjectGroupModel)) {
				setErrorMessage("传入的数据不完整或错误，无法校验数据！");
				return false;
			}
			
			String str = nameText.getText().trim();
			if(str.isEmpty()) {
				setErrorMessage("项目群名称不能为空！");
				return false;
			}
		}
		
		setErrorMessage(null);
		setMessage(I18nUtil.getMessage("PROJECTGROUPMODEL_ATTRI"));
		return true;
	}

	private void createControl() {
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.width = 70;
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("项目群ID:");
		
		idText = new Text(composite, SWT.BORDER);
		FormData fd_idText = new FormData();
		fd_idText.right = new FormAttachment(50);
		fd_idText.top = new FormAttachment(lblNewLabel, -3, SWT.TOP);
		fd_idText.left = new FormAttachment(lblNewLabel, 6);
		idText.setLayoutData(fd_idText);
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		fd_lblNewLabel_1.width = 70;
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("项目群名称:");
		
		nameText = new Text(composite, SWT.BORDER);
		fd_lblNewLabel_1.top = new FormAttachment(nameText, 3, SWT.TOP);
		FormData fd_nameText = new FormData();
		fd_nameText.right = new FormAttachment(100, -5);
		fd_nameText.top = new FormAttachment(idText, 10);
		fd_nameText.left = new FormAttachment(idText, 0, SWT.LEFT);
		nameText.setLayoutData(fd_nameText);
		
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_lblNewLabel_2.width = 70;
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		lblNewLabel_2.setText("备注:");
		
		noteText = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		fd_lblNewLabel_2.top = new FormAttachment(noteText, 3, SWT.TOP);
		FormData fd_noteText = new FormData();
		fd_noteText.bottom = new FormAttachment(100, -10);
		fd_noteText.right = new FormAttachment(100);
		fd_noteText.top = new FormAttachment(nameText, 12);
		fd_noteText.left = new FormAttachment(idText, 0, SWT.LEFT);
		noteText.setLayoutData(fd_noteText);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("CANCEL"), false);
	}

	public void setProjectTreeViewPart(ProjectTreeViewPart projectTreeViewPart) {
		this.projectTreeViewPart = projectTreeViewPart;
	}

	public void setProjectSpaceTreeContent(TreeContent projectSpaceTreeContent) {
		this.projectSpaceTreeContent = projectSpaceTreeContent;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setProjectGroupTreeContent(TreeContent projectGroupTreeContent) {
		this.projectGroupTreeContent = projectGroupTreeContent;
	}
	
	
	
}
