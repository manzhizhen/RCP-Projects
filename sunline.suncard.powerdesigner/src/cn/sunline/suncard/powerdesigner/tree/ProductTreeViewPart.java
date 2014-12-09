/* 文件名：     ProductTreeViewPart.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-23
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.tree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import cn.sunline.suncard.powerdesigner.action.DatabaseAction;
import cn.sunline.suncard.powerdesigner.action.DatabaseActionGroup;
import cn.sunline.suncard.powerdesigner.action.ProductActionGroup;
import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.handler.OpenHandler;
import cn.sunline.suncard.powerdesigner.manager.ProductSpaceManager;
import cn.sunline.suncard.powerdesigner.model.DocumentCategoryModel;
import cn.sunline.suncard.powerdesigner.model.DocumentModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.ProductSpaceModel;
import cn.sunline.suncard.powerdesigner.model.ProjectModel;
import cn.sunline.suncard.powerdesigner.model.SqlScriptModel;
import cn.sunline.suncard.powerdesigner.model.StoredProcedureModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.WorkSpaceModel;
import cn.sunline.suncard.powerdesigner.models.CodesNodeModel;
import cn.sunline.suncard.powerdesigner.models.DocumentsNodeModel;
import cn.sunline.suncard.powerdesigner.models.ModulesNodeModel;
import cn.sunline.suncard.powerdesigner.models.SqlsNodeModel;
import cn.sunline.suncard.powerdesigner.models.StoredProceduresNodeModel;
import cn.sunline.suncard.powerdesigner.models.TablesNodeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.powerdesigner.tools.FileDeal;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeViewComposite;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 产品树
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-11-23
 * @see
 * @since 1.0
 */
public class ProductTreeViewPart extends DefaultViewPart {
	public final static String ID = "cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart";
	private Tree tree;
	private TreeViewer treeViewer;

	private static Object SEMAPHORE = new Object();
	private static ProductTreeViewPart instance;
	
	private TreeViewComposite treeViewComposite;
	private static Log logger = LogManager.getLogger(ProductTreeViewPart.class
			.getName());

	/**
	 * @deprecated
	 */
	public ProductTreeViewPart(){
	}
	
