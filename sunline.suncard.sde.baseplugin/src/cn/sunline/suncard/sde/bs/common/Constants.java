/*
 * 文件名：Constants.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：存放常量通用工具类
 * 修改人： tpf
 * 修改时间：2011-09-20
 * 修改内容：新增
 * 
 * 修改人：heyong
 * 修改时间：2011-09-28
 * 修改内容：增加变量INITIAL_USER_STATUS、INITIAL_USER_PASSWORD和INITIAL_USER_DEPARTMENT_ID
*/
package cn.sunline.suncard.sde.bs.common;


/**
 * 存放常量通用工具类。
 * 存放常量通用工具类，方便后续管理，统一定义为public static final的
 * @author    tpf
 * @version   1.0, 2011-09-20
 * @see       
 * @since     1.0
 */
public interface Constants {

	//用户初始密码
	public static final String INITIAL_USER_PASSWORD = "000000";
	
	//用户初始状态
	public static final String INITIAL_USER_STATUS = "U";

	public static final String BANKORG_ID = "bankorgId";
	
	//当前登录用户对象
	public static final String CURRENT_USER = "currentUser";
	
	//当前登录用户id
	public static final String CURRENT_USER_ID = "currentUserId";
	
	//电脑号
	public static final String PC_ID = "pcId";
	
	public static final String PC_IP = "pcIp";
	
	// PC_ID的常量值
	public static final String PC_ID_VALUE = "suncard";
	
	//用户初始部门ID
	public static final String INITIAL_USER_DEPARTMENT_ID = "9999999999";
	
	//密码重置时，随机生成密码长度
	public static final int GEN_PASSWD_LENGTH = 6;
	
	//密码重置时，随机生成密码形式，值为1，2，3,分别表示数字，字母，数字和字母混合三种形式
	public static final int GEN_PASSWD_STYLE = 3;
	
	/*控件映射类型:
	 * A-添加该控件，如果该控件为父控件，同时添加全部子控件
	 *S-添加该控件，如果该控件为父控件，不添加子控件
	 *D-删除该控件，如果该控件为父控件，同时删除全部子控件)
	*/
	public static final String WIDGET_MAPPING_TYPE = "A";
	
	//配置文件名
	public static final String CONFIG_NAME = "card_config.xml";
	
	//控件映射类型
	public static final String MAPPING_TYPE_A = "A";
	public static final String MAPPING_TYPE_S = "S";
	public static final String MAPPING_TYPE_D = "D";
	
	
	//每页显示记录条数
	public static final int RECORD_SIZE_PER_PAGE = 20;
}
