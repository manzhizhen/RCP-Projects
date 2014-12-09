package cn.sunline.suncard.powerdesigner.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.ui.dialog.DatabaseGenerationWizardDialog;
import cn.sunline.suncard.powerdesigner.wizard.DatabaseGenerationWizard;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;


public class CreateDBHandler extends AbstractHandler{
	private Log logger = LogManager.getLogger(NewHandler.class.getName());
	private DatabaseGenerationWizardDialog wizardDialog;
	private DatabaseGenerationWizard wizard;
	@Override
	public Object execute(ExecutionEvent event) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		
		if(page == null){
			logger.error("当前无活跃的Editor！");
			MessageDialog.openInformation(window.getShell(), I18nUtil.getMessage("MESSAGE")
					, "当前无活跃的Editor，无法打开生成数据库的对话框!");
			return null;
		}
		
		IEditorPart editorPart = page.getActiveEditor();
		if(editorPart == null){
			logger.error("当前无活跃的Editor！");
			MessageDialog.openInformation(window.getShell(), I18nUtil.getMessage("MESSAGE")
					, "当前无活跃的EditorPart，无法打开生成数据库的对话框!");
			return null;
		}
		
		if(editorPart instanceof DatabaseDiagramEditor) {
			 DatabaseDiagramEditor databaseDiagramEditor = (DatabaseDiagramEditor) editorPart;
			 
//			 DatabaseGenerationWizardDialog dialog = new DatabaseGenerationWizardDialog(window.getShell(), null);
//			 dialog.setPhysicalDiagramModel(databaseDiagramEditor.getPhysicalDiagramModel());
			  wizard = new DatabaseGenerationWizard(databaseDiagramEditor.getPhysicalDiagramModel());
			
			  wizardDialog = new DatabaseGenerationWizardDialog(window.getShell(), wizard);
			
			  wizardDialog.open();
		 }

		return null;
	}
	
}
