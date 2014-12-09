/*
 * 文件名：Log4jLogger.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：日志操作类
 * 修改人： xcc
 * 修改时间：2011-10-11
 * 修改内容：新增
 * 
*/
package cn.sunline.suncard.sde.bs.log;

import org.apache.log4j.Logger;

import org.apache.log4j.Level;

/**
 * 日志操作类。
 * 提供系统所有日志操作
 * @author    xcc
 * @version   1.0, 2011-10-11
 * @see       
 * @since     1.0
 */

public class Log4jLogger implements Log {
	
	private Logger logger=null;
	
	private Log4jLogger(){}
	
	public Log4jLogger(String name){
		if(logger == null){
			logger = Logger.getLogger(name);
		}
	}

	@Override
	public void debug(Object message, Throwable t) {
		logger.log(Level.DEBUG,String.valueOf(message),t);
	}

	@Override
	public void debug(Object message) {
		logger.log(Level.DEBUG,String.valueOf(message));
	}

	@Override
	public void error(Object message, Throwable t) {
		logger.log(Level.ERROR,String.valueOf(message),t);
	}

	@Override
	public void error(Object message) {
		logger.log(Level.ERROR,String.valueOf(message));
	}

	@Override
	public void fatal(Object message, Throwable t) {
		logger.log(Level.FATAL,String.valueOf(message),t);
	}

	@Override
	public void fatal(Object message) {
		logger.log(Level.FATAL,String.valueOf(message));
	}

	@Override
	public void info(Object message, Throwable t) {
		logger.log(Level.INFO,String.valueOf(message),t);
	}

	@Override
	public void info(Object message) {
		logger.log(Level.INFO,String.valueOf(message));
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isEnabledFor(Level.WARN);
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isEnabledFor(Level.WARN);
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isEnabledFor(Level.ERROR);
	}

	@Override
	public boolean isFatalEnabled() {
		return logger.isEnabledFor(Level.FATAL);
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isEnabledFor(Level.INFO);
	}

	@Override
	public void warn(Object message, Throwable t) {
		logger.log(Level.WARN,String.valueOf(message),t);
	}

	@Override
	public void warn(Object message) {
		logger.log(Level.WARN,String.valueOf(message));
	}

}
