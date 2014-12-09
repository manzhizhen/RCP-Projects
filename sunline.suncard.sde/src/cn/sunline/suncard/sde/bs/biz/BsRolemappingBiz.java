/*
 * 文件名：BsRolemappingBiz.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：BsRolemapping业务层操作类
 * 修改人：heyong
 * 修改时间：2011-9-22
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.biz;

import java.util.List;
import java.util.Set;

import cn.sunline.suncard.sde.bs.dao.BsRolemappingDao;
import cn.sunline.suncard.sde.bs.entity.BsRole;
import cn.sunline.suncard.sde.bs.entity.BsRolemapping;
import cn.sunline.suncard.sde.bs.entity.BsRolemappingId;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;

/**
 * BsRolemapping业务层操作类
 * BsRolemapping业务层操作类，主要提供对BsRolemapping类的的crud操作，提供了对事务的支持
 * @author heyong
 * @version 1.0, 2011-9-22
 * @see 
 * @since 1.0
 */
public class BsRolemappingBiz {

		//用户Dao类，业务类可以通过此类对象调用其持久化方法
		private BsRolemappingDao bsRolemappingDao = new BsRolemappingDao();
		
		/**
		 * 删除方法
		 * 提供对BsRolemapping实体类的删除方法，根据主键删除
		 * @param id BsRolemapping主键类
		 */
		public void delete(BsRolemappingId id) {
			HibernateUtil.openSession();
			bsRolemappingDao.deleteEntity(id, BsRolemapping.class);
			HibernateUtil.closeSession();
		}
		
		
		/**
		 * 添加方法
		 * 提供对BsRolemapping实体类的添加方法
		 * @param entity BsRolemapping实体类
		 */
		public void insert(BsRolemapping entity) {
			HibernateUtil.openSession();
			bsRolemappingDao.addEntity(entity);
			HibernateUtil.closeSession();
		}
		
		/**
		 * 批量添加方法
		 * 提供对BsRolemapping实体类的批量添加方法
		 * @param bsRolemappings BsRolemapping集合类
		 */
		public void batchInsert(Set<BsRolemapping> bsRolemappings) {
			HibernateUtil.openSession();
			bsRolemappingDao.batchInsert(bsRolemappings);
			HibernateUtil.closeSession();
		}
		
		/**
		 * 更新方法
		 * 提供对BsRolemapping实体类的更新方法
		 * @param entity BsRolemapping实体类
		 */
		public void update(BsRolemapping entity){
			HibernateUtil.openSession();
			bsRolemappingDao.updateEntity(entity);
			HibernateUtil.closeSession();
		}
		
		/**
		 * 查找方法
		 * 提供对BsRolemapping实体类的查找方法，根据主键查找，只能找到一条记录或找不到记录
		 * @param id BsRolemapping主键类
		 */
		public BsRolemapping findByPk(BsRolemappingId id){
			return bsRolemappingDao.findByPK(id, BsRolemapping.class);
		}
		
		/**
		 * 查找
		 * 提供对BsRolemapping实体类的查找方法，找到所有BsRolemapping的实体记录
		 */
		public List<BsRolemapping> getAll(){
			return bsRolemappingDao.findAll(BsRolemapping.class);
			
		}
		
		/**
		 * 根据角色查找角色对应的所有功能
		 * 
		 * @param role 角色
		 * @return List<BsRolemapping> 功能集合
		 */
		public List<BsRolemapping> getAllByRole(BsRole role){
			return bsRolemappingDao.getAllByRole(role);
		}
		
		/**
		 * 根据角色删除角色对应的所有功能
		 * 
		 * @param role 角色
		 */
		public void deleteByRole(BsRole role){
			HibernateUtil.openSession();
			bsRolemappingDao.deleteByRole(role);
			HibernateUtil.closeSession();
		}
}
