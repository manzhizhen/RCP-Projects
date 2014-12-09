/* 文件名：     SwitchObjAndXml.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-7
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
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

import cn.sunline.suncard.powerdesigner.gef.model.AbstractGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractLineGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.DatabaseDiagramGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableShortcutGefModel;
import cn.sunline.suncard.powerdesigner.gef.xml.LineXmlProperty;
import cn.sunline.suncard.powerdesigner.gef.xml.LineXmlPropertyFactory;
import cn.sunline.suncard.powerdesigner.gef.xml.NodeXmlProperty;
import cn.sunline.suncard.powerdesigner.manager.ProductSpaceManager;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.IndexSqlModel;
import cn.sunline.suncard.powerdesigner.model.InitTableDataModel;
import cn.sunline.suncard.powerdesigner.model.LineModel;
import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.PackageModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.ProductSpaceModel;
import cn.sunline.suncard.powerdesigner.model.ProjectModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.TableShortcutModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.model.factory.PhysicalDiagramModelFactory;
import cn.sunline.suncard.powerdesigner.model.factory.TableModelFactory;
import cn.sunline.suncard.powerdesigner.model.factory.TableShortcutModelFactory;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 对象和XML文件互转类
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-9-7
 * @see
 * @since 1.0
 */
public class SwitchObjAndXml {
	public static Log logger = LogManager.getLogger(SwitchObjAndXml.class);

