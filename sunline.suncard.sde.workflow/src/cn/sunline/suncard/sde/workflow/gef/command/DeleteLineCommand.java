/* 文件名：     DeleteConnectionCommand.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	删除控制线的Command
 * 修改人：     易振强
 * 修改时间：2011-11-15
 * 修改内容：创     建
 */
package cn.sunline.suncard.sde.workflow.gef.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
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


/**
 * 删除连接线的Command
 * @author  易振强
 * @version 1.0, 2012-3-26
 * @see 
 * @since 1.0
 */
public class DeleteLineCommand extends Command {
	private AbstractLineModel connection;
	private WorkFlowModel workFlowModel;
	
	private ConnectLine oldLine;
//	private List<CommonNode> needNodeList;
//	private List<ConnectLine> needLineList;
	
	public DeleteLineCommand() {
		IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(part instanceof WorkFlowEditor) {
			WorkFlowEditor workPart = (WorkFlowEditor) part;
			workFlowModel = workPart.getWorkFlowModel();
		} 
	}
	
	@Override
	public void execute() {
		AbstractModel model = connection.getSource();
		AbstractModel targetModel = connection.getTarget();
		
		// 如果是开始模型，则要更新其连接线
		if(model instanceof StartModel) {
			StartModel startModel = (StartModel) connection.getSource();
			oldLine = startModel.getStartNode().getConnectLine();
			startModel.getStartNode().setConnectLine(new ConnectLine());
			
		} else if(model instanceof TaskModel) {
//			String name = null;
//			if(targetModel instanceof TaskModel) {
//				name = ((TaskModel)targetModel).getTaskNode().getName();
//			} else if(targetModel instanceof EndModel) {
//				name = ((EndModel)targetModel).getEndNode().getName();
//			}
//			
//			if(name != null) {
//				TaskModel taskModel = (TaskModel) model;
//				List<CommonNode> nodeList = taskModel.getTaskNode().getDecisionNode().getCommonNodes();
//				List<ConnectLine> lineList = taskModel.getTaskNode().getDecisionNode().getConnectLines();
//				
//				needNodeList = new ArrayList<CommonNode>();
//				needLineList = new ArrayList<ConnectLine>();
//				
//				for(int i = 0; i < nodeList.size(); i++) {
//					if(name.equals(nodeList.get(i).getConnectLine().getTargetNodeName())) {
//						needNodeList.add(nodeList.get(i));
//						needLineList.add(lineList.get(i));
//					}
//				}
//				
//				nodeList.removeAll(needNodeList);
//				lineList.removeAll(needLineList);
//			}
		}
		
		// 在开始节点上删除该连接线
		connection.detachSource();
		
		// 在结束节点上删除该连接线
		connection.detachTarget();
		
		
		WorkFlowEditor.checkModel(workFlowModel);
	}
	
	/**
	 *  设置连接线对象
	 * @param  Object 连接线对象
	 */
	public void setConnectionModel(Object model) {
		this.connection = (AbstractLineModel) model;
	}
	
	/**
	 *  撤销动作
	 */
	@Override
	public void undo() {
		AbstractModel model = connection.getSource();
		
		// 如果是开始模型，则要更新其连接线
		if(model instanceof StartModel) {
			StartModel startModel = (StartModel) connection.getSource();
			startModel.getStartNode().setConnectLine(oldLine);
			
		} else if(model instanceof TaskModel) {
//			TaskModel taskModel = (TaskModel) model;
//			
//			List<CommonNode> nodeList = taskModel.getTaskNode().getDecisionNode().getCommonNodes();
//			List<ConnectLine> lineList = taskModel.getTaskNode().getDecisionNode().getConnectLines();
//			
//			nodeList.addAll(needNodeList);
//			lineList.addAll(needLineList);
		}
		
		connection.attachSource(); 
		connection.attachTarget(); 
		
		WorkFlowEditor.checkModel(workFlowModel);
	}

}
