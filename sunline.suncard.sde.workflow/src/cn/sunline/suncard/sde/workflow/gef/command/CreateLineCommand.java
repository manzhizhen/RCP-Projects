/* 文件名：     CreateLineCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	创建连接线的Command
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.command;

import java.util.List;

import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.sde.workflow.gef.model.AbstractLineModel;
import cn.sunline.suncard.sde.workflow.gef.model.AbstractModel;
import cn.sunline.suncard.sde.workflow.gef.model.EndModel;
import cn.sunline.suncard.sde.workflow.gef.model.StartModel;
import cn.sunline.suncard.sde.workflow.gef.model.TaskModel;
import cn.sunline.suncard.sde.workflow.gef.model.WorkFlowModel;
import cn.sunline.suncard.sde.workflow.gef.ui.editor.WorkFlowEditor;
import cn.sunline.suncard.sde.workflow.gef.xml.GefFigureSwitchXml;
import cn.sunline.suncard.sde.workflow.model.CommonNode;


/**
 * 创建连接线的Command
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 */
public class CreateLineCommand extends Command {
	private AbstractModel source;
	private AbstractModel target;
	private WorkFlowModel workFlowModel;
	private AbstractLineModel connection;
	
	private String oldTargetValue;
	private String oldNameValue;


	public CreateLineCommand() {
		IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(part instanceof WorkFlowEditor) {
			WorkFlowEditor scorePart = (WorkFlowEditor) part;
			workFlowModel = scorePart.getWorkFlowModel();
		}
	}

	// 首先判断是否能执行连接
	@Override
	public boolean canExecute() {
		if (source == null || target == null) {
			return false;
		}
		
		if (source.equals(target)) {
			return false;
		}
		
		if(target instanceof StartModel || source instanceof EndModel) {
			return false; 
		}
		
		// 开始节点和结束节点只能连接一根线
		if(source instanceof StartModel) {
			if(source.getSourceConnections().size() >= 1) {
				return false;
			}
			
		} 
		
		List<AbstractLineModel> linkModels = target.getSourceConnections();
		for(AbstractLineModel linkModel : linkModels) {
			if(linkModel.getTarget().equals(source)) {
				return false;
			}
		}
		
		linkModels = source.getSourceConnections();
		for(AbstractLineModel linkModel : linkModels) {
			if(linkModel.getTarget().equals(target)) {
				return false;
			}
		}
		
		GefFigureSwitchXml.initLineProperty(connection, workFlowModel);
		
		return true;
	}

	@Override
	public void execute() {
		// 执行的时候分两步。连接起点和终点
		connection.attachSource();
		connection.attachTarget();
		
		if(source instanceof StartModel && target instanceof TaskModel) {
			StartModel startModel = (StartModel) source;
			TaskModel taskModel = (TaskModel) target;
			oldTargetValue = startModel.getStartNode().getConnectLine().getTargetNodeName();
			oldNameValue = startModel.getStartNode().getConnectLine().getName();
			startModel.getStartNode().getConnectLine().setTargetNodeName(taskModel.getTaskNode().getName());
			startModel.getStartNode().getConnectLine().setName(taskModel.getTaskNode().getTask().getDescription());
			
		} else if(source instanceof StartModel && target instanceof EndModel) {
			StartModel startModel = (StartModel) source;
			EndModel endModel = (EndModel) target;
			oldTargetValue = startModel.getStartNode().getConnectLine().getTargetNodeName();
			oldNameValue = startModel.getStartNode().getConnectLine().getName();
			startModel.getStartNode().getConnectLine().setTargetNodeName(endModel.getEndNode().getName());
			startModel.getStartNode().getConnectLine().setName(endModel.getEndNode().getDescription());
			
		}
		
		WorkFlowEditor.checkModel(workFlowModel);
	}

	public void setConnection(Object model) {
		connection = (AbstractLineModel) model;
	}

	public void setSource(Object model) {
		source = (AbstractModel) model;
		connection.setSource(source);
	}

	public void setTarget(Object model) {
		target = (AbstractModel) model;
		connection.setTarget(target);
	}

	@Override
	public void undo() {
		
		if(source instanceof StartModel) {
			StartModel startModel = (StartModel) source;
			startModel.getStartNode().getConnectLine().setTargetNodeName(oldTargetValue);
			startModel.getStartNode().getConnectLine().setName(oldNameValue);
		} 
		
		connection.detachSource();
		connection.detachTarget();
		
		WorkFlowEditor.checkModel(workFlowModel);
	}

}
