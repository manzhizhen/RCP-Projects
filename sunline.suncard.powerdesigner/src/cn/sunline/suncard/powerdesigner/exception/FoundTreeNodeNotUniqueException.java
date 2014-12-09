/* 文件名：     CanNotFoundParentIDException.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-5-14
 * 修改内容：创     建
 */
package cn.sunline.suncard.powerdesigner.exception;

/**
 * 找到的树节点不唯一
 * @author  易振强
 * @version 1.0, 2012-5-14
 * @see 
 * @since 1.0
 */
public class FoundTreeNodeNotUniqueException extends Exception{
	private String notUniqueId = "";
	
	public FoundTreeNodeNotUniqueException(String notUniqueId) {
		this.notUniqueId = notUniqueId;
	}
	
	@Override
	public String getMessage() {
		return "找到的树节点不唯一:" + notUniqueId;
	}
}
