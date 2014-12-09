/* 文件名：     AddDefaultColumnHandler_Y.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-28
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import cn.sunline.suncard.powerdesigner.resource.DmConstants;

/**
 * 
 * @author  Manzhizhen
 * @version 1.0, 2012-11-28
 * @see 
 * @since 1.0
 */
public class AddDefaultColumnHandler_N extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		AddDefaultColumnHandler_Y.changeAddDefaultColumn(DmConstants.NO);
		return null;
	}

}
