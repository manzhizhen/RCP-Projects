/*
 * 文件名：BsPcBiz.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：BsPc业务层操作类
 * 修改人：heyong
 * 修改时间：2011-9-22
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.biz;

import java.util.List;

import cn.sunline.suncard.sde.bs.dao.BsPcDao;
import cn.sunline.suncard.sde.bs.entity.BsPc;
import cn.sunline.suncard.sde.bs.entity.BsPcId;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;

/**
 * BsPc业务层操作类
 * BsPc业务层操作类，主要提供对BsPc类的的crud操作，提供了对事务的支持
 * @author heyong
 * @version 1.0, 2011-9-22
 * @see 
 * @since 1.0
 */
public class BsPcBiz {

		//用户Dao类，业务类可以通过此类对象调用其持久化方法
		private BsPcDao bsPcDao = new BsPcDao();
		
		/**
		 * 删除方法
		 * 提供对BsPc实体类的删除方法，根据主键删除
		 * @param id BsPc主键类
		 */
		public void delete(BsPcId id) {
			HibernateUtil.openSession();
			bsPcDao.deleteEntity(id, BsPc.class);
			HibernateUtil.closeSession();
		}
		
		
		/**
		 * 添加方法
		 * 提供对BsPc实体类的添加方法
		 * @param entity BsPc实体类
		 */
		public void insert(BsPc entity) {
			HibernateUtil.openSession();
			bsPcDao.addEntity(entity);
			HibernateUtil.closeSession();
		}
		
		/**
		 * 更新方法
		 * 提供对BsPc实体类的更新方法
		 * @param entity BsPc实体类
		 */
		public void update(BsPc entity){
			HibernateUtil.openSession();
			bsPcDao.updateEntity(entity);
			HibernateUtil.closeSession();
		}
		
		/**
		 * 查找方法
		 * 提供对BsPc实体类的查找方法，根据主键查找，只能找到一条记录或找不到记录
		 * @param id BsPc主键类
		 */
		public BsPc findByPk(BsPcId id){
			return bsPcDao.findByPK(id, BsPc.class);
		}
		
		/**
		 * 查找
		 * 提供对BsPc实体类的查找方法，找到所有BsPc的实体记录
		 */
		public List<BsPc> getAll(){
			return bsPcDao.findAll(BsPc.class);
			
		}
}
