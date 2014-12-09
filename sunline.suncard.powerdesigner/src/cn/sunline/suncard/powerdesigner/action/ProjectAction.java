/* 文件名：     ProjectAction.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-29
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.action;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import cn.sunline.suncard.powerdesigner.Activator;
import cn.sunline.suncard.powerdesigner.exception.CanNotFoundFolderFromWorkSpaceException;
import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.gef.ui.dialog.TableGefModelDialog;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.manager.ProjectSpaceManager;
import cn.sunline.suncard.powerdesigner.manager.WorkSpaceManager;
import cn.sunline.suncard.powerdesigner.model.DocumentCategoryModel;
import cn.sunline.suncard.powerdesigner.model.DocumentModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.ProjectGroupModel;
import cn.sunline.suncard.powerdesigner.model.ProjectModel;
import cn.sunline.suncard.powerdesigner.model.ProjectSpaceModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.WorkSpaceModel;
import cn.sunline.suncard.powerdesigner.models.DocumentsNodeModel;
import cn.sunline.suncard.powerdesigner.models.ModulesNodeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.powerdesigner.tools.FileDeal;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProjectDefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProjectTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeViewComposite;
import cn.sunline.suncard.powerdesigner.ui.dialog.ImportModuleDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.SynchronizationProjectModelDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.UploadFileDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.modelattri.DocCategoryDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.modelattri.ProjectGroupModelDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.modelattri.ProjectModelDialog;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 项目树的Action
 * @author  Manzhizhen
 * @version 1.0, 2012-12-29
 * @see 
 * @since 1.0
 */
public class ProjectAction extends Action{
	private ProjectTreeViewPart projectTreeViewPart;	
	private String actionFlag;		// Action标记
	
	private static Log logger = LogManager.getLogger(ProjectAction.class);
	
	public ProjectAction(String actionFlag, ProjectTreeViewPart projectTreeViewPart) {
		this.projectTreeViewPart = projectTreeViewPart;
		this.actionFlag = actionFlag;
		
		if(DmConstants.ADD_PROJECT_GROUP_MODEL.equals(actionFlag)) {
			setText("新建项目群模型");
		} else if(DmConstants.CLOSE_PROJECT_GROUP_MODEL.equals(actionFlag)) {
			setText("关闭项目群模型");
		} else if(DmConstants.MODIFY_PROJECT_GROUP_MODEL.equals(actionFlag)) {
			setText("属性");
		} else if(DmConstants.ADD_PROJECT_MODEL.equals(actionFlag)) {
			setText("新建项目模型");
		} else if(DmConstants.CLOSE_PROJECT_MODEL.equals(actionFlag)) {
			setText("关闭项目模型");
		} else if(DmConstants.OPEN_PROJECT_MODEL.equals(actionFlag)) {
			setText("打开项目模型");
		} else if(DmConstants.ATTRI_PROJECT_MODEL.equals(actionFlag)) {
			setText("属性");
		} else if(DmConstants.IMPORT_MODULE_MODEL.equals(actionFlag)) {
			setText("导入模块");
		} else if(DmConstants.SYNCHRONOUS_MODULE_MODEL.equals(actionFlag)) {
			setText("同步模块");
		} else if(DmConstants.ATTRI_TABLE_MODEL.equals(actionFlag)) {
			setText("查看");
		} else if(DmConstants.ADD_DOC_CATEGORY.equals(actionFlag)) {
			setText("添加文档分类");
		} else if(DmConstants.DEL_DOC_CATEGORY.equals(actionFlag)) {
			setText("删除");
		} else if(DmConstants.MODIFY_DOC_CATEGORY.equals(actionFlag)) {
			setText("修改");
		} else if(DmConstants.ADD_DOC.equals(actionFlag)) {
			setText("添加文档");
		} else if(DmConstants.DEL_DOC.equals(actionFlag)) {
			setText("删除文档");
		} else if(DmConstants.DOWNLOAD_DOC.equals(actionFlag)) {
			setText("下载文档");
		} else if(DmConstants.VIEW_DOC.equals(actionFlag)) {
			setText("查看");
		}
	}
	
	@Override
	public void run() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if(window == null) {
			logger.error("找不到活跃的WorkbenchWindow，ProjectAction执行失败！");
			return ;
		}
		
