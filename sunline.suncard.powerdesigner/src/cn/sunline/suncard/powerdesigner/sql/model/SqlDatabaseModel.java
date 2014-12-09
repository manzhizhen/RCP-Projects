/* 文件名：     DatabaseModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-11
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.sql.model;

import java.util.LinkedHashMap;

/**
 * 数据库模型
 * @author  Manzhizhen
 * @version 1.0, 2013-1-11
 * @see 
 * @since 1.0
 */
public class SqlDatabaseModel {
	private String name;
	
	private LinkedHashMap<String, SqlTableModel> tableMap = new LinkedHashMap<String, SqlTableModel>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LinkedHashMap<String, SqlTableModel> getTableMap() {
		return tableMap;
	}
}
