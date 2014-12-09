/* 文件名：     GefFigureSwitchXml.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-11
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.gef.model.AbstractLineGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.DatabaseDiagramGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableShortcutGefModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.LineModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.TableShortcutModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * Gef与对象互转
 * 
 * @author 易振强
 * @version 1.0, 2012-4-11
 * @see
 * @since 1.0
 */
public class GefFigureSwitchXml {
	public static Log logger = LogManager.getLogger(GefFigureSwitchXml.class);

	/**
	 * 给新增的模型设定xml属性 并自动新增新的表名及其描述
	 * 
	 * @param model 需要设定id的模型
	 * @param physicalDiagramModel 物理图形模型
	 * @param diagramGefModel 集合模型
	 * @param newTableName  新的表格名称，如果需要自动生成，可传入null
	 * @param newTableDesc  新的表格描述，如果需要自动生成，可传入null     
	 */
	public static void initNodeProperty(AbstractGefModel model,
			PhysicalDiagramModel physicalDiagramModel, DatabaseDiagramGefModel diagramGefModel, String newTableName, String newTableDesc) {
		FileModel fileModel = FileModel.getFileModelFromObj(physicalDiagramModel
				.getPackageModel());
		
		if(fileModel == null) {
			logger.warn("无法获取到FileModel，initNodeProperty()调用失败！");
			return ;
		}
		
		if(model instanceof TableGefModel) {
			// 获取该文件下的所有表模型
			Set<TableModel> tableModelSet = fileModel.getAllTableModel();
			
//			// 需要除去当前物理图形模型里面的表模型，因为要把它换成当前用户正在编辑的表模型，以免自动生成表名时错误
//			tableModelSet.addAll(physicalDiagramModel.getTableMap().values());
//			List<AbstractGefModel> gefModelList = diagramGefModel.getChildren();
//			for(AbstractGefModel abstractGefModel : gefModelList) {
//				if(abstractGefModel instanceof TableGefModel) {
//					tableModelSet.add((TableModel) abstractGefModel.getDataObject());
//				}
//			}
			
			List<String> tableModelNameList = new ArrayList<String>(); // 用来储存所有存在的表名
			for (TableModel tableModel : tableModelSet) {
				tableModelNameList.add(tableModel.getTableName());
			}
	
			TableModel tableModel = (TableModel) model.getDataObject();
			if(newTableName != null ) {
				if(!tableModelNameList.contains(newTableName)) {
					model.getNodeXmlProperty().setId(tableModel.getId());
					model.getNodeXmlProperty().setType(model.getClass().getName());
					
					tableModel.setTableName(newTableName);
					tableModel.setTableDesc(newTableDesc == null 
							? newTableName : newTableDesc);
				} else {
					for (int tableNum = 1;; tableNum++) {
						if (!tableModelNameList.contains(newTableName
								+ "_" + tableNum)) {
							model.getNodeXmlProperty().setId(tableModel.getId());
							model.getNodeXmlProperty().setType(model.getClass().getName());
							
							tableModel.setTableName(newTableName
									+ "_" + tableNum);
							tableModel.setTableDesc(newTableDesc == null 
									? newTableName : newTableDesc);
							
							break;
						}
					}
				}
			} else {
				for (int tableNum = tableModelNameList.size() + 1;; tableNum++) {
					if (!tableModelNameList.contains(DmConstants.AUTO_TABLE_NAME_PREFIX
							+ "_" + tableNum)) {
						model.getNodeXmlProperty().setId(tableModel.getId());
						model.getNodeXmlProperty().setType(model.getClass().getName());
		
						tableModel.setTableName(DmConstants.AUTO_TABLE_NAME_PREFIX
								+ "_" + tableNum);
						tableModel.setTableDesc(DmConstants.AUTO_TABLE_DESC_PREFIX
								+ "_" + tableNum);
		
						break;
					}
				}
			}
			
			
		} else if(model instanceof TableShortcutGefModel) {
			TableShortcutModel tableShortcutModel = (TableShortcutModel) model.getDataObject();
			
			model.getNodeXmlProperty().setId(tableShortcutModel.getId());
			model.getNodeXmlProperty().setType(model.getClass().getName());
		}
		
	}

