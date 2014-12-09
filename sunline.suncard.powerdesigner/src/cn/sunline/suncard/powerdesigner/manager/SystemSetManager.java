/* 文件名：     SystemSetManager.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-22
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.manager;

import cn.sunline.suncard.powerdesigner.resource.DmConstants;

/**
 * 系统级参数设置管理类
 * @author  Manzhizhen
 * @version 1.0, 2012-11-22
 * @see 
 * @since 1.0
 */
public class SystemSetManager {
	private static String tableModelGefShowType; // 定义表格模型在GEF中的显示方式
	private static String isAddDefaultColumns;   // 创建表格时是否加入默认列
	private static String defaultTableType;		 // 创建表格时默认的表格类型

	public static String getTableModelGefShowType() {
		return tableModelGefShowType;
	}

	public static void setTableModelGefShowType(String tableModelGefShowType) {
		SystemSetManager.tableModelGefShowType = tableModelGefShowType;
	}

	public static String getIsAddDefaultColumns() {
		return isAddDefaultColumns;
	}

	public static void setIsAddDefaultColumns(String isAddDefaultColumns) {
		SystemSetManager.isAddDefaultColumns = isAddDefaultColumns;
	}

	public static String getDefaultTableType() {
		return defaultTableType;
	}

	public static void setDefaultTableType(String defaultTableType) {
		SystemSetManager.defaultTableType = defaultTableType;
	}
	
}
