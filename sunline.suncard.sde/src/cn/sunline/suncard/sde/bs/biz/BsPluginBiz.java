/*
 * 文件名：     BsPluginBiz.java
 * 版权：      
 * 描述：
 * 修改人：     易振强
 * 修改时间：2011-9-29
 * 修改内容：
 */
package cn.sunline.suncard.sde.bs.biz;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.dao.BsPatchDao;
import cn.sunline.suncard.sde.bs.dao.BsPluginDao;
import cn.sunline.suncard.sde.bs.dao.BsPluginlogDao;
import cn.sunline.suncard.sde.bs.entity.BsPlugin;
import cn.sunline.suncard.sde.bs.entity.BsPluginId;
import cn.sunline.suncard.sde.bs.entity.BsWidget;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.ui.plugin.PluginAdd;
import cn.sunline.suncard.sde.bs.ui.plugin.PluginManager;
import cn.sunline.suncard.sde.bs.ui.plugin.patch.PatchManager;
import cn.sunline.suncard.sde.bs.ui.plugin.tools.FileDeal;
import cn.sunline.suncard.sde.bs.ui.plugin.tools.SDEPluginZip;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;

public class BsPluginBiz {
	private BsPluginDao bsPluginDao = new BsPluginDao();
	private BsWidgetBiz bsWidgetBiz = new BsWidgetBiz();
	private BsPatchDao bsPatchDao = new BsPatchDao();
	private BsPluginlogDao bsPluginlogDao = new BsPluginlogDao();

	private long bankorgId = ((Long)Context.getSessionMap().get(Constants.BANKORG_ID)).longValue();
	private String pcId = (String) Context.getSessionMap().get(Constants.PC_ID);
	
	// 添加日志
	public static Log logger = LogManager.getLogger(BsPluginBiz.class.getName());
	
	// 插件备份目录
	public final static String REPLACE_URL = PatchManager.REPLACE_URL;

	public BsPlugin findByPluginName(String PluginName) {
		return bsPluginDao.findByPluginName(PluginName, bankorgId, pcId);
	}

	public void delete(BsPlugin bsPlugin) {
		HibernateUtil.openSession();
		bsPluginDao.deleteEntity(bsPlugin.getId(), BsPlugin.class);
		HibernateUtil.closeSession();
	}

	public void add(BsPlugin bsPlugin) {
		HibernateUtil.openSession();
		bsPluginDao.addEntity(bsPlugin);
		HibernateUtil.closeSession();
	}

	// public void updatePluginDb(BsPlugin oldPlugin, BsPlugin newPlugin,
	// List<BsWidget> widgetList) {
	// HibernateUtil.openSession();
	//
	// long maxSeq = bsPluginlogDao.getMaxSeq();
	// String oldPluginVersion = null;
	//
	// //如果是覆盖安装，得先删除数据库中的旧的插件信息。
	// if(oldPlugin != null) {
	// // 删除插件信息
	// bsPluginDao.deleteEntity(oldPlugin.getId(), BsPlugin.class);
	//
	// // 删除插件的控件信息
	// bsWidgetDao.deleteByPluginId(oldPlugin.getId().getPluginId());
	//
	// // 删除插件的补丁信息
	// bsPatchDao.deleteByPluginId(oldPlugin.getId().getPluginId());
	//
	// bsPluginlogDao.addPluginlog(oldPlugin.getId().getPluginId(), "D", new
	// Timestamp(System.currentTimeMillis()),
	// oldPlugin.getPluginVer().toString(), null, null, null);
	// }
	//
	// // 添加新的插件信息。
	// bsPluginDao.addEntity(newPlugin);
	//
	// // 添加新插件对应的控件信息到数据库。
	// Iterator it = widgetList.iterator();
	// while(it.hasNext()) {
	// bsWidgetDao.addEntity((BsWidget)it.next());
	// }
	//
	// // 添加日志信息
	// BsPluginlogId newPluginlogId = new BsPluginlogId(bankorgId, pcId, maxSeq
	// + 1);
	// BsPluginlog newPluginlog = new BsPluginlog(newPluginlogId,
	// newPlugin.getId().getPluginId(), "A",
	// new Timestamp(System.currentTimeMillis()), oldPluginVersion,
	// newPlugin.getPluginVer(),
	// null, null, null, null, null);
	// bsPluginlogDao.addEntity(newPluginlog);
	//
	// HibernateUtil.closeSession();
	// }

	
	// 安装一个插件
	public void updatePluginDb(PluginAdd pluginAdd) throws IOException, BundleException {
		logger.info("void updatePluginDb(PluginAdd pluginAdd) 更新数据库信息，" +
				"包括删除旧的插件和控件信息，添加新的插件和控件信息...");
		HibernateUtil.openSession();

		String oldPluginVersion = null;

		BsPlugin newPlugin = pluginAdd.getNewBsPlugin();
		BsPlugin oldPlugin = pluginAdd.getDbPlugin();
		List<BsWidget> widgetList = pluginAdd.getControlXMLAnalysis().getList();

		// 如果是覆盖安装，得先删除数据库中的旧的插件信息。
		if (oldPlugin != null) {
			logger.info("如果是覆盖安装，得先删除数据库中的旧的插件信息。。。");
			uninstallPluginFromDb(oldPlugin);
		}

		// 添加新的插件信息。
		bsPluginDao.addEntity(newPlugin);

		// 添加新插件对应的控件信息到数据库。
		Iterator<BsWidget> it = widgetList.iterator();
		int i = 0;
		while (it.hasNext()) {
			i ++;
			bsWidgetBiz.add((BsWidget) it.next());
			if(i>=100) {
//				HibernateUtil.getSession().flush();
//				HibernateUtil.getSession().clear();
			}
		}

		// 添加日志信息
		bsPluginlogDao.addPluginlog(newPlugin.getId().getPluginId(), "A", new Timestamp(
				System.currentTimeMillis()), oldPluginVersion,
				newPlugin.getPluginVer(), null, null, bankorgId, pcId);
		
		// 如果基础框架安装过该插件，则安装新插件后要删除原插件文件
		if (oldPlugin != null) {
			// 拆卸一个插件所涉及的文件操作
			deleteFileOfPlugin(oldPlugin.getPluginName());
		}
		
		// 添加Jar包到项目
		pluginAdd.addJarToProjectPath();
		
		// 创建link文件
		pluginAdd.createLinkFile();
		
//		// 向config.ini文件中添加插件信息。
//		pluginAdd.addPluingToConfigini();

		HibernateUtil.closeSession();
	}