	/**
	 * 给新增的连接线设定xml属性
	 * 
	 * @param AbstractGefModel
	 *            需要设定id的模型
	 * @param Object 能找到对应FileModel的对象
	 *            
	 */
	public static void initLineProperty(AbstractLineGefModel line,
			Object canFindFileModel) {
		FileModel fileModel = FileModel.getFileModelFromObj(canFindFileModel);
		if(fileModel == null) {
			logger.error("找不到对应的FileModel，无法初始化连接线信息！！");
			return ;
		}
		
		String id = LineXmlPropertyFactory.addNewLineIdToFactory(fileModel);
		if(id == null) {
			return ;
		}
		line.getLineXmlProperty().setId(id);
		line.getLineXmlProperty().setType(line.getClass().getName());
		line.getLineXmlProperty().setSourceNodeId(
				line.getSource().getNodeXmlProperty().getId()); // 绑定源节点ID
		line.getLineXmlProperty().setTargetNodeId(
				line.getTarget().getNodeXmlProperty().getId()); // 绑定目标节点ID
	}



	// /**
	// * 从工作流树节点生成Document对象
	// * @param WorkFlowModel 集合模型
	// */
	// public static Document getDocFromWorkFlowTreeModel(WorkFlowTreeNode
	// treeNode) {
	// Document document = getDocFromWorkFlowModel(treeNode.getModel());
	//
	// Element treeElement = document.getRootElement();
	// treeElement.addAttribute("id", treeNode.getId());
	// treeElement.addAttribute("name", treeNode.getName());
	// Element desc = treeElement.addElement("description");
	// desc.setText(treeNode.getDesc());
	//
	// return document;
	// }

	// /**
	// * 获得克隆的WorkFlowModel对象，避免修改Editor但未保存时能避免修改DatabaseDiagramModel对象
	// */
	// public static DatabaseDiagramGefModel
	// getWorkFlowTreeNodeClone(PhysicalDiagramModel model) {
	// Document doc = getDocFromFileModel(model);
	// return getWorkFlowModelFromDoc(doc);
	// }

	// /**
	// * 将xml数据对象转化成树节点对象
	// * @throws DocumentException
	// */
	// public static WorkFlowTreeNode getWorkFlowTreeNodeFromXml(String
	// filePath) throws DocumentException {
	// // //将xml字符串解析为document
	// // Document document = DocumentHelper.parseText(xml);
	//
	// SAXReader saxReader = new SAXReader();
	// Document document = saxReader.read(new File(filePath));
	//
	// return getWorkFlowTreeNodeFromDoc(document);
	// }

	// /**
	// * 从Doc文档获得WorkFlowTreeNode对象
	// */
	// public static WorkFlowTreeNode getWorkFlowTreeNodeFromDoc(Document
	// document) {
	// Element root = document.getRootElement();
	//
	// WorkFlowTreeNode treeNode = new WorkFlowTreeNode();
	// treeNode.setModel(getWorkFlowModelFromDoc(document));
	// treeNode.setId(root.attributeValue("id"));
	// treeNode.setName(root.attributeValue("name"));
	// treeNode.setDesc(root.element("description").getTextTrim());
	//
	// return treeNode;
	// }



