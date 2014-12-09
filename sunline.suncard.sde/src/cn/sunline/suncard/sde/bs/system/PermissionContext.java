/*
 * 文件名：PermissionContext.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：权限工具类
 * 修改人：heyong
 * 修改时间：2011-10-13
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.system;

import java.util.ArrayList;
import java.util.List;

import cn.sunline.suncard.sde.bs.biz.BsRolemappingBiz;
import cn.sunline.suncard.sde.bs.biz.BsUsermappingBiz;
import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsRole;
import cn.sunline.suncard.sde.bs.entity.BsRoleId;
import cn.sunline.suncard.sde.bs.entity.BsRolemapping;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.entity.BsUsermapping;

/**
 * 权限工具类
 * 根据当前登录用户，得到用户所有权限，然后验证,此类为单例类
 * @author heyong
 * @version 1.0, 2011-10-22
 * @see 
 * @since 1.0
 */
public class PermissionContext{
	//是否初始化，默认为false
	private boolean hasInit = false;
	//权限集合，用于存储用户所有权限
	private List<BsRolemapping> permissions = new ArrayList<BsRolemapping>();
	//本类实例
	private static PermissionContext instance = new PermissionContext();
	
	private PermissionContext(){
		init();
	}
	//返回类创建的实例
	public static PermissionContext getInstance(){
		return instance;
	}
	/**
	 * 初始化方法，根据用户取得所有权限，并存储到权限集合中，初始化后，hasInit值为true
	 */
	private void init(){
		BsUsermappingBiz usermappingBiz = new BsUsermappingBiz();
		BsRolemappingBiz rolemappingBiz = new BsRolemappingBiz();
		
		//得到当前登录用户
		BsUser user = (BsUser) Context.getSessionMap().get(Constants.CURRENT_USER);
		List<BsUsermapping> usermappings = usermappingBiz.getAllByUser(user);
		if (usermappings != null && usermappings.size() > 0){
			for (int i=0; i<usermappings.size(); i++){
				BsUsermapping usermapping = usermappings.get(i);
				BsRole bsRole = new BsRole(new BsRoleId());
				bsRole.getId().setBankorgId(usermapping.getId().getBankorgId());
				bsRole.getId().setPcId(usermapping.getId().getPcId());
				bsRole.getId().setRoleId(usermapping.getId().getRoleId());
				//根据角色得到其所有角色与功能映射
				List<BsRolemapping> rolemappings = rolemappingBiz.getAllByRole(bsRole);
				permissions.addAll(rolemappings);
			}
		}
		hasInit = true;
	}
	
	
	
	public boolean checkPermission(String functionId){
		//如果没有初始化权限
		if (!hasInit){
			init();
		}
		//如果权限为空，即没有权限
		if (permissions.size() == 0){
			return false;
		}
		for (int i=0; i<permissions.size(); i++){
			BsRolemapping rolemapping = permissions.get(i);
			if (rolemapping.getId().getFunctionId().equals(functionId) 
					&& rolemapping.getId().getPcId().equals(Context.getSessionMap().get(Constants.PC_ID))
					&& rolemapping.getId().getBankorgId().equals(Context.getSessionMap().get(Constants.BANKORG_ID))){
				return true;
			}
		}
		return false;
	}
}