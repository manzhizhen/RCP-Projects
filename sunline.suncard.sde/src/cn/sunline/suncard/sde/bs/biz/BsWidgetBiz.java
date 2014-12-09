/*
 * 文件名：     BsWidgetBiz.java
 * 版权：      	Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2011-9-29
 * 修改内容：创建
 */
package cn.sunline.suncard.sde.bs.biz;

import java.util.List;

import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.dao.BsWidgetDao;
import cn.sunline.suncard.sde.bs.entity.BsPluginId;
import cn.sunline.suncard.sde.bs.entity.BsWidget;
import cn.sunline.suncard.sde.bs.entity.BsWidgetId;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.ui.plugin.PluginAdd;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;


public class BsWidgetBiz {
	private BsWidgetDao bsWidgetDao = new BsWidgetDao();
	public static Log logger = LogManager.getLogger(BsWidgetBiz.class.getName());
	
	private long bankorgId = ((Long)Context.getSessionMap().get(Constants.BANKORG_ID)).longValue();
	private String pcId = (String) Context.getSessionMap().get(Constants.PC_ID);
	
	public void add(BsWidget bsWidget) {
		
		BsWidget dbBsWidget = bsWidgetDao.findByPK(bsWidget.getId(), BsWidget.class);
		if(dbBsWidget == null) {
			HibernateUtil.openSession();
			logger.info( "正在添加：" + bsWidget.getId().getWidgetId());
			bsWidgetDao.addEntity(bsWidget);
			HibernateUtil.closeSession();
			
		}
	}
	
	public void deleteByPluginId(String pluginId) {
		BsPluginId id = new BsPluginId(bankorgId, pcId, pluginId);
		bsWidgetDao.deleteByPluginId(id);
	}
	
	public void delete(BsWidget bsWidget) {
		HibernateUtil.openSession();
		bsWidgetDao.deleteEntity(bsWidget.getId(), BsWidget.class);
		HibernateUtil.closeSession();
	}
	
	public BsWidget findByPk(BsWidgetId id){
		return bsWidgetDao.findByPK(id, BsWidget.class);
	}
	
	/**
	 * 按主键查找控件
	 * 按主键查找控件信息
	 * @param widgetId
	 * @return 控件
	 */
	public BsWidget findWidgetByPk(BsWidgetId widgetId) {
		BsWidget widget = bsWidgetDao.findByPK(widgetId, BsWidget.class);
		return widget;
	}
	
	/**
	 * 查找父控件下的所有子控件
	 * 查找父控件下的所有子控件
	 * @param parWidgetId
	 * @return
	 */
	public List<BsWidget> findWidgetByPar(String parWidgetId) {
		return bsWidgetDao.findWidgetByPar(parWidgetId, bankorgId, pcId);
	}
	
	public List<BsWidget> findAll(){
		return bsWidgetDao.findAll(BsWidget.class);
	}
}
