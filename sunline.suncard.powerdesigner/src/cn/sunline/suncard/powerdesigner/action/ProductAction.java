/* 文件名：     ProductAction.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-23
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.action;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import cn.sunline.suncard.powerdesigner.Activator;
import cn.sunline.suncard.powerdesigner.command.UpdateModuleModelCommand;
import cn.sunline.suncard.powerdesigner.command.UpdateModuleTableCommand;
import cn.sunline.suncard.powerdesigner.command.UpdateProductModelCommand;
import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.model.DocumentCategoryModel;
import cn.sunline.suncard.powerdesigner.model.DocumentModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.models.DocumentsNodeModel;
import cn.sunline.suncard.powerdesigner.models.ModulesNodeModel;
import cn.sunline.suncard.powerdesigner.models.SqlsNodeModel;
import cn.sunline.suncard.powerdesigner.models.StoredProceduresNodeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.powerdesigner.tools.FileDeal;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.powerdesigner.ui.dialog.ImportTableModelDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.SQLManagerDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.StoredProcedureManagerDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.UploadFileDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.modelattri.DocCategoryDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.modelattri.ModuleModelDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.modelattri.ProductModelDialog;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 产品树的Action
 * @author  Manzhizhen
 * @version 1.0, 2012-11-23
 * @see 
 * @since 1.0
 */
public class ProductAction extends Action{
	private ProductTreeViewPart productTreeViewPart;	
	private String actionFlag;		// Action标记
	
	Log logger = LogManager.getLogger(ProductAction.class);
	
	public ProductAction(String actionFlag, ProductTreeViewPart productTreeViewPart) {
		this.productTreeViewPart = productTreeViewPart;
		this.actionFlag = actionFlag;
		
		if(DmConstants.ADD_PRODUCT_MODEL.equals(actionFlag)) {
			setText("新建产品模型");
		} else if(DmConstants.DEL_PRODUCT_MODEL.equals(actionFlag)) {
			setText("删除产品模型");
		} else if(DmConstants.MODIFY_PRODUCT_MODEL.equals(actionFlag)) {
			setText("修改产品模型");
		} else if(DmConstants.IMPORT_TABLE_MODEL.equals(actionFlag)) {
			setText("引入表格模型");
		} else if(DmConstants.REMOVE_TABLE_MODEL.equals(actionFlag)) {
			setText("移除表格模型");
		} else if(DmConstants.ADD_MODULE_MODEL.equals(actionFlag)) {
			setText("新增模块模型");
		} else if(DmConstants.REMOVE_MODULE_MODEL.equals(actionFlag)) {
			setText("删除");
		} else if(DmConstants.ATTRI_MODULE_MODEL.equals(actionFlag)) {
			setText("属性");
		} else if(DmConstants.MANAGE_SQL_MODEL.equals(actionFlag)) {
			setText("管理SQL脚本");
		} else if(DmConstants.MANAGE_STORED_MODEL.equals(actionFlag)) {
			setText("管理储存过程");
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
		if(window == null || window.getActivePage() == null) {
			logger.error("找不到活跃的WorkbenchWindow或WorkbenchPage，ProductAction执行失败！");
			return ;
		}
		
		if(DmConstants.ADD_PRODUCT_MODEL.equals(actionFlag)) {
			addProductModel(window);
			
		} else if(DmConstants.DEL_PRODUCT_MODEL.equals(actionFlag)) {
			delProductModel(window);
			
		} else if(DmConstants.MODIFY_PRODUCT_MODEL.equals(actionFlag)) {
			modifyProductModel(window);
			
		} else if(DmConstants.IMPORT_TABLE_MODEL.equals(actionFlag)) {
			importTableModel(window);
			
		} else if(DmConstants.REMOVE_TABLE_MODEL.equals(actionFlag)) {
			removeTableModel(window);
			
		} else if(DmConstants.ADD_MODULE_MODEL.equals(actionFlag)) {
			addModuleModel(window);
			
		} else if(DmConstants.REMOVE_MODULE_MODEL.equals(actionFlag)) {
			removeModuleModel(window);
			
		} else if(DmConstants.ATTRI_MODULE_MODEL.equals(actionFlag)) {
			attriModuleModel(window);
			
		} else if(DmConstants.MANAGE_SQL_MODEL.equals(actionFlag)) {
			manageSqlModel(window);
			
		} else if(DmConstants.MANAGE_STORED_MODEL.equals(actionFlag)) {
			manageStoredModel(window);
			
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
		
		super.run();
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
			FileModel fileModel = FileModel.getFileModelFromObj(documentModel);
			try {
				FileDeal.copyFileToPath(new File(SystemConstants.WORKSPACEPATH + File.separator
						+ fileModel.getFolderName() + File.separator
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
		FileModel fileModel = FileModel.getFileModelFromObj(documentModel);
		
		File file = new File(SystemConstants.WORKSPACEPATH + File.separator
				+ fileModel.getFolderName() + File.separator
				+ SystemConstants.ZIP_FILE_DOC + File.separator 
				+ documentModel.getDocumentCategoryModel().getName() + File.separator 
				+ documentModel.getFileName());
		
//		String filePath = "E:/test.pdf";
//		FileShowDialog dialog = new FileShowDialog(window.getShell(), filePath);
//		dialog.open();
		
		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.open(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加存储过程
	 * @param window
	 */
	private void manageStoredModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || !(treeContent.getObj() instanceof StoredProceduresNodeModel)) {
			return ;
		}
		
		ProductModel productModel = (ProductModel) treeContent.getParent().getObj();
		
		StoredProcedureManagerDialog dialog = new StoredProcedureManagerDialog(window.getShell(), productModel, treeContent);
		int returnCode = dialog.open();

		if (IDialogConstants.OK_ID == returnCode) {
			CommandStack commandStack = DefaultViewPart
					.getFileCommandFromObj(productModel);
			if (commandStack != null) {
				commandStack.execute(dialog.getCommand());
				// 刷新树上的文件节点
				DefaultViewPart.refreshFileModelTreeContent();
			}
		}
	}

	/**
	 * 添加SQL脚本
	 * @param window
	 */
	private void manageSqlModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || !(treeContent.getObj() instanceof SqlsNodeModel)) {
			return ;
		}
		
		ProductModel productModel = (ProductModel) treeContent.getParent().getObj();
		
		SQLManagerDialog sqlManagerDialog = new SQLManagerDialog(window.getShell(), productModel, treeContent);
		int returnCode = sqlManagerDialog.open();

		if (IDialogConstants.OK_ID == returnCode) {
			CommandStack commandStack = DefaultViewPart
					.getFileCommandFromObj(productModel);
			if (commandStack != null) {
				commandStack.execute(sqlManagerDialog.getCommand());
				// 刷新树上的文件节点
				DefaultViewPart.refreshFileModelTreeContent();
			}
		}
	}

	/**
	 * 修改一个模块
	 * @param window
	 */
	private void attriModuleModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || !(treeContent.getObj() instanceof ModuleModel)) {
			return ;
		}
		
		ModuleModelDialog dialog = new ModuleModelDialog(window.getShell());
		dialog.setFlag(DmConstants.COMMAND_MODIFY);
		dialog.setModuleModel((ModuleModel) treeContent.getObj());
		dialog.setProductTreeViewPart(productTreeViewPart);
		dialog.open();
	}

	/**
	 * 移除一个模块
	 * @param window
	 */
	private void removeModuleModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || (!(treeContent.getObj() instanceof ModuleModel))) {
			return ;
		}
		
