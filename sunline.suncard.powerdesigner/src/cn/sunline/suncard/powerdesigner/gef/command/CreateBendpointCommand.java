/* 文件名：     CreateBendpointCommand.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	创建连接线控制点的Command
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
 * 创建连接线控制点的Command
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 */
public class CreateBendpointCommand extends Command {
	private AbstractLineGefModel connection;
	
	// bend 点的位置
	private Point location; 
	
	// bend点的索引
	private int index; 

	public void execute() {
		connection.addBendpoint(index, location);
	}

	public void setConnection(Object object) {
		this.connection = (AbstractLineGefModel) object;
	}

	/**
	 * 设置控制点的位置，即连接线的第几个控制点
	 * @param  int
	 */
	public void setIndex(int i) {
		// 增加的 bend点所在的位置
		this.index = i;
	}

	/**
	 * 设置控制点的坐标
	 * @param  Point
	 */
	public void setLocation(Point loc) {
		this.location = loc;
	}

	public void undo() {
		connection.removeBendpoint(index);
	}
	
	@Override
	public void redo() {
		connection.addBendpoint(index, location);
		super.redo();
	}
}
