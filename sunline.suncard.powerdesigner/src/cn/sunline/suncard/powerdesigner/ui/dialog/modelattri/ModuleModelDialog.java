/* 文件名：     ModuleModelDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-7
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog.modelattri;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.dom4j.DocumentException;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.powerdesigner.command.UpdateModuleModelCommand;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.xml.ModuleXmlModel;
import cn.sunline.suncard.powerdesigner.models.ModulesNodeModel;
import cn.sunline.suncard.powerdesigner.provider.ModuleXmlModelLabelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.powerdesigner.xml.ModuleDataXmlManager;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;

/**
 * 模块模型对话框
 * @author  Manzhizhen
 * @version 1.0, 2012-12-7
 * @see 
 * @since 1.0
 */
public class ModuleModelDialog extends TitleAreaDialog{
	private Composite composite;
	private CCombo idCombo;
	private Text nameText;
	private Text noteText;
	
	private String flag;
	private TreeContent modulesNodeModelTreeContent;
	private ModuleModel moduleModel;
	private ProductTreeViewPart productTreeViewPart;
	private ComboViewer idComboViewer;
	private Log logger = LogManager.getLogger(ModuleModelDialog.class.getName());

	public ModuleModelDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		// 模块模型对话框
		newShell.setText(I18nUtil.getMessage("MODULEMODEL_DIALOG"));
		setDefaultImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.MODULE_LABEL_16));
		super.configureShell(newShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(550, 358);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		// 模块模型属性
		setTitle(I18nUtil.getMessage("MODULEMODEL_ATTRI"));
		setMessage(I18nUtil.getMessage("MODULEMODEL_ATTRI"));
		setTitleImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.MODULE_LABEL_64));
		
		Control control = super.createDialogArea(parent);
		
		composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new FormLayout());
		
		createControl();
		initControlValue();
		createEvent();
		
		return control;
	}

	private void createControl() {
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.width = 60;
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("模块ID:");
		
		idCombo = new CCombo(composite, SWT.BORDER | SWT.READ_ONLY);
		FormData fd_idText = new FormData();
		fd_idText.top = new FormAttachment(lblNewLabel, -3, SWT.TOP);
		fd_idText.left = new FormAttachment(lblNewLabel, 6);
		fd_idText.right = new FormAttachment(100, -10);
		idCombo.setLayoutData(fd_idText);
		idComboViewer = new ComboViewer(idCombo);
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_lblNewLabel_1.width = 60;
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("模块名称:");
		
		nameText = new Text(composite, SWT.BORDER);
		fd_lblNewLabel_1.top = new FormAttachment(nameText, 3, SWT.TOP);
		FormData fd_nameText = new FormData();
		fd_nameText.right = new FormAttachment(100, -10);
		fd_nameText.top = new FormAttachment(idCombo, 11);
		fd_nameText.left = new FormAttachment(idCombo, 0, SWT.LEFT);
		nameText.setLayoutData(fd_nameText);
		
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_lblNewLabel_2.width = 60;
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		lblNewLabel_2.setText("备注:");
		
		noteText = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		fd_lblNewLabel_2.top = new FormAttachment(noteText, 3, SWT.TOP);
		FormData fd_noteText = new FormData();
		fd_noteText.height = 100;
		fd_noteText.right = new FormAttachment(100, -10);
		fd_noteText.top = new FormAttachment(nameText, 11);
		fd_noteText.left = new FormAttachment(idCombo, 0, SWT.LEFT);
		noteText.setLayoutData(fd_noteText);
	}
	
	private void createEvent() {
		idComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				comboSelectionChange();
			}
		});
	}

	/**
	 * 下拉列表改变时的方法
	 */
	protected void comboSelectionChange() {
		IStructuredSelection select = (IStructuredSelection) idComboViewer.getSelection();
		if(!select.isEmpty()) {
			ModuleXmlModel moduleXmlModel = (ModuleXmlModel) select.getFirstElement();
			nameText.setText(moduleXmlModel.getName() == null ? "" : moduleXmlModel.getName());
			noteText.setText(moduleXmlModel.getNote() == null ? "" : moduleXmlModel.getNote());
		}
	}

	private void initControlValue() {
		idComboViewer.setContentProvider(new ArrayContentProvider());
		idComboViewer.setLabelProvider(new ModuleXmlModelLabelProvider());
		
		if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			if(moduleModel == null) {
				logger.error("传入的ModuleModel为空，无法初始化ModuleModelDialog！");
				setErrorMessage("传入的ModuleModel为空，无法初始化ModuleModelDialog！");
				return ;
			}
			
			idCombo.setText(moduleModel.getId() == null ? "" : moduleModel.getId());
			nameText.setText(moduleModel.getName() == null ? "" : moduleModel.getName());
			noteText.setText(moduleModel.getNote() == null ? "" : moduleModel.getNote());
			
			idCombo.setEnabled(false);
			
		} else if(DmConstants.COMMAND_ADD.equals(flag)) {
			try {
				List<ModuleXmlModel> moduleXmlModels = ModuleDataXmlManager.getAllModuleXmlModel();
				if(moduleXmlModels != null) {
					// 先除去在该产品下已经使用过的模块ID
					ProductModel productModel = null;

					if(DmConstants.COMMAND_ADD.equals(flag) ) {
						if(modulesNodeModelTreeContent == null || !(modulesNodeModelTreeContent.getObj() instanceof ModulesNodeModel)) {
							// 产品模型为空，无法初始化数据!
							setErrorMessage(I18nUtil.getMessage("PRODUCTMODEL_IS_EMPTY_CAN_NOT_INIT"));
							return ;
						}
						
						productModel = ((ModulesNodeModel) modulesNodeModelTreeContent.getObj()).getProductModel();
						
						// 先将该产品下面所有用过的模块标签找出来
						Set<ModuleModel> moduleModelSet = productModel.getModuleModelSet();
						List<String> alreadyUsedIds = new ArrayList<String>();
						for(ModuleModel moduleModel : moduleModelSet) {
							alreadyUsedIds.add(moduleModel.getId());
						}
						 
						// 先将需要删除的模块数据对象找出来
						List<ModuleXmlModel> needDelModuleXmlModelList = new ArrayList<ModuleXmlModel>();
						for(ModuleXmlModel moduleXmlModel: moduleXmlModels) {
							if(alreadyUsedIds.contains(moduleXmlModel.getId())) {
								needDelModuleXmlModelList.add(moduleXmlModel);
							}
						}
						moduleXmlModels.removeAll(needDelModuleXmlModelList);
						
						idComboViewer.setInput(moduleXmlModels);
					}
					
					
				}
			} catch (DocumentException e) {
				logger.error("从ModuleDataConfig.xml文件中获取数据失败！");
				setErrorMessage("从ModuleDataConfig.xml文件中获取数据失败！");
				e.printStackTrace();
			}
		}
	}

	/**
	 * 检查数据的正确性
	 */
	private boolean checkData() {
		String str = null;
		
		// 模块ID不能为空！
		if(DmConstants.COMMAND_ADD.equals(flag)) {
			IStructuredSelection select = (IStructuredSelection) idComboViewer.getSelection();
			if(select.isEmpty()) {
				setErrorMessage("模块ID不能为空！");
				return false;
			}
		} else if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			str = idCombo.getText().trim();
			if(str.isEmpty()) {
				// ID不能为空！
				setErrorMessage(I18nUtil.getMessage("ID_NOT_EMPTY"));
				return false;
			}
		}
		
		str = nameText.getText().trim();
		if("".equals(str)) {
			setErrorMessage(I18nUtil.getMessage("NAME_NOT_EMPTY"));
			return false;
		}
		
		setErrorMessage(null);
		setMessage(I18nUtil.getMessage("MODULEMODEL_ATTRI"));
		
		return true;
	}
	
	@Override
	protected void okPressed() {
		if(!checkData()) {
			return ;
		}
		
		UpdateModuleModelCommand command = new UpdateModuleModelCommand();
		command.setFlag(flag);
		command.setProductTreeViewPart(productTreeViewPart);
		if(DmConstants.COMMAND_ADD.equals(flag)) {
			ModuleModel newModuleModel = new ModuleModel();
			
			IStructuredSelection select = (IStructuredSelection) idComboViewer.getSelection();
			newModuleModel.setId(((ModuleXmlModel)select.getFirstElement()).getId());
			newModuleModel.setName(nameText.getText().trim());
			newModuleModel.setNote(noteText.getText().trim());
			newModuleModel.setProductModel((ProductModel) modulesNodeModelTreeContent.getParent().getObj());
			
			command.setModulesNodeModelTreeContent(modulesNodeModelTreeContent);
			command.setModuleModel(newModuleModel);
			
			CommandStack commandStack = DefaultViewPart.getFileCommandFromObj(FileModel
					.getFileModelFromObj(((ProductModel) modulesNodeModelTreeContent.getParent().getObj()).getPhysicalDataModel()));
			if(commandStack != null) {
				commandStack.execute(command);

			}
			
		} else if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			command.setNewName(nameText.getText().trim());
			command.setNewNote(noteText.getText().trim());
			command.setModuleModel(moduleModel);
			
			CommandStack commandStack = DefaultViewPart.getFileCommandFromObj(FileModel
					.getFileModelFromObj(moduleModel.getProductModel().getPhysicalDataModel()));
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

	public void setModuleModel(ModuleModel moduleModel) {
		this.moduleModel = moduleModel;
	}

	public void setModulesNodeModelTreeContent(TreeContent modulesNodeModelTreeContent) {
		this.modulesNodeModelTreeContent = modulesNodeModelTreeContent;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setProductTreeViewPart(ProductTreeViewPart productTreeViewPart) {
		this.productTreeViewPart = productTreeViewPart;
	}
	
	

}
