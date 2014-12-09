/*
 * 文件名：WorkFlowAbstractConnectionEditPart.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述： 抽象连接线的控制器
 * 修改人： 易振强
 * 修改时间：2011-12-15
 * 修改内容：创建
 */
package cn.sunline.suncard.sde.workflow.gef.editpart;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.swt.graphics.Color;


import cn.sunline.suncard.sde.workflow.gef.model.AbstractLineModel;
import cn.sunline.suncard.sde.workflow.gef.policy.WFlowBendpointEditPolicy;
import cn.sunline.suncard.sde.workflow.gef.policy.WFlowConnectionEndpointEditPolicy;
import cn.sunline.suncard.sde.workflow.gef.policy.WFlowLinkDelPolicy;


/**
 * 抽象连接线的控制器
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 */
public abstract class WorkFlowAbstractConnectionEditPart extends
		AbstractConnectionEditPart implements PropertyChangeListener, LayerConstants {

	/**
	 *  注册 PropertyChange
	 */
	@Override
	public void activate() {
		((AbstractLineModel) getModel()).addPropertyChangeLinstener(this);
		super.activate();
	}

	/**
	 *  重载 deactivate，删除 PropertyChange
	 */
	@Override
	public void deactivate() {
		((AbstractLineModel) getModel())
				.removePropertyChangeLinstener(this);
		super.deactivate();
	}

	/**
	 *  接受模型改变的通知
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(AbstractLineModel.P_BEND_POINT)) {
			refreshVisuals(); // 刷新控制点
			
		} else if(evt.getPropertyName().equals(AbstractLineModel.LINK_COLOR)) {
			refreshLinkColor();
		}
			
	}

	/**
	 *  刷新控制点
	 */
	@Override
	protected void refreshVisuals() {
		// 首先获得 bending 点的位置
		List<Point> bendpoints = ((AbstractLineModel) getModel())
				.getBendpoints();
		
		// 控制点的列表
		List<AbsoluteBendpoint> constraint = new ArrayList<AbsoluteBendpoint>();

		for (int i = 0; i < bendpoints.size(); i++) {
			// 根本连接模型的数据创建一个控制点
			constraint.add(new AbsoluteBendpoint((Point) bendpoints.get(i)));
		}
		
		// 创建一个连接，把刚才生成的控制点作为约束
		getConnectionFigure().setRoutingConstraint(constraint);
	}
	


	@Override
	protected void createEditPolicies() {
		// 增加连接线控制点选中的政策
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new WFlowConnectionEndpointEditPolicy());

		// 增加连接线控制点删除的策略
		installEditPolicy(EditPolicy.CONNECTION_ROLE,
				new WFlowLinkDelPolicy());

		// 控制点锚点的策略
		installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE,
				new WFlowBendpointEditPolicy());
		
	}

	/**
	 * 刷新连接线的颜色
	 */
	private void refreshLinkColor() {
		AbstractLineModel model = (AbstractLineModel) getModel();
		
		if(model != null) {
			getConnectionFigure().setForegroundColor(ColorConstants.black);
		}
	}
	
	@Override
	protected IFigure createFigure() {
		PolylineConnection connection = new PolylineConnection();
		connection.setConnectionRouter(new BendpointConnectionRouter());

		return connection;
	}


}
