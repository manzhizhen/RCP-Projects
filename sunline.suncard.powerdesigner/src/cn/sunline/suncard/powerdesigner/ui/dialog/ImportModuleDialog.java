/* 文件名：     ImportModuleDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-3
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.powerdesigner.command.ImportModuleCommand;
import cn.sunline.suncard.powerdesigner.command.UpdateDefualtColumnCommand;
import cn.sunline.suncard.powerdesigner.manager.ProductSpaceManager;
import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.ProductSpaceModel;
import cn.sunline.suncard.powerdesigner.model.ProjectModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.models.ModulesNodeModel;
import cn.sunline.suncard.powerdesigner.provider.PhysicalDataLabelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.tree.ProjectDefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProjectTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContentProvider;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeLabelProvider;
import cn.sunline.suncard.powerdesigner.ui.dialog.composite.ModuleCheckboxTreeComposite;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Combo;

/**
 * 给新建的项目模型第一导入模块模型时调用的对话框
 * @author  Manzhizhen
 * @version 1.0, 2013-1-3
 * @see 
 * @since 1.0
 */
public class ImportModuleDialog extends TitleAreaDialog{
	private Map<PhysicalDataModel, Set<ProductModel>> productModelMap;
	public ProjectTreeViewPart projectTreeViewPart;
	public TreeContent modulesNodeTreeContent;
	
	private Composite composite;
	private Tree checkboxTree;
	private CheckboxTreeViewer checkboxTreeViewer;

	private Log logger = LogManager.getLogger(ImportModuleDialog.class
			.getName());
	private Combo physicalDataCombo;
	private ComboViewer comboViewer;
	
