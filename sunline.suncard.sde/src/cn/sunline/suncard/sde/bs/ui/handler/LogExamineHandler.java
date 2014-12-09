/*
 * 文件名：LogExamineHandler.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述： Handler所有类，所有动作的操作类
 * 修改人： 周兵
 * 修改时间：2001-09-21
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.bs.ui.handler;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.ui.plugin.logmanage.PluginLogManage;
/**
 *  Handler所有类，所有动作的操作类
 *  “日志查看”的Handler
 * @author    周兵
 * @version   1.0 2001-09-21
 * @see       
 * @since     1.0 
 */
public class LogExamineHandler extends AbstractHandler {
	Log log = LogManager.getLogger(LogExamineHandler.class.getName());

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
//		Desktop desktop = Desktop.getDesktop();
//		File file = new File("plguinlog/pluginInfoLog.log");
//		try {
//			desktop.open(file);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
		PluginLogManage pluginLogManage = new PluginLogManage(page.getWorkbenchWindow().getShell());
		pluginLogManage.open();
		log.info("日志查看");
		return null;
	}

}
