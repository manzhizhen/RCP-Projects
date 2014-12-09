/*
 * 文件名：SystemException.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：系统异常父类
 * 修改人： xcc
 * 修改时间：2011-11-8
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.exception;

/**
 * 系统异常父类
 * 系统异常父类，所有系统自定义异常必须继承此类
 * @author    xcc
 * @version   1.0, 2011-11-8
 * @since     1.0
 */
public class SystemException extends Exception {
	private static final long serialVersionUID = 5730069014474370404L;
	
	private String errorCode = null;
	private final static String defaultErrorCode="99999999";
	
	public SystemException(String errorCode){
		this.errorCode = errorCode;
	}
	
	public SystemException(){
		this.errorCode = defaultErrorCode;
	}
}
