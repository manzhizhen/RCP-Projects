/* 文件名：     ProductSpcaeManager.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-4
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.ProductSpaceModel;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 产品空间管理
 * @author  Manzhizhen
 * @version 1.0, 2013-1-4
 * @see 
 * @since 1.0
 */
public class ProductSpaceManager {
	private static Log logger = LogManager.getLogger(ProductSpaceManager.class.getName());
	
	/**
	 * 通过文件模型来返回其下的所有产品模型列表
	 * @param fileModel
	 * @return
	 */
	public static final List<ProductModel> getProductModelList(FileModel fileModel) {
		if(fileModel == null) {
			logger.error("传入的FileModel为空,无法返回对应的List<ProductModel>!");
			return null;
		}
		
		if(ProductSpaceModel.getProductModelMap().get(fileModel) == null) {
			logger.error("找不到该FileModel对应的产品模型列表，FileModel:" + fileModel.getFileName());
			return null;
		}
		
		return ProductSpaceModel.getProductModelMap().get(fileModel);
	}
	
	/**
	 * 返回该产品空间中的所有模块
	 * @param fileModel
	 * @return
	 */
	public static final Set<ModuleModel> getAllModuleModels() {
		Set<ModuleModel> moduleModelSet = new HashSet<ModuleModel>();
		
		// 找到该产品空间中的所有产品
		Collection<List<ProductModel>> productModels = ProductSpaceModel.getProductModelMap().values();
		for(List<ProductModel> tempList : productModels) {
			for(ProductModel productModel : tempList) {
				moduleModelSet.addAll(productModel.getModuleModelSet());
			}
		}
		
		return moduleModelSet;
	}
	
	/**
	 * 返回该产品空间中的所有产品
	 * @param fileModel
	 * @return
	 */
	public static final Set<ProductModel> getAllProductModels() {
		Set<ProductModel> productModelSet = new HashSet<ProductModel>();
		
		// 找到该产品空间中的所有产品
		Collection<List<ProductModel>> productModels = ProductSpaceModel.getProductModelMap().values();
		for(List<ProductModel> tempList : productModels) {
			productModelSet.addAll(tempList);
		}
		
		return productModelSet;
	}
	
	
	/**
	 * 添加一个产品模型
	 * @param fileModel
	 * @return
	 */
	public static void addProductModel(FileModel fileModel, ProductModel productModel) {
		if(fileModel == null) {
			logger.error("传入的FileModel或ProductModel为空,无法添加产品模型!");
			return ;
		}
		
		if(getProductModelList(fileModel) == null) {
			logger.info("尝试向产品空间中添加一个产品模型失败，找不到对应文件模型的产品列表:" + fileModel.getFileName());
			addFileModel(fileModel);
		}
		
		getProductModelList(fileModel).add(productModel);
		logger.info("成功向产品空间中添加一个产品模型:" + productModel.getName());
	}
	
	/**
	 * 删除一个产品模型
	 * @param fileModel
	 * @return
	 */
	public static void removeProductModel(FileModel fileModel, ProductModel productModel) {
		if(fileModel == null) {
			logger.error("传入的FileModel或ProductModel为空,无法删除产品模型！");
			return ;
		}
		
		if(getProductModelList(fileModel) == null) {
			logger.info("尝试向产品空间中删除一个产品模型失败，找不到对应文件模型的产品列表:" + fileModel.getFileName());
			addFileModel(fileModel);
		}
		
		boolean success = getProductModelList(fileModel).remove(productModel);
		if(success) {
			logger.info("成功向产品空间中删除一个产品模型:" + productModel.getName());
		} else {
			logger.info("产品空间中不存在该产品模型:" + productModel.getName() + "，无法删除！");
		}
	}
	
	/**
	 * 添加一个文件模型
	 * @param fileModel
	 * @return
	 */
	public static void addFileModel(FileModel fileModel) {
		if(fileModel == null) {
			logger.debug("传入的FileModel为空,无法添加到产品空间Map!");
			return ;
		} 
		
		if(ProductSpaceModel.getProductModelMap().get(fileModel) != null) {
			logger.debug("产品空间中已经存在此FileModel,添加失败!" + fileModel.getFileName());
			return ;
		}
		
		ProductSpaceModel.getProductModelMap().put(fileModel, new ArrayList<ProductModel>());
		logger.info("成功向产品空间中添加一个文件模型:" + fileModel.getFileName());
	}
	
	/**
	 * 移除一个文件模型
	 * @param fileModel
	 * @return
	 */
	public static void removeFileModel(FileModel fileModel) {
		if(fileModel == null) {
			logger.error("传入的FileModel为空,无法从产品空间中移除!");
			return ;
		}
		
		ProductSpaceModel.getProductModelMap().remove(fileModel);
		logger.info("成功从产品空间中移除一个文件模型:" + fileModel.getFileName());
	}

	/**
	 * 通过产品ID来获得产品对象
	 */
	public static ProductModel getProductModelFromId(FileModel fileModel, String productId) {
		if(fileModel == null || productId == null) {
			logger.warn("传入的FileModel或者产品ID为空，无法获得对应的产品对象！");
			return null;
		}
		
		List<ProductModel> productModelList = getProductModelList(fileModel);
		if(productModelList != null) {
			for(ProductModel prouductModel : productModelList) {
				if(productId.equals(prouductModel.getId())) {
					return prouductModel;
				}
			}
		}
		
		logger.warn("在文件模型:" + fileModel.getFileName() + "中找不到ID为:" + productId + "的产品模型！");
		return null;
	}
	
	/**
	 * 通过模块ID来获得产品对象
	 */
	public static ModuleModel getModuleModelFromId(FileModel fileModel, String moduleId) {
		if(fileModel == null || moduleId == null) {
			logger.warn("传入的FileModel或者模块ID为空，无法获得对应的模块对象！");
			return null;
		}
		
		List<ProductModel> productModelList = getProductModelList(fileModel);
		if(productModelList != null) {
			for(ProductModel productModel : productModelList) {
				Set<ModuleModel> moduleModelSet = productModel.getModuleModelSet();
				for(ModuleModel moduleModel : moduleModelSet) {
					if(moduleId.equals(moduleModel.getId())) {
						return moduleModel;
					}
				}
			}
		}
		
		logger.warn("在文件模型:" + fileModel.getFileName() + "中找不到ID为:" + moduleId + "的模块模型！");
		return null;
	}
}
