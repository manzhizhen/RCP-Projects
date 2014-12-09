/* 文件名：     ColumnModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-5
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Element;
import org.hibernate.ejb.criteria.expression.function.LengthFunction;

import cn.sunline.suncard.powerdesigner.model.db.DataTypeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;


/**
 * 表格的列对象
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-9-5
 * @see
 * @since 1.0
 */
public class ColumnModel implements DataObjectInterface{
	private String id = ""; // 在一个文件中唯一标识一个列
	
	private boolean isPrimaryKey; // 是否是主键
	private boolean isCanNotNull; // 是否不能为空，true表示不能为空，false表示能为空
	private String parentTableColumnId = ""; // 该列如果是外键，则对应的父表的主键列的id
	
	// 常规
	private String columnName = ""; // 列的名称
	private String columnDesc = ""; // 列描述
	private String columnNote = ""; // 列备注
	private DataTypeModel dataTypeModel = new DataTypeModel(); // 表格列的数据类型
	private TableModel tableModel = new TableModel(); // 该列所属的TableModel
	private String domainId = ""; // 如果该列引用了公共ColumnModel，则储存该公共ColumnModel的ID；（用isRefDomainColumnModel()来判断是否有引用公共ColumnModel）
	private boolean isDomainColumnModel = false; // 该列是否是公共列对象
	private String systemDefaultValueType = DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_C; // 系统默认值类型，如系统日期、系统时间、登录用户、机构代码和自定义数据等五种类型（DmConstants中有定义）
	private String systemDefaultValue = ""; // 系统默认值，只有系统默认值类型选中自定义数据时，才可以编辑它
	private String initDefaultValue = ""; // 表格初始化默认值，当给一个表格新增一条初始化数据时，该列就会附上该初始化默认值
	
	// 标准检查
	private String minValue = "";	// 最小值
	private String maxValue = "";	// 最大值
	private String defaultValue = "";// 默认值
	private boolean isCanGetMaxValue = false; // 是否能取到边界值的最大值
	private boolean isCanGetMinValue = false; // 是否能取到边界值的最小值
	private boolean isMustNumber = false; // 字符串是否必须是数字字符串
	private int strMinLength = -1; // 字符串值的最小长度，如果为-1，表示没此限制。
	
	// 数据格式
	private String unitDesc = ""; // 自动附加的单位描述，如果该字段isEmpty()，说明不自动附加
	private boolean isAutoChangeChDesc = false; // 是否自动转换中文描述
	private boolean isPassDataSourceChange = false; // 根据数据来源转换，当用户选择了自动转换中文描述是才有可能选它。
	private LinkedHashMap<String, String> defaultChangeMap = new LinkedHashMap<String, String>(); // 当用户不选择根据数据来源转换时，就采用的是默认转换。

	// 数据来源
	private String dataSourceType = DmConstants.NULL_DATA_SOURCE; // 数据源的类型，是表数据源还是自定义数据源，如果没有数据源，需要赋值为DmConstants.NULL_DATA_SOURCE（DmConstants.TABLE_DATA_SOURCE 和 DmConstants.CUSTOM_DATA_SOURCE）
	private boolean isSingleTableSource = true; // 是单表来源还是多表来源
	private String dataSourceContent = ""; // 当用户选择了表数据源中的单表数据源时，该变量保存数据字段对应列对象的ID
	private String dataSourceDescContent = ""; // 当用户选择了表数据源中的单表数据源时，该变量保存数据描述对应列对象的ID
//	private String customDefaultValue = ""; // 用户自定义默认值，最后被customDataMap取代
	private LinkedHashMap<String, String> customDataMap = new LinkedHashMap<String, String>(); // 维护自定义数据的Map，当选择自定义数据源时，该表格可以编辑
	private String limitCondition = ""; // 限制条件
	private String matchDefaultValue = ""; // 默认匹配值
	
	private boolean isImportFromProduct = false; // 标记该ColumnModel是否来自于从产品树导入（只有项目树的表格中才会用到该属性）

	private static String elementName = "columnModel"; // 保存为document时候的顶节点name
	private Log logger = LogManager.getLogger(ColumnModel.class
			.getName());
	
	public ColumnModel() {
	}
	
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnDesc() {
		return columnDesc;
	}

	public void setColumnDesc(String columnDesc) {
		this.columnDesc = columnDesc;
	}

	public DataTypeModel getDataTypeModel() {
		return dataTypeModel;
	}

