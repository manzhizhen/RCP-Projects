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

import cn.sunline.suncard.sde.bs.entity.BsDepartment;

/**
 * 部门排序器
 * 可根据部门实体类中各个字段进行排序
 * @author heyong
 * @version 1.0, 2011-10-21
 * @see 
 * @since 1.0
 */
public class BsDepartmentViewerSorter extends ViewerSorter {
	//每列对应一个常量，升序为正，降序为负
	private static final int BANKORG_ID = 1;
	private static final int DEPARTMENT_ID = 2;
	private static final int DEPARTMENT_NAME = 3;
	
	//外界使用排序对象
	public static final BsDepartmentViewerSorter BANKORG_ID_ASC 
		= new BsDepartmentViewerSorter(BANKORG_ID);
	public static final BsDepartmentViewerSorter BANKORG_ID_DESC 
		= new BsDepartmentViewerSorter(-BANKORG_ID);
	public static final BsDepartmentViewerSorter DEPARTMENT_ID_ASC 
		= new BsDepartmentViewerSorter(DEPARTMENT_ID);
	public static final BsDepartmentViewerSorter DEPARTMENT_ID_DESC 
		= new BsDepartmentViewerSorter(-DEPARTMENT_ID);
	public static final BsDepartmentViewerSorter DEPARTMENT_NAME_ASC 
		= new BsDepartmentViewerSorter(DEPARTMENT_NAME);
	public static final BsDepartmentViewerSorter DEPARTMENT_NAME_DESC 
		= new BsDepartmentViewerSorter(-DEPARTMENT_NAME);
	
	//当前排序列
	private int sortType ;
	
	private BsDepartmentViewerSorter(int sortType){
		this.sortType = sortType;
	}
	
	@Override
	public int compare(Viewer viewer, Object obj1, Object obj2) {
		BsDepartment o1 = (BsDepartment) obj1;
		BsDepartment o2 = (BsDepartment) obj2;
		
		switch (this.sortType) {
		case BANKORG_ID:
			return o1.getId().getBankorgId().compareTo(o2.getId().getBankorgId());
		case -BANKORG_ID:
			return o2.getId().getBankorgId().compareTo(o1.getId().getBankorgId());
		case DEPARTMENT_ID:
			return o1.getId().getDepartmentId().compareTo(o2.getId().getDepartmentId());
		case -DEPARTMENT_ID:
			return o2.getId().getDepartmentId().compareTo(o1.getId().getDepartmentId());
		case DEPARTMENT_NAME:
			return o1.getDepartmentName().compareTo(o2.getDepartmentName());
		case -DEPARTMENT_NAME:
			return o2.getDepartmentName().compareTo(o1.getDepartmentName());
		}
		return 0;
	}
}
