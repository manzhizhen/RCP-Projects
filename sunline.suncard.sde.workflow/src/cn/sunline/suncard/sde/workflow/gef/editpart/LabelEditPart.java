/* 文件名：     LabelEditPart.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	图标下Label的EditPart
 * 修改人：     易振强
 * 修改时间：2011-12-8
 * 修改内容：创     建
 */
package cn.sunline.suncard.sde.workflow.gef.editpart;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.TextUtilities;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.sde.workflow.gef.model.AbstractModel;
import cn.sunline.suncard.sde.workflow.gef.model.BaseFigure;
import cn.sunline.suncard.sde.workflow.gef.model.EndModel;
import cn.sunline.suncard.sde.workflow.gef.model.LabelModel;
import cn.sunline.suncard.sde.workflow.gef.model.StartModel;
import cn.sunline.suncard.sde.workflow.gef.model.TaskModel;
import cn.sunline.suncard.sde.workflow.gef.policy.LabelModelDirectEditPolicy;
import cn.sunline.suncard.sde.workflow.gef.ui.editor.LabelModelCellEditorLocator;
import cn.sunline.suncard.sde.workflow.gef.ui.editor.LabelModelDirectEditManager;

import cn.sunline.suncard.sde.bs.resource.CacheImage;

/**
 * 图标下Label的EditPart
 * @author  易振强
 * @version 1.0, 2011-12-8
 * @see 
 * @since 1.0
 */
public class LabelEditPart  extends EditPartWithListener {
	private LabelModelDirectEditManager directEditManager = null;
	private final static int SPACE = BaseFigure.FIGURE_HIGHT + BaseFigure.SPACE;
	
	@Override
	protected IFigure createFigure() {
		LabelModel labelModel = (LabelModel) getModel();
		Label label = new Label(labelModel.getLabelName());
		
		Rectangle constraint = labelModel.getConstraint();
		
		label.setTextAlignment(PositionConstants.CENTER);
		label.setForegroundColor(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
		return label;
	}
	
	@Override
	protected void refreshVisuals() {
		Rectangle constraint = ((LabelModel) getModel()).getConstraint().getCopy();
		Rectangle parentConstraint = ((LabelModel) getModel()).getParentModel().getConstraint();
		
		Label label = (Label) getFigure();
		
		int labelWidth;
		if(label.getTextBounds() != null) {
			labelWidth = label.getTextBounds().width / 2;
		} else {
			labelWidth = label.getText().getBytes().length * 7 / 2;
		}
		 
		constraint.x = parentConstraint.x + BaseFigure.FIGURE_WIDTH / 2 - labelWidth;
		constraint.y = parentConstraint.y + SPACE;
		constraint.width = -1;
		constraint.height = -1;
		
		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), constraint);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event){
		if(event.getPropertyName().equals(LabelModel.CONSTRAINT)) {
			refreshVisuals();
			
		} else if(event.getPropertyName().equals(LabelModel.TEXT)){
			((Label)getFigure()).setText((String)event.getNewValue());
			LabelModel labelModel = (LabelModel) getModel();
			AbstractModel parentModel = labelModel.getParentModel();
			if(parentModel instanceof StartModel) {
				StartModel startModel = (StartModel) parentModel;
				startModel.getStartNode().setDescription((String)event.getNewValue());
				
			} else if(parentModel instanceof EndModel) {
				EndModel endModel = (EndModel) parentModel;
				endModel.getEndNode().setDescription((String)event.getNewValue());
				
			} else if(parentModel instanceof TaskModel) {
				TaskModel taskModel = (TaskModel) parentModel;
				taskModel.getTaskNode().getTask().setDescription((String)event.getNewValue());
			}
			
			refreshVisuals();
		} 
	}
	
	@Override
	public void performRequest(Request req) {
		//如果Request是SEQ_DIRECT_EDIT,则执行直接编辑属性的辅助函数
		if(req.getType().equals(RequestConstants.REQ_DIRECT_EDIT)){
			performDirectEdit();
			return ;
		}
		
		super.performRequest(req);
	}
	

	public void performDirectEdit(){
		if(directEditManager == null){
			//如果还没有directManager，则创建一个：类型是Text，位置有图形决定
			directEditManager = new LabelModelDirectEditManager(this, TextCellEditor.class, 
					new LabelModelCellEditorLocator(getFigure()));
		}
		
		directEditManager.show();
	}
	
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new LabelModelDirectEditPolicy());
	}
	
}
