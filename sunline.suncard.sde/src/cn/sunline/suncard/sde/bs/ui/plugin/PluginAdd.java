/*
 * 文件名：     PluginAdd.java
 * 版权：      	Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	插件添加类
 * 修改人：     易振强
 * 修改时间：2011-9-21
 * 修改内容：创建
 */

package cn.sunline.suncard.sde.bs.ui.plugin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

import cn.sunline.suncard.sde.bs.Activator;
import cn.sunline.suncard.sde.bs.biz.BsPluginBiz;
import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsPlugin;
import cn.sunline.suncard.sde.bs.entity.BsPluginId;
import cn.sunline.suncard.sde.bs.exception.FileNumError;
import cn.sunline.suncard.sde.bs.exception.MD5CheckException;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.ui.dailogs.PluginProgressBar;
import cn.sunline.suncard.sde.bs.ui.plugin.tools.ControlXMLAnalysis;
import cn.sunline.suncard.sde.bs.ui.plugin.tools.FileDeal;
import cn.sunline.suncard.sde.bs.ui.plugin.tools.JarMD5Checkout;
import cn.sunline.suncard.sde.bs.ui.plugin.tools.SDEPluginZip;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 插件添加类，主要负责插件的添加和安装
 * 
 * @author 易振强
 * @version 1.0, 2011-10-21
 * @see 
 * @since 1.0
 */
public class PluginAdd {
	/**
	 * 主窗口的Shell对象
	 */
	private Shell shell;
	
	/**
	 * 项目插件文件存放的目录
	 */
	private final static String PLUGIN_ROOT_PATH = PluginManager.PLUGIN_ROOT_PATH;
//	private final static String CONFIGINI_PATH = PluginManager.CONFIGINI_PATH;
	
	// 为了复制插件Jar内resources和config文件夹下的内容所建立的临时目录。
	private final static String RES_CONFIG_PATH = "%temp%" + File.separator;
	
	// 项目link文件存放的目录
	public final static String LINK_FILE = PluginManager.LINK_FILE;
	
	// 插件文件的扩展名
	private String filterExtensions = "*.plugin";
	
	// 插件jar包MD5校验对象
	private JarMD5Checkout JarMD5Checkout = new JarMD5Checkout();
	
	// 插件的XML文件分析对象
	private JarXMLAnalysis jarXMLAnalysis = null;
	
	// 控件的XML文件分析对象
	private ControlXMLAnalysis controlXMLAnalysis = null;
	
	// 解压加压对象
	private SDEPluginZip sDEPluginZip = null;
	
	// 插件Biz
	private BsPluginBiz bsPluginBiz = new BsPluginBiz();
	
	// 数据库中的插件版本
	private Version dbVersion;
	
	// XML配置文件中的插件版本
	private Version xmlVersion;
	
	private long bankorgId = ((Long)Context.getSessionMap().get(Constants.BANKORG_ID)).longValue();
	private String pcId = (String) Context.getSessionMap().get(Constants.PC_ID);
	
	// 新的插件
	private BsPlugin newBsPlugin;
	
	// 数据库中的插件
	private BsPlugin dbPlugin;
	
	// 插件文件的目录
	private String filePath = null;
	
	// 文件Map，用于存储插件Jar包、插件配置文件等的数据流来进行提前分析
	private Map<String, byte[]> fileMap = null;
	
	// Jar包文件名
	private String jarFileName = null;
	
	// XML文件名
	private String xmlFileName = null;
	
	public final static int BUFFER_SIZE = SDEPluginZip.BUFFER_SIZE;
	
	// 插件文件路径为空的信息码
	public final static int PLUGIN_FILE_PATH_NULL = 1001;
	
	// 插件文件数量不对的信息码
	public final static int PLUGIN_FILE_MISS = 2001;
	
	// 插件文件数量正确的信息码
	public final static int  PLUGIN_FILE_RIGHT = 2000;
	
	// MD5码不一致的信息码
	public final static int MD5_DIFFERENCE = 3001;
	
    // Document对象的获得失败的信息码
	public final static int INIT_DOCUMENT_FAIL = 4001;
	
