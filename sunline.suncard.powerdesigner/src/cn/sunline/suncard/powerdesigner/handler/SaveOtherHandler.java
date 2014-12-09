package cn.sunline.suncard.powerdesigner.handler;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.action.DatabaseAction;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

public class SaveOtherHandler extends AbstractHandler{

	private Log logger = LogManager.getLogger(SaveOtherHandler.class.getName());
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DatabaseTreeViewPart databaseTreeViewPart = (DatabaseTreeViewPart) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(DatabaseTreeViewPart.ID);
		
		if(databaseTreeViewPart == null) {
			return null;
		}
		
		TreeContent treeContent = databaseTreeViewPart.getSelection();
		
		FileModel fileModel = FileModel.getFileModelFromObj(treeContent.getObj());
		
		if(fileModel == null) {
			logger.error("没用选中的FileModel，SaveOtherHandler执行失败！");
			return null;
		}
		
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if(window == null) {
			logger.error("找不到活跃的WorkbenchWindow，SaveOtherHandler执行失败！");
			return null;
		}
		
		FileDialog fileDialog = new FileDialog(window.getShell(), SWT.SAVE);
		fileDialog.setFilterExtensions(new String[]{"*." + SystemConstants.ZIP_FILE_EXTEND_NAME});
		fileDialog.setText("另存为数据库设计文件");
		
		String filePath = fileDialog.open();
		
		if(filePath != null) {
			File file = new File(filePath);
			if(file.exists())  {
				if(!MessageDialog.openConfirm(window.getShell(), I18nUtil.getMessage("MESSAGE")
						, I18nUtil.getMessage("IS_COVER_FILE"))) {
					return null;
				}
			}
			
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error("创建新文件失败！" + e.getMessage());
				MessageDialog.openError(window.getShell(), I18nUtil.getMessage("MESSAGE")
						, I18nUtil.getMessage("CREATE_FILE_FAIL"));
				e.printStackTrace();
				
				return null;
			}
			
			fileModel.setFile(file);
			DatabaseTreeViewPart.saveFileModel(fileModel);
			databaseTreeViewPart.refreshTree();
		}
		
		return null;
	}

}
