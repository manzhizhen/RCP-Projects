/* 文件名：     PhysicalDiagramModelFactory.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-27
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model.factory;

import java.util.HashMap;
import java.util.Map;

import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 管理PhysicalDiagramModel的工厂
 * @author  Manzhizhen
 * @version 1.0, 2012-12-27
 * @see 
 * @since 1.0
 */
public class PhysicalDiagramModelFactory {
	private static Map<FileModel, Map<String, PhysicalDiagramModel>> allPhysicalDiagramModelMap = 
			new HashMap<FileModel, Map<String, PhysicalDiagramModel>>();
	
	private static Log logger = LogManager.getLogger(PhysicalDiagramModelFactory.class
			.getName());
	
	/**
	 * 添加一个文件模型到工厂
	 * @param fileModel
	 */
	public static void addFileModel(FileModel fileModel) {
		if(fileModel == null) {
			logger.error("要添加的FileModel为空，添加失败！");
			return ;
		}
		
		if(allPhysicalDiagramModelMap.get(fileModel) != null) {
			logger.warn("该FileModel已经在PhysicalDiagramModelFactory中存在，添加失败！");
			return ;
		}
		
		allPhysicalDiagramModelMap.put(fileModel, new HashMap<String, PhysicalDiagramModel>());
		logger.info("成功向PhysicalDiagramModelFactory中添加一个文件模型:" + fileModel.getFileName());
	}
	
	/**
	 * 检查该FileModel在Map中是否存在
	 * @param fileModel
	 * @return
	 */
	public static boolean checkFileModel(FileModel fileModel) {
		if(fileModel == null || allPhysicalDiagramModelMap.get(fileModel) == null) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 关闭FileModel时需要把它从Map中移除
	 * @param fileModel
	 * @return
	 */
	public static boolean removeFileModel(FileModel fileModel) {
		if(fileModel == null || allPhysicalDiagramModelMap.get(fileModel) == null) {
			logger.info("该FileModel在PhysicalDiagramModelFactory中不存在！");
			return false;
		}
		
		allPhysicalDiagramModelMap.remove(fileModel);
		logger.info("成功从PhysicalDiagramModelFactory中移除一个文件模型:" + fileModel.getFileName());
		return true;
	}
	
	/**
	 * 往Map中添加一个新的还没有ID的PhysicalDiagramModel
	 * @return String 新的PhysicalDiagramModel对应的id，如果返回为null，说明添加失败！
	 */
	public static String addPhysicalDiagramModel(FileModel fileModel, String id, PhysicalDiagramModel physicalDiagramModel) {
		if(physicalDiagramModel == null || id == null || "".equals(id.trim())) {
			logger.error("传入的TableModel或Id为空，无法加入PhysicalDiagramModelFactory!");
			return null;
		}
		
		if(!checkFileModel(fileModel)) {
			addFileModel(fileModel);
		}
		
		Map<String, PhysicalDiagramModel> physicalDiagramModelMap = allPhysicalDiagramModelMap.get(fileModel);
		
		if(physicalDiagramModelMap.get(id) != null) {
			logger.error("已经存在ID为：" + id + "的PhysicalDiagramModel，添加PhysicalDiagramModel：" + 
					physicalDiagramModel.getName() + "失败！");
			return null;
		}
		
		logger.info("向PhysicalDiagramModelFactory成功添加PhysicalDiagramModel(ID:" + id + ",NAME:" + 
				physicalDiagramModel.getName() +")");
		physicalDiagramModelMap.put(id, physicalDiagramModel);
		
		
		return id;
	}
	
	/**
	 * 向Map中移除PhysicalDiagramModel
	 * @return String 新的PhysicalDiagramModel对应的id，如果返回为null，说明添加失败！
	 */
	public static void removePhysicalDiagramModel(FileModel fileModel, String id) {
		if(id == null || "".equals(id.trim())) {
			logger.error("传入的TableModel或Id为空，无法从PhysicalDiagramModelFactory中删除!");
			return ;
		}
		
		if(!checkFileModel(fileModel)) {
			addFileModel(fileModel);
		}
		
		Map<String, PhysicalDiagramModel> physicalDiagramModelMap = allPhysicalDiagramModelMap.get(fileModel);
		
		physicalDiagramModelMap.remove(id);
	}
	
	/**
	 * 通过ID来查找一个PhysicalDiagramModel
	 * @param id
	 * @return
	 */
	public static PhysicalDiagramModel getPhysicalDiagramModel(FileModel fileModel, String id) {
		if(id == null || "".equals(id)) {
			logger.info("传入的Id为空，无法查找PhysicalDiagramModel!");
			return null;
		}
		
		if(!checkFileModel(fileModel)) {
			return null;
		}
		
		Map<String, PhysicalDiagramModel> physicalDiagramModelMap = allPhysicalDiagramModelMap.get(fileModel);
		
		PhysicalDiagramModel physicalDiagramModel = physicalDiagramModelMap.get(id);
		if(physicalDiagramModel == null) {
			logger.info("通过ID：" + id + "查找到的PhysicalDiagramModel为空！");
		}
		
		return physicalDiagramModel;
	}
}
