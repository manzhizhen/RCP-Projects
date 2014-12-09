/* 文件名：     ProjectActionGroup.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-29
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.actions.ActionGroup;

import cn.sunline.suncard.powerdesigner.model.DocumentCategoryModel;
import cn.sunline.suncard.powerdesigner.model.DocumentModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.ProjectGroupModel;
import cn.sunline.suncard.powerdesigner.model.ProjectModel;
import cn.sunline.suncard.powerdesigner.model.ProjectSpaceModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.models.DocumentsNodeModel;
import cn.sunline.suncard.powerdesigner.models.ModulesNodeModel;
import cn.sunline.suncard.powerdesigner.models.SqlsNodeModel;
import cn.sunline.suncard.powerdesigner.models.StoredProceduresNodeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.tree.ProjectTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;

/**
 * 项目树的ActionGroup
 * @author  Manzhizhen
 * @version 1.0, 2012-12-29
 * @see 
 * @since 1.0
 */
public class ProjectActionGroup extends ActionGroup{
	private ProjectTreeViewPart projectTreeViewPart;
	
	private IAction addProjectGroupAction;
	private IAction delProjectGroupAction;
	private IAction attriProjectGroupAction;
	private IAction addProjectAction;
	private IAction openProjectAction;
	private IAction closeProjectAction;
	private IAction attriProjectAction;
	private IAction importModuleAction;
	private IAction synchronousModuleAction;
	private IAction attriTableModelAction;
	
	private IAction addDocumentCategoryAction;
	private IAction delDocumentCategoryAction;
	private IAction modifyDocumentCategoryAction;
	private IAction addDocumentAction;
	private IAction delDocumentAction;
	private IAction downloadDocumentAction;
	private IAction viewDocumentAction;
	
	public ProjectActionGroup(ProjectTreeViewPart projectTreeViewPart) {
		this.projectTreeViewPart = projectTreeViewPart;
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
					Object dataObject = treeContent.getObj();
					
					if(dataObject instanceof ProjectSpaceModel) {
						manager.add(addProjectGroupAction);
						manager.add(openProjectAction);
						
					} else if(dataObject instanceof ProjectGroupModel) {
						manager.add(addProjectAction);
						manager.add(delProjectGroupAction);
						manager.add(attriProjectGroupAction);
						
					} else if(dataObject instanceof ProjectModel) {
						manager.add(closeProjectAction);
						manager.add(attriProjectAction);
						
					} else if(dataObject instanceof ModulesNodeModel) {
						ModulesNodeModel modulesNodeModel = (ModulesNodeModel) treeContent.getObj();
						ProjectModel projectModel = (ProjectModel) modulesNodeModel.getProductModel();
						if(!projectModel.isAlreadyImport()) {
							manager.add(importModuleAction);
						}
						manager.add(synchronousModuleAction);
						
					} else if(dataObject instanceof TableModel) {
						manager.add(attriTableModelAction);
						
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
		
		Tree tree = projectTreeViewPart.getTreeViewer().getTree();
		Menu treeMenu = menuManager.createContextMenu(tree);
		tree.setMenu(treeMenu);
		
		super.fillContextMenu(menu);
		
	}
	
	/**
	 * 初始化Action
	 */
	private void initAction() {
		addProjectGroupAction = new ProjectAction(DmConstants.ADD_PROJECT_GROUP_MODEL, 
				projectTreeViewPart);
		delProjectGroupAction = new ProjectAction(DmConstants.CLOSE_PROJECT_GROUP_MODEL, 
				projectTreeViewPart);
		attriProjectGroupAction = new ProjectAction(DmConstants.MODIFY_PROJECT_GROUP_MODEL, 
				projectTreeViewPart);
		addProjectAction = new ProjectAction(DmConstants.ADD_PROJECT_MODEL, 
				projectTreeViewPart);
		openProjectAction = new ProjectAction(DmConstants.OPEN_PROJECT_MODEL, 
				projectTreeViewPart);
		closeProjectAction = new ProjectAction(DmConstants.CLOSE_PROJECT_MODEL, 
				projectTreeViewPart);
		attriProjectAction = new ProjectAction(DmConstants.ATTRI_PROJECT_MODEL, 
				projectTreeViewPart);
		importModuleAction = new ProjectAction(DmConstants.IMPORT_MODULE_MODEL, 
				projectTreeViewPart);
		synchronousModuleAction = new ProjectAction(DmConstants.SYNCHRONOUS_MODULE_MODEL, 
				projectTreeViewPart);
		attriTableModelAction = new ProjectAction(DmConstants.ATTRI_TABLE_MODEL, 
				projectTreeViewPart);
	
		addDocumentCategoryAction = new ProjectAction(DmConstants.ADD_DOC_CATEGORY, 
				projectTreeViewPart);
		delDocumentCategoryAction = new ProjectAction(DmConstants.DEL_DOC_CATEGORY, 
				projectTreeViewPart);
		modifyDocumentCategoryAction = new ProjectAction(DmConstants.MODIFY_DOC_CATEGORY, 
				projectTreeViewPart);
		addDocumentAction = new ProjectAction(DmConstants.ADD_DOC, 
				projectTreeViewPart);
		delDocumentAction = new ProjectAction(DmConstants.DEL_DOC, 
				projectTreeViewPart);
		downloadDocumentAction = new ProjectAction(DmConstants.DOWNLOAD_DOC, 
				projectTreeViewPart);
		viewDocumentAction = new ProjectAction(DmConstants.VIEW_DOC, 
				projectTreeViewPart);
	
	}
	
	private TreeContent getCurrentSelection() {
		return projectTreeViewPart.getSelection();
	}
	
	
}
