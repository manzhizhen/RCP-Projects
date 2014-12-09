/* 文件名：     PhysicalDragramDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-6
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog.modelattri;

import java.util.Set;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.powerdesigner.command.UpdatePhysicalDiagramCommand;
import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.PackageModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.factory.PhysicalDiagramModelFactory;
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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * 物理图形模型对话框
 * @author  Manzhizhen
 * @version 1.0, 2012-9-6
 * @see 
 * @since 1.0
 */
public class PhysicalDiagramDialog extends TitleAreaDialog{
	private Composite composite;
	private Text idText;
	private Text noteText;
	
	private TreeContent packageModelTreeContent;
	private TreeContent physicalDiagramTreeContent;
	private Text nameText;
	
	private String flag;
	private Log logger = LogManager.getLogger(PhysicalDiagramDialog.class.getName());
	
	/**
	 * @param parentShell
	 */
	public PhysicalDiagramDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		// 物理图形模型对话框
		newShell.setText(I18nUtil.getMessage("PHYSICALDIAGRAMMODEL_DIALOG"));
		setDefaultImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.PHYSICAL_DRAGRAM_IMAGE_16));
		super.configureShell(newShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(550, 369);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		// 物理图形模型属性
		setTitle(I18nUtil.getMessage("PHYSICALDIAGRAMMODEL_ATTRI"));
		setMessage(I18nUtil.getMessage("PHYSICALDIAGRAMMODEL_ATTRI"));
		setTitleImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.PHYSICAL_DRAGRAM_IMAGE_64));
		
		Control control =   super.createDialogArea(parent);
		
		composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new FormLayout());
		
		createControl();
		initControlValue();
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

	private void initControlValue() {
		if(DmConstants.COMMAND_MODIFY.equals(flag) && (physicalDiagramTreeContent 
				== null || !(physicalDiagramTreeContent.getObj() instanceof PhysicalDiagramModel))) {
			setErrorMessage("传入的物理图形树节点数据错误或为空，无法初始化控件数据！");
			logger.error("传入的物理图形树节点数据错误或为空，无法初始化控件数据！");
			return ;
		}
		
		if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			PhysicalDiagramModel model = (PhysicalDiagramModel) physicalDiagramTreeContent.getObj();
			idText.setText(model.getId() == null ? "" : model.getId());
			idText.setEnabled(false);
			nameText.setText(model.getName() == null ? "" : model.getName());
			noteText.setText(model.getNote() == null ? "" : model.getNote());
		}
	}

	private void createControl() {
		// 模型ID
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.width = 60;
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText(I18nUtil.getMessage("MODEL_ID") + ":");
		idText = new Text(composite, SWT.BORDER);
		FormData fd_idText = new FormData();
		fd_idText.right = new FormAttachment(100, -10);
		fd_idText.width = 100;
		fd_idText.top = new FormAttachment(lblNewLabel, -3, SWT.TOP);
		fd_idText.left = new FormAttachment(lblNewLabel, 6);
		idText.setLayoutData(fd_idText);
		
		// 模型名称
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.width = 60;
		fd_lblNewLabel_2.left = new FormAttachment(0, 0);
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		lblNewLabel_2.setText(I18nUtil.getMessage("MODEL_NAME") + ":");
		nameText = new Text(composite, SWT.BORDER);
		fd_lblNewLabel_2.top = new FormAttachment(nameText, 3, SWT.TOP);
		FormData fd_nameText = new FormData();
		fd_nameText.top = new FormAttachment(idText, 15);
		fd_nameText.left = new FormAttachment(idText, 0, SWT.LEFT);
		fd_nameText.right = new FormAttachment(100, -10);
		nameText.setLayoutData(fd_nameText);
		
		// 备注
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.width = 60;
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText(I18nUtil.getMessage("NOTE") + ":");
		noteText = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		fd_lblNewLabel_1.top = new FormAttachment(noteText, 3, SWT.TOP);
		fd_lblNewLabel_1.right = new FormAttachment(noteText, -6);
		FormData fd_noteText = new FormData();
		fd_noteText.top = new FormAttachment(nameText, 15);
		fd_noteText.height = 100;
		fd_noteText.left = new FormAttachment(idText, 0, SWT.LEFT);
		fd_noteText.right = new FormAttachment(100, -10);
		noteText.setLayoutData(fd_noteText);
	}
	
	/**
	 * 检查数据的正确性
	 */
	private boolean checkData() {
		String str = idText.getText().trim();
		if(str.isEmpty()) {
			// ID不能为空！
			setErrorMessage(I18nUtil.getMessage("ID_NOT_EMPTY"));
			return false;
		}
		
		PackageModel packageModel = null;
		
		// 检查该文件下是否还有相同ID的物理图形模型
		if(DmConstants.COMMAND_ADD.equals(flag) ) {
			if(packageModelTreeContent == null || !(packageModelTreeContent.getObj() instanceof PackageModel)) {
				// 包模型为空，无法校验数据
				setErrorMessage(I18nUtil.getMessage("PACKAGEMODEL_IS_EMPTY_CAN_NOT_CHECK"));
				return false;
			}
				
			packageModel = (PackageModel) packageModelTreeContent
						.getObj();
			
			if(PhysicalDiagramModelFactory.getPhysicalDiagramModel(FileModel.getFileModelFromObj(packageModel), str) != null) {
					setErrorMessage(I18nUtil.getMessage("PHYSICALDIAGRAMMODEL_NAME_IS_ALREADY"));
					return false;
			}
		}
		
		str = nameText.getText().trim();
		if("".equals(str)) {
			setErrorMessage(I18nUtil.getMessage("NAME_NOT_EMPTY"));
			return false;
		}
		
		setErrorMessage(null);
		setMessage(I18nUtil.getMessage("PHYSICALDIAGRAMMODEL_ATTRI"));
		
		return true;
	}
	
	@Override
	protected void okPressed() {
		if(!checkData()) {
			return ;
		}
		
		UpdatePhysicalDiagramCommand command = new UpdatePhysicalDiagramCommand();
		command.setFlag(flag);
		if(DmConstants.COMMAND_ADD.equals(flag) && packageModelTreeContent != null) {
			PhysicalDiagramModel diagramModel = new PhysicalDiagramModel();
			diagramModel.setId(idText.getText().trim());
			diagramModel.setName(nameText.getText().trim());
			diagramModel.setNote(noteText.getText());
			diagramModel.setPackageModel((PackageModel) packageModelTreeContent.getObj());
			
			command.setPhysicalDiagramModel(diagramModel);
			command.setPhysicalDataTreeContent(packageModelTreeContent);
			
			CommandStack commandStack = DefaultViewPart.getFileCommandFromObj(FileModel
					.getFileModelFromObj(diagramModel));
			if(commandStack != null) {
				commandStack.execute(command);
				// 刷新树上的文件节点
				DefaultViewPart.refreshFileModelTreeContent();
			}
			
		} else if(DmConstants.COMMAND_MODIFY.equals(flag) && physicalDiagramTreeContent != null) {
			command.setNewName(nameText.getText());
			command.setNewNote(noteText.getText());
			command.setPhysicalDiagramTreeContent(physicalDiagramTreeContent);
			
			CommandStack commandStack = DefaultViewPart.getFileCommandFromObj(FileModel
					.getFileModelFromObj(physicalDiagramTreeContent.getObj()));
			if(commandStack != null) {
				commandStack.execute(command);
				// 刷新树上的文件节点
				DatabaseTreeViewPart.refreshFileModelTreeContent();
			}
		}
		
		
		super.okPressed();
	}
	
	public void setFlag(String flag) {
		this.flag = flag;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("CANCEL"), false);
	}

	public void setPackageModelTreeContent(TreeContent packageModelTreeContent) {
		this.packageModelTreeContent = packageModelTreeContent;
	}

	public void setPhysicalDiagramTreeContent(TreeContent physicalDiagramTreeContent) {
		this.physicalDiagramTreeContent = physicalDiagramTreeContent;
	}
}
