/*
 * 文件名：BsRoleBiz.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：BsRole业务层操作类
 * 修改人：heyong
 * 修改时间：2011-9-22
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.biz;

import java.util.ArrayList;
import java.util.List;

import cn.sunline.suncard.sde.bs.dao.BsRoleDao;
import cn.sunline.suncard.sde.bs.dao.BsRolemappingDao;
import cn.sunline.suncard.sde.bs.dao.BsUsermappingDao;
import cn.sunline.suncard.sde.bs.entity.BsRole;
import cn.sunline.suncard.sde.bs.entity.BsRoleId;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;

/**
 * BsRole业务层操作类
 * BsRole业务层操作类，主要提供对BsRole类的的crud操作，提供了对事务的支持
 * @author heyong
 * @version 1.0, 2011-9-22
 * @see 
 * @since 1.0
 */
public class BsRoleBiz extends AbstractEntityBiz<BsRole>{

		//用户Dao类，业务类可以通过此类对象调用其持久化方法
		private BsRoleDao bsRoleDao = new BsRoleDao();
		private BsRolemappingDao bsRolemappingDao = new BsRolemappingDao();
		private BsUsermappingDao bsUsermappingDao = new BsUsermappingDao();
		
		/**
		 * 删除方法
		 * 提供对BsRole实体类的删除方法，根据主键删除，角色删除后，再删除角色与功能映射关系,然后删除角色与用户映射关系
		 * @param id BsRole主键类
		 */
		public void delete(BsRoleId id) {
			HibernateUtil.openSession();
			bsRoleDao.deleteEntity(id, BsRole.class);
			BsRole role = bsRoleDao.findByPK(id, BsRole.class);
			bsRolemappingDao.deleteByRole(role);
			bsUsermappingDao.deleteByRole(role);
			HibernateUtil.closeSession();
		}
		
		/**
		 * 删除方法
		 * 提供对BsRole实体类的删除方法，根据主键删除，可一次删除多个，角色删除后，再删除角色与功能映射关系,然后删除角色与用户映射关系
		 * @param roles BsRole列表
		 */
		public void delete(ArrayList<BsRole> roles) {
			HibernateUtil.openSession();
			if (roles != null && roles.size() > 0){
				for(int i=0; i<roles.size(); i++){
					BsRole role = roles.get(i);
					bsRoleDao.deleteEntity(role.getId(), BsRole.class);
					bsRolemappingDao.deleteByRole(role);
					bsUsermappingDao.deleteByRole(role);
				}
			}
			HibernateUtil.closeSession();
		}
		/**
		 * 添加方法
		 * 提供对BsRole实体类的添加方法
		 * @param entity BsRole实体类
		 */
		public void insert(BsRole entity) {
			HibernateUtil.openSession();
			bsRoleDao.addEntity(entity);
			bsRolemappingDao.batchInsert(entity.getBsRolemappings());
			HibernateUtil.closeSession();
		}
		
		/**
		 * 更新方法
		 * 由于角色与功能具体映射关系，在更新角色时，先删除所有角色对应的功能，然后再重新添加
		 * 提供对BsRole实体类的更新方法
		 * @param entity BsRole实体类
		 */
		public void update(BsRole entity){
			HibernateUtil.openSession();
			bsRoleDao.updateEntity(entity);
			bsRolemappingDao.deleteByRole(entity);
			bsRolemappingDao.batchInsert(entity.getBsRolemappings());
			HibernateUtil.closeSession();
		}
		
		/**
		 * 查找方法
		 * 提供对BsRole实体类的查找方法，根据主键查找，只能找到一条记录或找不到记录
		 * @param id BsRole主键类
		 */
		public BsRole findByPk(BsRoleId id){
			return bsRoleDao.findByPK(id, BsRole.class);
		}
		
		/**
		 * 查找
		 * 提供对BsRole实体类的查找方法，找到所有BsRole的实体记录
		 */
		public List<BsRole> getAll(){
			return bsRoleDao.findAll(BsRole.class);
			
		}

		/**
		 * 实现抽象类AbstractEntityBiz中的getRecordCount方法
		 */
		@Override
		public int getRecordCount() {
			return bsRoleDao.getRecordCount(BsRole.class);
		}
		/**
		 * 实现抽象类AbstractEntityBiz中的getByPage方法
		 */
		@Override
		public List<BsRole> getByPage(int currPage, int pageSize) {
			return bsRoleDao.findByPage(currPage, pageSize, BsRole.class);
		}
		
		
}