		ModuleModel moduleModel = (ModuleModel) treeContent.getObj();
		if(!MessageDialog.openConfirm(window.getShell(), I18nUtil.getMessage("MESSAGE")
				, "是否确实删除该模块模型：" + moduleModel.getName() + "？")) {
			return ;
		}
		
		UpdateModuleModelCommand command = new UpdateModuleModelCommand();
		command.setFlag(DmConstants.COMMAND_DEL);
		command.setProductTreeViewPart(productTreeViewPart);
		command.setModuleModelTreeContent(treeContent);
		
		CommandStack commandStack = DefaultViewPart.getFileCommandFromObj(
				moduleModel.getProductModel());
		if(commandStack != null) {
			commandStack.execute(command);
			DefaultViewPart.refreshFileModelTreeContent();
		}
		
	}

	/**
	 * 新增一个模块
	 * @param window
	 */
	private void addModuleModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || !(treeContent.getObj() instanceof ModulesNodeModel)) {
			return ;
		}
		
		ModuleModelDialog dialog = new ModuleModelDialog(window.getShell());
		dialog.setFlag(DmConstants.COMMAND_ADD);
		dialog.setModulesNodeModelTreeContent(treeContent);
		dialog.setProductTreeViewPart(productTreeViewPart);
		dialog.open();
	}

	/**
	 * 移除一个表格模型
	 * @param window
	 */
	private void removeTableModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || !(treeContent.getObj() instanceof TableModel)) {
			return ;
		}
		
		UpdateModuleTableCommand command = new UpdateModuleTableCommand();
		command.setFlag(DmConstants.COMMAND_DEL);
		command.setTableModelTreeContent(treeContent);
		command.setProductTreeViewPart(productTreeViewPart);
		
		CommandStack commandStack = DefaultViewPart.getFileCommandFromObj(
				treeContent.getObj());
		if(commandStack != null) {
			commandStack.execute(command);
			DefaultViewPart.refreshFileModelTreeContent();
		}
		
	}

	/**
	 * 导入一个表格模型
	 * @param window
	 */
	private void importTableModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || (!(treeContent.getObj() instanceof ModuleModel) 
				&& !(treeContent.getObj() instanceof TableModel))) {
			return ;
		}
		
		if(treeContent.getObj() instanceof TableModel) {
			treeContent = treeContent.getParent();
		}
		
		ImportTableModelDialog dialog = new ImportTableModelDialog(window.getShell());
		dialog.setModuleModel((ModuleModel) treeContent.getObj());
		if(IDialogConstants.OK_ID == dialog.open()) {
			UpdateModuleTableCommand command = new UpdateModuleTableCommand();
			command.setFlag(DmConstants.COMMAND_ADD);
			command.setTableModelList(dialog.getSelectTableList());
			command.setModuleModelTreeContent(treeContent);
			command.setProductTreeViewPart(productTreeViewPart);
			
			CommandStack commandStack = DefaultViewPart.getFileCommandFromObj(
					treeContent.getObj());
			if(commandStack != null) {
				commandStack.execute(command);
				DefaultViewPart.refreshFileModelTreeContent();
			}
			
		}
	}

	/**
	 * 修改一个产品模型
	 * @param window
	 */
	private void modifyProductModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || !(treeContent.getObj() instanceof ProductModel)) {
			return ;
		}
		
		ProductModelDialog dialog = new ProductModelDialog(window.getShell());
		dialog.setFileModelTreeContent(treeContent.getParent());
		dialog.setFlag(DmConstants.COMMAND_MODIFY);
		dialog.setProductModel((ProductModel) treeContent.getObj());
		dialog.setProductTreeViewPart(productTreeViewPart);
		dialog.open();
	}

	/**
	 * 删除一个产品模型
	 * @param window
	 */
	private void delProductModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || (!(treeContent.getObj() instanceof ProductModel))) {
			return ;
		}
		
		if(!MessageDialog.openConfirm(window.getShell(), I18nUtil.getMessage("MESSAGE")
				, "是否确实删除该产品模型？")) {
			return ;
		}
		
		UpdateProductModelCommand command = new UpdateProductModelCommand();
		command.setFlag(DmConstants.COMMAND_DEL);
		command.setProductTreeViewPart(productTreeViewPart);
		command.setProductModelTreeContent(treeContent);
		
		CommandStack commandStack = DefaultViewPart.getFileCommandFromObj(
				treeContent.getParent().getObj());
		if(commandStack != null) {
			commandStack.execute(command);
		}
	}

	/**
	 * 添加一个产品模型
	 * @param window
	 */
	private void addProductModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if(treeContent == null || (!(treeContent.getObj() instanceof FileModel) && !(treeContent.getObj() instanceof ProductModel))) {
			return ;
		}
		
		if(treeContent.getObj() instanceof ProductModel) {
			treeContent = treeContent.getParent();
		}
		
		ProductModelDialog dialog = new ProductModelDialog(window.getShell());
		dialog.setFlag(DmConstants.COMMAND_ADD);
		dialog.setFileModelTreeContent(treeContent);
		dialog.setProductTreeViewPart(productTreeViewPart);
		dialog.open();
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		ImageDescriptor descriptor = null;
		
		if(actionFlag.equals(DmConstants.ADD_PRODUCT_MODEL) || actionFlag.equals(DmConstants.ADD_DOC_CATEGORY) 
				|| actionFlag.equals(DmConstants.ADD_DOC)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_NEW_IMAGE);
				
		} else if(actionFlag.equals(DmConstants.DEL_PRODUCT_MODEL) || actionFlag.equals(DmConstants.DEL_DOC_CATEGORY) 
				|| actionFlag.equals(DmConstants.DEL_DOC)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_DELETE_IMAGE);
				
		} else if (actionFlag.equals(DmConstants.MODIFY_PRODUCT_MODEL) || actionFlag.equals(DmConstants.MODIFY_DOC_CATEGORY)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_MODIFY_IMAGE);
				
		} else if (actionFlag.equals(DmConstants.IMPORT_TABLE_MODEL)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_IMPORT_IMAGE);
				
		} else if (actionFlag.equals(DmConstants.REMOVE_TABLE_MODEL)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_EXPORT_IMAGE);
				
		}  else if (actionFlag.equals(DmConstants.ATTRI_MODULE_MODEL)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_MODIFY_IMAGE);
				
		} else if (actionFlag.equals(DmConstants.ADD_MODULE_MODEL)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_NEW_IMAGE);
				
		} else if (actionFlag.equals(DmConstants.REMOVE_MODULE_MODEL)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_DELETE_IMAGE);
				
		} else if (actionFlag.equals(DmConstants.MANAGE_SQL_MODEL)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_MANAGER_IMAGE);
				
		} else if (actionFlag.equals(DmConstants.MANAGE_STORED_MODEL)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_MANAGER_IMAGE);
				
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
	
	/**
	 * 获得所选择的树节点
	 * @return
	 */
	public TreeContent getSelect() {
		return productTreeViewPart.getSelection();
	}
}
