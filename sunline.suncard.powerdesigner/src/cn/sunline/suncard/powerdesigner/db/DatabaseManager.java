/* 文件名：     DatabaseManager.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-10
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.db;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.AbstractDocument.Content;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.IndexSqlModel;
import cn.sunline.suncard.powerdesigner.model.LineModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.SqlScriptModel;
import cn.sunline.suncard.powerdesigner.model.StoredProcedureModel;
import cn.sunline.suncard.powerdesigner.model.TableDataModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.db.DataTypeModel;
import cn.sunline.suncard.powerdesigner.model.db.DatabaseTypeModel;
import cn.sunline.suncard.powerdesigner.model.db.KeyWords;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.model.factory.TableModelFactory;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.powerdesigner.ui.dialog.StoredProcedureEditDialog;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 数据库管理类 用于获取数据库类型；进行数据库之间数据类型转换等功能。
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-9-10
 * @see
 * @since 1.0
 */
public class DatabaseManager {

	private static Document databaseDocument = null;
	private static Document dataTypeDocument = null;
	private static Document databaseSQLDocument = null;
	private static Document defaultDocument = null;

	public static Log logger = LogManager
			.getLogger(DatabaseTypeConfigXml.class);

	static {// 静态块，初始化方法，优化代码效率
		createDatabaseDocuemnt();
		createDatabaseSQLDocument();
		createDataTypeDocuemnt();
		createDefaultDocument();
	}

