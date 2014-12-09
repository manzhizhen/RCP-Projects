/* 文件名：     GefFigureSwitchXml.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-11
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.workflow.gef.model.AbstractLineModel;
import cn.sunline.suncard.sde.workflow.gef.model.AbstractModel;
import cn.sunline.suncard.sde.workflow.gef.model.LabelModel;
import cn.sunline.suncard.sde.workflow.gef.model.WorkFlowModel;
import cn.sunline.suncard.sde.workflow.model.LineNode;
import cn.sunline.suncard.sde.workflow.model.WorkFlowTreeNode;

/**
 * Gef与对象互转
 * @author  易振强
 * @version 1.0, 2012-4-11
 * @see 
 * @since 1.0
 */
public class GefFigureSwitchXml {
	public static Log logger = LogManager.getLogger(GefFigureSwitchXml.class);
	
	/**
	 * 给新增的模型设定xml属性
	 * @param AbstractModel 需要设定id的模型
	 * @param WorkFlowModel 集合模型
	 */
	public static void initNodeProperty(AbstractModel node, WorkFlowModel workFlowModel) {
		List<AbstractModel> nodeList = workFlowModel.getChildren();
		List<String> idList = new ArrayList<String>();
		// 为了避免重复，先获得所有的id
		for(AbstractModel model : nodeList) {
			idList.add(model.getNodeXmlProperty().getId());
		}
		
		int i = 0;
		String id = node.getClass().getSimpleName();
		// 找到唯一的新的id
		while(true) {
			if(idList.contains(id + i)) {
				i++;
			} else {
				break;
			}
		}
		
		id = id + i;
		node.getNodeXmlProperty().setId(id);
		node.getNodeXmlProperty().setName(id);
		node.getNodeXmlProperty().setType(node.getClass().getName());
	}
	
	/**
	 * 给新增的连接线设定xml属性
	 * @param AbstractModel 需要设定id的模型
	 * @param WorkFlowModel 集合模型
	 */
	public static void initLineProperty(AbstractLineModel line, WorkFlowModel workFlowModel) {
		List<AbstractModel> nodeList = workFlowModel.getChildren();
		List<String> idList = new ArrayList<String>();
		// 为了避免重复，先获得所有的id
		for(AbstractModel model : nodeList) {
			List<AbstractLineModel> lineList = model.getSourceConnections();
			for(AbstractLineModel lineModel : lineList) {
				idList.add(lineModel.getLineXmlProperty().getId());
			}
		}
		
		int i = 0;
		String id = line.getClass().getSimpleName();
		// 找到唯一的新的id
		while(true) {
			if(idList.contains(id + i)) {
				i++;
			} else {
				break;
			}
		}
		
		id = id + i;
		line.getLineXmlProperty().setId(id);
		line.getLineXmlProperty().setName(id);
		line.getLineXmlProperty().setType(line.getClass().getName());
		line.getLineXmlProperty().setSourceNodeId(line.getSource().getNodeXmlProperty().getId()); // 绑定源节点ID
		line.getLineXmlProperty().setTargetNodeId(line.getTarget().getNodeXmlProperty().getId()); // 绑定目标节点ID
	}
	
	/**
	 * 从集合模型生成Document对象
	 * @param WorkFlowModel 集合模型
	 */
	public static Document getDocFromWorkFlowModel(WorkFlowModel workFlowModel) {
		List<AbstractModel> modelList = workFlowModel.getChildren();
		
		Document document = DocumentHelper.createDocument();	//创建document对象
		
		Element rootElemnt = document.addElement("SuncardWorkFlow"); //XML的根节点
		
		// 把模型转化成XML
		changeNodeToXml(rootElemnt, modelList);
		
		// 写入连接线的XML信息
		List<AbstractLineModel> lineList = new ArrayList<AbstractLineModel>();
		// 先获取所有的连接线
		for(AbstractModel model : modelList) {
			lineList.addAll(model.getSourceConnections());
		}
		
		// 把连接线转化成XML
		changeLineToXml(rootElemnt, lineList);
		
		return document;
	}
	