	public ImportModuleDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		// 导入模块对话框
		newShell.setText(I18nUtil.getMessage("IMPORT_MODULEMODEL_DIALOG"));
		setDefaultImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.MODULE_LABEL_16));
		super.configureShell(newShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(611, 544);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		// 导入模块
		setTitle(I18nUtil.getMessage("IMPORT_MODULEMODEL"));
		setMessage(I18nUtil.getMessage("IMPORT_MODULEMODEL"));
		setTitleImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.MODULE_LABEL_64));
		
		Control control =   super.createDialogArea(parent);
		
		composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new FormLayout());
		
		createControl();
		initControlValue();
		createEvent();
		
		return control;
	}

	private void createControl() {
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.top = new FormAttachment(0, 10);
		fd_lblNewLabel_1.left = new FormAttachment(0, 10);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("物理数据模型:");
		
		physicalDataCombo = new Combo(composite, SWT.READ_ONLY);
		FormData fd_physicalDataCombo = new FormData();
		fd_physicalDataCombo.right = new FormAttachment(50);
		fd_physicalDataCombo.top = new FormAttachment(lblNewLabel_1, -3, SWT.TOP);
		fd_physicalDataCombo.left = new FormAttachment(lblNewLabel_1, 6);
		physicalDataCombo.setLayoutData(fd_physicalDataCombo);
		comboViewer = new ComboViewer(physicalDataCombo);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(physicalDataCombo, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("请选择需要导入的模块:");
		
		checkboxTreeViewer = new CheckboxTreeViewer(composite, SWT.BORDER | SWT.CHECK);
		checkboxTree = checkboxTreeViewer.getTree();
		FormData fd_checkboxTree = new FormData();
		fd_checkboxTree.bottom = new FormAttachment(100, -10);
		fd_checkboxTree.right = new FormAttachment(100, -10);
		fd_checkboxTree.top = new FormAttachment(lblNewLabel, 6);
		fd_checkboxTree.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		checkboxTree.setLayoutData(fd_checkboxTree);
	}

	private void initControlValue() {
		if(modulesNodeTreeContent == null || !(modulesNodeTreeContent.getObj() instanceof ModulesNodeModel)) {
			setErrorMessage("传入的数据不完整，初始化数据失败!");
			return ;
		}
		
		ModulesNodeModel modulesNodeModel = (ModulesNodeModel) modulesNodeTreeContent.getObj();
		ProjectModel projectModel = (ProjectModel) modulesNodeModel.getProductModel();
		
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new PhysicalDataLabelProvider());
		checkboxTreeViewer.setContentProvider(new TreeContentProvider());
		checkboxTreeViewer.setLabelProvider(new TreeLabelProvider());
		
		// 找到产品空间中所用到的物理数据模型
		productModelMap = new HashMap<PhysicalDataModel, Set<ProductModel>>();
		Set<ProductModel> productModels = ProductSpaceManager.getAllProductModels();
		for(ProductModel productModel : productModels) {
			if(productModel.getPhysicalDataModel().getDatabaseTypeModel()
					.equals(projectModel.getDatabaseTypeModel())) {
				
				if(productModelMap.get(productModel.getPhysicalDataModel()) != null 
						&& productModel.getPhysicalDataModel().getDatabaseTypeModel()
						.equals(projectModel.getDatabaseTypeModel())) {
					productModelMap.get(productModel.getPhysicalDataModel()).add(productModel);
				} else {
					Set<ProductModel> productModelSet = new HashSet<ProductModel>();
					productModelSet.add(productModel);
					productModelMap.put(productModel.getPhysicalDataModel(), productModelSet);
				}
			}
		}
		
		comboViewer.setInput(productModelMap.keySet());
	}

	private void createEvent() {
		checkboxTreeViewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				refreshCheckboxTreeChecked(event.getElement());
			}
		});
		
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				refreshCheckboxTree();
			}
		});
	}
	
	/**
	 * 通过不同的物理数据模型来展现旗下的产品模型
	 */
	protected void refreshCheckboxTree() {
		IStructuredSelection select = (IStructuredSelection) comboViewer.getSelection();
		if(select.isEmpty()) {
			return ;
		}
		
		PhysicalDataModel physicalDataModel = (PhysicalDataModel) select.getFirstElement();
		
		List<TreeContent> productModelTreeContentList = new ArrayList<TreeContent>();
		Set<ProductModel> productModelList = productModelMap.get(physicalDataModel);
		for(ProductModel productModel : productModelList) {
			TreeContent productTreeContent = new TreeContent();
			productTreeContent.setId(productModel.getId());
			productTreeContent.setObj(productModel);
			productModelTreeContentList.add(productTreeContent);
			
			// 添加产品下面的模块
			Set<ModuleModel> moduleModelSet = productModel.getModuleModelSet();
			for(ModuleModel moduleModel : moduleModelSet) {
				TreeContent moduleTreeContent = new TreeContent();
				moduleTreeContent.setId(productTreeContent.getId() + moduleModel.getId());
				moduleTreeContent.setObj(moduleModel);
				
				productTreeContent.getChildrenList().add(moduleTreeContent);
				moduleTreeContent.setParent(productTreeContent);

				// 在模块下添加表格
				Set<TableModel> tableModels = moduleModel.getTableModelSet();
				for(TableModel tableModel : tableModels) {
					TreeContent tableTreeContent = new TreeContent();
					tableTreeContent.setId(moduleTreeContent.getId() + tableModel.getId());
					tableTreeContent.setObj(tableModel);
					
					tableTreeContent.setParent(moduleTreeContent);
					moduleTreeContent.getChildrenList().add(tableTreeContent);
				}
			}
			
			checkboxTreeViewer.setInput(productModelTreeContentList);
			
		}
		
	}

	/**
	 * 刷新模块树的勾选状态
	 */
	protected void refreshCheckboxTreeChecked(Object selectedObj) {
		if(selectedObj instanceof TreeContent) {
			TreeContent selectTreeContent = (TreeContent) selectedObj;
			Object dataObj = selectTreeContent.getObj();
			boolean checked = checkboxTreeViewer.getChecked(selectTreeContent);
			
			// 如果是产品模型的checkbox改变，则需要改变其下的子节点的状态
			if(dataObj instanceof ProductModel) {
				List<TreeContent> moduleTreeContentList = selectTreeContent.getChildrenList();
				for(TreeContent moduleTreeContent : moduleTreeContentList) {
					checkboxTreeViewer.setChecked(moduleTreeContent, checked);
					
					List<TreeContent> tableTreeContentList = moduleTreeContent.getChildrenList();
					for(TreeContent tableTreeContent : tableTreeContentList) {
						checkboxTreeViewer.setChecked(tableTreeContent, checked);
					}
				}
				
			} else if(dataObj instanceof ModuleModel) {
				int num = 0;
				List<TreeContent> moduleTreeContentList = selectTreeContent.getParent().getChildrenList();
				for(TreeContent moduleTreeContent : moduleTreeContentList) {
					if(checkboxTreeViewer.getChecked(moduleTreeContent)) {
						num++;
					}
				}
				
				if(num == moduleTreeContentList.size()) {
					checkboxTreeViewer.setChecked(selectTreeContent.getParent(), true);
				} else {
					checkboxTreeViewer.setChecked(selectTreeContent.getParent(), false);
				}
				
				List<TreeContent> tableTreeContentList = selectTreeContent.getChildrenList();
				for(TreeContent tableTreeContent : tableTreeContentList) {
					checkboxTreeViewer.setChecked(tableTreeContent, checked);
				}
				
			}  else if(dataObj instanceof TableModel) {
				int num = 0; 
				List<TreeContent> tableTreeContentList = selectTreeContent.getParent().getChildrenList();
				for(TreeContent tableTreeContent : tableTreeContentList) {
					if(checkboxTreeViewer.getChecked(tableTreeContent)) {
						num++;
					}
				}
				
				if(num == tableTreeContentList.size()) {
					checkboxTreeViewer.setChecked(selectTreeContent.getParent(), true);
				} else {
					checkboxTreeViewer.setChecked(selectTreeContent.getParent(), false);
				}
				
				// 看是否要勾选产品树节点
				num = 0;
				List<TreeContent> moduleTreeContentList = selectTreeContent.getParent().getParent().getChildrenList();
				for(TreeContent moduleTreeContent : moduleTreeContentList) {
					if(checkboxTreeViewer.getChecked(moduleTreeContent)) {
						num++;
					}
				}
				
				if(num == moduleTreeContentList.size()) {
					checkboxTreeViewer.setChecked(selectTreeContent.getParent().getParent(), true);
				} else {
					checkboxTreeViewer.setChecked(selectTreeContent.getParent().getParent(), false);
				}
			}
		}
		
	}
	
	/**
	 * 获得树上所选择的表格的树节点
	 * @return
	 */
	public List<TreeContent> getCheckedTableElements() {
		List<TreeContent> tableTreeContentList = new ArrayList<TreeContent>();
		Object[] objs = checkboxTreeViewer.getCheckedElements();
		for(Object obj : objs) {
			if(obj instanceof TreeContent) {
				TreeContent treeContent = (TreeContent) obj;
				if(treeContent.getObj() instanceof TableModel) {
					tableTreeContentList.add(treeContent);
				}
			}
		}
		
		return tableTreeContentList;
	}
	
	private boolean checkData() {
		IStructuredSelection select = (IStructuredSelection) comboViewer.getSelection();
		if(select.isEmpty()) {
			setErrorMessage("请选择一个物理数据模型！");
			return false;
		}
		
		List<TreeContent> treeContentList = getCheckedTableElements();
		if(treeContentList == null || treeContentList.isEmpty()) {
			setErrorMessage("请至少选择一个需要导入的表格！");
			return false;
		}
		
		setErrorMessage(null);
		setMessage(I18nUtil.getMessage("IMPORT_MODULEMODEL"));
		return true;
	}
	
	@Override
	protected void okPressed() {
		if(!checkData()) {
			return ;
		}
		
		ImportModuleCommand command = new ImportModuleCommand();
		command.setTableTreeContentList(getCheckedTableElements());
		command.setProjectTreeViewPart(projectTreeViewPart);
		command.setModulesNodeTreeContent(modulesNodeTreeContent);
		
		CommandStack commandStack = ProjectDefaultViewPart.getCommandStackFromProjectModel(
				(ProjectModel) modulesNodeTreeContent.getParent().getObj());
		
		if(commandStack != null) {
			commandStack.execute(command);
		} else {
			logger.error("获得的CommandStack为空，无法导入给项目树模块模型！");
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

	public void setProjectTreeViewPart(ProjectTreeViewPart projectTreeViewPart) {
		this.projectTreeViewPart = projectTreeViewPart;
	}

	public void setModulesNodeTreeContent(TreeContent modulesNodeTreeContent) {
		this.modulesNodeTreeContent = modulesNodeTreeContent;
	}
}
