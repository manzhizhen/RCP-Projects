package cn.sunline.suncard.powerdesigner.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.action.DatabaseAction;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;

public class NewViewHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DatabaseTreeViewPart databaseTreeViewPart = (DatabaseTreeViewPart) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(DatabaseTreeViewPart.ID);
		
		if(databaseTreeViewPart == null) {
			return null;
		}
		
		TreeContent treeContent = databaseTreeViewPart.getSelection();
		if(treeContent != null) {
			new DatabaseAction(DmConstants.ADD_PHYSICAL_DIAGRAM_FLAG, 
					databaseTreeViewPart.getTreeViewer()).run();
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		DatabaseTreeViewPart databaseTreeViewPart = (DatabaseTreeViewPart) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(DatabaseTreeViewPart.ID);
		
		return databaseTreeViewPart != null;
	}

}
