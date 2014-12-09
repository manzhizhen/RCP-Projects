/*
 * 文件名：
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：     插件安装包文件数目不对，有丢失
 * 修改人：易振强
 * 修改时间：2012-2-7
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.exception;

/**
 * 插件安装包文件数目不对，有丢失
 * @author 易振强
 * @version 1.0, 2012-2-7
 * @see 
 * @since 1.0
 */
public class FileNumError extends Exception{
	public FileNumError() {
	}
	
	public FileNumError(String msg) {
		super(msg);
	}
}
