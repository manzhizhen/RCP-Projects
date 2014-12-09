/* 文件名：     DatabaseGenerationProductTressPage.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-12-6
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import cn.sunline.suncard.powerdesigner.manager.ProductSpaceManager;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.ProductSpaceModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContentProvider;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeLabelProvider;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 
 * @author Agree
 * @version 1.0, 2012-12-6
 * @see
 * @since 1.0
 */
public class DatabaseGenerationProductTreePage extends WizardPage {

	private Log logger = LogManager
			.getLogger(DatabaseGenerationProductTreePage.class.getName());
	private PhysicalDiagramModel physicalDiagramModel;
	private CheckboxTreeViewer checkboxTreeViewer;
	private Composite composite;
	private Tree tree;
	private Button buttonLog;
	private Button buttonUserSet;
	private Button buttonBussinessData;
	private Button buttonSystemConfig;

	/**
	 * @param pageName
	 */
	protected DatabaseGenerationProductTreePage(String pageName,
			PhysicalDiagramModel physicalDiagramModel) {
		super(pageName);
		this.physicalDiagramModel = physicalDiagramModel;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createControl(Composite parent) {
		setTitle("选择生成内容");
		setMessage("选择生成内容");
		
		composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new FormLayout());

		checkboxTreeViewer = new CheckboxTreeViewer(composite);

		tree = checkboxTreeViewer.getTree();
		FormData fd_tree = new FormData();
		fd_tree.top = new FormAttachment(0);
		fd_tree.left = new FormAttachment(0);
		fd_tree.right = new FormAttachment(100, -10);
		tree.setLayoutData(fd_tree);

		Group group = new Group(composite, SWT.NONE);
		fd_tree.bottom = new FormAttachment(100, -61);
		FormData fd_group = new FormData();
		fd_group.top = new FormAttachment(tree, 6);
		fd_group.left = new FormAttachment(0);
		fd_group.bottom = new FormAttachment(100, -10);
		fd_group.right = new FormAttachment(0, 564);
		group.setLayoutData(fd_group);
		group.setText("表格类型");

		buttonLog = new Button(group, SWT.CHECK);
		buttonLog.setBounds(289, 18, 87, 17);
		// 日志表
		buttonLog.setText(I18nUtil.getMessage("TABLE_LOG"));
		buttonLog.setSelection(true);

		buttonUserSet = new Button(group, SWT.CHECK);
		buttonUserSet.setBounds(10, 18, 87, 17);
		// 用户配置表
		buttonUserSet.setText(I18nUtil.getMessage("TABLE_USER_SET"));
		buttonUserSet.setSelection(true);

		buttonBussinessData = new Button(group, SWT.CHECK);
		buttonBussinessData.setBounds(103, 18, 87, 17);
		// 业务数据表
		buttonBussinessData
				.setText(I18nUtil.getMessage("TABLE_BUSSINESS_DATA"));
		buttonBussinessData.setSelection(true);

		buttonSystemConfig = new Button(group, SWT.CHECK);
		buttonSystemConfig.setBounds(196, 18, 87, 17);
		// 系统配置表
		buttonSystemConfig.setText(I18nUtil.getMessage("TABLE_SYSTEM_CONFIG"));
		buttonSystemConfig.setSelection(true);

		// 填充内容
		initControl();

		//全选
		checkboxTreeViewer.expandAll();//展开才能全选
		checkboxTreeViewer.setAllChecked(true);//这个方法只能选中展开的按钮
		
		// 创造全选方法
		createEvent();
	}

