/* 文件名：     TableShortcutModelFactory.java
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
import cn.sunline.suncard.powerdesigner.model.TableShortcutModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 管理TableShortcutModel的工厂
 * @author  Manzhizhen
 * @version 1.0, 2012-10-31
 * @see 
 * @since 1.0
 */
public class TableShortcutModelFactory {
	private static Map<FileModel, Map<String, TableShortcutModel>> allTableShortcutModelMap = new HashMap<FileModel, Map<String, TableShortcutModel>>();
	
	private static Log logger = LogManager.getLogger(TableShortcutModelFactory.class
			.getName());
	
	public static void addFileModel(FileModel fileModel) {
		if(fileModel == null) {
			logger.error("要添加的FileModel为空，添加失败！");
			return ;
		}
		
		if(allTableShortcutModelMap.get(fileModel) != null) {
			logger.warn("该FileModel已经在allTableShortcutModelMap中存在，添加失败！");
			return ;
		}
		
		allTableShortcutModelMap.put(fileModel, new HashMap<String, TableShortcutModel>());
		logger.info("成功向TableShortcutModelFactory中添加一个文件模型:" + fileModel.getFileName());
	}
	
	/**
	 * 检查该FileModel在Map中是否存在
	 * @param fileModel
	 * @return
	 */
	private static boolean checkFileModel(FileModel fileModel) {
		if(fileModel == null || allTableShortcutModelMap.get(fileModel) == null) {
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
		if(fileModel == null || allTableShortcutModelMap.get(fileModel) == null) {
			logger.info("该FileModel在TableShortcutModelFactory中不存在！");
			return false;
		}
		
		allTableShortcutModelMap.remove(fileModel);
		logger.info("成功从TableShortcutModelFactory中移除一个文件模型:" + fileModel.getFileName());
		return true;
	}
	
	
	/**
	 * 往Map中添加一个新的有ID的TableShortcutModel
	 * @param fileModel
	 * @param tableShortcutModel 
	 * @return String 新的TableShortcutModel对应的id，如果返回为null，说明添加失败！
	 */
	public static String addTableShortcutModel(FileModel fileModel, TableShortcutModel tableShortcutModel) {
		if(tableShortcutModel == null) {
			logger.error("传入的TableShortcutModel为空，无法加入TableShortcutModelFactory!");
			return null;
		}
		
		if(!checkFileModel(fileModel)) {
			addFileModel(fileModel);
		}
		
		Map<String, TableShortcutModel> tableModelMap = allTableShortcutModelMap.get(fileModel);
		
		String id = DmConstants.AUTO_TABLE_SHORTCUT_ID_PREFIX;
		for(int index = 1; ;index++) {
			if(tableModelMap.get(id + index) == null) {
				id += index;
				
				tableShortcutModel.setId(id);
				tableModelMap.put(id, tableShortcutModel);
				
				logger.info("添加了TableShortcutModel(ID:" + id + ",NAME:" + 
						tableShortcutModel.getId() +")");
				
				break ;
			}
		}
		
		
		return id;
	}
	
	/**
	 * 往Map中添加一个新的还没有ID的TableShortcutModel
	 * 注意：该方法只有在从文件转换成对象时构建Map时调用
	 * @param tableShortcutModel 
	 * @return String 新的TableShortcutModel对应的id，如果返回为null，说明添加失败！
	 */
	public static String addTableShortcutModel(FileModel fileModel, String id, TableShortcutModel tableShortcutModel) {
		if(tableShortcutModel == null || id == null || "".equals(id.trim())) {
			logger.error("传入的TableShortcutModel或Id为空，无法加入TableShortcutModelFactory!");
			return null;
		}
		
		if(!checkFileModel(fileModel)) {
			addFileModel(fileModel);
		}
		
		Map<String, TableShortcutModel> tableModelMap = allTableShortcutModelMap.get(fileModel);
		
		if(tableModelMap.get(id) != null) {
			logger.error("已经存在ID为：" + id + "的TableShortcutModel，添加TableShortcutModel：" + 
					tableShortcutModel.getId() + "失败！");
			return null;
		}
		
		logger.info("从文件中导入TableShortcutModel(ID:" + id + ",NAME:" + 
				tableShortcutModel.getId() +")");
		tableModelMap.put(id, tableShortcutModel);
		
		
		return id;
	}

	/**
	 * 通过ID来查找一个TableShortcutModel
	 * @param id
	 * @return
	 */
	public static TableShortcutModel getTableShortcutModel(FileModel fileModel, String id) {
		if(id == null || "".equals(id)) {
			logger.info("传入的Id为空，无法查找TableShortcutModel!");
			return null;
		}
		
		if(!checkFileModel(fileModel)) {
			return null;
		}
		
		Map<String, TableShortcutModel> tableModelMap = allTableShortcutModelMap.get(fileModel);
		
		TableShortcutModel tableModel = tableModelMap.get(id);
		if(tableModel == null) {
			logger.info("通过ID：" + id + "查找到的TableShortcutModel为空！");
		}
		
		return tableModel;
	}
	
}
