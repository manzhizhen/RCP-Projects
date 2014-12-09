/* 文件名：     ReconnectConnectionCommand.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	 重定向连接线连接的Command
 * 修改人：     易振强
 * 修改时间：2011-11-15
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.command;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Connection;
import org.eclipse.gef.commands.Command;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.sde.workflow.gef.model.AbstractLineModel;
import cn.sunline.suncard.sde.workflow.gef.model.AbstractModel;
import cn.sunline.suncard.sde.workflow.gef.model.EndModel;
import cn.sunline.suncard.sde.workflow.gef.model.StartModel;
import cn.sunline.suncard.sde.workflow.gef.model.TaskModel;
import cn.sunline.suncard.sde.workflow.gef.model.WorkFlowModel;
import cn.sunline.suncard.sde.workflow.gef.ui.editor.WorkFlowEditor;
import cn.sunline.suncard.sde.workflow.model.CommonNode;
import cn.sunline.suncard.sde.workflow.model.ConnectLine;
import cn.sunline.suncard.sde.workflow.model.LineNode;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;


/**
 *  重定向连接线连接的Command
 * @author    易振强
 * @version   [1.0, 2011-11-15]
 * @since     1.0
 */
public class ReconnectLineCommand extends Command {
	private AbstractModel oldModel;
	private AbstractModel source;
	private AbstractModel target;
	private AbstractLineModel connection;
	private WorkFlowModel workFlowModel;
	
	private String oldTargetValue;
	private String oldNameValue;
	
	private String oldDecisionId;
	private String oldTarget;
	
//	private List<CommonNode> needDelNodeList;
//	private List<ConnectLine> needDelLineList;	
	
	public static Log logger = LogManager.getLogger(ReconnectLineCommand.class.getName());
	
	/**
	 *  用于标记该Command是由于移动连接线的开始端还是移动连接线的结束端产生的。
	 *  1为开始端，2为结束端
	 */
	private int flag = -1;
	public final static int START = 1;
	public final static int END = 2;

	/**
	 * 
	 */
	public ReconnectLineCommand() {
		IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(part instanceof WorkFlowEditor) {
			WorkFlowEditor workPart =  (WorkFlowEditor) part;
			workFlowModel = workPart.getWorkFlowModel();
		}
	}
	
	/**
	 *  首先判断是否能执行连接
	 */
	@Override
	public boolean canExecute() {
		// 开始节点和结束节点都不能为空
		if (source == null || target == null) {
			return false;
		}
		
		// 开始节点和结束节点不能相等
		if (source.equals(target)) {
			return false;
		}
		
		List<AbstractLineModel> list;
		
		if(START == flag) {
			list = target.getTargetConnections();
			
			for(AbstractLineModel temp : list) {
				if(temp.getSource().equals(source)) {
					return false;
				}
			}
		} else if(END == flag) {
			list = source.getSourceConnections();
			
			for(AbstractLineModel temp : list) {
				if(temp.getTarget().equals(target)) {
					return false;
				}
			}
		}
		
		return true;
	}

	@Override
	public void execute() {
		logger.info("void execute() start...");
		
		// 如果是重定向连接线的起点
		if(flag == START) {
			
			// 如果新的起点是开始节点，且目标节点是任务节点
			if(source instanceof StartModel && target instanceof TaskModel) {
				
			// 如果新的起点和目标节点都是任务节点，新的任务节点无需作数据处理
			} else if(source instanceof TaskModel && target instanceof TaskModel) {
				// 如果旧节点是任务节点
				if(oldModel instanceof TaskModel) {
					
				} else if(oldModel instanceof StartModel) {
					StartModel oldStartModel = (StartModel) oldModel;
					oldTargetValue = oldStartModel.getStartNode().getConnectLine().getTargetNodeName();
					oldNameValue = oldStartModel.getStartNode().getConnectLine().getName();
					
					oldStartModel.getStartNode().getConnectLine().setTargetNodeName("");
					oldStartModel.getStartNode().getConnectLine().setName("");
					
				}
				
				// 如果新的起点是开始节点，且目标节点是结束节点
			} else if(source instanceof StartModel && target instanceof EndModel) {
				StartModel startModel = (StartModel) source;
				EndModel endModel = (EndModel) target;
				oldTargetValue = startModel.getStartNode().getConnectLine().getTargetNodeName();
				oldNameValue = startModel.getStartNode().getConnectLine().getName();
				startModel.getStartNode().getConnectLine().setTargetNodeName(endModel.getEndNode().getName());
				startModel.getStartNode().getConnectLine().setName(endModel.getEndNode().getDescription());
			}
			
			connection.setSource(source);
			connection.attachSource();// 连接开始节点
			oldModel.removeSourceConnection(connection);
			
		// 如果是重定向连接线的终点
		} else if(flag == END) {
			// 起点是开始节点，新的目标是任务节点
			if(source instanceof StartModel && target instanceof TaskModel) {
				// 给新节点赋值
				StartModel startModel = (StartModel) source;
				TaskModel taskModel = (TaskModel) target;
				oldTargetValue = startModel.getStartNode().getConnectLine().getTargetNodeName();
				oldNameValue = startModel.getStartNode().getConnectLine().getName();
				startModel.getStartNode().getConnectLine().setTargetNodeName(taskModel.getTaskNode().getName());
				startModel.getStartNode().getConnectLine().setName(taskModel.getTaskNode().getTask().getDescription());
				
			// 起点是任务节点，新的目标是任务节点
			} else if(source instanceof TaskModel && target instanceof TaskModel) {
//				TaskModel targetModel = (TaskModel) target;
//				oldTarget = ((LineNode)connection.getDataObject()).getCommonNode().getConnectLine().getTargetNodeName();
//				((LineNode)connection.getDataObject()).getCommonNode().getConnectLine().setTargetNodeName(targetModel.getTaskNode().getName());
				
				
				// 如果新的起点是开始节点，且目标节点是结束节点
			} else if(source instanceof StartModel && target instanceof EndModel) {
				StartModel startModel = (StartModel) source;
				EndModel endModel = (EndModel) target;
				oldTargetValue = startModel.getStartNode().getConnectLine().getTargetNodeName();
				oldNameValue = startModel.getStartNode().getConnectLine().getName();
				startModel.getStartNode().getConnectLine().setTargetNodeName(endModel.getEndNode().getName());
				startModel.getStartNode().getConnectLine().setName(endModel.getEndNode().getDescription());
			}
			
			connection.setTarget(target);
			connection.attachTarget();// 连接结束节点
			oldModel.removeTargetConnection(connection);
		}
		
		WorkFlowEditor.checkModel(workFlowModel);
	}

