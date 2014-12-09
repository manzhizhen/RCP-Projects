/**
 * 文件名：DefaultDao.java
 * 版权：Copyright 2012-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：[描述]
 * 修改人：tpf
 * 修改内容：新增
 * 修改时间：2012-2-20
 */
package cn.sunline.suncard.sde.bs.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author    tpf
 * @version   1.0  2012-2-20
 * @see       [相关类/方法]
 * @since     [产品/模块版本] 
 */

public interface DefaultDao<T> {

	public void save(T t);
	
	public void delete(T t);
	
	public void saveOrUpdate(T t);
	
	public void update(T t);
	
//	public List<T> queryByTemplate(Map<String,Object> paraMap, Class clazz);
	
//	public List<T> queryForList(String cond, List<Object> params);
//	
//	public List<T> queryForList(String cond, Object... params);
//	
//	public List<T> queryForList(String cond, int pageNo, int pageSize, List<Object> params);
//	
//	public List<T> queryForList(String cond, int pageNo, int pageSize);
	
	public List<T> findByTemplate(T t, Map<String,String> clause);
	
	public List<T> findByTemplateWithSize(T t, Map<String,String> clause, int size);
	
	public List<T> findByTemplateWithOrders(T t, Map<String,String> clause, String[] ... orders);
	
	public List<T> findByTemplateWithSizeAndOrders(T t, Map<String,String> clause, int size, String[] ... orders);
	
	public List<T> findByTemplateWithPage(T t, Map<String,String> clause, int pageNo, int pageSize);
	
	public List<T> findByTemplateWithPageAndOrders(T t, Map<String,String> clause, int pageNo, int pageSize, String[] ... orders);
	
	public List<T> findByTemplate(T t);
	
	public List<T> findByTemplateWithSize(T t, int size);
	
	public List<T> findByTemplateWithOrders(T t, String[] ... orders);
	
	public List<T> findByTemplateWithSizeAndOrders(T t, int size, String[] ... orders);
	
	public List<T> findByTemplateWithPage(T t, int pageNo, int pageSize);
	
	public List<T> findByTemplateWithPageAndOrders(T t, int pageNo, int pageSize, String[] ... orders);
	
	public long queryCount(T t, Map<String,String> clause);
	
	public long queryCount(T t);
	
	public List queryWithFunc(T t, Map<String,String> clause, String[] ... fieldNames);
	
	public List queryWithFunc(T t, String[] ... fieldNames);
	
	public List queryMax(T t, Map<String,String> clause, String ... fieldNames);
	
	public List queryMin(T t, Map<String,String> clause, String ... fieldNames);
	
	public List querySum(T t, Map<String,String> clause, String ... fieldNames);
	
	public List queryAvg(T t, Map<String,String> clause, String ... fieldNames);
	
	public List queryMax(T t, String ... fieldNames);
	
	public List queryMin(T t, String ... fieldNames);
	
	public List querySum(T t, String ... fieldNames);
	
	public List queryAvg(T t, String ... fieldNames);
	
	public List queryBySql(String sql);

	public List queryByHql(String hql);

	public void deleteAll(List<T> list);

	/**
	 * 执行sql添删改操作
	 * 修改日期：2012-7-26
	 * @author: tpf
	 * @param sql
	 * @return
	 */
	public int excuteBySql(String sql);

	//long queryCount(String cond, List<Object> params);
}
