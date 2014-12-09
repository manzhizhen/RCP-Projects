/* 文件名：     TableModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-5
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import cn.sunline.suncard.powerdesigner.manager.ConfigurationFile;
import cn.sunline.suncard.powerdesigner.model.db.DataTypeModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 表格数据模型 包含了一张表所需要的所有信息
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-9-5
 * @see
 * @since 1.0
 */
public class TableModel implements DataObjectInterface{
	private String id = "";
	
	private String tableName = ""; // 表格在数据库中的名字
	private String tableDesc = ""; // 表格描述，用于在图上显示
	private String tableNote = ""; // 表格备注，用于记录表格其他信息
	private List<ColumnModel> columnList = new ArrayList<ColumnModel>();// 表格中的列信息
	private boolean isAutoSize = true; // 如果用户在界面上手动改变过表格尺寸，则该值为false
	
	private String tableType = ""; // 表格类型（系统配置表(不允许用户进行修改)、用户配置表(允许用户进行修改)、业务数据表(生产数据)、日志表(记录日志，数据可清除)）
	private InitTableDataModel initTableDataModel = new InitTableDataModel(); // 初始化表格数据的sql对象
	private LinkedHashSet<IndexSqlModel> indexSqlModelSet = new LinkedHashSet<IndexSqlModel>(); // 表格索引对象的Set
	
//	private LinkedHashSet<String> moduleIdSet = new LinkedHashSet<String>(); //该表格所属的模块标签
	
	private List<LineModel> lineModelList = new ArrayList<LineModel>(); // 以该表格为起点的连接线模型

	private PhysicalDiagramModel physicalDiagramModel;	// 该表格模型所属的物理图形模型
	
	private static String elementName = "tableModel"; // 保存为document时候的顶节点name
	private Log logger = LogManager.getLogger(TableModel.class
			.getName());
	
