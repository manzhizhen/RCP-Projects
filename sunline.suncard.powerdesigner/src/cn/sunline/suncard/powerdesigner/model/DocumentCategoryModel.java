/* 文件名：     DocumentCategoryModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-2-17
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.util.LinkedHashSet;
import java.util.Set;

import cn.sunline.suncard.powerdesigner.models.DocumentsNodeModel;

/**
 * 文档分类模型
 * @author  Manzhizhen
 * @version 1.0, 2013-2-17
 * @see 
 * @since 1.0
 */
public class DocumentCategoryModel {
	private String name; // 文档分类的名称，也就是一个文件夹的名称
	private Set<DocumentModel> documentModelSet = new LinkedHashSet<DocumentModel>(); // 属于该文档分类的文档模型的Set

	private DocumentsNodeModel documentsNodeModel;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<DocumentModel> getDocumentModelSet() {
		return documentModelSet;
	}

	public DocumentsNodeModel getDocumentsNodeModel() {
		return documentsNodeModel;
	}

	public void setDocumentsNodeModel(DocumentsNodeModel documentsNodeModel) {
		this.documentsNodeModel = documentsNodeModel;
	}
	
	
}