	/**
	 * 从工作流树节点生成Document对象
	 * @param WorkFlowModel 集合模型
	 */
	public static Document getDocFromWorkFlowTreeModel(WorkFlowTreeNode treeNode) {
		Document document = getDocFromWorkFlowModel(treeNode.getModel());
		
		Element treeElement = document.getRootElement();
		treeElement.addAttribute("id", treeNode.getId());
		treeElement.addAttribute("name", treeNode.getName());
		Element desc = treeElement.addElement("description");
		desc.setText(treeNode.getDesc());
		
		return document;
	}
	
	/**
	 * 获得克隆的WorkFlowModel对象，避免修改Editor但未保存时能避免修改WorkFlowModel对象
	 */
	public static WorkFlowModel getWorkFlowTreeNodeClone(WorkFlowModel model) {
		Document doc = getDocFromWorkFlowModel(model);
		return getWorkFlowModelFromDoc(doc);
	}
	
	
	/**
	 * 将xml数据对象转化成树节点对象
	 * @throws DocumentException 
	 */
	public static WorkFlowTreeNode getWorkFlowTreeNodeFromXml(String filePath) throws DocumentException {
//		//将xml字符串解析为document
//		Document document = DocumentHelper.parseText(xml);
		
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(new File(filePath));
		
		return getWorkFlowTreeNodeFromDoc(document);
	}
	
	/**
	 * 从Doc文档获得WorkFlowTreeNode对象
	 */
	public static WorkFlowTreeNode getWorkFlowTreeNodeFromDoc(Document document) {
		Element root = document.getRootElement();
		
		WorkFlowTreeNode treeNode = new WorkFlowTreeNode();
		treeNode.setModel(getWorkFlowModelFromDoc(document));
		treeNode.setId(root.attributeValue("id"));
		treeNode.setName(root.attributeValue("name"));
		treeNode.setDesc(root.element("description").getTextTrim());
		
		return treeNode;
	}
	
	/**
	 * 从Doc文档获得WorkFlowModel对象
	 */
	public static WorkFlowModel getWorkFlowModelFromDoc(Document document) {
		WorkFlowModel workFlowModel = new WorkFlowModel();
		
		Element root = document.getRootElement();

		// 获得抽象模型List
		workFlowModel.addChildList(changeXmlToNodes(root.element("nodes")));
		
		// 获得连接线的List
		List<AbstractLineModel> lineList = changeXmlToLines(root.element("connections"));
		
		// 给模型绑定连接线
		bindModelAndLine(workFlowModel.getChildren(), lineList);
		
		return workFlowModel;
	}
	
	/**
	 * 将模型和其连接线相互绑定
	 */
	private static void bindModelAndLine(List<AbstractModel> modelList, List<AbstractLineModel> lineList) {
		if(modelList == null || lineList == null) {
			logger.error("模型或连接线的List为空，无法相互绑定！");
			return ;
		}
		
		// 定义一个Map，方便查找
		Map<String, AbstractModel> modelMap = new HashMap<String, AbstractModel>();
		for(AbstractModel model : modelList) {
			modelMap.put(model.getNodeXmlProperty().getId(), model);
		}
		
		for(AbstractLineModel lineModel : lineList) {
			String sourceId = lineModel.getLineXmlProperty().getSourceNodeId();
			String targetId = lineModel.getLineXmlProperty().getTargetNodeId();
			
			if(sourceId == null || targetId == null) {
				logger.error("从连接线获取源节点Id:" + sourceId + " 或目标节点的Id:" + targetId + "时，为空！");
				continue;
			}
			
			AbstractModel sourceModel = modelMap.get(sourceId);
			AbstractModel targetModel = modelMap.get(targetId);
			
			if(sourceModel == null || targetModel == null) {
				logger.error("从连接线获取源节点和目标节点的sourceId=" + sourceId + " 或targetId=" + targetId + " 在模型列表中没有，无法绑定！");
				continue;
			}
			
			// 设置连接线
			lineModel.setSource(sourceModel);
			lineModel.setTarget(targetModel);
			
			// 设置模型
			sourceModel.getSourceConnections().add(lineModel);
			targetModel.getTargetConnections().add(lineModel);
		}
	}
	
