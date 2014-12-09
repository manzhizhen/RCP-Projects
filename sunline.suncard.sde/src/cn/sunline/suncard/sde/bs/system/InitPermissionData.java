/*
 * 文件名：
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：heyong
 * 修改时间：2011-10-19
 * 修改内容：新增
 * 修改人：tpf
 * 修改时间：2011-10-20
 * 修改内容：添加控件权限初始化工作
 */
package cn.sunline.suncard.sde.bs.system;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.dao.BsFuncmappingDao;
import cn.sunline.suncard.sde.bs.dao.BsFunctionDao;
import cn.sunline.suncard.sde.bs.dao.BsRoleDao;
import cn.sunline.suncard.sde.bs.dao.BsRolemappingDao;
import cn.sunline.suncard.sde.bs.dao.BsUserDao;
import cn.sunline.suncard.sde.bs.dao.BsUsermappingDao;
import cn.sunline.suncard.sde.bs.dao.BsWidgetDao;
import cn.sunline.suncard.sde.bs.entity.BsFuncmapping;
import cn.sunline.suncard.sde.bs.entity.BsFuncmappingId;
import cn.sunline.suncard.sde.bs.entity.BsFunction;
import cn.sunline.suncard.sde.bs.entity.BsFunctionId;
import cn.sunline.suncard.sde.bs.entity.BsRole;
import cn.sunline.suncard.sde.bs.entity.BsRoleId;
import cn.sunline.suncard.sde.bs.entity.BsRolemapping;
import cn.sunline.suncard.sde.bs.entity.BsRolemappingId;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.entity.BsUserId;
import cn.sunline.suncard.sde.bs.entity.BsUsermapping;
import cn.sunline.suncard.sde.bs.entity.BsUsermappingId;
import cn.sunline.suncard.sde.bs.entity.BsWidget;
import cn.sunline.suncard.sde.bs.entity.BsWidgetId;
import cn.sunline.suncard.sde.bs.ui.resource.IWidgetId;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;

public class InitPermissionData {

	static BsFunctionDao functionDao = new BsFunctionDao();
	static BsRoleDao roleDao = new BsRoleDao();
	static BsRolemappingDao rolemappingDao = new BsRolemappingDao();
	static BsUsermappingDao usermappingDao = new BsUsermappingDao();
	static BsUserDao userDao = new BsUserDao();
	static BsWidgetDao widgetDao = new BsWidgetDao();
	static BsFuncmappingDao funcmappingDao = new BsFuncmappingDao();
	
	static long default_bankorg_id = Long.parseLong(System.getProperty(Constants.BANKORG_ID));
	static String default_pc_id = "";
	
	static Map<String, String> widgetMap = new HashMap<String, String>();
	
