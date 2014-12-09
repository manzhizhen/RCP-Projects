/* 文件名：     TaskModelEditPart.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	任务模型的控制器
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.editpart;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;

import cn.sunline.suncard.powerdesigner.gef.figure.TableFigure;
import cn.sunline.suncard.powerdesigner.gef.model.TableGefModel;
import cn.sunline.suncard.powerdesigner.gef.policy.DelModelEditPolicy;
import cn.sunline.suncard.powerdesigner.gef.policy.LineAndModelGraphiNodeEditPlicy;
import cn.sunline.suncard.powerdesigner.model.TableModel;


/**
 * 表格模型的控制器
 * @author  易振强
 * @version 1.0, 2012-3-26
 * @see 
 * @since 1.0
 */
public class TableGefModelEditPart extends EditPartWithListener {

	@Override
	protected IFigure createFigure() {
		TableGefModel model = (TableGefModel) getModel();
		TableFigure tableFigure = new TableFigure(model.getDataObject());
		model.getConstraint().setSize(tableFigure.getPreferredSize());
		return tableFigure;
	}

	@Override
	protected void refreshVisuals() {
		// 模型大小和位置改变后，由控制器通知界面
		Rectangle constraint = ((TableGefModel) getModel()).getConstraint();
		Rectangle oldRectangle = getFigure().getBounds();
		if(!constraint.getSize().equals(oldRectangle.width, oldRectangle.height) && 
				!constraint.getSize().equals(-1, -1)) {
			((TableGefModel) getModel()).getDataObject().setAutoSize(false);
		}
		
		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), constraint);
	}
	
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new DelModelEditPolicy()); // 安装删除模型的策略
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new LineAndModelGraphiNodeEditPlicy()); // 安装和连接线对接的策略
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(TableGefModel.CONSTRAINT)) {
			refreshVisuals();
			
			//改变是要不断刷新
		} else if(evt.getPropertyName().equals(TableGefModel.SOURCE_CONNECTION)){
			refreshSourceConnections();
			
			// 当模型的状态改变了，图标也要相应的改变
		} else if(evt.getPropertyName().equals(TableGefModel.TARGET_CONNECTION)){
			refreshTargetConnections();
			
		} else if(evt.getPropertyName().equals(TableGefModel.TABLEMODEL_CHANGE)) {
			((TableFigure)getFigure()).updateFigure(((TableGefModel)getModel()).getDataObject());
			// 根据新的图形尺寸来修改模型尺寸，这样模型尺寸的变更就会影响到editpart
			Rectangle constraint = ((TableGefModel) getModel()).getConstraint();
			Rectangle cloneConstraint = constraint.getCopy().setSize(((TableFigure)getFigure()).getSize());
			((TableGefModel) getModel()).setConstraint(cloneConstraint);
			refreshVisuals();
		
		// 选中某一个表格
		} else if(evt.getPropertyName().equals(TableGefModel.TABLE_SELECT)) {
//			setSelected(EditPart.SELECTED_PRIMARY);
//			setSelected(EditPart.SELECTED);
			getFigure().requestFocus();
			getViewer().appendSelection(this);
//			setFocus(true);
			
			
		// 取消某一个表格的选中状态	
		} else if(evt.getPropertyName().equals(TableGefModel.TABLE_NONE_SELECT)) {
			setSelected(EditPart.SELECTED_NONE);
		}
		
	}
	
	/**
	 * 由refreshSourceConnections()调用
	 */
	@Override
	protected List getModelSourceConnections() {
		return ((TableGefModel)getModel()).getSourceConnections();
	}

	/**
	 * 由refreshSourceConnections()调用
	 */
	@Override
	protected List getModelTargetConnections() {
		return ((TableGefModel)getModel()).getTargetConnections();
	}


}
