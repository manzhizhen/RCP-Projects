package cn.sunline.suncard.powerdesigner;

import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
//        IAction undoAction = ActionFactory.UNDO.create(window);
//        undoAction.setAccelerator(SWT.CTRL + 'Z');
//        IAction redoAction = ActionFactory.REDO.create(window);
//        redoAction.setAccelerator(SWT.CTRL + 'Y');
//        
//        register(undoAction);
//        register(redoAction);
//        
//        register(new UndoRetargetAction());
//        register(new RedoRetargetAction());
    }

    protected void fillMenuBar(IMenuManager menuBar) {
    }
    
}
