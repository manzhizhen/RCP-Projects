/*
 * 文件名：.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述： SUNCARD决策引擎系统
 * 修改人： 周兵
 * 修改时间：2001-02-16
 * 修改内容：新增
*/
package cn.sunline.suncard.powerdesigner.gef.outline;

import java.beans.PropertyChangeListener;
import org.eclipse.gef.editparts.AbstractTreeEditPart;

import cn.sunline.suncard.powerdesigner.gef.model.AbstractGefModel;



/**
 * 开始模型树的EditPart
 * @author  易振强
 * @version 1.0, 2012-4-9
 * @see 
 * @since 1.0
 */
public abstract class TreeEditPart extends AbstractTreeEditPart implements
		PropertyChangeListener {

	@Override
	public void activate() {
		super.activate();
		((AbstractGefModel)getModel()).addPropertyChangeLinstener(this);
	}

	@Override
	public void deactivate() {
		((AbstractGefModel)getModel()).removePropertyChangeLinstener(this);
		super.deactivate();
	}
	
}