	public void initControl() {
		checkboxTreeViewer.setLabelProvider(new TreeLabelProvider());
		checkboxTreeViewer.setContentProvider(new TreeContentProvider());

		List<ProductModel> productModelList = new ArrayList<ProductModel>();

		// 需要传入
		FileModel fileModel = this.physicalDiagramModel.getPackageModel()
				.getPhysicalDataModel().getFileModel();
		ProductSpaceModel productSpaceModel = new ProductSpaceModel();
		List<ProductModel> productModels = ProductSpaceManager
				.getProductModelList(fileModel);
		// 得到所有同一物理图形模型下的产品模型
		for (ProductModel productModel : productModels) {
			if (productModel.getPhysicalDataModel() == this.physicalDiagramModel
					.getPackageModel().getPhysicalDataModel()) {
				productModelList.add(productModel);
			}
		}

		// 根据得到的产品模型给树节点赋值
		if (productModelList.isEmpty()) {
			logger.error("产品类型为空，无法创建向导");
		} else {
			List<TreeContent> productContentList = new ArrayList<TreeContent>();
			for (ProductModel productModel : productModelList) {
				TreeContent productTreeContent = new TreeContent();
				productTreeContent.setId(productModel.getId());
				productTreeContent.setObj(productModel);
				productContentList.add(productTreeContent);
				
				Set<ModuleModel>  moduleModelSet = productModel.getModuleModelSet();
				for(ModuleModel moduleModel : moduleModelSet) {
					TreeContent moduleTreeContent = new TreeContent();
					moduleTreeContent.setId(moduleModel.getId());
					moduleTreeContent.setObj(moduleModel);
					
					productTreeContent.getChildrenList().add(moduleTreeContent);
					moduleTreeContent.setParent(productTreeContent);
					
					Set<TableModel> tableModels = moduleModel.getTableModelSet();
					for(TableModel tableModel : tableModels){
						TreeContent tableTreeContent = new TreeContent();
						tableTreeContent.setId(tableModel.getId());
						tableTreeContent.setObj(tableModel);
						
						moduleTreeContent.getChildrenList().add(tableTreeContent);
						tableTreeContent.setParent(moduleTreeContent);
					}
				}
				
//				List<TreeContent> moduleList = new ArrayList<TreeContent>();
//				Set<ModuleModel> moduleModels = productModel
//						.getModuleModelSet();
//
//				if (moduleModels.isEmpty()) {
//					logger.error("模型类型为空，无法创建向导");
//
//					TreeContent productContent = new TreeContent(
//							productModel.getId(), productModel, CacheImage
//									.getCacheImage().getImage(
//											DmConstants.APPLICATION_ID,
//											IDmImageKey.PRODUCT_IMAGE_16));
//					productContentList.add(productContent);
//				} else {
//					for (ModuleModel moduleModel : moduleModels) {
//						Set<TableModel> tableModels = moduleModel
//								.getTableModelSet();
//						List<TreeContent> tableList = new ArrayList<TreeContent>();
//
//						if (tableModels.isEmpty()) {
//							logger.error("表模型为空");
//
//							TreeContent moduleContent = new TreeContent(
//									productModel.getId() + moduleModel.getId(),
//									moduleModel,
//									CacheImage.getCacheImage().getImage(
//											DmConstants.APPLICATION_ID,
//											IDmImageKey.MODULE_LABEL_16));
//							moduleContent.setParent();
//							
//							moduleList.add(moduleContent);
//						} else {
//							for (TableModel tableModel : tableModels) {
//								TreeContent tableContent = new TreeContent(
//										productModel.getId()
//												+ moduleModel.getId()
//												+ tableModel.getId(),
//										tableModel,
//										CacheImage.getCacheImage().getImage(
//												DmConstants.APPLICATION_ID,
//												IDmImageKey.TABLE_16));
//								tableList.add(tableContent);
//							}
//							TreeContent moduleContent = new TreeContent(
//									productModel.getId() + moduleModel.getId(),
//									moduleModel, tableList,
//									CacheImage.getCacheImage().getImage(
//											DmConstants.APPLICATION_ID,
//											IDmImageKey.MODULE_LABEL_16));
//							moduleList.add(moduleContent);
//						}
//
//					}
//					TreeContent productContent = new TreeContent(
//							productModel.getId(), productModel, moduleList,
//							CacheImage.getCacheImage().getImage(
//									DmConstants.APPLICATION_ID,
//									IDmImageKey.PRODUCT_IMAGE_16));
//					productContentList.add(productContent);
//				}
			}
			checkboxTreeViewer.setInput(productContentList);
		}
		
		
		setControl(composite);
		
	}

