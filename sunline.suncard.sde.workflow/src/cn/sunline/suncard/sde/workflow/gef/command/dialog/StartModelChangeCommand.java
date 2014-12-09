/* 文件名：     StartModelChangeCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-11
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.command.dialog;

import org.eclipse.gef.commands.Command;

import cn.sunline.suncard.sde.workflow.gef.model.StartModel;

/**
 * 修改开始模型对话框后执行的Command
 * @author  易振强
 * @version 1.0, 2012-4-11
 * @see 
 * @since 1.0
 */
public class StartModelChangeCommand extends Command{
	private String oldName;
	private String oldDesc;
	
	private String newName;
	private String newDesc;
	
	private StartModel startModel;

	@Override
	public void execute() {
		oldName = startModel.getStartNode().getName();
		oldDesc = startModel.getStartNode().getDescription();
		
		startModel.setStartName(newName);
		startModel.setStartDesc(newDesc);
		
		super.execute();
	}
	
	public void setStartModel(StartModel startModel) {
		this.startModel = startModel;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public void setNewDesc(String newDesc) {
		this.newDesc = newDesc;
	}
	
	@Override
	public void undo() {
		startModel.setStartName(oldName);
		startModel.setStartDesc(oldDesc);
	}
	
	
}