	/**
	 * 从物理图形模型中获得GEF集合模型
	 * 
	 * @param model
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public static DatabaseDiagramGefModel getDatabaseDiagramGefModel(
			PhysicalDiagramModel model) throws CloneNotSupportedException {
		DatabaseDiagramGefModel databaseDiagramGefModel = new DatabaseDiagramGefModel();
		if (model == null) {
			return databaseDiagramGefModel;
		}

		// ------------------------添加节点数据--------------------------
		List<AbstractGefModel> abstractModelList = new ArrayList<AbstractGefModel>();
		List<NodeXmlProperty> nodeList = model.getNodeXmlPropertyList();
		if (nodeList == null) {
			logger.info("从PhysicalDiagramModel中得到的NodeXmlProperty的List为空！");
			return databaseDiagramGefModel;
		}

		for (NodeXmlProperty nodeXmlProperty : nodeList) {
			String id = nodeXmlProperty.getId();
			String type = nodeXmlProperty.getType();

			// 根据类型获得一个新模型
			AbstractGefModel abstractModel = AbstractGefModel
					.abstractModelFactory(type);
			if (abstractModel == null) {
				logger.error("id:" + id + " type:" + type + "  无效的模型type！");
				continue;
			}

			// 设置模型的GEF属性
			NodeXmlProperty nodeProperty = abstractModel.getNodeXmlProperty();
			nodeProperty.setId(id);
			nodeProperty.setType(type);
			List<String> lineIdList = new ArrayList<String>();
			List<String> oldIdList = nodeXmlProperty.getLineIdList();
			for (String lineId : oldIdList) {
				lineIdList.add(lineId);
			}
			nodeProperty.setLineIdList(lineIdList);

			// 设定位置
			abstractModel.setConstraint(nodeXmlProperty.getRectangle()
					.getCopy());

			// 绑定数据对象
			// abstractModel.setDataObject(model.getTableMap().get(id).clone());
			// abstractModel.setDataObject(model.getTableMap().get(id));
			if(abstractModel instanceof TableGefModel) {
				abstractModel.setDataObject(TableModelFactory.getTableModel(
						FileModel.getFileModelFromObj(model), id));
			} else if(abstractModel instanceof TableShortcutGefModel) {
				abstractModel.setDataObject(TableShortcutModelFactory.getTableShortcutModel(
						FileModel.getFileModelFromObj(model), id));
			} else {
				logger.error("将物理数据模型抓换成DatabaseDiagramGefModel时绑定数据对象失败，无效的Gef模型类型:" 
						+ abstractModel.getClass());
			}
			

			// 将其添加到列表
			abstractModelList.add(abstractModel);
		}

		// 添加到集合模型中
		databaseDiagramGefModel.addChildList(abstractModelList);

		// ------------------------添加连接线数据--------------------------
		// 用来存储获得的连接线List
		List<AbstractLineGefModel> abstractLinelList = new ArrayList<AbstractLineGefModel>();
		List<LineXmlProperty> lineList = model.getLineXmlPropertyList();
		if (lineList == null) {
			logger.info("从PhysicalDiagramModel中得到的NodeXmlProperty的List为空！");
			return databaseDiagramGefModel;
		}

		for (LineXmlProperty lineXmlProperty : lineList) {
			String id = lineXmlProperty.getId();
			String label = lineXmlProperty.getLabel();
			String type = lineXmlProperty.getType();
			String sourceNodeId = lineXmlProperty.getSourceNodeId(); // 这个ID其实就是源表名
			String targetNodeId = lineXmlProperty.getTargetNodeId(); // 这个ID其实就是目标表名

			// 赋值锚点信息
			List<Point> pointList = lineXmlProperty.getBendPointList();
			List<Point> newPointList = new ArrayList<Point>();
			for (Point point : pointList) {
				newPointList.add(point.getCopy());
			}

			// 根据类型获得一个新模型
			AbstractLineGefModel lineGefModel = AbstractLineGefModel
					.abstractLineFactory(type);
			if (lineGefModel == null) {
				logger.error("id:" + id + " label:" + label + " type:" + type
						+ "  无效的连接线type！");
				continue;
			}

			// 获得连接线对象的GEF属性
			LineXmlProperty linePro = lineGefModel.getLineXmlProperty();
			linePro.setId(id);
			linePro.setLabel(label);
			linePro.setType(type);
			linePro.setSourceNodeId(sourceNodeId);
			linePro.setTargetNodeId(targetNodeId);
			linePro.setBendPointList(newPointList);

			lineGefModel.setLabelText(label); // 设置连接线上展现的标签
			lineGefModel.setBendpoints(newPointList); // 设置连接线的锚点

			// --------------给连接线绑定LineModel----------------------
			// TableModel sourceTableModel =
			// model.getTableMap().get(sourceNodeId);
			TableModel sourceTableModel = TableModelFactory.getTableModel(
					FileModel.getFileModelFromObj(model), sourceNodeId);
			if (sourceTableModel == null) {
				logger.error("连接线信息错误，给连接线绑定LineModel出错！无法匹配到对应的表："
						+ sourceNodeId);
			} else {
				List<LineModel> lineModelList = sourceTableModel
						.getLineModelList();
				boolean flag = false; // 是否绑定成功的标记
				for (LineModel lineModel : lineModelList) {
					if (targetNodeId.equals(lineModel.getParentTableModelId())) {
						lineGefModel.setDataObject(lineModel);
						flag = true;
						break;
					}
				}

				if (flag == false) {
					logger.error("给连接线匹配LineModel对象失败，子表：" + sourceNodeId
							+ "，父表：" + targetNodeId);
				}
			}

			// 将连接线对象放置List
			abstractLinelList.add(lineGefModel);
		}

		// 给模型绑定连接线
		bindModelAndLine(databaseDiagramGefModel.getChildren(),
				abstractLinelList);

		return databaseDiagramGefModel;
	}

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
	 * 从项目模型生成Document对象
	 * 
	 */
	public static Document getDocFromProjectModel(ProjectModel projectModel) {
		Document document = DocumentHelper.createDocument(); // 创建document对象

		// 创建根节点
		Element rootElement = document.addElement(DmConstants.PPD_XML_ROOT_ELEMENT_NAME);

		// 添加数据信息
		projectModel.getElementFromObject(rootElement);
		
		//----------------------------添加表格集合节点信息-----------------------
		Element tableModelsElement = rootElement.addElement(DmConstants.SPD_XML_TABLES_ELEMENT_NAME);
		Set<TableModel> tableModelSet = projectModel.getAllTableModel();
		for(TableModel tableModel : tableModelSet) {
			tableModel.getElementFromObject(tableModelsElement);
		}
		
		//----------------------------添加列对象集合节点信息-----------------------
		Element columnModelsElement = rootElement.addElement(DmConstants.SPD_XML_COLUMNS_ELEMENT_NAME);
		Set<ColumnModel> columnModelSet = new HashSet<ColumnModel>();
		for(TableModel tableModel : tableModelSet) {
			columnModelSet.addAll(tableModel.getColumnList());
		}
		for(ColumnModel columnModel : columnModelSet) {
			columnModel.getElementFromObject(columnModelsElement);
		}
		
		// ------------------------生成IndexSqlModel节点信息-----------------
		Element indexSqlModelsElement = rootElement
				.addElement(DmConstants.SPD_XML_INDEX_SQL_ELEMENT_NAME);
		for (TableModel tableModel : tableModelSet) {
			LinkedHashSet<IndexSqlModel> indexSqlModelSet = tableModel.getIndexSqlModelSet();
			if (indexSqlModelSet == null
					|| indexSqlModelSet
							.isEmpty()) {
				continue;
			}

			for(IndexSqlModel indexSqlModel : indexSqlModelSet) {
							indexSqlModel.getElementFromObject(
					indexSqlModelsElement);
			}
		}
		
		return document;
		
	}

