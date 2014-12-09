/*
 * 文件名：IDaoInterface.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：持久层顶级接口文件
 * 修改人： tpf
 * 修改时间：2011-09-22
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.bs.db;

import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;

/**
 * 持久层顶级接口
 * Dao持久层类必须实现该接口
 * @author    tpf
 * @version   1.0, 2011-09-19
 * @see       addEntity
 * @see       deleteEntity
 * @see       updateEntity
 * @see       findByPK
 * @see       findAll
 * @since     1.0
 */
@SuppressWarnings("rawtypes")
public interface IDaoInterface<T> {

	/**
	 * 实体添加方法
	 * @param entity 添加实体对象
	 * @throws HibernateException
	 */
	public void addEntity(T entity) throws HibernateException;
	
	/**
	 * 批量添加某实体对象
	 * @param entities  某实体对象集合
	 * @throws HibernateException
	 */
	public void addBatchEntities(Collection<T> entities) throws HibernateException;
	
	/**
	 * 实体删除方法
	 * @param obj
	 * @throws HibernateException
	 */
	public void deleteEntity(Object obj, Class clazz) throws HibernateException;
	
	/**
	 * 直接删除实体对象
	 * @param entity 实体对象
	 * @throws HibernateException
	 */
	public void deleteEntity(T entity) throws HibernateException;
	
	/**
	 * 批量删除某实体对象
	 * @param entities 某实体对象集合
	 */
	public void deleteBatchEntities(Collection<T> entities) throws HibernateException;
	
	/**
	 * 实体更新方法
	 * @param entity 更新实体对象
	 * @throws HibernateException
	 */
	public void updateEntity(T entity) throws HibernateException;
	
	/**
	 * 插入或更新的方法
	 * @throws HibernateException
	 */
	public void saveOrUpdate(T entity) throws HibernateException;
	
	/**
	 * 批量插入或更新某对象实体
	 * @param entities 实体对象集合
	 * @throws HibernateException
	 */
	public void saveOrUpdateBatchEntities(Collection<T> entities) throws HibernateException;
	
	/**
	 * 实体主键查找方法
	 * @param obj  主键
	 * @param clazz 实体对象的class
	 * @return
	 */
	public Object findByPK(Object obj, Class clazz);
	
	/**
	 * 实体全查方法
	 * @param clazz
	 * @return
	 */
	public List<T> findAll(Class clazz);
	
	/**
	 * 得到数据库中记录总数
	 * 
	 * @param clazz
	 * @return
	 */
	public int getRecordCount(Class clazz);
	
	/**
	 * 分页查找
	 * 
	 * @param currPage  当前页
	 * @param pageSize	每页显示记录数
	 * @param clazz
	 * @return
	 */
	public List<T> findByPage(int currPage, int pageSize, Class clazz);
	
	/**
	 * 根据hql查询实体
	 * @param hql 要执行的hql
	 * @return
	 */
	public List<T> findByHql(String hql);
	
	/**
	 * 根据hql和参数查询实体
	 * @param i18nKey 要执行的hql的国际化key
	 * @param args hql中的参数
	 * @return 查询的实体集合
	 */
	public List<T> findByHql(String i18nKey, Object[] args);
}
