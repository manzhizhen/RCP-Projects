/* 文件名：     WorkSpaceModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-5
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;

import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 工作空间模型
 * 一个工作空间模型可以包含多个文件模型（FileModel）
 * @author  Manzhizhen
 * @version 1.0, 2012-9-5
 * @see 
 * @since 1.0
 */
public class WorkSpaceModel{
	// 存放了一个工作空间中的所有文件模型
	private static Set<FileModel> fileModelSet = new HashSet<FileModel>();

	public static final Set<FileModel> getFileModelSet() {
		return fileModelSet;
		
	}
	

}
