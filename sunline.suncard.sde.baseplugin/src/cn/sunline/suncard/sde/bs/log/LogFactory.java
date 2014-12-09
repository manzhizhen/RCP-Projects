/*
 * 文件名：LogFactory.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：日志工厂类
 * 修改人： xcc
 * 修改时间：2011-10-11
 * 修改内容：新增
 * 
*/
package cn.sunline.suncard.sde.bs.log;

import org.apache.log4j.PropertyConfigurator;

import cn.sunline.suncard.sde.bs.util.SystemParameters;

/**
 * 日志工厂类。
 * 定义日志工厂，通过日志工厂进行日志实例的管理
 * @author    xcc
 * @version   1.0, 2011-10-11
 * @see       
 * @since     1.0
 */

public class LogFactory {
	private String configFilePath = null;
	
	public LogFactory(){
		configFilePath = System.getProperty("user.dir")+"/"+SystemParameters.getLogConfigFilePath();
		PropertyConfigurator.configure(configFilePath);
	}
	
	public Log getLogger(String name){
		return new Log4jLogger(name);
	}
	
	public Log getLogger(Class<?> clazz){
		return new Log4jLogger(clazz.getName());
	}
	
	public String getConfigFilePath(){
		return configFilePath;
	}
}
