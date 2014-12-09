/* 文件名：     EndModelEditPart.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
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
import cn.sunline.suncard.sde.workflow.gef.model.EndModel;
import cn.sunline.suncard.sde.workflow.gef.policy.DelModelEditPolicy;
import cn.sunline.suncard.sde.workflow.gef.policy.LineAndModelGraphiNodeEditPlicy;

/**
 * 描述
 * @author  易振强
 * @version 1.0, 2012-3-26
 * @see 
 * @since 1.0
 */
public class EndModelEditPart extends EditPartWithListener {
	public static final String SOURCE_CONNECTION = "start_source_connection";		//源连接线变动标记
	public static final String TARGET_CONNECTION = "start_target_connection";		//目标连接线变动标记
	
	@Override
	protected IFigure createFigure() {
		EndModel model = (EndModel) getModel();
		
		BaseFigure figure = new BaseFigure(model);
		
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new DelModelEditPolicy()); // 安装删除模型的策略
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new LineAndModelGraphiNodeEditPlicy()); // 安装和连接线对接的策略
	}
	
	@Override
	protected void refreshVisuals() {
		// 模型大小和位置改变后，由控制器通知界面
		Rectangle constraint = ((EndModel) getModel()).getConstraint();
		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), constraint);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(EndModel.CONSTRAINT)) {
			refreshVisuals();
			
			//改变是要不断刷新
		} else if(evt.getPropertyName().equals(EndModel.SOURCE_CONNECTION)){
			refreshSourceConnections();
			
			// 当模型的状态改变了，图标也要相应的改变
		} else if(evt.getPropertyName().equals(EndModel.TARGET_CONNECTION)){
			refreshTargetConnections();
		}  else if(evt.getPropertyName().equals(EndModel.PROP_ID_LABEL)){
			EndModel model = (EndModel) getModel();
			model.setLabelName((String)evt.getNewValue());
		} 
	}
	
	/**
	 * 由refreshSourceConnections()调用
	 */
	@Override
	protected List getModelSourceConnections() {
		return ((EndModel)getModel()).getSourceConnections();
	}

	/**
	 * 由refreshSourceConnections()调用
	 */
	@Override
	protected List getModelTargetConnections() {
		return ((EndModel)getModel()).getTargetConnections();
	}


}
