/*
 * 文件名：.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述： SUNCARD决策引擎系统
 * 修改人： 周兵
 * 修改时间：2001-02-16
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.workflow.gef.outline;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import cn.sunline.suncard.sde.workflow.gef.model.AbstractListenerModel;

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
		((AbstractListenerModel)getModel()).addPropertyChangeLinstener(this);
	}

	@Override
	public void deactivate() {
		((AbstractListenerModel)getModel()).removePropertyChangeLinstener(this);
		super.deactivate();
	}
	
}
