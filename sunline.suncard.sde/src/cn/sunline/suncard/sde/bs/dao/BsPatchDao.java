/*
 * 文件名：BsPatchDao.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：BsPatch实体的Dao类，主要提供对此类对应表的crud操作
 * 修改人：易振强
 * 修改时间：2011-9-22
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.dao;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;

import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.db.AbstractEntityDao;
import cn.sunline.suncard.sde.bs.entity.BsPatch;
import cn.sunline.suncard.sde.bs.entity.BsPatchId;
import cn.sunline.suncard.sde.bs.entity.BsPluginId;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 
 * BsPatch实体的Dao类，主要提供对此类对应表的crud操作
 * BsPatch实体的Dao类，主要提供对此类对应表的crud操作
 * @author 易振强
 * @version 1.0, 2011-9-22
 * @see 
 * @since 1.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class BsPatchDao extends AbstractEntityDao<BsPatch> {
//	private long bankorgId = ((Long)Context.getSessionMap().get(Constants.BANKORG_ID)).longValue();
//	private String pcId = (String) Context.getSessionMap().get(Constants.PC_ID);
	
	private BsPatchmappingDao bsPatchmappingDao = new BsPatchmappingDao();
	
	public List<BsPatch> findByPluginName(String pluginName, long bankorgId, String pcId) {
//		String hql = "from BsPatch p, BsPlugin b where p.pluginId = b.id.pluginId " +
//				"and b.pluginName = ? and p.id.bankorgId = ?" + 
//				" and p.id.pcId = ?";
		
		String hql = I18nUtil.getMessage("BsPatchDao_findByPluginName");
		
		Query query = HibernateUtil.getSession().createQuery(hql);
		query.setParameter(0, pluginName);
		query.setParameter(1, bankorgId);	
		query.setParameter(2, pcId);		
		
		return query.list();
	}
	
	public List<BsPatch> findByPluginId(BsPluginId pluginId) {
//		String hql = "from BsPatch p where p.pluginId = ? and p.id.bankorgId = ?" + 
//				" and p.id.pcId = ?";
		
		String hql = I18nUtil.getMessage("BsPatchDao_findByPluginId");
		
		Query query = HibernateUtil.getSession().createQuery(hql);
		query.setParameter(0, pluginId.getPluginId());
		query.setParameter(1, pluginId.getBankorgId());		
		query.setParameter(2, pluginId.getPcId());
		
		return query.list();
	}
	
	public void delete(BsPatch bsPatch) {
		BsPatchId patchId = bsPatch.getId();
		deleteEntity(bsPatch.getId(), BsPatch.class);
		bsPatchmappingDao.deleteByPatchId(patchId);
	}
	
	public void deleteByPluginId(BsPluginId pluginId) {
//		String hql = "from BsPatch p where p.pluginId = ? and " +
//				"p.id.bankorgId = ?" +
//				" and p.id.pcId = ?";
		
		String hql = I18nUtil.getMessage("BsPatchDao_deleteByPluginId");
		
		Query query = HibernateUtil.getSession().createQuery(hql);
		query.setParameter(0, pluginId.getPluginId());
		query.setParameter(1, pluginId.getBankorgId());
		query.setParameter(2, pluginId.getPcId());

		List<BsPatch> list = query.list();
		Iterator it = list.iterator();
		while(it.hasNext()) {
			BsPatch bsPatch = (BsPatch)it.next();
			delete(bsPatch);
		}
	}
	
}
