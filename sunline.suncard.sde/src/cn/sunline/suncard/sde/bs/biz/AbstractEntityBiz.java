/*
 * 文件名：AbstractEntityBiz.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：业务层抽象类
 * 修改人：heyong
 * 修改时间：2011-10-23
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.biz;

import java.util.List;

/**
 * 业务层抽象类
 * 此类主要封装了分页所需的两个方法,如果某个业务有分页的需求，请继承此类
 * @author heyong
 * @version 1.0, 2011-10-23
 * @see 
 * @since 1.0
 */
public abstract class AbstractEntityBiz<T> {

	/**
	 * 得到某数据库表中有多少条记录
	 * 
	 * @return
	 */
	public abstract int getRecordCount();
	/**
	 * 
	 * 分页查询，具体实现由子类完成
	 * @param currPage 当前页码
	 * @param pageSize 每页显示记录数
	 * @return
	 */
	public abstract List<T> getByPage(int currPage, int pageSize);
}
