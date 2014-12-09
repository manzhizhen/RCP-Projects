/* 文件名：     DeleteBendpointCommand.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	删除连接线控制点的Command
 * 修改人：     易振强
 * 修改时间：2011-11-18
 * 修改内容：创     建
 */
package cn.sunline.suncard.sde.workflow.gef.command;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import cn.sunline.suncard.sde.workflow.gef.model.AbstractLineModel;


/**
 * 文件名：    DeleteBendpointCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	删除连接线控制点的Command
 * 修改人：     易振强
 * 修改时间：2011-11-21
 * 修改内容：创     建
 */
public class DeleteBendpointCommand extends Command {
	private AbstractLineModel connection;
	
	// bend 点的位置
	private Point oldLocation;
	
	// bend点的索引
	private int index;

	public void execute() {
		oldLocation = (Point) connection.getBendpoints().get(index);
		connection.removeBendpoint(index);
	}

	public void setConnectionModel(Object model) {
		connection = (AbstractLineModel) model;
	}

	public void setIndex(int i) {
		index = i;
	}

	public void undo() {
		connection.addBendpoint(index, oldLocation);
	}
}
