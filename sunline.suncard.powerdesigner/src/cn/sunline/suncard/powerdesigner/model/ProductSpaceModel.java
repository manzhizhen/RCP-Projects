/* 文件名：     WorkSpaceModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-5
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 产品空间模型
 * 一个产品空间模型可以包含多个产品模型（ProductModel）
 * @author  Manzhizhen
 * @version 1.0, 2012-9-5
 * @see 
 * @since 1.0
 */
public class ProductSpaceModel{
	// 存放了一个产品空间中的所有产品
	private static Map<FileModel, List<ProductModel>> productModelMap = new 
				HashMap<FileModel, List<ProductModel>>();
	
	private static Log logger = LogManager.getLogger(ProductSpaceModel.class.getName());

	public static Map<FileModel, List<ProductModel>> getProductModelMap() {
		return productModelMap;
	}
	
}
