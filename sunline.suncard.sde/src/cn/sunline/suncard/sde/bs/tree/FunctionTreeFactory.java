/*
 * 文件名：FunctionTreeFactory.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：创建树的结构
 * 修改人：周兵
 * 修改时间：2011-10-13
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.tree;

import java.util.ArrayList;
import java.util.List;

import cn.sunline.suncard.sde.bs.biz.BsDepartmentBiz;
import cn.sunline.suncard.sde.bs.biz.BsFunctionBiz;
import cn.sunline.suncard.sde.bs.biz.BsRoleBiz;
import cn.sunline.suncard.sde.bs.biz.BsUserBiz;
import cn.sunline.suncard.sde.bs.entity.BsDepartment;
import cn.sunline.suncard.sde.bs.entity.BsFunction;
import cn.sunline.suncard.sde.bs.entity.BsRole;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.entity.BsUsermapping;
import cn.sunline.suncard.sde.bs.system.PermissionContext;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 创建树的结构
 * @author zhoub
 * @version 1.0, 2011-10-24
 * @see 
 * @since 1.0
 */
public class FunctionTreeFactory {
	
	public static List<FunctionTree> createTreeData() {
		List<FunctionTree> functionTrees = new ArrayList<FunctionTree>();
		
		FunctionTree functionTree = null;
		//用户管理
		if (PermissionContext.getInstance().checkPermission("usermang")){
			functionTree = new FunctionTree(I18nUtil.getMessage("USER_MANAGER"),new BsUserBiz().getAll(),BsUser.class);
			functionTrees.add(functionTree);
		}
		//部门管理
		if (PermissionContext.getInstance().checkPermission("departmang")){
			functionTree = new FunctionTree(I18nUtil.getMessage("DEPART_MANAGER"), new BsDepartmentBiz().getAll(), BsDepartment.class);
			functionTrees.add(functionTree);
		}
		//角色管理
		if (PermissionContext.getInstance().checkPermission("rolemang")){
			functionTree = new FunctionTree(I18nUtil.getMessage("ROLE_MANAGER"), new BsRoleBiz().getAll(), BsRole.class);
			functionTrees.add(functionTree);
		}
		//功能管理
		if (PermissionContext.getInstance().checkPermission("funcmang")){
			functionTree = new FunctionTree(I18nUtil.getMessage("FUNCTION_MANAGER"), new BsFunctionBiz().getAll(), BsFunction.class);
			functionTrees.add(functionTree);
		}
		//权限设置
		if (PermissionContext.getInstance().checkPermission("permmenu")){
			functionTree = new FunctionTree(I18nUtil.getMessage("PEMISSION_SET"), null, BsUsermapping.class);
			functionTrees.add(functionTree);
		}
		
		if (functionTrees.size() == 0){
			functionTree = new FunctionTree(I18nUtil.getMessage("null"), null, FunctionTree.class);
			functionTrees.add(functionTree);
		}
		//权限管理
		functionTree = new FunctionTree(I18nUtil.getMessage("funcnavigation"), functionTrees, null);
		
		List<FunctionTree> trees = new ArrayList<FunctionTree>();
		trees.add(functionTree);
		
 		return trees;
		
	}

}
