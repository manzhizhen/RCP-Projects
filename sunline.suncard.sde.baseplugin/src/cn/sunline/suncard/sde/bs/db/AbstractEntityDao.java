/*
 * 文件名：AbstractEntityDao.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：持久层抽象类文件
 * 修改人： tpf
 * 修改时间：2011-09-22
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.bs.db;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import cn.sunline.suncard.sde.bs.util.HibernateUtil;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 持久层抽象类
 * 此类实现了IDaoInterface接口，实现了其中的方法，后续的Dao类可以继承此类，不必再重复制造此类
 * @author    tpf
 * @version   1.0, 2011-09-19
 * @see       addEntity
 * @see       deleteEntity
 * @see       updateEntity
 * @see       findByPK
 * @see       findAll
 * @since     1.0
 */
public abstract class AbstractEntityDao<T> implements IDaoInterface<T> {

	/**
	 * 实现接口IDaoInterface的实体添加方法
	 * @param entity
	 */
	@Override
	public void addEntity(T entity) throws HibernateException {
		Session session = HibernateUtil.getSession();
		session.save(entity);
	}

	/**
	 * 实现接口IDaoInterface的实体删除方法
	 * @param obj    主键对象
	 * @param clazz  实体对象的class
	 */
	@Override
	public void deleteEntity(Object obj, Class clazz) throws HibernateException {
		Session session = HibernateUtil.getSession();
		T t = findByPK(obj, clazz);
		if(t != null) {
			session.delete(findByPK(obj, clazz));
		}
	}
	
	/**
	 * 实现接口IDaoInterface的实体直接删除方法
	 * @param entity
	 */
	@Override
	public void deleteEntity(T entity) throws HibernateException {
		HibernateUtil.getSession().delete(entity);
	}

	/**
	 * 实现接口IDaoInterface的实体修改方法
	 * @param entity
	 */
	@Override
	public void updateEntity(T entity) {
		Session session = HibernateUtil.getSession();
		session.update(entity);
	}

	/**
	 * 实现接口IDaoInterface的实体主键查方法
	 * @param obj   主键对象
	 * @param clazz 实体对象的class
	 */
	@Override
	public T findByPK(Object obj, Class clazz) {
		Session session = HibernateUtil.getSession();
		T t = (T) session.get(clazz, (Serializable) obj);
		session.evict(t);
		return t;
	}

	/**
	 * 实现接口IDaoInterface的实体全查方法
	 * @param clazz 实体对象的class
	 */
	@Override
	public List<T> findAll(Class clazz) {
		Session session = HibernateUtil.getSession();
		String className = clazz.getName();
		String hql = "from " + className.substring(className.lastIndexOf(".")+1);
		//System.out.println(hql);
		Query query = session.createQuery(hql);
		
		List<T> list = query.list();
		for(T t : list) {
			session.evict(t);
		}
		
		return list;
	}

	/**
	 * 实现接口中IDaoInterface的得到数据库中记录数的方法
	 * @param clazz
	 * @return
	 * @see cn.sunline.suncard.sde.bs.db.IDaoInterface#getRecordCount(java.lang.Class)
	 */
	public int getRecordCount(Class clazz){
		List<T> list = findAll(clazz);
		if (list != null)
			return list.size();
		return 0;
	}
	
	/**
	 * 实现接口中IDaoInterface的分页查询的方法
	 * @param currPage
	 * @param pageSize
	 * @param clazz
	 * @return
	 * @see cn.sunline.suncard.sde.bs.db.IDaoInterface#findByPage(int, int, java.lang.Class)
	 */
	@Override
	public List<T> findByPage(int currPage, int pageSize, Class clazz){
		Session session = HibernateUtil.getSession();
		String className = clazz.getName();
		String hql = "from " + className.substring(className.lastIndexOf(".")+1);
		Query query = session.createQuery(hql);
		query.setFirstResult((currPage - 1) * pageSize);
		query.setMaxResults(pageSize);
		
		List<T> list = query.list();
		for(T t : list) {
			session.evict(t);
		}
		
		return list;
	}
	
	/**
	 * 实现接口IDaoInterface的实体插入或更新方法
	 * 如果不存在，则插入；如果存在，则更新。
	 * @param entity 实体对象
	 */
	@Override
	public void saveOrUpdate(T entity) throws HibernateException {
		HibernateUtil.getSession().saveOrUpdate(entity);
	}

	/**
	 * 根据hql查询实体
	 * @param hql 要执行的hql
	 * @return 查询的实体集合
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<T> findByHql(String hql) {
		List<T> list = HibernateUtil.getSession().createQuery(hql).list();
		
		for(T t : list) {
			HibernateUtil.getSession().evict(t);
		}
		
		return list;
	}

	/**
	 * 批量添加某实体对象
	 * @param entities  某实体对象集合
	 * @throws HibernateException
	 */
	@Override
	public void addBatchEntities(Collection<T> entities) throws HibernateException {
		Session session = HibernateUtil.getSession();
		for (T entity : entities) {
			session.save(entity);
		}
	}

	/**
	 * 批量删除某实体对象
	 * @param entities 某实体对象集合
	 */
	@Override
	public void deleteBatchEntities(Collection<T> entities) throws HibernateException {
		Session session = HibernateUtil.getSession();
		for (T entity : entities) {
			session.delete(entity);
		}
	}

	/**
	 * 批量插入或更新某对象实体
	 * @param entities 实体对象集合
	 * @throws HibernateException
	 */
	@Override
	public void saveOrUpdateBatchEntities(Collection<T> entities) throws HibernateException {
		Session session = HibernateUtil.getSession();
		for (T entity : entities) {
			session.saveOrUpdate(entity);
		}
	}

	/**
	 * 根据hql和参数查询实体
	 * @param i18nKey 要执行的hql的国际化key
	 * @param args hql中的参数
	 * @return 查询的实体集合
	 */
	@Override
	public List<T> findByHql(String i18nKey, Object[] args) {
		String hql = I18nUtil.getMessage(i18nKey, args);
		return findByHql(hql);
	}
	
	public static void main(String[] args) {
		String s = String.class.getName();
		System.out.println("===="+s.substring(s.lastIndexOf(".")+1));
	}
}