	/**
	 * 初始化数据库类型定义文件的Document
	 * 
	 * @return
	 */
	private static void createDatabaseDocuemnt() {
		if (databaseDocument == null) {
			SAXReader saxReader = new SAXReader();

			try {
				databaseDocument = saxReader.read(new File(
						SystemConstants.DATABASE_TYPE_CONFIG_FILE));

			} catch (DocumentException e) {
				logger.error("初始化Document失败！" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * 初始化数据库DROP SQL文件的Document
	 * 
	 * @return
	 */
	private static void createDatabaseSQLDocument() {
		if (databaseSQLDocument == null) {
			SAXReader saxReader = new SAXReader();

			try {
				databaseSQLDocument = saxReader.read(new File(
						SystemConstants.DATABASE_SQL_CONFIG_FILE));

			} catch (DocumentException e) {
				logger.error("初始化Document失败！" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * 初始化数据库数据类型定义文件的Document
	 * 
	 * @return
	 */
	private static void createDataTypeDocuemnt() {
		if (dataTypeDocument == null) {
			SAXReader saxReader = new SAXReader();

			try {
				dataTypeDocument = saxReader.read(new File(
						SystemConstants.DATA_TYPE_CONFIG_FILE));

			} catch (DocumentException e) {
				logger.error("初始化Document失败！" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * 初始化数据种类返回List<String>的Document
	 * 
	 * @return
	 */
	private static void createDefaultDocument() {
		if (defaultDocument == null) {
			SAXReader saxReader = new SAXReader();

			try {
				defaultDocument = saxReader.read(new File(
						SystemConstants.DATABASE_STANDARDCHECKS_DEFAULT_FILE));

			} catch (DocumentException e) {
				logger.error("初始化Document失败！" + e.getMessage());
				e.printStackTrace();
			}
		}

	}

	/**
	 * 获得所有的数据类型定义
	 * 
	 * @param databaseModel
	 * @return
	 */
	public static List<DataTypeModel> getDataTypeList(
			DatabaseTypeModel databaseModel) {

		if (databaseModel == null) {
			logger.error("传入的数据库模型为空，无法获得其数据类型！");
			return null;
		}

		List<DataTypeModel> allDataTypeList = new ArrayList<DataTypeModel>();

		if (dataTypeDocument == null) {
			logger.error("初始化Document失败！");
			return null;
		}

		Element rootElement = dataTypeDocument.getRootElement();

		if (rootElement == null) {
			logger.error("Document根节点为空！");
			return null;
		}

		List<Element> databaseList = rootElement.elements("database");
		if (databaseList == null) {
			return null;
		}

		// 这里会根据数据库类型来获取所有该数据库支持的数据类型信息
		for (Element databaseElement : databaseList) {
			if (databaseElement.attributeValue("type").equals(
					databaseModel.getType())) {
				if (databaseElement.attributeValue("version").equals(
						databaseModel.getVersion())) {
					List<Element> dataTypeList = databaseElement
							.elements("dataType");
					for (Element dataTypeElement : dataTypeList) {
						if (dataTypeElement.attributeValue("default").equals(
								"0")) {
							DataTypeModel model = new DataTypeModel();
							model.setName(dataTypeElement.getTextTrim());
							model.setLength(Integer.parseInt(dataTypeElement
									.attributeValue("length")));
							model.setPrecision(Integer.parseInt(dataTypeElement
									.attributeValue("precision")));
							model.setType(dataTypeElement
									.attributeValue("name"));

							allDataTypeList.add(model);
						}
					}
				}
			}
		}
		return allDataTypeList;
	}

	/**
	 * 通过pdm文件中DataType来获取对应的数据类型
	 * 
	 * @param dataTypeModelList
	 * @param typeCode
	 * @return
	 */
	public static DataTypeModel getDataTypeFromPdm(
			List<DataTypeModel> dataTypeModelList, String typeCode) {

		if (typeCode == null) {
			logger.error("传入的数据库模型或TypeCode为空，无法获得其数据类型，getDataTypeFromPdm执行失败！");
			return null;
		}

		// List<DataTypeModel> dataTypeModelList =
		// getDataTypeList(databaseModel);
		if (dataTypeModelList == null) {
			logger.error("无法找到数据库下的数据类型，getDataTypeFromPdm执行失败！");
			return null;
		}
		
		typeCode = typeCode.toUpperCase();

		String name = null;
		for (DataTypeModel dataTypeModel : dataTypeModelList) {
			name = dataTypeModel.getName();
			// 两个参数情况
			if (name.contains(",")) {
				if (!typeCode.contains(",")) {
					continue;
				}

				if (typeCode.substring(0, typeCode.indexOf("(")).equals(
						name.substring(0, name.indexOf("(")))) {
					return dataTypeModel;
				} else {
					continue;
				}
			}

			// 没有参数情况
			if (!name.contains("(")) {
				if (typeCode.contains("(")) {
					continue;
				}

				if (typeCode.equals(name)) {
					return dataTypeModel;
				} else {
					continue;
				}
			}

			// 一个参数情况
			if (name.contains("(") && !name.contains(",")) {
				if (!typeCode.contains("(") || typeCode.contains(",")) {
					continue;
				}

				if (typeCode.substring(0, typeCode.indexOf("(")).equals(
						name.substring(0, name.indexOf("(")))) {
					return dataTypeModel;
				} else {
					continue;
				}
			}
		}

		return null;

	}

	/**
	 * 
	 * 获得所有的数据库类型定义
	 * 
	 * @return
	 */
	public static List<DatabaseTypeModel> getDatabaseTypeList() {

		List<DatabaseTypeModel> allDatabaseTypeList = new ArrayList<DatabaseTypeModel>();

		createDatabaseDocuemnt();
		if (databaseDocument == null) {
			logger.error("初始化Document失败！");
			return null;
		}

		Element rootElement = databaseDocument.getRootElement();

		if (rootElement == null) {
			logger.error("Document根节点为空！");
			return null;
		}

		List<Element> databaseList = rootElement.elements("database");
		if (databaseList == null) {
			return null;
		}

		// 这里会根据数据库类型来获取所有该数据库支持的数据类型信息
		for (Element databaseElement : databaseList) {

			DatabaseTypeModel model = new DatabaseTypeModel();
			model.setDatabaseName(databaseElement.getTextTrim());
			model.setType(databaseElement.attributeValue("type"));
			model.setVersion(databaseElement.attributeValue("version"));
			model.setCode(databaseElement.attributeValue("code"));

			allDatabaseTypeList.add(model);
		}

		return allDatabaseTypeList;
	}

	/**
	 * 转换所有的数据类型定义
	 * 
	 * @param oldDataTypeModel
	 * @param newDatabaseTypeModel
	 * @return
	 */
	public static DataTypeModel getSwtichDataTypeModel(
			DataTypeModel oldDataTypeModel,
			DatabaseTypeModel newDatabaseTypeModel) {

		DataTypeModel newDataTypeModel = new DataTypeModel();

		if (dataTypeDocument == null) {
			logger.error("初始化Document失败！");
			return null;
		}

		Element rootElement = dataTypeDocument.getRootElement();

		if (rootElement == null) {
			logger.error("Document根节点为空！");
			return null;
		}

		List<Element> databaseList = rootElement.elements("database");
		if (databaseList == null) {
			return null;
		}

		// 这里会根据type转换数据类型
		for (Element databaseElement : databaseList) {
			if (databaseElement.attributeValue("type").equals(
					newDatabaseTypeModel.getType().trim())
					&& databaseElement.attributeValue("version").equals(
							newDatabaseTypeModel.getVersion().trim())) {
				List<Element> databaseNameList = databaseElement
						.elements("dataType");

				for (Element databaseTypeElement : databaseNameList) {
					if (oldDataTypeModel.getType().equals(
							databaseTypeElement.attributeValue("name"))) {
						newDataTypeModel.setName(databaseTypeElement
								.getTextTrim());
						newDataTypeModel.setType(databaseTypeElement
								.attributeValue("name").trim());
						newDataTypeModel
								.setLength(oldDataTypeModel.getLength());
						newDataTypeModel.setPrecision(oldDataTypeModel
								.getPrecision());
					}
				}
			}
		}
		return newDataTypeModel;
	}

	/**
	 * 得到不同数据库drop语言
	 * 
	 * @param nowDatabaseTypeModel
	 * @return
	 */
	public static String getDropModel(DatabaseTypeModel nowDatabaseTypeModel) {

		String dropSQL = null;

		if (databaseSQLDocument == null) {
			logger.error("初始化Document失败！");
			return null;
		}

		Element rootElement = databaseSQLDocument.getRootElement();

		if (rootElement == null) {
			logger.error("Document根节点为空！");
			return null;
		}

		List<Element> databaseSQLList = rootElement.elements("database");
		if (databaseSQLList == null) {
			return null;
		}

		// 这里会根据type转换数据类型
		for (Element databaseSQLElement : databaseSQLList) {
			if (nowDatabaseTypeModel.getType().equals(
					databaseSQLElement.attributeValue("type").trim())
					&& nowDatabaseTypeModel.getVersion()
							.equals(databaseSQLElement
									.attributeValue("version").trim())) {
				dropSQL = databaseSQLElement.elementText("dropSQL").trim();
			}
		}
		return dropSQL;
	}

	/**
	 * 得到不同数据库表注释语言
	 * 
	 * @param nowDatabaseTypeModel
	 * @return
	 */
	public static String getTableCommentModel(
			DatabaseTypeModel nowDatabaseTypeModel) {

		String tabCommentSQL = null;

		if (databaseSQLDocument == null) {
			logger.error("初始化Document失败！");
			return null;
		}

		Element rootElement = databaseSQLDocument.getRootElement();

		if (rootElement == null) {
			logger.error("Document根节点为空！");
			return null;
		}

		List<Element> databaseSQLList = rootElement.elements("database");
		if (databaseSQLList == null) {
			return null;
		}

		for (Element databaseSQLElement : databaseSQLList) {
			if (nowDatabaseTypeModel.getType().equals(
					databaseSQLElement.attributeValue("type").trim())
					&& nowDatabaseTypeModel.getVersion()
							.equals(databaseSQLElement
									.attributeValue("version").trim())) {
				tabCommentSQL = databaseSQLElement.elementText("tabCommentSQL");
			}
		}
		return tabCommentSQL;
	}

	/**
	 * 得到不同数据库列注释语言
	 * 
	 * @param nowDatabaseTypeModel
	 * @return
	 */
	public static String getColumnCommentModel(
			DatabaseTypeModel nowDatabaseTypeModel) {

		String columnCommentSQL = null;

		if (databaseSQLDocument == null) {
			logger.error("初始化Document失败！");
			return null;
		}

		Element rootElement = databaseSQLDocument.getRootElement();

		if (rootElement == null) {
			logger.error("Document根节点为空！");
			return null;
		}

		List<Element> databaseSQLList = rootElement.elements("database");
		if (databaseSQLList == null) {
			return null;
		}

		for (Element databaseSQLElement : databaseSQLList) {
			if (nowDatabaseTypeModel.getType().equals(
					databaseSQLElement.attributeValue("type").trim())
					&& nowDatabaseTypeModel.getVersion()
							.equals(databaseSQLElement
									.attributeValue("version").trim())) {
				columnCommentSQL = databaseSQLElement.elementText("sqlType");
			}
		}
		return columnCommentSQL;
	}

	/**
	 * 得到不同数据库表间delete约束语言
	 * 
	 * @param nowDatabaseTypeModel
	 * @return
	 */
	public static String getDeleteFKSQLModel(
			DatabaseTypeModel nowDatabaseTypeModel) {

		String columnCommentSQL = null;

		if (databaseSQLDocument == null) {
			logger.error("初始化Document失败！");
			return null;
		}

		Element rootElement = databaseSQLDocument.getRootElement();

		if (rootElement == null) {
			logger.error("Document根节点为空！");
			return null;
		}

		List<Element> databaseSQLList = rootElement.elements("database");
		if (databaseSQLList == null) {
			return null;
		}

		for (Element databaseSQLElement : databaseSQLList) {
			if (nowDatabaseTypeModel.getType().equals(
					databaseSQLElement.attributeValue("type").trim())
					&& nowDatabaseTypeModel.getVersion()
							.equals(databaseSQLElement
									.attributeValue("version").trim())) {
				columnCommentSQL = databaseSQLElement
						.elementText("deleteFKSQL").trim();
				break;
			}
		}
		return columnCommentSQL;
	}

	/**
	 * 得到不同数据库表间add约束语言
	 * 
	 * @param nowDatabaseTypeModel
	 * @return
	 */
	public static String getAddFKSQLModel(DatabaseTypeModel nowDatabaseTypeModel) {

		String columnCommentSQL = null;

		if (databaseSQLDocument == null) {
			logger.error("初始化Document失败！");
			return null;
		}

		Element rootElement = databaseSQLDocument.getRootElement();

		if (rootElement == null) {
			logger.error("Document根节点为空！");
			return null;
		}

		List<Element> databaseSQLList = rootElement.elements("database");
		if (databaseSQLList == null) {
			return null;
		}

		for (Element databaseSQLElement : databaseSQLList) {
			if (nowDatabaseTypeModel.getType().equals(
					databaseSQLElement.attributeValue("type").trim())
					&& nowDatabaseTypeModel.getVersion()
							.equals(databaseSQLElement
									.attributeValue("version").trim())) {
				columnCommentSQL = databaseSQLElement.elementText("addFKSQL");
			}
		}
		return columnCommentSQL;
	}

	/**
	 * 根据数据库种类返回默认List<String>
	 * 
	 * @param nowDatabaseTypeModel
	 * @return
	 */
	public static List<String> getStandardChecksDefaultModel(
			DatabaseTypeModel nowDatabaseTypeModel) {

		List<String> defaultList = new ArrayList<String>();

		if (defaultDocument == null) {
			logger.error("初始化Document失败！");
			return null;
		}

		Element rootElement = defaultDocument.getRootElement();

		if (rootElement == null) {
			logger.error("Document根节点为空！");
			return null;
		}

		List<Element> databaseList = rootElement.elements("database");
		if (databaseList == null) {
			return null;
		}

		// 这里会根据type转换数据类型
		for (Element databaseElement : databaseList) {
			if (databaseElement.attributeValue("type").equals(
					nowDatabaseTypeModel.getType().trim())
					&& databaseElement.attributeValue("version").equals(
							nowDatabaseTypeModel.getVersion().trim())) {
				List<Element> databaseNameList = databaseElement
						.elements("stringList");

				for (Element databaseTypeElement : databaseNameList) {
					defaultList.add(databaseTypeElement.getTextTrim());
				}
			}
		}
		return defaultList;
	}

	/**
	 * 根据TableModel和当前的数据库设置，来返回对应的Sql语句 drop表格，用于生成数据库设置
	 * 
	 */
	public static String getSqlDropTableModel(TableModel tableModel) {
		if (tableModel == null) {
			logger.error("传入的TableModel为空，无法生成SQL！");
			return "";
		}

		DatabaseTypeModel databaseTypeModel = tableModel
				.getPhysicalDiagramModel().getPackageModel().getPhysicalDataModel()
				.getDatabaseTypeModel();

		StringBuffer sqlBuffer = new StringBuffer();

		// Drop表格的语句
		// if (DatabaseGeneration.isTableDrop()) {

		String dropTableTemplate = getDropModel(databaseTypeModel);
		dropTableTemplate = dropTableTemplate == null ? null : dropTableTemplate.trim();
		
		if (dropTableTemplate != null) {
			dropTableTemplate = dropTableTemplate.replaceAll(
					DmConstants.SQLCONFIG_TABLENAME, tableModel.getTableName());// String
																				// 需要重新赋值，不然无法改变，修改是存在副本
			sqlBuffer.append(dropTableTemplate + " " + DmConstants.FILE_WRAP);
		}
		
		return sqlBuffer.toString();
	}

	/**
	 * 根据TableModel和当前的数据库设置，来返回对应的Sql语句 drop外键，用于生成数据库设置
	 * 
	 * @param tableModel
	 * @param dropForeginKey
	 * @return
	 */
	public static String getSqlDropForeignKeyModel(TableModel tableModel) {
		if (tableModel == null) {
			logger.error("传入的TableModel为空，无法生成SQL！");
			return "";
		}

		DatabaseTypeModel databaseTypeModel = tableModel
				.getPhysicalDiagramModel().getPackageModel().getPhysicalDataModel()
				.getDatabaseTypeModel();

		StringBuffer sqlBuffer = new StringBuffer();

		String dropForeginKeyStr = getDeleteFKSQLModel(databaseTypeModel);
		dropForeginKeyStr = dropForeginKeyStr == null ? null : dropForeginKeyStr.trim();

		if (dropForeginKeyStr != null) {
			List<LineModel> lineModelList = tableModel.getLineModelList();
			for (LineModel lineModel : lineModelList) {
				dropForeginKeyStr = dropForeginKeyStr.replaceAll(
						DmConstants.SQLCONFIG_TABLENAME_1,
						tableModel.getTableName());

				dropForeginKeyStr = dropForeginKeyStr.replaceAll(
						DmConstants.SQLCONFIG_CONSTRAINT,
						lineModel.getConstraintName());

				sqlBuffer.append(dropForeginKeyStr + " " + DmConstants.FILE_WRAP);
			}
		}
		
		return sqlBuffer.toString();
	}

	/**
	 * 根据TableModel和当前的数据库设置，来返回对应的Sql语句create外键，用于生成数据库设置
	 * 
	 * @param tableModel
	 * @return
	 */
	public static String getSqlCreateForeignKeyModel(TableModel tableModel) {
		if (tableModel == null) {
			logger.error("传入的TableModel为空，无法生成SQL！");
			return "";
		}

		DatabaseTypeModel databaseTypeModel = tableModel
				.getPhysicalDiagramModel().getPackageModel().getPhysicalDataModel()
				.getDatabaseTypeModel();

		StringBuffer sqlBuffer = new StringBuffer();

		// Create外键
		// if (DatabaseGeneration.isKeyDropPrimary()) {

		// if (createForeginKey) {

		// if(parentTableModel != null){
		String createPrimaryKeyStr = getAddFKSQLModel(databaseTypeModel);
		createPrimaryKeyStr = createPrimaryKeyStr == null ? null : createPrimaryKeyStr.trim();
		
		if (createPrimaryKeyStr != null) {
			
			List<LineModel> lineModelList = tableModel.getLineModelList();
			// alter table @TABLENAME_1@
			// add constraint @CONSTRAINT@ foreign key (@KEY_LIST@)
			// references @TABLENAME_2@ (@KEY_LIST@);
			for (LineModel lineModel : lineModelList) {
				createPrimaryKeyStr = getAddFKSQLModel(databaseTypeModel);
				createPrimaryKeyStr = createPrimaryKeyStr == null ? null : createPrimaryKeyStr.trim();
				
				TableModel parentTableModel = TableModelFactory.getTableModel(
						FileModel.getFileModelFromObj(tableModel),
						lineModel.getParentTableModelId());
				if (parentTableModel != null) {
					createPrimaryKeyStr = createPrimaryKeyStr.replaceAll(
							DmConstants.SQLCONFIG_TABLENAME_1,
							tableModel.getTableName());
					// if(parentTableModel.getId().equals(lineModel.getParentTableModelId())){
					createPrimaryKeyStr = createPrimaryKeyStr.replaceAll(
							DmConstants.SQLCONFIG_CONSTRAINT,
							lineModel.getConstraintName());
					// }
					createPrimaryKeyStr = createPrimaryKeyStr.replaceAll(
							DmConstants.SQLCONFIG_TABLENAME_2,
							parentTableModel.getTableName());
				}
				// List<String> primatyKeyList = getPrimaryKeyList(tableModel);
				// List<String> foreignKeyList = getForeignKeyList(tableModel);
				StringBuffer primatyKeyListStr = new StringBuffer();
				StringBuffer foreignKeyListStr = new StringBuffer();
				// for(String foreignKeyStr : foreignKeyList){
				// for(String primatyKeyStr : primatyKeyList){
				// primatyKeyListStr.append(primatyKeyStr+", ");
				// if(primatyKeyStr.equals(foreignKeyListStr)){
				// foreignKeyListStr.append(foreignKeyStr+", ");
				// }else{
				// foreignKeyListStr.append(", ");
				// }
				// }
				// }
					List<ColumnModel> columnModelList = tableModel.getColumnList();
					for (ColumnModel columnModel : columnModelList) {
						if (columnModel.isForeignKey()) {
							ColumnModel parentModel = ColumnModelFactory
									.getColumnModel(FileModel
											.getFileModelFromObj(columnModel),
											columnModel.getParentTableColumnId());

							if (parentModel != null) {
								if (lineModel.getParentTableModelId().equals(parentModel.getTableModel().getId())
										) {
									foreignKeyListStr.append(columnModel
											.getColumnName() + ", ");
									primatyKeyListStr.append(parentModel
											.getColumnName() + ", ");
								}
							}
						}
					}

					if (primatyKeyListStr.toString().endsWith(", ")) {
						primatyKeyListStr.replace(primatyKeyListStr.length() - 2,
								primatyKeyListStr.length(), "");
					}
					if (foreignKeyListStr.toString().endsWith(", ")) {
						foreignKeyListStr.replace(foreignKeyListStr.length() - 2,
								foreignKeyListStr.length(), "");
					}
					
					if(foreignKeyListStr.toString().trim().isEmpty()||primatyKeyListStr.toString().trim().isEmpty()){
						continue;
					}
					createPrimaryKeyStr = createPrimaryKeyStr.replaceAll(
							DmConstants.SQLCONFIG_KEYLIST_1,
							foreignKeyListStr.toString());
					createPrimaryKeyStr = createPrimaryKeyStr.replaceAll(
							DmConstants.SQLCONFIG_KEYLIST_2,
							primatyKeyListStr.toString());
					// createPrimaryKeyStr =
					// createPrimaryKeyStr.replaceAll(DmConstants.SQLCONFIG_KEYLIST,
					// lineModel);
					System.out.println(createPrimaryKeyStr);
					sqlBuffer.append(createPrimaryKeyStr + " " + DmConstants.FILE_WRAP);
			}
		}
		// }
		// }
		// }
		return sqlBuffer.toString();
	}

	/**
	 * 创建表和主键SQL（生成数据库对话框用到）
	 * 
	 * @param tableModel
	 * @param columnDefault
	 * @param createPrimaryKey
	 * @return
	 */
	public static String getSqlCreateTableAndPrimaryKeyModel(
			TableModel tableModel, Boolean columnDefault,
			Boolean createPrimaryKey, Boolean columnCheck) {
		if (tableModel == null) {
			logger.error("传入的TableModel为空，无法生成SQL！");
			return "";
		}

		StringBuffer sqlBuffer = new StringBuffer();

		// if (DatabaseGeneration.isTableCreate()) {
		sqlBuffer.append("create table " + tableModel.getTableName());

		List<ColumnModel> columnList = tableModel.getColumnList();
		List<String> primaryKeyList = new ArrayList<String>();

		if (!columnList.isEmpty()) {
			sqlBuffer.append(" \r(" + " \r");
			for (ColumnModel columnModel : columnList) {
				if (columnModel.isPrimaryKey()) {
					primaryKeyList.add(columnModel.getColumnName());
				}

				DataTypeModel dataTypeModel = columnModel.getDataTypeModel();

				sqlBuffer.append("             "
						+ String.format("%-40s", columnModel.getColumnName()));

				String typeName = columnModel.getDataTypeModel().getName();
				if (typeName.contains("(")) {
					typeName = typeName.substring(0, typeName.indexOf("("));
				}

				if (dataTypeModel.length >= 0) {
					typeName += "(" + dataTypeModel.length;
					if (dataTypeModel.precision >= 0) {
						typeName += ", " + dataTypeModel.precision + " ),";

					} else {
						typeName += " )";
					}
				}

				// 生成列默认值语句
				if (columnDefault) {
					if (columnModel.getDefaultValue().length() > 0) {
						typeName += "    default "
								+ columnModel.getDefaultValue();
					} else {
						typeName += "    ";
					}
				}

				if (columnModel.isCanNotNull()) {
					typeName += " not null ";
				}

				// 生成列检查语句
				if (columnCheck) {
					if (columnModel.getMinValue().length() > 0
							&& columnModel.getMaxValue().length() < 0) {
						typeName += "\r  constraint CKC_"
								+ columnModel.getColumnName() + "_"
								+ tableModel.getTableName() + " check ( "
								+ columnModel.getColumnName() + " >= "
								+ columnModel.getMinValue() + " )";
					}
					if (columnModel.getMaxValue().length() > 0
							&& columnModel.getMinValue().length() < 0) {
						typeName += "\r  constraint CKC_"
								+ columnModel.getColumnName() + "_"
								+ tableModel.getTableName() + " check ( "
								+ columnModel.getColumnName() + " <= "
								+ columnModel.getMaxValue() + " )";
					}
					if (columnModel.getMaxValue().length() > 0
							&& columnModel.getMinValue().length() > 0) {
						if (columnModel.isCanNotNull()) {
							typeName += "\r  constraint CKC_"
									+ columnModel.getColumnName() + "_"
									+ tableModel.getTableName() + " check ( "
									+ columnModel.getColumnName() + " between "
									+ columnModel.getMinValue() + " and "
									+ columnModel.getMaxValue() + " )";
						} else {
							typeName += "\r  constraint CKC_"
									+ columnModel.getColumnName() + "_"
									+ tableModel.getTableName() + " check ( "
									+ columnModel.getColumnName()
									+ " is null or ( "
									+ columnModel.getColumnName() + " between "
									+ columnModel.getMinValue() + " and "
									+ columnModel.getMaxValue() + " ))";
						}
					}
				}
				typeName += " , \r";

				sqlBuffer.append(typeName);
			}

			// 添加主键
			if (createPrimaryKey) {
				if (!primaryKeyList.isEmpty()) {
					sqlBuffer.append("             " + "constraint PK_"
							+ tableModel.getTableName() + " primary key (");
					for (String key : primaryKeyList) {
						sqlBuffer.append(key + ", ");
					}

					// 去掉末尾的", "
					sqlBuffer.replace(sqlBuffer.length() - 2,
							sqlBuffer.length(), "");
					sqlBuffer.append(") \r");

				}
			}

			sqlBuffer.append("); " + DmConstants.FILE_WRAP);
		} else {
			sqlBuffer.append("; "  + DmConstants.FILE_WRAP);
		}

		// }

		return sqlBuffer.toString();
	}

	/**
	 * 创建表和主键SQL（表格对话框用到）
	 * 
	 * @param tableModel
	 * @return
	 */
	public static String getSqlCreateTableAndPrimaryKeyModel(
			TableModel tableModel) {
		if (tableModel == null) {
			logger.error("传入的TableModel为空，无法生成SQL！");
			return "";
		}

		StringBuffer sqlBuffer = new StringBuffer();

		// if (DatabaseGeneration.isTableCreate()) {
		sqlBuffer.append("create table " + tableModel.getTableName());

		List<ColumnModel> columnList = tableModel.getColumnList();
		List<String> primaryKeyList = new ArrayList<String>();

		if (!columnList.isEmpty()) {
			sqlBuffer.append(" " + DmConstants.FILE_WRAP + "(" + " " + DmConstants.FILE_WRAP);
			for (ColumnModel columnModel : columnList) {
				if (columnModel.isPrimaryKey()) {
					primaryKeyList.add(columnModel.getColumnName());
				}

				DataTypeModel dataTypeModel = columnModel.getDataTypeModel();

				sqlBuffer.append("             "
						+ String.format("%-40s", columnModel.getColumnName()));

				String typeName = columnModel.getDataTypeModel().getName();
				if (typeName.contains("(")) {
					typeName = typeName.substring(0, typeName.indexOf("("));
				}

				if (dataTypeModel.length >= 0) {
					typeName += "(" + dataTypeModel.length;
					if (dataTypeModel.precision >= 0) {
						typeName += ", " + dataTypeModel.precision + "),";

					} else {
						typeName += "), " + DmConstants.FILE_WRAP;
					}
				} else {
					typeName += ", " + DmConstants.FILE_WRAP;
				}
				sqlBuffer.append(typeName);
			}

			// 添加主键
			if (!primaryKeyList.isEmpty()) {
				sqlBuffer.append("             " + "constraint PK_"
						+ tableModel.getTableName() + " primary key (");
				for (String key : primaryKeyList) {
					sqlBuffer.append(key + ", ");
				}

				// 去掉末尾的", "
				sqlBuffer.replace(sqlBuffer.length() - 2, sqlBuffer.length(),
						"");
				sqlBuffer.append(") " + DmConstants.FILE_WRAP);

			}
		}
		sqlBuffer.append("); " + DmConstants.FILE_WRAP);

		// }

		return sqlBuffer.toString();
	}

	/**
	 * 创建注释
	 * 
	 * @param tableModel
	 * @return
	 */
	public static String getSqlCommentTableModel(TableModel tableModel) {
		if (tableModel == null) {
			logger.error("传入的TableModel为空，无法生成SQL！");
			return "";
		}

		StringBuffer sqlBuffer = new StringBuffer();

		DatabaseTypeModel databaseTypeModel = tableModel
				.getPhysicalDiagramModel().getPackageModel().getPhysicalDataModel()
				.getDatabaseTypeModel();
		// CommentTable的语句
		// if (DatabaseGeneration.isTableComment()) {
		String commentTemplate = getTableCommentModel(databaseTypeModel);
		commentTemplate = commentTemplate == null ? null : commentTemplate.trim();
		// System.out.println("转换前：" + commentTemplate);
		if (commentTemplate != null) {
			commentTemplate = commentTemplate.replaceAll(
					DmConstants.SQLCONFIG_TABLENAME, tableModel.getTableName());
			commentTemplate = commentTemplate.replaceAll(
					DmConstants.SQLCONFIG_TABLECOMMENT,
					tableModel.getTableDesc());
			// System.out.println("转换后" + commentTemplate);
			sqlBuffer.append(commentTemplate + " " + DmConstants.FILE_WRAP);
		}
		// }
		return sqlBuffer.toString();
	}

	/**
	 * 得到初始化SQL语句的方法
	 * 
	 * @param tableModel
	 * @return
	 */
	public static String getSqlInitializeSQLModel(TableModel tableModel) {
		if (tableModel == null) {
			logger.error("传入的TableModel为空，无法生成SQL！");
			return "";
		}

		StringBuffer sqlBuffer = new StringBuffer();

		List<TableDataModel> initializeSQLList = tableModel
				.getInitTableDataModel().getInitDataList();
		for (TableDataModel initializeSQL : initializeSQLList) {
			System.out.println(initializeSQL.getDataMap().values());
			sqlBuffer.append(initializeSQL.getDataMap().values() + " \r");
		}
		return sqlBuffer.toString();
	}

	/**
	 * 得到物理模型SQL语句的方法
	 * 
	 * @param tableModel
	 * @return
	 */
	public static String getProductModelSql(ProductModel productModel) {
		if (productModel == null) {
			logger.error("传入的ProductModel为空，无法生成SQL！");
			return "";
		}

		StringBuffer sqlBuffer = new StringBuffer();

		Set<SqlScriptModel> physicalModelSQLList = productModel.getSqlSet();
		for (SqlScriptModel physicalModelSQL : physicalModelSQLList) {
			sqlBuffer.append(physicalModelSQL.getRealSql() + " " + DmConstants.FILE_WRAP);
		}
		
		return sqlBuffer.toString();
	}

	/**
	 * 得到物理模型存储过程SQL语句的方法
	 * 
	 * @param tableModel
	 * @return
	 */
	public static String getStoredProcedureFromProduct(ProductModel productModel) {
		if (productModel == null) {
			logger.error("传入的ProductModel为空，无法生成SQL！");
			return "";
		}

		StringBuffer sqlBuffer = new StringBuffer();

		Set<StoredProcedureModel> storedProcedureList = productModel.getStoredProcedureSet();
		for (StoredProcedureModel storedProcedure : storedProcedureList) {
			sqlBuffer.append(storedProcedure.getRealStoredProcedure() + " " + DmConstants.FILE_WRAP);
		}
		return sqlBuffer.toString();
	}

	/**
	 * 得到索引SQL语句的方法
	 * 
	 * @param tableModel
	 * @return
	 */
	public static String getIndexSql(LinkedHashSet<IndexSqlModel> indexSqlModelSet) {
		if (indexSqlModelSet == null) {
			logger.error("传入的indexSqlModel为空，无法生成SQL！");
			return "";
		}

//		StringBuffer sqlBuffer = new StringBuffer();
//
//		List<ColumnModel> indexSqlModelList = indexSqlModel.getColumnList();
//		for (ColumnModel indexSql : indexSqlModelList) {
//			sqlBuffer.append(indexSql + " " + DmConstants.FILE_WRAP);
//		}
		StringBuffer allIndexSql = new StringBuffer();
		
		for(IndexSqlModel indexSqlModel : indexSqlModelSet){
			String indexSql = "CREATE INDEX @INDEX_NAME@ ON @TABLE_NAME@ (@COLUMN_LIST@);";

			StringBuffer columnIndex = new StringBuffer();

			for (ColumnModel selectedColumn : indexSqlModel.getColumnList()) {
				columnIndex.append(selectedColumn.getColumnName() + ", ");
			}
			columnIndex.deleteCharAt(columnIndex.lastIndexOf(","));
			
			indexSql = indexSql.replaceAll("@INDEX_NAME@", indexSqlModel.getName());
			indexSql = indexSql.replaceAll("@TABLE_NAME@",
					indexSqlModel.getTableModel().getTableName());
			indexSql = indexSql.replaceAll("@COLUMN_LIST@", columnIndex.toString());
			
			allIndexSql.append(indexSql + " " + DmConstants.FILE_WRAP);
		}
		
		return allIndexSql.toString();
	}


	/**
	 * 通过传入的tablemodel得到父表，用于创造外键约束所用到
	 * 
	 * @param tableModel
	 * @return
	 */
	private static TableModel getParentTable(TableModel tableModel) {
		TableModel parentTable = null;
		// 外键的列的集合
		
		List<ColumnModel> foreignKeyColumnList = new ArrayList<ColumnModel>();
		List<ColumnModel> columnModelList = tableModel.getColumnList();
		for (ColumnModel columnModel : columnModelList) {
			if (columnModel.isForeignKey()) {
				foreignKeyColumnList.add(columnModel);
			}
		}
		if (foreignKeyColumnList.size() == 0
				|| foreignKeyColumnList.get(0).getParentTableColumnId() == null) {
			return parentTable;
		} else {
			parentTable = ColumnModelFactory.getColumnModel(
					FileModel.getFileModelFromObj(foreignKeyColumnList.get(0)),
					foreignKeyColumnList.get(0).getParentTableColumnId())
					.getTableModel();
		}
		return parentTable;
	}

	/**
	 * 通过传入的tablemodel得到主键List，用于生成外键约束所用到
	 * 
	 * @param tableModel
	 * @return
	 */
	private static List<String> getPrimaryKeyList(TableModel tableModel) {
		List<String> primaryKeyList = new ArrayList<String>();

		List<ColumnModel> columnModelList = tableModel.getColumnList();
		for (ColumnModel columnModel : columnModelList) {
			if (columnModel.isPrimaryKey()) {
				primaryKeyList.add(columnModel.getColumnName());
			}
		}
		return primaryKeyList;
	}

	/**
	 * 通过传入的tablemodel得到外键list，用于生成外键约束所用到
	 * 
	 * @param tableModel
	 * @return
	 */
	private static List<String> getForeignKeyList(TableModel tableModel) {
		List<String> foreignKeyList = new ArrayList<String>();

		List<ColumnModel> columnModelList = tableModel.getColumnList();
		for (ColumnModel columnModel : columnModelList) {
			if (columnModel.isForeignKey()) {
				foreignKeyList.add(columnModel.getColumnName());
			}
		}

		return foreignKeyList;
	}

	/**
	 * 通过databaseCode节点值匹配对应的数据库类型 此方法专门为pdm读取数据库类型时使用,以获取跟系统定义的数据库类型匹配
	 * 
	 * @param databaseCode
	 *            databaseCode节点值
	 * @return 与databaseCode节点值匹配对应的数据库类型(如果找不到相匹配的刷空间类型,则直接返回null)
	 */
	public static DatabaseTypeModel getDatabaseTypeModelBydatabaseCode(
			String databaseCode) {
		List<DatabaseTypeModel> databaseTypeModels = getDatabaseTypeList();
		for (DatabaseTypeModel databaseTypeModel : databaseTypeModels) {
			if (databaseCode.equalsIgnoreCase(databaseTypeModel.getCode())) {
				return databaseTypeModel;
			}
		}
		logger.error("can not find any DatabaseTypeModel match with databaseCode : "
				+ databaseCode);
		return null;
	}

	/**
	 * 字体变色
	 */
	public StyleRange[] changeColor(StyledText text) {
		// TODO Auto-generated method stub
		KeyWordsManager keyWordsManager = new KeyWordsManager();
		List<KeyWords> kewWordsList = keyWordsManager.getKeyWordsList(text
				.getText());
		int keyWordsCount = kewWordsList.size();
		StyleRange[] ranges = new StyleRange[keyWordsCount];
		int i = 0;
		for (KeyWords keywords : kewWordsList) {
			// ranges[i] = new StyleRange(keywords.getKeyWordsStart(), keywords
			// .getKeyWords().length(), getShell().getDisplay()
			// .getSystemColor(SWT.COLOR_BLUE), null);
			ranges[i] = new StyleRange(keywords.getKeyWordsStart(), keywords
					.getKeyWords().length(), keywords.getKeyWordsColor(), null);
			i++;
		}
		return ranges;
	}
}
