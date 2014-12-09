/*
 * 文件名：     ExtVarComposite.java
 * 版权：          Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：          输入校验工具类
 * 修改人：     文忠孝
 * 修改时间：2011-10-31
 * 修改内容：创建
 */

package cn.sunline.suncard.sde.bs.util;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 
 * 
 * @author  文忠孝
 * @version , 2011-10-31
 * @see     
 * @since   1.0
 */

public class VerifyUtil {
	public static final String REGEX_NON_NEGATIVE_INTEGER = "(\\d+)|(0+)";	// 非负整数正则表达式
	public static final String REGEX_INTEGER = "^-?\\d+$";					// 整数正则表达式
	public static final String REGEX_DOUBLE = "^-?\\d+\\.\\d+$";			// 浮点数正则表达式
	public static final String REGEX_CHINESE_CHARTER = "^[^\\u4e00-\\u9fa5]{0,}$";	// 非中文字符正则表达式
	public static final String REGEX_CHAR = "\\w*";
	
	/**
	 * 正则表达式校验
	 * @param regex 正则表达式
	 * @param input 待校验字符串
	 * @return true=校验成功;false=校验失败
	 */
	public static boolean verifyRegex(String regex, String input){
		if ((input == null) | (input.trim().length() == 0)){
			return false;
		}
		String inputValue = input.trim();
		return Pattern.matches(regex, inputValue);
	}
	
	/**
	 * 非负整数校验
	 * @param numLength 数据位数(输入负数时被忽略)
	 * @param numValue 数据值
	 * @return true=校验成功;false=校验失败
	 */
	public static boolean verifyNonNegativeInteger(int numLength, String numValue){
		if ((numValue == null) | (numValue.trim().length() == 0)){
			return false;
		}
		
		// 位数非正时被忽略
		if (numLength < 0){
			return verifyRegex(REGEX_NON_NEGATIVE_INTEGER, numValue.trim());
		}
		if (numValue.trim().length() > numLength){
			return false;
		}
		return verifyRegex(REGEX_NON_NEGATIVE_INTEGER, numValue.trim());
	}
	
	/**
	 * 整数校验
	 * @param integerValue 整数值
	 * @return true=校验成功;false=校验失败
	 */
	public static boolean verifyInteger(String integerValue){
		return verifyRegex(REGEX_INTEGER, integerValue);
	}
	
	/**
	 * 整数校验
	 * @param integerValue 整数值
	 * @return true=校验成功;false=校验失败
	 */
	public static boolean verifyInteger(int integerValue){
		return verifyRegex(REGEX_INTEGER, Integer.toString(integerValue));
	}
	
	/**
	 * 非负整数验证
	 * @param integerValue
	 * @return boolean true=校验成功;false=校验失败
	 */
	public static boolean verifyNotNegativeInteger(String integerValue) {
		return verifyRegex(REGEX_NON_NEGATIVE_INTEGER, integerValue);
	}
	
	/**
	 * 浮点数验证
	 * @param doubleValue
	 * @return boolean true=校验成功;false=校验失败
	 */
	public static boolean verifyDouble(String doubleValue) {
		boolean isInteger = verifyRegex(REGEX_INTEGER, doubleValue);
		if (isInteger) {
			return isInteger;
		}
		
		return verifyRegex(REGEX_DOUBLE, doubleValue);
	}
	
	/**
	 * 保存或修改时，校验各字段是否符合保存要求，若符合才可保存
	 * @param list
	 * @return flag(检查是否通过)
	 */
	public static boolean verifySaveOrUpdate(List<Boolean> list) {
		boolean flag = true;
		if(list != null && !list.isEmpty()) {
			for (Boolean bool : list) {
				if(bool == false) {
					flag = bool;
					break;
				}
			}
		}
		return flag;
	}
	
	/**
	 * 验证非中文字符串
	 * @param string 待验证的字符串
	 * @return true=待验证的字符串不含中文字符(串);false=待验证的字符串含中文字符(串)
	 */
	public static boolean verifyNotChineseString(String string) {
		return verifyRegex(REGEX_CHINESE_CHARTER, string);
	}
	
	/**
	 * 校验字符串中只包含字母、数字或下划线，也就是  A~Z,a~z,0~9,_ 中任意一个
	 * @param string 待验证的字符串
	 * @return true=字符串中只包含字母、数字或下划线，也就是  A~Z,a~z,0~9,_ 等字符;
	 * 		   false=字符串中包含字母、数字或下划线，也就是  A~Z,a~z,0~9,_ 以外的字符
	 */
	public static boolean verifyNormalString(String string) {
		return verifyRegex(REGEX_CHAR, string);
	}
	
	public static void main(String[] args) {
		System.out.println(verifyNotChineseString("qq平ee"));
//		System.out.println(verifyNormalString("qq2"));
	}
}
