/* 文件名：     UploadFileDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：    Manzhizhen
 * 修改时间：2013-3-8
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;


import java.io.File;

import org.dom4j.DocumentException;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.powerdesigner.command.UpdateDocumentModelCommand;
import cn.sunline.suncard.powerdesigner.model.DocumentCategoryModel;
import cn.sunline.suncard.powerdesigner.model.DocumentModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.ProjectModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProjectDefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ProgressBar;

/**
 * 上传文件的对话框
 * @author  Manzhizhen
 * @version 1.0, 2013-3-8
 * @see 
 * @since 1.0
 */
public class UploadFileDialog extends TitleAreaDialog {

	private Composite composite;
	
	private Log logger = LogManager.getLogger(UploadFileDialog.class.getName());
	private Text filePathText;

	private ProgressBar progressBar;
	private Button browseButton;
	private TreeContent docCategoryModelTreeContent;

	
	/**
	 * @param parentShell
	 */
	public UploadFileDialog(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void configureShell(Shell newShell) {
		// 上传文件对话框
		newShell.setText(I18nUtil.getMessage("UPLOAD_FILE_DIALOG"));
		setDefaultImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.UPLOAD_LABEL_16));
		super.configureShell(newShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(569, 256);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		// 上传文件
		setTitle(I18nUtil.getMessage("UPLOAD_FILE"));
		setMessage(I18nUtil.getMessage("UPLOAD_FILE"));
		setTitleImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.UPLOAD_LABEL_64));
		
		Control control =   super.createDialogArea(parent);
		
		composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new FormLayout());
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.width = 70;
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("上传的文件:");
		
		filePathText = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		FormData fd_filePathText = new FormData();
		fd_filePathText.right = new FormAttachment(100, -70);
		fd_filePathText.top = new FormAttachment(lblNewLabel, -3, SWT.TOP);
		fd_filePathText.left = new FormAttachment(lblNewLabel, 6);
		filePathText.setLayoutData(fd_filePathText);
		
		browseButton = new Button(composite, SWT.NONE);
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.right = new FormAttachment(100, -10);
		fd_btnNewButton.width = 50;
		fd_btnNewButton.top = new FormAttachment(filePathText, -2, SWT.TOP);
		browseButton.setLayoutData(fd_btnNewButton);
		browseButton.setText("浏览");
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.top = new FormAttachment(lblNewLabel, 15);
		fd_lblNewLabel_1.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_lblNewLabel_1.width = 70;
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("进度:");
		
		progressBar = new ProgressBar(composite, SWT.NONE);
		FormData fd_progressBar = new FormData();
		fd_progressBar.right = new FormAttachment(100, -10);
		fd_progressBar.bottom = new FormAttachment(lblNewLabel_1, 0, SWT.BOTTOM);
		fd_progressBar.left = new FormAttachment(filePathText, 0, SWT.LEFT);
		progressBar.setLayoutData(fd_progressBar);
		
		createControl();
		initControlValue();
		createEvent();
		
		return control;
	}

	private void createControl() {
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
				String filePath = dialog.open();
				if(filePath != null) {
					filePathText.setText(filePath);
				}
				
				super.widgetSelected(e);
			}
		});
		
		
	}
	
	/**
	 * 检查控件数据是否正确
	 * @return
	 */
	private boolean checkData() {
		if(filePathText.getText().isEmpty()) {
			setErrorMessage("请选择一个文件！");
			return false;
		}
		
		setErrorMessage(null);
		return true;
	}

	@Override
	protected void okPressed() {
		if(!checkData()) {
			return ;
		}
		
		File sourceFile = new File(filePathText.getText().trim());
		DocumentModel documentModel = new DocumentModel();
		documentModel.setFileName(sourceFile.getName());
		
		DocumentCategoryModel documentCategoryModel = (DocumentCategoryModel) docCategoryModelTreeContent.getObj();
		ProductModel productModel = documentCategoryModel.getDocumentsNodeModel().getParentModel();
		
		UpdateDocumentModelCommand command = new UpdateDocumentModelCommand();
		command.setFlag(DmConstants.COMMAND_ADD);
		command.setDocCategoryModelTreeContent(docCategoryModelTreeContent);
		command.setDocumentModel(documentModel);
		command.setSourceFile(sourceFile);
		
		if(productModel instanceof ProjectModel) {
			CommandStack commandStack = ProjectDefaultViewPart.getCommandStackFromProjectModel(
					(ProjectModel)productModel);
			if(commandStack != null) {
				commandStack.execute(command);
			}
		} else {
			CommandStack commandStack = DefaultViewPart.getFileCommandFromObj(docCategoryModelTreeContent.getObj());
			if(commandStack != null) {
				commandStack.execute(command);
			}
		}
		
		
		super.okPressed();
	}
	
	
	private void initControlValue() {
		// TODO Auto-generated method stub
		
	}

	private void createEvent() {
		// TODO Auto-generated method stub
		
	}
	
	
	public void setDocCategoryModelTreeContent(
			TreeContent docCategoryModelTreeContent) {
		this.docCategoryModelTreeContent = docCategoryModelTreeContent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("CANCEL"), false);
	}
}
