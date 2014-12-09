/**
 * 文件名：     BsPluginlogDao.java
 * 版权：      
 * 描述：
 * 修改人：     易振强
 * 修改时间：2011-9-28
 * 修改内容：
 */
package cn.sunline.suncard.sde.bs.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;

import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.db.AbstractEntityDao;
import cn.sunline.suncard.sde.bs.entity.BsPlugin;
import cn.sunline.suncard.sde.bs.entity.BsPluginlog;
import cn.sunline.suncard.sde.bs.entity.BsPluginlogId;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

@SuppressWarnings({"rawtypes", "unchecked"})
public class BsPluginlogDao extends AbstractEntityDao<BsPluginlog> {
//	private static String pcId = (String) Context.getSessionMap().get(Constants.PC_ID); 
//	private static Long bankorgId = (Long)Context.getSessionMap().get(Constants.BANKORG_ID);
	
	public List<BsPluginlog> getDateToDate(Date start, Date end, long bankorgId, String pcId) {
//		String hql = "from BsPluginlog as pl where pl.processDate >= ? and pl.processDate <= ? " +
//				"and pl.id.bankorgId = ? and pl.id.pcId = ?" ;
		String hql = I18nUtil.getMessage("BsPluginlogDao_getDateToDate");
		
		Query query = HibernateUtil.getSession().createQuery(hql);
		
		query.setParameter(0, start);
		query.setParameter(1, end);
		query.setParameter(2, bankorgId);
		query.setParameter(3, pcId);
		return  query.list();
	}
	
	// 通过插件ID和补丁编号找出安装补丁的日志。
	public List<BsPluginlog> findByPatchPluginId(String patchId, String pluginId, long bankorgId, String pcId) {
//		String hql = "from BsPluginlog as pl where pl.patchId = ? and pl.pluginId = ?" +
//				" and pl.processType = ? and pl.id.bankorgId = ?" +
//				" and pl.id.pcId = ?" ;
		
		String hql = I18nUtil.getMessage("BsPluginlogDao_findByPatchPluginId");
		
		Query query = HibernateUtil.getSession().createQuery(hql);
		query.setParameter(0, patchId);
		query.setParameter(1, pluginId);
		query.setParameter(2, "P");	
		query.setParameter(3, bankorgId);	
		query.setParameter(4, pcId);
		
		return  query.list();
	}
	
	// 获得最大的日志号
	public long getMaxSeq(long bankorgId, String pcId) {
//		String hql = "select max(l.id.logSeq) from BsPluginlog as l " +
//				"where l.id.bankorgId = ?" +
//				" and l.id.pcId = ?";
		
		String hql = I18nUtil.getMessage("BsPluginlogDao_getMaxSeq");
		
		Query query = HibernateUtil.getSession().createQuery(hql);
		query.setParameter(0, bankorgId);	
		query.setParameter(1, pcId);
		
		Iterator it = query.iterate();
		if (it.hasNext()) {
			Object object = it.next();
			if(object instanceof Long) {
				return ((Long)object).longValue();
			}else {
				return 0L;
			}
		} else {
			return 0L;
		}
	}
	
	// 添加新日志
	public void addPluginlog(String pluginId, String processType, Timestamp processDate,
			String srcPluginVer, String pluginVer, String patchId,
			String replaceUrl, long bankorgId, String pcId) {
		BsPluginlogId bsPluginlogId = new BsPluginlogId(bankorgId, pcId, getMaxSeq(bankorgId, pcId) + 1);
		BsPluginlog bsPluginlog = new BsPluginlog();
		bsPluginlog.setId(bsPluginlogId);
		bsPluginlog.setPluginId(pluginId);
		bsPluginlog.setProcessType(processType);
		bsPluginlog.setProcessDate(processDate);
		bsPluginlog.setSrcPluginVer(srcPluginVer);
		bsPluginlog.setPluginVer(pluginVer);
		bsPluginlog.setPatchId(patchId);
		bsPluginlog.setReplaceUrl(replaceUrl);
		
		addEntity(bsPluginlog);
		
	}
	
	// 通过插件及其操作类型找出对应的日志。
	public List<BsPluginlog> findByBsPlugin(BsPlugin bsPlugin, String processType) {
//		String hql = "from BsPluginlog as pl where pl.pluginId = ?" +
//				" and pl.processType = ? and pl.id.bankorgId = ?" +
//				" and pl.id.pcId = ?" ;
		
		String hql = I18nUtil.getMessage("BsPluginlogDao_findByBsPlugin");

		Query query = HibernateUtil.getSession().createQuery(hql);
		query.setParameter(0, bsPlugin.getId().getPluginId());
		query.setParameter(1, processType);
		query.setParameter(2, bsPlugin.getId().getBankorgId());	
		query.setParameter(3, bsPlugin.getId().getPcId());
		
		return  query.list();
	}
	
	public static void main(String[] args) {
	}
	
}
