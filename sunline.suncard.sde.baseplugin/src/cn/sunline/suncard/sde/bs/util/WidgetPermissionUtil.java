/*
 * 文件名：WidgetPermissionUtil.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：控件权限控制类
 * 修改人：tpf
 * 修改时间：2011-10-19
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
  * 控件权限控制类
  * 控件权限控制类，主要提供权限检查方法等
  * @author tpf
  * @version 1.0, 2011-10-19
  * @see
  * @since 1.0 
 */
public class WidgetPermissionUtil {

	//控件权限控制开关：true-->检查 false-->不检查
	private static boolean checkFlag = false;
	
	//控件Id的list
	private static List<String> widgetPermissionList = new ArrayList<String>();
	
	public static Map<String,String> permissionMap = new HashMap<String,String>();

	public static List<String> getWidgetPermissionList() {
		return widgetPermissionList;
	}
	public static void setWidgetPermissionList(List<String> widgetPermissionList) {
		WidgetPermissionUtil.widgetPermissionList = widgetPermissionList;
	}
	/**
	 * 检查控件是否有权限显示
	 * 检查控件是否有权限显示
	 * @param widgetId
	 * @return 是否显示
	 */
	public static boolean checkPermission(String widgetId) {
//		permissionMap.put(functionId, widgetId);
		if(checkFlag == true) {
			List<String> list = WidgetPermissionUtil.getWidgetPermissionList();
			if(list.contains(widgetId)) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
}
