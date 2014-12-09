/* 文件名：     SystemConstants.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-26
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.resource;

import java.io.File;

/**
 * 系统级的常量
 * @author  Manzhizhen
 * @version 1.0, 2012-11-26
 * @see 
 * @since 1.0
 */
public final class SystemConstants {
	// 表格图形显示方式
	public final static String TABLE_SHOW_TYPE_P = "P"; // 只显示主键
	public final static String TABLE_SHOW_TYPE_PF = "PF"; // 显示主外键	
	public final static String TABLE_SHOW_TYPE_ALL = "ALL"; // 显示全部
	
	// 表格类型
	public final static String TABLE_TYPE_S = "S"; // 系统配置表
	public final static String TABLE_TYPE_U = "U"; // 用户配置表
	public final static String TABLE_TYPE_B = "B"; // 业务数据表
	public final static String TABLE_TYPE_D = "D"; // 日志表
	
	// 表格显示方式在ini文件中对应的值
	public final static String TABLEMODEL_GET_SHOW_TYPE = "TABLEMODEL_GET_SHOW_TYPE";
	// 是否添加默认字段在ini文件中的对应的值
	public final static String ADD_DEFAULT_COLUMNS = "ADD_DEFAULT_COLUMNS";
	// 默认表格类型
	public final static String DEFAULT_TABLE_TYPE = "DEFAULT_TABLE_TYPE";	
	
	// GBK的编码
	public final static String FILE_CODE_GBK = "GBK";
	// UTF-8的编码
	public final static String FILE_CODE_UTF = "UTF-8";
	
	
	// 打开或新建后文件存放的工作空间目录
	public final static String WORKSPACEPATH = "@workspace@" + File.separator;
	
	// 打开或新建项目后文件存放的项目空间目录
	public final static String PROJECTSPACEPATH = "@projectspace@" + File.separator;
	
	// 检查文件正确性时在工作空间下建立的临时目录
	public final static String WORKSPACEPATH_TEMP = WORKSPACEPATH + "~worktemp~" + File.separator;
	
	// 检查项目文件正确性时在工作空间下建立的临时目录
	public final static String PROJECTSPACEPATH_TEMP = PROJECTSPACEPATH + "~projecttemp~" + File.separator;
	
	// 压缩文件中的各文件名称 
	public final static String ZIP_FILE_SUNCARDDESIGNER = "SuncardDesigner.spd"; // 数据库设计文件
	public final static String ZIP_FILE_PROJECT = "ProjectDesigner.ppd"; // 数据库设计文件
	public final static String ZIP_FILE_SUNCARDDESIGNER_DATA = "SuncardDesignerData.spdd"; // 数据库设计文件的数据文件
	public final static String ZIP_FILE_SUNCARDDESIGNER_DATA_FOLDER = "SuncardDesignerData"; // 数据库设计文件的数据文件存放的文件夹
	public final static String ZIP_FILE_DOC = "文档"; // 文档文件夹
	public final static String ZIP_FILE_CODE = "代码"; // 代码文件夹
	
	// 配置文件存放根目录
	public static final String CONFIG_FILE = "config" + File.separator;
	
	// 数据库类型定义文件所在位置
	public static final String DATABASE_TYPE_CONFIG_FILE = CONFIG_FILE + 
			"DatabaseTypeConfig.xml";;
	
	//数据类型定义文件所在位置
	public static final String DATA_TYPE_CONFIG_FILE = CONFIG_FILE + 
			"DataTypeConfig.xml";
	
	//Drop语句定义文件所在位置
	public static final String DATABASE_SQL_CONFIG_FILE = CONFIG_FILE + 
			"DatabaseSQLConfig.xml";
	
	//StandardChecks中Default定义文件所在位置
	public static final String DATABASE_STANDARDCHECKS_DEFAULT_FILE = CONFIG_FILE + 
			"StandardChecksDefaultConfig.xml";
	
	//optionItemDocument配置文件位置
	public static final String DATABASE_OPTIONITEM_CONFIG_FILE = CONFIG_FILE + 
			"OptionItemConfig.xml";
	
	// 模块数据的配置文件ModuleDataConfig.xml存放位置
	public static final String MODULE_DATA_CONFIG_FILE = CONFIG_FILE + 
			"ModuleDataConfig.xml";
	
	// 系统级属性的配置文件SunlineProperties.ini存放位置
	public static final String SYSTEM_PAR_CONFIG_FILE = CONFIG_FILE + 
			"SunlineProperties.ini";
	
	// 压缩文件扩展名
	public static final String ZIP_FILE_EXTEND_NAME = "zip";
	
	// 数据库设计文件扩展名
	public static final String SPD_FILE_EXTEND_NAME = "spd";
	
	// 工作空间文件扩展名
	public static final String WORK_SPACE_FILE_EXTEND_NAME = "wspd";
	
	// pdm文件扩展名
	public static final String FILE_EXTEND_NAME_PDM = "pdm";
	
	//JDBCConfig文件位置
	public static final String JDBC_CONFIG_Driver = CONFIG_FILE + 
			"JDBCDriverConfig.xml";
	public static final String JDBC_CONFIG_FILE = CONFIG_FILE + 
			"JDBCFileConfig.xml";
	
	
}


