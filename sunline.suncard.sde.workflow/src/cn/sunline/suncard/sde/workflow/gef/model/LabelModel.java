/* 文件名：     LabelModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.model;

import org.eclipse.draw2d.geometry.Rectangle;

import cn.sunline.suncard.sde.workflow.gef.model.props.PropertySourceAdapter;
import cn.sunline.suncard.sde.workflow.model.DataObjectInterface;



/**
 * 标签模型
 * 标签模型属于特殊模型，为了给父模型显示名称之用
 * @author  易振强
 * @version 1.0, 2012-3-26
 * @see 
 * @since 1.0
 */
public class LabelModel extends AbstractModel{
	public final static String CONSTRAINT = "LABEL_CONSTRAINT";
	public final static String TEXT = "LABEL_TEXT";
	
	public AbstractModel parentModel; 	// 该LabelModel的父模型
	private String name;				// 该LabelModel的文本
	private Rectangle constraint = new Rectangle(0, 0, -1, -1);

	public LabelModel(AbstractModel parentModel) {
		this.parentModel = parentModel;
	}
	
	@Override
	public LabelModel getLabelModel() {
		return null;
	}

	@Override
	public void refreshName() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateImage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setConstraint(Rectangle constraint) {
		// 因为LabelModel是随着父模型移动而移动的，所以不能在此方法中改变位置
	}
	
	public void setPosition(Rectangle constraint) {
		this.constraint.x = constraint.x;
		this.constraint.y = constraint.y;
		
		firePropertyListenerChange(CONSTRAINT, null, this.constraint);
	}

	@Override
	public Rectangle getConstraint() {
		return constraint;
	}

	public String getLabelName() {
		return name;
	}

	public void setLabelName(String name) {
		this.name = name;
		firePropertyListenerChange(TEXT, null, name);
	}

	public AbstractModel getParentModel() {
		return parentModel;
	}

	public void setParentModel(AbstractModel parentModel) {
		this.parentModel = parentModel;
	}
	
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}
	@Override
	public DataObjectInterface getDataObject() {
		return null;
	}

	@Override
	public void setDataObject(DataObjectInterface object) {
	}

	public String getName() {
		return name;
	}
}
