package cn.sunline.suncard.powerdesigner.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.ui.dialog.ChangeDBMSDialog;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

public class SwitchCurrentDBHandler extends AbstractHandler{
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if(window == null) {
			return false;
		}
		
		IWorkbenchPage page = window.getActivePage();
		if(page == null) {
			return false;
		}
		
		IEditorPart editorPart = page.getActiveEditor();
		if(!(editorPart instanceof DatabaseDiagramEditor)) {
			MessageDialog.openConfirm(window.getShell(), 
					I18nUtil.getMessage("MESSAGE"), "无法找到活跃的Editor，无法转换数据库！");
			return false;
		}
		
		DatabaseDiagramEditor databaseDiagramEditor = (DatabaseDiagramEditor) editorPart;
		
		PhysicalDiagramModel physicalDiagramModel = databaseDiagramEditor.getPhysicalDiagramModel();
		
		ChangeDBMSDialog dialog = new ChangeDBMSDialog(databaseDiagramEditor.getSite().
				getShell());
		dialog.setPhysicalDataModel(physicalDiagramModel.getPackageModel().getPhysicalDataModel());
		dialog.open();
		return null;
	}
	
}
