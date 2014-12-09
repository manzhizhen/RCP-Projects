/* 文件名：     DatabaseActionGroup.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-5
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

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.PackageModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.TableShortcutModel;
import cn.sunline.suncard.powerdesigner.model.WorkSpaceModel;
import cn.sunline.suncard.powerdesigner.models.DomainsNodeModel;
import cn.sunline.suncard.powerdesigner.models.TablesNodeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;

/**
 * 数据库树的ActionGroup
 * @author  Manzhizhen
 * @version 1.0, 2012-9-5
 * @see 
 * @since 1.0
 */
public class DatabaseActionGroup extends ActionGroup{
	private TreeViewer treeViewer;
	
	private IAction addPhysicalDataAction;
	private IAction addPhysicalDiagramAction;
	private IAction openPhysicalDiagramAction;
	private IAction attriPhysicalDataAction;
	private IAction attriPhysicalDiagramAction;
	private IAction delPhysicalDataAction;
	private IAction delPhysicalDiagramAction;
	private IAction findInDiagramAction;
	private IAction attriTableModelAction;
	private IAction delTableModelAction;
	private IAction closeFileAction;
	private IAction newFileAction;
	private IAction openFileAction;
	private IAction defaultColumnAction;
//	private IAction editSqlAction;
//	private IAction editStoredProcedureAction;
	private IAction addColumnAction;
	private IAction editColumnAction;
	private IAction delColumnAction;
	private IAction initTableDataAction;
	private IAction addPackageAction;
	private IAction delPackageAction;
	private IAction attriPackageAction;
	private IAction importPdmAction;	
	private IAction indexAction;
	
	private IAction importDefaultColumnAction;
	private IAction importDomainsAction;
	
	private IAction pasteTableAction;
	
	
	