	public void setDataTypeModel(DataTypeModel dataTypeModel) {
		this.dataTypeModel = dataTypeModel;
	}

	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
		if(isPrimaryKey) {
			setCanNotNull(true);
		}
	}

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}
	
	public boolean isCanGetMaxValue() {
		return isCanGetMaxValue;
	}

	public void setCanGetMaxValue(boolean isCanGetMaxValue) {
		this.isCanGetMaxValue = isCanGetMaxValue;
	}

	public boolean isCanGetMinValue() {
		return isCanGetMinValue;
	}

	public void setCanGetMinValue(boolean isCanGetMinValue) {
		this.isCanGetMinValue = isCanGetMinValue;
	}

	public boolean isMustNumber() {
		return isMustNumber;
	}

	public void setMustNumber(boolean isMustNumber) {
		this.isMustNumber = isMustNumber;
	}

	public int getStrMinLength() {
		return strMinLength;
	}

	public void setStrMinLength(int strMinLength) {
		this.strMinLength = strMinLength;
	}

	public String getDataSourceType() {
		return dataSourceType;
	}

	public boolean isDomainColumnModel() {
		return isDomainColumnModel;
	}

	public void setDomainColumnModel(boolean isDomainColumnModel) {
		this.isDomainColumnModel = isDomainColumnModel;
	}

	public void setDataSourceType(String dataSourceType) {
		this.dataSourceType = dataSourceType;
	}
	
	public String getDataSourceDescContent() {
		return dataSourceDescContent;
	}

	public void setDataSourceDescContent(String dataSourceDescContent) {
		this.dataSourceDescContent = dataSourceDescContent;
	}
	
	public boolean isImportFromProduct() {
		return isImportFromProduct;
	}

	public void setImportFromProduct(boolean isImportFromProduct) {
		this.isImportFromProduct = isImportFromProduct;
	}

	/**
	 * 判断该列是否有引用公共
	 */
	public boolean isRefDomainColumnModel() {
		if(domainId == null || domainId.trim().isEmpty() || domainId.trim().equals(DmConstants.NONE)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断该列是否是外键
	 * @return
	 */
	public boolean isForeignKey() {
		return parentTableColumnId != null && !"".equals(parentTableColumnId.trim());
	}

	/**
	 * 判断该列是否引用了公共ColumnModel
	 * @return
	 */
	public boolean isUsedDomain() {
		return domainId != null && !"".equals(domainId.trim());
	}
	
	public boolean isCanNotNull() {
		return isCanNotNull;
	}

	public void setCanNotNull(boolean isCanNotNull) {
		// 如果是主键，则必须为非空
		if(isPrimaryKey) {
			this.isCanNotNull = true;
			return ;
		}
		
		this.isCanNotNull = isCanNotNull;
	}
	
	public void setTableModel(TableModel tableModel) {
		this.tableModel = tableModel;
	}
	
	public TableModel getTableModel() {
		return tableModel;
	}

	public String getColumnNote() {
		return columnNote;
	}

	public void setColumnNote(String columnNote) {
		this.columnNote = columnNote;
	}
	

	public String getParentTableColumnId() {
		return parentTableColumnId;
	}

	public void setParentTableColumnId(String parentTableColumnId) {
		this.parentTableColumnId = parentTableColumnId;
	}

	public String getMinValue() {
		return minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDataSourceContent() {
		return dataSourceContent;
	}

	public void setDataSourceContent(String dataSourceContent) {
		this.dataSourceContent = dataSourceContent;
	}

//	public String getDataForm() {
//		return dataForm;
//	}
//
//	public void setDataForm(String dataForm) {
//		this.dataForm = dataForm;
//	}
	
	public boolean isSingleTableSource() {
		return isSingleTableSource;
	}

	public void setSingleTableSource(boolean isSingleTableSource) {
		this.isSingleTableSource = isSingleTableSource;
	}

	public LinkedHashMap<String, String> getCustomDataMap() {
		return customDataMap;
	}

	public void setCustomDataMap(LinkedHashMap<String, String> customDataMap) {
		this.customDataMap = customDataMap;
	}

	public String getLimitCondition() {
		return limitCondition;
	}

	public void setLimitCondition(String limitCondition) {
		this.limitCondition = limitCondition;
	}

	public String getMatchDefaultValue() {
		return matchDefaultValue;
	}

	public void setMatchDefaultValue(String matchDefaultValue) {
		this.matchDefaultValue = matchDefaultValue;
	}

	public String getSystemDefaultValueType() {
		return systemDefaultValueType;
	}

	public void setSystemDefaultValueType(String systemDefaultValueType) {
		this.systemDefaultValueType = systemDefaultValueType;
	}

	public String getSystemDefaultValue() {
		return systemDefaultValue;
	}

	public void setSystemDefaultValue(String systemDefaultValue) {
		this.systemDefaultValue = systemDefaultValue;
	}

	public String getInitDefaultValue() {
		return initDefaultValue;
	}

	public void setInitDefaultValue(String initDefaultValue) {
		this.initDefaultValue = initDefaultValue;
	}
	
	

//	public String getCustomDefaultValue() {
//		return customDefaultValue;
//	}
//
//	public void setCustomDefaultValue(String customDefaultValue) {
//		this.customDefaultValue = customDefaultValue;
//	}
	
	public String getUnitDesc() {
		return unitDesc;
	}

	public void setUnitDesc(String unitDesc) {
		this.unitDesc = unitDesc;
	}

	public boolean isAutoChangeChDesc() {
		return isAutoChangeChDesc;
	}

	public void setAutoChangeChDesc(boolean isAutoChangeChDesc) {
		this.isAutoChangeChDesc = isAutoChangeChDesc;
	}

	public boolean isPassDataSourceChange() {
		return isPassDataSourceChange;
	}

	public void setPassDataSourceChange(boolean isPassDataSourceChange) {
		this.isPassDataSourceChange = isPassDataSourceChange;
	}

	public LinkedHashMap<String, String> getDefaultSwitchMap() {
		return defaultChangeMap;
	}

	public void setDefaultSwitchMap(LinkedHashMap<String, String> defaultChangeMap) {
		this.defaultChangeMap = defaultChangeMap;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element columnModelElement = parent.addElement(elementName);

		columnModelElement.addAttribute("id", id == null ? "" : id);
		columnModelElement.addAttribute("name", columnName == null ? "" : columnName);
		columnModelElement.addAttribute("desc", columnDesc == null ? "" : columnDesc);
		columnModelElement.setText(columnNote == null ? "" : columnNote);
		columnModelElement.addAttribute("isPrimaryKey", isPrimaryKey + "");
		columnModelElement.addAttribute("isCanNotNull", isCanNotNull + "");
		columnModelElement.addAttribute("maxValue", maxValue == null ? "" : maxValue);
		columnModelElement.addAttribute("minValue", minValue == null ? "" : minValue);
		columnModelElement.addAttribute("defaultValue", defaultValue == null ? "" : defaultValue);
		columnModelElement.addAttribute("parentTableColumnId", parentTableColumnId == null ? "" : parentTableColumnId);
		
		columnModelElement.addAttribute("domainId", domainId == null ? "" : domainId);
		columnModelElement.addAttribute("isDomainColumnModel", isDomainColumnModel + "");
		
		columnModelElement.addAttribute("dataSourceType", dataSourceType == null ? "" : dataSourceType);
		columnModelElement.addAttribute("isSingleTableSource", isSingleTableSource + "");
		columnModelElement.addAttribute("limitCondition", limitCondition == null ? "" : limitCondition);
		columnModelElement.addAttribute("matchDefaultValue", matchDefaultValue == null ? "" : matchDefaultValue);	
		columnModelElement.addAttribute("systemDefaultValueType", systemDefaultValueType == null ? "" : systemDefaultValueType);		
		columnModelElement.addAttribute("systemDefaultValue", systemDefaultValue == null ? "" : systemDefaultValue);
		columnModelElement.addAttribute("initDefaultValue", initDefaultValue == null ? "" : initDefaultValue);	
		columnModelElement.addAttribute("dataSourceContent", dataSourceContent == null ? "" : dataSourceContent);
		columnModelElement.addAttribute("dataSourceDescContent", dataSourceDescContent == null ? "" : dataSourceDescContent);
//		columnModelElement.addAttribute("customDefaultValue", customDefaultValue == null ? "" : customDefaultValue);
		
//		columnModelElement.addAttribute("dataForm", dataForm == null ? "" : dataForm);
		columnModelElement.addAttribute("unitDesc", unitDesc == null ? "" : unitDesc);
		columnModelElement.addAttribute("isAutoChangeChDesc", isAutoChangeChDesc + "");		
		columnModelElement.addAttribute("isPassDataSourceChange", isPassDataSourceChange + "");
		
		
		columnModelElement.addAttribute("isCanGetMaxValue", isCanGetMaxValue + "");
		columnModelElement.addAttribute("isCanGetMinValue", isCanGetMinValue + "");
		columnModelElement.addAttribute("isMustNumber", isMustNumber + "");
		columnModelElement.addAttribute("strMinLength", strMinLength + "");
		columnModelElement.addAttribute("isImportFromProduct", isImportFromProduct + "");
		
		
		// 自定义数据源数据
		Set<String> keySet = customDataMap.keySet();
		if(keySet != null && !keySet.isEmpty()) {
			for(String key : keySet) {
				Element customDataValueE = columnModelElement.addElement("customDataValue");
				customDataValueE.addAttribute("key", key);
				customDataValueE.setText(customDataMap.get(key) == null ? "" : customDataMap.get(key));
			}
		}
		
		// 默认转换
		keySet = defaultChangeMap.keySet();
		if(keySet != null && !keySet.isEmpty()) {
			for(String key : keySet) {
				Element defaultSwitchValueE = columnModelElement.addElement("defaultSwitchValue");
				defaultSwitchValueE.addAttribute("key", key);
				defaultSwitchValueE.setText(defaultChangeMap.get(key) == null ? "" : defaultChangeMap.get(key));
			}
		}
		
		
		dataTypeModel.getElementFromObject(columnModelElement);
		
		return columnModelElement;
	}

	@Override
	public ColumnModel getObjectFromElement(Element element, Object...obj) {
		if(element == null ) {
			logger.warn("ColumnModel的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("ColumnModel的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		setId(element.attributeValue("id"));
		setColumnName(element.attributeValue("name"));
		setColumnDesc(element.attributeValue("desc"));
		setColumnNote(element.getText());
		
		setDataTypeModel(new DataTypeModel().getObjectFromElement(element.element(DataTypeModel.getElementName())));
		
		setPrimaryKey(new Boolean(element.attributeValue("isPrimaryKey").trim()));
		setCanNotNull(new Boolean(element.attributeValue("isCanNotNull").trim()));
		setMaxValue(element.attributeValue("maxValue").trim());
		setMinValue(element.attributeValue("minValue").trim());
		setDefaultValue(element.attributeValue("defaultValue").trim());
		setParentTableColumnId("".equals(element.attributeValue("parentTableColumnId").trim()) ? 
				null : element.attributeValue("parentTableColumnId").trim());
		
		setDomainId(element.attributeValue("domainId").trim());
		setDomainColumnModel(new Boolean(element.attributeValue("isDomainColumnModel").trim()));
		
		setDataSourceType(element.attributeValue("dataSourceType").trim());
		setDataSourceContent(element.attributeValue("dataSourceContent").trim());
		setDataSourceDescContent(element.attributeValue("dataSourceDescContent").trim());
//		setCustomDefaultValue(element.attributeValue("customDefaultValue").trim());		
		setSingleTableSource(new Boolean(element.attributeValue("isSingleTableSource").trim()));
		setLimitCondition(element.attributeValue("limitCondition").trim());		
		setMatchDefaultValue(element.attributeValue("matchDefaultValue").trim());		
		setSystemDefaultValueType(element.attributeValue("systemDefaultValueType").trim());	
		setSystemDefaultValue(element.attributeValue("systemDefaultValue").trim());	
		setInitDefaultValue(element.attributeValue("initDefaultValue").trim());			
		
//		setDataForm(element.attributeValue("dataForm").trim());
		setUnitDesc(element.attributeValue("unitDesc").trim());
		setAutoChangeChDesc(new Boolean(element.attributeValue("isAutoChangeChDesc").trim()));
		setPassDataSourceChange(new Boolean(element.attributeValue("isPassDataSourceChange").trim()));		
		
		setCanGetMaxValue(new Boolean(element.attributeValue("isCanGetMaxValue").trim()));
		setCanGetMinValue(new Boolean(element.attributeValue("isCanGetMinValue").trim()));
		setMustNumber(new Boolean(element.attributeValue("isMustNumber").trim()));
		setStrMinLength(new Integer(element.attributeValue("strMinLength").trim()));
		setImportFromProduct(new Boolean(element.attributeValue("isImportFromProduct").trim()));
		
		// 自定义数据源数据
		customDataMap.clear();
		List<Element> customDataElementList = element.elements("customDataValue");
		if(customDataElementList != null && !customDataElementList.isEmpty()) {
			for(Element customDataElement : customDataElementList) {
				customDataMap.put(customDataElement.attributeValue("key").trim(), customDataElement.getTextTrim());
			}
		}
		
		// 默认转换
		defaultChangeMap.clear();
		List<Element> defaultSwitchElementList = element.elements("defaultSwitchValue");
		if(defaultSwitchElementList != null && !defaultSwitchElementList.isEmpty()) {
			for(Element defaultSwitchElement : defaultSwitchElementList) {
				defaultChangeMap.put(defaultSwitchElement.attributeValue("key").trim(), defaultSwitchElement.getTextTrim());
			}
		}
		
		return this;
	}
	
	public static String getElementName() {
		return elementName;
	}

	@Override
	public ColumnModel clone() throws CloneNotSupportedException {
		ColumnModel newColumnModel = new ColumnModel();
		newColumnModel.setId(id);
		newColumnModel.setColumnName(columnName);
		newColumnModel.setColumnDesc(columnDesc);
		newColumnModel.setColumnNote(columnNote);
		newColumnModel.setTableModel(tableModel);
		newColumnModel.setPrimaryKey(isPrimaryKey);
		newColumnModel.setCanNotNull(isCanNotNull);
		newColumnModel.setDataTypeModel(dataTypeModel.clone());
		newColumnModel.setMaxValue(maxValue);
		newColumnModel.setMinValue(minValue);
		newColumnModel.setDefaultValue(defaultValue);
		newColumnModel.setParentTableColumnId(parentTableColumnId);
		
		newColumnModel.setDomainId(domainId);
		newColumnModel.setDomainColumnModel(isDomainColumnModel);
		
		newColumnModel.setDataSourceContent(dataSourceContent);
		newColumnModel.setDataSourceType(dataSourceType);
		newColumnModel.setDataSourceDescContent(dataSourceDescContent);
//		newColumnModel.setCustomDefaultValue(customDefaultValue);
		newColumnModel.setSingleTableSource(isSingleTableSource);
		newColumnModel.setLimitCondition(limitCondition);
		newColumnModel.setMatchDefaultValue(matchDefaultValue);
		newColumnModel.setSystemDefaultValueType(systemDefaultValueType);
		newColumnModel.setSystemDefaultValue(systemDefaultValue);
		newColumnModel.setInitDefaultValue(initDefaultValue);
		
		newColumnModel.setCustomDataMap((LinkedHashMap<String, String>) customDataMap.clone());
		
//		newColumnModel.setDataForm(dataForm);
		newColumnModel.setUnitDesc(unitDesc);
		newColumnModel.setAutoChangeChDesc(isAutoChangeChDesc);
		newColumnModel.setPassDataSourceChange(isPassDataSourceChange);
		newColumnModel.setDefaultSwitchMap((LinkedHashMap<String, String>) defaultChangeMap.clone());
		
		newColumnModel.setCanGetMaxValue(isCanGetMaxValue);
		newColumnModel.setCanGetMinValue(isCanGetMinValue);
		newColumnModel.setMustNumber(isMustNumber);
		newColumnModel.setStrMinLength(strMinLength);
		newColumnModel.setImportFromProduct(isImportFromProduct);
		
		return newColumnModel;
	}
	
//	/**
//	 * 使自己能向上遍历到文件模型
//	 * 返回遍历时创建的表格模型
//	 */
//	public TableModel makeUpErgodic(PhysicalDataModel physicalDataModel) {
//		if(physicalDataModel == null) {
//			logger.warn("传入的PhysicalDataModel为空，使ColumnModel向上遍历失败！" + columnName);
//			return null;
//		}
//		
//		PackageModel packageModel = new PackageModel();
//		packageModel.setPhysicalDataModel(physicalDataModel);
//		PhysicalDiagramModel newPhysicalDiagramModel = new PhysicalDiagramModel();
//		newPhysicalDiagramModel.setPackageModel(packageModel);
//		TableModel newTableModel = new TableModel();
//		newTableModel.setPhysicalDiagramModel(newPhysicalDiagramModel);
//		newTableModel.getColumnList().add(this);
//		setTableModel(newTableModel);
//		
//		return newTableModel;
//	}
	
//	@Override
//	public boolean equals(Object obj) {
//		if(obj instanceof ColumnModel) {
//			return id.equals(((ColumnModel)obj).id);
//		}
//		return super.equals(obj);
//	}
	

	public static void main(String[] args) {
	}

}
