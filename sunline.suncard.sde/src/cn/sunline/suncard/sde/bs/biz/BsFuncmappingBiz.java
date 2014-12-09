/*
 * 文件名：BsFuncmappingBiz.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：BsFuncmapping业务层操作类
 * 修改人：heyong
 * 修改时间：2011-9-22
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.biz;

import java.util.List;

import cn.sunline.suncard.sde.bs.dao.BsFuncmappingDao;
import cn.sunline.suncard.sde.bs.entity.BsFuncmapping;
import cn.sunline.suncard.sde.bs.entity.BsFuncmappingId;
import cn.sunline.suncard.sde.bs.entity.BsFunction;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;

/**
 * BsFuncmapping业务层操作类
 * BsFuncmapping业务层操作类，主要提供对BsFuncmapping类的的crud操作，提供了对事务的支持
 * @author heyong
 * @version 1.0, 2011-9-22
 * @see 
 * @since 1.0
 */
public class BsFuncmappingBiz {

	//用户Dao类，业务类可以通过此类对象调用其持久化方法
		private BsFuncmappingDao bsFuncmappingDao = new BsFuncmappingDao();
		
		/**
		 * 删除方法
		 * 提供对BsFuncmapping实体类的删除方法，根据主键删除
		 * @param id BsFuncmapping主键类
		 */
		public void delete(BsFuncmappingId id) {
			HibernateUtil.openSession();
			bsFuncmappingDao.deleteEntity(id, BsFuncmapping.class);
			HibernateUtil.closeSession();
		}
		/**
		 * 添加方法
		 * 提供对BsFuncmapping实体类的添加方法
		 * @param entity BsFuncmapping实体类
		 */
		public void insert(BsFuncmapping entity) {
			HibernateUtil.openSession();
			bsFuncmappingDao.addEntity(entity);
			HibernateUtil.closeSession();
		}
		/**
		 * 更新方法
		 * 提供对BsFuncmapping实体类的更新方法
		 * @param entity BsFuncmapping实体类
		 */
		public void update(BsFuncmapping entity){
			HibernateUtil.openSession();
			bsFuncmappingDao.updateEntity(entity);
			HibernateUtil.closeSession();
		}
		/**
		 * 查找方法
		 * 提供对BsFuncmapping实体类的查找方法，根据主键查找，只能找到一条记录或找不到记录
		 * @param id BsFuncmapping主键类
		 */
		public BsFuncmapping findByPk(BsFuncmappingId id){
			return bsFuncmappingDao.findByPK(id, BsFuncmapping.class);
		}
		/**
		 * 查找
		 * 提供对BsFuncmapping实体类的查找方法，找到所有BsFuncmapping的实体记录
		 */
		public List<BsFuncmapping> getAll(){
			return bsFuncmappingDao.findAll(BsFuncmapping.class);
			
		}
		
		/**
		 * 根据功能得到其对应所有控件
		 * 
		 * @param function 功能
		 * @return List<BsFuncmapping> 功能集合
		 */
		public List<BsFuncmapping> getAllByFunction(BsFunction function){
			return bsFuncmappingDao.getAllByFunction(function);
		}
}
