/* 文件名：     DocCategoryDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-2-17
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog.modelattri;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.powerdesigner.command.UpdateDocCategoryModelCommand;
import cn.sunline.suncard.powerdesigner.model.DocumentCategoryModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.ProjectModel;
import cn.sunline.suncard.powerdesigner.models.DocumentsNodeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProjectDefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;

/**
 * 文档分类对象的对话框
 * @author  Manzhizhen
 * @version 1.0, 2013-2-17
 * @see 
 * @since 1.0
 */
public class DocCategoryDialog extends TitleAreaDialog{
	private Composite composite;
	private Text nameText;
	private Text descText;
	private String flag;
	
	private TreeContent documentsNodeTreeContent;
	private DocumentCategoryModel documentCategoryModel;
	
	public DocCategoryDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		// 文档分类对话框
		newShell.setText(I18nUtil.getMessage("DOCCATEGORY_DIALOG"));
		setDefaultImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.DOC_CATEGROY_LABEL_64));
		super.configureShell(newShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(550, 250);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		// 文档分类模型属性
		setTitle(I18nUtil.getMessage("DOCCATEGORYMODEL_ATTRI"));
		setMessage(I18nUtil.getMessage("DOCCATEGORYMODEL_ATTRI"));
		setTitleImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.DOC_CATEGROY_LABEL_64));
		
		Control control = super.createDialogArea(parent);
		
		composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new FormLayout());
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.width = 35;
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("名称:");
		
		nameText = new Text(composite, SWT.BORDER);
		FormData fd_nameText = new FormData();
		fd_nameText.right = new FormAttachment(100, -10);
		fd_nameText.top = new FormAttachment(lblNewLabel, -3, SWT.TOP);
		fd_nameText.left = new FormAttachment(lblNewLabel, 6);
		nameText.setLayoutData(fd_nameText);
		
//		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
//		lblNewLabel_1.setAlignment(SWT.RIGHT);
//		FormData fd_lblNewLabel_1 = new FormData();
//		fd_lblNewLabel_1.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
//		fd_lblNewLabel_1.width = 35;
//		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
//		lblNewLabel_1.setText("描述:");
//		
//		descText = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.MULTI);
//		fd_lblNewLabel_1.top = new FormAttachment(descText, 3, SWT.TOP);
//		FormData fd_descText = new FormData();
//		fd_descText.bottom = new FormAttachment(100, -10);
//		fd_descText.right = new FormAttachment(100, -10);
//		fd_descText.top = new FormAttachment(nameText, 6);
//		fd_descText.left = new FormAttachment(nameText, 0, SWT.LEFT);
//		descText.setLayoutData(fd_descText);
		
		createControl();
		initControlValue();
		createEvent();
		
		return control;
	}

	private void createControl() {
		

	}
	
	private void initControlValue() {
		if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			if(documentCategoryModel == null) {
				setErrorMessage("传入的数据不完整，无法初始化控件值！");
				return ;
			}
			
			nameText.setText(documentCategoryModel.getName() + "");
		}
	}
	
	private void createEvent() {
		nameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				checkData();
			}
		});
	}
	
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	public void setDocumentsNodeTreeContent(TreeContent documentsNodeTreeContent) {
		this.documentsNodeTreeContent = documentsNodeTreeContent;
	}

	@Override
	protected void okPressed() {
		if(!checkData()) {
			return ;
		}
		
		UpdateDocCategoryModelCommand command = new UpdateDocCategoryModelCommand();
		command.setFlag(flag);
		ProductModel productModel = ((DocumentsNodeModel)documentsNodeTreeContent.getObj()).getParentModel();
		if(DmConstants.COMMAND_ADD.equals(flag)) {
			DocumentCategoryModel model = new DocumentCategoryModel();
			model.setName(nameText.getText().trim());
			model.setDocumentsNodeModel((DocumentsNodeModel) documentsNodeTreeContent
					.getObj());
			
			command.setDocumentCategoryModel(model);
			command.setDocumentsNodeTreeContent(documentsNodeTreeContent);
			
			if(productModel instanceof ProjectModel) {
				CommandStack commandStack = ProjectDefaultViewPart.getCommandStackFromProjectModel(
						(ProjectModel) productModel);
				if(commandStack != null) {
					commandStack.execute(command);
				}
			} else {
				CommandStack commandStack = DefaultViewPart.getFileCommandFromObj(model);
				if(commandStack != null) {
					commandStack.execute(command);
				}
			}
			
		} else if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			command.setDocumentCategoryModel(documentCategoryModel);
			command.setNewName(nameText.getText().trim());
			
			if(productModel instanceof ProjectModel) {
				CommandStack commandStack = ProjectDefaultViewPart.getCommandStackFromProjectModel(
						(ProjectModel) productModel);
				if(commandStack != null) {
					commandStack.execute(command);
				}
			} else {
				CommandStack commandStack = DefaultViewPart.getFileCommandFromObj(documentCategoryModel);
				if(commandStack != null) {
					commandStack.execute(command);
				}
			}
		}
		
		super.okPressed();
	}

	private boolean checkData() {
		if(nameText.getText().trim().isEmpty()) {
			setErrorMessage("文档类别名称不能为空！");
			return false;
		}
		
		DocumentsNodeModel nodeModel = (DocumentsNodeModel) documentsNodeTreeContent
				.getObj();
		List<String> docCategoryNameList = new ArrayList<String>();
		for(DocumentCategoryModel docCategoryModel : nodeModel.getDocCategoryModelSet()) {
			docCategoryNameList.add(docCategoryModel.getName());
		}

		String newName = nameText.getText().trim();
		// 文档类型名称必须唯一
		if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			docCategoryNameList.remove(documentCategoryModel.getName());
		} 
		if(docCategoryNameList.contains(newName)) {
			setErrorMessage("该文档类别已经存在！");
			return false;
		}
		
		setErrorMessage(null);
		setMessage(I18nUtil.getMessage("DOCCATEGORY_DIALOG"));
		return true;
	}
	
	
	public void setDocumentCategoryModel(DocumentCategoryModel documentCategoryModel) {
		this.documentCategoryModel = documentCategoryModel;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("CANCEL"), false);
	}
}
