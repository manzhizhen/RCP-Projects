/* 文件名：     AbstractModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.model;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Rectangle;

import cn.sunline.suncard.powerdesigner.gef.figure.TableFigure;
import cn.sunline.suncard.powerdesigner.gef.model.props.PropertySourceAdapter;
import cn.sunline.suncard.powerdesigner.gef.xml.NodeXmlProperty;
import cn.sunline.suncard.powerdesigner.model.DataObjectInterface;



/**
 * 抽象模型
 * 保存模型的一些公共属性和必须实现的方法
 * @author  易振强
 * @version 1.0, 2012-3-26
 * @see 
 * @since 1.0
 */
public abstract class AbstractGefModel extends AbstractListenerGefModel  implements IAdaptable{
	protected List<AbstractLineGefModel> sourceConnection = new ArrayList<AbstractLineGefModel>();	// 源连接线
	protected List<AbstractLineGefModel> targetConnection = new ArrayList<AbstractLineGefModel>();	// 目标连接线
	
	public static final String SOURCE_CONNECTION = "source_connection";		//源连接线变动标记
	public static final String TARGET_CONNECTION = "target_connection";		//目标连接线变动标记
	
	public static final String TEXT = "_text";
	public static final String CONSTRAINT = "_constraint";
	
	// 这个字段是在程序运行时使用的，而NodeXmlProperty中的rectangle是文件保存时使用的
	protected Rectangle constraint = new Rectangle();
	
	protected NodeXmlProperty nodeXmlProperty = new NodeXmlProperty();
	
	private AbstractListenerGefModel parentGefModel;	// 记录其所属的父亲GefModel
	
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
		
		nodeXmlProperty.getRectangle().setX(constraint.x());
		nodeXmlProperty.getRectangle().setY(constraint.y());
		nodeXmlProperty.getRectangle().setWidth(constraint.width);
		nodeXmlProperty.getRectangle().setHeight(constraint.height);	

		firePropertyListenerChange(CONSTRAINT, null, constraint);
	}
	
	/**
	 * 获得以该模型出发的源连接线
	 */
	public List<AbstractLineGefModel> getSourceConnections() {
		return sourceConnection;
	}

	/**
	 * 获得以该模型为目标的连接线
	 */
	public List<AbstractLineGefModel> getTargetConnections() {
		return targetConnection;
	}

	/**
	 * 设置该模型出发的源连接线List
	 */
	public void setSourceConnections(List<AbstractLineGefModel> source) {
		sourceConnection = source;
	}

	/**
	 * 设置以该模型为目标的连接线List
	 */
	public void setTargetConnections(List<AbstractLineGefModel> target) {
		targetConnection = target;
	}

	/**
	 * 添加一条以该模型出发的源连接线
	 */
	public void addSourceConnection(
			AbstractLineGefModel abstractConnectionModel) {
		sourceConnection.add(abstractConnectionModel);
		nodeXmlProperty.getLineIdList().add(abstractConnectionModel.getLineXmlProperty().getId());
		firePropertyListenerChange(SOURCE_CONNECTION, null, null);
	}

	/**
	 * 添加一条该模型为目标的连接线
	 */
	public void addTargetConnection(
			AbstractLineGefModel abstractConnectionModel) {
		targetConnection.add(abstractConnectionModel);
		firePropertyListenerChange(TARGET_CONNECTION, null, null);
	}
	
	/**
	 * 删除一条以该模型出发的源连接线
	 */
	public void removeSourceConnection(
			AbstractLineGefModel abstractConnectionModel) {
		sourceConnection.remove(abstractConnectionModel);
		nodeXmlProperty.getLineIdList().remove(abstractConnectionModel.getLineXmlProperty().getId());
		firePropertyListenerChange(SOURCE_CONNECTION, null, null);
	}

	/**
	 * 删除一条以该模型为目标的连接线
	 */
	public void removeTargetConnection(
			AbstractLineGefModel abstractConnectionModel) {
		targetConnection.remove(abstractConnectionModel);
		firePropertyListenerChange(TARGET_CONNECTION, null, null);
	}

	
	@Override
	public Object getAdapter(Class adapter) {
		return new PropertySourceAdapter().getAdapter(this);
	}
	
	public AbstractListenerGefModel getParentGefModel() {
		return parentGefModel;
	}

	public void setParentGefModel(AbstractListenerGefModel parentGefModel) {
		this.parentGefModel = parentGefModel;
	}

	public NodeXmlProperty getNodeXmlProperty() {
		return nodeXmlProperty;
	}
	
	public void setNodeXmlProperty(NodeXmlProperty nodeXmlProperty) {
		this.nodeXmlProperty = nodeXmlProperty;
	}

	@Override
	public abstract AbstractGefModel clone();
	
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
	public static AbstractGefModel abstractModelFactory(String type) {
		if(type == null) {
			return null;
		}
		
		if(type.equals(TableGefModel.class.getName())) {
			return new TableGefModel();
			
		} else if(type.equals(TableShortcutGefModel.class.getName())) {
			return new TableShortcutGefModel();
		}
		
		return null;
	}
	
}
