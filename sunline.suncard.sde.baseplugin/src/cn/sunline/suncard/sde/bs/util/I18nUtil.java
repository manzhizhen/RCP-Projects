/*
 * 文件名：I18nUtil.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：国际化通用工具类
 * 修改人： tpf
 * 修改时间：2011-09-19
 * 修改内容：新增
 * 
 * 修改人：xcc
 * 修改时间：2011-10-11
 * 修改内容：将相关内容移到jar包内
*/
package cn.sunline.suncard.sde.bs.util;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * 国际化通用工具类
 * 通过些类可以获取国际化文件中的信息
 * @author    tpf
 * @version   1.0, 2011-09-19
 * @see       getMessage
 * @since     1.0
 */
public class I18nUtil {
	
	/**
	 * 国际化通用方法
	 * 通过些方法可以获取国际化文件中的信息
	 * @param msg  要获取的文件
	 * @param objects  带参数的国际化。有几个参数都可以往后添加，是可变长参数的
	 * @return i18nMsg
	 * @throws IOException 
	 * @see   
	 */
	public static String getMessage(String msg,Object...objects) {
		ResourceBundle resourceBundle = ResourceUtil.getDefaultResourceBundle();
		MessageFormat messageFormat = new MessageFormat(resourceBundle.getString(msg));
		String i18nMsg = messageFormat.format(objects);
		return i18nMsg;
	}
	
	public static String getMessage(String msg) {
		ResourceBundle resourceBundle = ResourceUtil.getDefaultResourceBundle();
		MessageFormat messageFormat = new MessageFormat(resourceBundle.getString(msg));
		String i18nMsg = messageFormat.format(null);
		return i18nMsg;
	}
	
	public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
		System.out.println(getMessage("test", "小易", "长亮科技"));
	}
}
