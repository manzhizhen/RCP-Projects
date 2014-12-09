package cn.sunline.suncard.sde.bs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import cn.sunline.suncard.sde.bs.util.I18nUtil;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "sunline.card.sde.perspective"; //$NON-NLS-1$

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
    	
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		configurer.setSaveAndRestore(true);	// 此句作用：RCP就可以记住退出时用户自定义的布局了
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.SHOW_PROGRESS_ON_STARTUP, true);
	}
	@Override
	public boolean preShutdown() {
		return MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), 
				I18nUtil.getMessage("titleInfo"), 
				I18nUtil.getMessage("confirmExit"));
	}
}
