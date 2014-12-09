/* 文件名：     CanNotFoundFolderFromWorkSpaceException.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-27
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.exception;

/**
 * 在工作空间中找不到该FileModel对应的文件夹！
 * @author  Manzhizhen
 * @version 1.0, 2012-12-27
 * @see 
 * @since 1.0
 */
public class CanNotFoundFolderFromWorkSpaceException extends Exception{
	private String folderName;
	
	public CanNotFoundFolderFromWorkSpaceException(String folderName) {
		this.folderName = folderName;
	}
	
	@Override
	public String getMessage() {
		return "在工作空间中找不到该FileModel对应的文件夹" + folderName;
	}

}
