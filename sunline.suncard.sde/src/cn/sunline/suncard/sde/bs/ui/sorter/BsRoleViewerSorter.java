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

import cn.sunline.suncard.sde.bs.entity.BsRole;

/**
 * 部门排序器
 * 可根据部门实体类中各个字段进行排序
 * @author heyong
 * @version 1.0, 2011-10-21
 * @see 
 * @since 1.0
 */
public class BsRoleViewerSorter extends ViewerSorter {
	//每列对应一个常量，升序为正，降序为负
	private static final int BANKORG_ID = 1;
	private static final int PC_ID = 2;
	private static final int ROLE_ID = 3;
	private static final int ROLE_NAME = 4;
	
	//外界使用排序对象
	public static final BsRoleViewerSorter BANKORG_ID_ASC 
		= new BsRoleViewerSorter(BANKORG_ID);
	public static final BsRoleViewerSorter BANKORG_ID_DESC 
		= new BsRoleViewerSorter(-BANKORG_ID);
	public static final BsRoleViewerSorter PC_ID_ASC 
		= new BsRoleViewerSorter(PC_ID);
	public static final BsRoleViewerSorter PC_ID_DESC 
		= new BsRoleViewerSorter(-PC_ID);
	public static final BsRoleViewerSorter ROLE_ID_ASC 
		= new BsRoleViewerSorter(ROLE_ID);
	public static final BsRoleViewerSorter ROLE_ID_DESC 
		= new BsRoleViewerSorter(-ROLE_ID);
	public static final BsRoleViewerSorter ROLE_NAME_ASC 
		= new BsRoleViewerSorter(ROLE_NAME);
	public static final BsRoleViewerSorter ROLE_NAME_DESC 
		= new BsRoleViewerSorter(-ROLE_NAME);
	
	//当前排序列
	private int sortType ;
	
	private BsRoleViewerSorter(int sortType){
		this.sortType = sortType;
	}
	
	@Override
	public int compare(Viewer viewer, Object obj1, Object obj2) {
		BsRole o1 = (BsRole) obj1;
		BsRole o2 = (BsRole) obj2;
		
		switch (this.sortType) {
		case BANKORG_ID:
			return o1.getId().getBankorgId().compareTo(o2.getId().getBankorgId());
		case -BANKORG_ID:
			return o2.getId().getBankorgId().compareTo(o1.getId().getBankorgId());
		case PC_ID:
			return o1.getId().getPcId().compareTo(o2.getId().getPcId());
		case -PC_ID:
			return o2.getId().getPcId().compareTo(o1.getId().getPcId());
		case ROLE_ID:
			return o1.getId().getRoleId().compareTo(o2.getId().getRoleId());
		case -ROLE_ID:
			return o2.getId().getRoleId().compareTo(o1.getId().getRoleId());
		case ROLE_NAME:
			return o1.getRoleName().compareTo(o2.getRoleName());
		case -ROLE_NAME:
			return o2.getRoleName().compareTo(o1.getRoleName());
		}
		return 0;
	}
}
