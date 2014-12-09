/* 文件名：     ModuleCheckboxTreeComposite.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-3
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.tree.factory.CheckboxComposite;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContentProvider;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeLabelProvider;

/**
 * 带复选框的模块树的Composite
 * @author  Manzhizhen
 * @version 1.0, 2013-1-3
 * @see 
 * @since 1.0
 */
public class ModuleCheckboxTreeComposite extends Composite implements ISubComposite{
	private CheckboxComposite checkboxComposite;
	private TableModel tableModel;
	private List<ProductModel> productModelList;


	public ModuleCheckboxTreeComposite(Composite parent, int style) {
		super(parent, style);
		createControl();
	}
	
	@Override
	public void initControlData() {
		List<TreeContent> productTreeContentList = new ArrayList<TreeContent>();
		List<TreeContent> needCheckedTreeContentList = new ArrayList<TreeContent>(); // 需要被选中的树节点
		
		for(ProductModel productModel : productModelList) {
			// 添加产品
			TreeContent productTreeContent = new TreeContent();
			productTreeContent.setId(productModel.getId());
			productTreeContent.setObj(productModel);
			
			productTreeContentList.add(productTreeContent);
			
			// 添加产品下面的模块
			Set<ModuleModel> moduleModelSet = productModel.getModuleModelSet();
			for(ModuleModel moduleModel : moduleModelSet) {
				TreeContent moduleTreeContent = new TreeContent();
				moduleTreeContent.setId(productTreeContent.getId() + moduleModel.getId());
				moduleTreeContent.setObj(moduleModel);
				
				productTreeContent.getChildrenList().add(moduleTreeContent);
				moduleTreeContent.setParent(productTreeContent);

				if(tableModel != null) {
					// 如果该表格在该模块下 ，这该模块节点需要被选中
					if(moduleModel.getTableModelSet().contains(tableModel)) {
						needCheckedTreeContentList.add(moduleTreeContent);
					}
				}
			}
		}
		
		checkboxComposite.initControlData(productTreeContentList);
		
		// 选中需要选中的树节点
		for(TreeContent checkedTreeContent : needCheckedTreeContentList) {
			checkboxComposite.getTreeViewer().setChecked(checkedTreeContent, true);
			
			// 设置半选择状态
			if(checkedTreeContent.getObj() instanceof ModuleModel) {
				checkboxComposite.getTreeViewer().setGrayChecked(checkedTreeContent.getParent(), true);
			}
			
			// 如果一个产品模型下面的所有模块节点都被选择，则该产品模型也应该被选中
			if(needCheckedTreeContentList.containsAll(checkedTreeContent.getParent().getChildrenList())) {
				checkboxComposite.getTreeViewer().setGrayChecked(checkedTreeContent.getParent(), false);
				checkboxComposite.getTreeViewer().setChecked(checkedTreeContent.getParent(), true);
			}
		}
	}

	@Override
	public void createControlEvent() {
		checkboxComposite.createEvent();
	}

	@Override
	public void createControl() {
		setLayout(new FormLayout());
		
		checkboxComposite = new CheckboxComposite(this, SWT.NONE);
		
		FormData fd_moduleTree = new FormData();
		fd_moduleTree.bottom = new FormAttachment(100, 0);
		fd_moduleTree.right = new FormAttachment(100, 0);
		fd_moduleTree.top = new FormAttachment(0, 0);
		fd_moduleTree.left = new FormAttachment(0, 0);
		checkboxComposite.setLayoutData(fd_moduleTree);
	}
	
	public void setProductModelList(List<ProductModel> productModelList) {
		this.productModelList = productModelList;
	}

	public void setTableModel(TableModel tableModel) {
		this.tableModel = tableModel;
	}

	public Object[] getCheckedElements() {
		return checkboxComposite.getTreeViewer().getCheckedElements();
	}

	@Override
	public String checkData() {
		// TODO Auto-generated method stub
		return null;
	}

}
