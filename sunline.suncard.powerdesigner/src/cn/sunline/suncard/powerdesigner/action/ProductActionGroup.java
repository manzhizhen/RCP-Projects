/* 文件名：     ProductActionGroup.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-23
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.actions.ActionGroup;

import cn.sunline.suncard.powerdesigner.model.DocumentCategoryModel;
import cn.sunline.suncard.powerdesigner.model.DocumentModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.ProductSpaceModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.models.DocumentsNodeModel;
import cn.sunline.suncard.powerdesigner.models.ModulesNodeModel;
import cn.sunline.suncard.powerdesigner.models.SqlsNodeModel;
import cn.sunline.suncard.powerdesigner.models.StoredProceduresNodeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;

/**
 * 
 * @author  Manzhizhen
 * @version 1.0, 2012-11-23
 * @see 
 * @since 1.0
 */
public class ProductActionGroup extends ActionGroup{
	private ProductTreeViewPart productTreeViewPart;
	
	private IAction addProductAction;
	private IAction delProductAction;
	private IAction attriProductAction;
	private IAction importTableAction;
	private IAction removeTableAction;
	
	private IAction closeFileAction;
	private IAction findInDiagramAction;
	
	private IAction addModuleAction;
	private IAction delModuleAction;
	private IAction attriModuleAction;
	
	private IAction addSqlAction;
	private IAction addStoredAction;
	
	private IAction addDocumentCategoryAction;
	private IAction delDocumentCategoryAction;
	private IAction modifyDocumentCategoryAction;
	private IAction addDocumentAction;
	private IAction delDocumentAction;
	private IAction downloadDocumentAction;
	private IAction viewDocumentAction;
	
	public ProductActionGroup(ProductTreeViewPart productTreeViewPart) {
		this.productTreeViewPart = productTreeViewPart;
	}
	
	@Override
	public void fillContextMenu(IMenuManager menu) {
		initAction();	// 初始化Action
		
		MenuManager menuManager = (MenuManager) menu;
		menuManager.setRemoveAllWhenShown(true);
		
		menuManager.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				
				Object obj = getCurrentSelection();
				if(obj instanceof TreeContent) {
					TreeContent treeContent = (TreeContent) obj;
					
					if(treeContent.getObj() instanceof FileModel) {
						manager.add(addProductAction);
						manager.add(closeFileAction);
						
					} else if(treeContent.getObj() instanceof ProductModel) {
						manager.add(addProductAction);
						manager.add(delProductAction);
						manager.add(attriProductAction);
						
					} else if(treeContent.getObj() instanceof ModuleModel) {
						manager.add(importTableAction);
						manager.add(delModuleAction);
						manager.add(attriModuleAction);
						
					} else if(treeContent.getObj() instanceof ModulesNodeModel) {
						manager.add(addModuleAction);
						
					} else if(treeContent.getObj() instanceof TableModel) {
						manager.add(findInDiagramAction);
						manager.add(importTableAction);
						manager.add(removeTableAction);
						
					} else if(treeContent.getObj() instanceof SqlsNodeModel) {
						manager.add(addSqlAction);
						
					} else if(treeContent.getObj() instanceof StoredProceduresNodeModel) {
						manager.add(addStoredAction);
						
					} else if(treeContent.getObj() instanceof DocumentsNodeModel) {
						manager.add(addDocumentCategoryAction);
						
					} else if(treeContent.getObj() instanceof DocumentCategoryModel) {
						manager.add(addDocumentAction);
						manager.add(delDocumentCategoryAction);
						manager.add(modifyDocumentCategoryAction);
						
					} else if(treeContent.getObj() instanceof DocumentModel) {
						manager.add(viewDocumentAction);
						manager.add(downloadDocumentAction);
						manager.add(delDocumentAction);
						
					}
				}
				
			}
		});
		
		Tree tree = productTreeViewPart.getTreeViewer().getTree();
		Menu treeMenu = menuManager.createContextMenu(tree);
		tree.setMenu(treeMenu);
		
		super.fillContextMenu(menu);
		
	}
	
	/**
	 * 初始化Action
	 */
	private void initAction() {
		addProductAction = new ProductAction(DmConstants.ADD_PRODUCT_MODEL, 
				productTreeViewPart);
		delProductAction = new ProductAction(DmConstants.DEL_PRODUCT_MODEL, 
				productTreeViewPart);
		attriProductAction = new ProductAction(DmConstants.MODIFY_PRODUCT_MODEL, 
				productTreeViewPart);
		importTableAction = new ProductAction(DmConstants.IMPORT_TABLE_MODEL, 
				productTreeViewPart);
		removeTableAction = new ProductAction(DmConstants.REMOVE_TABLE_MODEL, 
				productTreeViewPart);
		addModuleAction = new ProductAction(DmConstants.ADD_MODULE_MODEL, 
				productTreeViewPart);
		delModuleAction = new ProductAction(DmConstants.REMOVE_MODULE_MODEL, 
				productTreeViewPart);
		attriModuleAction = new ProductAction(DmConstants.ATTRI_MODULE_MODEL, 
				productTreeViewPart);
		addSqlAction = new ProductAction(DmConstants.MANAGE_SQL_MODEL, 
				productTreeViewPart);
		addStoredAction = new ProductAction(DmConstants.MANAGE_STORED_MODEL, 
				productTreeViewPart);
		
		closeFileAction = new DatabaseAction(DmConstants.CLOSE_FILE_MODEL, 
				productTreeViewPart.getTreeViewer());
		findInDiagramAction = new DatabaseAction(DmConstants.FIND_IN_DIAGRAM, 
				productTreeViewPart.getTreeViewer());
		
		addDocumentCategoryAction = new ProductAction(DmConstants.ADD_DOC_CATEGORY, 
				productTreeViewPart);
		delDocumentCategoryAction = new ProductAction(DmConstants.DEL_DOC_CATEGORY, 
				productTreeViewPart);
		modifyDocumentCategoryAction = new ProductAction(DmConstants.MODIFY_DOC_CATEGORY, 
				productTreeViewPart);
		addDocumentAction = new ProductAction(DmConstants.ADD_DOC, 
				productTreeViewPart);
		delDocumentAction = new ProductAction(DmConstants.DEL_DOC, 
				productTreeViewPart);
		downloadDocumentAction = new ProductAction(DmConstants.DOWNLOAD_DOC, 
				productTreeViewPart);
		viewDocumentAction = new ProductAction(DmConstants.VIEW_DOC, 
				productTreeViewPart);
	}
	
	private TreeContent getCurrentSelection() {
		return productTreeViewPart.getSelection();
	}

}
