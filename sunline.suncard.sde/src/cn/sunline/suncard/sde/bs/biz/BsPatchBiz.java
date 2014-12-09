/*
 * 文件名：BsPatchBiz.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：BsPatch业务层操作类
 * 修改人：易振强
 * 修改时间：2011-9-22
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.biz;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.DocumentException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.dao.BsPatchDao;
import cn.sunline.suncard.sde.bs.dao.BsPatchmappingDao;
import cn.sunline.suncard.sde.bs.dao.BsPluginDao;
import cn.sunline.suncard.sde.bs.dao.BsPluginlogDao;
import cn.sunline.suncard.sde.bs.entity.BsPatch;
import cn.sunline.suncard.sde.bs.entity.BsPatchId;
import cn.sunline.suncard.sde.bs.entity.BsPatchmapping;
import cn.sunline.suncard.sde.bs.entity.BsPatchmappingId;
import cn.sunline.suncard.sde.bs.entity.BsPlugin;
import cn.sunline.suncard.sde.bs.entity.BsPluginId;
import cn.sunline.suncard.sde.bs.entity.BsPluginlog;
import cn.sunline.suncard.sde.bs.entity.BsWidget;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.ui.plugin.PluginManager;
import cn.sunline.suncard.sde.bs.ui.plugin.logmanage.PluginLogManage;
import cn.sunline.suncard.sde.bs.ui.plugin.patch.PatchAdd;
import cn.sunline.suncard.sde.bs.ui.plugin.patch.PatchXMLAnalysis;
import cn.sunline.suncard.sde.bs.ui.plugin.patch.history.PatchMessageContent;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;

/**
 * BsPatch业务层操作类 BsPatch业务层操作类，主要提供对BsPatch类的的crud操作，提供了对事务的支持
 * 
 * @author 易振强
 * @version 1.0, 2011-9-22
 * @see
 * @since 1.0
 */
public class BsPatchBiz {
	private BsPatchmappingDao bsPatchmappingDao = new BsPatchmappingDao();
	private BsPluginlogDao bsPluginlogDao = new BsPluginlogDao();
	private BsWidgetBiz bsWidgetBiz = new BsWidgetBiz();
	private BsPluginDao bsPluginDao = new BsPluginDao();
	
	private long bankorgId = ((Long)Context.getSessionMap().get(Constants.BANKORG_ID)).longValue();
	private String pcId = (String) Context.getSessionMap().get(Constants.PC_ID);

	// 用户Dao类，业务类可以通过此类对象调用其持久化方法
	private BsPatchDao bsPatchDao = new BsPatchDao();

	/**
	 * 删除方法 提供对BsPatch实体类的删除方法，根据主键删除
	 * 
	 * @param id
	 *            BsPatch主键类
	 */
	public void delete(BsPatchId id) {
		HibernateUtil.openSession();
		bsPatchDao.deleteEntity(id, BsPatch.class);
		HibernateUtil.closeSession();
	}

	/**
	 * 添加方法 提供对BsPatch实体类的添加方法
	 * 
	 * @param entity
	 *            BsPatch实体类
	 */
	public void insert(BsPatch entity) {
		HibernateUtil.openSession();
		bsPatchDao.addEntity(entity);
		HibernateUtil.closeSession();
	}

	/**
	 * 更新方法 提供对BsPatch实体类的更新方法
	 * 
	 * @param entity
	 *            BsPatch实体类
	 */
	public void update(BsPatch entity) {
		HibernateUtil.openSession();
		bsPatchDao.updateEntity(entity);
		HibernateUtil.closeSession();
	}

	/**
	 * 查找方法 提供对BsPatch实体类的查找方法，根据主键查找，只能找到一条记录或找不到记录
	 * 
	 * @param id
	 *            BsPatch主键类
	 */
	public BsPatch findByPk(BsPatchId id) {
		return bsPatchDao.findByPK(id, BsPatch.class);
	}

	/**
	 * 查找 提供对BsPatch实体类的查找方法，找到所有BsPatch的实体记录
	 */
	public List<BsPatch> getAll() {
		return bsPatchDao.findAll(BsPatch.class);

	}

	// 通过插件名称得到其补丁。
	public List<BsPatch> findByPluginName(String pluginName) {
		return bsPatchDao.findByPluginName(pluginName, bankorgId, pcId);
	}

