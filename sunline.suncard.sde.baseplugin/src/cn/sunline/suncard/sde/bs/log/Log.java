/*
 * 文件名：Log.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：日志操作接口
 * 修改人： xcc
 * 修改时间：2011-10-11
 * 修改内容：新增
 * 
*/
package cn.sunline.suncard.sde.bs.log;

/**
 * 日志操作接口。
 * 定义系统所有的日志操作
 * @author    xcc
 * @version   1.0, 2011-10-11
 * @see       
 * @since     1.0
 */

public interface Log {
	public void debug(Object message, Throwable t);

	public void debug(Object message);

	public void error(Object message, Throwable t);

	public void error(Object message);

	public void fatal(Object message, Throwable t);

	public void fatal(Object message);

	public void info(Object message, Throwable t);

	public void info(Object message);

	public boolean isDebugEnabled();

	public boolean isWarnEnabled();

	public boolean isErrorEnabled();

	public boolean isFatalEnabled();

	public boolean isInfoEnabled();

	public void warn(Object message, Throwable t);

	public void warn(Object message);
	
}
