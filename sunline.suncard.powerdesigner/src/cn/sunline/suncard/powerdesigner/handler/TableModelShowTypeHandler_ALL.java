/* 文件名：     TableModelShowType_P.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-27
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.handler;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.core.commands.IStateListener;
import org.eclipse.core.commands.State;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RegistryToggleState;

import cn.sunline.suncard.powerdesigner.action.DatabaseAction;
import cn.sunline.suncard.powerdesigner.gef.editpart.DatabaseDiagramModelEditPart;
import cn.sunline.suncard.powerdesigner.manager.ConfigurationFile;
import cn.sunline.suncard.powerdesigner.manager.SystemSetManager;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 表格图形显示方式-只显示主键
 * @author  Manzhizhen
 * @version 1.0, 2012-11-27
 * @see 
 * @since 1.0
 */
public class TableModelShowTypeHandler_ALL extends AbstractHandler{
	private static Log logger = LogManager.getLogger(TableModelShowTypeHandler_ALL.class);
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		changeTableModelShowType(SystemConstants.TABLE_SHOW_TYPE_ALL);
		return null;
	}
	
	/**
	 * 改变表格模型在GEF的Editor中的显示方式
	 * @param type
	 */
	public static void changeTableModelShowType(String type) {
		// 因为只能同时勾选一个，所以得去掉其他的勾选状态
		ICommandService service = (ICommandService) PlatformUI.getWorkbench()
				.getService(ICommandService.class); 
		
		Command allCommand = service.getCommand("sunline.suncard.powerdesigner" +
				".commands.showall"); 
		State allState = allCommand.getState(RegistryToggleState.STATE_ID); 
	    
		Command pCommand = service.getCommand("sunline.suncard.powerdesigner" +
				".commands.onlyshowprimarykey"); 
		State pState = pCommand.getState(RegistryToggleState.STATE_ID); 
		
		Command pfCommand = service.getCommand("sunline.suncard.powerdesigner" +
				".commands.showprimaryandforeign"); 
		State pfState = pfCommand.getState(RegistryToggleState.STATE_ID); 
		
		if(SystemConstants.TABLE_SHOW_TYPE_ALL.equals(type)) {
			allState.setValue(true);
			pState.setValue(false);
			pfState.setValue(false);
			
		} else if(SystemConstants.TABLE_SHOW_TYPE_PF.equals(type)) {
			allState.setValue(false);
			pState.setValue(false);
			pfState.setValue(true);
			
		} else if(SystemConstants.TABLE_SHOW_TYPE_P.equals(type)) {
			allState.setValue(false);
			pState.setValue(true);
			pfState.setValue(false);
		}
		
		
		service.refreshElements(allCommand.getId(), null);
		service.refreshElements(pCommand.getId(), null);
		service.refreshElements(pfCommand.getId(), null);
		
		SystemSetManager.setTableModelGefShowType(type);
		DatabaseDiagramModelEditPart.refreshEditor();
		try {
			ConfigurationFile.setProfileString(SystemConstants.TABLEMODEL_GET_SHOW_TYPE, type);
		} catch (IOException e) {
			logger.error("将新的参数保存到文件失败！！");
			e.printStackTrace();
		}
	}
	
}
