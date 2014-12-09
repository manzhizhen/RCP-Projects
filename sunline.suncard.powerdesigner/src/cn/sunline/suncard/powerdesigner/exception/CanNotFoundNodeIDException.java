/* 文件名：     CanNotFoundParentIDException.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-5-14
 * 修改内容：创     建
 */
package cn.sunline.suncard.powerdesigner.exception;

/**
 * 不存在的节点Id
 * @author  易振强
 * @version 1.0, 2012-5-14
 * @see 
 * @since 1.0
 */
public class CanNotFoundNodeIDException extends Exception{
	private String treeContentId;
	
	public CanNotFoundNodeIDException(String treeContentId) {
		this.treeContentId = treeContentId;
	}
	
	@Override
	public String getMessage() {
		return "不存在的节点ID:" + treeContentId;
	}
}