	/**
	 * 从文件模型生成Document对象
	 * 
	 * @param DatabaseDiagramGefModel
	 *            集合模型
	 */
	public static Document getDocFromFileModel(FileModel fileModel) {
		Document document = DocumentHelper.createDocument(); // 创建document对象

		// 创建根节点
		Element rootElement = document.addElement(DmConstants.SPD_XML_ROOT_ELEMENT_NAME);

		// 添加数据信息
		fileModel.getElementFromObject(rootElement);
		
		//----------------------------添加物理图形模型集合节点信息-----------------------
		Element physicalDiagramModelsElement = rootElement.addElement(DmConstants.SPD_XML_PHYSICALDIAGRAMS_ELEMENT_NAME);
		Set<PhysicalDataModel> physicalDataModelSet = fileModel.getPhysicalDataSet();
		for(PhysicalDataModel physicalDataModel : physicalDataModelSet) {
			Set<PhysicalDiagramModel> physicalDiagramModelSet = physicalDataModel.getAllPhysicalDiagramModels();
			for(PhysicalDiagramModel physicalDiagramModel : physicalDiagramModelSet) {
				physicalDiagramModel.getElementFromObject(physicalDiagramModelsElement);
			}
		}

		//----------------------------添加表格集合节点信息-----------------------
		Element tableModelsElement = rootElement.addElement(DmConstants.SPD_XML_TABLES_ELEMENT_NAME);
		Set<TableModel> tableModelSet = fileModel.getAllTableModel();
		for(TableModel tableModel : tableModelSet) {
			tableModel.getElementFromObject(tableModelsElement);
		}
		
		//----------------------------添加表格快捷方式集合节点信息-----------------------
		Element tableShortcutModelsElement = rootElement.addElement(DmConstants.SPD_XML_SHORTCUTS_ELEMENT_NAME);
		Set<TableShortcutModel> tableShortcutModelSet = fileModel.getAllTableShortcutModel();
		for(TableShortcutModel tableShortcutModel : tableShortcutModelSet) {
			tableShortcutModel.getElementFromObject(tableShortcutModelsElement);
		}
		
		//----------------------------添加列对象集合节点信息-----------------------
		Element columnModelsElement = rootElement.addElement(DmConstants.SPD_XML_COLUMNS_ELEMENT_NAME);
		Set<ColumnModel> columnModelSet = fileModel.getAllColumnModel();
		for(ColumnModel columnModel : columnModelSet) {
			columnModel.getElementFromObject(columnModelsElement);
		}

		// ------------------------------添加GEF信息-----------------------------
		Element gefInfoElement = rootElement.addElement(DmConstants.SPD_XML_GEFINFO_ROOT_ELEMENT_NAME);
		List<NodeXmlProperty> nodeXmlPropertyList = new ArrayList<NodeXmlProperty>();
		List<LineXmlProperty> lineXmlPropertyList = new ArrayList<LineXmlProperty>();
		Set<PhysicalDataModel> dataModelSet = fileModel.getPhysicalDataSet();
		for (PhysicalDataModel physicalDataModel : dataModelSet) {
			Set<PhysicalDiagramModel> physicalDiagramModels = physicalDataModel
					.getAllPhysicalDiagramModels();
			for (PhysicalDiagramModel physicalDiagramModel : physicalDiagramModels) {
				nodeXmlPropertyList.addAll(physicalDiagramModel
						.getNodeXmlPropertyList());
				lineXmlPropertyList.addAll(physicalDiagramModel
						.getLineXmlPropertyList());
			}
		}

		// 添加节点信息
		Element nodesElement = gefInfoElement.addElement(DmConstants.SPD_XML_GEFINFO_NODES_ELEMENT_NAME);
		for (NodeXmlProperty nodeXmlProperty : nodeXmlPropertyList) {
			nodeXmlProperty.getElementFromObject(nodesElement);
		}

		// 添加连接线信息
		Element linesElement = gefInfoElement.addElement(DmConstants.SPD_XML_GEFINFO_LINES_ELEMENT_NAME);
		for (LineXmlProperty lineXmlProperty : lineXmlPropertyList) {
			lineXmlProperty.getElementFromObject(linesElement);
		}

		Set<TableModel> tableSet = fileModel.getAllTableModel();
//		// ------------------------生成InitTableDatas节点信息-----------------
//		Element initTableDatasElement = rootElement
//				.addElement(DmConstants.SPD_XML_GEFINFO_INIT_TABLE_DATA_ELEMENT_NAME);
//		for (TableModel tableModel : tableSet) {
//			if (tableModel.getInitTableDataModel() == null
//					|| tableModel.getInitTableDataModel().getInitDataList()
//							.isEmpty()) {
//				continue;
//			}
//
//			tableModel.getInitTableDataModel().getElementFromObject(
//					initTableDatasElement);
//		}
		
		// ------------------------生成IndexSqlModel节点信息-----------------
		Element indexSqlModelsElement = rootElement
				.addElement(DmConstants.SPD_XML_INDEX_SQL_ELEMENT_NAME);
		for (TableModel tableModel : tableSet) {
			LinkedHashSet<IndexSqlModel> indexSqlModelSet = tableModel.getIndexSqlModelSet();
			if (indexSqlModelSet == null
					|| indexSqlModelSet
							.isEmpty()) {
				continue;
			}

			for(IndexSqlModel indexSqlModel : indexSqlModelSet) {
							indexSqlModel.getElementFromObject(
					indexSqlModelsElement);
			}
		}

		// ------------------------生成productModels节点信息-----------------
		Element productModelsElement = rootElement.addElement(DmConstants.SPD_XML_PRODUCTS_ELEMENT_NAME);
		List<ProductModel> productModelList = ProductSpaceManager
				.getProductModelList(fileModel);
		if (productModelList != null) {
			for (ProductModel productModel : productModelList) {
				// 如果是该文件的产品，则添加
				if (fileModel.equals(FileModel.getFileModelFromObj(productModel
						.getPhysicalDataModel()))) {
					productModel.getElementFromObject(productModelsElement);
				}
			}
		} else {
			logger.error("将产品模型List组装成节点失败！");
		}

		return document;
	}
	
