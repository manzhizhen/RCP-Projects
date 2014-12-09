/* 文件名：     DatabaseTypeConfigXml.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-5
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.sunline.suncard.powerdesigner.model.db.DatabaseTypeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 解析数据库类型定义文件（DatabaseTypeConfig.xml）
 * @author  Manzhizhen
 * @version 1.0, 2012-9-5
 * @see 
 * @since 1.0
 */
public class DatabaseTypeConfigXml {
	private static Document document = null;
	
	public static Log logger = LogManager.getLogger(DatabaseTypeConfigXml.class);
	
	private static Document createDocuemnt() {
		if(document == null) {
			SAXReader saxReader = new SAXReader();
			
			try {
				document = saxReader.read(new File(SystemConstants.DATABASE_TYPE_CONFIG_FILE));
				
			} catch (DocumentException e) {
				logger.error("初始化Document失败！" + e.getMessage());
				e.printStackTrace();
			}
		}
		
		return document;
	}
	
	/**
	 * 获得所有的数据库类型定义
	 */
	public List<DatabaseTypeModel> getAllDatabaseType() {
		List<DatabaseTypeModel> list = new ArrayList<DatabaseTypeModel>();
		
		Document doc = createDocuemnt();
		if(doc == null) {
			logger.error("初始化Document失败！");
			return list;
		}
		
		Element rootElement = doc.getRootElement();
		
		if(rootElement == null) {
			logger.error("Document根节点为空！");
			return list;
		}
		
		List<Element> databaseList = rootElement.elements("database");
		if(databaseList == null) {
			return list;
		}
		
		for(Element databaseElement : databaseList) {
			DatabaseTypeModel model = new DatabaseTypeModel();
			model.setDatabaseName(databaseElement.getTextTrim());
			model.setType(databaseElement.attributeValue("type"));
			model.setVersion(databaseElement.attributeValue("version"));
			
			list.add(model);
			
		}
		
		return list;
	}
}
