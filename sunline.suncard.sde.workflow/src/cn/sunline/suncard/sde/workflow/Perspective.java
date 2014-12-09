package cn.sunline.suncard.sde.workflow;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;


public class Perspective implements IPerspectiveFactory {
	public final static String ID = "sunline.suncard.sde.workflow.perspective";
	public final static String properties = "org.eclipse.ui.views.PropertySheet";
	public final static String outline = "org.eclipse.ui.views.ContentOutline";
	public final static String debug = "cn.sunline.suncard.sde.dm.ui.scorecard.debug.DebugViewPart";
	
	// 数据改变标记，如果在评分卡策略的调色板中的六大对象有过数据库的删除或新增，则把该标记置为true
	public static boolean isDataChange = false;
	
	@Override
	public void createInitialLayout(IPageLayout layout) {

//		final String editorArea = layout.getEditorArea();
//		
//		//添加属性列表视图到透视图中
//		IFolderLayout leftTopFolder = layout.createFolder("LeftTop", IPageLayout.LEFT, 0.34f, editorArea);
//		leftTopFolder.addView(properties);
//		
//		//添加Outline视图到透视图中
//		IFolderLayout rightBottomFolder = layout.createFolder("LeftBottom", IPageLayout.BOTTOM, 0.34f, properties);
//		rightBottomFolder.addView(outline);
//		
//		IFolderLayout topBottomFolder = layout.createFolder("TopBottom", IPageLayout.TOP, 0.34f, properties);
//		topBottomFolder.addView(debug);

//		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//		if(page != null) {
//			IViewReference[] viewPerference = page.getViewReferences();
//			for(IViewReference viewRef : viewPerference) {
//				page.hideView(viewRef);
//			}
//		}
//		
		layout.setEditorAreaVisible(true);
	}
	
}
