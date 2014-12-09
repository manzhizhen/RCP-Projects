/*
 * 文件名：WidgetMappingType.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：映射类型枚举类
 * 修改人：heyong
 * 修改时间：2011-10-20
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.entity;

import java.util.ArrayList;

import java.util.List;

import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 *  A-添加该控件，如果该控件为父控件，同时添加全部子控件
 *  S-添加该控件，如果该控件为父控件，不添加子控件
 *  D-删除该控件，如果该控件为父控件，同时删除全部子控件
 * 
 * @author heyong
 * @version 1.0, 2011-10-20
 * @see 
 * @since 1.0
 */
public enum WidgetMappingType {
	AA, SS, DD;
	public static String[] names(){
		WidgetMappingType[] mappingTypes = WidgetMappingType.values();
		List<String> names = new ArrayList<String>();
		for (int i=0; i<mappingTypes.length; i++){
			names.add(I18nUtil.getMessage(mappingTypes[i].name()));
		}
		return names.toArray(new String[]{});
	}
	
}
