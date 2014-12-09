/* 文件名：     MoveBendpointCommand.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	移动连接线控制点的Command
 * 修改人：     易振强
 * 修改时间：2011-11-18
 * 修改内容：创     建
 */
package cn.sunline.suncard.powerdesigner.gef.command;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import cn.sunline.suncard.powerdesigner.gef.model.AbstractLineGefModel;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;



/**
 * 移动连接线控制点的Command
 * @author 	易振强
 * @version [1.0, 2011-11-01]
 * @see
 * @since 1.0
 */
public class MoveBendpointCommand extends Command {
	private AbstractLineGefModel connection;
	
	// 旧坐标
	private Point oldLocation;
	
	// 新坐标
	private Point newLocation;
	
	private int index;

	public void execute() {
		oldLocation = (Point) connection.getBendpoints().get(index);
		connection.replaceBendpoint(index, newLocation);
	}

	public void setConnection(Object model) {
		this.connection = (AbstractLineGefModel) model;
	}

	public void setIndex(int i) {
		this.index = i;
	}

	public void setNewLocation(Point newLoc) {
		this.newLocation = newLoc;
	}

	public void undo() {
		connection.replaceBendpoint(index, oldLocation);
	}
}
