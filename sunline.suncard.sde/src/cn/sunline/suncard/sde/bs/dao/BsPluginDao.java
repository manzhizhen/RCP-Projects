/*
 * 文件名：     BsPluginDao.java
 * 版权：      
 * 描述：
 * 修改人：     易振强
 * 修改时间：2011-9-29
 * 修改内容：
 */
package cn.sunline.suncard.sde.bs.dao;

import java.util.List;

import org.hibernate.Query;

import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.db.AbstractEntityDao;
import cn.sunline.suncard.sde.bs.entity.BsPlugin;
import cn.sunline.suncard.sde.bs.entity.BsPluginId;
import cn.sunline.suncard.sde.bs.entity.BsWidget;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

@SuppressWarnings("rawtypes")
public class BsPluginDao extends AbstractEntityDao<BsPlugin> {
//	private long bankorgId = ((Long)Context.getSessionMap().get(Constants.BANKORG_ID)).longValue();
//	private String pcId = (String) Context.getSessionMap().get(Constants.PC_ID);
	
	public BsPlugin findByPluginName(String pluginName, long bankorgId, String pcId) {
//		String hql = "from BsPlugin p where p.id.bankorgId = ?" +
//				" and p.id.pcId = ? and p.pluginName = ?";
		
		String hql = I18nUtil.getMessage("BsPluginDao_findByPluginName");
		
		Query query = HibernateUtil.getSession().createQuery(hql);
		query.setParameter(0, bankorgId);
		query.setParameter(1, pcId);		
		query.setParameter(2, pluginName);		
		
		List list = query.list();
		if(list == null || list.size() == 0) {
			return null;
		}
		return (BsPlugin)list.get(0);
	}
	
	public void updatePluginDb(BsPlugin oldPlugin, BsPlugin newPlugin, List<BsWidget> widgetList) {

	}
	
	public BsPlugin findByPluginId(BsPluginId pluginId) {
		BsPluginId bsPluginId = new BsPluginId(pluginId.getBankorgId(), pluginId.getPcId(), pluginId.getPluginId());
		return findByPK(bsPluginId, BsPlugin.class);
	}
	
}
