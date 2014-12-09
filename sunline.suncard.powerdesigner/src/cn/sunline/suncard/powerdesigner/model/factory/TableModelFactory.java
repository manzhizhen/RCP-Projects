/* 文件名：     TableModelFactory.java
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

import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 管理TableModel的工厂
 * @author  Manzhizhen
 * @version 1.0, 2012-10-31
 * @see 
 * @since 1.0
 */
public class TableModelFactory {
	private static Map<FileModel, Map<String, TableModel>> allTableModelMap = new HashMap<FileModel, Map<String, TableModel>>();
	
	private static Log logger = LogManager.getLogger(TableModelFactory.class
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
		
		if(allTableModelMap.get(fileModel) != null) {
			logger.warn("该FileModel已经在TableModelFactory中存在，添加失败！");
			return ;
		}
		
		allTableModelMap.put(fileModel, new HashMap<String, TableModel>());
		logger.info("成功向TableModelFactory中添加一个文件模型:" + fileModel.getFileName());
	}
	
	/**
	 * 检查该FileModel在Map中是否存在
	 * @param fileModel
	 * @return
	 */
	private static boolean checkFileModel(FileModel fileModel) {
		if(fileModel == null || allTableModelMap.get(fileModel) == null) {
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
		if(fileModel == null || allTableModelMap.get(fileModel) == null) {
			logger.info("该FileModel在TableModelFactory中不存在！");
			return false;
		}
		
		allTableModelMap.remove(fileModel);
		logger.info("成功从TableModelFactory中移除一个文件模型:" + fileModel.getFileName());
		return true;
	}
	
	
	/**
	 * 往Map中添加一个新的有ID的TableModel
	 * @param fileModel
	 * @param tableModel 
	 * @return String 新的TableModel对应的id，如果返回为null，说明添加失败！
	 */
	public static String addTableModel(FileModel fileModel, TableModel tableModel) {
		if(tableModel == null) {
			logger.error("传入的TableModel为空，无法加入allTableModelMap!");
			return null;
		}
		
		if(!checkFileModel(fileModel)) {
			addFileModel(fileModel);
		}
		
		Map<String, TableModel> tableModelMap = allTableModelMap.get(fileModel);
		
		String id = DmConstants.AUTO_TABLE_ID_PREFIX;
		for(int index = 1; ;index++) {
			if(tableModelMap.get(id + index) == null) {
				id += index;
				
				tableModel.setId(id);
				tableModelMap.put(id, tableModel);
				
				logger.info("添加了TableModel(ID:" + id + ",NAME:" + 
						tableModel.getTableName() +")");
				
				break ;
			}
		}
		
		
		return id;
	}
	
	/**
	 * 往Map中添加一个新的还没有ID的TableModel
	 * 注意：该方法只有在从文件转换成对象时构建Map时调用
	 * @param tableModel 
	 * @return String 新的TableModel对应的id，如果返回为null，说明添加失败！
	 */
	public static String addTableModel(FileModel fileModel, String id, TableModel tableModel) {
		if(tableModel == null || id == null || "".equals(id.trim())) {
			logger.error("传入的TableModel或Id为空，无法加入TableModelFactory!");
			return null;
		}
		
		if(!checkFileModel(fileModel)) {
			addFileModel(fileModel);
		}
		
		Map<String, TableModel> tableModelMap = allTableModelMap.get(fileModel);
		
		if(tableModelMap.get(id) != null) {
			logger.error("已经存在ID为：" + id + "的TableModel，添加TableModel：" + 
					tableModel.getTableName() + "失败！");
			return null;
		}
		
		logger.info("从文件中导入TableModel(ID:" + id + ",NAME:" + 
				tableModel.getTableName() +")");
		tableModelMap.put(id, tableModel);
		
		
		return id;
	}

	/**
	 * 通过ID来查找一个TableModel
	 * @param id
	 * @return
	 */
	public static TableModel getTableModel(FileModel fileModel, String id) {
		if(id == null || "".equals(id)) {
			logger.info("传入的Id为空，无法查找TableModel!");
			return null;
		}
		
		if(!checkFileModel(fileModel)) {
			return null;
		}
		
		Map<String, TableModel> tableModelMap = allTableModelMap.get(fileModel);
		
		TableModel tableModel = tableModelMap.get(id);
		if(tableModel == null) {
			logger.info("通过ID：" + id + "查找到的TableModel为空！");
		}
		
		return tableModel;
	}
	
	/**
	 * 为了保证Map中ID对应的TableModel和TableModel中的ID对应的TableModel是同一个对象，
	 * 允许更新Map中该ID对应的TableModel
	 * @param id
	 * @param newTableModel
	 */
	public static void updateTableModel(FileModel fileModel, String id, TableModel newTableModel) {
		if(id == null || "".equals(id.trim()) || newTableModel == null) {
			logger.error("传入要更新Map的ID或TableModel非法，更新失败！");
			return ;
		}
		
		if(!checkFileModel(fileModel)) {
			return ;
		}
		
		Map<String, TableModel> tableModelMap = allTableModelMap.get(fileModel);
		
		TableModel findTableModel = tableModelMap.get(id);
		if(findTableModel == null) {
			logger.error("传入要更新Map的ID找不到对应的TableModel，更新失败！");
			return ;
		}
		
		logger.info("更新了TableModel(ID:" + id + ",NAME:" + 
				newTableModel.getTableName() +")");
		tableModelMap.put(id, newTableModel);
	}
}
