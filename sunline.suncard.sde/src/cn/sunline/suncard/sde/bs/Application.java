package cn.sunline.suncard.sde.bs;

import java.net.UnknownHostException;
import java.util.List;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.sde.bs.biz.BsPcBiz;
import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsPc;
import cn.sunline.suncard.sde.bs.entity.BsPcId;
import cn.sunline.suncard.sde.bs.system.ComputerInfo;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.system.InitPermissionData;
import cn.sunline.suncard.sde.bs.system.SystemInit;
import cn.sunline.suncard.sde.bs.ui.dailogs.LoginDialog;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
		Display display = PlatformUI.createDisplay();
		try {
			//Context.getSessionMap().put(Constants.BANKORG_ID, 2001L);
			new SystemInit().init();
			Context.getInstance().fireLogonEvent();
			
			BsPcBiz bsPcBiz = new BsPcBiz();
			List<BsPc> bsPcs = bsPcBiz.getAll();
			if ((bsPcs == null) || (bsPcs.size() == 0)){
//				MainInstallShell shell = new MainInstallShell(display);
//				shell.open();
//				shell.initShell();
//				shell.layout();
//				
//				while (!shell.isDisposed()) {
//					if (!display.readAndDispatch()) {
//						display.sleep();
//					}
//				}
//				return IApplication.EXIT_OK;
				InitPermissionData.initData();	// 初始化信息
				insertPcInfo();					// 初始化PC信息
			}
			
			LoginDialog login = new LoginDialog(null);
			if(login.open() != Window.OK) {
				return IApplication.EXIT_OK;
			}
			
			int returnCode = PlatformUI.createAndRunWorkbench(display, 
					new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART)
				return IApplication.EXIT_RESTART;
			else
				return IApplication.EXIT_OK;
		}finally {
			display.dispose();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
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
	
	/**
	 * 将PC信息插入数据库
	 * @throws UnknownHostException 
	 */
	private void insertPcInfo() throws UnknownHostException {
		//初始化hibernate配置文件信息
//		ParseHibernateConfig.parseHibernateCfg(SystemParameters.getUserDir()+"/"+SystemParameters.getHibernateXmlPath(), SystemParameters.getUserDir()+"/"+SystemParameters.getHibernateMappingPath());
		
		BsPc bsPc = new BsPc(new BsPcId());
		bsPc.getId().setBankorgId(Long.parseLong(Context.getSessionMap().get(Constants.BANKORG_ID).toString()));
		bsPc.getId().setPcId(ComputerInfo.getHostName());
		bsPc.setPcName(ComputerInfo.getHostName());
		bsPc.setIpAddr(ComputerInfo.getIpAddress());
		BsPcBiz bsPcBiz = new BsPcBiz();
		bsPcBiz.insert(bsPc);
	}
}
