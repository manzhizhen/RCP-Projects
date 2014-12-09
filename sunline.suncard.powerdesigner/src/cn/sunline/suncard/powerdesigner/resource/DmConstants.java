/*
 * 文件名：DmConstants.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：<描述>
 * 修改人：易振强
 * 修改时间：2011-10-30
 * 修改内容：新增
 */
package cn.sunline.suncard.powerdesigner.resource;

import java.io.File;

import cn.sunline.suncard.powerdesigner.Activator;

/**
 * @author    易振强
 * @version   1.0  2011-10-30
 * @see       [相关类/方法]
 * @since     [产品/模块版本] 
 */
public interface DmConstants {
	
	public static final String APPLICATION_ID = "sunline.suncard.powerdesigner";

	public static final String PD_APPLICATION_ID = Activator.PLUGIN_ID;
	
	// spd文件根节点名称
	public static final String SPD_XML_ROOT_ELEMENT_NAME = "SunCardDatabaseDesigner";
	// spd文件GEF信息根节点名称
	public static final String SPD_XML_GEFINFO_ROOT_ELEMENT_NAME = "gefInfo";
	// spd文件GEF信息根节点下NODES节点名称
	public static final String SPD_XML_GEFINFO_NODES_ELEMENT_NAME = "nodes";	
	// spd文件GEF信息根节点下NODES节点名称
	public static final String SPD_XML_GEFINFO_LINES_ELEMENT_NAME = "lines";	
	// ppd文件根节点下初始化表格数据节点名称
	public static final String SPD_XML_INIT_TABLE_DATA_ELEMENT_NAME = "initTableDatas";
	// spd文件根节点下索引节点名称
	public static final String SPD_XML_INDEX_SQL_ELEMENT_NAME = "indexSqlModels";	
	// spd文件根节点下产品集合节点名称
	public static final String SPD_XML_PRODUCTS_ELEMENT_NAME = "productModels";	
	// spd文件根节点下物理图形模型集合节点名称
	public static final String SPD_XML_PHYSICALDIAGRAMS_ELEMENT_NAME = "physicalDiagramModels";	
	// spd文件根节点下表格集合节点名称
	public static final String SPD_XML_TABLES_ELEMENT_NAME = "tableModels";	
	// spd文件根节点下表格集合节点名称
	public static final String SPD_XML_SHORTCUTS_ELEMENT_NAME = "tableShortcutModels";	
	// spd文件根节点下列集合节点名称
	public static final String SPD_XML_COLUMNS_ELEMENT_NAME = "columnModels";
	// 模块标签配置文件根节点名称
	public static final String MODULE_XML_ROOT_ELEMENT_NAME = "ModuleDatas";	
	// 表格初始化文件spdd文件根节点名称
	public static final String SPDD_XML_ROOT_ELEMENT_NAME = "InitTableDatas";
	// 项目文件根节点名称
	public static final String PPD_XML_ROOT_ELEMENT_NAME = "SunCardProjectDesigner";
	
	// 工作空间树节点ID
	public static final String WORK_SPACE_TREE_ID = "work_space_tree_root_id";
	
	// 产品空间树节点ID
	public static final String PRODUCT_SPACE_TREE_ID = "product_space_tree_root_id";
	
	// 数据库连接树节点ID
	public static final String DB_CONNECTION_TREE_ID = "db_connection_tree_root_id";
	
	// 项目空间树节点ID
	public static final String PROJECT_SPACE_TREE_ID = "project_space_tree_root_id";
	
