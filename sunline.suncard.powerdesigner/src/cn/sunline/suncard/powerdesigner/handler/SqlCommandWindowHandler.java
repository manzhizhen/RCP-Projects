/* 文件名：     SqlCommandWindowHandler.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-19
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.sql.editor.SqlEditorInput;
import cn.sunline.suncard.powerdesigner.sql.editor.SqlExecuteEditor;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * SQL命令窗口
 * @author  Manzhizhen
 * @version 1.0, 2012-12-19
 * @see 
 * @since 1.0
 */
public class SqlCommandWindowHandler extends AbstractHandler{
	private Log logger = LogManager.getLogger(SqlCommandWindowHandler.class.getName());
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.openEditor(new SqlEditorInput(), SqlExecuteEditor.ID);
		} catch (PartInitException e) {
			logger.debug("打开Sql命令窗口失败！" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
