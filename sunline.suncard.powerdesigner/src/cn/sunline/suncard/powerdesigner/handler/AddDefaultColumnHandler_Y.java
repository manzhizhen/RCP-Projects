/* 文件名：     AddDefaultColumnHandler_Y.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-28
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.handler;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.RegistryToggleState;

import cn.sunline.suncard.powerdesigner.gef.editpart.DatabaseDiagramModelEditPart;
import cn.sunline.suncard.powerdesigner.manager.ConfigurationFile;
import cn.sunline.suncard.powerdesigner.manager.SystemSetManager;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 
 * @author  Manzhizhen
 * @version 1.0, 2012-11-28
 * @see 
 * @since 1.0
 */
public class AddDefaultColumnHandler_Y extends AbstractHandler{
	private static Log logger = LogManager.getLogger(AddDefaultColumnHandler_Y.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		changeAddDefaultColumn(DmConstants.YES);
		
		
		return null;
	}
	
	/**
	 * 创建表格时是否添加默认列
	 * @param type
	 */
	public static void changeAddDefaultColumn(String type) {
		// 因为只能同时勾选一个，所以得去掉其他的勾选状态
		ICommandService service = (ICommandService) PlatformUI.getWorkbench()
				.getService(ICommandService.class); 
		
		Command addCommand = service.getCommand("sunline.suncard.powerdesigner" +
				".commands.addefaultcolumn"); 
		State addState = addCommand.getState(RegistryToggleState.STATE_ID); 
	    
		Command notAddCommand = service.getCommand("sunline.suncard.powerdesigner" +
				".commands.notaddefaultcolumn"); 
		State notAddState = notAddCommand.getState(RegistryToggleState.STATE_ID); 
		
		
		if(DmConstants.YES.equals(type)) {
			addState.setValue(true);
			notAddState.setValue(false);
			
		} else if(DmConstants.NO.equals(type)) {
			addState.setValue(false);
			notAddState.setValue(true);
		} 
		
		service.refreshElements(addCommand.getId(), null);
		service.refreshElements(notAddCommand.getId(), null);
		
		SystemSetManager.setIsAddDefaultColumns(type);
		try {
			ConfigurationFile.setProfileString(SystemConstants.ADD_DEFAULT_COLUMNS, type);
		} catch (IOException e) {
			logger.error("将新的参数保存到文件失败！！");
			e.printStackTrace();
		}
	}

}
