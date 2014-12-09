/*
 * 文件名：BsPatchmappingDao.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：BsPatchmapping实体的Dao类，主要提供对此类对应表的crud操作
 * 修改人：易振强
 * 修改时间：2011-9-22
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.dao;

import java.util.List;

import org.hibernate.Query;

import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.db.AbstractEntityDao;
import cn.sunline.suncard.sde.bs.entity.BsPatch;
import cn.sunline.suncard.sde.bs.entity.BsPatchId;
import cn.sunline.suncard.sde.bs.entity.BsPatchmapping;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;

/**
 * 
  * BsPatchmapping实体的Dao类，主要提供对此类对应表的crud操作
  * BsPatchmapping实体的Dao类，主要提供对此类对应表的crud操作
  * @author 易振强
  * @version 1.0, 2011-9-22
  * @see 
  * @since 1.0
 */
@SuppressWarnings({"unchecked"})
public class BsPatchmappingDao extends AbstractEntityDao<BsPatchmapping> {
//	private long bankorgId = ((Long)Context.getSessionMap().get(Constants.BANKORG_ID)).longValue();
//	private String pcId = (String)Context.getSessionMap().get(Constants.PC_ID);
	
	// 通过插件版本得到 补丁列表
	public List<BsPatch> findByPluginVersion(String pluginVersion, long bankorgId, String pcId) {
		String hqlStr = "from BsPatchmapping p where p.id.bankorgId = ?" +
				" and p.id.pcId = ? and p.id.pluginVer = ?";
		
		Query query = HibernateUtil.getSession().createQuery(hqlStr);
		query.setParameter(0, bankorgId);
		query.setParameter(1, pcId);		
		query.setParameter(2, pluginVersion);		

		return query.list();
	}
	
	public void deleteByPatchId(BsPatchId patchId) {
		String hql = "delete BsPatchmapping bp where bp.id.patchId = ? and " +
				"bp.id.bankorgId = ? and bp.id.pcId = ?";
		
		Query query = HibernateUtil.getSession().createQuery(hql);
		query.setParameter(0, patchId.getPatchId());
		query.setParameter(1, patchId.getBankorgId());
		query.setParameter(2, patchId.getPcId());
		query.executeUpdate();
	}
}