	/**
	 * 将模型和其连接线相互绑定
	 */
	private static void bindModelAndLine(List<AbstractGefModel> modelList,
			List<AbstractLineGefModel> lineList) {
		if (modelList == null || lineList == null) {
			logger.error("模型或连接线的List为空，无法相互绑定！");
			return;
		}

		// 定义一个Map，方便查找
		Map<String, AbstractGefModel> modelMap = new HashMap<String, AbstractGefModel>();
		for (AbstractGefModel model : modelList) {
			modelMap.put(model.getNodeXmlProperty().getId(), model);
		}

		for (AbstractLineGefModel lineModel : lineList) {
			String sourceId = lineModel.getLineXmlProperty().getSourceNodeId();
			String targetId = lineModel.getLineXmlProperty().getTargetNodeId();

			if (sourceId == null || targetId == null) {
				logger.error("从连接线获取源节点Id:" + sourceId + " 或目标节点的Id:"
						+ targetId + "时，为空！");
				continue;
			}

			AbstractGefModel sourceModel = modelMap.get(sourceId);
			AbstractGefModel targetModel = modelMap.get(targetId);

			if (sourceModel == null || targetModel == null) {
				logger.error("从连接线获取源节点和目标节点的sourceId=" + sourceId
						+ " 或targetId=" + targetId + " 在模型列表中没有，无法绑定！");
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
	public static List<AbstractGefModel> changeXmlToNodes(Element element) {
		if (element == null) {
			logger.error("从Xml文件得到的抽象模型Element为空，无法解析！");
			return null;
		}

		List<AbstractGefModel> abstractModelList = new ArrayList<AbstractGefModel>();
		List<Element> nodeList = element.elements("node");
		if (nodeList == null) {
			logger.info("从Xml文件得到的抽象模型List为空，没有对象！");
			return abstractModelList;
		}

		for (Element node : nodeList) {
			String id = node.attributeValue("id");
			String name = node.attributeValue("name");
			String type = node.attributeValue("type");

			String label = node.elementText("label");
			Rectangle rectangle = new Rectangle();
			rectangle.setX(new Integer(node.element("location")
					.attributeValue("x").trim()).intValue());
			rectangle.setY(new Integer(node.element("location")
					.attributeValue("y").trim()).intValue());
			rectangle.setWidth(new Integer(node.element("location")
					.attributeValue("width").trim()).intValue());
			rectangle.setHeight(new Integer(node.element("location")
					.attributeValue("height").trim()).intValue());

			// 根据类型获得一个新模型
			AbstractGefModel abstractModel = AbstractGefModel
					.abstractModelFactory(type);
			if (abstractModel == null) {
				logger.error("id:" + id + " name:" + name + " type:" + type
						+ "  无效的模型type！");
				continue;
			}

			// 设置模型的xml属性
			NodeXmlProperty nodeProperty = abstractModel.getNodeXmlProperty();
			nodeProperty.setId(id);
			nodeProperty.setType(type);
			// nodeProperty.setLabel(label);
			// nodeProperty.setPoint(point);

			// 设置该模型xml属性的连接线
			List<Element> connList = node.element("connections").elements(
					"connection");
			for (Element connElement : connList) {
				nodeProperty.getLineIdList().add(
						connElement.attributeValue("lineId").trim());
			}

			// 设置模型的位置
			abstractModel.setConstraint(rectangle);

			// 设置该模型的数据对象
			if (abstractModel.getDataObject() != null) {
				abstractModel.getDataObject().getObjectFromElement(
						node.element("dataObject"));
			}

			// 将其添加到列表
			abstractModelList.add(abstractModel);
		}

		return abstractModelList;
	}

	/**
	 * 将xml文件解析成抽象模型
	 */
	public static List<AbstractLineGefModel> changeXmlToLines(Element element) {
		if (element == null) {
			logger.error("从Xml文件得到的连接线的Element为空，无法解析！");
			return null;
		}

		// 用来存储获得的连接线List
		List<AbstractLineGefModel> abstractLinelList = new ArrayList<AbstractLineGefModel>();
		List<Element> lineList = element.elements("connection");

		if (lineList == null) {
			logger.info("从Xml文件得到的抽象模型List为空，没有对象！");
			return abstractLinelList;
		}

		for (Element lineElement : lineList) {
			String id = lineElement.attributeValue("id");
			String name = lineElement.attributeValue("name");
			String label = lineElement.elementText("label");
			String type = lineElement.elementText("type");
			String sourceNode = lineElement.elementText("sourceNode").trim();
			String targetNode = lineElement.elementText("targetNode").trim();

			// 设置连接线锚点
			List<Element> anchorList = lineElement.element("anchors").elements(
					"anchor");
			List<Point> bendpoints = new ArrayList<Point>();
			for (Element anchorElement : anchorList) {
				Point point = new Point();
				point.setX(new Integer(anchorElement.attributeValue("x").trim())
						.intValue());
				point.setY(new Integer(anchorElement.attributeValue("y").trim())
						.intValue());

				bendpoints.add(point);
			}

			// 根据连接线类型来生成的连接线对象
			AbstractLineGefModel lineModel = AbstractLineGefModel
					.abstractLineFactory(type);
			if (lineModel == null) {
				logger.error("id:" + id + " name:" + name + " type:" + type
						+ "  无效的连接线type！");
				continue;
			}

			// 设置该模型的数据对象
			if (lineModel.getDataObject() != null) {
				lineModel.getDataObject().getObjectFromElement(
						lineElement.element("dataObject").element(
								LineModel.getElementName()));
			}

			// 获得连接线对象的xml属性
			LineXmlProperty linePro = lineModel.getLineXmlProperty();
			linePro.setId(id);
			// linePro.setLabel(label);
			linePro.setType(type);
			// linePro.setBendPointList(bendpoints);
			linePro.setSourceNodeId(sourceNode);
			linePro.setTargetNodeId(targetNode);

			lineModel.setLabelText(label); // 设置连接线上展现的标签
			lineModel.setBendpoints(bendpoints); // 设置连接线的锚点

			// 将连接线对象放置List
			abstractLinelList.add(lineModel);
		}

		return abstractLinelList;
	}

	/**
	 * 把模型转化成XML
	 * 
	 * @param 父节点的Element
	 * @param 需要转换的AbstractModel列表
	 */
	public static Element changeNodeToXml(Element rootElemnt,
			List<AbstractGefModel> modelList) {
		// -----------------写入模型的XML信息--------------------
		Element nodesElement = rootElemnt.addElement("nodes");

		for (AbstractGefModel model : modelList) {

			NodeXmlProperty nodePro = model.getNodeXmlProperty();
			Element nodeElement = nodesElement.addElement("node");

			nodeElement.addAttribute("id", nodePro.getId()); // 设置id
			nodeElement.addAttribute("type", nodePro.getType());

			Element locationElement = nodeElement.addElement("location"); // 设置location
			locationElement.addAttribute("x", nodePro.getRectangle().x() + "");
			locationElement.addAttribute("y", nodePro.getRectangle().y() + "");
			locationElement.addAttribute("width", nodePro.getRectangle().width
					+ "");
			locationElement.addAttribute("height",
					nodePro.getRectangle().height + "");

			// 添加数据对象
			Element dataObjectElement = nodeElement.addElement("dataObject");
			if (model.getDataObject() != null) {
				model.getDataObject().getElementFromObject(dataObjectElement);
			}

			// 添加连接线
			Element connectionsElement = nodeElement.addElement("connections"); // 设置connections
			for (String lineId : nodePro.getLineIdList()) {
				Element connectionElement = connectionsElement
						.addElement("connection");
				connectionElement.addAttribute("lineId", lineId);
			}
		}

		return nodesElement;
	}

	/**
	 * 把连接线转化成XML
	 * 
	 * @param 父节点的Element
	 * @param 需要转换的AbstractModel列表
	 */
	public static Element changeLineToXml(Element rootElemnt,
			List<AbstractLineGefModel> lineList) {
		Element connectionsElement = rootElemnt.addElement("connections"); // 设置connections
		for (AbstractLineGefModel line : lineList) {
			LineXmlProperty linePro = line.getLineXmlProperty();

			Element connectionElement = connectionsElement
					.addElement("connection");
			connectionElement.addAttribute("id", linePro.getId()); // 设置id

			Element labelElement = connectionElement.addElement("label"); // 设置label
			labelElement.setText(linePro.getLabel() + "");

			Element typeElement = connectionElement.addElement("type"); // 设置type
			typeElement.setText(linePro.getType() + "");

			Element sourceElement = connectionElement.addElement("sourceNode"); // 设置源节点
			sourceElement.setText(linePro.getSourceNodeId() + "");

			Element targetElement = connectionElement.addElement("targetNode"); // 设置目标节点
			targetElement.setText(linePro.getTargetNodeId() + "");

			// 添加数据对象
			Element dataObjectElement = connectionElement
					.addElement("dataObject");
			if (line.getDataObject() != null) {
				line.getDataObject().getElementFromObject(dataObjectElement);
			}

			// 添加锚点
			Element anchorsElement = connectionElement.addElement("anchors"); // 设置anchors
			for (Point point : linePro.getBendPointList()) {
				Element anchorElement = anchorsElement.addElement("anchor"); // 设置anchor
				anchorElement.addAttribute("x", point.x() + "");
				anchorElement.addAttribute("y", point.y() + "");
			}

		}

		return connectionsElement;
	}
}
