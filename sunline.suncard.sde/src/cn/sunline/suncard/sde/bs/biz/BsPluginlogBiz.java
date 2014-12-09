/**
 * 文件名：     BsPluginlogBiz.java
 * 版权：      
 * 描述：
 * 修改人：     易振强
 * 修改时间：2011-9-28
 * 修改内容：创建
 */
package cn.sunline.suncard.sde.bs.biz;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.dao.BsPluginlogDao;
import cn.sunline.suncard.sde.bs.entity.BsPlugin;
import cn.sunline.suncard.sde.bs.entity.BsPluginlog;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;

public class BsPluginlogBiz {
	private BsPluginlogDao bsPluginlogDao = new BsPluginlogDao();
	
	private long bankorgId = ((Long)Context.getSessionMap().get(Constants.BANKORG_ID)).longValue();
	private String pcId = (String) Context.getSessionMap().get(Constants.PC_ID);
	
	public List<BsPluginlog> getAllLog() {
		return bsPluginlogDao.findAll(BsPluginlog.class);
	}
	
	public List<BsPluginlog> getDateToDate(Date start, Date end) {
		return bsPluginlogDao.getDateToDate(start, end, bankorgId, pcId);
	}
	
	public void delete(BsPluginlog bsPluginlog) {
		HibernateUtil.openSession();
		bsPluginlogDao.deleteEntity(bsPluginlog.getId(), BsPluginlog.class);
		HibernateUtil.closeSession();
	}
	
	public void add(BsPluginlog bsPluginlog) {
		HibernateUtil.openSession();
		bsPluginlogDao.addEntity(bsPluginlog);
		HibernateUtil.closeSession();

	}
	
	// 通过补丁Id 和 插件Id找到相应的补丁安装日志。
	public BsPluginlog findByPluginPatchId(String patchId, String pluginId) {
		// 找出该补丁安装在该插件上的所有历史记录。
		List<BsPluginlog> list =  bsPluginlogDao.findByPatchPluginId(patchId, pluginId, bankorgId, pcId);

		Timestamp timestamp = null;
		BsPluginlog pluginlog = null;
		for(BsPluginlog bsPluginlog : list) {
			if(timestamp == null) {
				timestamp = bsPluginlog.getProcessDate();
				pluginlog = bsPluginlog;
				continue;
			}
			
			if(timestamp.compareTo(bsPluginlog.getProcessDate()) < 0) {
				timestamp = bsPluginlog.getProcessDate();
				pluginlog = bsPluginlog;
			}
		}
		
		return pluginlog;
	}
	
	public void addPluginlog(String pluginId, String processType, Timestamp processDate,
			String srcPluginVer, String pluginVer, String patchId,
			String replaceUrl) {
		HibernateUtil.openSession();
		bsPluginlogDao.addPluginlog(pluginId, processType, processDate, srcPluginVer, pluginVer, patchId, replaceUrl, bankorgId, pcId);
		HibernateUtil.closeSession();
	}
	
	public String findDateByBsPlugin(BsPlugin bsPlugin) {
		List<BsPluginlog> list = bsPluginlogDao.findByBsPlugin(bsPlugin, "A");
		
		if(list != null && !list.isEmpty()) {
			return list.get(0).getProcessDate().toString();
		}else {
			return null;
		}
	}
	
	// 通过插件找出最新的安装日志。
	public BsPluginlog findRecentlyInstallLog(BsPlugin bsPlugin) {
		List<BsPluginlog> list = bsPluginlogDao.findByBsPlugin(bsPlugin, "A");
		if(list == null || list.size() == 0) {
			return null;
		}
		
		BsPluginlog bsPluginlogStart = null;
		Timestamp timestampStart = new Timestamp(0);
		Iterator<BsPluginlog> it = list.iterator();
		while(it.hasNext()) {
			BsPluginlog bsPluginlog = it.next();
			Timestamp timestamp = bsPluginlog.getProcessDate();
			if(timestampStart.compareTo(timestamp) < 0) {
				timestampStart = timestamp;
				bsPluginlogStart = bsPluginlog;
			}
		}
		
		return bsPluginlogStart;
	}
}
