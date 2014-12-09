/* 文件名：     ScoreCardConnectionCreationTool.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-2-22
 * 修改内容：创     建
 */
package cn.sunline.suncard.sde.workflow.gef.editpart;

import org.eclipse.gef.tools.ConnectionCreationTool;

/**
 * 重写连接的创建工具
 * @author  易振强
 * @version 1.0, 2012-2-22
 * @see 
 * @since 1.0
 */
public class WorkFlowConnectionCreationTool extends ConnectionCreationTool{
	@Override
	protected boolean handleButtonDown(int button) {
        boolean result = super.handleButtonDown(button);
        if(button == 1 && !isInState(64) && isInState(1))
            getDomain().loadDefaultTool();
        return result;
	}
}
