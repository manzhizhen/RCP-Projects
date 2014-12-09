/* 文件名：     TaskModelEditPart.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	任务模型的控制器
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.editpart;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;

import cn.sunline.suncard.sde.workflow.gef.model.BaseFigure;
import cn.sunline.suncard.sde.workflow.gef.model.TaskModel;
import cn.sunline.suncard.sde.workflow.gef.policy.DelModelEditPolicy;
import cn.sunline.suncard.sde.workflow.gef.policy.LineAndModelGraphiNodeEditPlicy;

/**
 * 任务模型的控制器
 * @author  易振强
 * @version 1.0, 2012-3-26
 * @see 
 * @since 1.0
 */
public class TaskModelEditPart extends EditPartWithListener {

	@Override
	protected IFigure createFigure() {
		TaskModel model = (TaskModel) getModel();
		
		BaseFigure figure = new BaseFigure(model);
		
		return figure;
	}

	@Override
	protected void refreshVisuals() {
		// 模型大小和位置改变后，由控制器通知界面
		Rectangle constraint = ((TaskModel) getModel()).getConstraint();
		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), constraint);
	}
	
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new DelModelEditPolicy()); // 安装删除模型的策略
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new LineAndModelGraphiNodeEditPlicy()); // 安装和连接线对接的策略
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(TaskModel.CONSTRAINT)) {
			refreshVisuals();
			
			//改变是要不断刷新
		} else if(evt.getPropertyName().equals(TaskModel.SOURCE_CONNECTION)){
			refreshSourceConnections();
			
			// 当模型的状态改变了，图标也要相应的改变
		} else if(evt.getPropertyName().equals(TaskModel.TARGET_CONNECTION)){
			refreshTargetConnections();
			
		} else if(evt.getPropertyName().equals(TaskModel.PROP_ID_LABEL)){
			TaskModel model = (TaskModel) getModel();
			model.setLabelName((String)evt.getNewValue());
		} 
	}
	
	/**
	 * 由refreshSourceConnections()调用
	 */
	@Override
	protected List getModelSourceConnections() {
		return ((TaskModel)getModel()).getSourceConnections();
	}

	/**
	 * 由refreshSourceConnections()调用
	 */
	@Override
	protected List getModelTargetConnections() {
		return ((TaskModel)getModel()).getTargetConnections();
	}


}
