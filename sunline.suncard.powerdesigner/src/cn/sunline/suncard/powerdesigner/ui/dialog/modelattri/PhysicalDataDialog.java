/* 文件名：     PhysicalDataDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-5
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog.modelattri;

import java.util.List;
import java.util.Set;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.powerdesigner.command.UpdatePhysicalDataCommand;
import cn.sunline.suncard.powerdesigner.db.DatabaseTypeConfigXml;
import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.db.DatabaseTypeModel;
import cn.sunline.suncard.powerdesigner.provider.DatabaseTypeLabelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * 物理数据对话框
 * @author  Manzhizhen
 * @version 1.0, 2012-9-5
 * @see 
 * @since 1.0
 */
public class PhysicalDataDialog extends TitleAreaDialog{
	private Composite composite;
	private Text modelIdText;
	private Combo dbTypeCombo;
	private ComboViewer dbTypeViewer;
	
	private TreeContent fileModelTreeContent;
	
	private TreeContent physicalDataModelContent;	// 编辑物理数据模型时传入的树模型
	private PhysicalDataModel physicalDataModel; // 对应的物理数据模型
	private String flag;
	
	private Log logger = LogManager.getLogger(PhysicalDataDialog.class.getName());
	private Text nameText;
	private Text noteText;
	

	/**
	 * @param parentShell
	 */
	public PhysicalDataDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.BORDER | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		// 物理数据模型对话框
		newShell.setText(I18nUtil.getMessage("PHYSICALDATAMODEL_DIALOG"));
		setDefaultImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.PHYSICAL_DATA_IMAGE_16));
		super.configureShell(newShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(551, 372);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		// 物理数据模型属性
		setTitle(I18nUtil.getMessage("PHYSICALDATAMODEL_ATTRI"));
		setMessage(I18nUtil.getMessage("PHYSICALDATAMODEL_ATTRI"));
		setTitleImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.PHYSICAL_DATA_IMAGE_64));
		
		Control control =   super.createDialogArea(parent);
		
		composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new FormLayout());
		
		createControl();
		initControlData();
		createEvent();
		
		return control;
	}
	
	private void createEvent() {
		modelIdText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				checkData();
			}
		});
		
		nameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				checkData();
			}
		});
		
		dbTypeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				checkData();
			}
		});
		
	}

	private void initControlData() {
		dbTypeViewer.setContentProvider(new ArrayContentProvider());
		dbTypeViewer.setLabelProvider(new DatabaseTypeLabelProvider());
		
		dbTypeViewer.setInput(new DatabaseTypeConfigXml().getAllDatabaseType());
		
		dbTypeViewer.getCombo().setEnabled(true);
		
		if(DmConstants.COMMAND_MODIFY.equals(flag) && physicalDataModel != null) {
			modelIdText.setText(physicalDataModel.getId() == null ? "" : physicalDataModel.getId());
			nameText.setText(physicalDataModel.getName() == null ? "" : physicalDataModel.getName());
			noteText.setText(physicalDataModel.getNote() == null ? "" : physicalDataModel.getNote());
			dbTypeViewer.setSelection(new StructuredSelection(physicalDataModel.getDatabaseTypeModel()));
			dbTypeViewer.getCombo().setEnabled(false);
			
			// 如果是修改，则名称不可编辑
			modelIdText.setEnabled(false);
		}
	}

	private void createControl() {
		// 模型ID
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.width = 70;
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText(I18nUtil.getMessage("MODEL_ID") + ":");
		modelIdText = new Text(composite, SWT.BORDER);
		FormData fd_modelNameText = new FormData();
		fd_modelNameText.top = new FormAttachment(0, 7);
		fd_modelNameText.left = new FormAttachment(lblNewLabel, 5);
		fd_modelNameText.right = new FormAttachment(100, -10);
		modelIdText.setLayoutData(fd_modelNameText);
		
		// 模型名称
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		fd_lblNewLabel_2.width = 70;
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		lblNewLabel_2.setText(I18nUtil.getMessage("MODEL_NAME") + ":");
		nameText = new Text(composite, SWT.BORDER);
		fd_lblNewLabel_2.top = new FormAttachment(nameText, 3, SWT.TOP);
		FormData fd_nameText = new FormData();
		fd_nameText.right = new FormAttachment(100, -10);
		fd_nameText.top = new FormAttachment(modelIdText, 13);
		fd_nameText.left = new FormAttachment(modelIdText, 0, SWT.LEFT);
		nameText.setLayoutData(fd_nameText);
		
		Label lblNewLabel_3 = new Label(composite, SWT.NONE);
		lblNewLabel_3.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_3 = new FormData();
		fd_lblNewLabel_3.width = 70;
		fd_lblNewLabel_3.top = new FormAttachment(lblNewLabel_2, 20);
		fd_lblNewLabel_3.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		lblNewLabel_3.setLayoutData(fd_lblNewLabel_3);
		lblNewLabel_3.setText("模型备注:");
		noteText = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		FormData fd_noteText = new FormData();
		fd_noteText.height = 70;
		fd_noteText.top = new FormAttachment(nameText, 13);
		fd_noteText.left = new FormAttachment(modelIdText, 0, SWT.LEFT);
		fd_noteText.right = new FormAttachment(100, -10);
		noteText.setLayoutData(fd_noteText);
		
		// 数据库类型
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		fd_lblNewLabel_1.width = 70;
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText(I18nUtil.getMessage("DATABASE_TYPE") + ":");
		dbTypeCombo = new Combo(composite, SWT.READ_ONLY);
		FormData fd_dbTypeCombo = new FormData();
		fd_dbTypeCombo.top = new FormAttachment(noteText, 13);
		fd_dbTypeCombo.left = new FormAttachment(lblNewLabel_1, 6);
		fd_dbTypeCombo.right = new FormAttachment(100, -10);
		fd_lblNewLabel_1.top = new FormAttachment(dbTypeCombo, 3, SWT.TOP);
		dbTypeCombo.setLayoutData(fd_dbTypeCombo);
		dbTypeViewer = new ComboViewer(dbTypeCombo);
		
		
	}

	@Override
	protected void okPressed() {
		if(!checkData()) {
			return ;
		}
		
		UpdatePhysicalDataCommand command = new UpdatePhysicalDataCommand();
		// 说明用户是新建一个物理数据模型
		if(DmConstants.COMMAND_ADD.equals(flag) && physicalDataModel == null) {
			PhysicalDataModel physicalDataModel = new PhysicalDataModel();
			physicalDataModel.setId(modelIdText.getText().trim());
			physicalDataModel.setName(nameText.getText().trim());
			physicalDataModel.setNote(noteText.getText().trim());
			physicalDataModel.setDatabaseTypeModel((DatabaseTypeModel) ((IStructuredSelection)
					dbTypeViewer.getSelection()).getFirstElement());
			physicalDataModel.setFileModel((FileModel) fileModelTreeContent.getObj());
			
			command.setFlag(DmConstants.COMMAND_ADD);
			command.setPhysicalDataModel(physicalDataModel);
			command.setFileModelTreeContent(fileModelTreeContent);
			
			CommandStack commandStack = DefaultViewPart.getFileCommandFromObj(FileModel
					.getFileModelFromObj(physicalDataModel));
			if(commandStack != null) {
				commandStack.execute(command);
				// 刷新树上的文件节点
				DefaultViewPart.refreshFileModelTreeContent();
			}
			
		// 说明是修改物理图形数据
		} else if(DmConstants.COMMAND_MODIFY.equals(flag) && physicalDataModelContent != null){
			command.setFlag(DmConstants.COMMAND_MODIFY);

			command.setNewName(nameText.getText().trim());
			command.setNewNote(noteText.getText().trim());
			command.setNewDatabaseTypeModel((DatabaseTypeModel) 
					((IStructuredSelection)dbTypeViewer.getSelection()).getFirstElement());
			
			command.setPhysicalDataModelTreeContent(physicalDataModelContent);
			
			CommandStack commandStack = DefaultViewPart.getFileCommandFromObj(FileModel
					.getFileModelFromObj(physicalDataModelContent.getObj()));
			if(commandStack != null) {
				commandStack.execute(command);
				// 刷新树上的文件节点
				DatabaseTreeViewPart.refreshFileModelTreeContent();
			}
		} else {
			logger.error("无法获取父节点ID，向数据库树中添加、删除或更新物理数据节点失败！");
			setErrorMessage(I18nUtil.getMessage("UPDATE_PHYSICALDATAMODEL_TREE_NODE_FILE"));
			
			return ;	
		}
		
		
		super.okPressed();
	}
	
	/**
	 * 检查数据的正确性
	 */
	private boolean checkData() {
		String modelId = modelIdText.getText().trim();
		if(modelId.isEmpty()) {
			// ID不能为空！
			setErrorMessage(I18nUtil.getMessage("ID_NOT_EMPTY"));
			return false;
		}
		
		FileModel fileModel = null;
		// 检查该文件下是否还有同ID的物理数据模型
		if(DmConstants.COMMAND_ADD.equals(flag) ) {
			if( fileModelTreeContent == null || !(fileModelTreeContent.getObj() instanceof FileModel)) {
				// 找不到该物理数据模型对应的文件模型，校验数据失败！
				setErrorMessage(I18nUtil.getMessage("CAN_FIND_FILEMODEL_FROM_PHYSICALDATAMODEL_CHECK_DATA_FAIL"));
				return false;
			} 
			
			fileModel = (FileModel) fileModelTreeContent.getObj();
			
			Set<PhysicalDataModel> dataModelSet = fileModel.getPhysicalDataSet();
			for(PhysicalDataModel physicalDataModel : dataModelSet) {
				if(modelId.equals(physicalDataModel.getId())) {
					// 已经存在该名称的物理数据模型！
					setErrorMessage(I18nUtil.getMessage("PHYSICALDATAMODEL_NAME_IS_ALREADY"));
					return false;
				}
			}
			
		}
		
		String name = nameText.getText().trim();
		if("".equals(name)) {
			setErrorMessage("名称不能为空！");
			return false;
		}

		IStructuredSelection select = (IStructuredSelection) dbTypeViewer.getSelection();
		if(select.isEmpty()) {
			// 请选择一种数据库类型！
			setErrorMessage(I18nUtil.getMessage("MUST_CHOICE_ONE_DATABASETYPE"));
			return false;
		}
		
		setErrorMessage(null);
		setMessage(I18nUtil.getMessage("PHYSICALDATAMODEL_ATTRI"));
		
		return true;
	}
	
	public void setFileModelTreeContent(TreeContent fileModelTreeContent) {
		this.fileModelTreeContent = fileModelTreeContent;
	}

	public void setPhysicalDataModelContent(TreeContent physicalDataModelContent) {
		this.physicalDataModelContent = physicalDataModelContent;
		this.physicalDataModel = (PhysicalDataModel) physicalDataModelContent.getObj();
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
}
