package cn.sunline.suncard.powerdesigner;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import cn.sunline.suncard.powerdesigner.manager.ProjectSpaceManager;
import cn.sunline.suncard.powerdesigner.manager.WorkSpaceManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "sunline.suncard.powerdesigner.perspective"; //$NON-NLS-1$

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

    
    @Override
    public void initialize(IWorkbenchConfigurer configurer) {
    	super.initialize(configurer);
		configurer.setSaveAndRestore(true);	// 此句作用：RCP就可以记住退出时用户自定义的布局了
		// 设置圆角
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.
				SHOW_PROGRESS_ON_STARTUP, true);
    }
    
	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}
	
	@Override
	public boolean preShutdown() {
		boolean isClose =  MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), 
				I18nUtil.getMessage("MESSAGE"), 
				I18nUtil.getMessage("IS_EXIT"));
		
		if(isClose) {
			WorkSpaceManager.closeAllSpdFile();
			ProjectSpaceManager.closeAllPpdFile();
		}
			
		return isClose;
	}
}
