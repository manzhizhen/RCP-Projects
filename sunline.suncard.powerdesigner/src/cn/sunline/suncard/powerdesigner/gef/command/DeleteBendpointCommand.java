/* 文件名：     DeleteBendpointCommand.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	删除连接线控制点的Command
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
 * 文件名：    DeleteBendpointCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	删除连接线控制点的Command
 * 修改人：     易振强
 * 修改时间：2011-11-21
 * 修改内容：创     建
 */
public class DeleteBendpointCommand extends Command {
	private AbstractLineGefModel connection;
	
	// bend 点的位置
	private Point oldLocation;
	
	// bend点的索引
	private int index;

	public void execute() {
		oldLocation = (Point) connection.getBendpoints().get(index);
		connection.removeBendpoint(index);
	}

	public void setConnectionModel(Object model) {
		connection = (AbstractLineGefModel) model;
	}

	public void setIndex(int i) {
		index = i;
	}

	public void undo() {
		connection.addBendpoint(index, oldLocation);
	}
}
