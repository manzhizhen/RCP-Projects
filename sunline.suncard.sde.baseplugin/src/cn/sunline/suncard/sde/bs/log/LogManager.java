/*
 * 文件名：LogManager.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：日志管理类
 * 修改人： xcc
 * 修改时间：2011-10-11
 * 修改内容：新增
 * 
*/
package cn.sunline.suncard.sde.bs.log;


/**
 * 日志管理类。
 * 通过读取系统变量获得日志配置文件路径，读取配置文件，同时返回日志类实例
 * @author    xcc
 * @version   1.0, 2011-10-11
 * @see       
 * @since     1.0
 */

public class LogManager {
	
	private static LogManager _logManager = new LogManager();
	private static LogFactory _logFactory;
	
	
	private LogManager(){
		if(_logFactory == null){
			_logFactory = new LogFactory();
		}
	}
	
	
	public static LogManager getInstance(){
		return _logManager;
	}
	
	public static LogFactory getLogFactory(){
		return _logFactory;
	}
	
	public static Log getLogger(String name){
		return _logFactory.getLogger(name);
	}
	
	public static Log getLogger(Class<?> clazz){
		return _logFactory.getLogger(clazz);
	}
}