	/**
	 * 从文件模型生成初始化表格数据的Document对象
	 * 
	 * @param DatabaseDiagramGefModel
	 *            集合模型
	 */
	public static Document getInitTableDataDocFromFileModel(FileModel fileModel) {
		if(fileModel == null) {
			logger.debug("传入的FileModel为空，无法文件模型生成初始化表格数据的Document对象！");
			return null;
		}
		
		Document document = DocumentHelper.createDocument(); // 创建document对象

		// 创建根节点
		Element rootElement = document.addElement(DmConstants.SPDD_XML_ROOT_ELEMENT_NAME);
		
		Set<TableModel> allTableModels = fileModel.getAllTableModel();
		for(TableModel tableModel : allTableModels) {
			if (tableModel.getInitTableDataModel() == null
					|| tableModel.getInitTableDataModel().getInitDataList()
							.isEmpty()) {
				continue;
			}

			tableModel.getInitTableDataModel().getElementFromObject(
					rootElement);
		}
		
		return document;
	}
	
	/**
	 * 从项目模型生成初始化表格数据的Document对象
	 * 
	 * @param DatabaseDiagramGefModel
	 *            集合模型
	 */
	public static Document getInitTableDataDocFromProjectModel(ProjectModel projectModel) {
		if(projectModel == null) {
			logger.debug("传入的ProjectModel为空，无法文件模型生成初始化表格数据的Document对象！");
			return null;
		}
		
		Document document = DocumentHelper.createDocument(); // 创建document对象

		// 创建根节点
		Element rootElement = document.addElement(DmConstants.SPDD_XML_ROOT_ELEMENT_NAME);
		
		Set<TableModel> allTableModels = projectModel.getAllTableModel();
		for(TableModel tableModel : allTableModels) {
			if (tableModel.getInitTableDataModel() == null
					|| tableModel.getInitTableDataModel().getInitDataList()
							.isEmpty()) {
				continue;
			}

			tableModel.getInitTableDataModel().getElementFromObject(
					rootElement);
		}
		
		return document;
	}
	

