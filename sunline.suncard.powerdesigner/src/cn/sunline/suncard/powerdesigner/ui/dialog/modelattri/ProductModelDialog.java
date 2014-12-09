/* 文件名：     ProductModelDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-23
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog.modelattri;

import java.util.List;
import java.util.Set;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.powerdesigner.command.UpdateProductModelCommand;
import cn.sunline.suncard.powerdesigner.manager.ProductSpaceManager;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.ProductSpaceModel;
import cn.sunline.suncard.powerdesigner.provider.PhysicalDataLabelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
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

/**
 * 产品模型对话框
 * @author  Manzhizhen
 * @version 1.0, 2012-11-23
 * @see 
 * @since 1.0
 */
public class ProductModelDialog extends TitleAreaDialog{

	private Composite composite;
	private Text idText;
	private Text nameText;
	private Text noteText;
	private Combo physicalDataCombo;
	private ComboViewer physicalDataComboViewer;

	private String flag;
	private TreeContent fileModelTreeContent;
	private FileModel fileModel;
	private ProductModel productModel;		// 需要修改的产品模型
	private ProductTreeViewPart productTreeViewPart;
	
	private Log logger = LogManager.getLogger(ProductModelDialog.class.getName());
	
	
	/**
	 * @param parentShell
	 */
	public ProductModelDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		// 产品模型对话框
		newShell.setText(I18nUtil.getMessage("PRODUCT_MODEL_DIALOG"));
		setDefaultImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.PRODUCT_IMAGE_16));
		super.configureShell(newShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(615, 345);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		// 产品模型属性
		setTitle(I18nUtil.getMessage("PRODUCTMODEL_ATTRI"));
		setMessage(I18nUtil.getMessage("PRODUCTMODEL_ATTRI"));
		setTitleImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.PRODUCT_IMAGE_64));
		
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
	
	private boolean checkData() {
		if(fileModel == null) {
			setErrorMessage("传入的FileModel为空，无法校验数据！");
			logger.error("传入的FileModel为空，无法校验化数据！");
			return false;
		}
		
		String str = idText.getText().trim();
		if(str.isEmpty()) {
			setErrorMessage("产品ID不能为空！");
			logger.error("产品ID不能为空！");
			return false;
		}
		
		// 一个文件模型中，产品ID必须唯一
		if(DmConstants.COMMAND_ADD.equals(flag)) {
			List<ProductModel> productModelList = ProductSpaceManager.getProductModelList(fileModel);
			if(productModelList == null){
				setErrorMessage("该文件模型还未添加的产品空间中，校验数据失败！");
				logger.error("该文件模型还未添加的产品空间中，校验数据失败！");
				return false;
			}
			
			for(ProductModel productModel : productModelList) {
				if(str.equals(productModel.getId())){
					setErrorMessage("该产品ID已被使用，请重新输入！");
					logger.error("该产品ID已被使用，请重新输入！");
					return false;
				}
			}
		}
		
		str = nameText.getText().trim();
		if(str.isEmpty()) {
			setErrorMessage("产品名称不能为空！");
			logger.error("产品名称不能为空！");
			return false;
		}
		
		IStructuredSelection select = (IStructuredSelection) physicalDataComboViewer.getSelection();
		if(select.isEmpty()) {
			setErrorMessage("所选择的物理数据模型不能为空！");
			logger.error("所选择的物理数据模型不能为空！");
			return false;
		}
		
		setErrorMessage(null);
		setMessage(I18nUtil.getMessage("PRODUCTMODEL_ATTRI"));
		return true;
	}

	private void initControlValue() {
		if(fileModel == null) {
			setErrorMessage("传入的FileModel为空，无法初始化数据！");
			logger.error("传入的FileModel为空，无法初始化数据！");
		
			return ;
		}
		
		physicalDataComboViewer.setContentProvider(new ArrayContentProvider());
		physicalDataComboViewer.setLabelProvider(new PhysicalDataLabelProvider());
		
		// 获取该文件模型下的所有物理数据模型，因为一个产品必须建立在一个物理数据模型下
		Set<PhysicalDataModel> physicalDataModelSet = fileModel.getPhysicalDataSet();
		physicalDataComboViewer.setInput(physicalDataModelSet);
		
		if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			if(productModel == null) {
				setErrorMessage("传入的ProductModel为空，无法初始化数据！");
				logger.error("传入的ProductModel为空，无法初始化数据！");
			
				return ;
			}
			
			
			// 如果用户是来修改产品模型
			if(DmConstants.COMMAND_MODIFY.equals(flag)) {
				idText.setText(productModel.getId() == null ? "" : productModel.getId());
				idText.setEnabled(false);
				
				nameText.setText(productModel.getName() == null ? "" : productModel.getName());
				noteText.setText(productModel.getNote() == null ? "" : productModel.getNote());
			
				physicalDataComboViewer.setSelection(new StructuredSelection(productModel.getPhysicalDataModel()));
				physicalDataComboViewer.getCombo().setEnabled(false);
			}
		}
		
	}
	
	private void createControl() {
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.width = 60;
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 5);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("产品ID:");
		
		idText = new Text(composite, SWT.BORDER);
		FormData fd_idText = new FormData();
		fd_idText.right = new FormAttachment(50);
		fd_idText.top = new FormAttachment(lblNewLabel, -3, SWT.TOP);
		fd_idText.left = new FormAttachment(lblNewLabel, 6);
		idText.setLayoutData(fd_idText);
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.top = new FormAttachment(lblNewLabel, 0, SWT.TOP);
		fd_lblNewLabel_1.left = new FormAttachment(idText, 16);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("物理数据模型:");
		
		physicalDataCombo = new Combo(composite, SWT.READ_ONLY);
		FormData fd_physicalDataCombo = new FormData();
		fd_physicalDataCombo.right = new FormAttachment(100, -10);
		fd_physicalDataCombo.top = new FormAttachment(lblNewLabel, -3, SWT.TOP);
		fd_physicalDataCombo.left = new FormAttachment(lblNewLabel_1, 6);
		physicalDataCombo.setLayoutData(fd_physicalDataCombo);
		physicalDataComboViewer = new ComboViewer(physicalDataCombo);
		
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_lblNewLabel_2.width = 60;
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		lblNewLabel_2.setText("产品名称:");
		
		nameText = new Text(composite, SWT.BORDER);
		fd_lblNewLabel_2.top = new FormAttachment(nameText, 3, SWT.TOP);
		FormData fd_nameText = new FormData();
		fd_nameText.right = new FormAttachment(100, -10);
		fd_nameText.top = new FormAttachment(idText, 11);
		fd_nameText.left = new FormAttachment(idText, 0, SWT.LEFT);
		nameText.setLayoutData(fd_nameText);
		
		Label lblNewLabel_3 = new Label(composite, SWT.NONE);
		lblNewLabel_3.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_3 = new FormData();
		fd_lblNewLabel_3.width = 60;
		fd_lblNewLabel_3.top = new FormAttachment(lblNewLabel_2, 18);
		fd_lblNewLabel_3.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		lblNewLabel_3.setLayoutData(fd_lblNewLabel_3);
		lblNewLabel_3.setText("产品备注:");
		
		noteText = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		FormData fd_noteText = new FormData();
		fd_noteText.bottom = new FormAttachment(100, -10);
		fd_noteText.right = new FormAttachment(100, -10);
		fd_noteText.top = new FormAttachment(lblNewLabel_3, -3, SWT.TOP);
		fd_noteText.left = new FormAttachment(idText, 0, SWT.LEFT);
		noteText.setLayoutData(fd_noteText);
	}

	@Override
	protected void okPressed() {
		if(!checkData()) {
			return ;
		}
		
		UpdateProductModelCommand command = new UpdateProductModelCommand();
		command.setFlag(flag);
		command.setProductTreeViewPart(productTreeViewPart);
		if(DmConstants.COMMAND_ADD.equals(flag)) {
			ProductModel newProductModel = new ProductModel();
			newProductModel.setId(idText.getText().trim());
			newProductModel.setName(nameText.getText().trim());
			newProductModel.setNote(noteText.getText());
			
			IStructuredSelection select = (IStructuredSelection) physicalDataComboViewer
					.getSelection();
			newProductModel.setPhysicalDataModel((PhysicalDataModel) select.getFirstElement());
			
			command.setProductModel(newProductModel);
			command.setFileModelTreeContent(fileModelTreeContent);
			
			CommandStack commandStack = DefaultViewPart.getFileCommandFromObj(
					fileModelTreeContent.getObj());
			if(commandStack != null) {
				commandStack.execute(command);
			}
			
		} else if(DmConstants.COMMAND_MODIFY.equals(flag)){
			IStructuredSelection select = (IStructuredSelection) physicalDataComboViewer
					.getSelection();
			productModel.setPhysicalDataModel((PhysicalDataModel) select.getFirstElement());
			
			command.setFlag(DmConstants.COMMAND_MODIFY);
			command.setNewName(nameText.getText().trim());
			command.setNewNote(noteText.getText().trim());
			command.setProductModel(productModel);
			
			CommandStack commandStack = DefaultViewPart.getFileCommandFromObj(
					productModel.getPhysicalDataModel());
			if(commandStack != null) {
				commandStack.execute(command);
				
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
	
	public void setProductModel(ProductModel productModel) {
		this.productModel = productModel;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setFileModelTreeContent(TreeContent fileModelTreeContent) {
		this.fileModelTreeContent = fileModelTreeContent;
		if(fileModelTreeContent != null) {
			fileModel = (FileModel) fileModelTreeContent.getObj();
		}
	}

	public void setProductTreeViewPart(ProductTreeViewPart productTreeViewPart) {
		this.productTreeViewPart = productTreeViewPart;
	}


	
}
