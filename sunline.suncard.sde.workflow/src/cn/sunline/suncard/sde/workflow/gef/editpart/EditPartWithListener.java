/* 文件名：     EditPartWithListener.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	创建一个抽象类，来创建注册监听器
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.editpart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import cn.sunline.suncard.sde.workflow.gef.model.AbstractListenerModel;
import cn.sunline.suncard.sde.workflow.gef.model.AbstractModel;
import cn.sunline.suncard.sde.workflow.gef.model.StartModel;

/**
 * 创建一个抽象类，来创建注册监听器
 * 为了方便起见，实现NodeEditPart来帮模型统一实现对接连接线
 * @author    易振强
 * @version   [1.0, 2011-11-04]
 * @see       
 * @since     1.0 
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class EditPartWithListener extends AbstractGraphicalEditPart
		implements PropertyChangeListener, NodeEditPart{

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
	}

	@Override
	protected IFigure createFigure() {
		return null;
	}

	@Override
	protected void createEditPolicies() {

	}

	@Override
	public void activate() {
		super.activate();
		//editPart把自己作为监听器
		((AbstractListenerModel)getModel()).addPropertyChangeLinstener(this);
		
	}

	@Override
	public void deactivate() {
		super.deactivate();
		((AbstractListenerModel)getModel()).removePropertyChangeLinstener(this);
	}
	
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}
	
}