	/**
	 * 从文档获得FileModel对象
	 * @param 数据库设计spd文件
	 * @param 表格初始化spdd文件
	 * @throws DocumentException
	 */
	public static FileModel getFileModelFromFile(File spdFile, File spddFile, File zipFile)
			throws DocumentException {
		if (spdFile == null || !spdFile.isFile() || spddFile == null || !spddFile.isFile() || zipFile == null || !zipFile.isFile()) {
			logger.error("传入的File为空或无效，无法创建FileModel对象！");
			return null;
		}

		FileModel fileModel = new FileModel();
		fileModel.setFile(zipFile);
		
		SAXReader spdReader = new SAXReader();
		SAXReader spddReader = new SAXReader();
		Document spdDocument = spdReader.read(spdFile);
		Document spddDocument = spddReader.read(spddFile);

		Element root = spdDocument.getRootElement();
		
		// --------------------组装ColumnModelFactory-------------------------
		Element columnModelsE = root.element(DmConstants.SPD_XML_COLUMNS_ELEMENT_NAME);
		List<Element> columnModelElementList = columnModelsE.elements(ColumnModel.getElementName());
		if(columnModelElementList != null && !columnModelElementList.isEmpty()) {
			for(Element columnModelE : columnModelElementList) {
				ColumnModel columnModel = new ColumnModel().getObjectFromElement(columnModelE, fileModel);
				ColumnModelFactory.addColumnModel(fileModel, columnModel.getId(), columnModel);
			}
		}
		
		// --------------------组装TableModelFactory-------------------------
		Element tableModelsE = root.element(DmConstants.SPD_XML_TABLES_ELEMENT_NAME);
		List<Element> tableModelElementList = tableModelsE.elements(TableModel.getElementName());
		if(tableModelElementList != null && !tableModelElementList.isEmpty()) {
			for(Element tableModelE : tableModelElementList) {
				TableModel tableModel = new TableModel().getObjectFromElement(tableModelE, fileModel);
				TableModelFactory.addTableModel(fileModel, tableModel.getId(), tableModel);
			}
		}
		
		// --------------------组装TableShortcutModelFactory-------------------------
		Element tableShortcutModelsE = root.element(DmConstants.SPD_XML_SHORTCUTS_ELEMENT_NAME);
		List<Element> tableShortcutModelElementList = tableShortcutModelsE.elements(TableShortcutModel.getElementName());
		if(tableShortcutModelElementList != null && !tableShortcutModelElementList.isEmpty()) {
			for(Element tableShortcutModelE : tableShortcutModelElementList) {
				TableShortcutModel tableShortcutModel = new TableShortcutModel().getObjectFromElement(tableShortcutModelE, fileModel);
				TableShortcutModelFactory.addTableShortcutModel(fileModel, tableShortcutModel.getId(), tableShortcutModel);
			}
		}
		
		// --------------------组装PhysicalDiagramModelFactory-------------------------
		Element physicalDiagramModelsE = root.element(DmConstants.SPD_XML_PHYSICALDIAGRAMS_ELEMENT_NAME);
		List<Element> physicalDiagramElementList = physicalDiagramModelsE.elements(PhysicalDiagramModel.getElementName());
		if(physicalDiagramElementList != null && !physicalDiagramElementList.isEmpty()) {
			for(Element physicalDiagramModelE : physicalDiagramElementList) {
				PhysicalDiagramModel physicalDiagramModel = new PhysicalDiagramModel().getObjectFromElement(physicalDiagramModelE, fileModel);
				PhysicalDiagramModelFactory.addPhysicalDiagramModel(fileModel, physicalDiagramModel.getId(), physicalDiagramModel);
			}
		}
		
		// -------------------初始化FileModel中的信息-----------------
		fileModel
				.getObjectFromElement(root.element(FileModel.getElementName()));

		initFileModelInfo(fileModel);
		
		// 给物理图形模型下面的TableShortcutModel组装表格对象
		Collection<TableShortcutModel> tableShortcutModelSet = fileModel.getAllTableShortcutModel();
		for(TableShortcutModel tableShortcutModel : tableShortcutModelSet) {
			// 当时在解析XML时，把TableShortcutModel的目标表格模型的ID已经存在了该快捷方式的targetTableModel中了
			TableModel targetTableModel = TableModelFactory.getTableModel(fileModel, tableShortcutModel
					.getTargetTableModel().getId());
			if(targetTableModel != null) {
				tableShortcutModel.setTargetTableModel(targetTableModel);
			} else {
				logger.error("给TableShortcutModel模型:" + tableShortcutModel.getId() 
						+ "组装targetTableModel失败，无效的TableModel的ID:" 
						+ tableShortcutModel.getTargetTableModel().getId());
			}
		}

		// ----------------------------开始处理GEF信息------------------------

		// 初始化连接线信息
		Map<String, LineXmlProperty> lineMap = new HashMap<String, LineXmlProperty>();
		List<Element> lineElementList = root.element(DmConstants.SPD_XML_GEFINFO_ROOT_ELEMENT_NAME)
				.element(DmConstants.SPD_XML_GEFINFO_LINES_ELEMENT_NAME).elements();
		if (lineElementList != null) {
			for (Element lineE : lineElementList) {
				LineXmlProperty lineXmlProperty = new LineXmlProperty()
						.getObjectFromElement(lineE);
				lineMap.put(lineXmlProperty.getId(), lineXmlProperty);

			}
		}

		// 初始化节点信息
		Map<String, NodeXmlProperty> tableNodeMap = new HashMap<String, NodeXmlProperty>(); // 表到对应节点的Map
		Map<String, NodeXmlProperty> tableShortcutNodeMap = new HashMap<String, NodeXmlProperty>(); // 表快捷方式到对应节点的Map
		Map<NodeXmlProperty, List<LineXmlProperty>> nodeLineMap = new HashMap<NodeXmlProperty
				, List<LineXmlProperty>>(); // 对应节点到连接线的Map
		List<Element> nodeElementList = root.element(DmConstants.SPD_XML_GEFINFO_ROOT_ELEMENT_NAME)
				.element(DmConstants.SPD_XML_GEFINFO_NODES_ELEMENT_NAME).elements();
		if (nodeElementList != null) {
			for (Element nodeElement : nodeElementList) {
				NodeXmlProperty nodeXmlProperty = new NodeXmlProperty()
						.getObjectFromElement(nodeElement);

				String targetId = nodeXmlProperty.getId();

				// 用来储存该节点所有有关的连接线
				List<LineXmlProperty> lineXmlList = new ArrayList<LineXmlProperty>();
				List<String> lineIdList = nodeXmlProperty.getLineIdList();
				for (String lineId : lineIdList) {
					LineXmlProperty lineXmlProperty = lineMap.get(lineId);
					if (lineXmlProperty != null) {
						lineXmlList.add(lineXmlProperty);
					}
				}

				nodeLineMap.put(nodeXmlProperty, lineXmlList); // 把节点和连接线对应起来
				if(TableGefModel.class.getName().equals(nodeXmlProperty.getType())) {
					tableNodeMap.put(targetId, nodeXmlProperty); // 把表和节点对应起来
				} else if(TableShortcutGefModel.class.getName().equals(nodeXmlProperty.getType())) {
					tableShortcutNodeMap.put(targetId, nodeXmlProperty); // 把表快捷方式和节点对应起来
				}
			}

			// 通过TableModel来给PhysicalDiagramModel的gef信息赋值
			Set<TableModel> tableModelSet = fileModel.getAllTableModel();
			for (TableModel tableModel : tableModelSet) {
				NodeXmlProperty nodeXmlProperty = tableNodeMap.get(tableModel
						.getId());
				if (nodeXmlProperty != null
						&& tableModel.getPhysicalDiagramModel() != null) {
					tableModel.getPhysicalDiagramModel()
							.getNodeXmlPropertyList().add(nodeXmlProperty);
					if (nodeLineMap.get(nodeXmlProperty) != null)
						tableModel.getPhysicalDiagramModel()
								.getLineXmlPropertyList()
								.addAll(nodeLineMap.get(nodeXmlProperty));
				}
			}
			
			// 通过TableShortcutModel来给PhysicalDiagramModel的gef信息赋值
			for (TableShortcutModel tableShortcutModel : tableShortcutModelSet) {
				NodeXmlProperty nodeXmlProperty = tableShortcutNodeMap.get(tableShortcutModel
						.getId());
				if (nodeXmlProperty != null
						&& tableShortcutModel.getPhysicalDiagramModel() != null) {
					tableShortcutModel.getPhysicalDiagramModel()
							.getNodeXmlPropertyList().add(nodeXmlProperty);
					if (nodeLineMap.get(nodeXmlProperty) != null)
						tableShortcutModel.getPhysicalDiagramModel()
								.getLineXmlPropertyList()
								.addAll(nodeLineMap.get(nodeXmlProperty));
				}
			}
		}

		// ---------------------给TableModel组装InitTableDataModel---------
		Element rootElement = spddDocument.getRootElement();
		List<Element> initTableDataElements = rootElement.elements(InitTableDataModel.getElementName());
		if(initTableDataElements != null) {
			for(Element element : initTableDataElements) {
				InitTableDataModel initTableDataModel = new InitTableDataModel();
				initTableDataModel.getObjectFromElement(element, fileModel);

				TableModel tableModel = TableModelFactory.getTableModel(
						fileModel,
						element.attributeValue("refTableId")
								.trim());
				if (tableModel == null) {
					logger.error("组装InitTableDataModel出错，无效的表格ID:"
							+ element.attributeValue("refTableId")
									.trim());
					continue;
				}

				tableModel.setInitTableDataModel(initTableDataModel);
			}
			
		}
		
//		Element initTableDatasElement = root.element(DmConstants.SPD_XML_GEFINFO_INIT_TABLE_DATA_ELEMENT_NAME);
//		List<Element> initTableDataElementList = initTableDatasElement
//				.elements(InitTableDataModel.getElementName());
//
//		if (initTableDataElementList != null
//				&& !initTableDataElementList.isEmpty()) {
//			for (Element initTableDataElement : initTableDataElementList) {
//				InitTableDataModel initTableDataModel = new InitTableDataModel();
//				initTableDataModel.getObjectFromElement(initTableDataElement);
//
//				TableModel tableModel = TableModelFactory.getTableModel(
//						fileModel,
//						initTableDataElement.attributeValue("refTableId")
//								.trim());
//				if (tableModel == null) {
//					logger.error("组装InitTableDataModel出错，无效的表格ID:"
//							+ initTableDataElement.attributeValue("refTableId")
//									.trim());
//					continue;
//				}
//
//				tableModel.setInitTableDataModel(initTableDataModel);
//			}
//
//		}
		
		// ---------------------给TableModel组装IndexSqlModel---------
		Element indexSqlModelsElement = root.element(DmConstants.SPD_XML_INDEX_SQL_ELEMENT_NAME);
		List<Element> indexSqlModelElementList = indexSqlModelsElement
				.elements(IndexSqlModel.getElementName());

		if (indexSqlModelElementList != null
				&& !indexSqlModelElementList.isEmpty()) {
			for (Element indexSqlModelElement : indexSqlModelElementList) {
				IndexSqlModel indexSqlModel = new IndexSqlModel();
				indexSqlModel.getObjectFromElement(indexSqlModelElement);

				TableModel tableModel = TableModelFactory.getTableModel(
						fileModel,
						indexSqlModelElement.attributeValue("refTableId")
								.trim());
				if (tableModel == null) {
					logger.error("组装IndexSqlModel出错，无效的表格ID:"
							+ indexSqlModelElement.attributeValue("refTableId")
									.trim());
					continue;
				}

				tableModel.getIndexSqlModelSet().add(indexSqlModel);
			}

		}

		// --------------------组装产品模型--------------------------------
		Map<String, PhysicalDataModel> physicalDataModelMap = new HashMap<String, PhysicalDataModel>();
		Set<PhysicalDataModel> physicalDataModels = fileModel.getPhysicalDataSet();
		for (PhysicalDataModel physicalDataModel : physicalDataModels) {
			physicalDataModelMap.put(physicalDataModel.getId(),
					physicalDataModel);
		}

		Element productModelsElement = root.element(DmConstants.SPD_XML_PRODUCTS_ELEMENT_NAME);
		List<Element> productModelElementList = productModelsElement
				.elements(ProductModel.getElementName());
		if (productModelElementList != null
				&& !productModelElementList.isEmpty()) {
			for (Element productElement : productModelElementList) {
				ProductModel productModel = new ProductModel();
				productModel.getObjectFromElement(productElement, fileModel);

				PhysicalDataModel findPhysicalDataModel = physicalDataModelMap
						.get(productElement.attributeValue(
								"physicalDataModelId").trim());
				if (findPhysicalDataModel == null) {
					logger.error("组装产品模型时出错，产品:" + productModel.getName()
							+ " 找不到对应的PhysicalDataModel ："
							+ productElement.attributeValue("id").trim());
					continue;
				}

				productModel.setPhysicalDataModel(findPhysicalDataModel);

				// 添加到产品空间中
				ProductSpaceManager.addProductModel(fileModel, productModel);
				
//				// 给产品下面的模块模型绑定表格对象
//				Set<ModuleModel> moduleModelSet = productModel.getModuleModelSet();
//				for(ModuleModel moduleModel : moduleModelSet) {
//					// 新建一个表格模型的Set，因为此时该模块模型中的表格Set只储存了ID信息
//					LinkedHashSet<TableModel> newTableModelSet = new LinkedHashSet<TableModel>();
//					Set<TableModel> tableModelSet = moduleModel.getTableModelSet();
//					for(TableModel tableModel : tableModelSet) {
//						TableModel findTableModel = TableModelFactory.getTableModel(fileModel
//								, tableModel.getId());
//						if(findTableModel == null) {
//							logger.error("找不到该ID对应的表格对象，给模块模型添加表格失败！" + tableModel.getId());
//							continue ;
//						}
//						
//						newTableModelSet.add(findTableModel);
//					}
//					
//					moduleModel.setTableModelSet(newTableModelSet);
//				}
				
			}
		}

		return fileModel;
	}
	