	// Jar的XML配置文件类型不是插件类型的信息码
	public final static int XML_TYPE_ERROR = 4002;
	
	// Jar的XML配置文件校验成功的信息码
	public final static int XML_CHECK_SUCCESS = 4000;
	
	// 更新数据库取得成功
	public final static int UPDATE_DATA_SUCCESS = 5000;
	
	// 安装被取消的信息码
	public final static int INSTALL_CANCLE = 5001;
	
	// 安装版本和已安装的版本一样信息码
	public final static int VERSION_SAME = 5002;
	
	// 更新数据库时失败信息码
	public final static int	UPDATE_DATA_FAIL = 5003;
			
	// 进度条异常信息码
	public final static int INVOCATION_TARGET_EXCEPTION = 6001;
	
	// 进度条线程中断异常信息码
	public final static int INTERRUPTED_EXCEPTION = 6002;
	
	// 插件安装成功的信息码
	public final static int INSTALL_SUCCESS = 0;
	
	// 插件安装抛出了异常的信息码
	public final static int INSTALL_EXCEPTION = 1;
	
	
	// 添加日志
	public static Log logger = LogManager.getLogger(PluginAdd.class.getName());
	
	// 创建进度条对话框
	public PluginProgressBar pluginProgressBar;
	
	public boolean choice;
	
	// 设定插件的备份位置。
//	public final static String REPLACE_URL = "pluginsbackup/";
	
	private int initFileFlowValue;
	
	public PluginAdd(Shell shell) {
		this.shell = shell;
	}
	
	/**
	 * 设置插件
	 * 
	 * @param filePath
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	
	/**
	 * 安装插件
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws BundleException
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 */
	public void installPlugin()   {
		logger.info("void installPlugin()  安装插件开始。。。");
		
		int flag = INSTALL_SUCCESS;
		
		if(addPluginDialog() == null) {
			return ;
		}

		// 进度条对话框
		ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(shell);
		// PluginProgressBar 实现了IRunnableWithProgress 接口
		PluginProgressBar pluginProgressBar = new PluginProgressBar(this);
		
		try {
			logger.info("void installPlugin()  开始运行进度条 。。。");
			
			progressMonitorDialog.run(true, true, pluginProgressBar);
			flag = pluginProgressBar.getFlag();
		} catch (InvocationTargetException e) {
			progressMonitorDialog.getProgressMonitor().done();
//			progressMonitorDialog.close();
			flag = INVOCATION_TARGET_EXCEPTION;
			shell.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {	
					MessageDialog.openError(shell, I18nUtil.getMessage("PLUGIN_ERROR"),
							I18nUtil.getMessage("PLUGIN_INSTALL_FAIL"));
				}
			});
			
