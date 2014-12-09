/* 文件名：     AbstractConnectionModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	抽象连接线模型
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;

import cn.sunline.suncard.powerdesigner.gef.xml.LineXmlProperty;
import cn.sunline.suncard.powerdesigner.model.DataObjectInterface;



/**
 * 抽象连接线模型
 * 实现连接线公共的方法和保存公共的属性
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 */

public abstract class AbstractLineGefModel extends AbstractListenerGefModel implements IAdaptable, Cloneable, java.io.Serializable{
	public static final String P_BEND_POINT = "_bend_point";	// 标识 bending 点位置改变的 ID
	public static final String LINK_COLOR = "_link_color";	// 标志连接线颜色改变通知
	
	private AbstractGefModel source;
	private AbstractGefModel target;
	
	private List<Point> bendpoints = new ArrayList<Point>();	// 控制点（锚点）点位置
	protected LineXmlProperty lineXmlProperty = new LineXmlProperty();
	
	/**
	 *  添加控制点并通知 Editpart
	 */
	public void addBendpoint(int index, Point point) {
		bendpoints.add(index, point);
		lineXmlProperty.setBendPointList(bendpoints);
		firePropertyListenerChange(P_BEND_POINT, null, null);
	}

	/**
	 *  删除控制点并通知 Editpart
	 */
	public void removeBendpoint(int index) {
		bendpoints.remove(index);
		lineXmlProperty.setBendPointList(bendpoints);
		firePropertyListenerChange(P_BEND_POINT, null, null);
	}

	/**
	 *  控制点发生变化时并通知 Editpart
	 */
	public void replaceBendpoint(int index, Point point) {
		bendpoints.set(index, point);
		lineXmlProperty.setBendPointList(bendpoints);
		firePropertyListenerChange(P_BEND_POINT, null, null);
	}

	public List<Point> getBendpoints() {
		return bendpoints;
	}

	/**
	 * 在源模型上添加该连接线
	 */
	public void attachSource() {
		// 连接的头端添加到Source
		if (!source.getSourceConnections().contains(this)) {
			source.addSourceConnection(this);
		}
	}

	/**
	 * 在目标模型上添加该连接线
	 */
	public void attachTarget() {
		// 连接的尾端添加到Source
		if (!target.getTargetConnections().contains(this)) {
			target.addTargetConnection(this);
		}
	}

	/**
	 * 在源模型上删除该连接线
	 */
	public void detachSource() {
		source.removeSourceConnection(this);
	}

	/**
	 * 在目标模型上删除该连接线
	 */
	public void detachTarget() {
		target.removeTargetConnection(this);
	}

	public AbstractGefModel getSource() {
		return source;
	}

	public void setSource(AbstractGefModel source) {
		this.source = source;
	}

	public AbstractGefModel getTarget() {
		return target;
	}

	public void setTarget(AbstractGefModel target) {
		this.target = target;
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}
	
	@Override
	public abstract Object clone();

	public LineXmlProperty getLineXmlProperty() {
		return lineXmlProperty;
	}
	
	public abstract void setLabelText(String text);
	
	/**
	 * 抽象连接线工厂
	 * 可以根据连接线类型来返回相应的连接线对象
	 */
	public static AbstractLineGefModel abstractLineFactory(String type) {
		if(type == null) {
			return null;
		}
		
		if(type.equals(LineGefModel.class.getName())) {
			return new LineGefModel();
			
		} 
		
		return null;
	}

	public void setBendpoints(List<Point> bendpoints) {
		if(bendpoints == null) {
			return ;
		}
		
		int i = 0;
		for(Point point : bendpoints) {
			addBendpoint(i, point);
			i++;
		}
		
		this.bendpoints = bendpoints;
	}
	
	/**
	 * 返回模型所绑定的数据对象
	 */
	public abstract DataObjectInterface getDataObject() ;
	
	/**
	 * 设置模型所绑定的数据对象
	 */
	public abstract void setDataObject(DataObjectInterface object) ;

}