	static {
		try {
			default_pc_id = ComputerInfo.getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		widgetMap.put("SDE_W_DEPT", IWidgetId.SDE_W_DEPT);
		widgetMap.put("SDE_B_DEPT_CANCEL", IWidgetId.SDE_B_DEPT_CANCEL);
		widgetMap.put("SDE_B_DEPT_COMMIT", IWidgetId.SDE_B_DEPT_COMMIT);
		widgetMap.put("SDE_T_DEPT_BANKORGID_TEXT", IWidgetId.SDE_T_DEPT_BANKORGID_TEXT);
		widgetMap.put("SDE_T_DEPT_DEPTID_TEXT", IWidgetId.SDE_T_DEPT_DEPTID_TEXT);
		widgetMap.put("SDE_T_DEPT_DEPTNAME_TEXT", IWidgetId.SDE_T_DEPT_DEPTNAME_TEXT);
		
		widgetMap.put("SDE_W_FUNC", IWidgetId.SDE_W_FUNC);
		widgetMap.put("SDE_B_FUNC_CANCEL", IWidgetId.SDE_B_FUNC_CANCEL);
		widgetMap.put("SDE_B_FUNC_COMMIT", IWidgetId.SDE_B_FUNC_COMMIT);
		widgetMap.put("SDE_T_FUNC_BANKORGID_TEXT", IWidgetId.SDE_T_FUNC_BANKORGID_TEXT);
		widgetMap.put("SDE_T_FUNC_FUNID_TEXT", IWidgetId.SDE_T_FUNC_FUNID_TEXT);
		widgetMap.put("SDE_T_FUNC_FUNNAME_TEXT", IWidgetId.SDE_T_FUNC_FUNNAME_TEXT);
		widgetMap.put("SDE_T_FUNC_PCID_TEXT", IWidgetId.SDE_T_FUNC_PCID_TEXT);
		
		widgetMap.put("SDE_W_ROLE", IWidgetId.SDE_W_ROLE);
		widgetMap.put("SDE_B_ROLE_CANCEL", IWidgetId.SDE_B_ROLE_CANCEL);
		widgetMap.put("SDE_B_ROLE_COMMIT", IWidgetId.SDE_B_ROLE_COMMIT);
		widgetMap.put("SDE_T_ROLE_BANKORGID_TEXT", IWidgetId.SDE_T_ROLE_BANKORGID_TEXT);
		widgetMap.put("SDE_T_ROLE_PCID_TEXT", IWidgetId.SDE_T_ROLE_PCID_TEXT);
		widgetMap.put("SDE_T_ROLE_ROLEID_TEXT", IWidgetId.SDE_T_ROLE_ROLEID_TEXT);
		widgetMap.put("SDE_T_ROLE_ROLENAME_TEXT", IWidgetId.SDE_T_ROLE_ROLENAME_TEXT);
		
		widgetMap.put("SDE_W_USER", IWidgetId.SDE_W_USER);
		widgetMap.put("SDE_B_USER_CANCEL", IWidgetId.SDE_B_USER_CANCEL);
		widgetMap.put("SDE_B_USER_COMMIT", IWidgetId.SDE_B_USER_COMMIT);
		widgetMap.put("SDE_T_USER_BANKORGID_TEXT", IWidgetId.SDE_T_USER_BANKORGID_TEXT);
		widgetMap.put("SDE_T_USER_USERAME_TEXT", IWidgetId.SDE_T_USER_USERAME_TEXT);
		widgetMap.put("SDE_T_USER_USERID_TEXT", IWidgetId.SDE_T_USER_USERID_TEXT);
		widgetMap.put("SDE_E_USER_DEPTID_TEXT", IWidgetId.SDE_E_USER_DEPTID_TEXT);
		widgetMap.put("SDE_E_USER_USERSTATUS_TEXT", IWidgetId.SDE_E_USER_USERSTATUS_TEXT);
		
		widgetMap.put("SDE_W_LOG_EXAMINE", IWidgetId.SDE_W_LOG_EXAMINE);
		widgetMap.put("SDE_B_LOG_EXAMINE_DELETE", IWidgetId.SDE_B_LOG_EXAMINE_DELETE);
		widgetMap.put("SDE_B_LOG_EXAMINE_QUIT", IWidgetId.SDE_B_LOG_EXAMINE_QUIT);
		widgetMap.put("SDE_B_LOG_EXAMINE_SEARCH", IWidgetId.SDE_B_LOG_EXAMINE_SEARCH);
		
		widgetMap.put("SDE_W_PLUGIN", IWidgetId.SDE_W_PLUGIN);
		widgetMap.put("SDE_B_PLUGIN_PATCH_INSTALL", IWidgetId.SDE_B_PLUGIN_PATCH_INSTALL);
		widgetMap.put("SDE_B_PLUGIN_PLUGIN_INSTALL", IWidgetId.SDE_B_PLUGIN_PLUGIN_INSTALL);
		widgetMap.put("SDE_B_PLUGUN_CLOSE", IWidgetId.SDE_B_PLUGUN_CLOSE);
		widgetMap.put("SDE_B_PLUGUN_QUIT", IWidgetId.SDE_B_PLUGUN_QUIT);
		widgetMap.put("SDE_B_PLUGUN_START", IWidgetId.SDE_B_PLUGUN_START);
		widgetMap.put("SDE_B_PLUGUN_UNINSTALL", IWidgetId.SDE_B_PLUGUN_UNINSTALL);
		
		widgetMap.put("SDE_W_PWD_MODIFY", IWidgetId.SDE_W_PWD_MODIFY);
		widgetMap.put("SDE_B_PWD_MODIFY_CANCEL", IWidgetId.SDE_B_PWD_MODIFY_CANCEL);
		widgetMap.put("SDE_B_PWD_MODIFY_COMMIT", IWidgetId.SDE_B_PWD_MODIFY_COMMIT);
		widgetMap.put("SDE_T_PWD_MODIFY_OLDPWD_TEXT", IWidgetId.SDE_T_PWD_MODIFY_OLDPWD_TEXT);
		widgetMap.put("SDE_T_PWD_MODIFY_NEWPWD_TEXT", IWidgetId.SDE_T_PWD_MODIFY_NEWPWD_TEXT);
		widgetMap.put("SDE_T_PWD_MODIFY_CONFIRMPWD_TEXT", IWidgetId.SDE_T_PWD_MODIFY_CONFIRMPWD_TEXT);
		
		widgetMap.put("SDE_W_PERM_SET", IWidgetId.SDE_W_PERM_SET);
		widgetMap.put("SDE_B_PERM_SET_CANCEL", IWidgetId.SDE_B_PERM_SET_CANCEL);
		widgetMap.put("SDE_B_PERM_SET_CONFIRM", IWidgetId.SDE_B_PERM_SET_CONFIRM);
		
	}
	
	private static void deleteAll(){
		HibernateUtil.openSession();
		String hql = "delete from BsUser";
		HibernateUtil.getSession().createQuery(hql).executeUpdate();
		hql = "delete from BsRole";
		HibernateUtil.getSession().createQuery(hql).executeUpdate();
		hql = "delete from BsFunction";
		HibernateUtil.getSession().createQuery(hql).executeUpdate();
		hql = "delete from BsRolemapping";
		HibernateUtil.getSession().createQuery(hql).executeUpdate();
		hql = "delete from BsUsermapping";
		HibernateUtil.getSession().createQuery(hql).executeUpdate();
		
		hql = "delete from BsWidget";
		HibernateUtil.getSession().createQuery(hql).executeUpdate();
		hql = "delete from BsFuncmapping";
		HibernateUtil.getSession().createQuery(hql).executeUpdate();
		HibernateUtil.closeSession();
	}
	
	private static void initUser(){
		
		BsUserId id = new BsUserId();
		id.setBankorgId(default_bankorg_id);
		id.setUserId("sysadmin");
		
		BsUser user = new BsUser(id);
		user.setDepartmentId("9999999999");
		user.setPassword("8d1cb89a7594ef776c2d0a001aa59f3d");//表示密码六个0
		user.setUserName("sysadmin");
		user.setUserStatus("N");
		
		HibernateUtil.openSession();
		userDao.addEntity(user);
		HibernateUtil.closeSession();
	}
	
	private static void initRole(){
		BsRoleId id = new BsRoleId();
		id.setBankorgId(default_bankorg_id);
		id.setPcId(default_pc_id);
		id.setRoleId("sysadmin");
		
		BsRole role = new BsRole(id);
		role.setRoleName("系统管理员");
		
		HibernateUtil.openSession();
		roleDao.addEntity(role);
		HibernateUtil.closeSession();
	}
	
	private static void initFunction(){
		BsFunctionId id = null;
		BsFunction function = null;
		
		String[] functionIds = {"permmang", "departmang", "funcmang", "rolemang", 
				"passwdmodi", "permmenu", "usermang", "pluginmenu", "pluginmang", "logexamine",
				"helpmenu", "help", "about", "useradd", "userdel", "useredit", "passreset",
				"funcadd", "funcdel", "funcedit", "roleadd", "roledel", "roleedit",
				"departadd", "departdel", "departedit", "permset", "ftrefresh", "file", "open",
				"new", "close", "closeAll", "save", "saveAs", "saveAll", "quit", "edit", "cut",
				"copy", "paste", "delete", "window", "resetwin", "preference", "print", "search"};
		
		String[] functionNames = {"权限管理", "部门管理", "功能管理", "角色管理", "密码修改", 
				"权限设置-菜单", "用户管理", "插件管理-菜单", "插件管理", "日志查看",
				"帮助-菜单", "帮助", "关于", "用户增加", "用户删除", "用户修改", "密码重置",
				"功能增加", "功能删除", "功能修改", "角色增加", "角色删除", "角色修改",
				"部门增加", "部门删除", "部门修改", "权限设置", "功能树刷新", "文件-菜单", "打开", 
				"新建", "关闭", "关闭全部", "保存", "另存为", "全部保存", "退出", "编辑-菜单",
				"剪切", "复制", "粘贴", "删除", "窗口-菜单", "重置", "配置", "打印", "查找"};
		
		HibernateUtil.openSession();
		for (int i=0; i<functionIds.length; i++){
			id = new BsFunctionId();
			id.setBankorgId(default_bankorg_id);
			id.setFunctionId(functionIds[i]);
			id.setPcId(default_pc_id);
			
			function = new BsFunction(id);
			function.setFunctionName(functionNames[i]);
			
			functionDao.addEntity(function);
		}
		HibernateUtil.closeSession();
	}
	
	private static void initRolemapping(){
		BsRolemappingId id = null;
		BsRolemapping rolemapping = null;
		
		List<BsFunction> functions = functionDao.findAll(BsFunction.class);
		
		HibernateUtil.openSession();
		for(int i=0; i<functions.size(); i++){
			BsFunction function = functions.get(i);
			id = new BsRolemappingId();
			id.setBankorgId(default_bankorg_id);
			id.setFunctionId(function.getId().getFunctionId());
			id.setPcId(function.getId().getPcId());
			id.setRoleId("sysadmin");
			rolemapping = new BsRolemapping(id);
			
			rolemappingDao.addEntity(rolemapping);
		}
		HibernateUtil.closeSession();
	}
	
	private static void initUsermapping(){
		BsRoleId id = new BsRoleId();
		id.setBankorgId(default_bankorg_id);
		id.setPcId(default_pc_id);
		id.setRoleId("sysadmin");
		
		BsRole role = roleDao.findByPK(id, BsRole.class);
		
		BsUsermappingId usermappingId = new BsUsermappingId();
		usermappingId.setBankorgId(2001L);
		usermappingId.setPcId(role.getId().getPcId());
		usermappingId.setRoleId(role.getId().getRoleId());
		usermappingId.setUserId("sysadmin");
		
		BsUsermapping usermapping = new BsUsermapping(usermappingId);
		
		HibernateUtil.openSession();
		usermappingDao.addEntity(usermapping);
		HibernateUtil.closeSession();
		
	}
	
	/**
	 * 初始化控件信息
	 */
	private static void initWidget() {
		BsWidget widget = null;
		BsWidgetId id = null;
		String[] widgetIds = {IWidgetId.SDE_W_DEPT,IWidgetId.SDE_B_DEPT_CANCEL, IWidgetId.SDE_B_DEPT_COMMIT ,IWidgetId.SDE_T_DEPT_BANKORGID_TEXT,
				IWidgetId.SDE_T_DEPT_DEPTID_TEXT,IWidgetId.SDE_T_DEPT_DEPTNAME_TEXT, 
				
				IWidgetId.SDE_W_FUNC,IWidgetId.SDE_B_FUNC_CANCEL, IWidgetId.SDE_B_FUNC_COMMIT, IWidgetId.SDE_T_FUNC_BANKORGID_TEXT,
				IWidgetId.SDE_T_FUNC_FUNID_TEXT, IWidgetId.SDE_T_FUNC_FUNNAME_TEXT, IWidgetId.SDE_T_FUNC_PCID_TEXT,
				
				IWidgetId.SDE_W_ROLE,IWidgetId.SDE_B_ROLE_CANCEL,IWidgetId.SDE_B_ROLE_COMMIT,IWidgetId.SDE_T_ROLE_BANKORGID_TEXT,
				IWidgetId.SDE_T_ROLE_PCID_TEXT,IWidgetId.SDE_T_ROLE_ROLEID_TEXT,IWidgetId.SDE_T_ROLE_ROLENAME_TEXT,
				
				IWidgetId.SDE_W_USER, IWidgetId.SDE_B_USER_CANCEL,IWidgetId.SDE_B_USER_COMMIT, IWidgetId.SDE_T_USER_BANKORGID_TEXT,
				IWidgetId.SDE_T_USER_USERAME_TEXT,IWidgetId.SDE_T_USER_USERID_TEXT,IWidgetId.SDE_E_USER_DEPTID_TEXT,
				IWidgetId.SDE_E_USER_USERSTATUS_TEXT,
				
				IWidgetId.SDE_W_LOG_EXAMINE,IWidgetId.SDE_B_LOG_EXAMINE_DELETE,IWidgetId.SDE_B_LOG_EXAMINE_QUIT,IWidgetId.SDE_B_LOG_EXAMINE_SEARCH,
				
				IWidgetId.SDE_W_PLUGIN,IWidgetId.SDE_B_PLUGIN_PATCH_INSTALL,IWidgetId.SDE_B_PLUGIN_PLUGIN_INSTALL,IWidgetId.SDE_B_PLUGUN_CLOSE,
				IWidgetId.SDE_B_PLUGUN_QUIT,IWidgetId.SDE_B_PLUGUN_START,IWidgetId.SDE_B_PLUGUN_UNINSTALL,
				
				IWidgetId.SDE_W_PWD_MODIFY,IWidgetId.SDE_B_PWD_MODIFY_CANCEL,IWidgetId.SDE_B_PWD_MODIFY_COMMIT,IWidgetId.SDE_T_PWD_MODIFY_OLDPWD_TEXT,
				IWidgetId.SDE_T_PWD_MODIFY_NEWPWD_TEXT,IWidgetId.SDE_T_PWD_MODIFY_CONFIRMPWD_TEXT,
				
				IWidgetId.SDE_W_PERM_SET,IWidgetId.SDE_B_PERM_SET_CANCEL,IWidgetId.SDE_B_PERM_SET_CONFIRM
				};
		
		String[] widgetNames = {"部门管理对话框","部门管理取消","部门管理提交","部门管理银行机构ID","部门管理部门ID","部门管理部门名称",
				
				"功能管理对话框","功能管理取消","功能管理提交","功能管理银行机构ID","功能管理功能ID","功能管理功能名称","功能管理电脑号",
				
				"角色管理对话框","角色管理取消","角色管理提交","角色管理银行机构ID","角色管理电脑号","角色管理角色ID","角色管理角色名称",
				
				"用户管理对话框","用户管理取消","用户管理提交","用户管理银行机构ID","用户管理用户名","用户管理用户ID","用户管理部门ID","用户管理用户状态",
				
				"插件日志查看对话框","插件日志删除按钮","插件日志退出按钮","插件日志查询按钮",
				
				"插件管理对话框","插件补丁安装按钮","插件安装按钮","插件关闭按钮","插件管理对话框退出按钮","插件开启按钮","插件卸载按钮",
				
				"密码修改对话框","密码修改取消","密码修改提交","密码修改旧密码","密码修改新密码","密码修改新密码确认",
				
				"权限设置对话框","权限设置取消","权限设置提交"
		};
		
		HibernateUtil.openSession();
		for (int i=0; i<widgetIds.length; i++){
			id = new BsWidgetId();
			id.setBankorgId(default_bankorg_id);
			id.setWidgetId(widgetIds[i]);
			id.setPcId(default_pc_id);
			
			widget = new BsWidget(id);
			widget.setWidgetName(widgetNames[i]);
			widget.setWidgetType(widgetIds[i].substring(widgetIds[i].indexOf("-")+1,widgetIds[i].lastIndexOf("-")));
			widget.setPluginId(widgetIds[i].substring(0,widgetIds[i].lastIndexOf("-")));
			widgetDao.addEntity(widget);
		}
		HibernateUtil.closeSession();
	}
	
	/**
	 * 初始化功能控件映射
	 */
	private static void initFuncmapping() {
		BsFuncmappingId id = null;
		BsFuncmapping funcmapping = null;
		
		List<BsFunction> functions = functionDao.findAll(BsFunction.class);
		
		Set<String> widgetSet = widgetMap.keySet();
		
		HibernateUtil.openSession();
		for (int j = 0; j < functions.size(); j++) {
			String functionId = functions.get(j).getId().getFunctionId();
			if(functionId.lastIndexOf("mang")==-1) {
				continue;
			}
			String funStr = functionId.substring(0,functionId.lastIndexOf("mang")).toUpperCase();
			
			for (String widSetKey : widgetSet) {
				if(widSetKey.contains(funStr)) {
					String widgetId = widgetMap.get(widSetKey);
					id = new BsFuncmappingId();
					id.setBankorgId(default_bankorg_id);
					id.setPcId(functions.get(j).getId().getPcId());
					id.setFunctionId(functionId);
					id.setWidgetId(widgetId);
					funcmapping = new BsFuncmapping(id);
					funcmapping.setMappingType(Constants.MAPPING_TYPE_A);
					funcmappingDao.addEntity(funcmapping);
				}
			}
			
		}
		HibernateUtil.closeSession();
	}
	
	public static void initData(){
		new SystemInit().login();
		
		deleteAll();
		
		initUser();
		initRole();
		initFunction();
		initRolemapping();
		initUsermapping();
		
		initWidget();
		initFuncmapping();
	}
	
	public static void main(String[] args) {
		new SystemInit().login();
		
		deleteAll();
		
		initUser();
		initRole();
		initFunction();
		initRolemapping();
		initUsermapping();
		
		initWidget();
		initFuncmapping();
	}
}
