/* 文件名：     ScoreConnectionEditPolicy.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2011-11-15
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.policy;


import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import cn.sunline.suncard.powerdesigner.gef.command.DeleteLineCommand;


/**
 * 连接线的删除政策
 * @author  易振强
 * @version 1.0, 2011-11-18
 * @see 
 * @since 1.0
 */
public class PDLinkDelPolicy extends ConnectionEditPolicy {

	@Override
	protected Command getDeleteCommand(GroupRequest request) {
		// 删除连接线的Command
		DeleteLineCommand command = new DeleteLineCommand();
		command.setConnectionModel(getHost().getModel());
		return command;
	}

	
	
}