	public DatabaseActionGroup(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
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
					
					if(treeContent.getObj() instanceof WorkSpaceModel ) {
						manager.add(newFileAction);
						manager.add(openFileAction);
						manager.add(importPdmAction);
						
					} else if(treeContent.getObj() instanceof FileModel ) {
						manager.add(addPhysicalDataAction);
						manager.add(closeFileAction);
						
					} else if(treeContent.getObj() instanceof PhysicalDataModel){
//						manager.add(addPhysicalDiagramAction);
						manager.add(addPackageAction);
						manager.add(importDefaultColumnAction);
						manager.add(importDomainsAction);	
						manager.add(pasteTableAction);	
//						manager.add(defaultColumnAction);
//						manager.add(editSqlAction);
//						manager.add(editStoredProcedureAction);
						manager.add(delPhysicalDataAction);
						manager.add(attriPhysicalDataAction);
						
					} else if(treeContent.getObj() instanceof PackageModel) {
						manager.add(addPhysicalDiagramAction);
						manager.add(delPackageAction);
						manager.add(attriPackageAction);
						
					} else if(treeContent.getObj() instanceof PhysicalDiagramModel) {
						manager.add(openPhysicalDiagramAction);
						manager.add(delPhysicalDiagramAction);
						manager.add(pasteTableAction);
						manager.add(attriPhysicalDiagramAction);
						
					} else if(treeContent.getObj() instanceof TablesNodeModel 
							|| treeContent.getParent().getObj() instanceof PhysicalDiagramModel) {
						manager.add(pasteTableAction);
						
					} else if(treeContent.getObj() instanceof TableModel) {
						manager.add(findInDiagramAction);
//						manager.add(initTableDataAction);
						manager.add(indexAction);
						manager.add(delTableModelAction);
						manager.add(attriTableModelAction);
						
					} else if(treeContent.getObj() instanceof TableShortcutModel) {
						manager.add(findInDiagramAction);
						manager.add(delTableModelAction);
						manager.add(attriTableModelAction);
						
					} else if(treeContent.getObj() instanceof DomainsNodeModel) {
						manager.add(addColumnAction);
						
					} else if(treeContent.getObj() instanceof ColumnModel) {
						manager.add(addColumnAction);
						manager.add(delColumnAction);
						manager.add(editColumnAction);
					}
				}
				
			}
		});
		
		Tree tree = treeViewer.getTree();
		Menu treeMenu = menuManager.createContextMenu(tree);
		tree.setMenu(treeMenu);
		
		super.fillContextMenu(menu);
	}

	/**
	 * 初始化Action
	 */
	private void initAction() {
		addPhysicalDataAction = new DatabaseAction(DmConstants.ADD_PHYSICAL_DATA_FLAG, 
				treeViewer);
		addPhysicalDiagramAction = new DatabaseAction(DmConstants.ADD_PHYSICAL_DIAGRAM_FLAG, 
				treeViewer);
		openPhysicalDiagramAction = new DatabaseAction(DmConstants.OPEN_PHYSICAL_DIAGRAM_FLAG, 
				treeViewer);
		attriPhysicalDataAction = new DatabaseAction(DmConstants.ATTRI_PHYSICAL_DATA_FLAG, 
				treeViewer);
		attriPhysicalDiagramAction = new DatabaseAction(DmConstants.ATTRI_PHYSICAL_DIAGRAM_FLAG, 
				treeViewer);
		delPhysicalDataAction = new DatabaseAction(DmConstants.DEL_PHYSICAL_DATA_FLAG, 
				treeViewer);
		delPhysicalDiagramAction = new DatabaseAction(DmConstants.DEL_PHYSICAL_DIAGRAM_FLAG, 
				treeViewer);
		findInDiagramAction = new DatabaseAction(DmConstants.FIND_IN_DIAGRAM, 
				treeViewer);
		attriTableModelAction = new DatabaseAction(DmConstants.ATTRI_TABLE_MODEL, 
				treeViewer);
		delTableModelAction = new DatabaseAction(DmConstants.DEL_TABLE_MODEL, 
				treeViewer);
		closeFileAction = new DatabaseAction(DmConstants.CLOSE_FILE_MODEL, 
				treeViewer);
		newFileAction = new DatabaseAction(DmConstants.NEW_FILE_MODEL, 
				treeViewer);
		openFileAction = new DatabaseAction(DmConstants.OPEN_FILE_MODEL, 
				treeViewer);
		defaultColumnAction = new DatabaseAction(DmConstants.DEFAULT_COLUMN_FLAG, 
				treeViewer);
//		editSqlAction = new DatabaseAction(DmConstants.EDIT_SQL_FLAG, 
//				treeViewer);
//		editStoredProcedureAction = new DatabaseAction(DmConstants.EDIT_STOREDPROCEDURE_FLAG, 
//				treeViewer);
		
		addColumnAction = new DatabaseAction(DmConstants.ADD_DOMAINS_COLUMN_MODEL, 
				treeViewer);
		delColumnAction = new DatabaseAction(DmConstants.DEL_COLUMN_MODEL, 
				treeViewer);
		editColumnAction = new DatabaseAction(DmConstants.MODIFY_COLUMN_MODEL, 
				treeViewer);
		initTableDataAction = new DatabaseAction(DmConstants.INIT_TABLE_DATA, 
				treeViewer);
		
		importPdmAction = new DatabaseAction(DmConstants.IMPORT_PDM_FLAG, 
				treeViewer);
		addPackageAction = new DatabaseAction(DmConstants.ADD_PACKAGE_MODEL, 
				treeViewer);
		delPackageAction = new DatabaseAction(DmConstants.DEL_PACKAGE_MODEL, 
				treeViewer);
		attriPackageAction = new DatabaseAction(DmConstants.ATTRI_PACKAGE_MODEL, 
				treeViewer);
		
		indexAction = new DatabaseAction(DmConstants.TABLE_INDEX_FLAG, 
				treeViewer);
		
		importDefaultColumnAction = new DatabaseAction(DmConstants.IMPORT_DEFAULT_COLUMN_FLAG, 
				treeViewer);
		importDomainsAction = new DatabaseAction(DmConstants.IMPORT_DOMAINS_FLAG, 
				treeViewer);
		
		pasteTableAction = new DatabaseAction(DmConstants.PASTE_TABLE_FLAG, 
				treeViewer);
		
	}
	
	private Object getCurrentSelection() {
		IStructuredSelection iSelection = (IStructuredSelection) treeViewer.getSelection();
		Object element = iSelection.getFirstElement();
		return element;
	}

}
