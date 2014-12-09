/*
 * 文件名：SystemEnvironmen.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：系统环境
 * 修改人： xcc
 * 修改时间：2011-11-04
 * 修改内容：新增
*/
package cn.sunline.suncard.sde;

/**
 * 系统环境
 * 标志目前基础包运行于哪个系统环境
 * @author    xcc
 * @version   1.0, 2011-11-04
 * @since     1.0
 */

public class SystemEnvironmen {
	
	private static String enviroment=null;
	private static final String DM_ENV="DM";
	private static final String DP_ENV="DP";
	
	public static void setEnviromentAsDecisionMaker(){
		enviroment=DM_ENV;
	}
	
	public static void setEnviromentAsDecisionProcess(){
		enviroment=DP_ENV;
	}
	
	public static boolean isDecisionMaker(){
		return DM_ENV.equals(enviroment);
	}
	
	public static boolean isDecisionProcess(){
		return DP_ENV.equals(enviroment);
	}
	
	public static boolean isSetEnv(){
		return enviroment==null?false:true;
	}
	
}