	/**
	 * 设置连接线对象
	 * @param  Object 连接线对象
	 */
	public void setConnectionModel(Object model) {
		logger.info("void setConnectionModel(Object model) start...");
		
		connection = (AbstractLineModel) model;
		
		if(flag == START) {
			oldModel = connection.getSource();
			target = connection.getTarget();
			
			return ;
		} 
		
		if(flag == END) {
			oldModel = connection.getTarget();
			source = connection.getSource();
			
			return ;
		}
	}

	public void setNewSource(Object model) {
		logger.info("void setNewSource(Object model) start...");
		
		// 设置标记
		flag = START;
		
		source = (AbstractModel) model;
	}

	public void setNewTarget(Object model) {
		logger.info("void setNewTarget(Object model) start...");
	
		// 设置标记
		flag = END;
		
		target = (AbstractModel) model;
	}

	// 撤销
	@Override
	public void undo() {
		// 如果是重定向连接线的起点
		if(flag == START) {
			
			// 如果新的起点是开始节点，且目标节点是任务节点
			if(source instanceof StartModel && target instanceof TaskModel) {
				
			// 如果新的起点和目标节点都是任务节点，新的任务节点无需作数据处理
			} else if(source instanceof TaskModel && target instanceof TaskModel) {
				
				// 更新旧节点信息
				if(oldModel instanceof TaskModel) {
					
				} else if(oldModel instanceof StartModel) {
					StartModel oldStartModel = (StartModel) oldModel;
					
					oldStartModel.getStartNode().getConnectLine().setTargetNodeName(oldTargetValue);
					oldStartModel.getStartNode().getConnectLine().setName(oldNameValue);
					
				}
				
				// 如果新的起点是开始节点，且目标节点是结束节点
			} else if(source instanceof StartModel && connection.getTarget() instanceof EndModel) {
				StartModel startModel = (StartModel) source;
				EndModel endModel = (EndModel) target;
				startModel.getStartNode().getConnectLine().setTargetNodeName(oldTargetValue);
				startModel.getStartNode().getConnectLine().setName(oldNameValue);
			}
			
			AbstractModel model = connection.getSource();
			connection.setSource(oldModel);
			connection.attachSource();// 连接开始节点
			model.removeSourceConnection(connection);
			
		// 如果是重定向连接线的终点
		} else if(flag == END) {
			// 起点是开始节点，新的目标是任务节点
			if(source instanceof StartModel && target instanceof TaskModel) {
				// 给新节点赋值
				StartModel startModel = (StartModel) source;
				startModel.getStartNode().getConnectLine().setTargetNodeName(oldTargetValue);
				startModel.getStartNode().getConnectLine().setName(oldNameValue);
				
			// 起点是任务节点，新的目标是任务节点
			} else if(source instanceof TaskModel && target instanceof TaskModel) {
//				((LineNode)connection.getDataObject()).getCommonNode().getConnectLine().setTargetNodeName(oldTarget);
				
				// 如果新的起点是开始节点，且目标节点是结束节点
			} else if(source instanceof StartModel && connection.getTarget() instanceof EndModel) {
				StartModel startModel = (StartModel) source;
				startModel.getStartNode().getConnectLine().setTargetNodeName(oldTargetValue);
				startModel.getStartNode().getConnectLine().setName(oldNameValue);
			}
			
			AbstractModel model = connection.getTarget();
			connection.setTarget(oldModel);
			connection.attachTarget();// 连接结束节点
			model.removeTargetConnection(connection);
		}
		
		WorkFlowEditor.checkModel(workFlowModel);
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	
}
