/*
 * 文件名：BsFunctionBiz.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：BsFunction业务层操作类
 * 修改人：heyong
 * 修改时间：2011-9-22
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.biz;

import java.util.ArrayList;
import java.util.List;

import cn.sunline.suncard.sde.bs.dao.BsFuncmappingDao;
import cn.sunline.suncard.sde.bs.dao.BsFunctionDao;
import cn.sunline.suncard.sde.bs.dao.BsRolemappingDao;
import cn.sunline.suncard.sde.bs.entity.BsFunction;
import cn.sunline.suncard.sde.bs.entity.BsFunctionId;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;

/**
 * BsFunction业务层操作类
 * BsFunction业务层操作类，主要提供对BsFunction类的的crud操作，提供了对事务的支持
 * @author heyong
 * @version 1.0, 2011-9-22
 * @see 
 * @since 1.0
 */
public class BsFunctionBiz extends AbstractEntityBiz<BsFunction>{

		//用户Dao类，业务类可以通过此类对象调用其持久化方法
		private BsFunctionDao bsFunctionDao = new BsFunctionDao();
		private BsRolemappingDao rolemappingDao = new BsRolemappingDao();
		private BsFuncmappingDao funcmappingDao = new BsFuncmappingDao();
		/**
		 * 删除方法
		 * 提供对BsFunction实体类的删除方法，根据主键删除,同时删除功能与角色的映射关系
		 * @param id BsFunction主键类
		 */
		public void delete(BsFunctionId id) {
			HibernateUtil.openSession();
			bsFunctionDao.deleteEntity(id, BsFunction.class);
			rolemappingDao.deleteByFunction(new BsFunction(id));
			HibernateUtil.closeSession();
		}
		
		/**
		 * 删除方法
		 * 提供对BsFunction实体类的删除方法，根据主键删除，可一次删除多个，同时删除功能与角色的映射关系
		 * @param functions BsFunction列表
		 */
		public void delete(ArrayList<BsFunction> functions) {
			HibernateUtil.openSession();
			if (functions != null && functions.size() > 0){
				for(int i=0; i<functions.size(); i++){
					BsFunction function = functions.get(i);
					bsFunctionDao.deleteEntity(function.getId(), BsFunction.class);
					rolemappingDao.deleteByFunction(function);
					funcmappingDao.deleteAllByFunction(function);
				}
			}
			HibernateUtil.closeSession();
		}
		
		/**
		 * 添加方法
		 * 提供对BsFunction实体类的添加方法
		 * @param entity BsFunction实体类
		 */
		public void insert(BsFunction entity) {
			HibernateUtil.openSession();
			bsFunctionDao.addEntity(entity);
			//添加映射关系
			funcmappingDao.batchInsert(entity.getBsFuncmappings());
			HibernateUtil.closeSession();
		}
		
		/**
		 * 更新方法
		 * 提供对BsFunction实体类的更新方法
		 * @param entity BsFunction实体类
		 */
		public void update(BsFunction entity){
			HibernateUtil.openSession();
			bsFunctionDao.updateEntity(entity);
			funcmappingDao.deleteAllByFunction(entity);
			funcmappingDao.batchInsert(entity.getBsFuncmappings());
			HibernateUtil.closeSession();
		}
		
		/**
		 * 查找方法
		 * 提供对BsFunction实体类的查找方法，根据主键查找，只能找到一条记录或找不到记录
		 * @param id BsFunction主键类
		 */
		public BsFunction findByPk(BsFunctionId id){
			return bsFunctionDao.findByPK(id, BsFunction.class);
		}
		
		/**
		 * 查找
		 * 提供对BsFunction实体类的查找方法，找到所有BsFunction的实体记录
		 */
		public List<BsFunction> getAll(){
			return bsFunctionDao.findAll(BsFunction.class);
		}

		
		/**
		 * 实现抽象类AbstractEntityBiz中的getRecordCount方法
		 */
		@Override
		public int getRecordCount() {
			return bsFunctionDao.getRecordCount(BsFunction.class);
		}
		/**
		 * 实现抽象类AbstractEntityBiz中的getByPage方法
		 */
		@Override
		public List<BsFunction> getByPage(int currPage, int pageSize) {
			return bsFunctionDao.findByPage(currPage, pageSize, BsFunction.class);
		}
}
