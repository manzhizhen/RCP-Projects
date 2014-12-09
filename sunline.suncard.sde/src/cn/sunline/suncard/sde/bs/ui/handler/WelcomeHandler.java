/*
 * 文件名：
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：tpf
 * 修改时间：2011-12-31
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;

import cn.sunline.suncard.sde.bs.ApplicationWorkbenchWindowAdvisor;

public class WelcomeHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchConfigurer wbConfig = ApplicationWorkbenchWindowAdvisor.configurer;
//		IWorkbenchConfigurer wbConfig = getWindowConfigurer().getWorkbenchConfigurer();
		System.out.println("run welcome handler, config = " + wbConfig);

//		final String key = "introOpened"; //$NON-NLS-1$
//		Boolean introOpened = (Boolean) wbConfig.getData(key);
//		if (introOpened != null && introOpened.booleanValue()) {
//			return null;
//		}
//
//		wbConfig.setData(key, Boolean.TRUE);
		
		if (wbConfig.getWorkbench().getIntroManager().hasIntro()) {
			wbConfig.getWorkbench().getIntroManager().showIntro(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), false);
			
			IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
			for(IWorkbenchWindow iWorkbenchWindow : workbenchWindows) {
				wbConfig.getWorkbench().getIntroManager().showIntro(iWorkbenchWindow, false);
			}
		}
		return null;
	}
}
