/* 文件名：     ColumnModelFactory.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-10-31
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class LineXmlPropertyFactory {
	private static Map<FileModel, List<String>> allLineXmlPropertyMap = new HashMap<FileModel, List<String>>();
	
	private static Log logger = LogManager.getLogger(LineXmlPropertyFactory.class
			.getName());
	
	public static void addFileModel(FileModel fileModel) {
		if(fileModel == null) {
			logger.error("要添加的FileModel为空，添加失败！");
			return ;
		}
		
		if(allLineXmlPropertyMap.get(fileModel) != null) {
			logger.warn("该FileModel已经在allLineXmlPropertyMap中存在，添加失败！");
			return ;
		}
		
		allLineXmlPropertyMap.put(fileModel, new ArrayList<String>());
	}
	
	/**
	 * 检查该FileModel在Map中是否存在
	 * @param fileModel
	 * @return
	 */
	private static boolean checkFileModel(FileModel fileModel) {
		if(fileModel == null || allLineXmlPropertyMap.get(fileModel) == null) {
			logger.equals("该FileModel在allLineXmlPropertyMap中不存在！");
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
		if(fileModel == null || allLineXmlPropertyMap.get(fileModel) == null) {
			logger.equals("该FileModel在allLineXmlPropertyMap中不存在！");
			return false;
		}
		
		allLineXmlPropertyMap.remove(fileModel);
		
		return true;
	}
	
	/**
	 * 往Map中添加一个已经使用的连接线ID，只限于导入文件的时候使用
	 * @param fileModel
	 * @param lineId 
	 * @return String 新的ColumnModel对应的id，如果返回为null，说明添加失败！
	 */
	public static void addLineId(FileModel fileModel, String lineId) {
		if(lineId == null || fileModel == null) {
			logger.error("传入的连接线ID或FileModel为空，无法加入allLineXmlPropertyMap!");
			return ;
		}
		
		if(!checkFileModel(fileModel)) {
			addFileModel(fileModel);
		}
		
		List<String> idList = allLineXmlPropertyMap.get(fileModel);
		idList.add(lineId);
		logger.info("成功向LineXmlPropertyFactory中添加了一个连接线ID：" + lineId);
	}
	
	/**
	 * 获得一个新的连接线ID
	 * @param fileModel
	 * @return
	 */
	public static String addNewLineIdToFactory(FileModel fileModel) {
		if(fileModel == null) {
			logger.error("传入的FileModel为空，无法生成新的连接线ID!");
			return null;
		}
		
		if(!checkFileModel(fileModel)) {
			addFileModel(fileModel);
		}
		
		List<String> idList = allLineXmlPropertyMap.get(fileModel);
		
		String id = "LINE_ID_";
		for(int index = 1; ;index++) {
			if(!idList.contains(id + index)) {
				id += index;
				
				addLineId(fileModel, id);
				return id;
			}
		}
		
	}
	

	
}
