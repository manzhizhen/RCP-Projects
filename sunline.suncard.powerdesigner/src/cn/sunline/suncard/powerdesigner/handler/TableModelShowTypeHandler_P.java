/* 文件名：     TableModelShowType_P.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-27
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.RegistryToggleState;

import cn.sunline.suncard.powerdesigner.gef.editpart.DatabaseDiagramModelEditPart;
import cn.sunline.suncard.powerdesigner.manager.SystemSetManager;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;

/**
 * 表格图形显示方式-只显示主键
 * @author  Manzhizhen
 * @version 1.0, 2012-11-27
 * @see 
 * @since 1.0
 */
public class TableModelShowTypeHandler_P extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		TableModelShowTypeHandler_ALL.changeTableModelShowType(SystemConstants
				.TABLE_SHOW_TYPE_P);
		
		return null;
	}

}
