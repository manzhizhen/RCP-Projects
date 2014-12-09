/*
 * 文件名：     BsWidgetDao.java
 * 版权：      	 Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2011-9-29
 * 修改内容：创建
 */
package cn.sunline.suncard.sde.bs.dao;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;

import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.db.AbstractEntityDao;
import cn.sunline.suncard.sde.bs.entity.BsPatch;
import cn.sunline.suncard.sde.bs.entity.BsPluginId;
import cn.sunline.suncard.sde.bs.entity.BsWidget;
import cn.sunline.suncard.sde.bs.entity.BsWidgetId;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

@SuppressWarnings({"rawtypes", "unchecked"})
public class BsWidgetDao extends AbstractEntityDao<BsWidget>{
//	private long bankorgId = ((Long)Context.getSessionMap().get(Constants.BANKORG_ID)).longValue();
//	private String pcId = (String) Context.getSessionMap().get(Constants.PC_ID);
	 
	private BsFuncmappingDao bsFuncmappingDao = new BsFuncmappingDao();
	
	public void delete(BsWidget bsWidget) {
		BsWidgetId widgetId = bsWidget.getId();
		deleteEntity(bsWidget.getId(), BsWidget.class);
		bsFuncmappingDao.deleteByWidgetId(widgetId);
	}
	
	public void deleteByPluginId(BsPluginId pluginId) {
//		String hql = "from BsWidget bw where bw.pluginId = ? and " +
//				"bw.id.bankorgId = ?" +
//				" and bw.id.pcId = ?";
		
		String hql = I18nUtil.getMessage("BsWidgetDao_deleteByPluginId");
		
		Query query = HibernateUtil.getSession().createQuery(hql);
		query.setParameter(0, pluginId.getPluginId());
		query.setParameter(1, pluginId.getBankorgId());
		query.setParameter(2, pluginId.getPcId());

		List<BsPatch> list = query.list();
		Iterator it = list.iterator();
		while(it.hasNext()) {
			BsWidget bsWidget = (BsWidget)it.next();
			
			// 删除功能控件映射表中的信息
			BsWidgetId widgetId = bsWidget.getId();
			bsFuncmappingDao.deleteByWidgetId(widgetId);
			
			// 删除该控件
			delete(bsWidget);
		}
	}
	
	/**
	 * 查找父控件下的所有子控件
	 * 
	 * @param parWidgetId
	 * @return
	 */
	public List<BsWidget> findWidgetByPar(String parWidgetId, long bankorgId, String pcId) {
//		String hql = "from BsWidget bw where bw.parWidgetId = ? and " +
//				"bw.id.bankorgId = ?" +
//				" and bw.id.pcId = ?";
		
		String hql = I18nUtil.getMessage("BsWidgetDao_findWidgetByPar");
		
		Query query = HibernateUtil.getSession().createQuery(hql);
		query.setParameter(0, parWidgetId);
		query.setParameter(1, bankorgId);
		query.setParameter(2, pcId);

		return query.list();
	}
}
