/* 文件名：     ModulesNodeModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-7
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.models;

import cn.sunline.suncard.powerdesigner.model.ProductModel;

/**
 * 功能模块节点模型
 * @author  Manzhizhen
 * @version 1.0, 2012-12-7
 * @see 
 * @since 1.0
 */
public class ModulesNodeModel {
	private ProductModel productModel;

	public ProductModel getProductModel() {
		return productModel;
	}

	public void setProductModel(ProductModel productModel) {
		this.productModel = productModel;
	}

}
