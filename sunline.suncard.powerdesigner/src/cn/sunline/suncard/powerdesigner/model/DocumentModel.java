/* 文件名：     DocumentModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-2-17
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.io.File;

/**
 * 文档模型
 * @author  Manzhizhen
 * @version 1.0, 2013-2-17
 * @see 
 * @since 1.0
 */
public class DocumentModel {
	private String fileName; // 该文档模型对应的文件对象的名称
	
	private DocumentCategoryModel documentCategoryModel;

	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 返回文件模型对应的文件名称
	 * @return
	 */
	public String getFileName() {
		if(fileName == null) {
			return "NULL FILE";
		} else {
			return fileName;
		}
	}

	public DocumentCategoryModel getDocumentCategoryModel() {
		return documentCategoryModel;
	}

	public void setDocumentCategoryModel(DocumentCategoryModel documentCategoryModel) {
		this.documentCategoryModel = documentCategoryModel;
	}
	
	
}
