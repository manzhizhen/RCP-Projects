/* 文件名：     AbstractModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.model;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Rectangle;

import cn.sunline.suncard.sde.workflow.gef.model.props.PropertySourceAdapter;
import cn.sunline.suncard.sde.workflow.gef.xml.NodeXmlProperty;
import cn.sunline.suncard.sde.workflow.model.DataObjectInterface;


/**
 * 抽象模型
 * 保存模型的一些公共属性和必须实现的方法
 * @author  易振强
 * @version 1.0, 2012-3-26
 * @see 
 * @since 1.0
 */
public abstract class AbstractModel extends AbstractListenerModel  implements IAdaptable, java.io.Serializable{
	protected List<AbstractLineModel> sourceConnection = new ArrayList<AbstractLineModel>();	// 源连接线
	protected List<AbstractLineModel> targetConnection = new ArrayList<AbstractLineModel>();	// 目标连接线
	
	public static final String SOURCE_CONNECTION = "source_connection";		//源连接线变动标记
	public static final String TARGET_CONNECTION = "target_connection";		//目标连接线变动标记
	
	protected LabelModel labelModel;
	public static final String TEXT = "_text";
	public static final String CONSTRAINT = "_constraint";
	
	protected Rectangle constraint = new Rectangle();
	
	protected NodeXmlProperty nodeXmlProperty = new NodeXmlProperty();
	
	
	/**
	 *  设置模型的名字
	 */
	public void setLabelName(String name) {
		labelModel.setLabelName(name);
		nodeXmlProperty.setLabel(name);
	}

	/**
	 *  获得模型的名字
	 */
	public String getLabelName() {
		return labelModel.getLabelName();
	}
	
	/**
	 *  得到对应的LabelModel
	 */
	public LabelModel getLabelModel() {
		return labelModel;
	}

	/**
	 *  获得模型的大小和位置
	 */
	public Rectangle getConstraint() {
		return constraint;
	}
	
	/**
	 *  设置模型的大小和位置
	 */
	public void setConstraint(Rectangle constraint) {
		this.constraint = constraint;
		
		// 因为规定图元不能改变大小
		this.constraint.width = BaseFigure.FIGURE_WIDTH;
		this.constraint.height = BaseFigure.FIGURE_HIGHT;
		
		nodeXmlProperty.getRectangle().setX(constraint.x());
		nodeXmlProperty.getRectangle().setY(constraint.y());
		nodeXmlProperty.getRectangle().setWidth(constraint.width);
		nodeXmlProperty.getRectangle().setHeight(constraint.height);	

		// LabelModel要和父节点一起移动
		labelModel.setPosition(constraint.getCopy());
		
		firePropertyListenerChange(CONSTRAINT, null, constraint);
	}
	
	/**
	 * 获得以该模型出发的源连接线
	 */
	public List<AbstractLineModel> getSourceConnections() {
		return sourceConnection;
	}

	/**
	 * 获得以该模型为目标的连接线
	 */
	public List<AbstractLineModel> getTargetConnections() {
		return targetConnection;
	}

	/**
	 * 设置该模型出发的源连接线List
	 */
	public void setSourceConnections(List<AbstractLineModel> source) {
		sourceConnection = source;
	}

	/**
	 * 设置以该模型为目标的连接线List
	 */
	public void setTargetConnections(List<AbstractLineModel> target) {
		targetConnection = target;
	}

	/**
	 * 添加一条以该模型出发的源连接线
	 */
	public void addSourceConnection(
			AbstractLineModel abstractConnectionModel) {
		sourceConnection.add(abstractConnectionModel);
		nodeXmlProperty.getLineIdList().add(abstractConnectionModel.getLineXmlProperty().getId());
		firePropertyListenerChange(SOURCE_CONNECTION, null, null);
	}

	/**
	 * 添加一条该模型为目标的连接线
	 */
	public void addTargetConnection(
			AbstractLineModel abstractConnectionModel) {
		targetConnection.add(abstractConnectionModel);
		firePropertyListenerChange(TARGET_CONNECTION, null, null);
	}
	
	/**
	 * 删除一条以该模型出发的源连接线
	 */
	public void removeSourceConnection(
			AbstractLineModel abstractConnectionModel) {
		sourceConnection.remove(abstractConnectionModel);
		nodeXmlProperty.getLineIdList().remove(abstractConnectionModel.getLineXmlProperty().getId());
		firePropertyListenerChange(SOURCE_CONNECTION, null, null);
	}

	/**
	 * 删除一条以该模型为目标的连接线
	 */
	public void removeTargetConnection(
			AbstractLineModel abstractConnectionModel) {
		targetConnection.remove(abstractConnectionModel);
		firePropertyListenerChange(TARGET_CONNECTION, null, null);
	}

	/**
	 *  当LabelModel文本刷新时，对父模型进行通知。
	 */
	public abstract void refreshName();
	
	/**
	 *  当模型的状态改变时，改变图标
	 */
	public abstract void updateImage();
	
	
	@Override
	public Object getAdapter(Class adapter) {
		return new PropertySourceAdapter().getAdapter(this);
	}

	public NodeXmlProperty getNodeXmlProperty() {
		return nodeXmlProperty;
	}
	
	/**
	 * 返回模型所绑定的数据对象
	 */
	public abstract DataObjectInterface getDataObject() ;
	
	/**
	 * 设置模型所绑定的数据对象
	 */
	public abstract void setDataObject(DataObjectInterface object) ;
	
	/**
	 * 抽象模型工厂
	 * 可以根据模型类型来返回相应的模型对象
	 */
	public static AbstractModel abstractModelFactory(String type) {
		if(type == null) {
			return null;
		}
		
		if(type.equals(StartModel.class.getName())) {
			return new StartModel();
			
		} else if(type.equals(TaskModel.class.getName())) {
			return new TaskModel();
			
		} else if(type.equals(EndModel.class.getName())) {
			return new EndModel();
			
		}
		
		return null;
	}
	
}