	/**
	 *  拆卸一个插件
	 * 
	 * @param pluginManager
	 * @throws IOException
	 * @throws BundleException
	 */
	public void uninstallPlugin(PluginManager pluginManager)
			throws IOException, BundleException {
		logger.info("void uninstallPlugin(PluginManager pluginManager) 拆卸一个插件。。。");
		
		HibernateUtil.openSession();
	
		Bundle bundle = pluginManager.getPluginTreeContent().getBundle();
		if(bundle == null) {
			throw new BundleException("There has no selected Bundle!");
		}
		
		logger.info("bundle.uninstall()。。。");
		bundle.uninstall();
		
		String bundleName = bundle.getHeaders().get("Bundle-Name");
		BsPlugin bp = findByPluginName(bundleName);
		// 如果该插件已经安装，则需要先拆卸
		if (bp != null) {
			logger.info(bundleName + " 插件在数据库中有记录。。。");
			// 从数据库中删除旧的插件及其相关信息（包括控件及其补丁）
			uninstallPluginFromDb(bp);
			// 拆卸一个插件所涉及的文件操作
			deleteFileOfPlugin(bundleName);
		// 如果是基础框架自带的插件，则直接删除插件文件即可。
		} else {
//			FileDeal.deleteFile(bundleFile.getAbsolutePath());
		}
		
		// 从配置文件删除插件信息
//		pluginManager.deletePluingFromConfigini();
		
		HibernateUtil.closeSession();
	}
	
	/** 
	 * 拆卸一个插件所涉及的数据库操作
	 * @param bsPlugin
	 */
	private void uninstallPluginFromDb(BsPlugin bsPlugin) {
		logger.info("拆卸一个插件所涉及的数据库操作...");
		
		if (bsPlugin == null) {
			return;
		}
		
		// 删除插件信息
		logger.info("删除插件信息...");
		bsPluginDao.deleteEntity(bsPlugin.getId(), BsPlugin.class);
		
		// 删除插件的控件信息
		logger.info("删除插件的控件信息...");
		bsWidgetBiz.deleteByPluginId(bsPlugin.getId().getPluginId());
		
		// 删除插件的补丁信息
		logger.info("删除插件的补丁信息...");
		bsPatchDao.deleteByPluginId(bsPlugin.getId());
		bsPluginlogDao.addPluginlog(bsPlugin.getId().getPluginId(), "D",
				new Timestamp(System.currentTimeMillis()), bsPlugin
						.getPluginVer().toString(), null, null, null,bankorgId, pcId);
	}

	/**
	 *  拆卸一个插件所涉及的文件操作
	 * 
	 * @param bundleName
	 */
	public void deleteFileOfPlugin(String bundleName) {
		logger.info("void deleteFileOfPlugin(String bundleName)  拆卸一个插件所涉及的文件操作...");
		
		// 删除该插件文件（包括目录）
		logger.info("开始 删除该插件文件（包括目录）...");
		String pluginFile = new File(PluginManager.PLUGIN_ROOT_PATH + bundleName).getAbsolutePath();
		FileDeal.deleteFile(pluginFile);
		
		// 删除该插件的备份文件目录。
		logger.info("开始删除该插件的备份文件目录...");
		String backupFile = new File(PatchManager.REPLACE_URL + bundleName).getAbsolutePath();
		if(new File(backupFile).isDirectory()) {
			FileDeal.deleteFile(backupFile);
		}
		
		// 删除link文件
		logger.info("开始删除link文件...");
		String linkFile = new File(PluginManager.LINK_FILE + bundleName + ".link").getAbsolutePath();
		if(new File(linkFile).isFile()) {
			FileDeal.deleteFile(linkFile);
		}
		
		// 如果安装该插件时向config中的mapping中添加了文件，拆卸该插件时要删除mapping下面的相应文件
		logger.info("开始删除config下面的mapping文件...");
		File file = new File(SDEPluginZip.CONFIG);
		logger.info("mapping文件夹路径：" + file.getAbsolutePath());
		File[] files = file.listFiles();
		if(files != null) {
			for(File delFile : files) {
				String fileName = delFile.getName();
				
				logger.info("被浏览过的文件名：" + delFile.getName());
				logger.info("Bundle名：" + bundleName);				
				if(fileName.endsWith(".xml") && fileName.contains(bundleName)) {
					FileDeal.deleteFile(delFile.getAbsolutePath());
				}
			}
			
		}
	}
	
	public BsPlugin findByPluginId(String pluginId) {
		BsPluginId id = new BsPluginId(bankorgId, pcId, pluginId);
		return bsPluginDao.findByPluginId(id);
	}
	
	public void update(BsPlugin bsPlugin) {
		bsPluginDao.updateEntity(bsPlugin);
	}

	public BsPlugin findByPk(BsPluginId id){
		return bsPluginDao.findByPK(id, BsPlugin.class);
	}
}
