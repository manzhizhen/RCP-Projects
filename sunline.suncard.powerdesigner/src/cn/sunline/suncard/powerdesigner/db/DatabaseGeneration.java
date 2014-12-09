/* 文件名：     DatabaseGeneration.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-10-17
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.db;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.db.DatabaseTypeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 数据库脚本生成相关类
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-10-17
 * @see
 * @since 1.0
 */
public class DatabaseGeneration {
	private DatabaseTypeModel databaseTypeModel;
	
	private static Document optionItemDocument = null;

	private static boolean dbPhysical;
	private static boolean dbStartScript;
	private static boolean dbEndScript;

	private static boolean tableCreate = true;
	private static boolean tableDrop = true;
	private static boolean tableComment = true;

	private static boolean columnDefault;
	private static boolean columnCheck;

	private static boolean keyCreatePrimary = true;
	private static boolean keyDropPrimary = true;
	private static boolean keyCreateForegin = true;
	private static boolean keyDropForegin = true;
	private static boolean keyComment;
	
	//新增属性
	private static boolean initializeSQL = true;
	private static boolean physicalModelSQL = true;
	private static boolean storedProcedure = true;
	
	//新增索引
	private static boolean indexSQL = true;

	public static boolean isIndexSQL() {
		return indexSQL;
	}

	public static void setIndexSQL(boolean indexSQL) {
		DatabaseGeneration.indexSQL = indexSQL;
	}

	private static Log logger = LogManager.getLogger(DatabaseGeneration.class
			.getName());
	
	/**
	 * 生成创建的脚本
	 * @param tableModelList
	 * @return
	 */
	public String getDatabaseScript(List<TableModel> tableModelList) {
		StringBuffer sqlBuffer = new StringBuffer();
		if(databaseTypeModel == null || tableModelList == null) {
			logger.error("数据库类型或TableModel的List的为空，无法生成脚本！");
			return sqlBuffer.toString();
		}
		
		if(tableDrop) {
			
		}
		
		
		
		return sqlBuffer.toString();
	}
	
	public String getTableDropStr(String tableName) {
		StringBuffer sqlBuffer = new StringBuffer();
		if(databaseTypeModel == null || tableName == null) {
			logger.error("数据库类型或表格名称为空，无法生成表格的Drop脚本！");
			return sqlBuffer.toString();
		}
		
		if("ORACLE".equalsIgnoreCase(databaseTypeModel.getType())) {
			sqlBuffer.append("drop table ");
			sqlBuffer.append(tableName);
			
		}
		
		return sqlBuffer.toString();
	}
	public static boolean isInitializeSQL() {
		return initializeSQL;
	}

	public static void setInitializeSQL(boolean initializeSQL) {
		DatabaseGeneration.initializeSQL = initializeSQL;
	}

	public static boolean isPhysicalModelSQL() {
		return physicalModelSQL;
	}

	public static void setPhysicalModelSQL(boolean physicalModelSQL) {
		DatabaseGeneration.physicalModelSQL = physicalModelSQL;
	}

	public static boolean isStoredProcedure() {
		return storedProcedure;
	}

	public static void setStoredProcedure(boolean storedProcedure) {
		DatabaseGeneration.storedProcedure = storedProcedure;
	}
	

	public static boolean isDbPhysical() {
		return dbPhysical;
	}

	public static void setDbPhysical(boolean dbPhysical) {
		DatabaseGeneration.dbPhysical = dbPhysical;
	}

	public static boolean isDbStartScript() {
		return dbStartScript;
	}

	public static void setDbStartScript(boolean dbStartScript) {
		DatabaseGeneration.dbStartScript = dbStartScript;
	}

	public static boolean isDbEndScript() {
		return dbEndScript;
	}

	public static void setDbEndScript(boolean dbEndScript) {
		DatabaseGeneration.dbEndScript = dbEndScript;
	}

	public static boolean isTableCreate() {
		return tableCreate;
	}

	public static void setTableCreate(boolean tableCreate) {
		DatabaseGeneration.tableCreate = tableCreate;
	}

	public static boolean isTableDrop() {
		return tableDrop;
	}

	public static void setTableDrop(boolean tableDrop) {
		DatabaseGeneration.tableDrop = tableDrop;
	}

	public static boolean isTableComment() {
		return tableComment;
	}

	public static void setTableComment(boolean tableComment) {
		DatabaseGeneration.tableComment = tableComment;
	}

