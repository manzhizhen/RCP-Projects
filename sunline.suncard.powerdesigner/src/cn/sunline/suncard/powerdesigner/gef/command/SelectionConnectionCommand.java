/* 文件名：     SelectionConnectionCommand.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	选中连接线的Command
 * 修改人：     易振强
 * 修改时间：2011-11-18
 * 修改内容：创     建
 */
package cn.sunline.suncard.powerdesigner.gef.command;

import org.eclipse.gef.commands.Command;

import cn.sunline.suncard.powerdesigner.gef.model.AbstractLineGefModel;



/**
 * 选中连接线的Command，在此版本还没有实际作用
 * @author  易振强
 * @version 1.0, 2011-11-18
 * @see 
 * @since 1.0
 */
public class SelectionConnectionCommand extends Command {
	private AbstractLineGefModel connection;
	
	@Override
	public boolean canExecute() {
		// TODO Auto-generated method stub
		return super.canExecute();
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		super.execute();
	}

	public void setConnection(AbstractLineGefModel connection) {
		this.connection = connection;
	}
	
	
}
