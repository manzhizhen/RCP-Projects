/*
 * 文件名：
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：tpf
 * 修改时间：2011-10-19
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.resource;

/**
 * 控件ID接口类
 * 本类收集本插件所有控件ID常量
 * @author tpf
 * @version 1.0, 2011-10-19
 * @see 
 * @since 1.0
 */
public interface IWidgetId {
	//------------------------部门管理对话框----------------------------
	//部门管理对话框ID
	public static final String SDE_W_DEPT = "SDE-W-0001";
	
	//部门管理编辑和添加时提交按钮ID
	public static final String SDE_B_DEPT_COMMIT = "SDE-B-0001";
	
	//部门管理编辑和添加时取消按钮ID
	public static final String SDE_B_DEPT_CANCEL = "SDE-B-0002";
	
	//部门管理编辑时和添加银行机构id文本框ID
	public static final String SDE_T_DEPT_BANKORGID_TEXT= "SDE-T-0001";
	
	//部门管理编辑和添加时部门id文本框ID
	public static final String SDE_T_DEPT_DEPTID_TEXT= "SDE-T-0002";
	
	//部门管理编辑和添加时部门名称文本框ID
	public static final String SDE_T_DEPT_DEPTNAME_TEXT= "SDE-T-0003";
	
	//------------------------用户管理对话框-------------------------------
	//用户管理对话框ID
	public static final String SDE_W_USER = "SDE-W-0002";
	
	//用户管理编辑和添加时提交按钮
	public static final String SDE_B_USER_COMMIT = "SDE-B-0003";
	
	//用户管理编辑和添加时取消按钮
	public static final String SDE_B_USER_CANCEL = "SDE-B-0004";
	
	//用户管理编辑和添加时银行机构id文本框ID
	public static final String SDE_T_USER_BANKORGID_TEXT= "SDE-T-0004";
	
	//用户管理编辑和添加时用户id文本框ID
	public static final String SDE_T_USER_USERID_TEXT= "SDE-T-0005";
	
	//用户管理编辑时和添加用户名称文本框ID
	public static final String SDE_T_USER_USERAME_TEXT= "SDE-T-0006";
	
	//用户管理编辑和添加时部门id文本框ID
	public static final String SDE_E_USER_DEPTID_TEXT= "SDE-E-0007";
	
	//用户管理编辑和添加时用户状态文本框ID
	public static final String SDE_E_USER_USERSTATUS_TEXT= "SDE-E-0008";
	
	//---------------------------角色管理-----------------------------
	//角色管理对话框ID
	public static final String SDE_W_ROLE = "SDE-W-0003";
	
	//角色管理编辑和添加时提交按钮ID
	public static final String SDE_B_ROLE_COMMIT = "SDE-B-0005";
	
	//角色管理编辑和添加时提交按钮ID
	public static final String SDE_B_ROLE_CANCEL= "SDE-B-0006";
	
	//角色管理编辑和添加时银行机构id文本框ID
	public static final String SDE_T_ROLE_BANKORGID_TEXT= "SDE-T-0009";
	
    //角色管理编辑和添加时电脑编号id文本框ID
	public static final String SDE_T_ROLE_PCID_TEXT= "SDE-T-0010";
	
	//角色管理编辑和添加时角色id文本框ID
	public static final String SDE_T_ROLE_ROLEID_TEXT= "SDE-T-0011";
	
	//角色管理编辑和添加时角色名称文本框ID
	public static final String SDE_T_ROLE_ROLENAME_TEXT= "SDE-T-0012";
	
	//--------------------------功能管理------------------------------
	//功能管理对话框ID
	public static final String SDE_W_FUNC = "SDE-W-0004";
	
	//功能管理编辑和添加时提交按钮ID
	public static final String SDE_B_FUNC_COMMIT = "SDE-B-0007";
	
	//功能管理编辑和添加时提交按钮ID
	public static final String SDE_B_FUNC_CANCEL= "SDE-B-0008";
	
	//功能管理编辑和添加时银行机构id文本框ID
	public static final String SDE_T_FUNC_BANKORGID_TEXT= "SDE-T-0013";
	
	//功能管理编辑和添加时电脑编号id文本框ID
	public static final String SDE_T_FUNC_PCID_TEXT= "SDE-T-0014";
	
	//角色管理编辑和添加时角色id文本框ID
	public static final String SDE_T_FUNC_FUNID_TEXT= "SDE-T-0015";
	
	//角色管理编辑和添加时角色名称文本框ID
	public static final String SDE_T_FUNC_FUNNAME_TEXT= "SDE-T-0016";
	
	//----------------------插件管理对话框--------------------------
	//插件管理对话框ID
	public static final String SDE_W_PLUGIN = "SDE-W-0005";
	
	//插件管理补丁安装按钮ID
	public static final String SDE_B_PLUGIN_PATCH_INSTALL = "SDE-B-0009";
	
	//插件管理插件安装按钮ID
	public static final String SDE_B_PLUGIN_PLUGIN_INSTALL = "SDE-B-0010";
		
	//插件管理开启按钮ID
	public static final String SDE_B_PLUGUN_START = "SDE-B-0011";
		
	//插件管理关闭按钮ID
	public static final String SDE_B_PLUGUN_CLOSE = "SDE-B-0012";
	
	//插件管理拆卸按钮ID
	public static final String SDE_B_PLUGUN_UNINSTALL = "SDE-B-0013";
	
	//插件管理退出按钮ID
	public static final String SDE_B_PLUGUN_QUIT = "SDE-B-0014";
	
	//--------------------------日志查看对话框-------------------------
	//日志查看对话框ID
	public static final String SDE_W_LOG_EXAMINE = "SDE-W-0006";
	
	//日志查看对话框查询按钮ID
	public static final String SDE_B_LOG_EXAMINE_SEARCH = "SDE-B-0015";
	
	//日志查看对话框删除按钮ID
    public static final String SDE_B_LOG_EXAMINE_DELETE = "SDE-B-0016";
    
    //日志查看对话框退出按钮ID
  	public static final String SDE_B_LOG_EXAMINE_QUIT = "SDE-B-0017";
  	
  	//------------------------修改密码对话框------------------------
  	//密码修改对话框ID
  	public static final String SDE_W_PWD_MODIFY = "SDE-W-0007";
  	
  	//密码修改确认按钮ID
  	public static final String SDE_B_PWD_MODIFY_COMMIT = "SDE-B-0018";
  	
  	//密码修改确认按钮ID
  	public static final String SDE_B_PWD_MODIFY_CANCEL = "SDE-B-0019";
  	
    //密码修改原密码文本ID
  	public static final String SDE_T_PWD_MODIFY_OLDPWD_TEXT = "SDE-T-0017";
  	
    //密码修改新密码文本ID
  	public static final String SDE_T_PWD_MODIFY_NEWPWD_TEXT = "SDE-T-0018";
  	
    //密码修改确认密码文本ID
  	public static final String SDE_T_PWD_MODIFY_CONFIRMPWD_TEXT = "SDE-T-0019";
  	
  	//-------------------------权限设置对话框----------------------------
  	//权限设置对话框ID
  	public static final String SDE_W_PERM_SET = "SDE-W-0008";
  	
  	//权限设置确认按钮ID
  	public static final String SDE_B_PERM_SET_CONFIRM = "SDE-B-0020";
  	
  	//权限设置取消按钮ID
  	public static final String SDE_B_PERM_SET_CANCEL = "SDE-B-0021";
}
