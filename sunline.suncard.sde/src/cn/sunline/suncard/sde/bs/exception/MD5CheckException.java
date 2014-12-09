/*
 * 文件名：
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：易振强
 * 修改时间：2012-2-7
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.exception;

/**
 * MD5码校验不一致异常
 * @author 易振强
 * @version 1.0, 2012-2-7
 * @see 
 * @since 1.0
 */
public class MD5CheckException extends Exception{
	public MD5CheckException() {
	}
	
	public MD5CheckException(String msg) {
		super(msg);
	}
}
