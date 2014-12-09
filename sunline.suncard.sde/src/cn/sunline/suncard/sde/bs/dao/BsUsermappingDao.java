/*
 * 文件名：BsUsermappingDao.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：BsUsermapping实体的Dao类，主要提供对此类对应表的crud操作
 * 修改人：heyong
 * 修改时间：2011-9-22
 * 修改内容：新增
 * 
 * 修改人：heyong
 * 修改时间：2011-09-28
 * 修改内容：增加根据角色删除角色与用户映射记录 deleteByRole(BsRole role)
 */
package cn.sunline.suncard.sde.bs.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;

import cn.sunline.suncard.sde.bs.db.AbstractEntityDao;
import cn.sunline.suncard.sde.bs.entity.BsRole;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.entity.BsUsermapping;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;

/**
 * 
  * BsUsermapping实体的Dao类，主要提供对此类对应表的crud操作
  * BsUsermapping实体的Dao类，主要提供对此类对应表的crud操作
  * @author heyong
  * @version 1.0, 2011-9-22
  * @see 
  * @since 1.0
 */
public class BsUsermappingDao extends AbstractEntityDao<BsUsermapping> {
	
	@SuppressWarnings("unchecked")
	public List<BsUsermapping> getAllByUser(BsUser user){
		String hql = "from BsUsermapping t where t.id.bankorgId = " + user.getId().getBankorgId() 
				+ " and t.id.userId = '" + user.getId().getUserId() + "'";
		Query query = HibernateUtil.getSession().createQuery(hql);
		return query.list();
	}
	
	/**
	 * 批量添加
	 * 
	 * @param bsUsermappings 用户与角色映射集合
	 */
	public void batchInsert(Set<BsUsermapping> bsUsermappings){
		Iterator<BsUsermapping> iterator = bsUsermappings.iterator();
		while (iterator.hasNext()){
			addEntity(iterator.next());
		}
	}
	/**
	 * 根据user删除其对应所有角色
	 * 
	 * @param user 用户实体类
	 */
	public void deleteByUser(BsUser user){
		String hql = "delete from BsUsermapping t where t.id.bankorgId = " + user.getId().getBankorgId() 
				+ " and t.id.userId = '" + user.getId().getUserId() + "'";
		HibernateUtil.getSession().createQuery(hql).executeUpdate();
	}
	
	/**
	 * 根据role删除其对应所有功能与角色映射记录
	 * @param user 用户实体类
	 */
	public void deleteByRole(BsRole role){
		String hql = "delete from BsUsermapping t where t.id.bankorgId = " + role.getId().getBankorgId() 
				+ " and t.id.pcId = '" + role.getId().getPcId() + "'"
				+ " and t.id.roleId = '" + role.getId().getRoleId() + "'";
		HibernateUtil.getSession().createQuery(hql).executeUpdate();
	}
	
	public static void main(String[] args) {
		List<BsUsermapping> list = new BsUsermappingDao().findAll(BsUser.class);
		System.out.println(list);
	}
}
