package cn.sunline.suncard.powerdesigner.handler;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.dom4j.DocumentException;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.action.DatabaseAction;
import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.manager.WorkSpaceManager;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.WorkSpaceModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeViewComposite;
import cn.sunline.suncard.powerdesigner.xml.SwitchObjAndXml;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

public class OpenHandler extends AbstractHandler{
	private Log logger = LogManager.getLogger(OpenHandler.class.getName());
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if(window == null ) {
			logger.error("找不到活跃的WorkbenchWindow，OpenHandler执行失败！");
			return null;
		}
		
		FileDialog fileDialog = new FileDialog(window.getShell(), SWT.OPEN);
		fileDialog.setFilterExtensions(new String[] { "*."
				+ SystemConstants.ZIP_FILE_EXTEND_NAME });
		fileDialog.setText("打开数据库设计文件");

		String filePath = fileDialog.open();

		if (filePath != null) {
			// 同一个文件只能打开一次
			Set<FileModel> fileModelSet = WorkSpaceModel.getFileModelSet();
			for (FileModel fileModel : fileModelSet) {
				if (fileModel.getFile().getAbsolutePath().equals(filePath)) {
					MessageDialog.openInformation(window.getShell(),
							I18nUtil.getMessage("MESSAGE"),
							I18nUtil.getMessage("THIS_FILE_ALREADY_OPEN"));
					return null;
				}
			}

			try {
				File file = new File(filePath);
				
				FileModel fileModel;
				try {
					fileModel = WorkSpaceManager.addFileToWorkSpace(file);
				} catch (IOException e) {
					logger.error("向工作空间添加该文件失败！" + e.getMessage());
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), I18nUtil.getMessage("MESSAGE"), "向工作空间添加该文件失败！" + e.getMessage());
					e.printStackTrace();
					return null;
				} catch (Exception e) {
					logger.error("向工作空间添加该文件失败！" + e.getMessage());
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), I18nUtil.getMessage("MESSAGE"), "向工作空间添加该文件失败！" + e.getMessage());
					e.printStackTrace();
					return null;
				}
				
				if(fileModel == null) {
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), I18nUtil.getMessage("MESSAGE"), "解析到的FileModel为空，向工作空间添加该文件失败！！");
					return null;
				}
				
				DatabaseTreeViewPart databaseViewPart = (DatabaseTreeViewPart) window
						.getActivePage().findView(DatabaseTreeViewPart.ID);
				ProductTreeViewPart productViewPart = (ProductTreeViewPart) window
						.getActivePage().findView(ProductTreeViewPart.ID);

				if (productViewPart == null) {
					productViewPart = (ProductTreeViewPart) window
							.getActivePage().showView(ProductTreeViewPart.ID);
				}
				if (databaseViewPart == null) {
					databaseViewPart = (DatabaseTreeViewPart) window
							.getActivePage().showView(DatabaseTreeViewPart.ID);
				}

				TreeViewComposite.setExpand(false);
				if (databaseViewPart != null) {
					databaseViewPart.addFileModel(fileModel);
				}

				if (productViewPart != null) {
					productViewPart.addFileModel(fileModel);
				}
				TreeViewComposite.setExpand(true);

			} catch (CanNotFoundNodeIDException e) {
				logger.error("添加数据库树节点失败！" + e.getMessage());
				MessageDialog.openError(window.getShell(),
						I18nUtil.getMessage("MESSAGE"),
						"添加树节点失败！" + e.getMessage());
				e.printStackTrace();

				return null;
			} catch (PartInitException e) {
				logger.error("添加数据库树ViewPart失败！");
				MessageDialog.openError(window.getShell(),
						I18nUtil.getMessage("MESSAGE"),
						"添加数据库树ViewPart失败！" + e.getMessage());
				e.printStackTrace();

				return null;
			} catch (FoundTreeNodeNotUniqueException e) {
				logger.error("添加数据库树ViewPart失败！" + e.getMessage());
				MessageDialog.openError(window.getShell(),
						I18nUtil.getMessage("MESSAGE"),
						"添加数据库树ViewPart失败！" + e.getMessage());
				e.printStackTrace();

				return null;
			} catch (IOException e) {
				logger.error("添加数据库树ViewPart失败！" + e.getMessage());
				MessageDialog.openError(window.getShell(),
						I18nUtil.getMessage("MESSAGE"),
						"添加数据库树ViewPart失败！" + e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}

}
