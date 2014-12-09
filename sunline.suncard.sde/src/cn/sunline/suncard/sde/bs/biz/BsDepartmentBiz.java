/*
 * 文件名：BsDepartmentBiz.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：BsDepartment业务层操作类
 * 修改人：heyong
 * 修改时间：2011-9-25
 * 修改内容：新增
 * 
 * 修改人：heyong
 * 修改时间：2011-9-28
 * 修改内容：修改delete(ArrayList<BsDepartment> departments)方法的实现，使其支持对所属用户的删除
 * 			 增加方法delete_1(ArrayList<BsDepartment> departments)，该方法删除部门后，会将所属用户部门ID更新为9999999999
 */
package cn.sunline.suncard.sde.bs.biz;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import cn.sunline.suncard.sde.bs.dao.BsDepartmentDao;
import cn.sunline.suncard.sde.bs.dao.BsUserDao;
import cn.sunline.suncard.sde.bs.dao.BsUsermappingDao;
import cn.sunline.suncard.sde.bs.entity.BsDepartment;
import cn.sunline.suncard.sde.bs.entity.BsDepartmentId;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;

/**
 * BsDepartment业务层操作类
 * BsDepartment业务层操作类，主要提供对BsDepartment类的的crud操作，提供了对事务的支持
 * @author heyong
 * @version 1.0, 2011-9-25
 * @see 
 * @since 1.0
 */
@Transactional
public class BsDepartmentBiz extends AbstractEntityBiz<BsDepartment>{
	//用户Dao类，业务类可以通过此类对象调用其持久化方法
	private BsDepartmentDao bsDepartmentDao = new BsDepartmentDao();
	private BsUserDao userDao = new BsUserDao();
	private BsUsermappingDao usermappingDao = new BsUsermappingDao();
	
	/**
	 * 添加方法
	 * 提供对BsDepartment实体类的添加方法
	 * @param entity BsDepartment实体类
	 */
	public void insert(BsDepartment entity){
		HibernateUtil.openSession();	
		this.bsDepartmentDao.addEntity(entity);
		HibernateUtil.closeSession();
	}
	
	/**
	 * 删除方法
	 * 提供对BsDepartment实体类的删除方法，根据主键删除
	 * @param id BsDepartment主键类
	 */
	public void delete(BsDepartmentId id){
		HibernateUtil.openSession();	
		this.bsDepartmentDao.deleteEntity(id, BsDepartment.class);
		HibernateUtil.closeSession();
	}

	/**
	 * 删除方法
	 * 提供对BsDepartment实体类的删除方法，根据主键删除，可一次删除多个，在删除部门的同时，也实现了
	 * 对该部门所有员工及员工与角色对应关系的删除，涉及到bs_department、bs_user和bs_usermapping三个表
	 * @param departments BsDepartment列表
	 */
	public void delete(ArrayList<BsDepartment> departments) {
		HibernateUtil.openSession();
		if (departments != null && departments.size() > 0){
			for(int i=0; i<departments.size(); i++){
				BsDepartment department = departments.get(i);
				bsDepartmentDao.deleteEntity(department.getId(), BsDepartment.class);
				List<BsUser> users = userDao.getAllByDepartment(department);
				userDao.delete(users);
				if (users != null && users.size() > 0){
					for (int j = 0; j < users.size(); j++) {
						usermappingDao.deleteByUser(users.get(j));
					}
				}
			}
		}
		HibernateUtil.closeSession();
	}
	
	/**
	 * 删除方法
	 * 提供对BsDepartment实体类的删除方法，根据主键删除，可一次删除多个
	 * 该方法在部门删除后，会将属于该部门的所有员工的部门id更新为9999999999
	 * @param departments BsDepartment列表
	 */
	public void delete_1(ArrayList<BsDepartment> departments) {
		HibernateUtil.openSession();
		if (departments != null && departments.size() > 0){
			for(int i=0; i<departments.size(); i++){
				BsDepartment department = departments.get(i);
				bsDepartmentDao.deleteEntity(department.getId(), BsDepartment.class);
				//将所属部门ID更新为默认部门ID
				userDao.updateUserDepart(department);
			}
		}
		HibernateUtil.closeSession();
	}
	
	/**
	 * 更新方法
	 * 提供对BsDepartment实体类的更新方法
	 * @param entity BsDepartment实体类
	 */
	public void update(BsDepartment entity){
		HibernateUtil.openSession();	
		this.bsDepartmentDao.updateEntity(entity);	
		HibernateUtil.closeSession();
	}
	
	/**
	 * 查找方法
	 * 提供对BsDepartment实体类的查找方法，根据主键查找，只能找到一条记录或找不到记录
	 * @param id BsDepartment主键类
	 */
	public BsDepartment findByPk(BsDepartmentId bsDepartmentId){
		return (BsDepartment)this.bsDepartmentDao.findByPK(bsDepartmentId ,BsDepartment.class);
	}
	
	/**
	 * 查找
	 * 提供对BsDepartment实体类的查找方法，找到所有BsDepartment的实体记录
	 */
	public List<BsDepartment> getAll(){
		return this.bsDepartmentDao.findAll(BsDepartment.class);
	}

	/**
	 * 实现抽象类AbstractEntityBiz中的getRecordCount方法
	 */
	@Override
	public int getRecordCount() {
		return bsDepartmentDao.getRecordCount(BsDepartment.class);
	}
	/**
	 * 实现抽象类AbstractEntityBiz中的getByPage方法
	 */
	@Override
	public List<BsDepartment> getByPage(int currPage, int pageSize){
		return  bsDepartmentDao.findByPage(currPage, pageSize, BsDepartment.class);
	}
}
