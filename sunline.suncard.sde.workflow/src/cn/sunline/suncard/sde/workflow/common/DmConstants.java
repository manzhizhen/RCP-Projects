/*
 * 文件名：DmConstants.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：<描述>
 * 修改人：tpf
 * 修改时间：2011-10-30
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.workflow.common;

import java.io.File;

import cn.sunline.suncard.sde.workflow.Activator;


/**
 * @author    易振强
 * @version   1.0  2011-10-30
 * @see       [相关类/方法]
 * @since     [产品/模块版本] 
 */
public interface DmConstants {

	public static final String DM_APPLICATION_ID = Activator.PLUGIN_ID;
	
	// 工作流对象存放位置
	public final static String WORK_FLOW_DATA_FILE_PATH = "workflowdata" + File.separator;
	
	// 工作流对象存放文件的扩展名
	public final static String WORK_FLOW_DATA_FILE_EXTNAME = ".xml";
	
	// ACTION对象存放位置
	public final static String ACTION_DATA_FILE_PATH = "actiondata" + File.separator;
	
	// ACTION对象存放文件的扩展名
	public final static String ACTION_DATA_FILE_EXTNAME = ".xml";
	
	// 工作流对象ID前两个字母
	public final static String WORK_FLOW_ID = "WF";
	
	// 工作流树的根节点ID
	public final static String WORK_FLOW_TREE_ROOT_ID = "~work_flow_tree";
	
	// Action树的根节点ID
	public final static String ACTION_TREE_ROOT_ID = "~action_tree";
}