	// 数据库树的Action标志
	public final static String ADD_PHYSICAL_DATA_FLAG = "ADD_PHYSICAL_DATA_FLAG"; // 添加物理数据模型
	public final static String ADD_PHYSICAL_DIAGRAM_FLAG = "ADD_PHYSICAL_DIAGRAM_FLAG"; // 添加物理图形模型
	public final static String OPEN_PHYSICAL_DIAGRAM_FLAG = "OPEN_PHYSICAL_DIAGRAM_FLAG"; // 打开物理图形模型
	public final static String ATTRI_PHYSICAL_DATA_FLAG = "ATTRI_PHYSICAL_DATA_FLAG"; // 打开物理数据模型属性
	public final static String ATTRI_PHYSICAL_DIAGRAM_FLAG = "ATTRI_PHYSICAL_DIAGRAM_FLAG"; // 打开物理图形模型属性
	public final static String DEL_PHYSICAL_DATA_FLAG = "DEL_PHYSICAL_DATA_FLAG"; // 删除物理数据模型属性
	public final static String DEL_PHYSICAL_DIAGRAM_FLAG = "DEL_PHYSICAL_DIAGRAM_FLAG"; // 删除物理图形模型属性
	public final static String FIND_IN_DIAGRAM = "FIND_IN_DIAGRAM"; // 在图中查找
	public final static String ATTRI_TABLE_MODEL = "ATTRI_TABLE_MODEL"; // 表格模型的属性
	public final static String DEL_TABLE_MODEL = "DEL_TABLE_MODEL"; // 删除表格模型
	public final static String CLOSE_FILE_MODEL = "CLOSE_FILE_MODEL"; // 关闭一个文件模型
	public final static String NEW_FILE_MODEL = "NEW_FILE_MODEL"; // 新建一个文件模型
	public final static String OPEN_FILE_MODEL = "OPEN_FILE_MODEL"; // 打开一个文件模型
	public final static String DEFAULT_COLUMN_FLAG = "DEFAULT_COLUMN_FLAG"; // 默认字段设置
	public final static String EDIT_SQL_FLAG = "EDIT_SQL_FLAG"; // 编辑SQL脚本
	public final static String EDIT_STOREDPROCEDURE_FLAG = "EDIT_STOREDPROCEDURE_FLAG"; // 编辑存储过程
	public final static String ADD_DOMAINS_COLUMN_MODEL = "ADD_DOMAINS_COLUMN_MODEL"; // 新增Domains
	public final static String DEL_COLUMN_MODEL = "DEL_COLUMN_MODEL"; // 删除
	public final static String MODIFY_COLUMN_MODEL = "MODIFY_COLUMN_MODEL"; // 属性
	public final static String INIT_TABLE_DATA = "INIT_TABLE_DATA"; // 初始化表格数据
	public final static String ADD_PACKAGE_MODEL = "ADD_PACKAGE_MODEL"; // 新增包模型
	public final static String ATTRI_PACKAGE_MODEL = "ATTRI_PACKAGE_MODEL"; // 包模型属性
	public final static String DEL_PACKAGE_MODEL = "DEL_PACKAGE_MODEL"; // 删除包模型
	public final static String IMPORT_PDM_FLAG = "IMPORT_PDM_FLAG"; // 导入PDM文件
	public final static String TABLE_INDEX_FLAG = "TABLE_INDEX_FLAG"; // 编辑表格索引
	public final static String IMPORT_DEFAULT_COLUMN_FLAG = "IMPORT_DEFAULT_COLUMN_FLAG"; // 导入默认列
	public final static String IMPORT_DOMAINS_FLAG = "IMPORT_DOMAINS_FLAG"; // 导入Domains
	public final static String PASTE_TABLE_FLAG = "PASTE_TABLE_FLAG"; // 给物理图形模型粘贴表格
	
	// 产品树的Action标志
	public final static String ADD_PRODUCT_MODEL = "ADD_PRODUCT_MODEL"; // 新建产品模型
	public final static String DEL_PRODUCT_MODEL = "DEL_PRODUCT_MODEL"; // 删除产品模型
	public final static String MODIFY_PRODUCT_MODEL = "MODIFY_PRODUCT_MODEL"; // 修改产品模型
	public final static String IMPORT_TABLE_MODEL = "IMPORT_TABLE_MODEL"; // 引入表格模型
	public final static String REMOVE_TABLE_MODEL = "REMOVE_TABLE_MODEL"; // 移除表格模型
	public final static String ADD_MODULE_MODEL = "ADD_MODULE_MODEL"; // 新增模块模型
	public final static String ATTRI_MODULE_MODEL = "ATTRI_MODULE_MODEL"; // 修改模块模型
	public final static String REMOVE_MODULE_MODEL = "REMOVE_MODULE_MODEL"; // 移除模块模型
	public final static String MANAGE_SQL_MODEL = "MANAGE_SQL_MODEL"; // 管理SQL脚本
	public final static String MANAGE_STORED_MODEL = "MANAGE_STORED_MODEL"; // 管理储存过程
	public final static String ADD_DOC_CATEGORY = "ADD_DOC_CATEGORY"; // 添加文档分类
	public final static String DEL_DOC_CATEGORY = "DEL_DOC_CATEGORY"; // 删除文档分类
	public final static String MODIFY_DOC_CATEGORY = "MODIFY_DOC_CATEGORY"; // 修改文档分类
	public final static String ADD_DOC = "ADD_DOC"; // 添加文档
	public final static String DEL_DOC = "DEL_DOC"; // 删除文档
	public final static String DOWNLOAD_DOC = "DOWNLOAD_DOC"; // 下载文档
	public final static String VIEW_DOC = "VIEW_DOC"; // 查看文档
	
	
	// 项目树的Action标志
	public final static String ADD_PROJECT_GROUP_MODEL = "ADD_PROJECT_GROUP_MODEL"; // 新建项目群模型
	public final static String CLOSE_PROJECT_GROUP_MODEL = "CLOSE_PROJECT_GROUP_MODEL"; // 关闭项目群模型
	public final static String MODIFY_PROJECT_GROUP_MODEL = "MODIFY_PROJECT_GROUP_MODEL"; // 修改项目群模型
	public final static String ADD_PROJECT_MODEL = "ADD_PROJECT_MODEL"; // 新建项目模型
	public final static String OPEN_PROJECT_MODEL = "OPEN_PROJECT_MODEL"; // 打开项目模型
	public final static String CLOSE_PROJECT_MODEL = "CLOSE_PROJECT_MODEL"; // 关闭项目模型
	public final static String ATTRI_PROJECT_MODEL = "ATTRI_PROJECT_MODEL"; // 属性
	public final static String IMPORT_MODULE_MODEL = "IMPORT_MODULE_MODEL"; // 导入模块
	public final static String SYNCHRONOUS_MODULE_MODEL = "SYNCHRONOUS_MODULE_MODEL"; // 同步模块	
	
