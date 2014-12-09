/*
 * 文件名：StringBuilderUtil.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：StringBuilder工具类
 * 修改人： xcc
 * 修改时间：2011-11-10
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.bs.util;

/**
 * StringBuilder工具类
 * StringBuilder工具类
 * @author    xcc
 * @version   1.0, 2011-11-10
 * @since     1.0
 */
public class StringBuilderUtil {
	
	/**
	 * 对StringBuilder内所有和srcStr相同的字符串替换为replaceStr
	 * @param stringBuilder StringBuilder对象
	 * @param srcStr 源字符串
	 * @param replaceStr 替换字符串
	 */
	public static void replaceAll(StringBuilder stringBuilder , String srcStr, String replaceStr){
		int startIndex=0;
		int endIndex=0;
		int length=srcStr.length();
		while(true){
			startIndex = stringBuilder.indexOf(srcStr, endIndex);
			if(startIndex == -1)break;
			
			endIndex = startIndex+length;
			
			stringBuilder.replace(startIndex, endIndex, replaceStr);
		}
	}
	
	public static void main(String args[]){
		StringBuilder sb=new StringBuilder("efdfgadgfdgf[E]zz00001zz fdsf sf asf [E]zz00001zzdddddd[E]zz00001zzddddeee");
		StringBuilderUtil.replaceAll(sb, "[E]zz00001zz", "@@00001@@");
		System.out.println(sb.toString());
	}
}
