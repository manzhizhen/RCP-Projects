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

import cn.sunline.suncard.sde.bs.entity.BsUser;

/**
 * 部门排序器
 * 可根据部门实体类中各个字段进行排序
 * @author heyong
 * @version 1.0, 2011-10-21
 * @see 
 * @since 1.0
 */
public class BsUserViewerSorter extends ViewerSorter {
	//每列对应一个常量，升序为正，降序为负
	private static final int BANKORG_ID = 1;
	private static final int USER_ID = 2;
	private static final int USER_NAME = 3;
	private static final int DEPARTMENT_ID = 4;
	private static final int USER_STATUS = 5;
	private static final int LAST_LOGIN_DATE = 6;
	
	//外界使用排序对象
	public static final BsUserViewerSorter BANKORG_ID_ASC 
		= new BsUserViewerSorter(BANKORG_ID);
	public static final BsUserViewerSorter BANKORG_ID_DESC 
		= new BsUserViewerSorter(-BANKORG_ID);
	public static final BsUserViewerSorter USER_ID_ASC 
		= new BsUserViewerSorter(USER_ID);
	public static final BsUserViewerSorter USER_ID_DESC 
		= new BsUserViewerSorter(-USER_ID);
	public static final BsUserViewerSorter USER_NAME_ASC 
		= new BsUserViewerSorter(USER_NAME);
	public static final BsUserViewerSorter USER_NAME_DESC 
		= new BsUserViewerSorter(-USER_NAME);
	public static final BsUserViewerSorter DEPARTMENT_ID_ASC 
		= new BsUserViewerSorter(DEPARTMENT_ID);
	public static final BsUserViewerSorter DEPARTMENT_ID_DESC 
		= new BsUserViewerSorter(-DEPARTMENT_ID);
	public static final BsUserViewerSorter USER_STATUS_ASC 
		= new BsUserViewerSorter(USER_STATUS);
	public static final BsUserViewerSorter USER_STATUS_DESC 
		= new BsUserViewerSorter(-USER_STATUS);
	public static final BsUserViewerSorter LAST_LOGIN_DATE_ASC 
		= new BsUserViewerSorter(LAST_LOGIN_DATE);
	public static final BsUserViewerSorter LAST_LOGIN_DATE_DESC 
		= new BsUserViewerSorter(-LAST_LOGIN_DATE);
	
	//当前排序列
	private int sortType ;
	
	private BsUserViewerSorter(int sortType){
		this.sortType = sortType;
	}
	
	@Override
	public int compare(Viewer viewer, Object obj1, Object obj2) {
		BsUser o1 = (BsUser) obj1;
		BsUser o2 = (BsUser) obj2;
		
		switch (this.sortType) {
		case BANKORG_ID:
			return o1.getId().getBankorgId().compareTo(o2.getId().getBankorgId());
		case -BANKORG_ID:
			return o2.getId().getBankorgId().compareTo(o1.getId().getBankorgId());
		case USER_ID:
			return o1.getId().getUserId().compareTo(o2.getId().getUserId());
		case -USER_ID:
			return o2.getId().getUserId().compareTo(o1.getId().getUserId());
		case USER_NAME:
			return o1.getUserName().compareTo(o2.getUserName());
		case -USER_NAME:
			return o2.getUserName().compareTo(o1.getUserName());
		case DEPARTMENT_ID:
			return o1.getDepartmentId().compareTo(o2.getDepartmentId());
		case -DEPARTMENT_ID:
			return o2.getDepartmentId().compareTo(o1.getDepartmentId());
		case USER_STATUS:
			return o1.getUserStatus().compareTo(o2.getUserStatus());
		case -USER_STATUS:
			return o2.getUserStatus().compareTo(o1.getUserStatus());
		case LAST_LOGIN_DATE:
			if (o1.getLasetLogginDate() != null){
				return o1.getLasetLogginDate().compareTo(o2.getLasetLogginDate());
			}
			return 0;
		case -LAST_LOGIN_DATE:
			if (o2.getLasetLogginDate() != null){
				return o2.getLasetLogginDate().compareTo(o1.getLasetLogginDate());
			}
			return 0;
		}
		return 0;
	}
}
