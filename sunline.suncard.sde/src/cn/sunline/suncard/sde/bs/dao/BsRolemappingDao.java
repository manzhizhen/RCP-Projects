/*
 * 文件名：BsRolemappingDao.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：BsRolemapping实体的Dao类，主要提供对此类对应表的crud操作
 * 修改人：heyong
 * 修改时间：2011-9-22
 * 修改内容：新增
 * 
 * 修改人：heyong
 * 修改时间: 2011-09-25
 * 修改内容：添加根据角色得到其对应所有功能方法getAllByRole
 */
package cn.sunline.suncard.sde.bs.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;

import cn.sunline.suncard.sde.bs.db.AbstractEntityDao;
import cn.sunline.suncard.sde.bs.entity.BsFunction;
import cn.sunline.suncard.sde.bs.entity.BsRole;
import cn.sunline.suncard.sde.bs.entity.BsRolemapping;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;

/**
  * BsRolemapping实体的Dao类，主要提供对此类对应表的crud操作
  * BsRolemapping实体的Dao类，主要提供对此类对应表的crud操作
  * @author heyong
  * @version 1.0, 2011-9-22
  * @see 
  * @since 1.0
 */
public class BsRolemappingDao extends AbstractEntityDao<BsRolemapping> {

	/**
	 * 根据角色得到其对应所有功能
	 * 
	 * @param role 角色实体类
	 * @return List<BsRoleMapping> 角色与功能映射集合
	 */
	@SuppressWarnings("unchecked")
	public List<BsRolemapping> getAllByRole(BsRole role){
		String hql = "from BsRolemapping t where t.id.bankorgId = " + role.getId().getBankorgId() 
				+ " and t.id.pcId = '" + role.getId().getPcId() 
				+ "' and t.id.roleId = '" + role.getId().getRoleId() + "'";
		Query query = HibernateUtil.getSession().createQuery(hql);
		return query.list();
	}
	
	/**
	 * 批量添加
	 * 
	 * @param bsRolemappings 角色与功能映射集合
	 */
	public void batchInsert(Set<BsRolemapping> bsRolemappings){
		Iterator<BsRolemapping> iterator = bsRolemappings.iterator();
		while (iterator.hasNext()){
			addEntity(iterator.next());
		}
	}
	/**
	 * 根据角色删除其对应所有功能
	 * 
	 * @param role 角色实体类
	 */
	public void deleteByRole(BsRole role){
		String hql = "delete from BsRolemapping t where t.id.bankorgId = " + role.getId().getBankorgId() 
				+ " and t.id.pcId = '" + role.getId().getPcId() 
				+ "' and t.id.roleId = '" + role.getId().getRoleId() + "'";
		HibernateUtil.getSession().createQuery(hql).executeUpdate();
	}
	
	/**
	 * 根据功能删除功能与角色的映射记录
	 * 
	 * @param function
	 */
	public void deleteByFunction(BsFunction function){
		String hql = "delete from BsRolemapping t where t.id.bankorgId = " + function.getId().getBankorgId()
				+ " and t.id.pcId = '" + function.getId().getPcId()
				+ "' and t.id.functionId = '" + function.getId().getFunctionId() + "'";
		HibernateUtil.getSession().createQuery(hql).executeUpdate();
	}
}