	/**
	 * 制造选择事件
	 */
	private void createEvent() {
		tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK) {
					TreeItem item = (TreeItem) event.item;
					boolean checked = item.getChecked();
					checkChildren(item.getItems(), checked);

					// 触发这个的Item的grayed = false，因为这是个CHECK事件，要么全选，要么全不选。
					checkParent(item.getParentItem(), checked, false);
				}
			}
		});
	}

	private void checkParent(TreeItem parent, boolean checked, boolean grayed) {
		if (parent == null)// 递归退出条件：父亲为空。
			return;
		for (TreeItem child : parent.getItems()) {
			if (child.getGrayed() || checked != child.getChecked()) {
				// 1，子节点有一个为【部分选中的】，直接设置父节点为【部分选中的】。
				// 2，子节点不完全相同，说明【部分选中的】。
				checked = grayed = true;
				break;
			}
		}
		parent.setChecked(checked);
		parent.setGrayed(grayed);
		checkParent(parent.getParentItem(), checked, grayed);
	}

	private void checkChildren(TreeItem[] children, boolean checked) {
		if (children.length == 0)// 递归退出条件：孩子为空。
			return;
		for (TreeItem child : children) {
			child.setGrayed(false);// 必须设置这个，因为本来节点可能【部分选中的】。
			child.setChecked(checked);
			checkChildren(child.getItems(), checked);
		}
	}

	/*
	 * 用于传输设置下个页面的
	 */
	@Override
	public IWizardPage getNextPage() {
		// TODO Auto-generated method stub
		DatabaseGenerationPreviewPage databaseGenerationPreviewPage = (DatabaseGenerationPreviewPage) super
				.getNextPage();
		if (!(databaseGenerationPreviewPage == null)) {
			// 得到所有的表
			if (this.getSelection().size() == 0) {
				return super.getNextPage();
			}

			else {
				List<TableModel> childTableModels = this.getSelection();

				if (childTableModels.size() == 0) {
					return super.getNextPage();
				}

				List<TableModel> selectedTableModels = new ArrayList<TableModel>();
				for(TableModel childTableModel : childTableModels){
					if(buttonLog.getSelection()
							&& childTableModel.getTableType().equals(SystemConstants.TABLE_TYPE_D)){
						selectedTableModels.add(childTableModel);
					}
					if(buttonBussinessData.getSelection()
							&& childTableModel.getTableType().equals(SystemConstants.TABLE_TYPE_B)){
						selectedTableModels.add(childTableModel);
					}
					if(buttonUserSet.getSelection()
							&& childTableModel.getTableType().equals(SystemConstants.TABLE_TYPE_U)){
						selectedTableModels.add(childTableModel);
					}
					if(buttonSystemConfig.getSelection()
							&& childTableModel.getTableType().equals(SystemConstants.TABLE_TYPE_S)){
						selectedTableModels.add(childTableModel);
					}
				}
				databaseGenerationPreviewPage.initControl(selectedTableModels);
			}
		}
		return super.getNextPage();
	}

	/**
	 * 得到所选择的表格
	 */
	private List<TableModel> getSelection() {
		// TODO Auto-generated method stub
		Object[] objects = checkboxTreeViewer.getCheckedElements();

		List<TableModel> tableModels = new ArrayList<TableModel>();
		for (Object object : objects) {
			// 树节点需要转换
			TreeContent treeContent = (TreeContent) object;
			Object obj = treeContent.getObj();
			if (obj instanceof TableModel) {
				tableModels.add((TableModel) obj);
			}
		}
		return tableModels;
	}
}