			// 写日志
			logger.error("插件安装进度条异常!有可能是数据库出错！" + " -- " + e.getMessage());
			e.printStackTrace();
			return ;
			
		} catch (InterruptedException e) {
			flag = INTERRUPTED_EXCEPTION;
			// 写日志
			logger.error("插件安装进度条线程中断异常！有可能是数据库出错！" + " -- " + e.getMessage());
			e.printStackTrace();
			
			return ;
		}
		
		if (PluginAdd.INSTALL_SUCCESS == flag) {
			MessageDialog.openInformation(shell,
					I18nUtil.getMessage("PLUGIN_MESSAGE"),
					I18nUtil.getMessage("PLUGIN_INSTALL_SUCCESS"));
			// 写日志
			logger.info(I18nUtil.getMessage("PLUGIN_INSTALL_SUCCESS"));
			
		} else if(INVOCATION_TARGET_EXCEPTION == flag || INTERRUPTED_EXCEPTION == flag || 
				PluginAdd.UPDATE_DATA_FAIL == flag) {
			MessageDialog.openInformation(shell,
					I18nUtil.getMessage("PLUGIN_ERROR"),
					I18nUtil.getMessage("PLUGIN_INSTALL_FAIL"));
		} 
	}
	
	/**
	 * 全面启动插件安装
	 * @param monitor
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws BundleException
	 * @throws FileNumError 
	 * @throws MD5CheckException 
	 * @throws DocumentException 
	 */
	public int allStart(final IProgressMonitor monitor) throws NoSuchAlgorithmException, 
		IOException, BundleException, FileNumError, MD5CheckException, DocumentException {
		logger.info("int allStart(final IProgressMonitor monitor) 全面开始开始插件安装。。。");
		
		// 初始化文件流，为读取数据做准备
		try {
			
			monitor.beginTask(I18nUtil.getMessage("PROGRESS_BAR_INIT_FILEFLOW"),
					IProgressMonitor.UNKNOWN);
			
			// 通过压缩文件流来初始化相应的管理对象。
//			initFileFlow();
			
			if(initFileFlow() != PLUGIN_FILE_RIGHT) {
				// 写日志
				logger.error("插件文件不完整！");
				return PLUGIN_FILE_MISS;
			}
		
			monitor.subTask(I18nUtil.getMessage("PROGRESS_BAR_MD5"));
			// 校验MD5码
//			compareMD5();
			if (!compareMD5()) {
				// 写日志
				logger.error("MD5校验失败！");
				return MD5_DIFFERENCE;
			}
			
			monitor.subTask(I18nUtil.getMessage("PROGRESS_BAR_CHECK_XML"));
			// 初步检查插件配置文件
			int flag = checkPluginXML();
			if(flag != XML_CHECK_SUCCESS) {
				// 写日志
				logger.error("插件配置文件内容错误！");
				
				return flag;
			}

			monitor.subTask(I18nUtil.getMessage("PROGRESS_BAR_INSTALL"));
			// 如果安装被取消
			flag = startInstallPlugin();
			if(flag != UPDATE_DATA_SUCCESS) {
				return flag;
			}
		} finally {
			monitor.done();
		}
		
		return INSTALL_SUCCESS;
	}
	
	
	/*
	 *  打开插件添加对话框
	 */
	public String addPluginDialog() {
		// 创建一个文件对话框，用于打开插件文件。
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN | SWT.SINGLE);
		fileDialog.setFilterExtensions(new String[] { filterExtensions });
		fileDialog.setText(I18nUtil.getMessage("PLUGIN_OPEN"));

		filePath = fileDialog.open();
		return filePath;
	}
	
	/**
	 * 通过压缩文件流来初始化相应的管理对象。
	 * 
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws FileNumError 
	 * @throws DocumentException 
	 */
	public int initFileFlow() throws IOException, NoSuchAlgorithmException, FileNumError, DocumentException {
		logger.info("int initFileFlow() 从压缩文件中获取需要的字节流。。。");
		
		if (filePath == null) {
			// 写日志
			logger.error("插件文件路径为空！");
			throw new IOException();
		}

		// 创建一个SDEPluginZip对象，用于从压缩文件中读取数据或解压文件，并初始化压缩文件路径。
		sDEPluginZip = new SDEPluginZip();
		sDEPluginZip.setFilePath(filePath);

		// 因为压缩文件中共有两个文件（XML和Jar），创建一个Map便于管理，其键为文件名，
		// 值为对应的文件数据流。
		fileMap = sDEPluginZip.getByteFromDecompressionFile();
		
		// 得到键的Set。
		Set<String> fileSet = fileMap.keySet();
		Iterator<String> fileIterator = fileSet.iterator();

		// 有效文件数量，有三个有效文件（插件Jar包、插件Jar包中的控件配置文件和插件的配置文件）
		int fileNum = 0;
		// 读取列表中的文件流对象
		while (fileIterator.hasNext()) {

			String fileName = (String) fileIterator.next();
			// 如果是控件的配置文件
			if(fileName.contains(SDEPluginZip.WIDGETS_FILE_NAME)) {	
				logger.info("如果是控件的配置文件:" + fileName);	

				controlXMLAnalysis = new ControlXMLAnalysis(fileMap.get(fileName));
	
				fileNum++;
				
			// 如果是插件的XML文件
			} else if (fileName.endsWith(".xml")) {
				logger.info("如果是插件的XML文件");	
				xmlFileName = fileName;
				// 用XML文件数据流初始化JarXMLAnalysis对象以便于提取其中的MD5值。
				jarXMLAnalysis = new JarXMLAnalysis(// 如果是插件的配置文件
						(byte[]) fileMap.get(fileName));
				fileNum++;
				
			// 如果是Jar文件
			} else if (fileName.endsWith(".jar")) {	
				logger.info("如果是Jar文件");	
				jarFileName = fileName;
				// 用Jar文件数据流初始化JarXMLAnalysis对象以便于计算其中的MD5值。
				JarMD5Checkout.initHashCode((byte[]) fileMap.get(fileName));
				fileNum++;
				
			}
//			else {
//				break;
//			}
		}
		
		logger.info(fileNum);
		// 如果Map里面的文件数目不为三，则说明插件压缩文件有缺失。
		if(fileNum != 3) {
			logger.info("Map里面的文件数目不为三，说明插件压缩文件有缺失。。。");
			
//			throw new FileNumError();
			
			return PLUGIN_FILE_MISS;
		} else {
			logger.info("Map里面的文件数目为三，说明插件压缩文件完整。。。");
			
			return PLUGIN_FILE_RIGHT;
		}
	}
	
	/**
	 *  校验MD5码
	 * 
	 * @return
	 * @throws MD5CheckException 
	 */
	public boolean compareMD5() throws MD5CheckException {
		logger.info("boolean compareMD5()  开始校验插件Jar的MD5码。。。");
		
		// 从JarXMLAnalysis对象中得到XML文件的MD5值。
		String XMLCode = jarXMLAnalysis.getMD5FromXML();
		
		logger.info("配置文件中的MD5码：" + XMLCode);
		
		// 通过JarMD5Checkout对象中算出Jar文件的MD5值。
		String fileCode = JarMD5Checkout.getHashCode();
		
		logger.info("Jar的MD5码：" + fileCode);

		logger.info("相等吗？：" + fileCode.equals(XMLCode));
		
//		if(!fileCode.equals(XMLCode)) {
//			throw new MD5CheckException();
//		}
		return fileCode.equals(XMLCode);
	}

	/**
	 *  初步检查插件配置文件
	 */
	public int checkPluginXML() {
		logger.info("int checkPluginXML() 初步插件的配置文件，看是否信息正确。。。");
		
		Document document = jarXMLAnalysis.getDocument();
		if(document == null) {
			logger.info("获取插件配置文件的Document文档对象出错。。。");
			
			return INIT_DOCUMENT_FAIL;
		}
		
		// 插件配置文件中插件类型是否为“G”（表示插件）
		if(!"G".equals(jarXMLAnalysis.getPluginType().trim())) {
			logger.info("插件配置文件中插件类型不为“G”。。。");
			
			return XML_TYPE_ERROR;
		}
		
		logger.info("初步检查通过。。。");
		return XML_CHECK_SUCCESS;
	}
	
	/**
	 * 等所有检查都通过后，开始安装插件，更新插件及其相关控件信息到数据库。
	 * @return
	 * @throws BundleException
	 * @throws IOException
	 */
	public int startInstallPlugin() throws BundleException, IOException {
		logger.info("boolean startInstallPlugin()  等所有检查都通过后，开始安装插件，" +
				"更新插件及其相关控件信息到数据库...");
		
		// 通过XML中的插件名称从数据库中找到对应的插件数据。
		dbPlugin = bsPluginBiz.findByPluginName(jarXMLAnalysis.getPluginName());
		
		// 如果数据库有该插件的记录
		if(dbPlugin != null) {
			logger.info("从配置文件中获取插件名称，发现该插件在数据库中有记录。。。");
			
			dbVersion = new Version(dbPlugin.getPluginVer().toString());	
			xmlVersion = new Version(jarXMLAnalysis.getPluginVersion());
					
			String compare = null;
			int flag = 0;
			// 如果待安装的插件版本比已安装的插件版本高
			if(xmlVersion.compareTo(dbVersion) > 0) {
				logger.info("该插件的版本比数据库中的版本高。。。");
				
				compare = I18nUtil.getMessage("PLUGIN_VERSION_MESSAGE1");
				
			// 如果待安装的插件版本比已安装的插件版本低	
			}else if(xmlVersion.compareTo(dbVersion) < 0) {
				logger.info("该插件的版本比数据库中的版本低。。。");
				
				compare = I18nUtil.getMessage("PLUGIN_VERSION_MESSAGE2");
				
			// 如果待安装的插件版本和已安装的插件版本一样，则不允许继续安装
			}else {
				logger.info("该插件的版本比数据库中的版本一样，不允许继续安装。。。");
				compare = I18nUtil.getMessage("PLUGIN_VERSION_MESSAGE3");	
				flag = 1;
			}
			
			if(flag != 1) {
				final String send = compare;
				shell.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {	
						final boolean inChoice =  MessageDialog.openConfirm(shell, I18nUtil.getMessage("PLUGIN_PROMPT"), send);
						choice = inChoice;
					}
				});	
				
				// 如果用户选择放弃安装，则返回。
				if(!choice) {
					logger.info("用户选择放弃安装。。。");
					return INSTALL_CANCLE;
				}
				
				logger.info("用户选择继续安装。。。");
				
			} else {
				// 该插件的版本比数据库中的版本一样，不允许继续安装
				final String nowCompare = compare;
				shell.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {	
						MessageDialog.openInformation(shell, I18nUtil.getMessage("PLUGIN_PROMPT"), nowCompare);
					}
				});

				return VERSION_SAME;
			}
			
			BundleContext bundleContext = Activator.getBundleContext();
			Bundle[] bundles = bundleContext.getBundles();
			String pluginName = dbPlugin.getPluginName();
			
			// 遍历所有插件，找到该插件，拆卸并删除对应的插件文件。
			for(Bundle bundle : bundles) {
				logger.info("遍历所有插件，看是否能找已经安装的该插件。。。");
				if(bundle.getHeaders().get("Bundle-Name").equals(pluginName)) {
					logger.info("找到了！！开始拆卸该插件。。。");
					// 拆卸插件
					bundle.uninstall();
//					bundle.getLocation();
					
					// 删除插件所在文件夹
					File path = new File(PLUGIN_ROOT_PATH, pluginName + File.separator);
					if(path.exists() && path.isDirectory()) {
						logger.info("删除该插件所在的文件夹。。。");
						FileDeal.deleteFile(path.getAbsolutePath());
					}
					
					break;
				}
			}
		}
			
		// 用配置文件的信息来创建插件对象
		BsPluginId bsPluginId = new BsPluginId(bankorgId, pcId,
				jarXMLAnalysis.getPluginId());
		newBsPlugin = new BsPlugin();
		newBsPlugin.setId(bsPluginId);
		newBsPlugin.setPluginName(jarXMLAnalysis.getPluginName());
		newBsPlugin.setPluginVer(jarXMLAnalysis.getPluginVersion());

		logger.info("用配置文件的信息来创建插件对象完毕。。。");
		
		// 从控件XML文件中解析信息，初始化控件对象列表
		logger.info("从控件XML文件中解析信息，开始初始化控件对象列表。。。");
		try {
			controlXMLAnalysis.initControlList();
		} catch (Exception e) {
			e.printStackTrace();
			return UPDATE_DATA_FAIL;
		}
		logger.info("从控件XML文件中解析信息，初始化控件对象列表完毕。。。");
		
		// 更新数据库信息，包括删除旧的插件和控件信息，添加新的插件和控件信息。
		logger.info("开始更新数据库信息，包括删除旧的插件和控件信息，添加新的插件和控件信息..");
		bsPluginBiz.updatePluginDb(this);
		
		return UPDATE_DATA_SUCCESS;		
	}
	
	/**
	 *  添加插件Jar包到项目路径
	 *  如果插件的Jar包中有config和resources这两个文件夹，也许要拷贝到制定目录。
	 * @return
	 * @throws IOException
	 * @throws BundleException
	 */
	public String addJarToProjectPath() throws IOException, BundleException {
		logger.info("public String addJarToProjectPath() ....添加插件Jar包到项目路径");
		
//		// 得到Jar文件的名字（去掉小数点和扩展名部分）
//		String name = jarFileName.substring(0, jarFileName.lastIndexOf("."));
		
		String newPluginName = jarXMLAnalysis.getPluginName();
		
		// 插件的Jar包和配置文件就放在该文件夹下
		String filePath = new File(PLUGIN_ROOT_PATH + newPluginName, File.separator + 
				"eclipse" + File.separator + "plugins" + File.separator).getAbsolutePath();
		File file = new File(filePath);
		// 如果该目录不存在，则创建
		if(!file.exists()) {
			file.mkdirs();
		}
		
		logger.info(" 准备在" + filePath + "目录下生成Jar文件... ");
		// 准备在该目录下生成Jar文件
		byte[] jarByte = (byte[]) fileMap.get(jarFileName);
		
//		// 获得jarByte的真实长度
//		int byteLength = BUFFER_SIZE - 1;
//		while(jarByte[byteLength] == 0) {
//			byteLength--;
//		}
		
		// 要生成的Jar文件的完整路径。
		String newJarPath = new File(filePath, File.separator + jarFileName).getAbsolutePath();
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
				new FileOutputStream(newJarPath));
		// 文件结束符占4个字节