		if(DmConstants.ADD_PROJECT_GROUP_MODEL.equals(actionFlag)) {
			addProjectGroupModel(window);
			
		} else if(DmConstants.CLOSE_PROJECT_GROUP_MODEL.equals(actionFlag)) {
			closeProjectGroupModel(window);
			
		} else if(DmConstants.MODIFY_PROJECT_GROUP_MODEL.equals(actionFlag)) {
			modifyProjectGroupModel(window);
			
		} else if(DmConstants.ADD_PROJECT_MODEL.equals(actionFlag)) {
			addProjectModel(window);
			
		} else if(DmConstants.CLOSE_PROJECT_MODEL.equals(actionFlag)) {
			closeProjectModel(window, getSelect());
			
		} else if(DmConstants.OPEN_PROJECT_MODEL.equals(actionFlag)) {
			openProjectModel(window);
			
		} else if(DmConstants.ATTRI_PROJECT_MODEL.equals(actionFlag)) {
			attriProjectModel(window);
			
		} else if(DmConstants.IMPORT_MODULE_MODEL.equals(actionFlag)) {
			importModuleModel(window);
			
		} else if(DmConstants.ATTRI_TABLE_MODEL.equals(actionFlag)) {
			attriTableModel(window);
			
		} else if(DmConstants.SYNCHRONOUS_MODULE_MODEL.equals(actionFlag)) {
			synModuleModel(window);
			
		} else if(DmConstants.ADD_DOC_CATEGORY.equals(actionFlag)) {
			addDocCategoryModel(window);
			
		} else if(DmConstants.DEL_DOC_CATEGORY.equals(actionFlag)) {
			delDocCategoryModel(window);
			
		} else if(DmConstants.MODIFY_DOC_CATEGORY.equals(actionFlag)) {
			modifyDocCategoryModel(window);
			
		} else if(DmConstants.ADD_DOC.equals(actionFlag)) {
			addDocModel(window);
			
		} else if(DmConstants.DEL_DOC.equals(actionFlag)) {
			delDocModel(window);
			
		} else if(DmConstants.DOWNLOAD_DOC.equals(actionFlag)) {
			downloadDocModel(window);
			
		} else if(DmConstants.VIEW_DOC.equals(actionFlag)) {
			viewDocModel(window);
		}
	}
	
	/**
	 * 添加文档分类
	 * @param window
	 */
	private void addDocCategoryModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || !(treeContent.getObj() instanceof DocumentsNodeModel)) {
			return ;
		}
		
		DocCategoryDialog dialog = new DocCategoryDialog(window.getShell());
		dialog.setFlag(DmConstants.COMMAND_ADD);
		dialog.setDocumentsNodeTreeContent(treeContent);
		dialog.open();
	}
	
	/**
	 * 删除文档分类
	 * @param window
	 */
	private void delDocCategoryModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || !(treeContent.getObj() instanceof DocumentCategoryModel)) {
			return ;
		}
		
		DocumentCategoryModel documentCategoryModel = (DocumentCategoryModel) treeContent.getObj();
		if(!MessageDialog.openConfirm(window.getShell(), I18nUtil.getMessage("MESSAGE")
				, "是否要删除该文档类别：" + documentCategoryModel.getName() + " ？")) {
			return ;
		}
		
		try {
			ProductTreeViewPart productTreeViewPart = ProductTreeViewPart.getInstance();
			productTreeViewPart.removeDocCategoryModel(treeContent);
			
		} catch (CanNotFoundNodeIDException e) {
			logger.debug("删除文档分类失败！" + e.getMessage());
			MessageDialog.openError(window.getShell(), I18nUtil.getMessage("MESSAGE")
					, "删除文档分类失败！" + e.getMessage());
			e.printStackTrace();
		} catch (FoundTreeNodeNotUniqueException e) {
			logger.debug("删除文档分类失败！" + e.getMessage());
			MessageDialog.openError(window.getShell(), I18nUtil.getMessage("MESSAGE")
					, "删除文档分类失败！" + e.getMessage());
			e.printStackTrace();
		} catch (PartInitException e) {
			logger.debug("删除文档分类失败！" + e.getMessage());
			MessageDialog.openError(window.getShell(), I18nUtil.getMessage("MESSAGE")
					, "删除文档分类失败！" + e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 修改文档分类
	 * @param window
	 */
	private void modifyDocCategoryModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || !(treeContent.getObj() instanceof DocumentCategoryModel)) {
			return ;
		}
		
		DocCategoryDialog dialog = new DocCategoryDialog(window.getShell());
		dialog.setFlag(DmConstants.COMMAND_MODIFY);
		dialog.setDocumentCategoryModel((DocumentCategoryModel) treeContent
				.getObj());
		dialog.setDocumentsNodeTreeContent(treeContent.getParent());
		dialog.open();
	}
	
	/**
	 * 添加文档
	 * @param window
	 */
	private void addDocModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || !(treeContent.getObj() instanceof DocumentCategoryModel)) {
			return ;
		}
		
		UploadFileDialog dialog = new UploadFileDialog(window.getShell());
		dialog.setDocCategoryModelTreeContent(treeContent);
		dialog.open();
	}
	
	/**
	 * 删除文档
	 * @param window
	 * @throws PartInitException 
	 * @throws FoundTreeNodeNotUniqueException 
	 * @throws CanNotFoundNodeIDException 
	 */
	private void delDocModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || !(treeContent.getObj() instanceof DocumentModel)) {
			return ;
		}
		
		try {
			DocumentModel documentModel = (DocumentModel) treeContent.getObj();
			if(MessageDialog.openQuestion(window.getShell(), I18nUtil.getMessage("MESSAGE")
					, "是否确认删除此文档：" + documentModel.getFileName() + " ?")) {
				ProductTreeViewPart productTreeViewPart = ProductTreeViewPart.getInstance();
				productTreeViewPart.removeDocumentModel(treeContent);
			}
		} catch (PartInitException e) {
			logger.debug("删除文档失败！" + e.getMessage());
			MessageDialog.openError(window.getShell(), I18nUtil.getMessage("MESSAGE")
					, "删除文档失败！" + e.getMessage());
			e.printStackTrace();
		} catch (CanNotFoundNodeIDException e) {
			logger.debug("删除文档失败！" + e.getMessage());
			MessageDialog.openError(window.getShell(), I18nUtil.getMessage("MESSAGE")
					, "删除文档失败！" + e.getMessage());
			e.printStackTrace();
		} catch (FoundTreeNodeNotUniqueException e) {
			logger.debug("删除文档失败！" + e.getMessage());
			MessageDialog.openError(window.getShell(), I18nUtil.getMessage("MESSAGE")
					, "删除文档失败！" + e.getMessage());
			e.printStackTrace();
		}
		
	}

	/**
	 * 下载文档
	 * @param window
	 */
	private void downloadDocModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || !(treeContent.getObj() instanceof DocumentModel)) {
			return ;
		}
		
		DirectoryDialog dialog = new DirectoryDialog(window.getShell());
		String path = dialog.open();
		if(path != null) {
			DocumentModel documentModel = (DocumentModel) treeContent.getObj();
			ProjectModel projectModel = (ProjectModel) documentModel.getDocumentCategoryModel()
					.getDocumentsNodeModel().getParentModel();
			try {
				FileDeal.copyFileToPath(new File(SystemConstants.PROJECTSPACEPATH + File.separator
						+ projectModel.getFolderName() + File.separator
						+ SystemConstants.ZIP_FILE_DOC + File.separator 
						+ documentModel.getDocumentCategoryModel().getName() + File.separator 
						+ documentModel.getFileName()).getAbsolutePath(), new File(path, documentModel.getFileName()).getAbsolutePath());
			} catch (IOException e) {
				logger.debug("下载文件失败！" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * 查看文档
	 * @param window
	 */
	private void viewDocModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || !(treeContent.getObj() instanceof DocumentModel)) {
			return ;
		}
		
		DocumentModel documentModel = (DocumentModel) treeContent.getObj();
		ProjectModel projectModel = (ProjectModel) documentModel.getDocumentCategoryModel()
				.getDocumentsNodeModel().getParentModel();
		
		File file = new File(SystemConstants.PROJECTSPACEPATH + File.separator
				+ projectModel.getFolderName() + File.separator
				+ SystemConstants.ZIP_FILE_DOC + File.separator 
				+ documentModel.getDocumentCategoryModel().getName() + File.separator 
				+ documentModel.getFileName());
		
		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.open(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 同步模块
	 * @param window
	 */
	private void synModuleModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent == null || !(treeContent.getObj() instanceof ModulesNodeModel)) {
			return;
		}
		
		ModulesNodeModel modulesNodeModel = (ModulesNodeModel) treeContent.getObj();
		ProjectModel projectModel = (ProjectModel) modulesNodeModel.getProductModel();
		SynchronizationProjectModelDialog dialog = new SynchronizationProjectModelDialog(window.getShell());
		dialog.setProjectModel(projectModel);
		dialog.setProjectTreeViewPart(projectTreeViewPart);
		dialog.open();
	}

	/**
	 * 查看表格对象
	 * @param window
	 */
	private void attriTableModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent == null || !(treeContent.getObj() instanceof TableModel)) {
			return;
		}
		
		TableGefModelDialog dialog = new TableGefModelDialog(window.getShell());
		dialog.setTableModel((TableModel) treeContent.getObj());
		dialog.setProjectTable(true);
		dialog.open();
		
	}

	/**
	 * 打开项目模型
	 * @param window
	 */
	private void openProjectModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || !(treeContent.getObj() instanceof ProjectSpaceModel)) {
			return ;
		}
		
		FileDialog fileDialog = new FileDialog(window.getShell(), SWT.OPEN);
		fileDialog.setFilterExtensions(new String[] { "*."
				+ SystemConstants.ZIP_FILE_EXTEND_NAME });
		fileDialog.setText("打开项目文件");

		String filePath = fileDialog.open();

		if (filePath != null) {
			// 同一个文件只能打开一次
			Set<ProjectModel> projectModelSet = ProjectSpaceManager.getAllProjectModel();
			for (ProjectModel projectModel : projectModelSet) {
				if (projectModel.getFile().getAbsolutePath().equals(filePath)) {
					MessageDialog.openInformation(window.getShell(),
							I18nUtil.getMessage("MESSAGE"),
							I18nUtil.getMessage("THIS_FILE_ALREADY_OPEN_PROJECTSPACE"));
					return ;
				}
			}

			try {
				File file = new File(filePath);
				
				ProjectModel projectModel;
				try {
					projectModel = ProjectSpaceManager.addProjectFileToProjectSpace(file);
				} catch (IOException e) {
					logger.error("向项目空间添加该文件失败！" + e.getMessage());
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), I18nUtil.getMessage("MESSAGE"), "向项目空间添加该文件失败！" + e.getMessage());
					e.printStackTrace();
					return ;
				} catch (Exception e) {
					logger.error("向项目空间添加该文件失败！" + e.getMessage());
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), I18nUtil.getMessage("MESSAGE"), "向项目空间添加该文件失败！" + e.getMessage());
					e.printStackTrace();
					return ;
				}
				
				if(projectModel == null) {
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), I18nUtil.getMessage("MESSAGE"), "解析到的ProjectModel为空，向项目空间添加该文件失败！！");
					return ;
				}
				
				ProjectTreeViewPart projectTreeViewPart = (ProjectTreeViewPart) window
						.getActivePage().findView(ProjectTreeViewPart.ID);
				if (projectTreeViewPart == null) {
					projectTreeViewPart = (ProjectTreeViewPart) window
							.getActivePage().showView(DatabaseTreeViewPart.ID);
				}

				// 添加项目模型时得先找到其上的项目群模型，如果没有找到，得先创建项目群模型
				TreeContent projectGroupTreeContent;
				Set<String> keySet = ProjectSpaceModel.getProjectGroupModelMap().keySet();
				// 如果项目树中已经存在该ID的项目群模型，则只需要找到该项目群模型的树节点
				if(keySet.contains(projectModel.getProjectGroupModel().getId())) {
					projectGroupTreeContent = projectTreeViewPart.getProjectGroupTreeContentFromId(projectModel
							.getProjectGroupModel().getId());
				} else {
					projectGroupTreeContent = projectTreeViewPart.addProjectGroupModel(projectModel.getProjectGroupModel(), treeContent);
				}
				
				TreeViewComposite.setExpand(false);
				// 向项目树节点添加项目树节点
				if (projectTreeViewPart != null) {
					projectTreeViewPart.addProjectModel(projectModel, projectGroupTreeContent);
				}

				TreeViewComposite.setExpand(true);

			} catch (CanNotFoundNodeIDException e) {
				logger.error("添加项目树节点失败！" + e.getMessage());
				MessageDialog.openError(window.getShell(),
						I18nUtil.getMessage("MESSAGE"),
						"添加项目树节点失败！" + e.getMessage());
				e.printStackTrace();

				return ;
			} catch (PartInitException e) {
				logger.error("添加项目树节点失败！");
				MessageDialog.openError(window.getShell(),
						I18nUtil.getMessage("MESSAGE"),
						"添加数据库树节点失败！" + e.getMessage());
				e.printStackTrace();

				return ;
			} catch (FoundTreeNodeNotUniqueException e) {
				logger.error("添加项目树节点失败！" + e.getMessage());
				MessageDialog.openError(window.getShell(),
						I18nUtil.getMessage("MESSAGE"),
						"添加项目树节点失败！" + e.getMessage());
				e.printStackTrace();

				return ;
			} catch (IOException e) {
				logger.error("添加项目树节点失败！" + e.getMessage());
				MessageDialog.openError(window.getShell(),
						I18nUtil.getMessage("MESSAGE"),
						"添加项目树节点失败！" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * 关闭一个项目文件
	 */
	public static void closeProjectModel(IWorkbenchWindow window, TreeContent treeContent) {
		closeProjectTreeContent(window, treeContent);
	}

	/**
	 * 关闭一个项目文件
	 * @param window
	 * @param projectContent
	 */
	private static void closeProjectTreeContent(IWorkbenchWindow window,
			TreeContent projectContent) {
		ProjectModel projectModel = (ProjectModel) projectContent.getObj();

		boolean isDirty = false;

		// 先检查该文件是否变脏
		CommandStack commandStack = ProjectDefaultViewPart
				.getCommandStackFromProjectModel(projectModel);
		if (commandStack != null && commandStack.isDirty()) {
			isDirty = true;
		}

		if (isDirty) {
			if (MessageDialog.openConfirm(window.getShell(),
					I18nUtil.getMessage("MESSAGE"), "该项目文件已被修改，是否保存该变动？")) {
				ProjectDefaultViewPart.saveProjectModel(projectModel);
			}
		}

		// 从项目树中删除该ProjectModel
		ProjectTreeViewPart projectTreeViewPart = (ProjectTreeViewPart) window
				.getActivePage().findView(ProjectTreeViewPart.ID);

		try {
			projectTreeViewPart.closeProjectModel(projectContent);

		} catch (CanNotFoundNodeIDException e) {
			logger.error("项目节点关闭失败！" + e.getMessage());
			MessageDialog.openError(window.getShell(),
					I18nUtil.getMessage("MESSAGE"), "项目节点关闭失败！" + e.getMessage());
			e.printStackTrace();
			return ;
		} catch (FoundTreeNodeNotUniqueException e) {
			logger.error("项目节点关闭失败！" + e.getMessage());
			MessageDialog.openError(window.getShell(),
					I18nUtil.getMessage("MESSAGE"), "项目节点关闭失败！" + e.getMessage());
			e.printStackTrace();
			return ;
		}
		
		// 先把该项目模型对应的文件夹中的内容覆盖掉该项目模型所指向的压缩文件，成功后会把该文件夹从工作空间移除
		try {
			ProjectSpaceManager.removeFromProjectSpace(projectModel);
		} catch (CanNotFoundFolderFromWorkSpaceException e1) {
			logger.error("关闭该项目模型失败:" + e1.getMessage());
			e1.printStackTrace();
			return ;
		} catch (IOException e1) {
			logger.error("关闭该项目模型失败:" + e1.getMessage());
			e1.printStackTrace();
			return ;
		} catch (Throwable e1) {
			logger.error("关闭该项目模型失败:" + e1.getMessage());
			e1.printStackTrace();
			return ;
		}
		
	}

	/**
	 * 新建项目后需要导入一次模块模型
	 * @param window
	 */
	private void importModuleModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || !(treeContent.getObj() instanceof ModulesNodeModel)) {
			return ;
		}
		
		ModulesNodeModel modulesNodeModel = (ModulesNodeModel) treeContent.getObj();
		ProjectModel projectModel = (ProjectModel) modulesNodeModel.getProductModel();
		
		if(projectModel.getDatabaseTypeModel() == null) {
			MessageDialog.openWarning(window.getShell(), I18nUtil.getMessage("MESSAGE")
					, "项目模型数据库类型为空，无法导入模块表格！");
			return ;
		}
		
		ImportModuleDialog dialog = new ImportModuleDialog(window.getShell());
		dialog.setProjectTreeViewPart(projectTreeViewPart);
		dialog.setModulesNodeTreeContent(treeContent);
		dialog.open();
		
	}

	/**
	 * 项目模型属性
	 * @param window
	 */
	private void attriProjectModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || !(treeContent.getObj() instanceof ProjectModel)) {
			return ;
		}
		
		ProjectModelDialog dialog = new ProjectModelDialog(window.getShell());
		dialog.setFlag(DmConstants.COMMAND_MODIFY);
		dialog.setProjectTreeViewPart(projectTreeViewPart);
		dialog.setProjectTreeContent(treeContent);
		dialog.open();
		
	}

	/**
	 * 新建一个项目
	 * @param window
	 */
	private void addProjectModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || !(treeContent.getObj() instanceof ProjectGroupModel)) {
			return ;
		}
		
		FileDialog fileDialog = new FileDialog(window.getShell(), SWT.SAVE);
		fileDialog.setFilterExtensions(new String[] { "*."
				+ SystemConstants.ZIP_FILE_EXTEND_NAME });
		fileDialog.setText("新建项目文件");

		String filePath = fileDialog.open();

		if (filePath != null) {
			try {
				File file = new File(filePath);
				if (file.exists()) {
					if (!MessageDialog.openConfirm(window.getShell(),
							I18nUtil.getMessage("MESSAGE"),
							I18nUtil.getMessage("IS_COVER_FILE"))) {
						return ;
					}
				}

				file = ProjectSpaceManager.createNewProjectFile(filePath);
				if(file == null) {
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), I18nUtil.getMessage("MESSAGE"), "创建新项目文件失败！");
					return ;
				}
				
				ProjectModel projectModel;
				try {
					projectModel = ProjectSpaceManager.addProjectFileToProjectSpace(file);
				} catch (Exception e) {
					logger.error("将项目文件添加到项目空间失败！" + e.getMessage());
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), I18nUtil.getMessage("MESSAGE"), "将项目文件添加到项目空间失败！" + e.getMessage());
					e.printStackTrace();
					return ;
				}
				
				if(projectModel == null) {
					logger.error("解析到的ProjectModel为空，添加该项目文件到项目空间失败！");
					return ;
				}

				projectTreeViewPart.addProjectModel(projectModel, treeContent);
