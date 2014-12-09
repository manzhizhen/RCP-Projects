package cn.sunline.suncard.sde.bs;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(true);
	}
	/**
	 * 显示控制台方法
	 * 
	 * @param layout
	 */
//	private void showConsole(IPageLayout layout) {
//		ConsoleFactory cf = new ConsoleFactory();
//		layout.addView(IConsoleConstants.ID_CONSOLE_VIEW, IPageLayout.BOTTOM,0.70f, layout.getEditorArea());
//		cf.openConsole();
//	}
 }
