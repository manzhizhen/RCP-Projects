/* 文件名：     PorjectSpaceModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-28
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.commands.CommandStack;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 项目空间模型
 * @author  Manzhizhen
 * @version 1.0, 2012-12-28
 * @see 
 * @since 1.0
 */
public class ProjectSpaceModel {
	// 存放了一个项目空间中的所有项目
	private static Map<String, ProjectGroupModel> projectGroupModelMap = new 
				HashMap<String, ProjectGroupModel>();
	
	private static Log logger = LogManager.getLogger(ProductSpaceModel.class.getName());

	public static Map<String, ProjectGroupModel> getProjectGroupModelMap() {
		return projectGroupModelMap;
	}

}