	public static boolean isColumnDefault() {
		return columnDefault;
	}

	public static void setColumnDefault(boolean columnDefault) {
		DatabaseGeneration.columnDefault = columnDefault;
	}

	public static boolean isColumnCheck() {
		return columnCheck;
	}

	public static void setColumnCheck(boolean columnCheck) {
		DatabaseGeneration.columnCheck = columnCheck;
	}

	public static boolean isKeyCreatePrimary() {
		return keyCreatePrimary;
	}

	public static void setKeyCreatePrimary(boolean keyCreatePrimary) {
		DatabaseGeneration.keyCreatePrimary = keyCreatePrimary;
	}

	public static boolean isKeyDropPrimary() {
		return keyDropPrimary;
	}

	public static void setKeyDropPrimary(boolean keyDropPrimary) {
		DatabaseGeneration.keyDropPrimary = keyDropPrimary;
	}

	public static boolean isKeyComment() {
		return keyComment;
	}

	public static void setKeyComment(boolean keyComment) {
		DatabaseGeneration.keyComment = keyComment;
	}

	public static boolean isKeyCreateForegin() {
		return keyCreateForegin;
	}

	public static void setKeyCreateForegin(boolean keyCreateForegin) {
		DatabaseGeneration.keyCreateForegin = keyCreateForegin;
	}

	public static boolean isKeyDropForegin() {
		return keyDropForegin;
	}

	public static void setKeyDropForegin(boolean keyDropForegin) {
		DatabaseGeneration.keyDropForegin = keyDropForegin;
	}

	/**
	 * 调用本地的OptionItemXML文件，初始化数据！
	 */
	public static void init() {
		// TODO Auto-generated method stub
		Document doc = createOptionItemDocument();
		if (doc == null) {
			logger.error("初始化Document失败！");
		}

		Element rootElement = doc.getRootElement();

		if (rootElement == null) {
			logger.error("Document根节点为空！");
		}

//		List<Element> tableGroupList = rootElement.elements("TableGroup");
//		List<Element> lineGroupList = rootElement.elements("LineGroup");
//		List<Element> keyGroupList = rootElement.elements("KeyGroup");

		/**赋值给多选框**/
		if(rootElement.element("createTable").getTextTrim().equals("0")){
			tableCreate = true;
		}else{
			tableCreate = false;
		}
		if(rootElement.element("dropTable").getTextTrim().equals("0")){
			tableDrop = true;
		}else{
			tableDrop = false;
		}
		if(rootElement.element("commentTable").getTextTrim().equals("0")){
			tableComment = true;
		}else{
			tableComment = false;
		}
		if(rootElement.element("defaultLine").getTextTrim().equals("0")){
			columnDefault = true;
		}else{
			columnDefault = false;
		}
		if(rootElement.element("checkLine").getTextTrim().equals("0")){
			columnCheck = true;
		}else{
			columnCheck = false;
		}
		if(rootElement.element("createForeignKey").getTextTrim().equals("0")){
			keyCreatePrimary = true;
		}else{
			keyCreatePrimary = false;
		}
		if(rootElement.element("dropForeignKey").getTextTrim().equals("0")){
			keyDropPrimary = true;
		}else{
			keyDropPrimary = false;
		}
		
		if(rootElement.element("initializeSQL").getTextTrim().equals("0")){
			columnCheck = true;
		}else{
			columnCheck = false;
		}
		if(rootElement.element("physicalModelSQL").getTextTrim().equals("0")){
			physicalModelSQL = true;
		}else{
			physicalModelSQL = false;
		}
		if(rootElement.element("storedProcedure").getTextTrim().equals("0")){
			storedProcedure = true;
		}else{
			storedProcedure = false;
		}
		if(rootElement.element("indexSQL").getTextTrim().equals("0")){
			indexSQL = true;
		}else{
			indexSQL = false;
		}
	}
	
	public static Document createOptionItemDocument() {
		if (optionItemDocument == null) {
			SAXReader saxReader = new SAXReader();

			try {
				optionItemDocument = saxReader.read(new File(
						SystemConstants.DATABASE_OPTIONITEM_CONFIG_FILE));

			} catch (DocumentException e) {
				logger.error("初始化Document失败！" + e.getMessage());
				e.printStackTrace();
			}
		}
		return optionItemDocument;
	}

}