	/**
	 * 单例
	 * @return
	 * @throws PartInitException 
	 */
	public static ProductTreeViewPart getInstance() throws PartInitException {
		synchronized (SEMAPHORE) {
			if (instance == null) {
				instance = (ProductTreeViewPart) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().findView(ProductTreeViewPart.ID);
				
				if(instance == null) {
					instance = (ProductTreeViewPart) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().showView(ProductTreeViewPart.ID);
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
			logger.error("创建ProductTreeViewPart失败！" + e.getMessage());
			e.printStackTrace();
		} catch (FoundTreeNodeNotUniqueException e) {
			logger.error("创建ProductTreeViewPart失败！" + e.getMessage());
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
				DmConstants.APPLICATION_ID, IDmImageKey.PRODUCT_SPACE_IMAGE);

		// 添加一棵树
		treeViewComposite.createTreeRootNode(DmConstants.PRODUCT_SPACE_TREE_ID,
				new ProductSpaceModel(), image);
	}

	/**
	 * 创建菜单
	 */
	private void createTreeMenu() {
		ProductActionGroup productActionGroup = new ProductActionGroup(this);
		productActionGroup.fillContextMenu(new MenuManager());
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
						if(treeContent.getObj() instanceof ProductModel) {
							treeContent = treeContent.getParent();
						}
						
						saveFileModel(FileModel.getFileModelFromObj(treeContent.getObj()));
					}
					
				//Ctrl+O来打开某些对象	
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
					DatabaseAction databaseAction = new DatabaseAction(DmConstants.ATTRI_TABLE_MODEL, treeViewer);
					databaseAction.run();
				}
			}
		}
	}
	
	/**
	 * 添加一个文件
	 * 
	 * @throws CanNotFoundNodeIDException
	 * @throws FoundTreeNodeNotUniqueException
	 * @throws IOException 
	 * @throws PartInitException 
	 * @throws AddFileModelToWorkSpaceModelFail
	 */
	public TreeContent addFileModel(FileModel fileModel)
			throws CanNotFoundNodeIDException, FoundTreeNodeNotUniqueException, IOException, PartInitException {
		if (fileModel == null) {
			logger.error("传入的FileModel为空，无法在ProductTreeViewPart上添加该文件！");
			return null;
		}

		// 添加文件节点
		TreeContent fileContent = treeViewComposite.addNode(
				DmConstants.PRODUCT_SPACE_TREE_ID,
				fileModel.getFile().getAbsolutePath(),
				fileModel,
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID,
						IDmImageKey.SPD_FILE_IMAGE_16));

		// 在产品空间模型中添加此文件模型
		ProductSpaceManager.addFileModel(fileModel);

		List<ProductModel> productModelList = ProductSpaceManager
				.getProductModelList(fileModel);
		for (ProductModel productModel : productModelList) {
			TreeContent productTreeContent = addProductModel(productModel,
					fileContent, treeViewComposite);
		}

		return fileContent;
	}
	
	/**
	 * 关闭一个文件
	 * 
	 * @throws CanNotFoundNodeIDException
	 * @throws FoundTreeNodeNotUniqueException
	 * @throws AddFileModelToWorkSpaceModelFail
	 */
	public void closeFileModel(TreeContent fileModelTreeContent)
			throws CanNotFoundNodeIDException, FoundTreeNodeNotUniqueException {
		if (fileModelTreeContent == null || !(fileModelTreeContent.getObj() instanceof FileModel)) {
			logger.error("传入的文件树节点为空或不正确，无法在ProductTreeViewPart上关闭该文件！");
			return ;
		}
		
		FileModel fileModel = (FileModel) fileModelTreeContent.getObj();
		
		ProductSpaceManager.removeFileModel(fileModel);
		
		treeViewComposite.removeNode(fileModelTreeContent.getId());

	}
	
	/**
	 * 添加一个产品
	 * 
	 * @throws CanNotFoundNodeIDException
	 * @throws FoundTreeNodeNotUniqueException
	 * @throws IOException 
	 * @throws PartInitException 
	 * @throws AddFileModelToWorkSpaceModelFail
	 */
	public static TreeContent addProductModel(ProductModel productModel,
			TreeContent parentTreeContent, TreeViewComposite treeViewComposite) throws CanNotFoundNodeIDException,
			FoundTreeNodeNotUniqueException, IOException, PartInitException {
		if (productModel == null
				|| !(parentTreeContent.getObj() instanceof FileModel) || treeViewComposite == null) {
			logger.error("传入的ProductModel为空或父节点不正确，无法在ProductTreeViewPart上添加该文件！");
			return null;
		}

		FileModel fileModel = (FileModel) parentTreeContent.getObj();

		// 在产品空间模型中添加此产品模型
		List<ProductModel> productModelList = ProductSpaceManager
				.getProductModelList(fileModel);

		if (productModelList == null) {
			logger.warn("无法从产品空间获取到对应FileModel的产品列表，产品添加失败！");
			return null;
		}

		if (!productModelList.contains(productModel)) {
			productModelList.add(productModel);
		}

		// 添加产品节点
		TreeContent productModelContent = treeViewComposite.addNode(
				parentTreeContent.getId(),
				parentTreeContent.getId() + DmConstants.SEPARATOR
						+ productModel.getId(),
				productModel,
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID,
						IDmImageKey.PRODUCT_IMAGE_16));

		// 在产品模型下添加一个功能模块集合节点
		String id = productModelContent.getId() + DmConstants.SEPARATOR + DmConstants.MODULE_NODE_ID_TAIL;
		ModulesNodeModel modulesNodeModel = new ModulesNodeModel();
		modulesNodeModel.setProductModel(productModel);
		TreeContent modulesTreeContent = treeViewComposite.addNode(productModelContent.getId(), id, 
				modulesNodeModel, 
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, IDmImageKey.MODULES_16));
		// 在功能模块节点下添加模块节点
		Set<ModuleModel> moduleModelSet = productModel.getModuleModelSet();
		for(ModuleModel moduleModel : moduleModelSet) {
			addModuleModel(moduleModel, modulesTreeContent, treeViewComposite);
		}
		
		// 在产品模型下添加一个SQL脚本集合节点
		id = productModelContent.getId() + DmConstants.SEPARATOR + DmConstants.SQLS_NODE_ID_TAIL;
		TreeContent sqlsTreeContent = treeViewComposite.addNode(productModelContent.getId(), id, 
				new SqlsNodeModel(), 
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, IDmImageKey.SQLS_16));
		// 在功能模块节点下添加模块节点
		Set<SqlScriptModel> sqlModelSet = productModel.getSqlSet();
		for(SqlScriptModel sqlScriptModel : sqlModelSet) {
			addSqlScriptModel(sqlScriptModel, sqlsTreeContent, treeViewComposite);
		}
		
		
		// 在产品模型下添加一个储存过程集合节点
		id = productModelContent.getId() + DmConstants.SEPARATOR + DmConstants.STOREDS_NODE_ID_TAIL;
		TreeContent storedsTreeContent = treeViewComposite.addNode(productModelContent.getId(), id, 
				new StoredProceduresNodeModel(), 
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, IDmImageKey.STOREDS_16));
		
		// 在功能模块节点下添加存储过程
		Set<StoredProcedureModel> storedModelSet = productModel.getStoredProcedureSet();
		for(StoredProcedureModel storedProcedureModel : storedModelSet) {
			addStoredModel(storedProcedureModel, storedsTreeContent, treeViewComposite);
		}
		
		// 在产品模型下添加一个程序代码集合节点
		id = productModelContent.getId() + DmConstants.SEPARATOR + DmConstants.CODES_NODE_ID_TAIL;
		treeViewComposite.addNode(productModelContent.getId(), id, 
				new CodesNodeModel(), 
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, IDmImageKey.CODES_16));
		
		// 在产品模型下添加一个文档集合节点
		DocumentsNodeModel documentsNodeModel = new DocumentsNodeModel();
		documentsNodeModel.setParentModel(productModel);
		id = productModelContent.getId() + DmConstants.SEPARATOR + DmConstants.DOCS_NODE_ID_TAIL;
		TreeContent docsModelTreeContent = treeViewComposite.addNode(productModelContent.getId(), id, 
				documentsNodeModel, 
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, IDmImageKey.DOCS_16));
		// 从目录中加载文档类别及其文档
		File productDocFile = new File(SystemConstants.WORKSPACEPATH + File.separator 
				+ fileModel.getFolderName() + File.separator + SystemConstants.ZIP_FILE_DOC + File.separator);
		if(productDocFile.isDirectory()) {
			// 列出该目录下的所有文档类别文件夹
			for(File docCategoryFile : productDocFile.listFiles()) {
				if(docCategoryFile.isDirectory()) {
					// 添加该文档类别节点
					DocumentCategoryModel docCategoryModel = new DocumentCategoryModel();
					docCategoryModel.setName(docCategoryFile.getName());
					TreeContent docCategoryTreeContent = addDocCategoryModel(docCategoryModel, docsModelTreeContent);
					
					// 在文档类别节点下添加文档节点
					for(File documentFile : docCategoryFile.listFiles()) {
						if(documentFile.isFile()) {
							DocumentModel documentModel = new DocumentModel();
							documentModel.setFileName(documentFile.getName());
							addDocumentModel(documentModel, docCategoryTreeContent);
						}
					}
				}
			}
		}
		
		return productModelContent;

	}
	
	/**
	 * 删除一个产品
	 * 
	 * @throws CanNotFoundNodeIDException
	 * @throws FoundTreeNodeNotUniqueException
	 * @throws AddFileModelToWorkSpaceModelFail
	 */
	public void removeProductModel(TreeContent productTreeContent) throws CanNotFoundNodeIDException,
			FoundTreeNodeNotUniqueException {
		if (productTreeContent == null
				|| !(productTreeContent.getObj() instanceof ProductModel)) {
			logger.error("传入的ProductModel树节点为空或不正确，无法在ProductTreeViewPart上删除该产品！");
			return ;
		}

		ProductModel productModel = (ProductModel) productTreeContent.getObj();
		
		// 在产品空间中删除该产品
		ProductSpaceManager.removeProductModel((FileModel)productTreeContent.getParent().getObj()
				, productModel);

		// 在文件节点下删除该产品模型
		treeViewComposite.removeNode(productTreeContent.getId());

	}

	/**
	 * 添加一个模块
	 * 注意，此方法没有向模块模型的父容器添加自己，因为它可能属于不同的父容器，比如产品模型或项目模型
	 * @throws CanNotFoundNodeIDException
	 * @throws FoundTreeNodeNotUniqueException
	 * @throws AddFileModelToWorkSpaceModelFail
	 */
	public static TreeContent addModuleModel(ModuleModel moduleModel,
			TreeContent parentTreeContent, TreeViewComposite treeViewComposite) throws CanNotFoundNodeIDException,
			FoundTreeNodeNotUniqueException {
		if (moduleModel == null
				|| !(parentTreeContent.getObj() instanceof ModulesNodeModel) || treeViewComposite == null) {
			logger.error("传入的ModuleModel为空或父亲树节点不正确，无法在ProductTreeViewPart上添加该模块！");
			return null;
		}

		// 添加模块节点
		TreeContent moduleModelContent = treeViewComposite.addNode(
				parentTreeContent.getId(),
				parentTreeContent.getId() + DmConstants.SEPARATOR
						+ moduleModel.getId(),
						moduleModel,
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID,
						IDmImageKey.MODULE_LABEL_16));
		
		Set<TableModel> tableModelSet = moduleModel.getTableModelSet();
		for(TableModel tableModel : tableModelSet) {
			addTableModel(tableModel, moduleModelContent, treeViewComposite);
		}
		
		
		return moduleModelContent;
	}
	
	/**
	 * 移除一个模块
	 * 
	 * @throws CanNotFoundNodeIDException
	 * @throws FoundTreeNodeNotUniqueException
	 * @throws AddFileModelToWorkSpaceModelFail
	 */
	public void removeModuleModel(TreeContent moduleTreeContent) throws CanNotFoundNodeIDException,
			FoundTreeNodeNotUniqueException {
		if (moduleTreeContent == null
				|| !(moduleTreeContent.getObj() instanceof ModuleModel)) {
			logger.error("传入的模块模型的树节点为空或不正确，无法在ProductTreeViewPart上删除该模块！");
			return ;
		}

		ModuleModel moduleModel = (ModuleModel) moduleTreeContent.getObj();
		ProductModel productModel = (ProductModel) moduleTreeContent.getParent().getParent()
				.getObj();
		productModel.getModuleModelSet().remove(moduleModel);
		
		treeViewComposite.removeNode(moduleTreeContent.getId());

	}
	
	/**
	 * 添加一个SQL脚本模型
	 * 
	 * @throws CanNotFoundNodeIDException
	 * @throws FoundTreeNodeNotUniqueException
	 */
	public static TreeContent addSqlScriptModel(SqlScriptModel sqlModel,
			TreeContent parentTreeContent, TreeViewComposite treeViewComposite) throws CanNotFoundNodeIDException,
			FoundTreeNodeNotUniqueException {
		if (sqlModel == null
				|| !(parentTreeContent.getObj() instanceof SqlsNodeModel) || treeViewComposite == null) {
			logger.error("传入的SqlScriptModel为空或父亲树节点不正确，无法在ProductTreeViewPart上添加该SQL脚本！");
			return null;
		}

		// 添加SQL脚本节点
		TreeContent sqlModelContent = treeViewComposite.addNode(
				parentTreeContent.getId(),
				parentTreeContent.getId() + DmConstants.SEPARATOR
						+ sqlModel.getId(),
						sqlModel,
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID,
						IDmImageKey.SQL_16));
		
		sqlModel.getProductModel().getSqlSet().add(sqlModel);
		
		return sqlModelContent;
	}
	
	/**
	 * 添加一个存储过程模型
	 * 
	 * @throws CanNotFoundNodeIDException
	 * @throws FoundTreeNodeNotUniqueException
	 */
	public static TreeContent addStoredModel(StoredProcedureModel storedModel,
			TreeContent parentTreeContent, TreeViewComposite treeViewComposite) throws CanNotFoundNodeIDException,
			FoundTreeNodeNotUniqueException {
		if (storedModel == null
				|| !(parentTreeContent.getObj() instanceof StoredProceduresNodeModel)) {
			logger.error("传入的StoredProcedureModel为空或父亲树节点不正确，无法在ProductTreeViewPart上添加该存储过程！");
			return null;
		}

		// 添加存储过程节点
		TreeContent storedModelContent = treeViewComposite.addNode(
				parentTreeContent.getId(),
				parentTreeContent.getId() + DmConstants.SEPARATOR
						+ storedModel.getId(),
						storedModel,
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID,
						IDmImageKey.STORED_16));
		
		storedModel.getProductModel().getStoredProcedureSet().add(storedModel);
		
		return storedModelContent;
	}
	
	/**
	 * 引入一个表格
	 * 
	 * @throws CanNotFoundNodeIDException
	 * @throws FoundTreeNodeNotUniqueException
	 * @throws AddFileModelToWorkSpaceModelFail
	 */
	public static TreeContent addTableModel(TableModel tableModel,
			TreeContent parentTreeContent, TreeViewComposite treeViewComposite) throws CanNotFoundNodeIDException,
			FoundTreeNodeNotUniqueException {
		if (tableModel == null
				|| !(parentTreeContent.getObj() instanceof ModuleModel) || treeViewComposite == null) {
			logger.error("传入的TableModel为空或模块模型树节点不正确，无法在ProductTreeViewPart上添加该表格！");
			return null;
		}

		// 给模块模型中添加该表格
		ModuleModel moduleModel = (ModuleModel) parentTreeContent.getObj();
		moduleModel.getTableModelSet().add(tableModel);
		
		// 添加表格节点
		TreeContent tableModelContent = treeViewComposite.addNode(
				parentTreeContent.getId(),
				parentTreeContent.getId() + DmConstants.SEPARATOR
						+ tableModel.getTableName(),
				tableModel,
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID,
						IDmImageKey.TABLE_16));

		return tableModelContent;

	}
	
	/**
	 * 移除一个表格
	 * 
	 * @throws CanNotFoundNodeIDException
	 * @throws FoundTreeNodeNotUniqueException
	 * @throws AddFileModelToWorkSpaceModelFail
	 */
	public void removeTableModel(TreeContent tableTreeContent) throws CanNotFoundNodeIDException,
			FoundTreeNodeNotUniqueException {
		if (tableTreeContent == null
				|| !(tableTreeContent.getObj() instanceof TableModel)) {
			logger.error("传入的表格模型的树节点为空或不正确，无法在ProductTreeViewPart上删除该表格！");
			return ;
		}

		TableModel tableModel = (TableModel) tableTreeContent.getObj();
		ModuleModel moduleModel = (ModuleModel) tableTreeContent.getParent()
				.getObj();
		moduleModel.getTableModelSet().remove(tableModel);
		
		treeViewComposite.removeNode(tableTreeContent.getId());

	}
	
	/**
	 * 添加一个文档类别节点
	 * 注意，此方法没有向父容器中添加对自己的引用，因为父容器可能有多种，如产品模型和项目模型
	 * @throws CanNotFoundNodeIDException
	 * @throws FoundTreeNodeNotUniqueException
	 * @throws IOException 
	 * @throws PartInitException 
	 * @throws AddFileModelToWorkSpaceModelFail
	 */
	public static TreeContent addDocCategoryModel(DocumentCategoryModel docCategoryModel,
			TreeContent parentTreeContent) throws CanNotFoundNodeIDException,
			FoundTreeNodeNotUniqueException, IOException, PartInitException {
		if (docCategoryModel == null
				|| !(parentTreeContent.getObj() instanceof DocumentsNodeModel)) {
			logger.error("传入的DocumentCategoryModel为空或文档分类集合模型树节点不正确，无法添加该文档类别！");
			return null;
		}
		
		DocumentsNodeModel documentsNodeModel = (DocumentsNodeModel) parentTreeContent.getObj();
		ProductModel productModel = documentsNodeModel.getParentModel();
		TreeContent docCategroyModelContent;
		if(productModel instanceof ProjectModel) {
			ProjectModel projectModel = (ProjectModel) productModel;
			File docFile = new File(SystemConstants.PROJECTSPACEPATH + File.separator 
					+ projectModel.getFolderName() + File.separator + SystemConstants.ZIP_FILE_DOC 
					+ File.separator);
			if(!docFile.isDirectory()) {
				docFile.mkdirs();
			}
			
			File newDocCategoryFile = new File(docFile, docCategoryModel.getName() + File.separator);
			if(!newDocCategoryFile.isDirectory()) {
				newDocCategoryFile.mkdirs();
			}
			
			// 添加文档类别节点
			docCategroyModelContent = ProjectTreeViewPart.getInstance().getTreeViewComposite().addNode(
					parentTreeContent.getId(),
					parentTreeContent.getId() + DmConstants.SEPARATOR
					+ docCategoryModel.getName(),
					docCategoryModel,
					CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID,
							IDmImageKey.DOC_CATEGROY_LABEL_16));
		} else {
			FileModel fileModel = FileModel.getFileModelFromObj(documentsNodeModel);
			// 向该产品文件目录中添加该文件夹
			File docFile = new File(SystemConstants.WORKSPACEPATH + File.separator 
					+ fileModel.getFolderName() + File.separator + SystemConstants.ZIP_FILE_DOC 
					+ File.separator);
			if(!docFile.isDirectory()) {
				docFile.mkdirs();
			}
			
			File newDocCategoryFile = new File(docFile, docCategoryModel.getName() + File.separator);
			if(!newDocCategoryFile.isDirectory()) {
				newDocCategoryFile.mkdirs();
			}
			
			// 添加文档类别节点
			docCategroyModelContent = ProductTreeViewPart.getInstance().getTreeViewComposite().addNode(
					parentTreeContent.getId(),
					parentTreeContent.getId() + DmConstants.SEPARATOR
					+ docCategoryModel.getName(),
					docCategoryModel,
					CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID,
							IDmImageKey.DOC_CATEGROY_LABEL_16));
		}

		// 将该文档类别添加到文档集合模型中去
		documentsNodeModel.getDocCategoryModelSet().add(docCategoryModel);
		docCategoryModel.setDocumentsNodeModel(documentsNodeModel);
		
		
		Set<DocumentModel> documentModelSet = docCategoryModel.getDocumentModelSet();
		for(DocumentModel documentModel : documentModelSet) {
			addDocumentModel(documentModel, docCategroyModelContent);
		}
		
		return docCategroyModelContent;
	}
	
	/**
	 * 移除一个文档类别节点
	 * 
	 * @throws CanNotFoundNodeIDException
	 * @throws FoundTreeNodeNotUniqueException
	 * @throws PartInitException 
	 * @throws AddFileModelToWorkSpaceModelFail
	 */
	public static void removeDocCategoryModel(TreeContent docCategoryTreeContent) throws CanNotFoundNodeIDException,
			FoundTreeNodeNotUniqueException, PartInitException {
		if (docCategoryTreeContent == null
				|| !(docCategoryTreeContent.getObj() instanceof DocumentCategoryModel)) {
			logger.error("传入的数据为空或不正确，移除文档类别失败！");
			return ;
		}

		// 先删除文档类别的文件夹
		DocumentCategoryModel documentCategoryModel = (DocumentCategoryModel) docCategoryTreeContent.getObj();
		ProductModel productModel = documentCategoryModel.getDocumentsNodeModel().getParentModel();
		
		// 如果是在项目树上删除该文档类别节点
		if(productModel instanceof ProjectModel) {
			ProjectModel projectModel = (ProjectModel) productModel;
			File file = new File(SystemConstants.PROJECTSPACEPATH + File.separator 
					+ projectModel.getFolderName() + File.separator 
					+ SystemConstants.ZIP_FILE_DOC + File.separator 
					+ documentCategoryModel.getName() + File.separator);
			FileDeal.deleteFile(file.getAbsolutePath());
			ProjectTreeViewPart.getInstance().getTreeViewComposite().removeNode(docCategoryTreeContent.getId());
		
		// 如果是在产品树上删除该文档类别节点
		} else {
			FileModel fileModel = FileModel.getFileModelFromObj(documentCategoryModel);
			File file = new File(SystemConstants.WORKSPACEPATH + File.separator 
					+ fileModel.getFolderName() + File.separator 
					+ SystemConstants.ZIP_FILE_DOC + File.separator 
					+ documentCategoryModel.getName() + File.separator);
			FileDeal.deleteFile(file.getAbsolutePath());
			ProductTreeViewPart.getInstance().getTreeViewComposite().removeNode(docCategoryTreeContent.getId());
			
		}
		
		documentCategoryModel.getDocumentsNodeModel().getDocCategoryModelSet().remove(documentCategoryModel);

	}

	/**
	 * 添加一个文档节点
	 * @param documentModel
	 * @param docCategroyModelContent
	 * @param treeViewComposite2
	 * @throws IOException 
	 * @throws PartInitException 
	 */
	public static void addDocumentModel(DocumentModel documentModel,
			TreeContent docCategroyModelContent) throws CanNotFoundNodeIDException,
			FoundTreeNodeNotUniqueException, IOException, PartInitException {
		if (documentModel == null
				|| !(docCategroyModelContent.getObj() instanceof DocumentCategoryModel)) {
			logger.error("传入的DocumentModel为空或文档分类模型树节点不正确，无法添加该文档对象！");
			return ;
		}
		
		// 文件的上传不在这里处理。
		
		DocumentCategoryModel documentCategoryModel = (DocumentCategoryModel) docCategroyModelContent.getObj();
		documentModel.setDocumentCategoryModel(documentCategoryModel);
		documentCategoryModel.getDocumentModelSet().add(documentModel);
		
		// 添加文档节点
		ProductModel productModel = documentCategoryModel.getDocumentsNodeModel().getParentModel();
		if(productModel instanceof ProjectModel) {
			ProjectTreeViewPart.getInstance().getTreeViewComposite().addNode(
					docCategroyModelContent.getId(),
					docCategroyModelContent.getId() + DmConstants.SEPARATOR
					+ documentModel.getFileName(),
					documentModel,
					CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID,
							IDmImageKey.DOC_LABEL_16));
		} else {
			ProductTreeViewPart.getInstance().getTreeViewComposite().addNode(
					docCategroyModelContent.getId(),
					docCategroyModelContent.getId() + DmConstants.SEPARATOR
					+ documentModel.getFileName(),
					documentModel,
					CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID,
							IDmImageKey.DOC_LABEL_16));
		}
		
	}

	/**
	 * 移除一个文档
	 * 
	 * @throws CanNotFoundNodeIDException
	 * @throws FoundTreeNodeNotUniqueException
	 * @throws PartInitException 
	 * @throws AddFileModelToWorkSpaceModelFail
	 */
	public static void removeDocumentModel(TreeContent documentTreeContent) throws CanNotFoundNodeIDException,
			FoundTreeNodeNotUniqueException, PartInitException {
		if (documentTreeContent == null
				|| !(documentTreeContent.getObj() instanceof DocumentModel)) {
			logger.error("传入的数据为空或不正确，移除文档失败！");
			return ;
		}

		// 先删除该文件
		DocumentModel documentModel = (DocumentModel) documentTreeContent.getObj();
		ProductModel productModel = documentModel.getDocumentCategoryModel()
				.getDocumentsNodeModel().getParentModel();
		
		// 如果是在项目树上删除该文档
		if(productModel instanceof ProjectModel) {
			ProjectModel projectModel = (ProjectModel) productModel;
			File file = new File(SystemConstants.PROJECTSPACEPATH + File.separator + projectModel.getFolderName() 
					+ File.separator + SystemConstants.ZIP_FILE_DOC + File.separator + documentModel.getDocumentCategoryModel()
					.getName() + File.separator + documentModel.getFileName());
			FileDeal.deleteFile(file.getAbsolutePath());
			ProjectTreeViewPart.getInstance().getTreeViewComposite().removeNode(documentTreeContent.getId());
		
		// 如果是在产品树上删除该文档
		} else {
			FileModel fileModel = FileModel.getFileModelFromObj(documentModel);
			File file = new File(SystemConstants.WORKSPACEPATH + File.separator + fileModel.getFolderName() 
					+ File.separator + SystemConstants.ZIP_FILE_DOC + File.separator + documentModel.getDocumentCategoryModel()
					.getName() + File.separator + documentModel.getFileName());
			FileDeal.deleteFile(file.getAbsolutePath());
			ProductTreeViewPart.getInstance().getTreeViewComposite().removeNode(documentTreeContent.getId());
			
		}
		
		documentModel.getDocumentCategoryModel().getDocumentModelSet().remove(documentModel);

	}
	
	/**
	 * 根据文件模型来获取对应的树节点
	 * @return
	 */
	public TreeContent getTreeContentFromFileModel(FileModel fileModel) {
		if(fileModel == null) {
			logger.error("传入的文件模型为空，无法获取对应的树节点！");
			return null;
		}
		
		TreeContent rootTreeContent = treeViewComposite.getRootContentList().get(0);
		List<TreeContent> fileTreeContentList = rootTreeContent.getChildrenList();
		for(TreeContent treeContent : fileTreeContentList) {
			if(treeContent.getObj().equals(fileModel)) {
				return treeContent;
			}
		}
		
		logger.warn("没有找到该文件对应的树节点:" + fileModel.getFileName());
		return null;
	}

	/**
	 * 根据产品模型来获取对应的树节点
	 * @return
	 */
	public TreeContent getTreeContentFromProductModel(ProductModel productModel) {
		if(productModel == null) {
			logger.error("传入的产品模型为空，无法获取对应的树节点！");
			return null;
		}
		
		FileModel fileModel = FileModel.getFileModelFromObj(productModel.getPhysicalDataModel());
		
		TreeContent fileTreeContent = getTreeContentFromFileModel(fileModel);
		if(fileTreeContent != null) {
			List<TreeContent> productTreeContentList = fileTreeContent.getChildrenList();
			for(TreeContent productTreeContent : productTreeContentList) {
				if(productModel.equals(productTreeContent.getObj())) {
					return productTreeContent;
				}
			}
		}
		
		logger.warn("没有找到该产品对应的树节点:" + productModel.getName());
		return null;
	}
	
	/**
	 * 根据模块模型来获取对应的树节点
	 * @return
	 */
	public TreeContent getTreeContentFromModuleModel(ModuleModel moduleModel) {
		if(moduleModel == null) {
			logger.error("传入的模块模型为空，无法获取对应的树节点！");
			return null;
		}
		
		TreeContent productTreeContent = getTreeContentFromProductModel(moduleModel.getProductModel());
		
		if(productTreeContent != null) {
			List<TreeContent> treeContentList = productTreeContent.getChildrenList();
			// 先得找到功能模块节点
			for(TreeContent tempTreeContent : treeContentList) {
				if(tempTreeContent.getObj() instanceof ModulesNodeModel) {
					List<TreeContent> moduleTreeContentList = tempTreeContent.getChildrenList();
					for(TreeContent moduleTreeContent : moduleTreeContentList) {
						if(moduleModel.equals(moduleTreeContent.getObj())) {
							return moduleTreeContent;
						}
					}
				}
			}
		}
		
		logger.warn("没有找到该模块对应的树节点:" + moduleModel.getName());
		return null;
	}
	
	/**
	 * 根据表格模型来获取对应的树节点
	 * @return
	 */
	public List<TreeContent> getTreeContentFromTableModel(TableModel tableModel) {
		if(tableModel == null) {
			logger.error("传入的表格模型为空，无法获取对应的树节点！");
			return null;
		}
		
		FileModel fileModel = FileModel.getFileModelFromObj(tableModel);
		List<TreeContent> tableModels = new ArrayList<TreeContent>();
		
		TreeContent fileTreeContent = getTreeContentFromFileModel(fileModel);
		if(fileTreeContent != null) {
			List<TreeContent> productTreeContentList = fileTreeContent.getChildrenList();
			for(TreeContent productTreeContent : productTreeContentList) {
				List<TreeContent> treeContentList = productTreeContent.getChildrenList();
				for(TreeContent treeContent : treeContentList) {
					// 找到功能模型节点
					if(treeContent.getObj() instanceof ModulesNodeModel) {
						List<TreeContent> moduleModelTreeContentList = treeContent.getChildrenList();
						for(TreeContent moduleTreeContent : moduleModelTreeContentList) {
							List<TreeContent> tableModelTreeContentList = moduleTreeContent.getChildrenList();
							for(TreeContent tableModelTreeContent : tableModelTreeContentList) {
								if(tableModelTreeContent.getObj().equals(tableModel)) {
									tableModels.add(tableModelTreeContent);
									break ;
								}
							}
						}
						
					}
				}
			}
		}
		
		return tableModels;
	}
	
