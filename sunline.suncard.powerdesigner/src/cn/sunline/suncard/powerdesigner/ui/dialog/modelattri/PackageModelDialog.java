/* 文件名：     PackageModelDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-6
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog.modelattri;

import java.util.Set;

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

import cn.sunline.suncard.powerdesigner.command.UpdatePackageModelCommand;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.PackageModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;

/**
 * 包模型对话框
 * @author  Manzhizhen
 * @version 1.0, 2012-12-6
 * @see 
 * @since 1.0
 */
public class PackageModelDialog extends TitleAreaDialog {

	private TreeContent physicalDataTreeContent;
	private TreeContent packageModelTreeContent;
	private PackageModel packageModel;
	private String flag;
	
	private Composite composite;
	private Text idText;
	private Text nameText;
	private Text noteText;

	public PackageModelDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.BORDER | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		// 包模型对话框
		newShell.setText(I18nUtil.getMessage("PACKAGEMODEL_DIALOG"));
		setDefaultImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.PACKAGE_IMAGE_16));
		super.configureShell(newShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(581, 333);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		// 包模型属性
		setTitle(I18nUtil.getMessage("PACKAGEMODEL_ATTRI"));
		setMessage(I18nUtil.getMessage("PACKAGEMODEL_ATTRI"));
		setTitleImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.PACKAGE_IMAGE_64));
		
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
		idText.addModifyListener(new ModifyListener() {
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
	}

	private void initControlData() {
		if(DmConstants.COMMAND_MODIFY.equals(flag) && packageModel != null) {
			idText.setText(packageModel.getId() == null ? "" : packageModel.getId());
			nameText.setText(packageModel.getName() == null ? "" : packageModel.getName());
			noteText.setText(packageModel.getNote() == null ? "" : packageModel.getNote());			
			// 如果是修改，则ID不可编辑
			idText.setEnabled(false);
		}
	}

	private void createControl() {
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.left = new FormAttachment(0);
		fd_lblNewLabel.width = 60;
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("包ID:");
		idText = new Text(composite, SWT.BORDER);
		fd_lblNewLabel.top = new FormAttachment(idText, 3, SWT.TOP);
		FormData fd_idText = new FormData();
		fd_idText.right = new FormAttachment(100, -10);
		fd_idText.top = new FormAttachment(0, 4);
		fd_idText.left = new FormAttachment(lblNewLabel, 6);
		idText.setLayoutData(fd_idText);
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_lblNewLabel_1.width = 60;
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("包名称:");
		nameText = new Text(composite, SWT.BORDER);
		fd_lblNewLabel_1.top = new FormAttachment(nameText, 3, SWT.TOP);
		FormData fd_nameText = new FormData();
		fd_nameText.right = new FormAttachment(100, -10);
		fd_nameText.top = new FormAttachment(idText, 11);
		fd_nameText.left = new FormAttachment(idText, 0, SWT.LEFT);
		nameText.setLayoutData(fd_nameText);
		
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.width = 60;
		fd_lblNewLabel_2.top = new FormAttachment(lblNewLabel_1, 19);
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		lblNewLabel_2.setText("包备注:");
		noteText = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		FormData fd_noteText = new FormData();
		fd_noteText.height = 70;
		fd_noteText.right = new FormAttachment(100, -10);
		fd_noteText.top = new FormAttachment(lblNewLabel_2, -3, SWT.TOP);
		fd_noteText.left = new FormAttachment(idText, 0, SWT.LEFT);
		noteText.setLayoutData(fd_noteText);
	}
	
	/**
	 * 检查数据的正确性
	 */
	private boolean checkData() {
		String str = idText.getText().trim();
		if(str.isEmpty()) {
			// 名称不能为空！
			setErrorMessage(I18nUtil.getMessage("NAME_NOT_EMPTY"));
			return false;
		}
		
		// 检查该文件下是否还有相同ID的包模型
		if(DmConstants.COMMAND_ADD.equals(flag) ) {
			if( physicalDataTreeContent == null || !(physicalDataTreeContent.getObj() instanceof PhysicalDataModel)) {
				// 找不到该包模型对应的物理数据模型，校验数据失败！
				setErrorMessage(I18nUtil.getMessage("CAN_FIND_PHYSICALDATAMODEL_FROM_PACKAGEMODEL_CHECK_DATA_FAIL"));
				return false;
			} else {
				PhysicalDataModel physicalDataModel =  (PhysicalDataModel) physicalDataTreeContent.getObj();
				Set<PackageModel> packgetModelSet = physicalDataModel.getPackageModelSet();
				for(PackageModel packageModel : packgetModelSet) {
					if(str.equals(packageModel.getId())) {
						// 此包模型ID已经被使用！
						setErrorMessage(I18nUtil.getMessage("PACKAGE_ID_IS_USED"));
						return false;
					}
				}
			}
		}
		
		String name = nameText.getText().trim();
		if(name.isEmpty()) {
			setErrorMessage("名称不能为空！");
			return false;
		}

		setErrorMessage(null);
		setMessage(I18nUtil.getMessage("PACKAGEMODEL_ATTRI"));
		
		return true;
	}
	
	@Override
	protected void okPressed() {
		if(!checkData()) {
			return ;
		}
		
		UpdatePackageModelCommand command = new UpdatePackageModelCommand();
		command.setFlag(flag);
		if(DmConstants.COMMAND_ADD.equals(flag)) {
			PackageModel packageModel = new PackageModel();
			packageModel.setId(idText.getText().trim());
			packageModel.setName(nameText.getText().trim());
			packageModel.setNote(noteText.getText().trim());
			packageModel.setPhysicalDataModel((PhysicalDataModel) physicalDataTreeContent.getObj());
			
			command.setPhysicalDataModelTreeContent(physicalDataTreeContent);
			command.setPackageModel(packageModel);
			
			CommandStack commandStack = DefaultViewPart.getFileCommandFromObj(FileModel
					.getFileModelFromObj(packageModel));
			if(commandStack != null) {
				commandStack.execute(command);
				// 刷新树上的文件节点
				DefaultViewPart.refreshFileModelTreeContent();
			}
			
		} else if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			command.setNewName(nameText.getText().trim());
			command.setNewNote(noteText.getText().trim());
			command.setPackageModel(packageModel);
			
			CommandStack commandStack = DefaultViewPart.getFileCommandFromObj(FileModel
					.getFileModelFromObj(packageModel));
			if(commandStack != null) {
				commandStack.execute(command);
				// 刷新树上的文件节点
				DefaultViewPart.refreshFileModelTreeContent();
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

	public void setPhysicalDataTreeContent(TreeContent physicalDataTreeContent) {
		this.physicalDataTreeContent = physicalDataTreeContent;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setPackageModelTreeContent(TreeContent packageModelTreeContent) {
		this.packageModelTreeContent = packageModelTreeContent;
		if(packageModelTreeContent != null) {
			packageModel = (PackageModel) packageModelTreeContent.getObj();
		}
	}
	
	
	
}
