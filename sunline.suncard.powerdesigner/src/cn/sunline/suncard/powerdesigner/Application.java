package cn.sunline.suncard.powerdesigner;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.db.DatabaseGeneration;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.system.SystemInit;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	public Object start(IApplicationContext context) throws Exception {
		
		Display display = PlatformUI.createDisplay();
		try {
			// 下面两行是对baseplugin插件中的数据进行一些初始化操作。
			new SystemInit().init();
			Context.getInstance().fireLogonEvent();
			
			// 初始化生成数据库生成的设置
			DatabaseGeneration.init();
			
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART)
				return IApplication.EXIT_RESTART;
			else
				return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
		
	}

	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
}
