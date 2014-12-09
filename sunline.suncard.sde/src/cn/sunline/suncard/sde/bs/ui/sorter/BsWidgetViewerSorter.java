/*
 * 文件名：BsDepartViewerSorter.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：部门排序器
 * 修改人：heyong
 * 修改时间：2011-10-21
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.sorter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import cn.sunline.suncard.sde.bs.entity.BsWidget;

/**
 * 控件排序器
 * 可根据控件实体类中各个字段进行排序
 * @author heyong
 * @version 1.0, 2011-10-21
 * @see 
 * @since 1.0
 */
public class BsWidgetViewerSorter extends ViewerSorter {
	//每列对应一个常量，升序为正，降序为负
	private static final int BANKORG_ID = 1;
	private static final int PC_ID = 2;
	private static final int WIDGET_ID = 3;
	private static final int WIDGET_NAME = 4;
	private static final int WIDGET_TYPE = 5;
	private static final int PARENT_WIDGET_ID = 6;
	private static final int PLUGIN_ID = 7;
	
	//外界使用排序对象
	public static final BsWidgetViewerSorter BANKORG_ID_ASC 
		= new BsWidgetViewerSorter(BANKORG_ID);
	public static final BsWidgetViewerSorter BANKORG_ID_DESC 
		= new BsWidgetViewerSorter(-BANKORG_ID);
	public static final BsWidgetViewerSorter PC_ID_ASC 
		= new BsWidgetViewerSorter(PC_ID);
	public static final BsWidgetViewerSorter PC_ID_DESC 
		= new BsWidgetViewerSorter(-PC_ID);
	public static final BsWidgetViewerSorter WIDGET_ID_ASC 
		= new BsWidgetViewerSorter(WIDGET_ID);
	public static final BsWidgetViewerSorter WIDGET_ID_DESC 
		= new BsWidgetViewerSorter(-WIDGET_ID);
	public static final BsWidgetViewerSorter WIDGET_NAME_ASC 
		= new BsWidgetViewerSorter(WIDGET_NAME);
	public static final BsWidgetViewerSorter WIDGET_NAME_DESC 
		= new BsWidgetViewerSorter(-WIDGET_NAME);
	public static final BsWidgetViewerSorter WIDGET_TYPE_ASC 
		= new BsWidgetViewerSorter(WIDGET_TYPE);
	public static final BsWidgetViewerSorter WIDGET_TYPE_DESC 
		= new BsWidgetViewerSorter(-WIDGET_TYPE);
	public static final BsWidgetViewerSorter PARENT_WIDGET_ID_ASC 
		= new BsWidgetViewerSorter(PARENT_WIDGET_ID);
	public static final BsWidgetViewerSorter PARENT_WIDGET_ID_DESC 
		= new BsWidgetViewerSorter(-PARENT_WIDGET_ID);
	public static final BsWidgetViewerSorter PLUGIN_ID_ASC 
		= new BsWidgetViewerSorter(PLUGIN_ID);
	public static final BsWidgetViewerSorter PLUGIN_ID_DESC 
		= new BsWidgetViewerSorter(-PLUGIN_ID);
	
	
	//当前排序列
	private int sortType ;
	
	private BsWidgetViewerSorter(int sortType){
		this.sortType = sortType;
	}
	
	@Override
	public int compare(Viewer viewer, Object obj1, Object obj2) {
		BsWidget o1 = (BsWidget) obj1;
		BsWidget o2 = (BsWidget) obj2;
		
		switch (this.sortType) {
		case BANKORG_ID:
			return o1.getId().getBankorgId().compareTo(o2.getId().getBankorgId());
		case -BANKORG_ID:
			return o2.getId().getBankorgId().compareTo(o1.getId().getBankorgId());
		case PC_ID:
			return o1.getId().getPcId().compareTo(o2.getId().getPcId());
		case -PC_ID:
			return o2.getId().getPcId().compareTo(o1.getId().getPcId());
		case WIDGET_ID:
			return o1.getId().getWidgetId().compareTo(o2.getId().getWidgetId());
		case -WIDGET_ID:
			return o2.getId().getWidgetId().compareTo(o1.getId().getWidgetId());
		case WIDGET_NAME:
			return o1.getWidgetName().compareTo(o2.getWidgetName());
		case -WIDGET_NAME:
			return o2.getWidgetName().compareTo(o1.getWidgetName());
		case PARENT_WIDGET_ID:
			if(o1.getParWidgetId() != null && o2.getParWidgetId() != null){
				return o1.getParWidgetId().compareTo(o2.getParWidgetId());
			}else{
				return 0;
			}
		case -PARENT_WIDGET_ID:
			if(o1.getParWidgetId() != null && o2.getParWidgetId() != null){
				return o2.getParWidgetId().compareTo(o1.getParWidgetId());
			}else{
				return 0;
			}
		case WIDGET_TYPE:
			return o1.getWidgetType().compareTo(o2.getWidgetType());
		case -WIDGET_TYPE:
			return o2.getWidgetType().compareTo(o1.getWidgetType());
		case PLUGIN_ID:
			if (o1.getPluginId() != null && o2.getPluginId() != null){
				return o1.getPluginId().compareTo(o2.getPluginId());
			}else{
				return 0;
			}
		case -PLUGIN_ID:
			if (o1.getPluginId() != null && o2.getPluginId() != null){
				return o2.getPluginId().compareTo(o1.getPluginId());
			}else {
				return 0;
			}
		}
		return 0;
	}
}