	/**
	 * 将xml文件解析成抽象模型
	 */
	public static List<AbstractModel> changeXmlToNodes(Element element) {
		if(element == null) {
			logger.error("从Xml文件得到的抽象模型Element为空，无法解析！");
			return null;
		}
		
		List<AbstractModel> abstractModelList = new ArrayList<AbstractModel>();
		List<Element> nodeList = element.elements("node");
		if(nodeList == null) {
			logger.info("从Xml文件得到的抽象模型List为空，没有对象！");
			return abstractModelList;
		}
		
		for(Element node : nodeList) {
			String id = node.attributeValue("id");
			String name = node.attributeValue("name");
			String type = node.attributeValue("type");
			
			String label = node.elementText("label");
			Rectangle rectangle = new Rectangle() ;
			rectangle.setX(new Integer(node.element("location").attributeValue("x").trim()).intValue());
			rectangle.setY(new Integer(node.element("location").attributeValue("y").trim()).intValue());
			rectangle.setWidth(new Integer(node.element("location").attributeValue("width").trim()).intValue());
			rectangle.setHeight(new Integer(node.element("location").attributeValue("height").trim()).intValue());
			
			// 根据类型获得一个新模型
			AbstractModel abstractModel = AbstractModel.abstractModelFactory(type);
			if(abstractModel == null) {
				logger.error("id:" + id + " name:" + name + " type:" + type + "  无效的模型type！");
				continue;
			}
			
			// 设置模型的xml属性
			NodeXmlProperty nodeProperty = abstractModel.getNodeXmlProperty();
			nodeProperty.setId(id);
			nodeProperty.setName(name);
			nodeProperty.setType(type);
//			nodeProperty.setLabel(label);
//			nodeProperty.setPoint(point);
			
			// 设置该模型xml属性的连接线
			List<Element> connList = node.element("connections").elements("connection");
			for(Element connElement : connList) {
				nodeProperty.getLineIdList().add(connElement.attributeValue("lineId").trim());
			}

			// 设置模型的位置
			abstractModel.setConstraint(rectangle);
			// 设置模型上Label的显示内容
			abstractModel.setLabelName(label);
			
			// 设置该模型的数据对象
			if(abstractModel.getDataObject() != null) {
				abstractModel.getDataObject().getObjectFromElement(node.element("dataObject"));
			}
			
			// 将其添加到列表
			abstractModelList.add(abstractModel);
			abstractModelList.add(abstractModel.getLabelModel());	// 別忘了添加LabelModel
		}
		
		return abstractModelList;
	}
	
