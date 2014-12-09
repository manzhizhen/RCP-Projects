package cn.sunline.suncard.sde.bs;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.util.PrefUtil;

import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

@SuppressWarnings("restriction")
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {
	
	public static IWorkbenchConfigurer configurer;

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    public void preWindowOpen() {
    	IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
    	//Rectangle screenSize = Display.getDefault().getClientArea(); //获取屏幕大小
    	//configurer.setInitialSize(new Point(screenSize.width, screenSize.height));  //初始窗口大小
        configurer.setShowCoolBar(true);				 //是否显示工具栏，默认true
        configurer.setShowStatusLine(true);				 //是否显示状态栏，默认true
        configurer.setTitle(I18nUtil.getMessage("title"));		 //设置窗口标题
        configurer.setShowPerspectiveBar(true);			 //是否显示"选择透视图"的工具栏按钮，默认false
        configurer.setShowProgressIndicator(true);		 //是否显示状态栏上的进度指示器，默认false
        configurer.setShowMenuBar(true);				 //是否显示主菜单，默认true
        //configurer.setShellStyle(SWT.MIN | SWT.CLOSE);   //设置窗口只能最小化或关闭
        configurer.setShowProgressIndicator(true);			//显示job进度条
        
		IPreferenceStore store = PrefUtil.getAPIPreferenceStore();
        store.setValue(IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR, IWorkbenchPreferenceConstants.TOP_RIGHT);
        store.setValue(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, false);
        store.setValue(IWorkbenchPreferenceConstants.SHOW_PROGRESS_ON_STARTUP, true);
    }

	@Override
	public void postWindowOpen() {
		super.postWindowOpen();
		IStatusLineManager statusLine = getWindowConfigurer().getActionBarConfigurer().getStatusLineManager();
		statusLine.setMessage("Powered by sunline"); //设置状态栏信息图片和文字
	}

	/**
	 * 这个方法在窗口恢复到以前保存的状态(或者新建一个窗口)之后, 
	 * 打开窗口之前(调用). 
	 */
	@Override
	public void postWindowCreate() {
		super.postWindowCreate();
		getWindowConfigurer().getWindow().getShell().setMaximized(true);   //设置打开时最大化窗口 
	}

	@Override
	public void dispose() {
		super.dispose();
		CacheImage.getCacheImage().dispose();
	}
	
	@Override
	public void openIntro() {
		IWorkbenchConfigurer wbConfig = getWindowConfigurer().getWorkbenchConfigurer();
		this.configurer = wbConfig;
		final String key = "introOpened"; //$NON-NLS-1$
		Boolean introOpened = (Boolean) wbConfig.getData(key);
		if (introOpened != null && introOpened.booleanValue()) {
			return;
		}

		wbConfig.setData(key, Boolean.TRUE);
//
//		boolean showIntro = PrefUtil.getAPIPreferenceStore().getBoolean(
//				IWorkbenchPreferenceConstants.SHOW_INTRO);
//
//		if (!showIntro) {
//			return;
//		}

//		
//		// 得到当前元素的目录
//        Bundle bundle = Platform.getBundle("sunline.suncard.sde.dm");

//        bundle.getBundleContext().
		if (wbConfig.getWorkbench().getIntroManager().hasIntro()) {
			wbConfig.getWorkbench().getIntroManager().showIntro(getWindowConfigurer().getWindow(), false);
			
			IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
			for(IWorkbenchWindow iWorkbenchWindow : workbenchWindows) {
				wbConfig.getWorkbench().getIntroManager().showIntro(iWorkbenchWindow, false);
			}
			
//			IIntroManager introManager = wbConfig.getWorkbench().getIntroManager();
//			introManager.
			
			// 设置下次启动程序时时否打开欢迎页面
			PrefUtil.getAPIPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_INTRO, 
					false);

			PrefUtil.saveAPIPrefs();
		}
	}
}
