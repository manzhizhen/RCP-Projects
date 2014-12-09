/* 文件名：     PhysicalDataModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-5
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;

import cn.sunline.suncard.powerdesigner.model.db.DatabaseTypeModel;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 物理数据模型 一个物理数据模型可以包含多个包模型（PackageModel）
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-9-5
 * @see
 * @since 1.0
 */
public class PhysicalDataModel implements DataObjectInterface {
	private String id = ""; // 物理数据模型ID（在树节点中作为ID的一部分，同一个文件中该名称必须唯一）
	private String name = ""; // 物理数据模型的名称
	private String note = ""; // 物理数据模型的备注
	private LinkedHashSet<PackageModel> packageModelSet = new LinkedHashSet<PackageModel>();
	private DatabaseTypeModel databaseTypeModel = new DatabaseTypeModel(); // 当前物理数据模型使用的数据库

	private List<ColumnModel> domainList = new ArrayList<ColumnModel>(); // domain的List，用于储存在该物理数据模型中用到的公共ColumModel
	private List<ColumnModel> defaultColumnList = new ArrayList<ColumnModel>(); // 储存默认的ColumnModel的List
	private List<DictionaryModel> dictonaryModelList = new ArrayList<DictionaryModel>(); // 储存业务字典的List

	private FileModel fileModel; // 所属的文件模型

	private static String elementName = "physicalDataModel"; // 保存为document时候的顶节点name
	private Log logger = LogManager.getLogger(ColumnModel.class.getName());

	public Set<PackageModel> getPackageModelSet() {
		return packageModelSet;
	}

	public DatabaseTypeModel getDatabaseTypeModel() {
		return databaseTypeModel;
	}

	public void setDatabaseTypeModel(DatabaseTypeModel databaseTypeModel) {
		this.databaseTypeModel = databaseTypeModel;
	}

	public String getId() {
		return id;
	}

	public void setId(String name) {
		this.id = name;
	}

	public FileModel getFileModel() {
		return fileModel;
	}

	public void setFileModel(FileModel fileModel) {
		this.fileModel = fileModel;
	}

	public static String getElementName() {
		return elementName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ColumnModel> getDomainList() {
		return domainList;
	}

	public void setDomainList(List<ColumnModel> domainList) {
		this.domainList = domainList;
	}

	public List<ColumnModel> getDefaultColumnList() {
		return defaultColumnList;
	}

	public void setDefaultColumnList(List<ColumnModel> defaultColumnList) {
		this.defaultColumnList = defaultColumnList;
	}

	public List<DictionaryModel> getDictonaryModelList() {
		return dictonaryModelList;
	}

	public void setDictonaryModelList(List<DictionaryModel> dictonaryModelList) {
		this.dictonaryModelList = dictonaryModelList;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * 获得该物理数据模型下面的所有TableModel
	 * 
	 * @return
	 */
	public Set<TableModel> getAllTables() {
		Set<TableModel> tableModels = new HashSet<TableModel>();

		Set<PhysicalDiagramModel> physicalDiagramModelSet = getAllPhysicalDiagramModels();
		for (PhysicalDiagramModel physicalDiagramModel : physicalDiagramModelSet) {
			tableModels.addAll(physicalDiagramModel.getTableMap().values());
		}

		return tableModels;
	}
	
	/**
	 * 获得该物理数据模型下面的所有TableShortcutModel
	 * 
	 * @return
	 */
	public Set<TableShortcutModel> getAllTableShortcuts() {
		Set<TableShortcutModel> tableShortcutModels = new HashSet<TableShortcutModel>();

		Set<PhysicalDiagramModel> physicalDiagramModelSet = getAllPhysicalDiagramModels();
		for (PhysicalDiagramModel physicalDiagramModel : physicalDiagramModelSet) {
			tableShortcutModels.addAll(physicalDiagramModel.getTableShortcutMap().values());
		}

		return tableShortcutModels;
	}

	/**
	 * 获得该物理数据模型下面的所有的物理图形模型
	 * 
	 * @return
	 */
	public Set<PhysicalDiagramModel> getAllPhysicalDiagramModels() {
		Set<PhysicalDiagramModel> physicalDiagramModels = new HashSet<PhysicalDiagramModel>();

		Set<PackageModel> packageModels = getPackageModelSet();
		for (PackageModel packageModel : packageModels) {
			physicalDiagramModels.addAll(packageModel
					.getPhysicalDiagramModelSet());
		}

		return physicalDiagramModels;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element physicalDataModelElement = parent.addElement(elementName);

		physicalDataModelElement.addAttribute("id", id == null ? "" : id);
		physicalDataModelElement.addAttribute("name", name == null ? "" : name);
		physicalDataModelElement.addAttribute("note", note == null ? "" : note);
		databaseTypeModel.getElementFromObject(physicalDataModelElement);

		Element packageModelsE = physicalDataModelElement
				.addElement("packageModels");
		for (PackageModel packageModel : packageModelSet) {
			packageModel.getElementFromObject(packageModelsE);
		}

		Element domainListE = physicalDataModelElement.addElement("domainList");
		for (ColumnModel columnModel : domainList) {
			columnModel.getElementFromObject(domainListE);
		}

		Element defaultColumnListE = physicalDataModelElement
				.addElement("defaultColumnList");
		for (ColumnModel columnModel : defaultColumnList) {
			columnModel.getElementFromObject(defaultColumnListE);
		}

		return physicalDataModelElement;
	}

	@Override
	public PhysicalDataModel getObjectFromElement(Element element, Object...obj) {
		if (element == null) {
			logger.warn("PhysicalDataModel的Element为空，无法将xml转换为对象！");
			return null;
		}

		if (!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if (element == null) {
				logger.warn("PhysicalDataModel的Element为空，无法将xml转换为对象！");
				return null;
			}
		}

		setId(element.attributeValue("id").trim());
		setName(element.attributeValue("name").trim());
		setNote(element.attributeValue("note").trim());
		databaseTypeModel.getObjectFromElement(element.element(DatabaseTypeModel.getElementName()));

		packageModelSet = new LinkedHashSet<PackageModel>();
		List<Element> elementList = element.element("packageModels").elements();
		if (elementList != null) {
			for (Element packageModelElement : elementList) {
				PackageModel packageModel = new PackageModel();
				packageModel.setPhysicalDataModel(this);
				packageModelSet.add(packageModel
						.getObjectFromElement(packageModelElement));
			}
		}

		domainList.clear();
		elementList = element.element("domainList").elements();
		if (elementList != null) {
			for (Element domainElement : elementList) {
				ColumnModel columnModel = new ColumnModel();
				columnModel.getObjectFromElement(domainElement);
				domainList.add(columnModel);
			}
		}

		defaultColumnList.clear();
		elementList = element.element("defaultColumnList").elements();
		if (elementList != null) {
			for (Element defaultColumnElement : elementList) {
				ColumnModel columnModel = new ColumnModel();
				columnModel.getObjectFromElement(defaultColumnElement);
				defaultColumnList.add(columnModel);
			}
		}

		return this;
	}

}
