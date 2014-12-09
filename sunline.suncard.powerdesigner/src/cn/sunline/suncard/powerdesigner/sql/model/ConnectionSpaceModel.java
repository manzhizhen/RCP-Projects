/* 文件名：     ConnectionSpaceModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-11
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.sql.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 数据库连接空间模型
 * @author  Manzhizhen
 * @version 1.0, 2013-1-11
 * @see 
 * @since 1.0
 */
public class ConnectionSpaceModel {
	// 数据库连接空间中的所有连接模型的Map
	private static Map<String, ConnectionModel> connectionModelMap = 
			new LinkedHashMap<String, ConnectionModel>();

	public static Map<String, ConnectionModel> getConnectionModelMap() {
		return connectionModelMap;
	}
	
	
}
