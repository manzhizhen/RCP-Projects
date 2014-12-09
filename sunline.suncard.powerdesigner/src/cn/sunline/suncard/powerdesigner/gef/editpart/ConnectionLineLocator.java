/* 文件名：     ConnectionLineLocator.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	连接线星号的定位
 * 修改人：     易振强
 * 修改时间：2011-11-22
 * 修改内容：创     建
 */
package cn.sunline.suncard.powerdesigner.gef.editpart;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.geometry.Point;

/**
 * 连接线星号的定位
 * @author  易振强
 * @version 1.0, 2011-11-22
 * @since 1.0
 */
public class ConnectionLineLocator extends ConnectionLocator {
	public final static int x = 10;
	public final static int y = 15;
	
	public final static int hight = 15;
	
	
	private int offsetx;
	private int offsety;

	public ConnectionLineLocator(Connection connection, int align) {
		super(connection, align);
	}
	
	public ConnectionLineLocator(Connection connection) {
		super(connection);
	}
	
	public ConnectionLineLocator(Connection connection, int align, int offsetx, int offsety) {
		super(connection, align);
		this.offsetx = offsetx;
		this.offsety = offsety;
	}
	
	@Override
	protected Point getReferencePoint() {
		Point point =  super.getReferencePoint();
		point.setLocation(point.x + offsetx, point.y + offsety);
		return point;
	}
}
