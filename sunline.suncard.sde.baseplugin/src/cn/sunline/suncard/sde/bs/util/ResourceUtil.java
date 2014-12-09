/*
 * 文件名：ResourceUtil.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：资源工具类
 * 修改人： xcc
 * 修改时间：2011-10-11
 * 修改内容：新增
 * 
 * 修改人：tpf
 * 修改时间：2011-10-12
 * 修改内容：ResourceBundle改为PropertyResourceBundle
*/
package cn.sunline.suncard.sde.bs.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;


/**
 * 资源工具类
 * 提供资源操作的工具
 * @author    xcc
 * @version   1.0, 2011-09-19
 * @see       getMessage
 * @since     1.0
 */

public class ResourceUtil {
	
	
	/**
	 * 将配置文件内容读取放入系统环境变量内
	 * @param filePath 配置文件路径，不包含后缀名
	 */
	public static void setSystemPropertiesFromResource(String filePath){
		//ResourceBundle resourceBundle = ResourceBundle.getBundle(filePath,LocaleUtil.getConfigLocale());
		PropertyResourceBundle resourceBundle = null;
		try {
			resourceBundle = new PropertyResourceBundle(new FileInputStream(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(String key : resourceBundle.keySet()){
			System.setProperty(key, resourceBundle.getString(key));
		}
	}
	
	/**
	 * 获取所有国际化信息的resourceBundle
	 * @return 国际化信息的resourceBundle
	 */
	public static ResourceBundle getDefaultResourceBundle(){
		//return ResourceBundle.getBundle(SystemParameters.getMessageResourcePath(),LocaleUtil.getConfigLocale());
		PropertyResourceBundle resourceBundle = null;
		try {
			resourceBundle = new PropertyResourceBundle(new FileInputStream(System.getProperty("user.dir") +"/"+ SystemParameters.getMessageResourceTempPath()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resourceBundle;
	}
	
	/**
	 * 获取resources目录下的所有国际化信息
	 * @return 所有国际化信息字符串
	 */
	public static String getMessageFromResources(){
		return ParseHibernateConfig.readCfgFile(System.getProperty("user.dir") +"/"+ SystemParameters.getMessageResourcePath());
	}
	
	/**
	 * 向磁盘写入MessageResource临时国际化文件
	 */
	public static void initMessageResources() {
		String messageResourceTempPath = System.getProperty("user.dir") +"/"+ SystemParameters.getMessageResourceTempPath();
		ParseHibernateConfig.writeFile(messageResourceTempPath, getMessageFromResources());
	}
}