	public TableModel() {
		setInitTableDataModel(new InitTableDataModel());
		
		try {
			tableType = ConfigurationFile.getProfileString(SystemConstants
					.DEFAULT_TABLE_TYPE, SystemConstants.TABLE_TYPE_B);
		} catch (IOException e) {
			logger.info("读取系统配置文件出现问题，不过不影响表格的创建。。。");
			e.printStackTrace();
		}
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableDesc() {
		return tableDesc;
	}

	public void setTableDesc(String tableDesc) {
		this.tableDesc = tableDesc;
	}

	public String getTableNote() {
		return tableNote;
	}

	public void setTableNote(String tableNote) {
		this.tableNote = tableNote;
	}

	public List<ColumnModel> getColumnList() {
		return columnList;
	}
	
	public PhysicalDiagramModel getPhysicalDiagramModel() {
		return physicalDiagramModel;
	}

	public void setPhysicalDiagramModel(PhysicalDiagramModel physicalDiagramModel) {
		if(physicalDiagramModel != null) {
			this.physicalDiagramModel = physicalDiagramModel;
		}
	}

//	public void setColumnList(List<ColumnModel> columnList) {
//		this.columnList = columnList;
//	}

	public List<LineModel> getLineModelList() {
		return lineModelList;
	}

	public void setLineModelList(List<LineModel> lineModelList) {
		this.lineModelList = lineModelList;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public InitTableDataModel getInitTableDataModel() {
		return initTableDataModel;
	}

	public LinkedHashSet<IndexSqlModel> getIndexSqlModelSet() {
		return indexSqlModelSet;
	}

	public void setIndexSqlModelSet(LinkedHashSet<IndexSqlModel> indexSqlModelSet) {
		this.indexSqlModelSet = indexSqlModelSet;
		
		if(indexSqlModelSet != null && !indexSqlModelSet.isEmpty()) {
			for(IndexSqlModel indexSqlModel : indexSqlModelSet) {
				indexSqlModel.setTableModel(this);
			}
		}
	}

	public void setInitTableDataModel(InitTableDataModel initTableDataModel) {
		this.initTableDataModel = initTableDataModel;
		if(initTableDataModel != null) {
			initTableDataModel.setTableModel(this);
		}
	}

	public boolean isAutoSize() {
		return isAutoSize;
	}

	public void setAutoSize(boolean isAutoSize) {
		this.isAutoSize = isAutoSize;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public static String getElementName() {
		return elementName;
	}
	
//	public LinkedHashSet<String> getModuleIdSet() {
//		return moduleIdSet;
//	}
//
//	public void setModuleIdSet(LinkedHashSet<String> moduleIdSet) {
//		this.moduleIdSet = moduleIdSet;
//	}

	/**
	 * 备份一张表的List<ColumnModel> columnList
	 * @param List<ColumnModel> TableModel中原来的List<ColumnModel> columnList
	 * @throws CloneNotSupportedException 
	 */
	public List<ColumnModel> backUpTableColumnList() throws CloneNotSupportedException {
		List<ColumnModel> oldColumnList = columnList;
		List<ColumnModel> newColumnList = new ArrayList<ColumnModel>();
		for(ColumnModel columnModel : columnList) {
			ColumnModel cloneColumnModel = columnModel.clone();
			newColumnList.add(cloneColumnModel);
		}
		
		setColumnList(newColumnList);
		
		return oldColumnList;
	}
	
	public void setColumnList(List<ColumnModel> newColumnModelList) {
		if(newColumnModelList == null) {
			logger.error("传入的List<ColumnModel>为空，无法设置TableModel的List<ColumnModel>");
			return ;
		}
		
		for(ColumnModel columnModel : newColumnModelList) {
			columnModel.setTableModel(this);
			if(ColumnModelFactory.getColumnModel(FileModel.getFileModelFromObj(columnModel), 
					columnModel.getId()) == null) {
				ColumnModelFactory.addColumnModel(FileModel.getFileModelFromObj(columnModel), 
						columnModel);
				
			// 如果有该ID，则更新
			} else {
				ColumnModelFactory.updateColumnModel(FileModel.getFileModelFromObj(columnModel), 
						columnModel.getId(), columnModel);
			}
			
		}
		
		this.columnList = newColumnModelList;
	}
	
	/**
	 * 克隆一个TableModel
	 * @param isNewColumnId 为true表示会重新生成ColumnModel的Id，并添加到ColumnModelFactory中，为false表示克隆后ColumnModel的id不变
	 * 注意，当isNewColumnId为false时，新的ColumnModel对象并没有覆盖再ColumnModelFactory中以该Id存在的ColumnModel对象
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public TableModel clone(boolean isNewColumnId) throws CloneNotSupportedException {
		TableModel newTableModel = new TableModel();
		newTableModel.setId(id);
		newTableModel.setTableName(tableName);
		newTableModel.setTableDesc(tableDesc);
		newTableModel.setTableNote(tableNote);
		newTableModel.setTableType(tableType);
		newTableModel.setAutoSize(isAutoSize);
		
		InitTableDataModel newInitTableDataModel = initTableDataModel.clone();
		newInitTableDataModel.setTableModel(newTableModel);
		newTableModel.setInitTableDataModel(newInitTableDataModel);
		
		newTableModel.setTableType(tableType);
		
//		LinkedHashSet<String> newModuleIdSet = new LinkedHashSet<String>();
//		for(String idLabel : moduleIdSet) {
//			newModuleIdSet.add(idLabel);
//		}
//		newTableModel.setModuleIdSet(newModuleIdSet);
		
		newTableModel.setPhysicalDiagramModel(physicalDiagramModel);
		
		// 不能用下面的5行代码，因为这样会在ColumnModelFactory中覆盖原表的列ID对应的列对象。
//		List<ColumnModel> newColumnList = new ArrayList<ColumnModel>();// 表格中的列信息
//		for(ColumnModel columnModel : columnList) {
//			ColumnModel cloneColumnModel = columnModel.clone();
//			cloneColumnModel.setTableModel(newTableModel);
//			newColumnList.add(cloneColumnModel);
//		}
//		newTableModel.setColumnList(newColumnList);
		
		for(ColumnModel columnModel : columnList) {
			ColumnModel cloneColumnModel = columnModel.clone();
			cloneColumnModel.setTableModel(newTableModel);
			
			if(isNewColumnId) {
				ColumnModelFactory.addColumnModel(FileModel.getFileModelFromObj(this), cloneColumnModel);
			}
			
			newTableModel.getColumnList().add(cloneColumnModel);
		}
		
		LinkedHashSet<IndexSqlModel> newIndexSqlModelSet = new LinkedHashSet<IndexSqlModel>();
		for(IndexSqlModel indexSqlModel : indexSqlModelSet) {
			IndexSqlModel newIndexSqlModel = indexSqlModel.clone();
			newIndexSqlModel.setTableModel(newTableModel);
			newIndexSqlModelSet.add(newIndexSqlModel);
		}
		newTableModel.setIndexSqlModelSet(newIndexSqlModelSet);
		
		// 注意：克隆表格暂时没有克隆其连接线信息
		
		return newTableModel;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element tableModelElement = parent.addElement(elementName);
		
		tableModelElement.addAttribute("id", id == null ? "" : id);
		tableModelElement.addAttribute("name", tableName == null ? "" : tableName);
		tableModelElement.addAttribute("desc", tableDesc == null ? "" : tableDesc);
		tableModelElement.addAttribute("autoSize", isAutoSize + "");
		tableModelElement.addAttribute("tableType", tableType == null ? "" : tableType);
		tableModelElement.setText(tableNote == null ? "" : tableNote);
		
		Element columnModelE;
		for(ColumnModel columnModel : columnList) {
			columnModelE = tableModelElement.addElement(ColumnModel.getElementName());
			columnModelE.addAttribute("id", columnModel.getId());
//			columnModel.getElementFromObject(tableModelElement);
		}
		
		for(LineModel lineModel : lineModelList) {
			lineModel.getElementFromObject(tableModelElement);
		}
		
		// 注意，InitTableDataModel和IndexSqlModel需要额外导出
		
		return tableModelElement;
	}

	@Override
	public TableModel getObjectFromElement(Element element, Object...obj) {
		if(element == null ) {
			logger.warn("TableModel的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(obj.length != 1 || (!(obj[0] instanceof FileModel) && !(obj[0] instanceof HashMap))) {
			logger.warn("传入的参数列表为空或不正确，无法将xml转换为TableModel对象！");
			return null;
		}
		
		FileModel fileModel = null;	// 如果是在解析数据库设计文件时，应该传入文件模型
		Map<String, ColumnModel> columnModelMap = null;	// 如果是在解析项目文件时，则应该传入一个列对象的键值Map
		if(obj[0] instanceof FileModel) {
			fileModel = (FileModel) obj[0];
		} else {
			columnModelMap = (Map<String, ColumnModel>) obj[0];
		}
		
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("TableModel的Element为空，无法将xml转换为TableModel对象！");
				return null;
			}
		}
		
		setId(element.attributeValue("id").trim());
		setTableName(element.attributeValue("name").trim());
		setTableDesc(element.attributeValue("desc").trim());
		setAutoSize(new Boolean(element.attributeValue("autoSize").trim()));
		setTableType(element.attributeValue("tableType").trim());
		setTableNote(element.getText());
		
		columnList = new ArrayList<ColumnModel>();
		List<Element> columnElementList = element.elements(ColumnModel.getElementName());
		if(columnElementList != null) {
			for(Element columnModelElement : columnElementList) {
//				ColumnModel columnModel = new ColumnModel().getObjectFromElement(columnModelElement);
				ColumnModel columnModel = null;
				if(fileModel != null) {
					columnModel = ColumnModelFactory.getColumnModel(fileModel, columnModelElement
							.attributeValue("id").trim());
				} else {
					columnModel = columnModelMap.get(columnModelElement
							.attributeValue("id").trim());
				}
						
				if(columnModel != null) {
					columnModel.setTableModel(this);
					columnList.add(columnModel);
				} else {
					logger.error("给表格模型：" + tableName + "匹配列对象失败，在ColumnModelFactory或键值Map找不到对应的列对象ID:" 
							+ columnModelElement.attributeValue("id").trim());
				}
			}
		}
		
		lineModelList = new ArrayList<LineModel>();
		List<Element> lineElementList = element.elements(LineModel.getElementName());
		if(columnElementList != null) {
			for(Element lineModelElement : lineElementList) {
				lineModelList.add(new LineModel().getObjectFromElement(lineModelElement));
			}
		}
		
		// 注意，InitTableDataModel和IndexSqlModel需要在导入时额外设置
		
		return this;
	}
	
	
}
