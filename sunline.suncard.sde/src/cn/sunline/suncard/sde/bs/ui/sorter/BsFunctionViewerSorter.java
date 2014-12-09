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

import cn.sunline.suncard.sde.bs.entity.BsFunction;

/**
 * 部门排序器
 * 可根据部门实体类中各个字段进行排序
 * @author heyong
 * @version 1.0, 2011-10-21
 * @see 
 * @since 1.0
 */
public class BsFunctionViewerSorter extends ViewerSorter {
	//每列对应一个常量，升序为正，降序为负
	private static final int BANKORG_ID = 1;
	private static final int PC_ID = 2;
	private static final int FUNCTION_ID = 3;
	private static final int FUNCTION_NAME = 4;
	
	//外界使用排序对象
	public static final BsFunctionViewerSorter BANKORG_ID_ASC 
		= new BsFunctionViewerSorter(BANKORG_ID);
	public static final BsFunctionViewerSorter BANKORG_ID_DESC 
		= new BsFunctionViewerSorter(-BANKORG_ID);
	public static final BsFunctionViewerSorter PC_ID_ASC 
		= new BsFunctionViewerSorter(PC_ID);
	public static final BsFunctionViewerSorter PC_ID_DESC 
		= new BsFunctionViewerSorter(-PC_ID);
	public static final BsFunctionViewerSorter FUNCTION_ID_ASC 
		= new BsFunctionViewerSorter(FUNCTION_ID);
	public static final BsFunctionViewerSorter FUNCTION_ID_DESC 
		= new BsFunctionViewerSorter(-FUNCTION_ID);
	public static final BsFunctionViewerSorter FUNCTION_NAME_ASC 
		= new BsFunctionViewerSorter(FUNCTION_NAME);
	public static final BsFunctionViewerSorter FUNCTION_NAME_DESC 
		= new BsFunctionViewerSorter(-FUNCTION_NAME);
	
	//当前排序列
	private int sortType ;
	
	private BsFunctionViewerSorter(int sortType){
		this.sortType = sortType;
	}
	
	@Override
	public int compare(Viewer viewer, Object obj1, Object obj2) {
		BsFunction o1 = (BsFunction) obj1;
		BsFunction o2 = (BsFunction) obj2;
		
		switch (this.sortType) {
		case BANKORG_ID:
			return o1.getId().getBankorgId().compareTo(o2.getId().getBankorgId());
		case -BANKORG_ID:
			return o2.getId().getBankorgId().compareTo(o1.getId().getBankorgId());
		case PC_ID:
			return o1.getId().getPcId().compareTo(o2.getId().getPcId());
		case -PC_ID:
			return o2.getId().getPcId().compareTo(o1.getId().getPcId());
		case FUNCTION_ID:
			return o1.getId().getFunctionId().compareTo(o2.getId().getFunctionId());
		case -FUNCTION_ID:
			return o2.getId().getFunctionId().compareTo(o1.getId().getFunctionId());
		case FUNCTION_NAME:
			return o1.getFunctionName().compareTo(o2.getFunctionName());
		case -FUNCTION_NAME:
			return o2.getFunctionName().compareTo(o1.getFunctionName());
		}
		return 0;
	}
}