	// 通过插件ID得到其补丁。
	public List<BsPatch> findByPluginId(String pluginId) {
		BsPluginId id = new BsPluginId(bankorgId, pcId, pluginId);
		return bsPatchDao.findByPluginId(id);
	}

	
	// 更新补丁及其相关信息到数据库
	@SuppressWarnings({"rawtypes"})
	public void updatePatchDb(PatchAdd patchAdd) throws IOException, BundleException {
		HibernateUtil.openSession();
		
		// 备份插件Jar包
		patchAdd.backupJar();
		
		BsPatch bsPatch = patchAdd.getBsPatch();
		BsPlugin dbPlugin = patchAdd.getDbPlugin();
		
		List<BsWidget> widgetList = patchAdd.getControlXMLAnalysis().getList();
		
		PatchXMLAnalysis patchXMLAnalysis = patchAdd.getPatchXMLAnalysis();
		
		String installedVersion = patchXMLAnalysis.getInstalledVersion();
		
		// 添加补丁信息
		bsPatchDao.addEntity(bsPatch);
		
		// 添加补丁对应的控件信息到数据库。
		Iterator it = widgetList.iterator();
		while (it.hasNext()) {
			bsWidgetBiz.add((BsWidget) it.next());
		}
	
		// 添加补丁版本映射信息
		BsPatchmappingId bsPatchmappingId = new BsPatchmappingId(bankorgId,
				pcId, bsPatch.getId().getPatchId(), installedVersion);
		BsPatchmapping bsPatchMapping = new BsPatchmapping(bsPatchmappingId);
		bsPatchmappingDao.addEntity(bsPatchMapping);

		String oldVersion = dbPlugin.getPluginVer();
		// 更新插件版本
		dbPlugin.setPluginVer(installedVersion);
		bsPluginDao.updateEntity(dbPlugin);
		
		// 添加日志信息
		bsPluginlogDao.addPluginlog(dbPlugin.getId()
				.getPluginId(), "P", new Timestamp(System.currentTimeMillis()),
				oldVersion, installedVersion, bsPatch.getId().getPatchId(), 
				patchAdd.getBackFilePath(),bankorgId, pcId);
		

		patchAdd.addClassToJar();

		HibernateUtil.closeSession();
	}
	
	/**
	 *  还原插件版本 
	 * @param tableViewer
	 * @param bundle
	 * @return
	 * @throws IOException
	 * @throws DocumentException 
	 */
	@SuppressWarnings({"unchecked"})
	public String backToPlugin(TableViewer tableViewer,  Bundle bundle) throws IOException, DocumentException {
		HibernateUtil.openSession();
		
		Table table = tableViewer.getTable();
		int index = table.getSelectionIndex();
		if(index == 0) {
			return null;
		}
		List<PatchMessageContent> allList = (List<PatchMessageContent>) tableViewer.getInput();
		// 得到0（包括）到index（不包括）索引之间的视图
		List<PatchMessageContent> needDealList = allList.subList(0, index);
		
		
		List<BsPluginlog> logListTemp = new ArrayList<BsPluginlog>();
		List<BsPatch> patchList = new ArrayList<BsPatch>();
		if(needDealList != null && needDealList.size() > 0) {
			for(PatchMessageContent p : needDealList) {
				logListTemp.add(p.getBsPluginlog());
				patchList.add(p.getBsPatch());
			}
		}
		
		List<BsPluginlog> logList = logListTemp.subList(0, logListTemp.size() - 1);
		// 删除日志所对应的备份文件。
		PluginLogManage.deleteBackJarFiles(logList);
		
		// 从数据库删除补丁信息。
//		patchMessageDialog.deletePatchFromDb(patchList);
		
		for(BsPatch bsPatch : patchList) {
			bsPatchDao.delete(bsPatch);
		}
		
		// 把高于还原版本的控件信息从数据库删除。
		String version = allList.get(index - 1).getBsPluginlog().getSrcPluginVer();

		PluginManager.deleteWidgetFromDb(version, bundle);
		// 在数据库中更新插件版本
		BsPlugin bsPlugin = new BsPluginBiz().findByPluginName(bundle.getHeaders().get("Bundle-Name"));
		if(bsPlugin == null) {
			return null;
		}
		
//		patchMessageDialog.updatePluginVersion(version, bsPlugin);
		bsPlugin.setPluginVer(version);
		bsPluginDao.updateEntity(bsPlugin);
		
		File curJarFile = FileLocator.getBundleFile(bundle);
		
		// 移动备份文件到插件所在处，覆盖原文件。
		PluginManager.moveJarToJar(allList.get(index - 1).getBsPluginlog(), curJarFile);
//		patchMessageDialog.setNowVersion(version);
		
		HibernateUtil.closeSession();
		
		return version;
	}
	
	
	// 从数据库删除补丁列表
	public void deletePatchList(List<BsPatch> list) {
		if(list == null || list.isEmpty()) {
			return;
		}
		
		HibernateUtil.openSession();
		for(BsPatch bsPatch : list) {
			bsPatchDao.delete(bsPatch);
		}
		HibernateUtil.closeSession();
	}
	
	
	
}
