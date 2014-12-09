/*
 * 文件名：PermissionSetHandler.java
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.ui.views.PermissionSetViewPart;


/**
 *  Handler所有类，所有动作的操作类“权限设置”的Handler
 * @author    周兵
 * @version   1.0 2001-09-21
 * @see       
 * @since     1.0 
 */
public class PermissionSetHandler extends AbstractHandler {
	Log log = LogManager.getLogger(PermissionSetHandler.class.getName());
	
	/**
	 * handler类执行方法
	 * @param ExcutionEvent event
	 * @return Object 
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPage  page=HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			page.showView(PermissionSetViewPart.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
			log.info("权限设置"+e.getMessage());
		}
		return null;
	}

}
