/* 文件名：     ProjectTreeViewPart.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-25
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.tree;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.action.DatabaseAction;
import cn.sunline.suncard.powerdesigner.action.ProjectAction;
import cn.sunline.suncard.powerdesigner.action.ProjectActionGroup;
import cn.sunline.suncard.powerdesigner.exception.CanNotFoundFolderFromWorkSpaceException;
import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditorInput;
import cn.sunline.suncard.powerdesigner.gef.xml.LineXmlPropertyFactory;
import cn.sunline.suncard.powerdesigner.handler.OpenHandler;
import cn.sunline.suncard.powerdesigner.manager.ProjectSpaceManager;
import cn.sunline.suncard.powerdesigner.model.DocumentCategoryModel;
import cn.sunline.suncard.powerdesigner.model.DocumentModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.ProjectGroupModel;
import cn.sunline.suncard.powerdesigner.model.ProjectModel;
import cn.sunline.suncard.powerdesigner.model.ProductSpaceModel;
import cn.sunline.suncard.powerdesigner.model.ProjectSpaceModel;
import cn.sunline.suncard.powerdesigner.model.SqlScriptModel;
import cn.sunline.suncard.powerdesigner.model.StoredProcedureModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.WorkSpaceModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.models.CodesNodeModel;
import cn.sunline.suncard.powerdesigner.models.DocumentsNodeModel;
import cn.sunline.suncard.powerdesigner.models.ModulesNodeModel;
import cn.sunline.suncard.powerdesigner.models.SqlsNodeModel;
import cn.sunline.suncard.powerdesigner.models.StoredProceduresNodeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeViewComposite;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 项目树
 * @author  Manzhizhen
 * @version 1.0, 2012-12-25
 * @see 
 * @since 1.0
 */
public class ProjectTreeViewPart extends ProjectDefaultViewPart{
	public final static String ID = "cn.sunline.suncard.powerdesigner.tree.ProjectTreeViewPart";
	private static Log logger = LogManager.getLogger(ProjectTreeViewPart.class
			.getName());
	private Tree tree;
	private TreeViewer treeViewer;

	private TreeViewComposite treeViewComposite;
	
	private static Object SEMAPHORE = new Object();
	private static ProjectTreeViewPart instance;
	
	/**
	 * @deprecated
	 */
	public ProjectTreeViewPart() {
	}
	
	/**
	 * 单例
	 * @return
	 * @throws PartInitException 
	 */
	public static ProjectTreeViewPart getInstance() throws PartInitException {
		synchronized (SEMAPHORE) {
			if (instance == null) {
				instance = (ProjectTreeViewPart) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().findView(ProjectTreeViewPart.ID);
				
				if(instance == null) {
					instance = (ProjectTreeViewPart) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().showView(ProjectTreeViewPart.ID);
				}
			}
		}
		
		return instance;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		treeViewComposite = new TreeViewComposite(parent, true);

		tree = treeViewComposite.getTree();
		treeViewer = treeViewComposite.getTreeViewer();

		// 创建树的内容
		try {
			createContent();
			
			createEvent();
			
			// 创建树的右键菜单
			createTreeMenu();
			
		} catch (CanNotFoundNodeIDException e) {
			logger.error("创建ProjectTreeViewPart失败！" + e.getMessage());
			e.printStackTrace();
		} catch (FoundTreeNodeNotUniqueException e) {
			logger.error("创建ProjectTreeViewPart失败！" + e.getMessage());
			e.printStackTrace();
		}

		// createEvent();
	}
	
	/**
	 * 创建树的内容
	 * 
	 * @throws FoundTreeNodeNotUniqueException
	 * @throws CanNotFoundNodeIDException
	 */
	public void createContent() throws CanNotFoundNodeIDException,
			FoundTreeNodeNotUniqueException {
		Image image = CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.PROJECT_SPACE_IMAGE);

