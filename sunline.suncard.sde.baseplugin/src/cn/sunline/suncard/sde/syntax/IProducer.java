/*
 * 文件名：IElement.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：决策引擎系统元素生成接口
 * 修改人： xcc
 * 修改时间：2011-11-02
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.syntax;

import cn.sunline.suncard.sde.exception.SystemException;

/**
 * 决策引擎系统元素生成接口
 * 决策引擎系统元素生成类必须实现该接口
 * @author    xcc
 * @version   1.0, 2011-11-02 
 * @since     1.0
 */

public interface IProducer {
	
	/**
	 * 设置主键
	 * @param keys 主键
	 */
	public void setkeys(String ... keys) throws Exception;
	
	/**
	 * 元素处理
	 * @param dataPartId 数据片段ID
	 * @return
	 */
	public IElement produce(Object ... parameters) throws Exception;
	
	/**
	 * 填充元素，查询数据库，将需要的值填入元素内
	 * @throws Exception
	 */
	public IElement fillElement() throws Exception;
	
	/**
	 * 检查元素，如果通过判断是否更新状态
	 * @param dataPartId 数据片段ID
	 * @return true：检查通过；false：检查失败
	 * @throws Exception
	 */
	public boolean doCheck(Object ... parameters) throws Exception;
	
	/**
	 * 计算元素值，如果计算完成更新元素状态
	 * @param dataPartId 数据片段ID
	 * @return 元素值
	 * @throws Exception
	 */
	public Object doCalculate(Object ... parameters) throws Exception;
}
