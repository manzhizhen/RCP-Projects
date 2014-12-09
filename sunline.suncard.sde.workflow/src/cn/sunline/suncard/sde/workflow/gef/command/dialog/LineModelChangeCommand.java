/* 文件名：     LineModelChangeCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-11
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.command.dialog;

import org.eclipse.gef.commands.Command;

import cn.sunline.suncard.sde.workflow.gef.model.LineModel;
import cn.sunline.suncard.sde.workflow.model.CommonNode;
import cn.sunline.suncard.sde.workflow.model.ConnectLine;

/**
 * 连接线模型对话框修改后执行的Command
 * 
 * @author 易振强
 * @version 1.0, 2012-4-11
 * @see
 * @since 1.0
 */
public class LineModelChangeCommand extends Command {
	private String oldName;
	private String oldDesc;
	private String newName;
	private String newDesc;
	private String oldTarget;
	private String newTarget;
	
	private String oldHandler;
	private String newHandler;

//	private ConnectLine line;
	private CommonNode node;
	private LineModel lineModel;

	@Override
	public void execute() {
		oldName = node.getName();
		oldDesc = node.getDescription();
//		oldTarget = node.getConnectLine().getTargetNodeName(); 	// 保存旧的目标节点名称
		oldHandler = node.getActionClass();
		
//		line.setName(newName);
//		line.setTargetNodeName(newName);
//		line.setDescription(newDesc);
		lineModel.setLabelText(newDesc);
		node.setName(newName);
		node.setDescription(newDesc);
//		node.getConnectLine().setTargetNodeName(newTarget);		// 存储新的目标节点名称
		node.setActionClass(newHandler);
		
		super.execute();
	}
	
	@Override
	public void undo() {
//		line.setName(oldName);
//		line.setDescription(oldDesc);
//		line.setTargetNodeName(oldName);
		lineModel.setLabelText(oldDesc);
		node.setName(oldName);
		node.setDescription(oldDesc);
//		node.getConnectLine().setTargetNodeName(oldTarget);	
		node.setActionClass(oldHandler);
	}
	
	public void setNewName(String newName) {
		this.newName = newName;
	}

	public void setNewDesc(String newDesc) {
		this.newDesc = newDesc;
	}

//	public void setLine(ConnectLine line) {
//		this.line = line;
//	}

	public void setNode(CommonNode node) {
		this.node = node;
	}

	public void setNewHandler(String newHandler) {
		this.newHandler = newHandler;
	}

	public void setLineModel(LineModel lineModel) {
		this.lineModel = lineModel;
	}

	public void setNewTarget(String newTarget) {
		this.newTarget = newTarget;
	}
}
