/*
 * 文件名：BsPatchmappingBiz.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：BsPatchmapping业务层操作类
 * 修改人：易振强
 * 修改时间：2011-9-22
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.biz;

import java.util.List;

import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.dao.BsPatchmappingDao;
import cn.sunline.suncard.sde.bs.entity.BsPatch;
import cn.sunline.suncard.sde.bs.entity.BsPatchmapping;
import cn.sunline.suncard.sde.bs.entity.BsPatchmappingId;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;

/**
 * BsPatchmapping业务层操作类
 * BsPatchmapping业务层操作类，主要提供对BsPatchmapping类的的crud操作，提供了对事务的支持
 * 
 * @author 易振强
 * @version 1.0, 2011-9-22
 * @see
 * @since 1.0
 */
public class BsPatchmappingBiz {
	// 用户Dao类，业务类可以通过此类对象调用其持久化方法
	private BsPatchmappingDao bsPatchmappingDao = new BsPatchmappingDao();
	private long bankorgId = ((Long)Context.getSessionMap().get(Constants.BANKORG_ID)).longValue();
	private String pcId = (String) Context.getSessionMap().get(Constants.PC_ID);
	
	/**
	 * 删除方法 提供对BsPatchmapping实体类的删除方法，根据主键删除
	 * 
	 * @param id
	 *            BsPatchmapping主键类
	 */
	public void delete(BsPatchmappingId id) {
		HibernateUtil.openSession();
		bsPatchmappingDao.deleteEntity(id, BsPatchmapping.class);
		HibernateUtil.closeSession();
	}

	/**
	 * 添加方法 提供对BsPatchmapping实体类的添加方法
	 * 
	 * @param entity
	 *            BsPatchmapping实体类
	 */
	public void insert(BsPatchmapping entity) {
		HibernateUtil.openSession();
		bsPatchmappingDao.addEntity(entity);
		HibernateUtil.closeSession();
	}

	/**
	 * 更新方法 提供对BsPatchmapping实体类的更新方法
	 * 
	 * @param entity
	 *            BsPatchmapping实体类
	 */
	public void update(BsPatchmapping entity) {
		HibernateUtil.openSession();
		bsPatchmappingDao.updateEntity(entity);
		HibernateUtil.closeSession();
	}

	/**
	 * 查找方法 提供对BsPatchmapping实体类的查找方法，根据主键查找，只能找到一条记录或找不到记录
	 * 
	 * @param id
	 *            BsPatchmapping主键类
	 */
	public BsPatchmapping findByPk(BsPatchmappingId id) {
		return bsPatchmappingDao.findByPK(id, BsPatchmapping.class);
	}

	/**
	 * 查找 提供对BsPatchmapping实体类的查找方法，找到所有BsPatchmapping的实体记录
	 */
	public List<BsPatchmapping> getAll() {
		return bsPatchmappingDao.findAll(BsPatchmapping.class);

	}

	// 通过插件版本得到 补丁列表
	public List<BsPatch> findByPluginVersion(String pluginVersion) {
		return bsPatchmappingDao.findByPluginVersion(pluginVersion, bankorgId, pcId);
	}
}
