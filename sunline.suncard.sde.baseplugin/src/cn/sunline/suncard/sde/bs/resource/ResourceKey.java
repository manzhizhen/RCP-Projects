/*
 * 文件名：ResourceKey.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：资源主键类
 * 修改人： xcc
 * 修改时间：2011-10-11
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.bs.resource;

/**
 * 资源主键类
 * 保存系统内所有的资源的主键
 * @author    xcc
 * @version   1.0, 2011-09-19
 * @see       
 * @since     1.0
 */

public class ResourceKey {
	
	//config资源文件内日志配置文件路径
	public static final String CONFIG_LOG_CONFIG_FILE_PATH="LogConfigFilePath";
	
	//config资源文件内系统默认语言
	public static final String CONFIG_DEFAULT_LANGUAGE="DefaultLanguage";
	
	//config资源文件内系统默认国家
	public static final String CONFIG_DEFAULT_CUNTRY="DefaultCountry";
	
	//config资源文件内系统提示信息资源文件路径
	public static final String CONFIG_MESSAGE_RESOURCE_PATH="MessageResource";
	
	//config资源文件内Hibernate配置文件路径
	public static final String CONFIG_HIBERNATE_CFGXML_PATH="HibernateCfgXml";
	
	//config资源文件内Mapping配置文件路径
	public static final String CONFIG_HIBERNATE_MAPPING_PATH="HibernateMapping";
	
	//config资源文件内Hibernate配置文件临时文件路径
	public static final String CONFIG_HIBERNATE_TEMP_PATH="HibernateTempCfgXml";
	
	//config资源文件内国际化文件路径
	public static final String CONFIG_MESSAGE_TEMP_PATH="MessageResourceTemp";
	
	//config资源文件内业务字典文件路径
	public static final String CONFIG_DICT_CFGXML_PATH="DictConfigXml";
	
	//config资源文件内业务字典文件路径
	public static final String CONFIG_DICT_TEMP_PATH="DictConfigXmlTemp";
}
