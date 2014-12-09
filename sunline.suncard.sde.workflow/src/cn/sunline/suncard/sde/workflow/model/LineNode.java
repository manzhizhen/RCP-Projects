/* 文件名：     LineNode.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-16
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.model;

import org.dom4j.Element;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 连接线上绑定的数据对象 
 * 本来List<ConnectLine> connectLines 和 List<CommonNode> commonNodes
 * 部分应该是放在DecisionNode里面的， 但考虑到功能分离，现在每个连接线绑定的LineNode对象包含一个ConnectLine
 * 和一个CommonNode对象
 * 后来又发现，一个ConnectLine对象主要是为了记录对应的commonNode对象的Id，所以，这个对象可以省去了。
 * 
 * @author 易振强
 * @version 1.0, 2012-4-16
 * @see
 * @since 1.0
 */
public class LineNode implements DataObjectInterface{
	private static String elementName = "lineNode";
	
//	private ConnectLine connectLine;
	private CommonNode commonNode;
	
	private Log logger = LogManager.getLogger(LineNode.class.getName());

	/**
	 * 
	 */
	public LineNode() {
//		connectLine = new ConnectLine();
		commonNode = new CommonNode();
	}
	
//	public ConnectLine getConnectLine() {
//		return connectLine;
//	}
//
//	public void setConnectLine(ConnectLine connectLine) {
//		this.connectLine = connectLine;
//	}

	public CommonNode getCommonNode() {
		return commonNode;
	}

	public void setCommonNode(CommonNode commonNode) {
		this.commonNode = commonNode;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element decisionElement = parent.addElement(elementName);
		
//		connectLine.getElementFromObject(decisionElement);
		
		commonNode.getElementFromObject(decisionElement);
		
		return decisionElement;
		
	}

	@Override
	public LineNode getObjectFromElement(Element element) {
		if(element == null ) {
			logger.warn("LineNode的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("LineNode的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
//		connectLine.getObjectFromElement(lineElement.element("connectline"));
		
		commonNode.getObjectFromElement(element.element(CommonNode.getElementName()));
		
		return this;
	}
	
	public static String getElementName() {
		return elementName;
	}

}
