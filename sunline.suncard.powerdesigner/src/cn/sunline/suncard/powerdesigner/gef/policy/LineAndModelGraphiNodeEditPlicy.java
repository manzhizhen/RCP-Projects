/* 文件名：     LabelModelDirectEditPolicy.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	LineAndModelGraphiNodeEditPlicy
 * 修改人：     易振强
 * 修改时间：2011-12-8
 * 修改内容：创     建
 */
package cn.sunline.suncard.powerdesigner.gef.policy;


import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.DropRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import cn.sunline.suncard.powerdesigner.gef.command.CreateLineCommand;
import cn.sunline.suncard.powerdesigner.gef.command.ReconnectLineCommand;



/**
 * 建立连接线和模型对接的策略
 * 
 * @author 易振强
 * @version [1.0, 2011-11-11]
 * @see
 * @since [1.0]
 */
public class LineAndModelGraphiNodeEditPlicy extends GraphicalNodeEditPolicy {
	Point p1;
	Point p2;
	
	
	@Override
	protected Command getConnectionCompleteCommand(
			CreateConnectionRequest request) {
		//从请求得到创建连接命令
		CreateLineCommand command = (CreateLineCommand) request.getStartCommand();
		//得到模型,设为目标结点
		command.setTarget(getHost().getModel());
		return command;
	}

	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		// 新建建立连接命令
		CreateLineCommand command = new CreateLineCommand();
		// 得到连接模型
		command.setConnection(request.getNewObject());
		// 得到模型,设为源结点
		command.setSource(getHost().getModel());
		
		// 记录创建连接的记录
		request.setStartCommand(command);
		return command;
	}

	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		ReconnectLineCommand command = new ReconnectLineCommand();
		command.setNewTarget(getHost().getModel());
		command.setConnectionModel(request.getConnectionEditPart().getModel());
		command.setFlag(ReconnectLineCommand.END);
		
		return command; 
	}

	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		ReconnectLineCommand command = new ReconnectLineCommand();
		command.setNewSource(getHost().getModel()); 
		command.setConnectionModel(request.getConnectionEditPart().getModel());
		command.setFlag(ReconnectLineCommand.START);
		
		return command; 
	}
	
	@Override
	protected ConnectionAnchor getSourceConnectionAnchor(
			CreateConnectionRequest request) {
		return super.getSourceConnectionAnchor(request);
	}

	@Override
	protected ConnectionAnchor getTargetConnectionAnchor(
			CreateConnectionRequest request) {
		
		return super.getTargetConnectionAnchor(request);
	}

}
