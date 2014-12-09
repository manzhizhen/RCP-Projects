/* 文件名：     TaskModelChangeCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-11
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.command.dialog;

import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;

import cn.sunline.suncard.sde.workflow.gef.model.AbstractLineModel;
import cn.sunline.suncard.sde.workflow.gef.model.AbstractModel;
import cn.sunline.suncard.sde.workflow.gef.model.EndModel;
import cn.sunline.suncard.sde.workflow.gef.model.StartModel;
import cn.sunline.suncard.sde.workflow.gef.model.TaskModel;
import cn.sunline.suncard.sde.workflow.model.CommonNode;
import cn.sunline.suncard.sde.workflow.model.ConnectLine;
import cn.sunline.suncard.sde.workflow.model.DecisionNode;
import cn.sunline.suncard.sde.workflow.model.LineNode;
import cn.sunline.suncard.sde.workflow.model.Task;
import cn.sunline.suncard.sde.workflow.model.TaskNode;

/**
 * 修改TaskModel对话框后所执行的Command
 * 
 * @author 易振强
 * @version 1.0, 2012-4-11
 * @see
 * @since 1.0
 */
public class TaskModelChangeCommand extends Command {

	private TaskModel taskModel;

	private Task oldTask; // 旧的的Task
	private String oldDeciHandler; // 旧的决策处理类

	private Task newTask; // 新的的Task
	private String newDeciHandler; // 新的决策处理类
	
	private String oldType;		// 旧的分配方式
	private String newType;		// 新的分配方式
	
	private CommonNode commonNode;

	@Override
	public void execute() {
		// 保存旧值
		oldTask = new Task();
		oldTask.setName(taskModel.getTaskNode().getTask().getName());
		oldTask.setDescription(taskModel.getTaskNode().getTask().getDescription());
		oldTask.setAssignmentClass(taskModel.getTaskNode().getTask().getAssignmentClass());
		oldDeciHandler = taskModel.getTaskNode().getDecisionNode().getHandlerClass();
		oldType = taskModel.getTaskNode().getType();
		
		// 开始设置新值
		String name = newTask.getName();
		taskModel.getTaskNode().setName("".equals(name) ? "" : name + "Task");
		taskModel.getTaskNode().setDescription("".equals(name) ? "" : name + "Description");
		taskModel.getTaskNode().setType(newType);
		
		// 需要更新以该节点为目标节点的连接线的目标节点值
		List<AbstractLineModel> lineList = taskModel.getTargetConnections();
		for(AbstractLineModel line : lineList) {
			AbstractModel model = line.getSource();
			// 如果连接线的源的节点是开始节点
			if(model instanceof StartModel) {
				StartModel startModle = (StartModel) model;
				// 修改开始节点的连接线值
				startModle.getStartNode().getConnectLine().setTargetNodeName("".equals(name) ? "" : name + "Task");
				startModle.getStartNode().getConnectLine().setName(newTask.getDescription());
				
			} else if(model instanceof TaskModel) {
				// 在以前，如果连接线的目标节点的id（也就是TaskNode的Name）变了，
				// 需要修改该连接线上LineNode中的CommonNode的to值，但现在发现直接可以从模型的GEF信息中取得，所以不需要了
//				((LineNode)line.getDataObject()).getCommonNode().getConnectLine().setTargetNodeName("".equals(name) ? "" : name + "Task");
				
			}
		}
		
		
		taskModel.getTaskNode().setTask(newTask);
		taskModel.setTaskDesc(newTask.getDescription());	// 为了更新TaskModel的标签
		
		String desc = newTask.getDescription();
		taskModel.getTaskNode().getDecisionNode().setName("".equals(name) ? "" : name + "Decision");
		taskModel.getTaskNode().getDecisionNode().setDescription("".equals(desc) ? "" : desc + "决策");
		taskModel.getTaskNode().getDecisionNode().setHandlerClass(newDeciHandler);
		
		super.execute();
	}
	
	@Override
	public void undo() {
		
		String name = oldTask.getName();
		// 开始设置新值
		taskModel.getTaskNode().setName("".equals(name) ? "" : name + "Task");
		taskModel.getTaskNode().setDescription("".equals(name) ? "" : name + "Description");
		taskModel.getTaskNode().setType(oldType);
		
		// 需要更新以该节点为目标节点的连接线的目标节点值
		List<AbstractLineModel> lineList = taskModel.getTargetConnections();
		for(AbstractLineModel line : lineList) {
			AbstractModel model = line.getSource();
			// 如果前面的节点是开始节点
			if(model instanceof StartModel) {
				StartModel startModle = (StartModel) model;
				// 修改开始节点的连接线值
				startModle.getStartNode().getConnectLine().setTargetNodeName("".equals(name) ? "" : name + "Task");
				startModle.getStartNode().getConnectLine().setName(oldTask.getDescription());
				
			} 
		}
		
		
		taskModel.getTaskNode().setTask(oldTask);
		taskModel.setTaskDesc(oldTask.getDescription());
		
		String desc = oldTask.getDescription();
		taskModel.getTaskNode().getDecisionNode().setName("".equals(name) ? "" : name+ "Decision");
		taskModel.getTaskNode().getDecisionNode().setDescription("".equals(desc) ? "" : desc + "决策");
		taskModel.getTaskNode().getDecisionNode().setHandlerClass(oldDeciHandler);
	}
	
	public void setTaskModel(TaskModel taskModel) {
		this.taskModel = taskModel;
	}

	public void setNewTask(Task newTask) {
		this.newTask = newTask;
	}

	public void setNewDeciHandler(String newDeciHandler) {
		this.newDeciHandler = newDeciHandler;
	}

	public void setNewType(String newType) {
		this.newType = newType;
	}
}
