/* 文件名：     ScoreConnectionEndpointEditPolicy.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	评分卡的连接线控制点修改的政策
 * 修改人：     易振强
 * 修改时间：2011-11-15
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.policy;

import org.eclipse.draw2d.Connection;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;

import cn.sunline.suncard.sde.workflow.gef.command.SelectionConnectionCommand;



/**
 * 评分卡的连接线控制点修改的政策
 * @author  易振强
 * @version 1.0, 2011-11-18
 * @see 
 * @since 1.0
 */
public class WFlowConnectionEndpointEditPolicy extends
		ConnectionEndpointEditPolicy {
	
	@Override
	public Command getCommand(Request request) {
		SelectionConnectionCommand selectionCommand = new SelectionConnectionCommand();
		return selectionCommand;
	}
}
