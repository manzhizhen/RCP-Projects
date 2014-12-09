/* 文件名：     NormalConnectionEditPart.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	普通连接线的EditPart
 * 修改人：     易振强
 * 修改时间：2011-12-15
 * 修改内容：创     建
 */
package cn.sunline.suncard.sde.workflow.gef.editpart;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPart;

import cn.sunline.suncard.sde.workflow.gef.model.LineModel;
import cn.sunline.suncard.sde.workflow.gef.model.StartModel;

/**
 * 普通连接线的EditPart
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 */
public class LineEditPart extends WorkFlowAbstractConnectionEditPart{
	private LineModel connectionModel;
	private PolylineConnection connection;
	
	public LineEditPart(LineModel connectionModel) {
		this.connectionModel = connectionModel;
	}
	
	@Override
	protected IFigure createFigure() {
		// 使用 PolylineConnection 
		connection = (PolylineConnection) super.createFigure(); 
		connection.setTargetDecoration(new PolygonDecoration());

		connection.setForegroundColor(ColorConstants.black);
		
		// 连接线上加Label标签
		ConnectionLineLocator locator = new ConnectionLineLocator(connection, 
				ConnectionLocator.MIDDLE);
		
		connectionModel.getLabel().setText(connectionModel.getLabelText());
		connection.add(connectionModel.getLabel(), locator);
		
		return connection;
	}
	
	@Override
	public void setSelected(int value) {
		super.setSelected(value);
		if (value != EditPart.SELECTED_NONE) {
			((PolylineConnection) getFigure()).setLineWidth(3);
			
		} else {
			((PolylineConnection) getFigure()).setLineWidth(1);
		}
	}
	
	@Override
	protected void setFigure(IFigure figure) {
		super.setFigure(figure);
	}
}
