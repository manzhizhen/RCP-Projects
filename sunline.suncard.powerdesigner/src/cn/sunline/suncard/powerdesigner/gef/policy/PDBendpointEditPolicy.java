/* 文件名：     ScoreBendpointEditPolicy.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2011-11-18
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.policy;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.BendpointEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.requests.BendpointRequest;

import cn.sunline.suncard.powerdesigner.gef.command.CreateBendpointCommand;
import cn.sunline.suncard.powerdesigner.gef.command.DeleteBendpointCommand;
import cn.sunline.suncard.powerdesigner.gef.command.MoveBendpointCommand;


/**
 * 连接线节点的政策
 * @author  易振强
 * @version 1.0, 2011-11-18
 * @see 
 * @since 1.0
 */
public class PDBendpointEditPolicy extends BendpointEditPolicy {

	@Override
	protected Command getCreateBendpointCommand(BendpointRequest request) {
		// 获得增加 bend 点的位置
		Point point = request.getLocation();
		getConnection().translateToRelative(point);

		CreateBendpointCommand command = new CreateBendpointCommand();
		command.setLocation(point);
		command.setConnection(getHost().getModel());
		command.setIndex(request.getIndex());

		return command;
	}

	@Override
	protected Command getDeleteBendpointCommand(BendpointRequest request) {
		DeleteBendpointCommand command = new DeleteBendpointCommand();
		command.setConnectionModel(getHost().getModel());
		command.setIndex(request.getIndex());
		
		return command;
	}

	@Override
	protected Command getMoveBendpointCommand(BendpointRequest request) {
		// 获得增加bend点的位置
		Point location = request.getLocation();
		getConnection().translateToRelative(location);

		MoveBendpointCommand command = new MoveBendpointCommand();
		command.setConnection(getHost().getModel());
		command.setIndex(request.getIndex());
		command.setNewLocation(location);

		return command;
	}
	
	
}