		// 添加一棵树
		treeViewComposite.createTreeRootNode(DmConstants.PROJECT_SPACE_TREE_ID,
				new ProjectSpaceModel(), image);

	}
	
	/**
	 * 创建菜单
	 */
	private void createTreeMenu() {
		ProjectActionGroup projectActionGroup = new ProjectActionGroup(this);
		projectActionGroup.fillContextMenu(new MenuManager());
	}
	
	@Override
	public void setFocus() {
		if(tree != null) {
			tree.setFocus();
		}
	}

	private void createEvent() {
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				mouseDoubleEvent();
			}
		});
		
		//Ctrl+S来保存某个文件
		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.stateMask & SWT.CTRL) != 0 && e.keyCode == (int)'s') {
					TreeContent treeContent = getSelection();
					if(treeContent != null ) {
						if(treeContent.getObj() instanceof ProjectModel) {
							saveProjectModel((ProjectModel) treeContent.getObj());
						}
						
					}
				} else if((e.stateMask & SWT.CTRL) != 0 && e.keyCode == (int)'o') {
					try {
						new OpenHandler().execute(null);
					} catch (ExecutionException e1) {
						logger.debug("打开文件失败！" + e1.getMessage());
						MessageDialog.openError(getSite().getShell()
								, I18nUtil.getMessage("MESSAGE"), "打开文件失败！" + e1.getMessage());
						e1.printStackTrace();
					}
				}
			}
		});
	}
	
	/**
	 * 鼠标双击事件
	 * 如果是物理图形模型，则打开相应的Editor
	 */
	public void mouseDoubleEvent() {
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		if(selection.size() == 1) {
			Object obj = selection.getFirstElement();
			if(obj instanceof TreeContent) {
				TreeContent treeContent = (TreeContent) obj;
				Object selObj = treeContent.getObj();
				if(selObj instanceof TableModel) {
					ProjectAction projectAction = new ProjectAction(DmConstants.ATTRI_TABLE_MODEL, this);
					projectAction.run();
				}
			}
		}
	}
	
	/**
	 * 获得所选择的树节点
	 * 
	 * @return
	 */
	public TreeContent getSelection() {
		StructuredSelection selection = (StructuredSelection) treeViewer
				.getSelection();
		TreeContent treeContent = (TreeContent) selection.getFirstElement();

		return treeContent;
	}
	
	/**
	 * 添加一个项目群模型节点
	 * @param projectGroupModel
	 * @param parentTreeContent
	 * @return
	 * @throws CanNotFoundNodeIDException 
	 * @throws FoundTreeNodeNotUniqueException 
	 * @throws IOException 
	 * @throws PartInitException 
	 */
	public TreeContent addProjectGroupModel(ProjectGroupModel projectGroupModel
			, TreeContent parentTreeContent) throws FoundTreeNodeNotUniqueException, CanNotFoundNodeIDException, PartInitException, IOException {
		if(projectGroupModel == null || parentTreeContent == null || !(parentTreeContent
				.getObj() instanceof ProjectSpaceModel)) {
			logger.error("传入的ProjectGroupModel为空或父树节点不正确，无法在ProjectTreeViewPart上添加该项目群！");
			return null;
		}
		
		if(ProjectSpaceModel.getProjectGroupModelMap().keySet().contains(projectGroupModel.getId())) {
			logger.error("ProjectGroupModel在ProjectSpaceModel中已经存在，添加项目群失败！");
			return null;
		}
		
		// 将该项目群添加到项目空间
		ProjectSpaceModel.getProjectGroupModelMap().put(projectGroupModel.getId(), projectGroupModel);
		
		// 添加项目群节点
		TreeContent projectModelContent = treeViewComposite.addNode(
				parentTreeContent.getId(),
				parentTreeContent.getId() + DmConstants.SEPARATOR
						+ projectGroupModel.getId(),
						projectGroupModel,
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID,
						IDmImageKey.PROJECT_GROUP_IMAGE_16));
		
		// 添加项目群下面的项目节点
		if(projectModelContent != null) {
			LinkedHashSet<ProjectModel> projectModelSet = projectGroupModel.getProjectModelSet();
			for(ProjectModel projectModel : projectModelSet) {
				addProjectModel(projectModel, projectModelContent);
			}
		}
		
		return projectModelContent;
	}
	
	/**
	 * 添加一个项目
	 * 
	 * @throws CanNotFoundNodeIDException
	 * @throws FoundTreeNodeNotUniqueException
	 * @throws IOException 
	 * @throws PartInitException 
	 * @throws AddFileModelToWorkSpaceModelFail
	 */
	public TreeContent addProjectModel(ProjectModel projectModel,
			TreeContent parentTreeContent) throws CanNotFoundNodeIDException,
			FoundTreeNodeNotUniqueException, PartInitException, IOException {
		if (projectModel == null
				|| !(parentTreeContent.getObj() instanceof ProjectGroupModel)) {
			logger.error("传入的ProjectModel为空或父树节点不正确，无法在ProjectTreeViewPart上添加该项目！");
			return null;
		}

		ProjectGroupModel projectGroupModel = (ProjectGroupModel) parentTreeContent.getObj();

		// 在项目群模型中添加此项目模型
		projectGroupModel.getProjectModelSet().add(projectModel);
		projectModel.setProjectGroupModel(projectGroupModel);
		
		// 给该项目模型匹配默认的CommandStack
		addProjectCommandStack(projectModel);
		
		// 添加项目节点
		TreeContent projectModelContent = treeViewComposite.addNode(
				parentTreeContent.getId(),
				parentTreeContent.getId() + DmConstants.SEPARATOR
						+ projectModel.getId(),
				projectModel,
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID,
						IDmImageKey.PROJECT_16));

		// 在项目模型下添加一个功能模块集合节点
		String id = projectModelContent.getId() + DmConstants.SEPARATOR + DmConstants.MODULE_NODE_ID_TAIL;
		ModulesNodeModel modulesNodeModel = new ModulesNodeModel();
		modulesNodeModel.setProductModel(projectModel);
		TreeContent modulesTreeContent = treeViewComposite.addNode(projectModelContent.getId(), id, 
				modulesNodeModel, 
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, IDmImageKey.MODULES_16));
		// 在功能模块节点下添加模块节点
		Set<ModuleModel> moduleModelSet = projectModel.getModuleModelSet();
		for(ModuleModel moduleModel : moduleModelSet) {
			ProductTreeViewPart.addModuleModel(moduleModel, modulesTreeContent, treeViewComposite);
		}
		
		// 在项目模型下添加一个SQL脚本集合节点
		id = projectModelContent.getId() + DmConstants.SEPARATOR + DmConstants.SQLS_NODE_ID_TAIL;
		TreeContent sqlsTreeContent = treeViewComposite.addNode(projectModelContent.getId(), id, 
				new SqlsNodeModel(), 
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, IDmImageKey.SQLS_16));
		// 在功能模块节点下添加模块节点
		Set<SqlScriptModel> sqlModelSet = projectModel.getSqlSet();
		for(SqlScriptModel sqlScriptModel : sqlModelSet) {
			ProductTreeViewPart.addSqlScriptModel(sqlScriptModel, sqlsTreeContent, treeViewComposite);
		}
		
		
		// 在项目模型下添加一个储存过程集合节点
		id = projectModelContent.getId() + DmConstants.SEPARATOR + DmConstants.STOREDS_NODE_ID_TAIL;
		TreeContent storedsTreeContent = treeViewComposite.addNode(projectModelContent.getId(), id, 
				new StoredProceduresNodeModel(), 
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, IDmImageKey.STOREDS_16));
		
		// 在功能模块节点下添加存储过程
		Set<StoredProcedureModel> storedModelSet = projectModel.getStoredProcedureSet();
		for(StoredProcedureModel storedProcedureModel : storedModelSet) {
			ProductTreeViewPart.addStoredModel(storedProcedureModel, storedsTreeContent, treeViewComposite);
		}
		
		// 在项目模型下添加一个程序代码集合节点
		id = projectModelContent.getId() + DmConstants.SEPARATOR + DmConstants.CODES_NODE_ID_TAIL;
		treeViewComposite.addNode(projectModelContent.getId(), id, 
				new CodesNodeModel(), 
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, IDmImageKey.CODES_16));
		
		// 在项目模型下添加一个文档集合节点
		DocumentsNodeModel documentsNodeModel = new DocumentsNodeModel();
		documentsNodeModel.setParentModel(projectModel);
		id = projectModelContent.getId() + DmConstants.SEPARATOR + DmConstants.DOCS_NODE_ID_TAIL;
		TreeContent docsModelTreeContent = treeViewComposite.addNode(projectModelContent.getId(), id, 
				documentsNodeModel, 
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, IDmImageKey.DOCS_16));
		// 从目录中加载文档类别及其文档
		File projectDocFile = new File(SystemConstants.PROJECTSPACEPATH + File.separator 
				+ projectModel.getFolderName() + File.separator + SystemConstants.ZIP_FILE_DOC + File.separator);
		if(projectDocFile.isDirectory()) {
			// 列出该目录下的所有文档类别文件夹
			for(File docCategoryFile : projectDocFile.listFiles()) {
				if(docCategoryFile.isDirectory()) {
					// 添加该文档类别节点
					DocumentCategoryModel docCategoryModel = new DocumentCategoryModel();
					docCategoryModel.setName(docCategoryFile.getName());
					TreeContent docCategoryTreeContent = ProductTreeViewPart.addDocCategoryModel(docCategoryModel, docsModelTreeContent);
					
					// 在文档类别节点下添加文档节点
					for(File documentFile : docCategoryFile.listFiles()) {
						if(documentFile.isFile()) {
							DocumentModel documentModel = new DocumentModel();
							documentModel.setFileName(documentFile.getName());
							ProductTreeViewPart.addDocumentModel(documentModel, docCategoryTreeContent);
						}
					}
				}
			}
		}
		
		return projectModelContent;

	}
	
	/**
	 * 关闭一个项目文件
	 * @throws IOException 
	 * @throws CanNotFoundFolderFromWorkSpaceException 
	 * @throws CanNotFoundNodeIDException 
	 * @throws FoundTreeNodeNotUniqueException 
	 */
	public void closeProjectModel(TreeContent projectModelTreeContent) throws 
		FoundTreeNodeNotUniqueException, CanNotFoundNodeIDException  {
		
		if(projectModelTreeContent == null || !(projectModelTreeContent.getObj() instanceof ProjectModel)) {
			logger.error("传入的树节点新不完整，无法关闭该文件！");
			return;
		}
		
		ProjectModel projectModel = (ProjectModel) projectModelTreeContent.getObj();
		
		// 把该项目节点从项目空间移除
		projectModel.getProjectGroupModel().getProjectModelSet().remove(projectModel);
		ProjectDefaultViewPart.removeProjectCommandStack(projectModel);
		
		// 移除项目节点
		TreeContent fileContent = treeViewComposite.removeNode(projectModelTreeContent.getId());
	}
	
	/**
	 * 关闭一个项目群
	 * @throws IOException 
	 * @throws CanNotFoundFolderFromWorkSpaceException 
	 * @throws CanNotFoundNodeIDException 
	 * @throws FoundTreeNodeNotUniqueException 
	 */
	public void closeProjectGroupModel(TreeContent projectGroupModelTreeContent) throws 
		FoundTreeNodeNotUniqueException, CanNotFoundNodeIDException  {
		
		if(projectGroupModelTreeContent == null || !(projectGroupModelTreeContent
				.getObj() instanceof ProjectGroupModel)) {
			logger.error("传入的树节点新不完整，无法关闭该项目群！");
			return;
		}
		
		ProjectGroupModel projectGroupModel = (ProjectGroupModel) projectGroupModelTreeContent.getObj();
		
		LinkedHashSet<ProjectModel> projectModelSet = projectGroupModel.getProjectModelSet();
		
		for(ProjectModel projectModel : projectModelSet) {
			// 把该项目节点从项目空间移除
			ProjectSpaceModel.getProjectGroupModelMap().remove(projectModel);
			ProjectDefaultViewPart.removeProjectCommandStack(projectModel);
		}
		
		ProjectSpaceModel.getProjectGroupModelMap().remove(projectGroupModel.getId());
		
		// 删除项目群树节点
		treeViewComposite.removeNode(projectGroupModelTreeContent.getId());
	}
	
	/**
	 * 通过ProjectGroupModel的id获取其对应的树节点
	 * @param id
	 * @return
	 */
	public TreeContent getProjectGroupTreeContentFromId(String id) {
		TreeContent rootTreeContent = treeViewComposite.getRootContentList().get(0);
		
		// 获取所有的项目群树节点
		List<TreeContent> projectGroupTreeContentList = rootTreeContent.getChildrenList();
		for(TreeContent treeContent : projectGroupTreeContentList) {
			ProjectGroupModel projectGroupModel = (ProjectGroupModel) treeContent.getObj();
			if(projectGroupModel.getId().equals(id)) {
				return treeContent;
			}
		}
		
		logger.info("找不到对应该ID的项目群树节点:" + id);
		return null;
		
	}
	
	/**
	 * 通过ProjectModel的id获取其对应的树节点
	 * @param id
	 * @return
	 */
	public TreeContent getProjectTreeContentFromId(String id) {
		TreeContent rootTreeContent = treeViewComposite.getRootContentList().get(0);
		
		// 获取所有的项目群树节点
		List<TreeContent> projectGroupTreeContentList = rootTreeContent.getChildrenList();
		for(TreeContent treeContent : projectGroupTreeContentList) {
			for(TreeContent projectTreeContent : treeContent.getChildrenList()) {
				ProjectModel projectModel = (ProjectModel) projectTreeContent.getObj();
				if(id.equals(projectModel.getId())) {
					return projectTreeContent;
				}
			}
		}
		
		logger.info("找不到对应该ID的项目树节点:" + id);
		return null;
		
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}
	
	/**
	 * 刷新树
	 */
	public void refreshTree() {
		treeViewer.refresh();
	}

	public TreeViewComposite getTreeViewComposite() {
		return treeViewComposite;
	}
}
