/*
 * 文件名：BsUserDao.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：BsUser实体的Dao类，主要提供对此类对应表的crud操作
 * 修改人：heyong
 * 修改时间：2011-9-22
 * 修改内容：新增
 * 
 * 修改人: heyong
 * 修改时间：2011-9-28
 * 修改内容：增加根据部门得到其所有员工的方法, getAllByDepartment(BsDepartment department)
 * 			增加将所有部门的所有员工的部门Id修改为9999999999 ,  updateUserDepart(BsDepartment department)
 * 
 * 修改人: heyong
 * 修改时间： 2011-10-09
 * 修改内容：增加根据用户id查找用户信息方法，findByUserId(String userId)
 */
package cn.sunline.suncard.sde.bs.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.security.providers.encoding.Md5PasswordEncoder;

import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.db.AbstractEntityDao;
import cn.sunline.suncard.sde.bs.entity.BsDepartment;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;

/**
  * BsUser实体的Dao类，主要提供对此类对应表的crud操作
  * BsUser实体的Dao类，主要提供对此类对应表的crud操作
  * @author heyong
  * @version 1.0, 2011-9-22
  * @see 
  * @since 1.0
 */
public class BsUserDao extends AbstractEntityDao<BsUser> {
	
	/**
	 * 根据部门得到其属于该部门的所有员工
	 * 根据部门得到其属于该部门的所有员工
	 * @param department 部门实体类
	 * @return	List<BsUser> 根据部门得到的所有用户的集合
	 */
	@SuppressWarnings("unchecked")
	public List<BsUser> getAllByDepartment(BsDepartment department){
		String hql = "from BsUser t where t.id.bankorgId = " + department.getId().getBankorgId() 
				+ " and t.departmentId = '" + department.getId().getDepartmentId() + "'";
		Query query = HibernateUtil.getSession().createQuery(hql);
		return query.list();
	}
	
	/**
	 * 将部门id为参数departmentid值的用户的部门id更新为默认部门id
	 * 
	 * @param departmentId 部门id
	 */
	public void updateUserDepart(BsDepartment department){
		String hql = "update BsUser t set t.departmentId = '" + Constants.INITIAL_USER_DEPARTMENT_ID 
				+ "' where t.id.bankorgId = " + department.getId().getBankorgId() 
				+ " and t.departmentId = '" + department.getId().getDepartmentId() + "'";
		HibernateUtil.getSession().createQuery(hql).executeUpdate();
	}
	
	/**
	 * 删除多个用户
	 * 分别根据用户id删除用户
	 * @param users 要删除的用户集合
	 */
	public void delete(List<BsUser> users){
		for(int i=0; i<users.size(); i++){
			BsUser user = users.get(i);
			deleteEntity(user.getId(), BsUser.class);
		}
	}
	
	/**
	 * 根据用户id查找用户仔细信息
	 * 根据用户id查找用户仔细信息,由于用户id的惟一性，所以只有找到一个或找不到
	 * @param userId 查找用户id
	 * @return BsUser 返回查找的用户,可能为null
	 */
	public BsUser findByUserId(String userId){
		String hql = "from BsUser t where t.id.userId = '" + userId + "'";
		BsUser user = (BsUser) HibernateUtil.getSession().createQuery(hql).uniqueResult();
		HibernateUtil.getSession().evict(user);
		return user;
	}
	
	public static void main(String[] args) {
		List<BsUser> list = new BsUserDao().findAll(BsUser.class);
		System.out.println(list);
		
		Md5PasswordEncoder md2 = new Md5PasswordEncoder();
		System.out.println(md2.encodePassword("0000", "admin"));
	}
	
}
