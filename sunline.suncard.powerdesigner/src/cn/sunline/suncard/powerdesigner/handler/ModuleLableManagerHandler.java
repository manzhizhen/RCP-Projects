package cn.sunline.suncard.powerdesigner.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.powerdesigner.ui.dialog.ModuleLabelManagerDialog;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

public class ModuleLableManagerHandler extends AbstractHandler{
	private Log logger = LogManager.getLogger(ModuleLableManagerHandler.class
			.getName());
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if(window == null ) {
			logger.error("找不到活跃的WorkbenchWindow，ModuleLableManagerHandler执行失败！");
			return null;
		}
		
		ModuleLabelManagerDialog dialog = new ModuleLabelManagerDialog(window.getShell());
		dialog.open();
		
		return null;
	}
	
//	@Override
//	public boolean isEnabled() {
//		DatabaseTreeViewPart databaseTreeViewPart = (DatabaseTreeViewPart) PlatformUI
//				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
//				.findView(DatabaseTreeViewPart.ID);
//		
//		if(databaseTreeViewPart == null) {
//			return false;
//		}
//		
//		TreeContent treeContent = databaseTreeViewPart.getSelection();
//		if(treeContent == null) {
//			return false;
//		}
//		
//		fileModel = FileModel.getFileModelFromObj(treeContent.getObj());
//		
//		if(fileModel == null) {
//			return false;
//		}
//		
//		return true;
//		
//	}

}
