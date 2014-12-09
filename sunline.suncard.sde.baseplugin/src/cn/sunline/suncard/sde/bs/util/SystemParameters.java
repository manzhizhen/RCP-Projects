/*
 * 文件名：SystemParameters.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：系统参数类
 * 修改人： xcc
 * 修改时间：2011-10-11
 * 修改内容：新增
 * 
*/
package cn.sunline.suncard.sde.bs.util;

import cn.sunline.suncard.sde.bs.resource.ResourceKey;

/**
 * 系统参数类。
 * 读取系统环境变量获得数据
 * @author    xcc
 * @version   1.0, 2011-10-11
 * @see       
 * @since     1.0
 */

public class SystemParameters {
	
	/**
	 * 获取环境变量内的日志配置文件路径
	 * @return 日志配置文件路径
	 */
	public static String getLogConfigFilePath(){
		return System.getProperty(ResourceKey.CONFIG_LOG_CONFIG_FILE_PATH);
	}
	
	/**
	 * 获取环境变量内的默认语言
	 * @return 系统默认语言
	 */
	public static String getDefaultLanguage(){
		return System.getProperty(ResourceKey.CONFIG_DEFAULT_LANGUAGE);
	}
	
	/**
	 * 获取环境变量内的默认国家
	 * @return 系统默认国家
	 */
	public static String getDefaultCountry(){
		return System.getProperty(ResourceKey.CONFIG_DEFAULT_CUNTRY);
	}
	
	/**
	 * 获取环境变量内的提示信息资源文件路径
	 * @return 提示信息资源文件路径
	 */
	public static String getMessageResourcePath(){
		return System.getProperty(ResourceKey.CONFIG_MESSAGE_RESOURCE_PATH);
	}
	
	/**
	 * 获取环境变量内的Hibernate配置文件的路径
	 * @return Hibernate配置文件路径
	 */
	public static String getHibernateXmlPath() {
		return System.getProperty(ResourceKey.CONFIG_HIBERNATE_CFGXML_PATH);
	}
	
	/**
	 * 获取环境变量内的Hibernate Mapping配置文件的路径
	 * @return Mapping配置文件的路径
	 */
	public static String getHibernateMappingPath() {
		return System.getProperty(ResourceKey.CONFIG_HIBERNATE_MAPPING_PATH);
	}
	
	/**
	 * 获取环境变量内的Hibernate临时配置文件路径
	 * @return Hibernate临时配置文件路径
	 */
	public static String getHibernateTempXmlPath() {
		return System.getProperty(ResourceKey.CONFIG_HIBERNATE_TEMP_PATH);
	}
	
	public static String getMessageResourceTempPath(){
		return System.getProperty(ResourceKey.CONFIG_MESSAGE_TEMP_PATH);
	}
	
	/**
	 * 获取环境变量内的业务字典配置文件路径
	 * @return 业务字典配置文件路径
	 */
	public static String getDictConfigXml() {
		return System.getProperty(ResourceKey.CONFIG_DICT_CFGXML_PATH);
	}
	
	/**
	 * 获取环境变量内的业务字典临时生成的文件路径
	 * @return 业务字典临时生成的文件路径
	 */
	public static String getDictConfigXmlTemp() {
		return System.getProperty(ResourceKey.CONFIG_DICT_TEMP_PATH);
	}
	/**
	 * 获取用户当前运行所在目录
	 * @return 用户当前运行所在目录
	 */
	public static String getUserDir() {
		return System.getProperty("user.dir");
	}
}