	/**
	 * 将xml文件解析成抽象模型
	 */
	public static List<AbstractLineModel> changeXmlToLines(Element element) {
		if(element == null) {
			logger.error("从Xml文件得到的连接线的Element为空，无法解析！");
			return null;
		}
		
		// 用来存储获得的连接线List
		List<AbstractLineModel> abstractLinelList = new ArrayList<AbstractLineModel>();
		List<Element> lineList = element.elements("connection");
		
		if(lineList == null) {
			logger.info("从Xml文件得到的抽象模型List为空，没有对象！");
			return abstractLinelList;
		}
		
		for(Element lineElement : lineList) {
			String id = lineElement.attributeValue("id");
			String name = lineElement.attributeValue("name");
			String label = lineElement.elementText("label");
			String type = lineElement.elementText("type");
			String sourceNode = lineElement.elementText("sourceNode").trim();
			String targetNode = lineElement.elementText("targetNode").trim();
			
			// 设置连接线锚点
			List<Element> anchorList = lineElement.element("anchors").elements("anchor");
			List<Point> bendpoints = new ArrayList<Point>();
			for(Element anchorElement : anchorList) {
				Point point = new Point();
				point.setX(new Integer(anchorElement.attributeValue("x").trim()).intValue());
				point.setY(new Integer(anchorElement.attributeValue("y").trim()).intValue());
				
				bendpoints.add(point);
			}
			
			// 根据连接线类型来生成的连接线对象
			AbstractLineModel lineModel = AbstractLineModel.abstractLineFactory(type);
			if(lineModel == null) {
				logger.error("id:" + id + " name:" + name + " type:" + type + "  无效的连接线type！");
				continue;
			}
			
			// 设置该模型的数据对象
			if(lineModel.getDataObject() != null) {
				lineModel.getDataObject().getObjectFromElement(lineElement.
						element("dataObject").element(LineNode.getElementName()));
			}
			
			// 获得连接线对象的xml属性
			LineXmlProperty linePro = lineModel.getLineXmlProperty();
			linePro.setId(id);
			linePro.setName(name);
//			linePro.setLabel(label);
			linePro.setType(type);
//			linePro.setBendPointList(bendpoints);
			linePro.setSourceNodeId(sourceNode);
			linePro.setTargetNodeId(targetNode);
			
			lineModel.setLabelText(label);				// 设置连接线上展现的标签
			lineModel.setBendpoints(bendpoints);		// 设置连接线的锚点
			
			// 将连接线对象放置List
			abstractLinelList.add(lineModel);
		}
		
		return abstractLinelList;
	}
	
	
	/**
	 * 把模型转化成XML
	 * @param 父节点的Element
	 * @param 需要转换的AbstractModel列表
	 */
	public static Element changeNodeToXml(Element rootElemnt, List<AbstractModel> modelList) {
		//-----------------写入模型的XML信息--------------------
		Element nodesElement = rootElemnt.addElement("nodes");

		for(AbstractModel model : modelList) {
			if(model instanceof LabelModel) {
				continue ;
			}
			
			NodeXmlProperty nodePro = model.getNodeXmlProperty();
			Element nodeElement = nodesElement.addElement("node");
			
			nodeElement.addAttribute("id", nodePro.getId());				// 设置id
			nodeElement.addAttribute("name", nodePro.getName());			// 设置name
			nodeElement.addAttribute("type", nodePro.getType());
			
			Element labelElement = nodeElement.addElement("label"); 		// 设置label	
			labelElement.setText(nodePro.getLabel() + "");
			
			Element locationElement = nodeElement.addElement("location"); 	// 设置location	
			locationElement.addAttribute("x", nodePro.getRectangle().x() + "");
			locationElement.addAttribute("y", nodePro.getRectangle().y() + "");
			locationElement.addAttribute("width", nodePro.getRectangle().width + "");
			locationElement.addAttribute("height", nodePro.getRectangle().height + "");
			
			// 添加数据对象
			Element dataObjectElement = nodeElement.addElement("dataObject");
			if(model.getDataObject() != null) {
				model.getDataObject().getElementFromObject(dataObjectElement);
			}
			
			// 添加连接线
			Element connectionsElement = nodeElement.addElement("connections"); 	// 设置connections	
			for(String lineId : nodePro.getLineIdList()) {
				Element connectionElement = connectionsElement.addElement("connection"); 
				connectionElement.addAttribute("lineId", lineId);
			}
		}
		
		
		
		return nodesElement;
	}
	
	/**
	 * 把连接线转化成XML
	 * @param 父节点的Element
	 * @param 需要转换的AbstractModel列表
	 */
	public static Element changeLineToXml(Element rootElemnt, List<AbstractLineModel> lineList) {
		Element connectionsElement = rootElemnt.addElement("connections");	// 设置connections
		for(AbstractLineModel line : lineList) {
			LineXmlProperty linePro = line.getLineXmlProperty();
			
			Element connectionElement = connectionsElement.addElement("connection");
			connectionElement.addAttribute("id", linePro.getId());			// 设置id
			connectionElement.addAttribute("name", linePro.getName());		// 设置name
			
			Element labelElement = connectionElement.addElement("label");	// 设置label
			labelElement.setText(linePro.getLabel() + "");	
			
			Element typeElement = connectionElement.addElement("type");	// 设置type
			typeElement.setText(linePro.getType() + "");	
			
			Element sourceElement = connectionElement.addElement("sourceNode");	// 设置源节点
			sourceElement.setText(linePro.getSourceNodeId() + "");	
			
			Element targetElement = connectionElement.addElement("targetNode");	// 设置目标节点
			targetElement.setText(linePro.getTargetNodeId() + "");
			
			// 添加数据对象
			Element dataObjectElement = connectionElement.addElement("dataObject");
			if(line.getDataObject() != null) {
				line.getDataObject().getElementFromObject(dataObjectElement);
			}
			
			
			// 添加锚点
			Element anchorsElement = connectionElement.addElement("anchors");	// 设置anchors
			for(Point point : linePro.getBendPointList()) {
				Element anchorElement = anchorsElement.addElement("anchor");	// 设置anchor
				anchorElement.addAttribute("x", point.x() + "");
				anchorElement.addAttribute("y", point.y() + "");				
			}
			
		}
		
		return connectionsElement;
	}
} 