//		bufferedOutputStream.write(jarByte, 0, byteLength + 5);
		bufferedOutputStream.write(jarByte, 0, SDEPluginZip.JAR_SIZE);
		bufferedOutputStream.close();
		
		logger.info("生成Jar文件完毕！开始安装该插件... ");
		
		// 安装该插件
		installBundle(newJarPath);
		
		logger.info("安装该插件完毕！ ");
		
		// 准备在该目录下生成XML文件
		byte[] xmlByte = (byte[]) fileMap.get(xmlFileName);
		
//		// 获得xmlByte的真实长度
//		int xmlLength = BUFFER_SIZE - 1;
//		while(xmlByte[xmlLength] == 0) {
//			xmlLength--;
//		}
		
		String newXMLPath = new File(filePath, File.separator + xmlFileName).getAbsolutePath();
		logger.info("准备在" + newXMLPath + "目录下生成XML文件... ");
		bufferedOutputStream = new BufferedOutputStream(
				new FileOutputStream(newXMLPath));
		// 文件结束符占4个字节
//		bufferedOutputStream.write(xmlByte, 0, xmlLength + 1);
		bufferedOutputStream.write(xmlByte, 0, SDEPluginZip.XML_SIZE);
		
		bufferedOutputStream.close();
		logger.info("生成XML文件完毕！");
		
		File resConfigFile;
		// 如果该插件有resources文件夹，则也要拷贝到制定目录
		byte[] resByte = fileMap.get(SDEPluginZip.RESOURCES);
		
		File jarFile = null;
		File resConfigJarFile = null;
		if(resByte != null) {

			logger.info("该插件有resources文件夹，将要拷贝到制定目录...");
			logger.info("先拷贝插件的jar文件到临时目录...");
			
			// 先拷贝插件的jar文件到临时目录
			jarFile = new File(newJarPath);
			resConfigJarFile = new File(PLUGIN_ROOT_PATH, RES_CONFIG_PATH);
			if(!resConfigJarFile.exists()) {
				resConfigJarFile.mkdirs();
			}
			
			FileDeal.copyFileToPath(jarFile.getAbsolutePath(), new File(resConfigJarFile.getAbsolutePath(), 
					File.separator + jarFile.getName()).getAbsolutePath());
			
			logger.info("拷贝完毕！");
			logger.info("开始将拷贝来的Jar文件解压...");
			
			// 将拷贝来的Jar文件解压
			SDEPluginZip zip = new SDEPluginZip();
			zip.setFilePath(new File(resConfigJarFile.getAbsolutePath(), File.separator + jarFile.getName()).getAbsolutePath());
			zip.decompressionFile(new File(resConfigJarFile.getAbsolutePath(), File.separator).getAbsolutePath());
			
			// 文件解压以后，将下面的resources文件下的所有内容拷贝到基础框架下的resources文件夹下。
			logger.info("解压完毕，开始拷贝...");
			FileDeal.copyFolderToPath(new File(resConfigJarFile.getAbsolutePath(), File.separator + SDEPluginZip.RESOURCES).getAbsolutePath(), 
					new File(SDEPluginZip.RESOURCES).getAbsolutePath());
		}
		
		// 如果该插件有config文件夹，则也要拷贝到制定目录
		byte[] configByte = (byte[]) fileMap.get(SDEPluginZip.CONFIG);
		if(configByte != null) {
			logger.info("该插件有config文件夹，也要拷贝到制定目录...");
			
			// resByte为空说明该插件没有config目录，所以得先把Jar文件拷贝到临时目录以便解压。
			if(resByte == null) {
				logger.info("先把Jar文件拷贝到临时目录以便解压...");		
				
				jarFile = new File(newJarPath);
				resConfigJarFile = new File(PLUGIN_ROOT_PATH + RES_CONFIG_PATH);
				if(!resConfigJarFile.exists()) {
					resConfigJarFile.mkdirs();
				}
				
				logger.info("开始拷贝Jar文件...");	
				logger.info("jarFile.getName():" + jarFile.getName());				
				FileDeal.copyFileToPath(jarFile.getAbsolutePath(), 
						new File(resConfigJarFile.getAbsolutePath(), File.separator + jarFile.getName()).getAbsolutePath());
				
				logger.info("开始解压Jar文件...");	
				// 将拷贝来的Jar文件解压
				SDEPluginZip zip = new SDEPluginZip();
				zip.setFilePath(new File(resConfigJarFile.getAbsolutePath(), File.separator + jarFile.getName()).getAbsolutePath());
				zip.decompressionFile(new File(resConfigJarFile.getAbsolutePath(), File.separator).getAbsolutePath());
			}
			
			logger.info("将下面的config文件下的所有内容拷贝到基础框架下的config文件夹下...");	
			// 文件解压以后，将下面的config文件下的所有内容拷贝到基础框架下的config文件夹下。
			FileDeal.copyFolderToPath(new File(resConfigJarFile.getAbsolutePath() + File.separator, 
					"config" + File.separator + "mapping" + File.separator).getAbsolutePath(), 
					new File(SDEPluginZip.CONFIG).getAbsolutePath());
			
			logger.info("拷贝完毕...");	
		}
		 
		logger.info("删除临时目录...");	
		// 删除临时目录
		if(resByte != null || configByte != null) {
			FileDeal.deleteFile(new File(resConfigJarFile.getAbsolutePath()).getAbsolutePath());
		}
		
		return filePath;
	}
	
	// 通过Bundle路径来安装该插件。
	public void installBundle(String bundleLocation) throws BundleException, FileNotFoundException {
		logger.info("public void installBundle(String bundleLocation)...");
		logger.info("要安装插件的路径：" + bundleLocation);
		File bundleFile = new File(bundleLocation);
		if(bundleFile.exists() && bundleFile.isFile()) {
			try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(bundleFile.getAbsolutePath()));
				
			Bundle bundle = Activator.getBundleContext().installBundle(bundleFile.getAbsolutePath(), bufferedInputStream);	
			System.out.println(bundle.getState());
			
			if(bundle.getState() != Bundle.STARTING) {
				bundle.start();
			}
//			bundle.update();
			
//			ServiceReference[] services =  bundle.getServicesInUse();
//			
//			for(ServiceReference service : services) {
//				Activator.getBundleContext().addServiceListener(service);
//			}

			
			}catch(BundleException e) {
				System.out.println(e.getType());
				e.printStackTrace();

			}
		} 
	}
	
	
	// 创建link文件
	public void createLinkFile() throws IOException {
		String newPluginName = jarXMLAnalysis.getPluginName();
		File linkFile = new File(LINK_FILE + newPluginName + ".link");
		
		if(!linkFile.exists()) {
			linkFile.getParentFile().mkdirs();
			linkFile.createNewFile();
		}
		
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(linkFile.getAbsolutePath()));
		String content = "path=suncard/" + newPluginName;
		bufferedWriter.write(content);
		bufferedWriter.close();