//				ProjectDefaultViewPart.saveFileModel(fileModel);

			} catch (CanNotFoundNodeIDException e) {
				logger.error("添加数据库树节点失败！" + e.getMessage());
				MessageDialog.openError(window.getShell(),
						I18nUtil.getMessage("MESSAGE"),
						"添加树节点失败！" + e.getMessage());
				e.printStackTrace();

				return ;
			} catch (FoundTreeNodeNotUniqueException e) {
				logger.error("添加数据库树ViewPart失败！" + e.getMessage());
				MessageDialog.openError(window.getShell(),
						I18nUtil.getMessage("MESSAGE"),
						"添加数据库树ViewPart失败！" + e.getMessage());
				e.printStackTrace();

				return ;
			} catch (IOException e) {
				logger.error("创建文件失败！" + e.getMessage());
				MessageDialog.openError(window.getShell(),
						I18nUtil.getMessage("MESSAGE"),
						"创建文件失败！" + e.getMessage());
				e.printStackTrace();

				return ;
			} catch (PartInitException e) {
				logger.error("创建文件失败！" + e.getMessage());
				MessageDialog.openError(window.getShell(),
						I18nUtil.getMessage("MESSAGE"),
						"创建文件失败！" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * 修改一个项目群模型
	 * @param window
	 */
	private void modifyProjectGroupModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || !(treeContent.getObj() instanceof ProjectGroupModel)) {
			return ;
		}
		
		ProjectGroupModelDialog dialog = new ProjectGroupModelDialog(window.getShell());
		dialog.setFlag(DmConstants.COMMAND_MODIFY);
		dialog.setProjectGroupTreeContent(treeContent);
		dialog.setProjectTreeViewPart(projectTreeViewPart);
		dialog.open();
	}

	/**
	 * 关闭一个项目群模型
	 * @param window
	 */
	private void closeProjectGroupModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent == null || !(treeContent.getObj() instanceof ProjectGroupModel)) {
			return;
		}
		
		// 先依次关闭旗下的项目节点
		List<TreeContent> projectTreeContentList = treeContent.getChildrenList();
