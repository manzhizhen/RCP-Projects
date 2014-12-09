package cn.sunline.suncard.powerdesigner;

import java.io.IOException;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.RegistryToggleState;
import org.eclipse.ui.internal.util.PrefUtil;

import cn.sunline.suncard.powerdesigner.manager.ConfigurationFile;
import cn.sunline.suncard.powerdesigner.manager.SystemSetManager;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProjectTreeViewPart;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {
	private static Log logger = LogManager
			.getLogger(ApplicationWorkbenchWindowAdvisor.class.getName());

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(800, 500));
		configurer.setShowCoolBar(true);
		configurer.setShowMenuBar(true);
		configurer.setShowStatusLine(true);
		configurer.setShowPerspectiveBar(true);

//		configurer.setTitle(""); //设置窗口标题
//		configurer.setShowPerspectiveBar(true); //是否显示"选择透视图"的工具栏按钮，默认false
		configurer.setShowProgressIndicator(true); //是否显示状态栏上的进度指示器，默认false
		
		// 显示新型的标签样式
		IPreferenceStore store = PrefUtil.getAPIPreferenceStore();
		store.setValue(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, false);
		store.setValue(IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR, IWorkbenchPreferenceConstants.TOP_RIGHT);
//		store.setValue(IWorkbenchPreferenceConstants.SHOW_PROGRESS_ON_STARTUP, true);
	}

	@Override
	public void postWindowOpen() {
		super.postWindowOpen();
		// 打开两颗树
//		IWorkbenchPage page = PlatformUI.getWorkbench()
//				.getActiveWorkbenchWindow().getActivePage();
//		if (page != null) {
//			try {
//				IViewPart viewPart = page.showView(DatabaseTreeViewPart.ID);
//				page.showView(ProductTreeViewPart.ID);
//				page.showView(ProjectTreeViewPart.ID);
//				
//				if(viewPart != null) {
//					page.activate(viewPart);
//				}
//			} catch (PartInitException e) {
//				logger.error("打开数据库树或产品树或项目树失败！" + e.getMessage());
//				e.printStackTrace();
//			}
//		}
		
		try {
			IViewPart viewPart = DatabaseTreeViewPart.getInstance();
			ProductTreeViewPart.getInstance();
			ProjectTreeViewPart.getInstance();
			
			IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			if (page != null) {
				page.activate(viewPart);
			}
		} catch (PartInitException e1) {
			logger.error("打开数据库树或产品树或项目树失败！" + e1.getMessage());
			e1.printStackTrace();
		}
		
		
		
		// 读取系统级的参数
		try {
			SystemSetManager.setTableModelGefShowType(ConfigurationFile.getProfileString(
					SystemConstants.TABLEMODEL_GET_SHOW_TYPE, SystemConstants
					.TABLE_SHOW_TYPE_ALL));
			
			SystemSetManager.setIsAddDefaultColumns(ConfigurationFile.getProfileString(SystemConstants.ADD_DEFAULT_COLUMNS, DmConstants.NO));
			
			ICommandService service = (ICommandService) PlatformUI.getWorkbench()
					.getService(ICommandService.class); 
			
			// 设置表格模型的显示方式
			Command command = null;
			if(SystemConstants.TABLE_SHOW_TYPE_ALL.equals(SystemSetManager.getTableModelGefShowType())) {
				command = service.getCommand("sunline.suncard.powerdesigner" +
					".commands.showall"); 
				
			} else if(SystemConstants.TABLE_SHOW_TYPE_PF.equals(SystemSetManager.getTableModelGefShowType())) {
				command = service.getCommand("sunline.suncard.powerdesigner" +
						".commands.showprimaryandforeign"); 
				
			} else if(SystemConstants.TABLE_SHOW_TYPE_P.equals(SystemSetManager.getTableModelGefShowType())) {
				command = service.getCommand("sunline.suncard.powerdesigner" +
						".commands.onlyshowprimarykey"); 
			}

			if(command != null) {
				State state = command.getState(RegistryToggleState.STATE_ID); 
				if(state != null) {
					state.setValue(true);
				}
			}
			
			// 设置是否添加默认列
			command = null;
			if(DmConstants.YES.equals(SystemSetManager.getIsAddDefaultColumns())) {
				command = service.getCommand("sunline.suncard.powerdesigner" +
					".commands.addefaultcolumn"); 
				
			} else if(DmConstants.NO.equals(SystemSetManager.getIsAddDefaultColumns())) {
				command = service.getCommand("sunline.suncard.powerdesigner" +
						".commands.notaddefaultcolumn"); 
				
			} 
			
			if(command != null) {
				State state = command.getState(RegistryToggleState.STATE_ID); 
				if(state != null) {
					state.setValue(true);
				}
			}
		} catch (IOException e) {
			logger.error("将新的参数保存到文件失败！！");
			e.printStackTrace();
		}
	}
}
