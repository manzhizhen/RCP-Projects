/*
 * 文件名：IProcess.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：语法处理接口
 * 修改人： xcc
 * 修改时间：2011-11-10
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.syntax;

import java.util.ArrayList;

/**
 * 语法处理接口
 * 所有的语法处理类均需要实现该接口
 * 该接口提供语法转换和语法处理
 * @author    xcc
 * @version   1.0, 2011-11-10
 * @since     1.0
 */
public interface IProcess {
	
	/**
	 * 语法转换，将数据库保存的语法数据转换为标准groovy语法
	 * @param syntaxStr 数据库保存的脚步数据
	 * @return 标准groovy脚本
	 * @throws Exception
	 */
	public String convert(String syntaxStr,ArrayList<String>  parameters) throws Exception;
	
	/**
	 * 语法运算，运算标准groovy脚本，返回运算结果
	 * @param syntaxStr 标准groovy脚本
	 * @param inputObj 输入参数
	 * @return 运算结果
	 * @throws Exception
	 */
	public Object caculate(String syntaxStr,Object ... inputObj) throws Exception;
	
}