	/**
	 * 从文档获得ProjectModel对象
	 * 
	 * @throws DocumentException
	 */
	public static ProjectModel getProjectModelFromFile(File spdFile, File spddFile)
			throws DocumentException {
		if (spdFile == null || !spdFile.isFile() || spddFile == null || !spddFile.isFile()) {
			logger.error("传入的File为空或无效，无法创建ProjectModel对象！");
			return null;
		}

		ProjectModel projectModel = new ProjectModel();
		SAXReader spdReader = new SAXReader();
		SAXReader spddReader = new SAXReader();
		Document spdDocument = spdReader.read(spdFile);
		Document spddDocument = spddReader.read(spddFile);

		Element root = spdDocument.getRootElement();
		
		// --------------------先找到所有用到的ColumnModel-------------------------
		Map<String, ColumnModel> columnModelMap = new HashMap<String, ColumnModel>();
		Element columnModelsE = root.element(DmConstants.SPD_XML_COLUMNS_ELEMENT_NAME);
		List<Element> columnModelElementList = columnModelsE.elements(ColumnModel.getElementName());
		if(columnModelElementList != null && !columnModelElementList.isEmpty()) {
			for(Element columnModelE : columnModelElementList) {
				ColumnModel columnModel = new ColumnModel().getObjectFromElement(columnModelE);
				columnModelMap.put(columnModel.getId(), columnModel);
			}
		}
		
		// --------------------找到所有用到的TableModel-------------------------
		Map<String, TableModel> tableModelMap = new HashMap<String, TableModel>();
		Element tableModelsE = root.element(DmConstants.SPD_XML_TABLES_ELEMENT_NAME);
		List<Element> tableModelElementList = tableModelsE.elements(TableModel.getElementName());
		if(tableModelElementList != null && !tableModelElementList.isEmpty()) {
			for(Element tableModelE : tableModelElementList) {
				TableModel tableModel = new TableModel().getObjectFromElement(tableModelE, columnModelMap);
				tableModelMap.put(tableModel.getId(), tableModel);
			}
		}
		
		Element projectModelsE = root.element(ProjectModel.getElementName());
		if(projectModelsE != null) {
			projectModel.getObjectFromElement(projectModelsE, tableModelMap);
		}
		
		// ---------------------给TableModel组装InitTableDataModel---------
		Element initTableDatasElement = spddDocument.getRootElement();
		List<Element> initTableDataElementList = initTableDatasElement
				.elements(InitTableDataModel.getElementName());

		if (initTableDataElementList != null
				&& !initTableDataElementList.isEmpty()) {
			for (Element initTableDataElement : initTableDataElementList) {
				InitTableDataModel initTableDataModel = new InitTableDataModel();
				initTableDataModel.getObjectFromElement(initTableDataElement);

				TableModel tableModel = tableModelMap.get(initTableDataElement.attributeValue("refTableId")
								.trim());
				if (tableModel == null) {
					logger.error("组装InitTableDataModel出错，无效的表格ID:"
							+ initTableDataElement.attributeValue("refTableId")
									.trim());
					continue;
				}

				tableModel.setInitTableDataModel(initTableDataModel);
			}

		}
		
		// ---------------------给TableModel组装IndexSqlModel---------
		Element indexSqlModelsElement = root.element(DmConstants.SPD_XML_INDEX_SQL_ELEMENT_NAME);
		List<Element> indexSqlModelElementList = indexSqlModelsElement
				.elements(IndexSqlModel.getElementName());

		if (indexSqlModelElementList != null
				&& !indexSqlModelElementList.isEmpty()) {
			for (Element indexSqlModelElement : indexSqlModelElementList) {
				IndexSqlModel indexSqlModel = new IndexSqlModel();
				indexSqlModel.getObjectFromElement(indexSqlModelElement);

				TableModel tableModel = tableModelMap.get(indexSqlModelElement.attributeValue("refTableId")
						.trim());
				if (tableModel == null) {
					logger.error("组装IndexSqlModel出错，无效的表格ID:"
							+ indexSqlModelElement.attributeValue("refTableId")
									.trim());
					continue;
				}

				tableModel.getIndexSqlModelSet().add(indexSqlModel);
			}

		}

		return projectModel;
	}
	
