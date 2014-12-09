/* 文件名：     PdmReader.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     wzx
 * 修改时间：2012-9-25
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.xml;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.db.DatabaseManager;
import cn.sunline.suncard.powerdesigner.gef.model.LineGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableShortcutGefModel;
import cn.sunline.suncard.powerdesigner.gef.xml.LineXmlProperty;
import cn.sunline.suncard.powerdesigner.gef.xml.NodeXmlProperty;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.LineModel;
import cn.sunline.suncard.powerdesigner.model.PackageModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.TableShortcutModel;
import cn.sunline.suncard.powerdesigner.model.db.DataTypeModel;
import cn.sunline.suncard.powerdesigner.model.db.DatabaseTypeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 
 * @author  wzx
 * @version 1.0, 2012-9-25
 * @see 
 * @since 1.0
 */
public class PdmReader {
	public static final int CANVAS_RATE = 10;			// 画布缩小比率
	public static final String FLAG_MANDATORY = "1";	// pdm文件中强制不为空标识
	public static final String LEFT_BRACKET = "(";
	public static final String RIGHT_BRACKET = ")";
	public static final String COMMA = ",";
	public static final String STRING_CHANGE = "\\";	// 转义字符
	
	public static final String XMLNS_OBJECT = "o";
	public static final String XMLNS_COLLECTION = "c";
	public static final String XMLNS_ATTRIBUTE = "a";
	
	public static final String ELEMENT_MODEL = "Model";
	public static final String ELEMENT_DBMS = "DBMS";
	public static final String ELEMENT_SHORTCUT = "Shortcut";
	public static final String ELEMENT_PHYSICALDIAGRAMS = "PhysicalDiagrams";
	public static final String ELEMENT_PHYSICALDIAGRAM = "PhysicalDiagram";
	public static final String ELEMENT_PACKAGES = "Packages";
	public static final String ELEMENT_PACKAGE = "Package";
	public static final String ELEMENT_SYMBOLS = "Symbols";
	public static final String ELEMENT_TABLESYMBOL = "TableSymbol";
	public static final String ELEMENT_REFERENCESYMBOL = "ReferenceSymbol";
	public static final String ELEMENT_SOURCESYMBOL = "SourceSymbol";
	public static final String ELEMENT_DESTINATIONSYMBOL = "DestinationSymbol";
	public static final String ELEMENT_LISTOFPOINTS = "ListOfPoints";
	public static final String ELEMENT_RECT = "Rect";
	public static final String ELEMENT_DOMAINS = "Domains";
	public static final String ELEMENT_PHYSICALDOMAIN = "PhysicalDomain";
	public static final String ELEMENT_TABLES = "Tables";
	public static final String ELEMENT_TABLE = "Table";
	public static final String ELEMENT_COLUMNS = "Columns";
	public static final String ELEMENT_COLUMN = "Column";
	public static final String ELEMENT_CODE = "Code";
	public static final String ELEMENT_NAME = "Name";
	public static final String ELEMENT_OBJECTID = "ObjectID";
	public static final String ELEMENT_TARGETID = "TargetID";
	public static final String ELEMENT_DATATYPE = "DataType";
	public static final String ELEMENT_LENGTH = "Length";
	public static final String ELEMENT_PRECISION = "Precision";
	public static final String ELEMENT_KEYS = "Keys";
	public static final String ELEMENT_KEY = "Key";
	public static final String ELEMENT_KEY_COLUMNS = "Key.Columns";
	public static final String ELEMENT_PRIMARYKEY = "PrimaryKey";
	public static final String ELEMENT_MANDATORY = "Mandatory";
	public static final String ELEMENT_LOWVALUE = "LowValue";
	public static final String ELEMENT_HIGHVALUE = "HighValue";
	public static final String ELEMENT_DEFAULTVALUE = "DefaultValue";
	public static final String ELEMENT_REFERENCES = "References";
	public static final String ELEMENT_REFERENCE = "Reference";
	public static final String ELEMENT_COMMENT = "Comment";
	public static final String ELEMENT_CARDINALITY = "Cardinality";
	public static final String ELEMENT_PARENTTABLE = "ParentTable";
	public static final String ELEMENT_CHILDTABLE = "ChildTable";
	public static final String ELEMENT_FOREIGNKEYCONSTRAINTNAME = "ForeignKeyConstraintName";
	public static final String ELEMENT_JOINS = "Joins";
	public static final String ELEMENT_REFERENCEJOIN = "ReferenceJoin";
	public static final String ELEMENT_OBJECT = "Object";
	public static final String ELEMENT_OBJECT1 = "Object1";
	public static final String ELEMENT_OBJECT2 = "Object2";
	
	public static final String ATTRIBUTE_ID = "Id";
	public static final String ATTRIBUTE_REF = "Ref";
	
	public static final String DEFAULT_PACKAGE_ID = "Default_Package_id";
	public static final String DEFAULT_PACKAGE_NAME = "DefaultPackage";
	
	private Document document;
	private SAXReader saxReader = new SAXReader();
	
	private HashMap xmlMap;	// pdm文件命名空间
	
	private List<String> referencePrimaryKeyIds;
	private List<String> referenceForeignKeyIds;
	private Map<LineModel, String> lineModels;
	private Map<String, TableModel> tableModels;
	private List<TableShortcutModel> tableShortcutModels;
	private DatabaseTypeModel databaseTypeModel;
	private List<DataTypeModel> dataTypeModelList;
	private List<ColumnModel> domainModels;
	private List<PhysicalDiagramModel> physicalDiagramModels;
	private List<TableModel> tableModelsWithNoPhysicalDiagram;
	
	private Log logger = LogManager.getLogger(PdmReader.class.getName());
	
	/**
	 * 构造函数
	 */
	public PdmReader() {
		xmlMap = new HashMap();
		xmlMap.put(XMLNS_OBJECT, "object");
		xmlMap.put(XMLNS_COLLECTION, "collection");
		xmlMap.put(XMLNS_ATTRIBUTE, "attribute");
		
		referencePrimaryKeyIds = new ArrayList<String>();
		referenceForeignKeyIds = new ArrayList<String>();
		lineModels = new HashMap<LineModel, String>();
		tableModels = new HashMap<String, TableModel>();
		tableShortcutModels = new ArrayList<TableShortcutModel>();
		databaseTypeModel = new DatabaseTypeModel();
		dataTypeModelList = new ArrayList<DataTypeModel>();
		domainModels = new ArrayList<ColumnModel>();
		physicalDiagramModels = new ArrayList<PhysicalDiagramModel>();
		tableModelsWithNoPhysicalDiagram = new ArrayList<TableModel>();
	}
	
	/**
	 * @return the document
	 */
	public Document getDocument() {
		return document;
	}
	
	/**
	 * 加载pdm文件
	 * @param filePath pdm文件路径
	 * @throws DocumentException pdm文件读取异常
	 */
	public void loadPdm(String filePath) throws DocumentException {
		document = saxReader.read(new File(filePath));
	}
	
