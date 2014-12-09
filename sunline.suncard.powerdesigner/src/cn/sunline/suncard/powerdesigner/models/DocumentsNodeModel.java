/* 文件名：     DocumentsNodeModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-7
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.models;

import java.util.LinkedHashSet;

import cn.sunline.suncard.powerdesigner.model.DocumentCategoryModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;

/**
 * 文档节点模型
 * @author  Manzhizhen
 * @version 1.0, 2012-12-7
 * @see 
 * @since 1.0
 */
public class DocumentsNodeModel {
	private ProductModel parentModel; // 表示其所属的父对象，有可能是产品模型或项目模型

	private LinkedHashSet<DocumentCategoryModel> docCategoryModelSet = new LinkedHashSet<DocumentCategoryModel>();
	
	public ProductModel getParentModel() {
		return parentModel;
	}

	public void setParentModel(ProductModel parentModel) {
		this.parentModel = parentModel;
	}

	public LinkedHashSet<DocumentCategoryModel> getDocCategoryModelSet() {
		return docCategoryModelSet;
	}
	
	
}
