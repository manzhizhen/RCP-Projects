/*
 * 文件名：LocaleUtil.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：地区工具类
 * 修改人： xcc
 * 修改时间：2011-10-11
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.bs.util;

import java.util.Locale;


/**
 * 地区工具类
 * 地区资源操作的工具
 * @author    xcc
 * @version   1.0, 2011-09-19
 * @see       
 * @since     1.0
 */

public class LocaleUtil {
	
	
	/**
	 * 系统将配置文件也以资源形势进行保存和加载，该部分的所有地区熟悉均为英文
	 * @return 
	 */
	public static Locale getConfigLocale(){
		return Locale.ENGLISH;
	}
	
	public static Locale getDefaultLocale(){
		return new Locale(SystemParameters.getDefaultLanguage(), SystemParameters.getDefaultCountry());
	}
}