//	/**
//	 * 在产品树中删除一个文件模型下面的某个表格
//	 */
//	public void removeTableModelFromFileModel(FileModel fileModel, TableModel tableModel) {
//		if(productModel == null) {
//			logger.warn("传入的ProductModel为空，无法获得该产品下的所有表格！");
//			return null;
//		}
//		
//		List<TableModel> tableModelList = new ArrayList<TableModel>();
//		TreeContent productTreeContent = getTreeContentFromProductModel(productModel);
//		if(productTreeContent != null) {
//			 List<TreeContent> tableTreeContentList = productTreeContent
//					 .getChildrenList();
//			 for(TreeContent tableTreeContent : tableTreeContentList) {
//				 tableModelList.add((TableModel) tableTreeContent.getObj());
//			 }
//		}
//		
//		return tableModelList;
//	}
	
	
	
	/**
	 * 获得所选择的树节点
	 * 
	 * @return
	 */
	public TreeContent getSelection() {
		StructuredSelection selection = (StructuredSelection) treeViewer
				.getSelection();
		TreeContent select = (TreeContent) selection.getFirstElement();

		return select;
	}

	/**
	 * 刷新树
	 */
	public void refreshTree() {
		treeViewer.refresh();
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	public TreeViewComposite getTreeViewComposite() {
		return treeViewComposite;
	}

	@Override
	public void setFocus() {
		tree.setFocus();
	}

}