//		BufferedOutputStream b = new BufferedOutputStream(new FileOutputStream(linkFile));
//		byte[] bytes = new byte[BUFFER_SIZE];
//		b.write(content.getBytes());
//		b.close();
	}
	
//	// 向配置文件添加插件信息
//	public void addPluingToConfigini() throws IOException {
//
//		BufferedReader bufferedReader = new BufferedReader(new FileReader(CONFIGINI_PATH));
//		File configini = new File(CONFIGINI_PATH);
//		File tempConfigini = new File(configini.getParent() + "/" + "configTemp.ini");
//		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempConfigini));
//		
//		// 得到Jar文件的名字（去掉小数点和扩展名部分）
//		String name = jarFileName.substring(0, jarFileName.lastIndexOf("."));
//		
//		String read;
//		int flag = 0;
//		while(true) {
//			read = bufferedReader.readLine();
//			if(read == null) {
//				break;
//			}
//			
//			if(read.contains("osgi.bundles=")) {
//				flag = 1;
//				read = read + (",reference\\:file\\:cn.sunline.plugins/" + name +
//						"/" + jarFileName);
//			}
//			
//			bufferedWriter.write(read + "\n");
//		}
//		
//		if(flag != 1) {
//			MessageDialog.openError(shell, I18nUtil.getMessage("PLUGIN_ERROR"), I18nUtil.getMessage("PLUGIN_CONFIGINI_FAIL"));
//			throw new IOException();
//		}
//		
//		bufferedReader.close();
//		bufferedWriter.close();
//		
//		String path = configini.getPath();
//		configini.delete();
//		tempConfigini.renameTo(new File(path));
//	}

	
	public JarXMLAnalysis getJarXMLAnalysis() {
		return jarXMLAnalysis;
	}

	public BsPlugin getNewBsPlugin() {
		return newBsPlugin;
	}

	public BsPlugin getDbPlugin() {
		return dbPlugin;
	}

	public ControlXMLAnalysis getControlXMLAnalysis() {
		return controlXMLAnalysis;
	}

	public static void main(String[] args) throws IOException {
//		byte[] jarByte = "123456".getBytes();
//		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
//				new FileOutputStream("d:\\wenjian.txt"));
//
//		bufferedOutputStream.write(jarByte);
//		bufferedOutputStream.close();
		
//		BufferedInputStream f = new BufferedInputStream(new FileInputStream("d:/dd.txt"));
//		byte[] bytes = new byte[5000];
//		int count = f.read(bytes, 0, 5000);
//		f.close();
//		
//		int byteLength = 5000 - 1;
//		while(bytes[byteLength] == 0) {
//			byteLength--;
//		}
//		
//		System.out.println(count == byteLength + 1);
		
		
		String CONFIGINI_PATH1 = "d:/cccc/config.ini";
		File configini = new File(CONFIGINI_PATH1);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(CONFIGINI_PATH1));

		File tempConfigini = new File(configini.getParent() + "/" + "configTemp.ini");
		//if(!tempConfigini.getParentFile().isFile()) {
//			tempConfigini.getParentFile().mkdirs();
		//}
//		tempConfigini.createNewFile();

		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempConfigini.getPath()));
		
		// 得到Jar文件的名字（去掉小数点和扩展名部分）
		String name = "123456.jar".substring(0, "123456.jar".lastIndexOf("."));
		
		String read;
		int flag = 0;
		while(true) {
			read = bufferedReader.readLine();
			if(read == null) {
				break;
			}
			
			if(read.contains("osgi.bundles")) {
				flag = 1;
				read= read + ",reference\\:file\\:cn.sunline.plugins/" + name +
						"/" + "123456.jar";
			}
			
			bufferedWriter.write(read + "\n");
		}
	
		bufferedReader.close();
		bufferedWriter.close();
		
		
		String path = configini.getPath();
		configini.delete();
		tempConfigini.renameTo(new File(path));
	}

	public Shell getShell() {
		return shell;
	}

}
