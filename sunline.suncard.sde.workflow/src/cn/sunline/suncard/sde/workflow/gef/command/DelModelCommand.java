/*
 * 文件名：.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述： SUNCARD决策引擎系统
 * 修改人： 周兵
 * 修改时间：2001-02-16
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.workflow.gef.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;

import cn.sunline.suncard.sde.workflow.gef.model.AbstractLineModel;
import cn.sunline.suncard.sde.workflow.gef.model.AbstractModel;
import cn.sunline.suncard.sde.workflow.gef.model.WorkFlowModel;
import cn.sunline.suncard.sde.workflow.gef.ui.editor.WorkFlowEditor;


/**
 * 删除模型的Command
 * @author 	易振强
 * @version [1.0, 2011-11-01]
 * @see
 * @since 1.0
 */
public class DelModelCommand extends Command {

	private WorkFlowModel workFlowModel;
	private AbstractModel model;

	private List<AbstractLineModel> sourceConnections = new ArrayList<AbstractLineModel>();
	private List<AbstractLineModel> targetConnections = new ArrayList<AbstractLineModel>();
	
	private List<DeleteLineCommand> deleteLineCommandList = new ArrayList<DeleteLineCommand>();

	@Override
	public void execute() {
		
		// 删除这个连接模型，再删除一个对象前，搜索吧这个模型对象作为起点和终点的所有连接
		sourceConnections.addAll(model.getSourceConnections());
		targetConnections.addAll(model.getTargetConnections());
		
		// 删除该模型对应的source
		for (int i = 0; i < sourceConnections.size(); i++) {
			AbstractLineModel sourceModel = (AbstractLineModel) sourceConnections
					.get(i);
			
			DeleteLineCommand deleteLineCommand = new DeleteLineCommand();
			deleteLineCommand.setConnectionModel(sourceModel);
			deleteLineCommand.execute();
			
			deleteLineCommandList.add(deleteLineCommand);
		}
		
		// 删除该模型对应的target
		for (int i = 0; i < targetConnections.size(); i++) {
			AbstractLineModel targetModel = (AbstractLineModel) targetConnections
					.get(i);
			
			DeleteLineCommand deleteLineCommand = new DeleteLineCommand();
			deleteLineCommand.setConnectionModel(targetModel);
			deleteLineCommand.execute();
			
			deleteLineCommandList.add(deleteLineCommand);
		}
		
		workFlowModel.removeChild(model);
		WorkFlowEditor.checkModel(workFlowModel);
	}

	public void setWorkFlowModel(Object model) {
		workFlowModel = (WorkFlowModel) model;
	}

	public void setModel(Object model) {
		this.model = (AbstractModel) model;
	}

	@Override
	public void undo() {
		workFlowModel.addChild(model);
		
		int i = deleteLineCommandList.size() - 1;
		for(;i >= 0; i--) {
			deleteLineCommandList.get(i).undo();
		}
		
		//清除记录，这些记录用于恢复
		sourceConnections.clear();
		targetConnections.clear();
		
		
		WorkFlowEditor.checkModel(workFlowModel);
	}

}
