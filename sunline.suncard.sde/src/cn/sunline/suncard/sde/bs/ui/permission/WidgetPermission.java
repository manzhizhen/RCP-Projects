/*
 * 文件名：
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：tpf
 * 修改时间：2011-10-19
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.permission;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.sunline.suncard.sde.bs.biz.BsFuncmappingBiz;
import cn.sunline.suncard.sde.bs.biz.BsRolemappingBiz;
import cn.sunline.suncard.sde.bs.biz.BsUsermappingBiz;
import cn.sunline.suncard.sde.bs.biz.BsWidgetBiz;
import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsFuncmapping;
import cn.sunline.suncard.sde.bs.entity.BsFunction;
import cn.sunline.suncard.sde.bs.entity.BsFunctionId;
import cn.sunline.suncard.sde.bs.entity.BsRole;
import cn.sunline.suncard.sde.bs.entity.BsRoleId;
import cn.sunline.suncard.sde.bs.entity.BsRolemapping;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.entity.BsUsermapping;
import cn.sunline.suncard.sde.bs.entity.BsWidget;
import cn.sunline.suncard.sde.bs.entity.BsWidgetId;
import cn.sunline.suncard.sde.bs.system.Context;

public class WidgetPermission {
	
	BsUsermappingBiz usermappingBiz = new BsUsermappingBiz();
	BsRolemappingBiz rolemappingBiz = new BsRolemappingBiz();
	BsFuncmappingBiz funcmappingBiz = new BsFuncmappingBiz();
	BsWidgetBiz widgetBiz = new BsWidgetBiz();
	
	//保存所有有权限的控件ID
	private List widgetList = new ArrayList();
	
	/**
	 * 获取不重复的控件id
	 * 
	 * @return 不重复的控件id
	 */
	public List<String> initPermissionList() {
		List<String> oldList = getWidgetIdList();
		 Set<String> set = new HashSet<String>();
		 List<String> newList = new ArrayList<String>();
		 for (Iterator<String> iter = oldList.iterator(); iter.hasNext();) {
			 String element = iter.next();
			 if (set.add(element)) {
				 newList.add((String)element);
			 }
		}
		return newList;
	}
	
	/**
	 * 获取所有满足条件的控件信息
	 * 
	 * @return 控件id
	 */
	private List<String> getWidgetIdList() {
		
		List<String> widgetIdList = new ArrayList<String>();
		//得到当前登录用户
		BsUser user = (BsUser) Context.getSessionMap().get(Constants.CURRENT_USER);
		List<BsUsermapping> usermappings = usermappingBiz.getAllByUser(user);
		if (usermappings != null && usermappings.size() > 0){
			//通过用户角色映射信息查找角色功能映射信息
			List<BsRolemapping> rolemappings = findUsermappingByRole(usermappings);
			//通过角色功能映射信息查询功能控件映射信息
			List<BsFuncmapping> funcmappings = findFuncmappingByFunction(rolemappings);
			//通过功能控件映射查询控件信息
			List<BsWidget> widgetList = findWidgetByFuncmapping(funcmappings);
			//取出所有控件ID
			for(BsWidget widget : widgetList) {
				if(widget != null) {
					widgetIdList.add(widget.getId().getWidgetId());
				}
			}
		}
		return widgetIdList;
	}
	
	/**
	 * 查询角色功能映射信息
	 * 通过用户角色映射信息查找角色功能映射信息
	 * @param usermappings 用户角色映射信息
	 * @return rolemappings 色功能映射信息
	 */
	private List<BsRolemapping> findUsermappingByRole(List<BsUsermapping> usermappings) {
		List<BsRolemapping> rolemappings = new ArrayList<BsRolemapping>();
		for (int i=0; i<usermappings.size(); i++){
			BsUsermapping usermapping = usermappings.get(i);
			BsRole bsRole = new BsRole(new BsRoleId());
			bsRole.getId().setBankorgId(usermapping.getId().getBankorgId());
			bsRole.getId().setPcId(usermapping.getId().getPcId());
			bsRole.getId().setRoleId(usermapping.getId().getRoleId());
			//根据角色得到其所有角色与功能映射
			List<BsRolemapping> bsRolemappings = rolemappingBiz.getAllByRole(bsRole);
			rolemappings.addAll(bsRolemappings);
		}
		return rolemappings;
	}
	
	/**
	 * 查询功能控件映射信息
	 * 通过角色功能映射信息查询功能控件映射信息
	 * @param rolemappings 角色功能映射信息
	 * @return funcmappings 功能控件映射信息
	 */
	private List<BsFuncmapping> findFuncmappingByFunction(List<BsRolemapping> rolemappings) {
		List<BsFuncmapping> funcmappings = new ArrayList<BsFuncmapping>();
		for(BsRolemapping rolemapping : rolemappings) {
			BsFunction function = new BsFunction(new BsFunctionId());
			function.getId().setBankorgId(rolemapping.getId().getBankorgId());
			function.getId().setPcId(rolemapping.getId().getPcId());
			function.getId().setFunctionId(rolemapping.getId().getFunctionId());
			List<BsFuncmapping> bsFuncmappings = funcmappingBiz.getAllByFunction(function);
			funcmappings.addAll(bsFuncmappings);
		}
		return funcmappings;
	}
	
	/**
	 * 查询出所有满足条件的控件列表
	 * 
	 * @param funcmappings
	 * @return
	 */
	private List<BsWidget> findWidgetByFuncmapping(List<BsFuncmapping> funcmappings) {
		List<BsWidget> widgetList = new ArrayList<BsWidget>();
		List<BsWidget> widgetSonList = null;
		for(BsFuncmapping funcmapping : funcmappings) {
			BsWidgetId widgetId = new BsWidgetId();
			widgetId.setBankorgId(funcmapping.getId().getBankorgId());
			widgetId.setPcId(funcmapping.getId().getPcId());
			widgetId.setWidgetId(funcmapping.getId().getWidgetId());
			BsWidget widget = widgetBiz.findByPk(widgetId);
			widgetList.add(widget);
			if(Constants.MAPPING_TYPE_A.equals(funcmapping.getMappingType())) {
				//A-添加该控件，如果该控件为父控件，同时添加全部子控件
				widgetSonList = widgetBiz.findWidgetByPar(funcmapping.getId().getWidgetId());
				widgetList.addAll(widgetSonList);
			} else if(Constants.MAPPING_TYPE_S.equals(funcmapping.getMappingType())) {
				//S-添加该控件，如果该控件为父控件，不添加子控件
			} else if(Constants.MAPPING_TYPE_D.equals(funcmapping.getMappingType())) {
				//D-删除该控件，如果该控件为父控件，同时删除全部子控件
				//widgetSonList = widgetBiz.findWidgetByPar(funcmapping.getId().getWidgetId());
			}
		}
		return widgetList;
	}
	
}