	/**
	 * 获取一个物理数据模型
	 * @return 一个物理数据模型
	 */
	public PhysicalDataModel getPhysicalDataModel() {
		final PhysicalDataModel physicalDataModel = new PhysicalDataModel();
		
		try {
			PlatformUI.getWorkbench().getProgressService().run(true, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException,
						InterruptedException {
					monitor.beginTask("正在生成项目...请等候", 10);
					
					long t1 = Calendar.getInstance().getTime().getTime();	// 记录起始时间
					
					monitor.subTask("开始生成引用关系...");
					getReferenceJoinIds();	// 主/外键方引用字段Id列表(此方法直接赋值成员变量)
					monitor.worked(1);
					
					monitor.subTask("开始生成数据库类型模型...");
					databaseTypeModel = getDatabaseTypeModel();				// 获取数据库类型
					dataTypeModelList = DatabaseManager.getDataTypeList(databaseTypeModel);	// 获取数据库类型下的所有数据类型列表
					monitor.worked(1);
					
					monitor.subTask("开始生成连接线模型...");
					lineModels = getLineModels();	// 获取所有的连接线模型
					monitor.worked(1);
					
					monitor.subTask("开始生成表格模型...");
					tableModels = getTaleModels();	// 获取所有的表格模型
					monitor.worked(3);
					
					monitor.subTask("开始生成Domain模型...");
					domainModels = getDomainModels();	// 获取所有的Domain域模型
					monitor.worked(1);
					
					monitor.subTask("开始生成物理图形模型...");
					physicalDiagramModels = getPhysicalDiagramModels(physicalDataModel);	// 获取所有的物理图形模型
					monitor.worked(1);
					
					monitor.subTask("开始生成物理数据模型...");
					Element elementPhysicalDataModel = getElementPhysicalDataModel();
					
					String physicalDataModelName = elementPhysicalDataModel.element(ELEMENT_CODE).getText();
					physicalDataModel.setId(physicalDataModelName);				// 赋值物理数据模型名称
					physicalDataModel.setName(elementPhysicalDataModel.element(ELEMENT_NAME).getText());		// 赋值物理数据模型描述
					physicalDataModel.setDatabaseTypeModel(databaseTypeModel);	// 赋值物理数据模型的数据库类型
					physicalDataModel.setDomainList(domainModels);				// 赋值物理数据模型domain域
					monitor.worked(1);
					
					monitor.subTask("开始生成包模型...");
					// 赋值物理数据模型下的所有Package模型
					List<PackageModel> packageModels = getPackageModels(physicalDataModel);
					for (PackageModel packageModel : packageModels) {
						physicalDataModel.getPackageModelSet().add(packageModel);
					}
					monitor.worked(1);
					
					logger.debug("over to generate PhysicalDataModel id = " + physicalDataModelName);
					
					long t2 = Calendar.getInstance().getTime().getTime();	// 记录结束时间
					logger.debug("use time is : " + (t2-t1) + " ms");

				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		/*long t1 = Calendar.getInstance().getTime().getTime();	// 记录起始时间
		
		getReferenceJoinIds();	// 主/外键方引用字段Id列表(此方法直接赋值成员变量)
		databaseTypeModel = getDatabaseTypeModel();				// 获取数据库类型
		dataTypeModelList = DatabaseManager.getDataTypeList(databaseTypeModel);	// 获取数据库类型下的所有数据类型列表
		lineModels = getLineModels();	// 获取所有的连接线模型
		tableModels = getTaleModels();	// 获取所有的表格模型
		domainModels = getDomainModels();	// 获取所有的Domain域模型
		physicalDiagramModels = getPhysicalDiagramModels(physicalDataModel);	// 获取所有的物理图形模型
		
		Element elementPhysicalDataModel = getElementPhysicalDataModel();
		
		String physicalDataModelName = elementPhysicalDataModel.element(ELEMENT_CODE).getText();
		physicalDataModel.setId(physicalDataModelName);				// 赋值物理数据模型名称
		physicalDataModel.setName(elementPhysicalDataModel.element(ELEMENT_NAME).getText());		// 赋值物理数据模型描述
		physicalDataModel.setDatabaseTypeModel(databaseTypeModel);	// 赋值物理数据模型的数据库类型
		physicalDataModel.setDomainList(domainModels);				// 赋值物理数据模型domain域
		
		// 赋值物理数据模型下的所有Package模型
		List<PackageModel> packageModels = getPackageModels(physicalDataModel);
		for (PackageModel packageModel : packageModels) {
			physicalDataModel.getPackageModelSet().add(packageModel);
		}
		
		logger.debug("over to generate PhysicalDataModel id = " + physicalDataModelName);
		
		long t2 = Calendar.getInstance().getTime().getTime();	// 记录结束时间
		logger.debug("use time is : " + (t2-t1) + " ms");*/
		
		return physicalDataModel;
	}
	
	/**
	 * 获取所有的Package模型
	 * @param physicalDataModel 所有的Package模型所属的物理数据模型
	 * @return 所有的Package模型
	 */
	public List<PackageModel> getPackageModels(PhysicalDataModel physicalDataModel) {
		List<PackageModel> packageModels = new ArrayList<PackageModel>();
		
		List<Element> elementPackages = getElementPackages();
		
		// 如果Packages模型不存在或者为空,则直接返回null
		if (elementPackages == null || elementPackages.isEmpty()) {
			return packageModels;
		}
		
		for (Element elementPackage : elementPackages) {
			PackageModel packageModel = getPackageModel(elementPackage, physicalDataModel);
			if (packageModel != null) {
				packageModels.add(packageModel);
			}
		}
		
		PackageModel defaultPackageModel = generateDefaultPackage(physicalDataModel, packageModels);
		packageModels.add(defaultPackageModel);
		
		return packageModels;
	}
	
	/**
	 * 生成默认的包模型,用于包含所有不属于任何包模型的物理图形模型
	 * @param physicalDataModel	包模型所属的物理数据模型
	 * @param packageModels 所有的包模型集合
	 * @return
	 */
	public PackageModel generateDefaultPackage(
			PhysicalDataModel physicalDataModel, 
			List<PackageModel> packageModels) {
		PackageModel packageModel = new PackageModel();
		
		packageModel.setPhysicalDataModel(physicalDataModel);	// 设置Package模型所属的物理数据模型
		packageModel.setId(DEFAULT_PACKAGE_ID);					// 设置Package模型Id
		packageModel.setName(DEFAULT_PACKAGE_NAME);				// 设置Package模型Name
		
		LinkedHashSet<PhysicalDiagramModel> modelsWithNoPackage = new LinkedHashSet<PhysicalDiagramModel>();
		
		// 如果包模型集合不为空,则找出不属于任何报模型的物理图形模型列表,将此列表赋予默认的包模型
		if (!packageModels.isEmpty()) {
			// 找出没有报模型的物理图形模型列表
			List<PhysicalDiagramModel> modelsWithPackage = new ArrayList<PhysicalDiagramModel>();
			for (PackageModel pModel : packageModels) {
				modelsWithPackage.addAll(pModel.getPhysicalDiagramModelSet());
			}
			
			for (PhysicalDiagramModel physicalDiagramModel : physicalDiagramModels) {
				boolean isExist = false;
				for (PhysicalDiagramModel subPhysicalDiagramModel : modelsWithPackage) {
					if (physicalDiagramModel.getId().equalsIgnoreCase(subPhysicalDiagramModel.getId())) {
						isExist = true;
						break;
					}
				}
				
				if (!isExist) {
					physicalDiagramModel.setPackageModel(packageModel);	// 赋值物理图形模型所属的包模型
					modelsWithNoPackage.add(physicalDiagramModel);
				}
			}
		} else {
			// 如果包模型集合不为空,则直接将所有的物理图形模型赋予默认的包模型
			for (PhysicalDiagramModel physicalDiagramModel : physicalDiagramModels) {
				physicalDiagramModel.setPackageModel(packageModel);	// 赋值物理图形模型所属的包模型
				modelsWithNoPackage.add(physicalDiagramModel);
			}
		}
		
		// 赋值包模型下的物理图形模型所属
		packageModel.setPhysicalDiagramModelSet(modelsWithNoPackage);
		
		return packageModel;
	}
	
	/**
	 * 获取一个Package模型
	 * @param elementPackage Package模型对象(xml格式)
	 * @param physicalDataModel Package模型所属的物理数据模型
	 * @return 一个Package模型(如果传入的elementPackage不是一个实际的Package模型,即Id=null,则直接返回null)
	 */
	public PackageModel getPackageModel(Element elementPackage, PhysicalDataModel physicalDataModel) {
		if (elementPackage.attributeValue(ATTRIBUTE_ID) == null) {
			return null;
		}
		
		PackageModel packageModel = new PackageModel();
		
		String packageModelId = elementPackage.attributeValue(ATTRIBUTE_ID);
		logger.debug("start to generate packageModel id = " + packageModelId);
		
		packageModel.setPhysicalDataModel(physicalDataModel);	// 设置Package模型所属的物理数据模型
		packageModel.setId(packageModelId);						// 设置Package模型Id
		packageModel.setName(elementPackage.element(ELEMENT_NAME).getText());		// 设置Package模型Name
		
		if (elementPackage.element(ELEMENT_COMMENT) != null) {
			packageModel.setNote(elementPackage.element(ELEMENT_COMMENT).getText());	// 设置Package模型Comment
		}
		
		// 获取一个Package模型下的所有物理图形模型Id
		List<String> physicalDiagramModelIds = new ArrayList<String>();
		List<Element> elementPhysicalDiagramModels = elementPackage.element(ELEMENT_PHYSICALDIAGRAMS).elements(ELEMENT_PHYSICALDIAGRAM);
		for (Element element : elementPhysicalDiagramModels) {
			physicalDiagramModelIds.add(element.attributeValue(ATTRIBUTE_ID));
		}
		
		// 获取一个Package模型下的所有物理图形模型
		LinkedHashSet<PhysicalDiagramModel> physicalDiagramModelList = new LinkedHashSet<PhysicalDiagramModel>();
		for (String physicalDataModelId : physicalDiagramModelIds) {
			for (PhysicalDiagramModel physicalDiagramModel : physicalDiagramModels) {
				if (physicalDataModelId.equalsIgnoreCase(physicalDiagramModel.getId())) {
					physicalDiagramModel.setPackageModel(packageModel);
					physicalDiagramModelList.add(physicalDiagramModel);
					break;
				}
			}
		}
		
		packageModel.setPhysicalDiagramModelSet(physicalDiagramModelList);	// 设置Package模型的物理图形模型列表
		
		return packageModel;
	}
	
	/**
	 * 返回所有的物理图形模型
	 * @return 所有的物理图形模型
	 */
	public List<PhysicalDiagramModel> getPhysicalDiagramModels(PhysicalDataModel physicalDataModel) {
		List<PhysicalDiagramModel> physicalDiagramModels = new ArrayList<PhysicalDiagramModel>();
		
		List<Element> elementPhysicalDiagrams = getElementPhysicalDiagrams();
		
		// 获取所有在物理图形模型上有位置信息的表格模型对象Id列表
		List<String> tableIdsWithPhysicalDiagram = new ArrayList<String>();
		for (Element elementPhysicalDiagram : elementPhysicalDiagrams) {
			Element elementSymbols = elementPhysicalDiagram.element(ELEMENT_SYMBOLS);
			
			if (elementSymbols == null) {
				// 图形为空,没有任何表格及连接线位置信息,直接返回,开始下一个物理图形模型
				continue;
			}
			
			List<Element> elementTableSymbols = elementSymbols.elements(ELEMENT_TABLESYMBOL);
			for (Element elementTableSymbol : elementTableSymbols) {
				Element elementTable = elementTableSymbol.element(ELEMENT_OBJECT).element(ELEMENT_TABLE);
				if (elementTable != null) {
					tableIdsWithPhysicalDiagram.add(elementTable.attributeValue(ATTRIBUTE_REF));
				}
			}
		}
		
		// 获取所有在物理图形模型上没有位置信息的表格模型列表
		for (TableModel tableModel : tableModels.values()) {
			boolean isExist = false;
			for (String tableId : tableIdsWithPhysicalDiagram) {
				if (tableId.equalsIgnoreCase(tableModel.getId())) {
					isExist = true;
					break;
				}
			}
			
			if (!isExist) {
				tableModelsWithNoPhysicalDiagram.add(tableModel);
			}
		}
		
		
		
		for (Element elementPhysicalDiagram : elementPhysicalDiagrams) {
			PhysicalDiagramModel physicalDiagramModel = new PhysicalDiagramModel();
			physicalDiagramModel = getPhysicalDiagramModel(elementPhysicalDiagram.attributeValue(ATTRIBUTE_ID), elementPhysicalDiagram, physicalDataModel);
			physicalDiagramModels.add(physicalDiagramModel);
		}
		
		return physicalDiagramModels;
	}
	
	/**
	 * 通过xml文件中的物理图形模型对象,返回一个物理图形模型
	 * @param physicalDiagramId 物理图形模型Id
	 * @param elementPhysicalDiagram xml文件中的物理图形模型对象
	 * @param physicalDataModel 物理图形模型所属的物理数据模型
	 * @return 一个物理图形模型
	 */
	public PhysicalDiagramModel getPhysicalDiagramModel(
			String physicalDiagramId, 
			Element elementPhysicalDiagram, 
			PhysicalDataModel physicalDataModel) {
		logger.debug("start to generate PhysicalDiagramModel id = " + physicalDiagramId);
		
		PhysicalDiagramModel physicalDiagramModel = new PhysicalDiagramModel();
		
		physicalDiagramModel.setId(physicalDiagramId);	// 设置物理图形模型名称
		physicalDiagramModel.setName(elementPhysicalDiagram.element(ELEMENT_NAME).getText());	// 设置物理图形模型描述
		
		// 设置物理图形模型备注
		Element elementNode = elementPhysicalDiagram.element(ELEMENT_NAME);
		if (elementNode != null) {
			physicalDiagramModel.setNote(elementNode.getText());
		}
		
		// 表格及连接线模型位置对象
		Element elementSymbols = elementPhysicalDiagram.element(ELEMENT_SYMBOLS);
		// 如果elementSymbols不存在,则说明物理图形模型是空的
		if (elementSymbols == null) {
			logger.debug("PhysicalDiagramModel id = " 
					+ physicalDiagramId 
					+ " is blank, no exist tables and references");
			return physicalDiagramModel;
		}
		
		List<String> tableIds = new ArrayList<String>();
		// 获取表格位置模型对象列表
		List<Element> elementTableSymbols = elementSymbols.elements(ELEMENT_TABLESYMBOL);
		for (Element elementTableSymbol : elementTableSymbols) {
			Element elementTable = elementTableSymbol.element(ELEMENT_OBJECT).element(ELEMENT_TABLE);
			if (elementTable != null) {
				tableIds.add(elementTable.attributeValue(ATTRIBUTE_REF));
			}
		}
		
		// 挑选一个物理图形模型内的所有表格模型,赋值为map
		LinkedHashMap<String, TableModel> tableModelMap = new LinkedHashMap<String, TableModel>();
		Iterator<TableModel> iterators = tableModels.values().iterator();
		while (iterators.hasNext()) {
			TableModel tableModel = iterators.next();
			for (String tableId : tableIds) {
				if (tableId.equalsIgnoreCase(tableModel.getId())) {
					// 赋值表格所属的物理图形模型
					tableModel.setPhysicalDiagramModel(physicalDiagramModel);
					
					// 将一个物理图形模型中的所有表格填入map
					tableModelMap.put(tableModel.getTableName(), tableModel);
				}
			}
		}
		physicalDiagramModel.setTableMap(tableModelMap);
		
		// 获取表格快捷方式位置模型对象列表
		List<String> tableShortcutIds = new ArrayList<String>();
		for (Element elementTableSymbol : elementTableSymbols) {
			Element elementTableShortcut = elementTableSymbol.element(ELEMENT_OBJECT).element(ELEMENT_SHORTCUT);
			if (elementTableShortcut != null) {
				tableShortcutIds.add(elementTableShortcut.attributeValue(ATTRIBUTE_REF));
			}
		}
		
		// 挑选一个物理图形模型内的所有表格快捷方式模型,赋值为map
		Map<String, TableShortcutModel> tableShortcutMap = new HashMap<String, TableShortcutModel>();
		for (String tableShortcutId : tableShortcutIds) {
			for (TableShortcutModel tableShortcutModel : tableShortcutModels) {
				if (tableShortcutId.equalsIgnoreCase(tableShortcutModel.getId())) {
					TableModel tableModel = tableShortcutModel.getTargetTableModel();
					
					// 当目标表格模型所属的物理图形模型为空时,则需要上下赋值,以便遍历
					if (tableModel.getPhysicalDiagramModel() == null) {
						tableModel.setPhysicalDiagramModel(physicalDiagramModel);
						
						// 当目标表格模型不属于任何物理图形模型时,则加入到与表格快捷方式所属的物理图形模型中
						if (tableModelsWithNoPhysicalDiagram.contains(tableModel)) {
							physicalDiagramModel.getTableMap().put(tableModel.getTableName(), tableModel);
						}
					}
					
					// 赋值表格快捷方式所属的物理图形模型
					tableShortcutModel.setPhysicalDiagramModel(physicalDiagramModel);
					
					// 将一个物理图形模型中的所有表格快捷方式填入map
					tableShortcutMap.put(tableShortcutId, tableShortcutModel);
					
					break;
				}
			}
		}
		physicalDiagramModel.setTableShortcutMap(tableShortcutMap);
		
		// 一个物理图形模型中所有的连接线模型
		List<Element> elementReferenceSymbols = elementSymbols.elements(ELEMENT_REFERENCESYMBOL);
		List<LineXmlProperty> lineXmlProperties = getLineXmlProperties(elementReferenceSymbols, elementTableSymbols);
		
		// 设置一个物理图形模型的LineXmlProperty
		physicalDiagramModel.setLineXmlPropertyList(lineXmlProperties);
		
		// 一个物理图形模型中所有的表格模型
		List<NodeXmlProperty> nodeXmlProperties = getNodeXmlProperties(elementTableSymbols);
		
		// 设置每一个表格模型中以该表格为起点的连接线模型列表
		for (int i = 0; i < nodeXmlProperties.size(); i++) {
			List<String> lineIdList = new ArrayList<String>();
			
			for (LineXmlProperty lineXmlProperty : lineXmlProperties) {
				if (nodeXmlProperties.get(i).getId().equalsIgnoreCase(lineXmlProperty.getSourceNodeId())) {
					lineIdList.add(lineXmlProperty.getId());
				}
			}
			
			nodeXmlProperties.get(i).setLineIdList(lineIdList);
		}
		
		// 设置一个物理图形模型的NodeXmlProperty
		physicalDiagramModel.setNodeXmlPropertyList(nodeXmlProperties);
		
//		logger.debug("over to generate PhysicalDiagramModel id = " + physicalDiagramId);
		return physicalDiagramModel;
	}
	
	/**
	 * @deprecated
	 * 返回一个物理图形模型
	 * @param physicalDiagramId 物理图形模型Id
	 * @return 一个物理图形模型
	 */
	public PhysicalDiagramModel getPhysicalDiagramModel(String physicalDiagramId, PhysicalDataModel physicalDataModel) {
		logger.debug("start to generate PhysicalDiagramModel id = " + getPhysicalDiagramCode(physicalDiagramId));
		
		PhysicalDiagramModel physicalDiagramModel = new PhysicalDiagramModel();
		
//		physicalDiagramModel.setPackageModel(physicalDataModel);
		
		physicalDiagramModel.setId(getPhysicalDiagramCode(physicalDiagramId));
		physicalDiagramModel.setName(getPhysicalDiagramName(physicalDiagramId));
		physicalDiagramModel.setNote(getPhysicalDiagramComment(physicalDiagramId));
		
		// 获取一个物理图形模型内所有的表格名称
		List<String> tableNames = new ArrayList<String>();
		List<String> tableIds = getPhysicalDiagramTableIds(physicalDiagramId);
		for (String tableId : tableIds) {
			tableNames.add(getTableCode(tableId));
		}
		
		// 挑选一个物理图形模型内的所有表格模型,赋值为map
		LinkedHashMap<String, TableModel> tableModelMap = new LinkedHashMap<String, TableModel>();
//		List<TableModel> tableModels = getTaleModels(physicalDiagramModel);
		for (int i = 0; i < tableModels.size(); i++) {
			for (String tableName : tableNames) {
				if (tableName.equalsIgnoreCase(tableModels.get(i).getTableName())) {
					// 赋值表格所属的物理图形模型
					tableModels.get(i).setPhysicalDiagramModel(physicalDiagramModel);
					
					// 将一个物理图形模型中的所有表格填入map
					tableModelMap.put(tableName, tableModels.get(i));
				}
			}
		}
		physicalDiagramModel.setTableMap(tableModelMap);
		
		// 一个物理图形模型中所有的连接线模型
		List<LineXmlProperty> lineXmlProperties = getLineXmlProperties(physicalDiagramId);
		
		// 设置一个物理图形模型的LineXmlProperty
		physicalDiagramModel.setLineXmlPropertyList(lineXmlProperties);
		
		// 一个物理图形模型中所有的表格模型
		List<NodeXmlProperty> nodeXmlProperties = getNodeXmlProperties(physicalDiagramId);
		
		// 设置每一个表格模型中以该表格为起点的连接线模型列表
		for (int i = 0; i < nodeXmlProperties.size(); i++) {
			List<String> lineIdList = new ArrayList<String>();
			
			for (LineXmlProperty lineXmlProperty : lineXmlProperties) {
				if (nodeXmlProperties.get(i).getId().equalsIgnoreCase(lineXmlProperty.getSourceNodeId())) {
					lineIdList.add(lineXmlProperty.getId());
				}
			}
			
			nodeXmlProperties.get(i).setLineIdList(lineIdList);
		}
		
		
		// 设置一个物理图形模型的NodeXmlProperty
		physicalDiagramModel.setNodeXmlPropertyList(nodeXmlProperties);
		
		logger.debug("over to generate PhysicalDiagramModel id = " + getPhysicalDiagramCode(physicalDiagramId));
		return physicalDiagramModel;
	}
	
	/**
	 * 获取一个物理图形模型内所有的表格节点位置模型
	 * @param elementTableSymbolsd xml文件中一个物理图形模型内所有的表格节点节点位置模型对象
	 * @return 一个物理图形模型内所有的表格节点位置模型
	 */
	public List<NodeXmlProperty> getNodeXmlProperties(List<Element> elementTableSymbols) {
		List<NodeXmlProperty> nodeXmlProperties = new ArrayList<NodeXmlProperty>();
		
		if (elementTableSymbols == null) {
			logger.debug("no exist tableSymbols");
			return nodeXmlProperties;
		}
		
		for (Element elementTableSymbol : elementTableSymbols) {
			NodeXmlProperty nodeXmlProperty = getNodeXmlProperty(elementTableSymbol);
			if (nodeXmlProperty != null) {
				nodeXmlProperties.add(nodeXmlProperty);
			}
		}
		
		return nodeXmlProperties;
	}
	
	
	/**
	 * 获取一个表格节点位置模型
	 * @param elementTableSymbol xml文件中一个表格节点模型对象
	 * @return 一个表格节点位置模型(如果该表节点的不存在,则直接返回null)
	 */
	public NodeXmlProperty getNodeXmlProperty(Element elementTableSymbol) {
		NodeXmlProperty nodeXmlProperty = new NodeXmlProperty();
		
		// 设置节点模型type表格的类型
		nodeXmlProperty.setType(TableGefModel.class.getName());
		
		// 设置表格名称(Id)
		Element elementTable = elementTableSymbol.element(ELEMENT_OBJECT).element(ELEMENT_TABLE);
		
		// 如果该节点为null,可能是该对象为一个表格快捷方式模型
		// 此时,需要获取Shortcut节点对象进行进一步判断
		if (elementTable == null) {
			elementTable = elementTableSymbol.element(ELEMENT_OBJECT).element(ELEMENT_SHORTCUT);
			if (elementTable == null) {
				return null;
			} else {
				// 设置节点模型type为快捷方式的类型
				nodeXmlProperty.setType(TableShortcutGefModel.class.getName());
			}
		}
		String tableName = elementTable.attributeValue(ATTRIBUTE_REF);
		nodeXmlProperty.setId(tableName);
		
		// 设置表格位置信息
		nodeXmlProperty.setRectangle(getTableNodeRectangle(elementTableSymbol.element(ELEMENT_RECT).getText()));
		
		logger.debug("generate nodeXmlProperty id = " + tableName);
		return nodeXmlProperty;
	}
	
	/**
	 * @deprecated
	 * 获取一个物理图形模型内所有的表格节点位置模型
	 * @param physicalDiagramId 物理图形模型Id
	 * @return 一个物理图形模型内所有的表格节点位置模型
	 */
	public List<NodeXmlProperty> getNodeXmlProperties(String physicalDiagramId) {
		List<NodeXmlProperty> nodeXmlProperties = new ArrayList<NodeXmlProperty>();
		
		List<String> tableSymbolIds = getPhysicalDiagramTableSymbolIds(physicalDiagramId);
		for (String tableSymbolId : tableSymbolIds) {
			nodeXmlProperties.add(getNodeXmlProperty(tableSymbolId));
		}
		
		return nodeXmlProperties;
	}
	
	/**
	 * 获取一个表节点位置模型
	 * @param TableSymbolId 标记对象Id
	 * @return 一个表节点位置模型
	 */
	public NodeXmlProperty getNodeXmlProperty(String tableSymbolId) {
		NodeXmlProperty nodeXmlProperty = new NodeXmlProperty();
		
		// 设置节点模型type
		nodeXmlProperty.setType(TableGefModel.class.getName());
		
		// 获取表格模型位置信息对象
		Element elementTableSymbol = getElementTableSymbol(tableSymbolId);
		
		// 设置表格名称(Id)
		String tableName = elementTableSymbol.element(ELEMENT_OBJECT).element(ELEMENT_TABLE).attributeValue(ATTRIBUTE_REF);
		nodeXmlProperty.setId(tableName);
		
		// 设置表格位置信息
		nodeXmlProperty.setRectangle(getTableNodeRectangle(elementTableSymbol.element(ELEMENT_RECT).getText()));
		
//		logger.debug("generate nodeXmlProperty id = " + tableName);
		return nodeXmlProperty;
	}
	
	/**
	 * 获取一个表格模型位置对象
	 * @param tableSymbolId 表格模型位置对象Id
	 * @return 表格模型位置对象
	 */
	public Element getElementTableSymbol(String tableSymbolId) {
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_PHYSICALDIAGRAM 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_SYMBOLS
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLESYMBOL
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ tableSymbolId 
				+ "']";
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element elementTableSymbol = (Element) xPath.selectSingleNode(document);
		
		return elementTableSymbol;
	}
	
	/**
	 * 获取表格节点模型的位置信息对象
	 * @param positionValue 表格节点模型的位置信息字符串
	 * @return 表格节点模型的位置信息对象
	 */
	public Rectangle getTableNodeRectangle(String positionValue) {
		List<Point> points = getPositionValue(positionValue);
		
		Rectangle rectangle = new Rectangle();
//		rectangle.width = -1;
//		rectangle.height = -1;
		rectangle.width = Math.abs(points.get(0).x - points.get(1).x)/CANVAS_RATE*2;
		rectangle.height = Math.abs(points.get(0).y - points.get(1).y)/CANVAS_RATE*2;
		rectangle.x = points.get(0).x/CANVAS_RATE*2;
		rectangle.y = points.get(0).y/CANVAS_RATE * -2;
		
//		logger.debug("rectangle position is : " + rectangle);
		return rectangle;
	}
	
	/**
	 * 获取一个物理图形模型内所有的连接线节点位置模型
	 * @param elementReferenceSymbols xml文件中一个物理图形模型内所有的连接线节点位置模型对象
	 * @param elementTableSymbols xml文件中一个物理图形模型内所有的表格节点位置模型对象
	 * @return 一个物理图形模型内所有的连接线节点位置模型
	 */
	public List<LineXmlProperty> getLineXmlProperties(
			List<Element> elementReferenceSymbols, 
			List<Element> elementTableSymbols) {
		List<LineXmlProperty> lineXmlProperties = new ArrayList<LineXmlProperty>();
		
		if (elementReferenceSymbols == null) {
			logger.debug("no exist referenceSymbols");
			return lineXmlProperties;
		}
		
		for (Element elementReferenceSymbol : elementReferenceSymbols) {
			LineXmlProperty lineXmlProperty = getLineXmlProperty(elementReferenceSymbol, elementTableSymbols);
			if (lineXmlProperty != null) {
				lineXmlProperties.add(lineXmlProperty);
			}
		}
		
		return lineXmlProperties;
	}
	
	/**
	 * 获取一个连接线节点位置模型
	 * @param elementReferenceSymbol xml文件中一个连接线节点模型对象
	 * @param elementTableSymbol xml文件中一个表格节点模型对象
	 * @return 一个连接线节点位置模型(如果该连接线节点的起始表节点或者终止表节点不存在,则直接返回null)
	 */
	public LineXmlProperty getLineXmlProperty(Element elementReferenceSymbol, List<Element> elementTableSymbols) {
		LineXmlProperty lineXmlProperty = new LineXmlProperty();
		
		// 设置连接线模的Id
		lineXmlProperty.setId(elementReferenceSymbol.attributeValue(ATTRIBUTE_ID));
		
		// 设置连接线模的type
		lineXmlProperty.setType(LineGefModel.class.getName());
		
		// 赋值连接线模型位置对象
		lineXmlProperty.setBendPointList(getPositionValue(elementReferenceSymbol.element(ELEMENT_LISTOFPOINTS).getText().trim()));
		
		// 赋值连接线模型Id
		Element elementRef = elementReferenceSymbol.element(ELEMENT_OBJECT).element(ELEMENT_REFERENCE);
		
		if (elementRef == null) {
			return null;
		}
		
		String referenceName = elementRef.attributeValue(ATTRIBUTE_REF);
		lineXmlProperty.setId(referenceName);
		
		// 赋值连接线模型开始节点Id
		String sourceTableSymbolId = elementReferenceSymbol.element(ELEMENT_SOURCESYMBOL).element(ELEMENT_TABLESYMBOL).attributeValue(ATTRIBUTE_REF);
		String sourceNodeId = "";
		for (Element tableSymbol : elementTableSymbols) {
			if (sourceTableSymbolId.equalsIgnoreCase(tableSymbol.attributeValue(ATTRIBUTE_ID))) {
				Element elementSourceTable = tableSymbol.element(ELEMENT_OBJECT).element(ELEMENT_TABLE);
				if (elementSourceTable == null) {
					return null;
				}
				sourceNodeId = elementSourceTable.attributeValue(ATTRIBUTE_REF);
				break;
			}
		}
		lineXmlProperty.setSourceNodeId(sourceNodeId);
		
		// 赋值连接线模型结束节点Id
		String targetTableSymbolId = elementReferenceSymbol.element(ELEMENT_DESTINATIONSYMBOL).element(ELEMENT_TABLESYMBOL).attributeValue(ATTRIBUTE_REF);
		String targetNodeId = "";
		for (Element tableSymbol : elementTableSymbols) {
			if (targetTableSymbolId.equalsIgnoreCase(tableSymbol.attributeValue(ATTRIBUTE_ID))) {
				Element elementTargetTable = tableSymbol.element(ELEMENT_OBJECT).element(ELEMENT_TABLE);
				// 如果该节点为null,可能是该对象为一个表格快捷方式模型
				// 此时,需要获取Shortcut节点对象进行进一步判断
				if (elementTargetTable == null) {
					elementTargetTable = tableSymbol.element(ELEMENT_OBJECT).element(ELEMENT_SHORTCUT);
					if (elementTargetTable == null) {
						return null;
					}
				}
				targetNodeId = elementTargetTable.attributeValue(ATTRIBUTE_REF);
				break;
			}
		}
		lineXmlProperty.setTargetNodeId(targetNodeId);
		
		logger.debug("generate lineXmlProperty id = " + referenceName);
		return lineXmlProperty;
	}
	
	/**
	 * @deprecated
	 * 获取一个物理图形模型内所有的连接线节点位置模型
	 * @param physicalDiagramId 物理图形模型Id
	 * @return 一个物理图形模型内所有的连接线节点位置模型
	 */
	public List<LineXmlProperty> getLineXmlProperties(String physicalDiagramId) {
		List<LineXmlProperty> lineXmlProperties = new ArrayList<LineXmlProperty>();
		
		List<String> referenceSymbolIds = getPhysicalDiagramReferenceSymbolIds(physicalDiagramId);
		for (String referenceSymbolId : referenceSymbolIds) {
			lineXmlProperties.add(getLineXmlProperty(referenceSymbolId));
		}
		
		return lineXmlProperties;
	}
	
	/**
	 * 获取一个连接线节点位置模型
	 * @param referenceSymbolId 连接线节点对象Id
	 * @return 一个连接线节点位置模型
	 */
	public LineXmlProperty getLineXmlProperty(String referenceSymbolId) {
		LineXmlProperty lineXmlProperty = new LineXmlProperty();
		
		// 设置连接线模的type
		lineXmlProperty.setType(LineGefModel.class.getName());
		
		// 获取连接线模型位置信息对象
		Element elementReferenceSymbol = getElementReferenceSymbol(referenceSymbolId);
		
		// 赋值连接线模型位置对象
		lineXmlProperty.setBendPointList(getPositionValue(elementReferenceSymbol.element(ELEMENT_LISTOFPOINTS).getText().trim()));
		
		// 赋值连接线模型Id
		String referenceName = elementReferenceSymbol.element(ELEMENT_OBJECT).element(ELEMENT_REFERENCE).attributeValue(ATTRIBUTE_REF);
		lineXmlProperty.setId(referenceName);
		
		// 赋值连接线模型开始节点Id
		String sourceNodeName = getTableIdByTableSymbolId(elementReferenceSymbol.element(ELEMENT_SOURCESYMBOL).element(ELEMENT_TABLESYMBOL).attributeValue(ATTRIBUTE_REF));
		lineXmlProperty.setSourceNodeId(sourceNodeName);
		
		// 赋值连接线模型结束节点Id
		String targetNodeName = getTableIdByTableSymbolId(elementReferenceSymbol.element(ELEMENT_DESTINATIONSYMBOL).element(ELEMENT_TABLESYMBOL).attributeValue(ATTRIBUTE_REF));
		lineXmlProperty.setTargetNodeId(targetNodeName);
		
//		// 赋值连接线模型Id
//		String referenceName = getReferenceCode(elementReferenceSymbol.element(ELEMENT_OBJECT).element(ELEMENT_REFERENCE).attributeValue(ATTRIBUTE_REF));
//		lineXmlProperty.setId(referenceName);
//		
//		// 赋值连接线模型开始节点Id
//		String sourceNodeName = getTableCode(getTableIdByTableSymbolId(elementReferenceSymbol.element(ELEMENT_SOURCESYMBOL).element(ELEMENT_TABLESYMBOL).attributeValue(ATTRIBUTE_REF)));
//		lineXmlProperty.setSourceNodeId(sourceNodeName);
//		
//		// 赋值连接线模型结束节点Id
//		String targetNodeName = getTableCode(getTableIdByTableSymbolId(elementReferenceSymbol.element(ELEMENT_DESTINATIONSYMBOL).element(ELEMENT_TABLESYMBOL).attributeValue(ATTRIBUTE_REF)));
//		lineXmlProperty.setTargetNodeId(targetNodeName);
		
		
//		// 赋值连接线模型Id
//		String referenceName = getReferenceCode(getReferenceIdByReferenceSymbolId(referenceSymbolId));
//		lineXmlProperty.setId(referenceName);
//		
//		// 赋值连接线模型开始节点Id
//		String sourceNodeName = getTableCode(getTableIdByTableSymbolId(getSourceTableSymbolIdByReferenceSymbolId(referenceSymbolId)));
//		lineXmlProperty.setSourceNodeId(sourceNodeName);
//		
//		// 赋值连接线模型结束节点Id
//		String targetNodeName = getTableCode(getTableIdByTableSymbolId(getDestinationTableSymbolIdByReferenceSymbolId(referenceSymbolId)));
//		lineXmlProperty.setTargetNodeId(targetNodeName);
//		
//		// 赋值连接线模型位置对象
//		lineXmlProperty.setBendPointList(getLineListOfPoints(referenceSymbolId));
		
		logger.debug("generate lineXmlProperty id = " + referenceName);
		return lineXmlProperty;
	}
	
	/**
	 * 获取一个连接线模型位置对象
	 * @param referenceSymbolId 连接线模型位置对象Id
	 * @return 连接线模型位置对象
	 */
	public Element getElementReferenceSymbol(String referenceSymbolId) {
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_PHYSICALDIAGRAM 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_SYMBOLS
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_REFERENCESYMBOL
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ referenceSymbolId 
				+ "']";
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element elementReferenceSymbol = (Element) xPath.selectSingleNode(document);
		
		return elementReferenceSymbol;
	}
	
	/**
	 * 获取当前数据库模型
	 * @return 当前数据库模型
	 */
	public DatabaseTypeModel getDatabaseTypeModel() {
		DatabaseTypeModel databaseTypeModel = new DatabaseTypeModel();
		
		Element elementDatabase = getElementDatabase();
		
		// 赋值数据库类型名称
		String databaseName = elementDatabase.element(ELEMENT_NAME).getText();
		databaseTypeModel.setDatabaseName(databaseName);
		
		// 赋值数据库code
		String databaseCode = elementDatabase.element(ELEMENT_CODE).getText();
		databaseTypeModel.setCode(databaseCode);
		
		// DatabaseTypeModel模型中的version、type赋值
		DatabaseTypeModel databaseValue = DatabaseManager.getDatabaseTypeModelBydatabaseCode(databaseCode);
		if (databaseValue != null) {
			databaseTypeModel.setType(databaseValue.getType());
			databaseTypeModel.setVersion(databaseValue.getVersion());
		} else {
			databaseTypeModel.setType(DmConstants.UNDEFINED);
			databaseTypeModel.setVersion(DmConstants.UNDEFINED);
		}
		
		return databaseTypeModel;
	}
	
	/**
	 * 获取所有表格模型
	 * @return 所有表格模型Map列表 key=ObjectID, value=TableModel
	 */
	public Map<String, TableModel> getTaleModels() {
		List<Element> elementTableList = getElementTables();
		
		// 获取所有的表格模型(TableModel)
		Map<String, TableModel> tableModels = new HashMap<String, TableModel>();
		for (Element element : elementTableList) {
			List<Element> elementTables = element.elements(ELEMENT_TABLE);
			for (Element table : elementTables) {
				String tableObjectId = table.element(ELEMENT_OBJECTID).getText();
				tableModels.put(tableObjectId, getTableModel(table));
			}
		}
		
		// 获取所有的表格快捷方式模型(TableShortcutModel)
		for (Element element : elementTableList) {
			List<Element> elementTableShortcuts = element.elements(ELEMENT_SHORTCUT);
			for (Element tableShortsut : elementTableShortcuts) {
				TableShortcutModel tableShortcutModel = new TableShortcutModel();
				tableShortcutModel = getTableShortcutModel(tableShortsut);
				
				String targetId = tableShortsut.element(ELEMENT_TARGETID).getText();
				tableShortcutModel.setTargetTableModel(tableModels.get(targetId));
				
				tableShortcutModels.add(tableShortcutModel);
			}
		}
		
		return tableModels;
	}
	
	/**
	 * 通过xml文件中的表格对象,获取一个表格模型
	 * @param elementTable xml文件中的表格对象
	 * @return 一个表格模型
	 */
	public TableModel getTableModel(Element elementTable) {
		String tableId = elementTable.attributeValue(ATTRIBUTE_ID);
		logger.debug("start to generate table id = " + tableId);
		
		TableModel tableModel = new TableModel();
		
		// 设置表格Id
		tableModel.setId(tableId);
		
		// 设置表格模型的Id、Name值同为code值为xml文件中的Code节点值
		String tableCode = elementTable.element(ELEMENT_CODE).getText();
		tableModel.setTableName(tableCode);
		
		// 设置表格模型Desc值为xml文件中的Name节点值
		tableModel.setTableDesc(elementTable.element(ELEMENT_NAME).getText());
		
		// 设置表格模型备注值为xml文件中的Comment节点值
		Element elementTableComment = elementTable.element(ELEMENT_COMMENT);
		if (elementTableComment != null && !elementTableComment.getText().isEmpty()) {
			tableModel.setTableNote(elementTableComment.getText());
		}
		
		// 设置表格字段模型
		List<ColumnModel> columnModels = getTableColumns(elementTable.element(ELEMENT_COLUMNS), elementTable.element(ELEMENT_KEYS));
		for (int i = 0; i < columnModels.size(); i++) {
			columnModels.get(i).setTableModel(tableModel);
			tableModel.getColumnList().add(columnModels.get(i));
		}
		
		// 设置以该表格为起点的连接线模型
		List<LineModel> lineModelList = new ArrayList<LineModel>();
		for (LineModel lineModel : lineModels.keySet()) {
			if (tableId.equalsIgnoreCase(lineModels.get(lineModel))) {
				lineModelList.add(lineModel);
			}
		}
		tableModel.setLineModelList(lineModelList);
		
		return tableModel;
	}
	
	/**
	 * @deprecated
	 * 获取一个表格模型
	 * @param tableId 表格Id
	 * @return 表格模型
	 */
	public TableModel getTableModel(String tableId) {
		logger.debug("start to generate table id = " + tableId);

		TableModel tableModel = new TableModel();
		
		// 设置表格Id
		tableModel.setId(tableId);
		
		Element elementTableModel = getElementTableModel(tableId);
		
		// 设置表格模型的Id、Name值同为code值为xml文件中的Code节点值
		String tableCode = elementTableModel.element(ELEMENT_CODE).getText();
//		tableModel.setId(tableCode);
		tableModel.setTableName(tableCode);
		
		// 设置表格模型Desc值为xml文件中的Name节点值
		tableModel.setTableDesc(elementTableModel.element(ELEMENT_NAME).getText());
		
		// 设置表格模型备注值为xml文件中的Comment节点值
		Element elementTableComment = elementTableModel.element(ELEMENT_COMMENT);
		if (elementTableComment != null && !elementTableComment.getText().isEmpty()) {
			tableModel.setTableNote(elementTableComment.getText());
		}
		
//		String tableCode = getTableCode(tableId);
//		tableModel.setId(tableCode);
//		tableModel.setTableName(tableCode);
//		
//		tableModel.setTableDesc(getTableName(tableId));
//		tableModel.setTableNote(getTableComment(tableId));
		
		// 设置表格字段模型
		List<ColumnModel> columnModels = getTableColumns(tableId);
		for (int i = 0; i < columnModels.size(); i++) {
			columnModels.get(i).setTableModel(tableModel);
			tableModel.getColumnList().add(columnModels.get(i));
		}
//		tableModel.setColumnList(columnModels);
		
		
		
		// 设置表格连接线模型
//		List<LineModel> lineModels = getLineModels();	// 获取所以的连接线模型
//		tableModel.setLineModelList(lineModels);
		
//		logger.debug("over to generate table id = " + tableId);
		return tableModel;
	}
	
	/**
	 * 获取一个表格模型对象
	 * @param tableId 表格Id
	 * @return 表格模型对象
	 */
	public Element getElementTableModel(String tableId) {
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ tableId 
				+ "']";
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element elementTableModel = (Element) xPath.selectSingleNode(document);
		
		return elementTableModel;
	}
	
	/**
	 * 获取Domain域模型列表
	 * @return Domain域模型列表
	 */
	public List<ColumnModel> getDomainModels() {
		List<ColumnModel> domainModels = new ArrayList<ColumnModel>();
		
		List<Element> elementPhysicalDomains = getElementPhysicalDomains();
		
		// 如果PhysicalDomain节点对象不存在或者为空,则返回一个空的模型列表
		if (elementPhysicalDomains == null) {
			return domainModels;
		}
		
		for (Element physicalDomain : elementPhysicalDomains) {
			domainModels.add(getDomainModel(physicalDomain));
		}
		
		return domainModels;
	}
	
	/**
	 * 获取一个表格快捷方式模型
	 * @param elementTableShortcutModel 表格快捷方式模型对象(xml格式)
	 * @return 一个表格快捷方式模型
	 */
	public TableShortcutModel getTableShortcutModel(Element elementTableShortcutModel) {
		TableShortcutModel tableShortcutModel = new TableShortcutModel();
		
		tableShortcutModel.setId(elementTableShortcutModel.attributeValue(ATTRIBUTE_ID));
		
		return tableShortcutModel;
	}
	
	/**
	 * 通过一个PhysicalDomain,获取domain域的一个对象模型
	 * @param elementPhysicalDomain PhysicalDomain节点对象
	 * @return domain域的一个对象模型(ColumnModel)
	 */
	public ColumnModel getDomainModel(Element elementPhysicalDomain) {
		return getTableColumn(elementPhysicalDomain, true);	// 公共列,标识值=true
	}
	
	/**
	 * 获取所有的连接线模型
	 * @return 所有的连接线模型
	 */
	public Map<LineModel, String> getLineModels() {
		List<LineModel> lineModels = new ArrayList<LineModel>();
		Map<LineModel, String> mapLineModels = new HashMap<LineModel, String>();
		
		List<Element> elementReferencesList = getElementReferences();
		for (Element element : elementReferencesList) {
			List<Element> elementReferences = element.elements();
			for (Element elementReference : elementReferences) {
//				System.out.println(elementReference.attributeValue(ATTRIBUTE_ID));
				
				Element elementChild = elementReference.element(ELEMENT_CHILDTABLE);
				
				if (elementChild == null) {
					continue;
				}
				
				Element elementChildTable = elementChild.element(ELEMENT_TABLE);
				
				
				if (elementChildTable !=null && !elementChildTable.attributeValue(ATTRIBUTE_REF).isEmpty()) {
					String childTableId = elementChildTable.attributeValue(ATTRIBUTE_REF);
					mapLineModels.put(getLineModel(elementReference), childTableId);
//					lineModel.setChildTableModelId(elementChildTable.attributeValue(ATTRIBUTE_REF));
				}
				lineModels.add(getLineModel(elementReference));
			}
		}
		
//		List<String> referenceIds = getReferenceIds();
//		for (String referenceId : referenceIds) {
//			LineModel lineModel = getLineModel(referenceId);
//			lineModels.add(lineModel);
//		}
		
//		return lineModels;
		return mapLineModels;
	}
	
	/**
	 * 通过xml文件中的一个引用关系连接线对象,获取一个连接线对象模型
	 * @param elementLineModel xml文件中的一个引用关系连接线对象
	 * @return 一个连接线对象模型
	 */
	public LineModel getLineModel(Element elementLineModel) {
		LineModel lineModel = new LineModel();
		
		lineModel.setName(elementLineModel.element(ELEMENT_CODE).getText());
		lineModel.setDesc(elementLineModel.element(ELEMENT_NAME).getText());
		
		// 设置连接线模型备注值
		Element elementComment = elementLineModel.element(ELEMENT_COMMENT);
		if (elementComment != null && !elementComment.getText().isEmpty()) {
			lineModel.setNote(elementComment.getText());
		}
		
		// 设置连接线模型约束名称
		Element elementConstraintName = elementLineModel.element(ELEMENT_FOREIGNKEYCONSTRAINTNAME);
		if (elementConstraintName != null && !elementConstraintName.getText().isEmpty()) {
			lineModel.setConstraintName(elementConstraintName.getText());
		}
		
		// 设置连接线模型关联关系
		Element elementIncidenceRelation = elementLineModel.element(ELEMENT_CARDINALITY);
		if (elementIncidenceRelation != null && !elementIncidenceRelation.getText().isEmpty()) {
			lineModel.setIncidenceRelation(elementIncidenceRelation.getText());
		}
		
		// 设置连接线父节点名称
		Element elementParentTable = elementLineModel.element(ELEMENT_PARENTTABLE).element(ELEMENT_TABLE);
		
		// 如果该节点为null,可能是该对象为一个表格快捷方式模型
		// 此时,需要获取Shortcut节点对象进行进一步判断
		if (elementParentTable == null) {
			elementParentTable = elementLineModel.element(ELEMENT_PARENTTABLE).element(ELEMENT_SHORTCUT);
			if (elementParentTable != null) {
				lineModel.setParentTableModelId(elementParentTable.attributeValue(ATTRIBUTE_REF));
			}
		}
		
		return lineModel;
	}
	
	/**
	 * 获取xml文件中的一个连接线节点值
	 * @param referenceId 连接线Id
	 * @return 一个连接线节点值
	 */
	public Element getElementLineModel(String referenceId) {
		String tableIdXPath = "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_REFERENCES 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_REFERENCE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ referenceId 
				+ "']";
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element elementLineModel = (Element) xPath.selectSingleNode(document);
		
		return elementLineModel;
	}

	/**
	 * @deprecated
	 * 获取一个连接线模型
	 * @param referenceId 连接线对象id
	 * @return 连接线模型
	 */
	public LineModel getLineModel(String referenceId) {
		LineModel lineModel = new LineModel();
		
		Element elementLineModel = getElementLineModel(referenceId);
		
		lineModel.setName(elementLineModel.element(ELEMENT_CODE).getText());
		lineModel.setDesc(elementLineModel.element(ELEMENT_NAME).getText());
		
		// 设置连接线模型备注值
		Element elementComment = elementLineModel.element(ELEMENT_COMMENT);
		if (elementComment != null && !elementComment.getText().isEmpty()) {
			lineModel.setNote(elementComment.getText());
		}
		
		// 设置连接线模型约束名称
		Element elementConstraintName = elementLineModel.element(ELEMENT_FOREIGNKEYCONSTRAINTNAME);
		if (elementConstraintName != null && !elementConstraintName.getText().isEmpty()) {
			lineModel.setConstraintName(elementConstraintName.getText());
		}
		
		// 设置连接线模型关联关系
		Element elementIncidenceRelation = elementLineModel.element(ELEMENT_CARDINALITY);
		if (elementIncidenceRelation != null && !elementIncidenceRelation.getText().isEmpty()) {
			lineModel.setIncidenceRelation(elementIncidenceRelation.getText());
		}
		
//		lineModel.setName(getReferenceCode(referenceId));
//		lineModel.setDesc(getReferenceName(referenceId));
//		lineModel.setNote(getReferenceComment(referenceId));
//		lineModel.setConstraintName(getReferenceForeignKeyConstraintName(referenceId));
//		lineModel.setIncidenceRelation(getReferenceCardinality(referenceId));
		
		// 设置连接线父节点名称
//		String parentTableId = getReferenceParentTableId(referenceId);
		Element elementParentTable = elementLineModel.element(ELEMENT_PARENTTABLE).element(ELEMENT_TABLE);
		if (elementParentTable !=null && !elementParentTable.attributeValue(ATTRIBUTE_REF).isEmpty()) {
			lineModel.setParentTableModelId(elementParentTable.attributeValue(ATTRIBUTE_REF));
		}
		
		return lineModel;
	}
	
	/**
	 * 获取PhysicalDataModel模型对象
	 * @return PhysicalDataModel模型对象
	 */
	public Element getElementPhysicalDataModel() {
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_MODEL;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element elementPhysicalDataModel = (Element) xPath.selectSingleNode(document);
		
		return elementPhysicalDataModel;
	}
	
	/**
	 * 获取物理数据模型名称(Name)节点值
	 * @return 物理数据模型名称(Name)节点值
	 */
	public String getPhysicalDataModelName() {

//		String tableIdXPath = "//o:Model//a:Name";
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_MODEL 
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_NAME;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element name = (Element) xPath.selectSingleNode(document);
		
		return name.getText();
	}
	
	/**
	 * 获取物理数据模型代码(Code)节点值
	 * @return 物理数据模型代码(Code)节点值
	 */
	public String getPhysicalDataModelCode() {

//		String tableIdXPath = "//o:Model//a:Code";
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_MODEL 
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_CODE;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element code = (Element) xPath.selectSingleNode(document);
		
		logger.debug("PhysicalDataModel name is : " + code.getText());
		return code.getText();
	}
	
	/**
	 * 获取数据库类型对象
	 * @return 数据库类型对象
	 */
	public Element getElementDatabase() {
		String tableIdXPath = "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_DBMS 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_SHORTCUT;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element elementDatabase = (Element) xPath.selectSingleNode(document);
		
		return elementDatabase;
	}
	
	/**
	 * 获取当前数据库名称
	 * @return 当前数据库名称
	 */
	public String getDatabaseName() {

//		String tableIdXPath = "//o:Model//a:Name";
		
		String tableIdXPath = "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_DBMS 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_SHORTCUT 
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_NAME;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element name = (Element) xPath.selectSingleNode(document);
		
		logger.debug("DBMS name is : " + name.getText());
		return name.getText();
	}
	
	/**
	 * 获取当前数据库代码(Code)值
	 * @return 当前数据库代码(Code)值
	 */
	public String getDatabaseCode() {

//		String tableIdXPath = "//o:Model//a:Name";
		
		String tableIdXPath = "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_DBMS 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_SHORTCUT 
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_CODE;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element code = (Element) xPath.selectSingleNode(document);
		
		logger.debug("DBMS code is : " + code.getText());
		return code.getText();
	}
	
	/**
	 * 获取pdm文件中所有的Package模型对象
	 * @return pdm文件中所有的Package模型对象
	 */
	public List<Element> getElementPackages() {
		String tableIdXPath = "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_PACKAGES 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_PACKAGE;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		List<Element> packages = (List<Element>) xPath.selectNodes(document);
		
		return packages;
	}
	
	/**
	 * 获取pdm文件中所有的物理图形模型对象
	 * @return pdm文件中所有的物理图形模型对象
	 */
	public List<Element> getElementPhysicalDiagrams() {
		String tableIdXPath = "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_PHYSICALDIAGRAMS 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_PHYSICALDIAGRAM;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		List<Element> physicalDiagrams = (List<Element>) xPath.selectNodes(document);
		
//		for (Element element : physicalDiagrams) {
//			logger.debug("physicalDiagram code is : " + element.element(ELEMENT_CODE).getText());
//		}
		
		return physicalDiagrams;
	}
	
	/**
	 * 获取物理图形模型对象Id列表
	 * @return 物理图形模型对象Id列表
	 */
	public List<String> getPhysicalDiagramIds() {
		List<String> physicalDiagramIds = new ArrayList<String>();
		
		String tableIdXPath = "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_PHYSICALDIAGRAMS 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_PHYSICALDIAGRAM;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		List<Element> physicalDiagrams = (List<Element>) xPath.selectNodes(document);
		for (Element physicalDiagram : physicalDiagrams) {
			physicalDiagramIds.add(physicalDiagram.attributeValue(ATTRIBUTE_ID));
		}
		
		return physicalDiagramIds;
	}
	
	/**
	 * 获取物理图形模型名称(Name)节点值
	 * @param physicalDiagramId 物理图形模型Id
	 * @return 物理图形模型名称(Name)节点值
	 */
	public String getPhysicalDiagramName(String physicalDiagramId) {

//		String tableIdXPath = "//o:Table[@Id='o6']//a:Code";
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_PHYSICALDIAGRAM 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ physicalDiagramId 
				+ "']" 
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_NAME;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element name = (Element) xPath.selectSingleNode(document);
		
		return name.getText();
	}
	
	/**
	 * 获取物理图形模型代码(Code)节点值
	 * @param physicalDiagramId 物理图形模型Id
	 * @return 物理图形模型代码(Code)节点值
	 */
	public String getPhysicalDiagramCode(String physicalDiagramId) {

//		String tableIdXPath = "//o:Table[@Id='o6']//a:Code";
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_PHYSICALDIAGRAM 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ physicalDiagramId 
				+ "']" 
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_CODE;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element code = (Element) xPath.selectSingleNode(document);
		
		return code.getText();
	}
	
	/**
	 * 获取物理图形模型备注(Comment)节点值
	 * @param physicalDiagramId 物理图形模型Id
	 * @return 物理图形模型备注(Comment)节点值(如果备注节点值不存在或者为空,则直接返回null)
	 */
	public String getPhysicalDiagramComment(String physicalDiagramId) {

//		String tableIdXPath = "//o:Table[@Id='o6']//a:Code";
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_PHYSICALDIAGRAM 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ physicalDiagramId 
				+ "']" 
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_COMMENT;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element comment = (Element) xPath.selectSingleNode(document);
		
		// 如果备注节点值不存在或者为空,则直接返回null
		if (comment == null || comment.getText().isEmpty()) {
			return null;
		}
		
		return comment.getText();
	}
	
	/**
	 * 获取一个物理图形模型内所有的表格Id
	 * @param physicalDiagramId 物理图形模型Id
	 * @return 一个物理图形模型内所有的表格Id列表
	 */
	public List<String> getPhysicalDiagramTableIds(String physicalDiagramId) {
		List<String> physicalDiagramTableIds = new ArrayList<String>();
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_PHYSICALDIAGRAM 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ physicalDiagramId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_SYMBOLS
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLESYMBOL
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_OBJECT
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		List<Element> physicalDiagramTables = (List<Element>) xPath.selectNodes(document);
		for (Element physicalDiagram : physicalDiagramTables) {
			physicalDiagramTableIds.add(physicalDiagram.attributeValue(ATTRIBUTE_REF));
		}
		
		return physicalDiagramTableIds;
	}
	
	/**
	 * 获取一个物理图形模型内所有表格标记对象Id
	 * @param physicalDiagramId 物理图形模型Id
	 * @return 物理图形模型内表格标记对象Id列表
	 */
	public List<String> getPhysicalDiagramTableSymbolIds(String physicalDiagramId) {
		List<String> physicalDiagramTableSymbolIds = new ArrayList<String>();
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_PHYSICALDIAGRAM 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ physicalDiagramId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_SYMBOLS
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLESYMBOL;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		List<Element> physicalDiagramTableSymbols = (List<Element>) xPath.selectNodes(document);
		for (Element physicalDiagramTableSymbol : physicalDiagramTableSymbols) {
			String tableId = physicalDiagramTableSymbol.attributeValue(ATTRIBUTE_ID);
			if(tableId != null && !tableId.isEmpty()) {
				physicalDiagramTableSymbolIds.add(tableId);
			}
		}
		
		return physicalDiagramTableSymbolIds;
	}
	
	/**
	 * 通过标记对象Id,获取表格模型Id
	 * @param tableSymbolId 标记对象Id
	 * @return 表格模型Id
	 */
	public String getTableIdByTableSymbolId(String tableSymbolId) {
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_PHYSICALDIAGRAM 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_SYMBOLS
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLESYMBOL
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ tableSymbolId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_OBJECT
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element table = (Element) xPath.selectSingleNode(document);
		
//		if (table == null || table.attributeValue(ATTRIBUTE_REF).isEmpty()) {
//			return null;
//		}
		
		return table.attributeValue(ATTRIBUTE_REF);
	}
	
	/**
	 * @deprecated
	 * 通过表格标记对象Id,获取表格位置模型
	 * @param tableSymbolId 表格标记对象Id
	 * @return 获取表格位置模型
	 */
	public Rectangle getTableRectangle(String tableSymbolId) {
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_PHYSICALDIAGRAM 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_SYMBOLS
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLESYMBOL
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ tableSymbolId 
				+ "']" 
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_RECT;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element rect = (Element) xPath.selectSingleNode(document);
		
		String positionValue = rect.getText();
		List<Point> points = getPositionValue(positionValue);
		
		Rectangle rectangle = new Rectangle();
//		rectangle.width = points.get(0).x;
//		rectangle.height = points.get(0).y;
//		rectangle.x = points.get(1).x;
//		rectangle.y = points.get(1).y;
//		rectangle.width = 100;
//		rectangle.height = 100;
//		rectangle.x = 100;
//		rectangle.y = 100;
//		rectangle.width = Math.abs(points.get(0).x - points.get(1).x)/CANVAS_RATE;
//		rectangle.height = Math.abs(points.get(0).y - points.get(1).y)/CANVAS_RATE;
		rectangle.width = -1;
		rectangle.height = -1;
		rectangle.x = points.get(0).x/CANVAS_RATE;
		rectangle.y = points.get(0).y/CANVAS_RATE * -1;
		
		logger.debug("rectangle position is : " + rectangle);
		return rectangle;
	}
	
	/**
	 * 获取一个物理图形模型内所有连接线标记对象Id
	 * @param physicalDiagramId 物理图形模型Id
	 * @return 物理图形模型内连接线标记对象Id列表
	 */
	public List<String> getPhysicalDiagramReferenceSymbolIds(String physicalDiagramId) {
		List<String> physicalDiagramReferenceSymbolIds = new ArrayList<String>();
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_PHYSICALDIAGRAM 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ physicalDiagramId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_SYMBOLS
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_REFERENCESYMBOL;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		List<Element> physicalDiagramReferenceSymbols = (List<Element>) xPath.selectNodes(document);
		for (Element physicalDiagramReferenceSymbol : physicalDiagramReferenceSymbols) {
			physicalDiagramReferenceSymbolIds.add(physicalDiagramReferenceSymbol.attributeValue(ATTRIBUTE_ID));
		}
		
		return physicalDiagramReferenceSymbolIds;
	}
	
	/**
	 * 通过标记对象Id,获取连接线模型Id
	 * @param referenceSymbolId 标记对象Id
	 * @return 连接线模型Id
	 */
	public String getReferenceIdByReferenceSymbolId(String referenceSymbolId) {
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_PHYSICALDIAGRAM 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_SYMBOLS
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_REFERENCESYMBOL
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ referenceSymbolId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_OBJECT
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_REFERENCE;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element reference = (Element) xPath.selectSingleNode(document);
		
//		if (reference == null) {
//			return null;
//		}
		
		return reference.attributeValue(ATTRIBUTE_REF);
	}
	
	/**
	 * 通过标记对象Id,获取连接线起始表节点标记对象模型Id
	 * @param referenceSymbolId 标记对象Id
	 * @return 起始表节点标记对象模型Id
	 */
	public String getSourceTableSymbolIdByReferenceSymbolId(String referenceSymbolId) {
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_PHYSICALDIAGRAM 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_SYMBOLS
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_REFERENCESYMBOL
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ referenceSymbolId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_SOURCESYMBOL
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLESYMBOL;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element sourceTable = (Element) xPath.selectSingleNode(document);
		
		return sourceTable.attributeValue(ATTRIBUTE_REF);
	}
	
	/**
	 * 通过标记对象Id,获取连接线终止表节点标记对象模型Id
	 * @param referenceSymbolId 标记对象Id
	 * @return 终止表节点标记对象模型Id
	 */
	public String getDestinationTableSymbolIdByReferenceSymbolId(String referenceSymbolId) {
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_PHYSICALDIAGRAM 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_SYMBOLS
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_REFERENCESYMBOL
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ referenceSymbolId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_DESTINATIONSYMBOL
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLESYMBOL;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element destinationTable = (Element) xPath.selectSingleNode(document);
		
		return destinationTable.attributeValue(ATTRIBUTE_REF);
	}
	
	/**
	 * 获取一个连接线对象模型的位置信息
	 * @param referenceSymbolId 连接线对象Id
	 * @return 一个连接线对象模型的位置信息
	 */
	public List<Point> getLineListOfPoints(String referenceSymbolId) {
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_PHYSICALDIAGRAM 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_SYMBOLS
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_REFERENCESYMBOL
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ referenceSymbolId 
				+ "']" 
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_LISTOFPOINTS;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element listOfPoints = (Element) xPath.selectSingleNode(document);
		
		List<Point> points = getPositionValue(listOfPoints.getText());
		return points;
	}
	
	/**
	 * 获取pdm文件中所有的表格模型
	 * @return pdm文件中所有的表格模型
	 */
	public List<Element> getElementTables() {
		String tableIdXPath = "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_TABLES;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		List<Element> tables = (List<Element>) xPath.selectNodes(document);
		return tables;
	}
	
	/**
	 * 获取文件中的所有表格Id
	 * @return 表格Id列表
	 */
	public List<String> getTableIds() {
		List<String> tableIds = new ArrayList<String>();
		
		String tableIdXPath = "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_TABLES 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		List<Element> tables = (List<Element>) xPath.selectNodes(document);
		for (Element table : tables) {
			tableIds.add(table.attributeValue(ATTRIBUTE_ID));
		}
		
		return tableIds;
	}
	
	/**
	 * 通过表格Id表格code节点值
	 * @param tableId 表格Id
	 * @return 表格code节点值
	 */
	public String getTableCode(String tableId) {
		
//		String tableIdXPath = "//o:Table[@Id='o6']//a:Code";
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ tableId 
				+ "']" 
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_CODE;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element code = (Element) xPath.selectSingleNode(document);
		
//		if (code == null || code.getText().trim().isEmpty()) {
//			return null;
//		}
		
		return code.getText();
	}
	
	/**
	 * 通过表格Id表格Name节点值
	 * @param tableId 表格Id
	 * @return 表格Name节点值
	 */
	public String getTableName(String tableId) {
		
//		String tableIdXPath = "//o:Table[@Id='o6']//a:Code";
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ tableId 
				+ "']" 
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_NAME;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element name = (Element) xPath.selectSingleNode(document);
		
		return name.getText();
	}
	
	/**
	 * 通过表格Id表格Comment节点值
	 * @param tableId 表格Id
	 * @return 表格Comment节点值(如果此节点值不存在或者为空,则直接返回null)
	 */
	public String getTableComment(String tableId) {
		
//		String tableIdXPath = "//o:Table[@Id='o6']//a:Code";
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ tableId 
				+ "']" 
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_COMMENT;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element comment = (Element) xPath.selectSingleNode(document);
		
		if (comment == null || comment.getText().isEmpty()) {
			return null;
		}
		
		return comment.getText();
	}
	
	/**
	 * 获取一个表格的所有字段Id
	 * @param tableId 表格Id
	 * @return 表格字段Id列表
	 */
	public List<String> getTableColumnIds(String tableId) {
		List<String> ColumnIds = new ArrayList<String>();
		
//		String tableIdXPath = "//o:Table[@Id='o6']//c:Columns//o:Column";
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ tableId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_COLUMNS 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_COLUMN;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		List<Element> tables = (List<Element>) xPath.selectNodes(document);
		for (Element element : tables) {
//			System.out.println(element.attributeValue(ATTRIBUTE_ID));
			ColumnIds.add(element.attributeValue(ATTRIBUTE_ID));
		}
		
		return ColumnIds;
	}
	
	/**
	 * 通过xml文件中的表格列对象,获取一个表格中的所有字段模型
	 * @param columnList xml文件中的表格列对象
	 * @param elementPrimaryKeys xml文件中的表格主键对象
	 * @return 一个表格中的所有字段模型
	 */
	public List<ColumnModel> getTableColumns(Element columnList, Element elementPrimaryKeys) {
		List<ColumnModel> columnModels = new ArrayList<ColumnModel>();
		
		if (columnList == null) {
			return columnModels;
		}
		
		List<String> primaryKeyIds = new ArrayList<String>();	// 表格主键对象Id
		
		// 如果主键对象为空,则直接返回表格中所有字段模型
		if (elementPrimaryKeys != null) {
			Element elementKey = elementPrimaryKeys.element(ELEMENT_KEY);
			Element elementKeyCloumns = elementKey.element(ELEMENT_KEY_COLUMNS);
			
			if (elementKeyCloumns != null) {
				
				List<Element> elementKeyList = elementKeyCloumns.elements(ELEMENT_COLUMN);
				if (elementKeyList != null) {
					for (Element element : elementKeyList) {
						primaryKeyIds.add(element.attributeValue(ATTRIBUTE_REF));
					}
				}
			}
			
		}
		
		List<Element> columnModelList = columnList.elements();
		for (Element elementColumn : columnModelList) {
			ColumnModel columnModel = new ColumnModel();
			columnModel = getTableColumn(elementColumn, false);	// 非公共列,标识值=false
			String columnModelId = elementColumn.attributeValue(ATTRIBUTE_ID);
			
			// 设置主键标识
			if (primaryKeyIds.contains(columnModelId)) {
				columnModel.setPrimaryKey(true);
			} else {
				columnModel.setPrimaryKey(false);
			}
			
			// 设置外键标识
			if (referenceForeignKeyIds != null && !referenceForeignKeyIds.isEmpty()) {
				if (referenceForeignKeyIds.contains(columnModelId)) {
					// 这样获取主外键关系有个假设前提：主外键Id列表下标一一对应
					// 有可能出现bug
					int index = referenceForeignKeyIds.indexOf(columnModelId);
					String primaryKeyId = referencePrimaryKeyIds.get(index);
					
					columnModel.setParentTableColumnId(primaryKeyId);
				}
			}
			
			columnModels.add(columnModel);
		}
		
		return columnModels;
	}
	
	/**
	 * 获取表格中的所有字段
	 * @param tableId 表格Id
	 * @return 表格中的所有字段列表
	 */
	public List<ColumnModel> getTableColumns(String tableId) {
		List<ColumnModel> columnModels = new ArrayList<ColumnModel>();
		
		List<String> primaryKeys = getPrimaryKeys(tableId);			// 表格主键对象Id
		List<String> tableColumnIds = getTableColumnIds(tableId);	// 表格字段对象Id
//		List<String> referencePrimaryKeyIds = getReferencePrimaryKeyIds();	// 主键方引用字段Id列表
//		List<String> referenceForeignKeyIds = getReferenceForeignKeyIds();	// 外键方引用字段Id列表
		
		
		for (String tableColumnId : tableColumnIds) {
//			logger.debug("start to generate talble id = " + tableId + " , column id = " + tableColumnId);
			// 这里读取的速度比较慢,需要优化
			ColumnModel columnModel = getTableColumn(tableId, tableColumnId);
//			logger.debug("get TableColumn");
			
			// 设置主键标识
			if (primaryKeys == null) {
				columnModel.setPrimaryKey(false);
			} else {
				if (primaryKeys.contains(tableColumnId)) {
					columnModel.setPrimaryKey(true);
				} else {
					columnModel.setPrimaryKey(false);
				}
			}
			
			// 设置外键标识
			if (referenceForeignKeyIds != null && !referenceForeignKeyIds.isEmpty()) {
				if (referenceForeignKeyIds.contains(tableColumnId)) {
					// 这样获取主外键关系有个假设前提：主外键Id列表下标一一对应
					// 有可能出现bug
					int index = referenceForeignKeyIds.indexOf(tableColumnId);
					String primaryKeyId = referencePrimaryKeyIds.get(index);
					
					String parentTableColumnId = getTableColumnCodeById(primaryKeyId);
					columnModel.setParentTableColumnId(parentTableColumnId);
				}
			}
			
//			logger.debug("over to generate talble id = " + tableId + " , column id = " + tableColumnId);
			// 将该字段添加到表格的字段队列中
			columnModels.add(columnModel);
		}
		
		return columnModels;
	}
	
	/**
	 * 通过xml文件中的表格列对象,获取一个表格中的一个字段模型
	 * @param elementColumn xml文件中的表格列对象
	 * @param isDomain 公共列标识(true=公共列;false=非公共列)
	 * @return 一个表格中的一个字段模型
	 */
	public ColumnModel getTableColumn(Element elementColumn, boolean isDomain) {
		ColumnModel columnModel = new ColumnModel();
		
		columnModel.setId(elementColumn.attributeValue(ATTRIBUTE_ID));				// 设置字段模型Id值为文件中的Id值
		columnModel.setColumnName(elementColumn.element(ELEMENT_CODE).getText());	// 设置字段模型Name值为文件中的code值
		columnModel.setColumnDesc(elementColumn.element(ELEMENT_NAME).getText());	// 设置字段模型Desc值为文件中的name值
		columnModel.setDomainColumnModel(isDomain);	// 设置公共列对象标识
		
		// 设置字段模型DataType值为文件中的DataType值
		DataTypeModel dataTypeModel = new DataTypeModel();
		Element elementDatatype = elementColumn.element(ELEMENT_DATATYPE);
		
		// 如果datatype为空值,则直接返回undefined的默认值
		// 此时Length值、Precision值不会有,也返回默认值
		if (elementDatatype == null || elementDatatype.getText().isEmpty()) {
			dataTypeModel.setName(DmConstants.UNDEFINED);
			dataTypeModel.setType(DmConstants.UNDEFINED);
		} else {
			String datatypeCode = elementDatatype.getText().trim();
			DataTypeModel tempDataTypeModel = DatabaseManager.getDataTypeFromPdm(dataTypeModelList, datatypeCode);
			if (tempDataTypeModel == null) {
				dataTypeModel.setName(DmConstants.UNDEFINED);
				dataTypeModel.setType(DmConstants.UNDEFINED);
			} else {
				try {
					dataTypeModel = tempDataTypeModel.clone();
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		// 设置字段模型DataType值为文件中的Length值
		Element elementLength = elementColumn.element(ELEMENT_LENGTH);
		if (elementLength !=null && !elementLength.getText().isEmpty()) {
			dataTypeModel.setLength(Integer.parseInt(elementLength.getText().trim()));
		}
		
		// 设置字段模型DataType值为文件中的Precision值
		Element elementPrecision = elementColumn.element(ELEMENT_PRECISION);
		if (elementPrecision != null && !elementPrecision.getText().isEmpty()) {
			dataTypeModel.setPrecision(Integer.parseInt(elementPrecision.getText().trim()));
		}
		
		// 设置字段模型DataType值
		columnModel.setDataTypeModel(dataTypeModel);
		
		// 设置空值标识
		Element elementMantory = elementColumn.element(ELEMENT_MANDATORY);
		if (elementMantory != null && !elementMantory.getText().isEmpty()) {
			if (elementMantory.getText().equalsIgnoreCase(FLAG_MANDATORY)) {
				columnModel.setCanNotNull(true);
			} else {
				columnModel.setCanNotNull(false);
			}
		} else {
			columnModel.setCanNotNull(false);
		}
		
		// 设置最小值
		Element elementLowValue = elementColumn.element(ELEMENT_LOWVALUE);
		if (elementLowValue != null && !elementLowValue.getText().isEmpty()) {
			columnModel.setMinValue(elementLowValue.getText());
		}
		
		// 设置最大值
		Element elementMaxValue = elementColumn.element(ELEMENT_HIGHVALUE);
		if (elementMaxValue != null && !elementMaxValue.getText().isEmpty()) {
			columnModel.setMinValue(elementMaxValue.getText());
		}
	
		// 设置默认值
		Element elementDefualtValue = elementColumn.element(ELEMENT_DEFAULTVALUE);
		if (elementDefualtValue != null && !elementDefualtValue.getText().isEmpty()) {
			columnModel.setMinValue(elementDefualtValue.getText());
		}
		
		return columnModel;
	}
	
	/**
	 * 获取表格的一个字段
	 * @param tableId 表格Id
	 * @param columnId 字段Id 
	 * @return 表格字段模型
	 */
	public ColumnModel getTableColumn(String tableId, String columnId) {
		ColumnModel columnModel = new ColumnModel();
		
		Element elementColumn = getElementTableColumn(tableId, columnId);
//		logger.debug("element column value = " + elementColumn.asXML());
//		logger.debug("element column attribute = " + elementColumn.element("Name").getText());
		
		columnModel.setColumnName(elementColumn.element(ELEMENT_CODE).getText());	// 设置字段模型Name值为文件中的code值
		columnModel.setColumnDesc(elementColumn.element(ELEMENT_NAME).getText());	// 设置字段模型Desc值为文件中的name值
//		columnModel.setDataTypeModel(getDataTypeModel(elementColumn));		// 设置字段模型DataType值为文件中的DataType值
		
		// 设置字段模型DataType值为文件中的DataType值
		DataTypeModel dataTypeModel = new DataTypeModel();
		Element elementDatatype = elementColumn.element(ELEMENT_DATATYPE);
		
		// 如果datatype为空值,则直接返回undefined的默认值
		// 此时Length值、Precision值不会有,也返回默认值
		if (elementDatatype == null || elementDatatype.getText().isEmpty()) {
			dataTypeModel.setName(DmConstants.UNDEFINED);
			dataTypeModel.setType(DmConstants.UNDEFINED);
		} else {
			String datatypeCode = elementDatatype.getText().trim();
			DataTypeModel tempDataTypeModel = DatabaseManager.getDataTypeFromPdm(dataTypeModelList, datatypeCode);
			if (tempDataTypeModel == null) {
				dataTypeModel.setName(DmConstants.UNDEFINED);
				dataTypeModel.setType(DmConstants.UNDEFINED);
			} else {
				dataTypeModel = tempDataTypeModel;
			}
		}
		columnModel.setDataTypeModel(dataTypeModel);
		
//		columnModel.setColumnName(getTableColumnCode(tableId, columnId));	// 设置字段模型Name值为文件中的code值
//		columnModel.setColumnDesc(getTableColumnName(tableId, columnId));	// 设置字段模型Desc值为文件中的name值
//		columnModel.setDataTypeModel(getDataType(tableId, columnId));		// 设置字段模型DataType值为文件中的DataType值
		
		// 设置空值标识
//		String mandatory = getTableColumnMandatory(tableId, columnId);
//		String mandatory = elementColumn.element(ELEMENT_MANDATORY).getText();
		Element elementMantory = elementColumn.element(ELEMENT_MANDATORY);
		if (elementMantory != null && !elementMantory.getText().isEmpty()) {
			if (elementMantory.getText().equalsIgnoreCase(FLAG_MANDATORY)) {
				columnModel.setCanNotNull(true);
			} else {
				columnModel.setCanNotNull(false);
			}
		} else {
			columnModel.setCanNotNull(false);
		}
		
		// 设置最小值
//		String lowValue = getTableColumnLowValue(tableId, columnId);
//		String lowValue = elementColumn.element(ELEMENT_LOWVALUE).getText();
		Element elementLowValue = elementColumn.element(ELEMENT_LOWVALUE);
		if (elementLowValue != null && !elementLowValue.getText().isEmpty()) {
			columnModel.setMinValue(elementLowValue.getText());
		}
		
		// 设置最大值
//		String maxValue = getTableColumnHighValue(tableId, columnId);
//		String maxValue = elementColumn.element(ELEMENT_HIGHVALUE).getText();
		Element elementMaxValue = elementColumn.element(ELEMENT_HIGHVALUE);
		if (elementMaxValue != null && !elementMaxValue.getText().isEmpty()) {
			columnModel.setMinValue(elementMaxValue.getText());
		}
	
		// 设置默认值
//		String defualtValue = getTableColumnDefaultValue(tableId, columnId);
//		String defualtValue = elementColumn.element(ELEMENT_DEFAULTVALUE).getText();
		Element elementDefualtValue = elementColumn.element(ELEMENT_DEFAULTVALUE);
		if (elementDefualtValue != null && !elementDefualtValue.getText().isEmpty()) {
			columnModel.setMinValue(elementDefualtValue.getText());
		}
		
		return columnModel;
	}
	
	/**
	 * 获取xml文件中一个字段节点值
	 * @param tableId 表格Id
	 * @param columnId 字段Id
	 * @return 字段节点值
	 */
	public Element getElementTableColumn(String tableId, String columnId) {
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ tableId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_COLUMNS 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_COLUMN 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ columnId 
				+ "']";
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element column = (Element) xPath.selectSingleNode(document);
		
		return column;
	}
	
	/**
	 * 通过表格Id、字段Id获取表格字段code节点值
	 * @param tableId 表格Id
	 * @param columnId 字段Id 
	 * @return 表格字段code节点值
	 */
	public String getTableColumnCode(String tableId, String columnId) {
		
//		String tableIdXPath = "//o:Table[@Id='o6']//c:Columns//o:Column[@Id='o9']//a:Code";
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ tableId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_COLUMNS 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_COLUMN 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ columnId 
				+ "']"
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_CODE;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element code = (Element) xPath.selectSingleNode(document);
		
		return code.getText();
	}
	
	/**
	 * 获取表格字段name节点值
	 * @param tableId 表格Id
	 * @param columnId 字段Id 
	 * @return 表格字段name节点值
	 */
	public String getTableColumnName(String tableId, String columnId) {
		
//		String tableIdXPath = "//o:Table[@Id='o6']//c:Columns//o:Column[@Id='o9']//a:Name";
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ tableId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_COLUMNS 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_COLUMN 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ columnId 
				+ "']"
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_NAME;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element name = (Element) xPath.selectSingleNode(document);
		
		return name.getText();
	}
	
	/**
	 * 获取表格字段DataType节点值
	 * @param tableId 表格Id
	 * @param columnId 字段Id 
	 * @return 表格字段DataType节点值
	 */
	public String getTableColumnDataType(String tableId, String columnId) {
		
//		String tableIdXPath = "//o:Table[@Id='o6']//c:Columns//o:Column[@Id='o9']//a:Name";
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ tableId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_COLUMNS 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_COLUMN 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ columnId 
				+ "']"
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_DATATYPE;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element datatype = (Element) xPath.selectSingleNode(document);
		
		if (datatype != null) {
			return datatype.getText();
		} else {
			return null;
		}
		
	}
	
	/**
	 * 获取表格字段Length节点值
	 * @param tableId 表格Id
	 * @param columnId 字段Id 
	 * @return 表格字段Length节点值(如果没有Length节点,则直接返回null)
	 */
	public String getTableColumnLength(String tableId, String columnId) {
		
//		String tableIdXPath = "//o:Table[@Id='o6']//c:Columns//o:Column[@Id='o9']//a:Name";
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ tableId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_COLUMNS 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_COLUMN 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ columnId 
				+ "']"
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_LENGTH;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element length = (Element) xPath.selectSingleNode(document);
		
		if (length != null) {
			return length.getText();
		} else {
			return null;
		}
		
	}
	
	/**
	 * 获取表格字段Precision节点值
	 * @param tableId 表格Id
	 * @param columnId 字段Id 
	 * @return 表格字段Precision节点值(如果没有Precision节点,则直接返回null)
	 */
	public String getTableColumnPrecision(String tableId, String columnId) {
		
//		String tableIdXPath = "//o:Table[@Id='o6']//c:Columns//o:Column[@Id='o9']//a:Name";
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ tableId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_COLUMNS 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_COLUMN 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ columnId 
				+ "']"
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_PRECISION;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element precision = (Element) xPath.selectSingleNode(document);
		
		if (precision != null) {
			return precision.getText();
		} else {
			return null;
		}
	}
	
	/**
	 * 获取表格字段Mandatory节点值
	 * @param tableId 表格Id
	 * @param columnId 字段Id 
	 * @return 表格字段Mandatory节点值(如果没有Mandatory节点,则直接返回null)
	 */
	public String getTableColumnMandatory(String tableId, String columnId) {
		
//		String tableIdXPath = "//o:Table[@Id='o6']//c:Columns//o:Column[@Id='o9']//a:Name";
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ tableId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_COLUMNS 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_COLUMN 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ columnId 
				+ "']"
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_MANDATORY;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element mandatory = (Element) xPath.selectSingleNode(document);
		
		if (mandatory != null) {
			return mandatory.getText();
		} else {
			return null;
		}
	}
	
	/**
	 * 获取表格字段LowValue节点值
	 * @param tableId 表格Id
	 * @param columnId 字段Id 
	 * @return 表格字段LowValue节点值(如果没有LowValue节点,则直接返回null)
	 */
	public String getTableColumnLowValue(String tableId, String columnId) {
		
//		String tableIdXPath = "//o:Table[@Id='o6']//c:Columns//o:Column[@Id='o9']//a:Name";
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ tableId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_COLUMNS 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_COLUMN 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ columnId 
				+ "']"
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_LOWVALUE;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element lowValue = (Element) xPath.selectSingleNode(document);
		
		if (lowValue != null) {
			return lowValue.getText();
		} else {
			return null;
		}
	}
	
	/**
	 * 获取表格字段HighValue节点值
	 * @param tableId 表格Id
	 * @param columnId 字段Id 
	 * @return 表格字段HighValue节点值(如果没有HighValue节点,则直接返回null)
	 */
	public String getTableColumnHighValue(String tableId, String columnId) {
		
//		String tableIdXPath = "//o:Table[@Id='o6']//c:Columns//o:Column[@Id='o9']//a:Name";
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ tableId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_COLUMNS 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_COLUMN 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ columnId 
				+ "']"
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_HIGHVALUE;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element highValue = (Element) xPath.selectSingleNode(document);
		
		if (highValue != null) {
			return highValue.getText();
		} else {
			return null;
		}
	}
	
	/**
	 * 获取表格字段DefaultValue节点值
	 * @param tableId 表格Id
	 * @param columnId 字段Id 
	 * @return 表格字段DefaultValue节点值(如果没有DefaultValue节点,则直接返回null)
	 */
	public String getTableColumnDefaultValue(String tableId, String columnId) {
		
//		String tableIdXPath = "//o:Table[@Id='o6']//c:Columns//o:Column[@Id='o9']//a:Name";
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ tableId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_COLUMNS 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_COLUMN 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ columnId 
				+ "']"
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_DEFAULTVALUE;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element defaultValue = (Element) xPath.selectSingleNode(document);
		
		if (defaultValue != null) {
			return defaultValue.getText();
		} else {
			return null;
		}
	}
	
	/**
	 * 通过字段Id获取表格字段code节点值
	 * @param columnId 字段Id 
	 * @return 表格字段code节点值
	 */
	public String getTableColumnCodeById(String columnId) {
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_COLUMNS 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_COLUMN 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ columnId 
				+ "']"
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_CODE;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element code = (Element) xPath.selectSingleNode(document);
		
		return code.getText();
	}
	
	public DataTypeModel getDataTypeModel(Element elementColumn) {
		DataTypeModel dataTypeModel = new DataTypeModel();
		
//		String datatype = getTableColumnDataType(tableId, columnId);
		Element elementDatatype = elementColumn.element(ELEMENT_DATATYPE);
		
		// 如果datatype为空值,则直接返回undefined的默认值
		// 此时Length值、Precision值不会有,也返回默认值
		if (elementDatatype == null || elementDatatype.getText().isEmpty()) {
			dataTypeModel.setName(DmConstants.UNDEFINED);
			dataTypeModel.setType(DmConstants.UNDEFINED);
			return dataTypeModel;
		}
		String datatype = elementDatatype.getText();
		
		dataTypeModel.setName(datatype);	// 设置数据类型模型Name值为文件中的DataType值
		
		// 设置数据类型模型type值为文件中的DataType值
		if (datatype.contains(LEFT_BRACKET)) {
			dataTypeModel.setType(datatype.substring(0, datatype.indexOf(LEFT_BRACKET)));
		} else {
			dataTypeModel.setType(datatype);
		}
		
		// 设置数据类型模型length值为文件中的length值
//		String length = getTableColumnLength(tableId, columnId);
//		String length = elementColumn.element(ELEMENT_LENGTH).getText();
		Element elementLength = elementColumn.element(ELEMENT_LENGTH);
		if (elementLength !=null && !elementLength.getText().isEmpty()) {
			dataTypeModel.setLength(Integer.parseInt(elementLength.getText().trim()));
		}
		
		// 设置数据类型模型precision值为文件中的precision值
//		String precision = getTableColumnPrecision(tableId, columnId);
//		String precision = elementColumn.element(ELEMENT_PRECISION).getText();
		Element elementPrecision = elementColumn.element(ELEMENT_PRECISION);
		if (elementPrecision != null && !elementPrecision.getText().isEmpty()) {
			dataTypeModel.setPrecision(Integer.parseInt(elementPrecision.getText().trim()));
		}
		
		return dataTypeModel;
	}
	
	/**
	 * @deprecated
	 * 获取表格字段的数据类型
	 * @param tableId 表格Id
	 * @param columnId 字段Id 
	 * @return 表格字段的数据类型
	 */
	public DataTypeModel getDataType(String tableId, String columnId) {
		DataTypeModel dataTypeModel = new DataTypeModel();
		
		String datatype = getTableColumnDataType(tableId, columnId);
		
		// 如果datatype为空值,则直接返回undefined的默认值
		// 此时Length值、Precision值不会有,也返回默认值
		if (datatype == null || datatype.isEmpty()) {
			dataTypeModel.setName(DmConstants.UNDEFINED);
			dataTypeModel.setType(DmConstants.UNDEFINED);
			return dataTypeModel;
		}
		
		dataTypeModel.setName(datatype);	// 设置数据类型模型Name值为文件中的DataType值
		
		// 设置数据类型模型type值为文件中的DataType值
		if (datatype.contains(LEFT_BRACKET)) {
			dataTypeModel.setType(datatype.substring(0, datatype.indexOf(LEFT_BRACKET)));
		} else {
			dataTypeModel.setType(datatype);
		}
		
		// 设置数据类型模型length值为文件中的length值
		String length = getTableColumnLength(tableId, columnId);
		if (length !=null && !length.isEmpty()) {
			dataTypeModel.setLength(Integer.parseInt(length));
		}
		
		// 设置数据类型模型precision值为文件中的precision值
		String precision = getTableColumnPrecision(tableId, columnId);
		if (precision != null && !precision.isEmpty()) {
			dataTypeModel.setPrecision(Integer.parseInt(precision));
		}
		
		return dataTypeModel;
	}
	
	/**
	 * 获取一个表格主键对象Id
	 * @param tableId 表格Id
	 * @return 表格主键对象Id(如果表格没有主键,则返回null)
	 */
	public String getPrimaryKeyId(String tableId) {

//		String tableIdXPath = "//o:Table[@Id='o6']//c:Columns//o:Column";
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ tableId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_PRIMARYKEY 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_KEY;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element primaryKey = (Element) xPath.selectSingleNode(document);
		
		if (primaryKey != null) {
			return primaryKey.attributeValue("Ref");
		} else {
			return null;
		}
	}
	
	public List<String> getPrimaryKeys(String tableId) {
		List<String> primaryKeyIds = new ArrayList<String>();
		
		String primaryKeyId = getPrimaryKeyId(tableId);
		
		if (primaryKeyId == null || primaryKeyId.isEmpty()) {
			return null;
		}
		
//		String tableIdXPath = "//o:Table[@Id='o6']//c:Columns//o:Column[@Id='o9']//a:Code";
		
		String tableIdXPath = "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ tableId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_KEYS 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_KEY 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ primaryKeyId 
				+ "']"
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_KEY_COLUMNS 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_COLUMN;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		List<Element> columns = (List<Element>) xPath.selectNodes(document);
		for (Element column : columns) {
			primaryKeyIds.add(column.attributeValue(ATTRIBUTE_REF));
		}
		
		return primaryKeyIds;
	}
	
	/**
	 * 根据引用节点Id,获取ReferenceJoin对象列表主/外键列的Id
	 * @return ReferenceJoin对象列表的Id列表(如果ReferenceJoin对象列表不存在,则返回null)
	 */
	public List<String> getReferenceJoinIds() {
		List<String> referenceJoinIds = new ArrayList<String>();
		
		String tableIdXPath = "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_JOINS;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		List<Element> joins = (List<Element>) xPath.selectNodes(document);
		
		// 表格引用列表对象,如果是空值,则直接返回null
		if (joins == null || joins.isEmpty()) {
			return null;
		}
		
		// 赋值引用关系主键方引用字段Id列表
		for (Element elementReferenceJoin : joins) {
			List<Element> elementReferenceJoinList = elementReferenceJoin.elements(ELEMENT_REFERENCEJOIN);
			for (Element element : elementReferenceJoinList) {
				if (element.element(ELEMENT_OBJECT1) != null) {
					referencePrimaryKeyIds.add(element.element(ELEMENT_OBJECT1).element(ELEMENT_COLUMN).attributeValue(ATTRIBUTE_REF));
				}
			}
		}
		logger.debug("referencePrimaryKeyIds is : " + referencePrimaryKeyIds);
		
		// 赋值引用关系外键方引用字段Id列表
		for (Element elementReferenceJoin : joins) {
			List<Element> elementReferenceJoinList = elementReferenceJoin.elements(ELEMENT_REFERENCEJOIN);
			for (Element element : elementReferenceJoinList) {
				if (element.element(ELEMENT_OBJECT2) != null) {
					referenceForeignKeyIds.add(element.element(ELEMENT_OBJECT2).element(ELEMENT_COLUMN).attributeValue(ATTRIBUTE_REF));
				}
			}
		}
		logger.debug("referenceForeignKeyIds is : " + referenceForeignKeyIds);
		
		return referenceJoinIds;
	}
	
	/**
	 * 获取pdm文件中所有引用关系连接线对象
	 * @return pdm文件中所有引用关系连接线对象
	 */
	public List<Element> getElementReferences() {
		String tableIdXPath = "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_REFERENCES;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		List<Element> references = (List<Element>) xPath.selectNodes(document);
		
		return references;
	}
	
	/**
	 * 获取引用关系References的Id列表
	 * @return 引用关系References的Id列表(如果列表不存在或者为空,则返回null)
	 */
	public List<String> getReferenceIds() {
		List<String> referenceIds = new ArrayList<String>();

//		String tableIdXPath = "//o:Table[@Id='o6']//c:Columns//o:Column";
		
		String tableIdXPath = "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_REFERENCES 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_REFERENCE;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		List<Element> references = (List<Element>) xPath.selectNodes(document);
		
		// 表格引用列表对象,如果是空值,则直接返回null
		if (references == null || references.isEmpty()) {
			return null;
		}
		
		for (Element element : references) {
			referenceIds.add(element.attributeValue(ATTRIBUTE_ID));
		}
		
		return referenceIds;
	}
	
	/**
	 * 获取引用关系名称(name)的值
	 * @param referenceId 引用对象Id
	 * @return 引用关系名称(name)的值
	 */
	public String getReferenceName(String referenceId) {
//		String tableIdXPath = "//o:Table[@Id='o6']//c:Columns//o:Column";
		
		String tableIdXPath = "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_REFERENCES 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_REFERENCE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ referenceId 
				+ "']" 
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_NAME;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element name = (Element) xPath.selectSingleNode(document);
		
		return name.getText();
	}
	
	/**
	 * 获取引用关系代码(Code)的值
	 * @param referenceId 引用对象Id
	 * @return 引用关系代码(Code)的值
	 */
	public String getReferenceCode(String referenceId) {
//		String tableIdXPath = "//o:Table[@Id='o6']//c:Columns//o:Column";
		
		String tableIdXPath = "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_REFERENCES 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_REFERENCE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ referenceId 
				+ "']" 
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_CODE;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element code = (Element) xPath.selectSingleNode(document);
		
		return code.getText();
	}
	
	/**
	 * 获取引用关系描述(Comment)的值
	 * @param referenceId 引用对象Id
	 * @return 引用关系描述(Comment)的值
	 */
	public String getReferenceComment(String referenceId) {
//		String tableIdXPath = "//o:Table[@Id='o6']//c:Columns//o:Column";
		
		String tableIdXPath = "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_REFERENCES 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_REFERENCE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ referenceId 
				+ "']" 
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_COMMENT;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element comment = (Element) xPath.selectSingleNode(document);
		
		if (comment == null || comment.getText().isEmpty()) {
			return null;
		}
		
		return comment.getText();
	}
	
	/**
	 * 获取引用关系关联关系(Cardinality)的值
	 * @param referenceId 引用对象Id
	 * @return 引用关系关联关系(Cardinality)的值
	 */
	public String getReferenceCardinality(String referenceId) {
//		String tableIdXPath = "//o:Table[@Id='o6']//c:Columns//o:Column";
		
		String tableIdXPath = "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_REFERENCES 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_REFERENCE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ referenceId 
				+ "']" 
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_CARDINALITY;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element cardinality = (Element) xPath.selectSingleNode(document);
		
		return cardinality.getText();
	}
	
	/**
	 * 获取引用关系约束名称(ForeignKeyConstraintName)的值
	 * @param referenceId 引用对象Id
	 * @return 引用关系约束(ForeignKeyConstraintName)的值
	 * (如果不存在此节点值或者值为空,则直接返回null)
	 */
	public String getReferenceForeignKeyConstraintName(String referenceId) {
//		String tableIdXPath = "//o:Table[@Id='o6']//c:Columns//o:Column";
		
		String tableIdXPath = "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_REFERENCES 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_REFERENCE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ referenceId 
				+ "']" 
				+ "//" 
				+ XMLNS_ATTRIBUTE 
				+ ":" 
				+ ELEMENT_FOREIGNKEYCONSTRAINTNAME;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element foreignKeyConstraintName = (Element) xPath.selectSingleNode(document);
		
		// 如果不存在此节点值,则直接返回null
		if (foreignKeyConstraintName == null || foreignKeyConstraintName.getText().isEmpty()) {
			return null;
		}
		
		return foreignKeyConstraintName.getText();
	}
	
	/**
	 * 获取引用关系父表Id(ParentTable/Table[Ref])的值
	 * @param referenceId 引用对象Id
	 * @return 引用关系父表Id(ParentTable/Table[Ref])的值
	 */
	public String getReferenceParentTableId(String referenceId) {
//		String tableIdXPath = "//o:Table[@Id='o6']//c:Columns//o:Column";
		
		String tableIdXPath = "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_REFERENCES 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_REFERENCE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ referenceId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_PARENTTABLE 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element parentTable = (Element) xPath.selectSingleNode(document);
		
		if (parentTable == null || parentTable.attributeValue(ATTRIBUTE_REF).isEmpty()) {
			return null;
		}
		
		return parentTable.attributeValue(ATTRIBUTE_REF);
	}
	
	/**
	 * 获取引用关系子表Id(ChildTable/Table[Ref])的值
	 * @param referenceId 引用对象Id
	 * @return 引用关系子表Id(ChildTable/Table[Ref])的值
	 */
	public String getReferenceChildTableId(String referenceId) {
//		String tableIdXPath = "//o:Table[@Id='o6']//c:Columns//o:Column";
		
		String tableIdXPath = "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_REFERENCES 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_REFERENCE 
				+ "[@" 
				+ ATTRIBUTE_ID 
				+ "='" 
				+ referenceId 
				+ "']" 
				+ "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_CHILDTABLE 
				+ "//" 
				+ XMLNS_OBJECT 
				+ ":" 
				+ ELEMENT_TABLE;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element childTable = (Element) xPath.selectSingleNode(document);
		
		return childTable.attributeValue(ATTRIBUTE_REF);
	}
	
	/**
	 * 将位置字符串值转换为位置对象
	 * @param position 位置字符串值
	 * @return 位置对象
	 */
	public List<Point> getPositionValue(String position) {
		List<Point> points = new ArrayList<Point>();
		
		position = position.substring(1, position.length() - 1);
		position = COMMA + position;
		position = position.replaceAll(DmConstants.BLANK, DmConstants.EMPTY_STRING);
		String[] positions = position.split(STRING_CHANGE + RIGHT_BRACKET);
		for (String positionValue : positions) {
			positionValue = positionValue.substring(2, positionValue.length());
			String[] xyValues = positionValue.split(COMMA);
			
			Point pointPosition = new Point();
//			pointPosition.x = Integer.parseInt(xyValues[0]);
//			pointPosition.y = Integer.parseInt(xyValues[1]);
			pointPosition.x = Integer.parseInt(xyValues[0])/CANVAS_RATE;
			pointPosition.y = Integer.parseInt(xyValues[1])/CANVAS_RATE;
			
			points.add(pointPosition);
		}
		
//		logger.debug("line position is : " + points);
		return points;
	}
	
	/**
	 * 获取domain域所有的PhysicalDomain节点对象
	 * @return domain域所有的PhysicalDomain节点对象列表(如果domain域不存在或者为空,则直接返回null)
	 */
	public List<Element> getElementPhysicalDomains() {
		String tableIdXPath = "//" 
				+ XMLNS_COLLECTION 
				+ ":" 
				+ ELEMENT_DOMAINS;
		
		XPath xPath = document.createXPath(tableIdXPath);
		xPath.setNamespaceURIs(xmlMap);
		Element domains = (Element) xPath.selectSingleNode(document);
		
		if (domains == null || domains.elements().isEmpty()) {
			return null;
		}
		
		return domains.elements();
	}
}
