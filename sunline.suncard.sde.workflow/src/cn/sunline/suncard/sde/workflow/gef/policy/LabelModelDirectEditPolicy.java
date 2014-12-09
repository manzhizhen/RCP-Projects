/* 文件名：     LabelModelDirectEditPolicy.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	LabelModel的直接编辑策略
 * 修改人：     易振强
 * 修改时间：2011-12-8
 * 修改内容：创     建
 */
package cn.sunline.suncard.sde.workflow.gef.policy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

import cn.sunline.suncard.sde.workflow.gef.command.LabelModelDirectEditCommand;

public class LabelModelDirectEditPolicy extends DirectEditPolicy{

	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) {
		LabelModelDirectEditCommand command = new LabelModelDirectEditCommand();
		command.setModel(getHost().getModel());
		//从cell editor中得到newText来给Figure设置文本
		command.setText((String)request.getCellEditor().getValue());
		return command;
	}

	@Override
	protected void showCurrentEditValue(DirectEditRequest request) {
		
	}

}