//		for(TreeContent projectTreeContent : projectTreeContentList) {
//			closeProjectTreeContent(window, projectTreeContent);
//		}
		while(projectTreeContentList.size() > 0) {
			closeProjectTreeContent(window, projectTreeContentList.get(0));
		}
		
		// 从树上上传该项目群节点
		try {
			projectTreeViewPart.closeProjectGroupModel(treeContent);
		} catch (FoundTreeNodeNotUniqueException e) {
			logger.error("删除项目群节点失败！" + e.getMessage());
			MessageDialog.openError(window.getShell(),
					I18nUtil.getMessage("MESSAGE"),
					"删除项目群节点失败！" + e.getMessage());
			e.printStackTrace();

		} catch (CanNotFoundNodeIDException e) {
			logger.error("删除项目群节点失败！" + e.getMessage());
			MessageDialog.openError(window.getShell(),
					I18nUtil.getMessage("MESSAGE"),
					"删除项目群节点失败！" + e.getMessage());
			e.printStackTrace();
		}
		
	}

	/**
	 * 新增一个项目群模型
	 * @param window
	 */
	private void addProjectGroupModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || (!(treeContent.getObj() instanceof ProjectSpaceModel) && 
				!(treeContent.getObj() instanceof ProjectGroupModel))) {
			return ;
		}
		
		if(treeContent.getObj() instanceof ProjectGroupModel) {
			treeContent = treeContent.getParent();
		}
		
		ProjectGroupModelDialog dialog = new ProjectGroupModelDialog(window.getShell());
		dialog.setFlag(DmConstants.COMMAND_ADD);
		dialog.setProjectSpaceTreeContent(treeContent);
		dialog.setProjectTreeViewPart(projectTreeViewPart);
		dialog.open();
	}
	
	/**
	 * 获得所选择的树节点
	 * @return
	 */
	public TreeContent getSelect() {
		return projectTreeViewPart.getSelection();
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		ImageDescriptor descriptor = null;
		
		if(actionFlag.equals(DmConstants.ADD_PROJECT_GROUP_MODEL) || actionFlag.equals(DmConstants.ADD_DOC_CATEGORY)
				|| actionFlag.equals(DmConstants.ADD_DOC)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_NEW_IMAGE);
				
		} else if(actionFlag.equals(DmConstants.CLOSE_PROJECT_GROUP_MODEL)|| actionFlag.equals(DmConstants.DEL_DOC_CATEGORY)
				|| actionFlag.equals(DmConstants.DEL_DOC)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_CLOSE);
				
		} else if(actionFlag.equals(DmConstants.MODIFY_PROJECT_GROUP_MODEL) || actionFlag.equals(DmConstants.MODIFY_DOC_CATEGORY)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_MODIFY_IMAGE);
				
		} else if(actionFlag.equals(DmConstants.ADD_PROJECT_MODEL)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_NEW_IMAGE);
				
		} else if(actionFlag.equals(DmConstants.CLOSE_PROJECT_MODEL)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_CLOSE);
				
		} else if(actionFlag.equals(DmConstants.OPEN_PROJECT_MODEL)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_OPEN_FILE);
				
		} else if(actionFlag.equals(DmConstants.ATTRI_PROJECT_MODEL)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_MODIFY_IMAGE);
				
		} else if(actionFlag.equals(DmConstants.IMPORT_MODULE_MODEL)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_IMPORT_IMAGE);
				
		} else if(actionFlag.equals(DmConstants.SYNCHRONOUS_MODULE_MODEL)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_SYNCHRONOUS_IMAGE);
				
		} else if (actionFlag.equals(DmConstants.VIEW_DOC)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_READ_IMAGE);
				
		} else if (actionFlag.equals(DmConstants.DOWNLOAD_DOC)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_DOWNLOAD_IMAGE);
				
		}
		
		return descriptor;
	}
}
