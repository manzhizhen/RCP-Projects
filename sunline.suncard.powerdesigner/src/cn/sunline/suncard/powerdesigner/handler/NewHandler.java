package cn.sunline.suncard.powerdesigner.handler;

import java.io.File;
import java.io.IOException;

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

import cn.sunline.suncard.powerdesigner.Activator;
import cn.sunline.suncard.powerdesigner.action.DatabaseAction;
import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.manager.WorkSpaceManager;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

public class NewHandler extends AbstractHandler{
	private Log logger = LogManager.getLogger(NewHandler.class.getName());
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if(window == null || window.getActivePage() == null) {
			logger.error("找不到活跃的WorkbenchWindow，NewHandler执行失败！");
			return null;
		}
		
		FileDialog fileDialog = new FileDialog(window.getShell(), SWT.SAVE);
		fileDialog.setFilterExtensions(new String[] { "*."
				+ SystemConstants.ZIP_FILE_EXTEND_NAME });
		fileDialog.setText("新建数据库设计文件");

		String filePath = fileDialog.open();

		if (filePath != null) {
			try {
				File file = new File(filePath);
				if (file.exists()) {
					if (!MessageDialog.openConfirm(window.getShell(),
							I18nUtil.getMessage("MESSAGE"),
							I18nUtil.getMessage("IS_COVER_FILE"))) {
						return null;
					}
				}

//				file.createNewFile();
				file = WorkSpaceManager.createNewFile(filePath);
				if(file == null) {
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), I18nUtil.getMessage("MESSAGE"), "创建新文件失败！");
					return null;
				}
				
				FileModel fileModel;
				try {
					fileModel = WorkSpaceManager.addFileToWorkSpace(file);
				} catch (Exception e) {
					logger.error("将设计文件添加到工作空间失败！" + e.getMessage());
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), I18nUtil.getMessage("MESSAGE"), "将设计文件添加到工作空间失败！" + e.getMessage());
					e.printStackTrace();
					return null;
				}
				
				if(fileModel == null) {
					logger.error("解析到的FileModel为空，添加该文件到工作空间失败！");
					return null;
				}
				
				DatabaseTreeViewPart databaseViewPart = (DatabaseTreeViewPart) window
						.getActivePage().findView(DatabaseTreeViewPart.ID);
				ProductTreeViewPart productTreeViewPart = (ProductTreeViewPart) window
						.getActivePage().findView(ProductTreeViewPart.ID);
				if (databaseViewPart != null) {
					databaseViewPart.addFileModel(fileModel);
				} else {
					logger.error("找不到DatabaseTreeViewPart，添加FileModel失败！");
				}

				if (productTreeViewPart != null) {
					productTreeViewPart.addFileModel(fileModel);
					DefaultViewPart.saveFileModel(fileModel);
				} else {
					logger.error("找不到ProductTreeViewPart，添加FileModel失败！");
				}

			} catch (CanNotFoundNodeIDException e) {
				logger.error("添加数据库树节点失败！" + e.getMessage());
				MessageDialog.openError(window.getShell(),
						I18nUtil.getMessage("MESSAGE"),
						"添加树节点失败！" + e.getMessage());
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
				logger.error("创建文件失败！" + e.getMessage());
				MessageDialog.openError(window.getShell(),
						I18nUtil.getMessage("MESSAGE"),
						"创建文件失败！" + e.getMessage());
				e.printStackTrace();

				return null;
			} catch (PartInitException e) {
				logger.error("创建文件失败！" + e.getMessage());
				MessageDialog.openError(window.getShell(),
						I18nUtil.getMessage("MESSAGE"),
						"创建文件失败！" + e.getMessage());
				e.printStackTrace();
			}
		}
		
		return null;
	}
	

}
