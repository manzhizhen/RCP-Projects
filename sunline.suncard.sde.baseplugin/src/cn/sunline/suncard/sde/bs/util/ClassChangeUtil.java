/*
 * 文件名：ClassChangeUtil.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：类型转换工具类
 * 修改人： xcc
 * 修改时间：2011-11-22
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.bs.util;

/**
 * 类型转换工具类
 * 提供类型转换，校验等相关操作
 * @author    xcc
 * @version   1.0, 2011-11-22
 * @since     1.0
 */
public class ClassChangeUtil {
	private final static String NUMBER_TYPE = "N";
	private final static String STRING_TYPE = "S";
	private final static String DATE_TYPE = "D";
	private final static String BOOLEAN_TYPE = "B";
	private final static String JAVA_DOUBLE_TYPE = "java.lang.Double";
	private final static String JAVA_FLOAT_TYPE = "java.lang.Float";
	private final static String JAVA_INTEGER_TYPE = "java.lang.Integer";
	private final static String JAVA_LONG_TYPE = "java.lang.Long";
	private final static String JAVA_SHORT_TYPE = "java.lang.Short";
	private final static String JAVA_BIGDECIMAL_TYPE = "java.math.BigDecimal";
	private final static String JAVA_DATE_TYPE = "java.util.Date";
	private final static String JAVA_STRING_TYPE = "java.lang.String";
	private final static String JAVA_BOOLEAN_TYPE = "java.lang.Boolean";
	
	/**
	 * 通过输入java类型的字符串，转化为系统内部的类型代码
	 * @param javaType java类型
	 * @return 内部类型
	 */
	public static String getInnerType(String javaType){
		if(JAVA_DOUBLE_TYPE.equals(javaType))return NUMBER_TYPE;
		else if(JAVA_FLOAT_TYPE.equals(javaType))return NUMBER_TYPE;
		else if(JAVA_INTEGER_TYPE.equals(javaType))return NUMBER_TYPE;
		else if(JAVA_LONG_TYPE.equals(javaType))return NUMBER_TYPE;
		else if(JAVA_SHORT_TYPE.equals(javaType))return NUMBER_TYPE;
		else if(JAVA_BIGDECIMAL_TYPE.equals(javaType))return NUMBER_TYPE;
		else if(JAVA_DATE_TYPE.equals(javaType))return DATE_TYPE;
		else if(JAVA_STRING_TYPE.equals(javaType))return STRING_TYPE;
		else if(JAVA_BOOLEAN_TYPE.equals(javaType))return BOOLEAN_TYPE;
		else return STRING_TYPE;
		
	}
	
	/**
	 * 通过输入java对象，转化为系统内部的类型代码
	 * @param o java对象
	 * @return 内部类型
	 */
	public static String getInnerType(Object o){
		return getInnerType(o.getClass().getName());
	}
	
	/**
	 * 检查类型是否匹配
	 * @param innerType 内部类型
	 * @param o java对象
	 * @return true:匹配
	 * 		   flase: 不匹配
	 */
	public static boolean isTypeMatched(String innerType,Object o){
		return getInnerType(o).equals(innerType);
	}
}