	// 数据库连接树的Action标志
	public final static String NEW_CONNECTION = "NEW_CONNECTION"; // 新建数据库连接
	public final static String CONNECTION_DATABASE = "CONNECTION_DATABASE"; // 连接数据库
	public final static String EDIT_CONNECTION = "EDIT_CONNECTION"; // 编辑数据库连接
	public final static String DEL_CONNECTION = "DEL_CONNECTION"; // 删除数据库连接
	public final static String REFRESH_CONNECTION = "REFRESH_CONNECTION"; // 刷新数据库连接
	
	// 关联关系
	public final static String ONE_TO_ONE = "1..1"; // 一对一
	public final static String ONE_TO_MANY = "1..*"; // 一对多	
	public final static String MANY_TO_MANY = "0..*"; // 多对多	
	public final static String MANY_TO_ONE = "0..1"; // 多对一
	


	// 树节点ID前缀与后缀之间的分隔符
	public static final String SEPARATOR = "_";
	// 树节点下表格集合节点的名称
	public static final String TABLES_NODE_NAME = "Tables";
	// 树节点下Domains节点的名称
	public static final String DOMAINS_NODE_NAME = "Domains";
	// 树节点下默认列集合节点的名称
	public static final String DEFAULTS_NODE_NAME = "默认列集合";
	public static final String MODULES_NODE_NAME = "功能模块";
	public static final String SQLS_NODE_NAME = "SQL脚本";
	public static final String CODES_NODE_NAME = "程序代码";
	public static final String DOC_NODE_NAME = "文档";
	public static final String STOREDPROCEDURES_NODE_NAME = "存储过程";
	
	// 树节点下表格节点的ID后缀规则
	public static final String TABLE_NODE_ID_TAIL= "@1_TABLES@";
	// 树节点下默认列节点的ID后缀规则
	public static final String DEFAULT_NODE_ID_TAIL= "@2_DEFAULTS@";
	// 树节点下Domain节点的ID后缀规则
	public static final String DOMAIN_NODE_ID_TAIL= "@3_DOMAINS@";
	// 树节点下业务字典节点的ID后缀规则
	public static final String DICTIONARY_NODE_ID_TAIL= "@4_DICTIONARYS@";
	// 功能模块节点后缀规则
	public static final String MODULE_NODE_ID_TAIL= "@1_MODULES@";
	// SQL脚本节点后缀规则
	public static final String SQLS_NODE_ID_TAIL= "@2_SQLS@";
	// SQL脚本节点后缀规则
	public static final String STOREDS_NODE_ID_TAIL= "@3_STOREDS@";
	// 程序代码节点后缀规则
	public static final String CODES_NODE_ID_TAIL= "@4_CODES@";
	// 文档节点后缀规则
	public static final String DOCS_NODE_ID_TAIL= "@5_DOCS@";
	
	
	
	// 新建列时列名的前缀
	public static final String COLUMN_PREFIX = "COLUMN_";
	
	// 未定义数据类型的名称
	public static final String UNDEFINED = "<Undefined>";
	public static final String NONE = "<无>"; 
	
	// 将表格列对象转换成字符串时所用的分隔符
	public static final String COLUMN_MODEL_SEPARATOR = "#";
	
	// 新建表格时，默认自动生成的名字前缀
	public static final String AUTO_TABLE_NAME_PREFIX = "Table";
	public static final String AUTO_TABLE_DESC_PREFIX = "表";
	
