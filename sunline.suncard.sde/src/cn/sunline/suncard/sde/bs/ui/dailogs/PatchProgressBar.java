/*
 * 文件名：
 * 版权：	 Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2011-10-28
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.dailogs;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleException;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.ui.plugin.PluginAdd;
import cn.sunline.suncard.sde.bs.ui.plugin.PluginManager;
import cn.sunline.suncard.sde.bs.ui.plugin.patch.PatchAdd;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

public class PatchProgressBar implements IRunnableWithProgress {
	private PatchAdd patchAdd;
	private int flag;
	private Shell shell;
	
	/**
	 * 添加日志
	 */
	public static Log logger = LogManager.getLogger(PluginManager.class.getName());	
	
	public PatchProgressBar(PatchAdd patchAdd) {
		this.patchAdd = patchAdd;
		this.shell= patchAdd.getShell();
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		try {
			flag = patchAdd.allStart(monitor);
			
		} catch (BundleException e) {
			MessageDialog.openError(shell, I18nUtil.getMessage("PLUGIN_ERROR"), I18nUtil.getMessage("PATCH_INSTALL_FAIL"));
			// 写日志
			logger.error(I18nUtil.getMessage("PATCH_INSTALL_FAIL")+ " -- " +
					e.getMessage());
			e.printStackTrace();
			return;
			
		} catch (NoSuchAlgorithmException e) {
			MessageDialog.openInformation(shell, I18nUtil.getMessage("PLUGIN_ERROR"), I18nUtil.getMessage("PLUGIN_ENCRYPT_TYPE_ERROR"));
			// 写日志
			logger.error(I18nUtil.getMessage("PLUGIN_ENCRYPT_TYPE_ERROR")+ " -- " +
					e.getMessage());
			e.printStackTrace();
			return;
			
		} catch (IOException e) {
			MessageDialog.openInformation(shell, I18nUtil.getMessage("PLUGIN_ERROR"), I18nUtil.getMessage("PATCH_UNINSTALL_OR_COPYFILE_ERROR"));
			// 写日志
			logger.error(I18nUtil.getMessage("PLUGIN_UNINSTALL_OR_COPYFILE_ERROR")+ " -- " +
					e.getMessage());
			e.printStackTrace();
			return;
			
		} catch (Exception e) {
			MessageDialog.openError(shell, I18nUtil.getMessage("PLUGIN_ERROR"), I18nUtil.getMessage("PATCH_INSTALL_FAIL"));
			// 写日志
			logger.error(I18nUtil.getMessage("PATCH_INSTALL_FAIL")+ " -- " +
					e.getMessage());
			e.printStackTrace();
			return;
		}
		
		if(PluginAdd.PLUGIN_FILE_MISS == flag) {
			MessageDialog.openInformation(shell,
					I18nUtil.getMessage("PLUGIN_ERROR"),
					I18nUtil.getMessage("PLUGIN_FILE_MISS"));
			// 写日志
			logger.error(I18nUtil.getMessage("PLUGIN_FILE_MISS"));
			
		} else if(PluginAdd.MD5_DIFFERENCE == flag) {
			MessageDialog.openError(shell, I18nUtil.getMessage("PLUGIN_ERROR"), 
					I18nUtil.getMessage("PLUGIN_MD5_DIFFER"));
			// 写日志
			logger.error(I18nUtil.getMessage("PLUGIN_MD5_DIFFER"));
			
		} else if (PluginAdd.INIT_DOCUMENT_FAIL == flag) {
			MessageDialog.openInformation(shell,
					I18nUtil.getMessage("PLUGIN_ERROR"),
					I18nUtil.getMessage("PATCH_XML_ERROR"));
			// 写日志
			logger.error(I18nUtil.getMessage("PATCH_XML_ERROR"));
			
		} else if(PluginAdd.XML_TYPE_ERROR == flag) {
			MessageDialog.openInformation(shell,
					I18nUtil.getMessage("PLUGIN_ERROR"),
					I18nUtil.getMessage("PATCH_XML_PLUGINTYPE_ERROR"));
			// 写日志
			logger.error(I18nUtil.getMessage("PATCH_XML_PLUGINTYPE_ERROR"));
		}
		
	}


	public int getFlag() {
		return flag;
	}
	
}