	/**
	 * 初始化文件模型的成员到各个工厂中
	 * @param fileModel
	 */
	public static void initFileModelInfo(FileModel fileModel) {
//		// 组装TableModelFactory
//		Set<TableModel> allTableModelSet = fileModel.getAllTableModel();
//		for (TableModel tableModel : allTableModelSet) {
//			TableModelFactory.addTableModel(fileModel, tableModel.getId(),
//					tableModel);
//		}
//		
//		// 组装TableShortcutModelFactory
//		Set<TableShortcutModel> allTableShortcutModelSet = fileModel.getAllTableShortcutModel();
//		for (TableShortcutModel tableShortcutModel : allTableShortcutModelSet) {
//			TableShortcutModelFactory.addTableShortcutModel(fileModel, tableShortcutModel.getId(),
//					tableShortcutModel);
//		}
//
//		// 组装ColumnModelFactory
//		Set<ColumnModel> allColumnModelSet = fileModel.getAllColumnModel();
//		for (ColumnModel columnModel : allColumnModelSet) {
//			ColumnModelFactory.addColumnModel(fileModel, columnModel.getId(),
//					columnModel);
//		}
//		
//		// 组装PhysicalDiagramModelFactory
//		Set<PhysicalDiagramModel> allPhysicalDiagramModelSet = fileModel.getAllPhysicalDiagramModel();
//		for(PhysicalDiagramModel physicalDiagramModel : allPhysicalDiagramModelSet) {
//			PhysicalDiagramModelFactory.addPhysicalDiagramModel(fileModel, physicalDiagramModel.getId(), physicalDiagramModel);
//		}

		// 给物理数据模型的默认列对象列表赋值，使其可以向上遍历，并组装LineXmlPropertiesFactory
		Set<PhysicalDataModel> dataModelSet = fileModel.getPhysicalDataSet();
		for (PhysicalDataModel physicalDataModel : dataModelSet) {
			// 给物理数据模型的默认列对象列表和Domains的列对象赋值，使其可以向上遍历
			PackageModel packageModel = new PackageModel();
			packageModel.setPhysicalDataModel(physicalDataModel);
			PhysicalDiagramModel newPhysicalDiagramModel = new PhysicalDiagramModel();
			newPhysicalDiagramModel.setPackageModel(packageModel);
			TableModel newTableModel = new TableModel();
			newTableModel.setPhysicalDiagramModel(newPhysicalDiagramModel);

			List<ColumnModel> columnModelList = physicalDataModel
					.getDefaultColumnList();
			for (ColumnModel columnModel : columnModelList) {
				columnModel.setTableModel(newTableModel);
			}
			
			columnModelList = physicalDataModel.getDomainList();
			for (ColumnModel columnModel : columnModelList) {
				columnModel.setTableModel(newTableModel);
			}

			// 组装LineXmlPropertiesFactory
			Set<PhysicalDiagramModel> physicalDiagramModelSet = physicalDataModel.getAllPhysicalDiagramModels();
			for (PhysicalDiagramModel physicalDiagramModel : physicalDiagramModelSet) {
				List<LineXmlProperty> lineList = physicalDiagramModel
						.getLineXmlPropertyList();
				for (LineXmlProperty lineXmlProperty : lineList) {
					LineXmlPropertyFactory.addLineId(fileModel,
							lineXmlProperty.getId());
				}
			}
		}
		
//		// 添加到产品空间中
//		ProductSpaceModel.addFileModel(fileModel);
	}

}
