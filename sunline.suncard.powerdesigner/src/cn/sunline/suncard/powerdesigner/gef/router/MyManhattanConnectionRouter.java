/* 文件名：     MyManhattanConnectionRouter.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-16
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.router;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.AbstractRouter;
import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.geometry.Vector;

/**
 * 
 * @author  Manzhizhen
 * @version 1.0, 2012-11-16
 * @see 
 * @since 1.0
 */
public class MyManhattanConnectionRouter extends AbstractRouter{
	private ManhattanConnectionRouter manhattanConnectionRouter = new ManhattanConnectionRouter();

	private static final PrecisionPoint A_POINT = new PrecisionPoint();

	@Override
	public Object getConstraint(Connection connection) {
		PointList pointList = connection.getPoints();
		List<AbsoluteBendpoint> points = new ArrayList<AbsoluteBendpoint>();
		for(int i = 0; i < pointList.size(); i++) {
			points.add(new AbsoluteBendpoint(pointList.getPoint(i)));
		}
		
		return points;
	}
	
	@Override
	public void route(Connection conn) {
		manhattanConnectionRouter.route(conn);
//		// 清空连线的所有点
//		PointList points = conn.getPoints();
//		points.removeAllPoints();
//		
//		// 得到目标和源的中心点
//		Point sourceRef = conn.getSourceAnchor().getReferencePoint();
//		Point targetRef = conn.getTargetAnchor().getReferencePoint();
//		A_POINT.setLocation(sourceRef.x, targetRef.y);
//		
//		// 得到起始点和结束点
//		Point startPoint = conn.getSourceAnchor().getLocation(A_POINT);
//		Point endPoint = conn.getTargetAnchor().getLocation(A_POINT);
//		
//		// 添加起始点
//		A_POINT.setLocation(startPoint);
//		conn.translateToRelative(A_POINT);
//		points.addPoint(A_POINT);
//		
//		// 添加转折点
//		A_POINT.setLocation(sourceRef.x, targetRef.y);
//		conn.translateToRelative(A_POINT);
//		points.addPoint(A_POINT);
//		
//		// 添加结束点
//		A_POINT.setLocation(endPoint);
//		conn.translateToRelative(A_POINT);
//		points.addPoint(A_POINT);
//		
//		// 设置连线经过的所有点
//		conn.setPoints(points);

		
		
	}
	

	@Override
	public void setConstraint(Connection connection, Object constraint) {
		// 清空连线的所有点
		PointList points = connection.getPoints();
		points.removeAllPoints();
		
		List<AbsoluteBendpoint> pointList = (List<AbsoluteBendpoint>) constraint;
		
		for(AbsoluteBendpoint bendPoint : pointList) {
			points.addPoint(new Point(bendPoint.x, bendPoint.y));
		}
		
		connection.setPoints(points);

//		super.setConstraint(connection, constraint);
	}
	
}