	// 自动生成的表格模型的ID的前缀
	public static final String AUTO_TABLE_ID_PREFIX = "TABLE_ID_";	
	// 自动生成的表格快捷方式模型的ID的前缀
	public static final String AUTO_TABLE_SHORTCUT_ID_PREFIX = "TABLESHORTCUT_";
	
	// Command的各种状态
	public static final String COMMAND_ADD = "COMMAND_ADD";
	public static final String COMMAND_MODIFY = "COMMAND_MODIFY";
	public static final String COMMAND_DEL = "COMMAND_DEL";
	
	//DatabaseSQLConfig需要用的转换drop语句的
	public static final String SQLCONFIG_TABLENAME = "@TABLENAME@";//表名
	public static final String SQLCONFIG_TABLENAME_1 = "@TABLENAME_1@";//表名	
	public static final String SQLCONFIG_TABLENAME_2 = "@TABLENAME_2@";//表名	
	public static final String SQLCONFIG_CONSTRAINT = "@CONSTRAINT@";//表名	
	public static final String SQLCONFIG_COLUMNNAME = "@COLUMNNAME@";//列名
	public static final String SQLCONFIG_TABLECOMMENT = "@TABLECOMMENT@";//表注释
	public static final String SQLCONFIG_COLUMNCOMMENT = "@COLUMNCOMMENT@";//列注释
	public static final String SQLCONFIG_KEYLIST_1 = "@KEY_LIST1@";
	public static final String SQLCONFIG_KEYLIST_2 = "@KEY_LIST2@";
	
	// 引用名称前缀
	public static final String REFERENCE_NAME_PREFIX = "Reference_";
	public static final String REFERENCE_DESC_PREFIX = "引用_";
	
	// joins中的未匹配项的定义
	public static final String JOINS_NONE = "<None>";
	
	// 逗号分隔符
	public static final String COMMA_SEPARATOR = ",";
	
	// 空字符串
	public static final String EMPTY_STRING = "";
	
	// 空格
	public final static String BLANK = " ";
	
	// 窗口排列时一行的窗口数量
	public final static int RANK_WINDOW_ROW_NUM = 4;
	
	// 窗口排列 的总数
	public final static int RANK_WINDOW_NUM = 12 * 5;
	
	// 窗口排列时窗口大小
	public final static int RANK_WINDOW_WIDTH = 300;
	public final static int RANK_WINDOW_HEIGH = 40;
	
	// 窗口排列时行之间的距离
	public final static int RANK_WINDOW_ROW_SIZE = 25;
	
	// 数据源类型
	public final static String TABLE_DATA_SOURCE = "D"; // 表数据源
	public final static String CUSTOM_DATA_SOURCE = "C"; // 自定义数据
	public final static String NULL_DATA_SOURCE = "NULL"; // 不使用数据源

	//是与否的标记
	public final static String YES = "Y";
	public final static String NO = "N";
	
	//文件换行
	public final static String FILE_WRAP = "\n\r";
	
	// 数据类型的参数符号
	public final static String DATA_TYPE_PAR = "%";
	
	public final static String PRE_TABLE = "[T]";
	public final static String PRE_COLUMN = "[C]";
	
	public final static String PRE_SQL_ID = "objSql";	// sql语句对象id前缀
	public final static String PRE_SP_ID = "objSp";		// 存储过程对象Id前缀
	
	public final static String PRE_SQL_NAME = "Sql";	// sql名称前缀
	public final static String PRE_SP_NAME = "Procedure";		// 存储过程名称前缀
	
	// 列属性对话框中常规标签中的系统默认值类型
	public final static String SYSTEM_DEFAULT_VALUE_TYPE_D = "D";		// 系统日期
	public final static String SYSTEM_DEFAULT_VALUE_TYPE_T = "T";		// 系统时间
	public final static String SYSTEM_DEFAULT_VALUE_TYPE_U = "U";		// 登录用户
	public final static String SYSTEM_DEFAULT_VALUE_TYPE_O = "O";		// 机构代码
	public final static String SYSTEM_DEFAULT_VALUE_TYPE_C = "C";		// 自定义数据
	
	// 列属性表数据来源标识
	public final static boolean SINGLE_TABLE_SOURCE = true;	// 单表来源标识
	public final static boolean MULTI_TABLE_SOURCE = false;	// 多表来源标识
	
	// 自定义输入表格属性修改
	public final static String COLUMN_PROPERTY_INDEX = "index";
	public final static String COLUMN_PROPERTY_KEY = "key";
	public final static String COLUMN_PROPERTY_VALUE = "value";
	
	// 项目模型ID前缀
	public final static String PROJECT_ID_PREFIX = "PROJECT_ID_";
}