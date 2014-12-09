/* 文件名：     DelModelEditPolicy.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	删除模型的策略
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.policy;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import cn.sunline.suncard.sde.workflow.gef.command.DelModelCommand;


/**
 * 删除模型的策略
 * @author    易振强
 * @version   [1.0, 2011-11-04]
 * @see       
 * @since     1.0 
 */
public class DelModelEditPolicy extends ComponentEditPolicy {
	//重载createDeleteCommand
	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		//调用deleteCommand
		DelModelCommand deleteCommand = new DelModelCommand();
		//注意这类的getHost方法得到的是ModuleModel的EditPart，这是因为这个
		//ScoreEditPolicy要安装到ModuleModel对应的ModuleModel中
		deleteCommand.setWorkFlowModel(getHost().getParent().getModel());
		deleteCommand.setModel(getHost().getModel());
		
		return deleteCommand;
	}

	@Override
	public Command getCommand(Request request) {
		return super.getCommand(request);
	}

	@Override
	protected Command getDeleteCommand(GroupRequest request) {
		return super.getDeleteCommand(request);
	}

	

}
