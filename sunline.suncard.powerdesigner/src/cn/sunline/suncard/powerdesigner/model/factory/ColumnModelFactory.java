/* 文件名：     ColumnModelFactory.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-10-31
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 管理ColumnModel的工厂
 * @author  Manzhizhen
 * @version 1.0, 2012-10-31
 * @see 
 * @since 1.0
 */
public class ColumnModelFactory {
	private static Map<FileModel, Map<String, ColumnModel>> allColumnModelMap = new HashMap<FileModel, Map<String, ColumnModel>>();
	
	private static Log logger = LogManager.getLogger(ColumnModelFactory.class
			.getName());
	
	public static void addFileModel(FileModel fileModel) {
		if(fileModel == null) {
			logger.error("要添加的FileModel为空，添加失败！");
			return ;
		}
		
		if(allColumnModelMap.get(fileModel) != null) {
			logger.warn("该FileModel已经在allColumnModelMap中存在，添加失败！");
			return ;
		}
		
		allColumnModelMap.put(fileModel, new HashMap<String, ColumnModel>());
		logger.info("成功向ColumnModelFactory添加一个文件模型:" + fileModel.getFileName());
	}
	
	/**
	 * 检查该FileModel在Map中是否存在
	 * @param fileModel
	 * @return
	 */
	private static boolean checkFileModel(FileModel fileModel) {
		if(fileModel == null || allColumnModelMap.get(fileModel) == null) {
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
		if(fileModel == null || allColumnModelMap.get(fileModel) == null) {
			logger.info("该FileModel在ColumnModelFactory中不存在！");
			return false;
		}
		
		allColumnModelMap.remove(fileModel);
		logger.info("成功从ColumnModelFactory移除一个文件模型:" + fileModel.getFileName());
		
		return true;
	}
	
	/**
	 * 查找所有以该ID为外键的ColumModelList
	 * @param fileModel
	 * @param id
	 * @return
	 */
	public static List<ColumnModel> findAllForeginKeyChildColumn(FileModel fileModel, String id) {
		if(id == null || "".equals(id.trim()) || fileModel == null) {
			logger.error("传入的ID或FileModel为空，无法查找对应它的外键List!");
			return null;
		}
		
		Set<ColumnModel> allColumnSet = fileModel.getAllColumnModel();
		List<ColumnModel> findColumnList = new ArrayList<ColumnModel>();
		
		for(ColumnModel columnModel : allColumnSet) {
			if(id.equals(columnModel.getParentTableColumnId())) {
				findColumnList.add(columnModel);
			}
		}
		
		return findColumnList;
	}
	
	/**
	 * 往Map中添加一个新的有ID的ColumnModel
	 * @param fileModel
	 * @param columnModel 
	 * @return String 新的ColumnModel对应的id，如果返回为null，说明添加失败！
	 */
	public static String addColumnModel(FileModel fileModel, ColumnModel columnModel) {
		if(columnModel == null) {
			logger.error("传入的ColumnModel为空，无法加入allColumnModelMap!");
			return null;
		}
		
		if(!checkFileModel(fileModel)) {
			addFileModel(fileModel);
		}
		
		Map<String, ColumnModel> columnModelMap = allColumnModelMap.get(fileModel);
		
		String id = "COLUMN_ID_";
		for(int index = 1; ;index++) {
			if(columnModelMap.get(id + index) == null) {
				id += index;
				
				columnModel.setId(id);
				columnModelMap.put(id, columnModel);
				
				logger.info("添加了ColumnModel(ID:" + id + ",NAME:" + 
						columnModel.getColumnName() +")");
				
				break ;
			}
		}
		
		
		return id;
	}
	
	/**
	 * 往Map中添加一个新的还没有ID的ColumnModel
	 * 注意：该方法只有在从文件转换成对象时构建Map时调用
	 * @param columnModel 
	 * @return String 新的ColumnModel对应的id，如果返回为null，说明添加失败！
	 */
	public static String addColumnModel(FileModel fileModel, String id, ColumnModel columnModel) {
		if(columnModel == null || id == null || "".equals(id.trim())) {
			logger.error("传入的ColumnModel或Id为空，无法加入allColumnModelMap!");
			return null;
		}
		
		if(!checkFileModel(fileModel)) {
			addFileModel(fileModel);
		}
		
		Map<String, ColumnModel> columnModelMap = allColumnModelMap.get(fileModel);
		
		if(columnModelMap.get(id) != null) {
			logger.error("已经存在ID为：" + id + "的ColumnModel，添加ColumnModel：" + 
					columnModel.getColumnName() + "失败！");
			return null;
		}
		
		logger.info("从文件中导入ColumnModel(ID:" + id + ",NAME:" + 
				columnModel.getColumnName() +")");
		columnModelMap.put(id, columnModel);
		
		
		return id;
	}

	/**
	 * 通过ID来查找一个ColumnModel
	 * @param id
	 * @return
	 */
	public static ColumnModel getColumnModel(FileModel fileModel, String id) {
		if(id == null || "".equals(id)) {
			logger.info("传入的Id为空，无法查找ColumnModel!");
			return null;
		}
		
		if(!checkFileModel(fileModel)) {
			return null;
		}
		
		Map<String, ColumnModel> columnModelMap = allColumnModelMap.get(fileModel);
		
		ColumnModel columnModel = columnModelMap.get(id);
		if(columnModel == null) {
			logger.info("通过ID：" + id + "查找到的ColumnModel为空！");
		}
		
		return columnModel;
	}
	
	/**
	 * 为了保证Map中ID对应的ColumnModel和TableModel中的ID对应的ColumnModel是同一个对象，
	 * 允许更新Map中该ID对应的ColumnModel
	 * @param id
	 * @param newColumnModel
	 */
	public static void updateColumnModel(FileModel fileModel, String id, ColumnModel newColumnModel) {
		if(id == null || "".equals(id.trim()) || newColumnModel == null) {
			logger.error("传入要更新Map的ID或ColumnModel非法，更新失败！");
			return ;
		}
		
		if(!checkFileModel(fileModel)) {
			return ;
		}
		
		Map<String, ColumnModel> columnModelMap = allColumnModelMap.get(fileModel);
		
		ColumnModel findColumnModel = columnModelMap.get(id);
		if(findColumnModel == null) {
			logger.error("传入要更新Map的ID找不到对应的ColumnModel，更新失败！");
			return ;
		}
		
		logger.info("更新了ColumnModel(ID:" + id + ",NAME:" + 
				newColumnModel.getColumnName() +")");
		columnModelMap.put(id, newColumnModel);
	}
	
}
