/*
 * 文件名：IElement.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：决策引擎系统元素顶层接口
 * 修改人： xcc
 * 修改时间：2011-11-02
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.syntax;

/**
 * 决策引擎系统元素顶层接口
 * 决策引擎系统元素类必须实现该接口
 * @author    xcc
 * @version   1.0, 2011-11-02
 * @since     1.0
 */

public interface IElement {
	
	/**
	 * 获取元素值
	 * @return 元素值
	 */
	public Object getValue();
	
	/**
	 * 设置元素值
	 */
	public void setValue(Object elementValue);
	
	/**
	 * 获取元素ID
	 * @return 元素ID
	 */
	public String getId();
	
	/**
	 * 设置元素ID
	 * 
	 */
	public void setId(String _id);
	
	/**
	 * 获取元素名称
	 * @return 元素名称
	 */
	public String getName();
	
	/**
	 * 设置元素名称
	 * 
	 */
	public void setName(String _name);
	
	/**
	 * 获取元素描述，如果该元素没有描述就返回元素名称
	 * @return 元素描述
	 */
	public String getDescription();
	
	/**
	 * 设置元素描述
	 * 
	 */
	public void setDescription(String _description);
	
	/**
	 * 获取元素状态，如果该元素没有状态则返回null
	 * @return 元素状态/null
	 */
	public String getStatus();
	
	/**
	 * 设置元素状态
	 * 
	 */
	public void setStatus(String _status);
	
	/**
	 * 获取元素是否有表达式，如果有就返回true，没有的话就返回false
	 * @return true/false
	 */
	public boolean hasExpression();
	
	/**
	 * 获取元素表达式，如果没有则返回null
	 * @return 元素表达式
	 */
	public String getExpression();
	
	/**
	 * 设置元素表达式
	 * 
	 */
	public void setExpression(String _expression);
}
