/*
 * 文件名：
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：  易振强
 * 修改时间：2011-10-27
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

import cn.sunline.suncard.sde.bs.exception.FileNumError;
import cn.sunline.suncard.sde.bs.exception.MD5CheckException;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.ui.plugin.PluginAdd;
import cn.sunline.suncard.sde.bs.ui.plugin.PluginManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

public class PluginProgressBar implements IRunnableWithProgress {
	private PluginAdd pluginAdd;
	private int flag;
	private Shell shell;
	
	/**
	 * 添加日志
	 */
	public static Log logger = LogManager.getLogger(PluginProgressBar.class.getName());	
	
	
	public PluginProgressBar(PluginAdd pluginAdd) {
		this.pluginAdd = pluginAdd;
		this.shell= pluginAdd.getShell();
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException{
			logger.info("void run(IProgressMonitor monitor) 运行进度条，将调用PluginAdd对象的allStart方法");
		
			try {
				flag = pluginAdd.allStart(monitor);
				
			} catch (BundleException e) {
				e.printStackTrace();
				// 写日志
				logger.error(I18nUtil.getMessage("PLUGIN_INSTALL_FAIL")+ " -- " +
						e.getMessage());
				
				shell.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {	
						MessageDialog.openError(shell, I18nUtil.getMessage("PLUGIN_ERROR"),
								I18nUtil.getMessage("PLUGIN_INSTALL_FAIL"));
					}
				});
				return;

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				// 写日志
				logger.error(I18nUtil.getMessage("PLUGIN_ENCRYPT_TYPE_ERROR")+ " -- "
						+ e.getMessage());
				
				shell.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {	
						MessageDialog.openInformation(shell,
								I18nUtil.getMessage("PLUGIN_ERROR"),
								I18nUtil.getMessage("PLUGIN_ENCRYPT_TYPE_ERROR"));
					}
				});

				return;
				
			} catch (IOException e) {
				e.printStackTrace();
				// 写日志
				logger.error(I18nUtil.getMessage("PLUGIN_UNINSTALL_OR_COPYFILE_ERROR") +
						" -- " + e.getMessage());
				
				shell.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {	
						MessageDialog.openInformation(shell,
								I18nUtil.getMessage("PLUGIN_ERROR"),
								I18nUtil.getMessage("PLUGIN_UNINSTALL_OR_COPYFILE_ERROR"));
					}
				});
				
				return;
				
			} 
//			catch (MD5CheckException e) {
////				// 写日志
//				logger.info(I18nUtil.getMessage("PLUGIN_MD5_DIFFER"));
//				
//				shell.getDisplay().asyncExec(new Runnable() {
//					@Override
//					public void run() {	
//						MessageDialog.openError(shell, I18nUtil.getMessage("PLUGIN_ERROR"), 
//								I18nUtil.getMessage("PLUGIN_MD5_DIFFER"));
//					}
//				});
//				
//			} catch (FileNumError e) {
//				// 写日志
//				logger.info(I18nUtil.getMessage("PLUGIN_FILE_MISS"));
//				
//				shell.getDisplay().asyncExec(new Runnable() {
//					@Override
//					public void run() {	
//					MessageDialog.openInformation(shell,
//							I18nUtil.getMessage("PLUGIN_ERROR"),
//							I18nUtil.getMessage("PLUGIN_FILE_MISS"));
//					}
//				});
//				
//			}
			catch(Exception e) {
				e.printStackTrace();
				// 写日志
				logger.error(I18nUtil.getMessage("PLUGIN_INSTALL_FAIL")+ " -- " + e.getMessage());
				
				shell.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {	
						MessageDialog.openError(shell, I18nUtil.getMessage("PLUGIN_ERROR"),
								I18nUtil.getMessage("PLUGIN_INSTALL_FAIL"));
					}
				});
				
				flag = PluginAdd.INSTALL_EXCEPTION;
				
				return;
			}

			if(PluginAdd.PLUGIN_FILE_MISS == flag) {
				// 写日志
				logger.info(I18nUtil.getMessage("PLUGIN_FILE_MISS"));
				MessageDialog.openInformation(shell,
						I18nUtil.getMessage("PLUGIN_ERROR"),
						I18nUtil.getMessage("PLUGIN_FILE_MISS"));
				
			}  else if(PluginAdd.MD5_DIFFERENCE == flag) {
				// 写日志
				logger.info(I18nUtil.getMessage("PLUGIN_MD5_DIFFER"));
				shell.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {	
						MessageDialog.openError(shell, I18nUtil.getMessage("PLUGIN_ERROR"), 
								I18nUtil.getMessage("PLUGIN_MD5_DIFFER"));
							}
					});
				
			} else if (PluginAdd.INIT_DOCUMENT_FAIL == flag) {
				// 写日志
				logger.info(I18nUtil.getMessage("PLUGIN_XML_ERROR"));
				shell.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {	
						MessageDialog.openInformation(shell,
								I18nUtil.getMessage("PLUGIN_ERROR"),
								I18nUtil.getMessage("PLUGIN_XML_ERROR"));
					}
				});
				
			} else if(PluginAdd.XML_TYPE_ERROR == flag) {
				// 写日志
				logger.info(I18nUtil.getMessage("PLUGIN_XML_PLUGINTYPE_ERROR"));
				shell.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {	
					MessageDialog.openInformation(shell,
							I18nUtil.getMessage("PLUGIN_ERROR"),
							I18nUtil.getMessage("PLUGIN_XML_PLUGINTYPE_ERROR"));
					}
				});
			}
	}

	public int getFlag() {
		return flag;
	}
	
}
