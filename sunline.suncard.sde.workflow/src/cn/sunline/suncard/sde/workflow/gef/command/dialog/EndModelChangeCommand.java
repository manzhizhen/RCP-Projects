/* 文件名：     EndModelChangeCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-11
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.command.dialog;

import java.util.List;

import org.eclipse.gef.commands.Command;

import cn.sunline.suncard.sde.workflow.gef.model.AbstractLineModel;
import cn.sunline.suncard.sde.workflow.gef.model.EndModel;
import cn.sunline.suncard.sde.workflow.gef.model.TaskModel;
import cn.sunline.suncard.sde.workflow.model.LineNode;
import cn.sunline.suncard.sde.workflow.model.TaskNode;

/**
 * 结束模型对话框修改后执行的Command
 * @author  易振强
 * @version 1.0, 2012-4-11
 * @see 
 * @since 1.0
 */
public class EndModelChangeCommand extends Command{
	private String newName;
	private String newDesc;
	
	private String oldName;
	private String oldDesc;
	
	private EndModel endModel;

	@Override
	public void execute() {
		oldName = endModel.getEndNode().getName();
		oldDesc = endModel.getEndNode().getDescription();
		
		endModel.setNodeName(newName);
		endModel.setNodeDesc(newDesc);
		
		// 更新以它为目标的连接线的目标节点信息
		List<AbstractLineModel> lineList = endModel.getTargetConnections();
		for(AbstractLineModel line : lineList) {
			// 如果源节点是TaskModel才需要更新
			if(line.getSource() instanceof TaskModel) {
//				((LineNode)line.getDataObject()).getCommonNode().getConnectLine().setTargetNodeName(newName);
			}
		}
		
		super.execute();
	}
	
	@Override
	public void undo() {
		endModel.setNodeName(oldName);
		endModel.setNodeDesc(oldDesc);
		
		// 更新以它为目标的连接线的目标节点信息
		List<AbstractLineModel> lineList = endModel.getTargetConnections();
		for(AbstractLineModel line : lineList) {
			// 如果源节点是TaskModel才需要更新
			if(line.getSource() instanceof TaskModel) {
//				((LineNode)line.getDataObject()).getCommonNode().getConnectLine().setTargetNodeName(oldName);
			}
		}
	}
	
	public void setNewName(String newName) {
		this.newName = newName;
	}

	public void setNewDesc(String newDesc) {
		this.newDesc = newDesc;
	}

	public void setEndModel(EndModel endModel) {
		this.endModel = endModel;
	}
}
