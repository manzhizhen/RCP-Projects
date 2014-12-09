package cn.sunline.suncard.sde.bs.ui.plugin.patch;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarOutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

import cn.sunline.suncard.sde.bs.Activator;
import cn.sunline.suncard.sde.bs.biz.BsPatchBiz;
import cn.sunline.suncard.sde.bs.biz.BsPluginBiz;
import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsPatch;
import cn.sunline.suncard.sde.bs.entity.BsPatchId;
import cn.sunline.suncard.sde.bs.entity.BsPlugin;
import cn.sunline.suncard.sde.bs.entity.BsPluginId;
import cn.sunline.suncard.sde.bs.exception.FileNumError;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.ui.dailogs.PatchProgressBar;
import cn.sunline.suncard.sde.bs.ui.dailogs.PluginProgressBar;
import cn.sunline.suncard.sde.bs.ui.plugin.JarXMLAnalysis;
import cn.sunline.suncard.sde.bs.ui.plugin.PluginAdd;
import cn.sunline.suncard.sde.bs.ui.plugin.tools.ControlXMLAnalysis;
import cn.sunline.suncard.sde.bs.ui.plugin.tools.FileDeal;
import cn.sunline.suncard.sde.bs.ui.plugin.tools.JarMD5Checkout;
import cn.sunline.suncard.sde.bs.ui.plugin.tools.SDEPluginZip;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

public class PatchAdd {
	private Shell shell = null;
	
	/**
	 * 补丁文件的扩展名
	 */
	private String FILTER_EXTENSIONS = "*.patch";
	/**
	 * 设定插件的备份位置。
	 */
	public final static String REPLACE_URL = PatchManager.REPLACE_URL;
	private String backFilePath = null;
	
	/**
	 * 插件解压的临时目录
	 */
	public final static String TEMP_FILE_AREA = "@temp@";
	public final static String TEMP_FILE_AREA1 = "@temp1@";
	
	/**
	 * byte数组大小
	 */
	private final static int BUFFER_SIZE = SDEPluginZip.BUFFER_SIZE;
	
	private JarMD5Checkout JarMD5Checkout = new JarMD5Checkout();
	private PatchXMLAnalysis patchXMLAnalysis = null;
	private ControlXMLAnalysis controlXMLAnalysis = null;
	
	private long bankorgId = ((Long)Context.getSessionMap().get(Constants.BANKORG_ID)).longValue();
	private String pcId = (String) Context.getSessionMap().get(Constants.PC_ID);
	
	/**
	 * 即将被更新的Jar包
	 */
	private File oldJarFile = null;
	
	private BsPatch bsPatch;
	private BsPlugin dbPlugin;
	
	private BsPluginBiz bsPluginBiz = new BsPluginBiz();
	private BsPatchBiz bsPatchBiz = new BsPatchBiz();
	
	private SDEPluginZip sDEPluginZip = new SDEPluginZip();
	
	/**
	 * 补丁文件的路径
	 */
	private String filePath = null;
	private Map<String, byte[]> fileMap = null;
	
	/**
	 * Jar包文件名
	 */
	private String jarFileName = null;
	/**
	 * XML文件名
	 */
	private String xmlFileName = null;
	
	/**
	 * 插件文件路径为空的信息码
	 */
	public final static int PLUGIN_FILE_PATH_NULL = PluginAdd.PLUGIN_FILE_PATH_NULL;
	
	/**
	 * 插件文件数量不对的信息码
	 */
	public final static int PLUGIN_FILE_MISS = PluginAdd.PLUGIN_FILE_MISS;
	
	/**
	 * 插件文件数量正确的信息码
	 */
	public final static int PLUGIN_FILE_RIGHT = PluginAdd.PLUGIN_FILE_RIGHT;
	
	/**
	 * MD5码不一致的信息码
	 */
	public final static int MD5_DIFFERENCE = PluginAdd.MD5_DIFFERENCE;
	
	/**
	 * Document对象的获得失败的信息码
	 */
	public final static int INIT_DOCUMENT_FAIL = PluginAdd.INIT_DOCUMENT_FAIL;
	
	/**
	 * Jar的XML配置文件类型不是插件类型的信息码
	 */
	public final static int XML_TYPE_ERROR = PluginAdd.XML_TYPE_ERROR;
	
	/**
	 * Jar的XML配置文件校验成功的信息码
	 */
	public final static int XML_CHECK_SUCCESS = PluginAdd.XML_CHECK_SUCCESS;
	
	/**
	 * 安装被取消的信息码
	 */
	public final static int INSTALL_CANCLE = PluginAdd.INSTALL_CANCLE;
	
	/**
	 * 进度条异常信息码
	 */
	private final static int INVOCATION_TARGET_EXCEPTION = PluginAdd.INVOCATION_TARGET_EXCEPTION;
	
	/**
	 * 进度条线程中断异常信息码
	 */
	private final static int INTERRUPTED_EXCEPTION = PluginAdd.INTERRUPTED_EXCEPTION;
	
	/**
	 * 插件安装成功的信息码
	 */
	public final static int INSTALL_SUCCESS = PluginAdd.INSTALL_SUCCESS;
	
	/**
	 * 添加日志
	 */
	public static Log logger = LogManager.getLogger(PluginAdd.class.getName());
	
	/**
	 * 要被更新的Bundle。
	 */
	private Bundle updateBundle;
	
	
	public PatchAdd(Control shell) {
		this.shell = (Shell) shell;
	}
	
	/**
	 * 安装补丁
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 * @throws BundleException 
	 */
	public void installPatch() {
		int flag = INSTALL_SUCCESS;
		// 如果用户在浏览补丁文件时选择取消
		if(addPluginDialog() == null) {
			return ;
		}
		
		// 进度条对话框
		ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(shell);
		
		PatchProgressBar patchProgressBar = new PatchProgressBar(this);
		
		try {
			progressMonitorDialog.run(true, false, patchProgressBar);
			flag = patchProgressBar.getFlag();
			
		} catch (InvocationTargetException e) {
			flag = INVOCATION_TARGET_EXCEPTION;
			// 写日志
			logger.error("补丁安装进度条异常" + " -- " + e.getMessage());
			e.printStackTrace();
			
		} catch (InterruptedException e) {
			flag = INTERRUPTED_EXCEPTION;
			// 写日志
			logger.error("补丁安装进度条线程中断异常" + " -- " + e.getMessage());
			e.printStackTrace();
		}
		
		if (PluginAdd.INSTALL_SUCCESS == flag) {
			MessageDialog.openInformation(shell, I18nUtil.getMessage("PATCH_PROMPT"), 
					I18nUtil.getMessage("PATCH_INSTALL_SUCCESS"));
		}  else if(INVOCATION_TARGET_EXCEPTION == flag || INTERRUPTED_EXCEPTION == flag) {
			MessageDialog.openInformation(shell,
					I18nUtil.getMessage("PLUGIN_ERROR"),
					I18nUtil.getMessage("PATCH_INSTALL_FAIL"));
		}

	}
	
	/**
	 * 全面启动补丁安装
	 * @param monitor
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws BundleException
	 * @throws FileNumError 
	 * @throws DocumentException 
	 */
	public int allStart(IProgressMonitor monitor) throws NoSuchAlgorithmException, IOException, BundleException, FileNumError, DocumentException {
		try {
			monitor.beginTask(I18nUtil.getMessage("PROGRESS_BAR_INIT_FILEFLOW"), IProgressMonitor.UNKNOWN);
			// 初始化文件流，为读取数据做准备
			if(initFileFlow() != PLUGIN_FILE_RIGHT) {
				return PLUGIN_FILE_MISS;
			}
			
			monitor.subTask(I18nUtil.getMessage("PROGRESS_BAR_MD5"));
			// 校验MD5码
			if (!compareMD5()) {
				return MD5_DIFFERENCE;
			}
			
			monitor.subTask(I18nUtil.getMessage("PROGRESS_BAR_CHECK_XML"));
			// 初步检查插件配置文件
			int flag = checkPluginXML();
			if(flag != XML_CHECK_SUCCESS) {
				return flag;
			}
			
			monitor.subTask(I18nUtil.getMessage("PROGRESS_BAR_INSTALL"));
			// 开始正式安装
			if(!startInstallPatch()) {
				return INSTALL_CANCLE;
			}
		} finally {
			monitor.done();
		}
		
		return INSTALL_SUCCESS;
	}
	
	
	// 打开补丁添加对话框
	public String addPluginDialog() {
		// 创建一个文件对话框，用于打开补丁文件。
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN | SWT.SINGLE);
		fileDialog.setFilterExtensions(new String[] {FILTER_EXTENSIONS});
		fileDialog.setText(I18nUtil.getMessage("PATCH_OPEN"));

		filePath = fileDialog.open();
		return filePath;
	}
	
	// 通过压缩文件流来初始化相应的管理对象。
	public int initFileFlow() throws IOException, NoSuchAlgorithmException, FileNumError, DocumentException {
		if (filePath == null) {
			throw new IOException();
		}

		// 创建一个SDEPluginZip对象，用于从压缩文件中读取数据或解压文件，并初始化压缩文件路径。
		sDEPluginZip = new SDEPluginZip();
		sDEPluginZip.setFilePath(filePath);

		// 因为压缩文件中共有两个文件（XML和Jar），创建一个Map便于管理，其键为文件名，值为对应的文件数据流。
		fileMap = sDEPluginZip.getByteFromDecompressionFile();
		// 得到键的Set。
		Set<String> fileSet = fileMap.keySet();
		Iterator<String> fileIterator = fileSet.iterator();

		// 有效文件数量，有三个有效文件（插件Jar包、插件Jar包中的控件配置文件和补丁的配置文件）
		int fileNum = 0;
		// 读取列表中的文件流对象。
		while (fileIterator.hasNext()) {
			String fileName = (String) fileIterator.next();
			
				//如果是补丁的配置文件。
			if(fileName.endsWith(SDEPluginZip.WIDGETS_FILE_NAME)) {	//如果是控件的配置文件。
				controlXMLAnalysis = new ControlXMLAnalysis((byte[]) fileMap.get(fileName));
				fileNum++;
				
				// 如果是XML文件
			}else if (fileName.endsWith(".xml")) {		
				xmlFileName = fileName;
				// 用XML文件数据流初始化JarXMLAnalysis对象以便于提取其中的MD5值。
				patchXMLAnalysis = new PatchXMLAnalysis(
						(byte[]) fileMap.get(fileName));
				fileNum++;
				
				//如果是Jar文件。
			} else if (fileName.endsWith(".jar")) {	
				jarFileName = fileName;
				// 用Jar文件数据流初始化JarXMLAnalysis对象以便于计算其中的MD5值。
				JarMD5Checkout.initHashCode((byte[]) fileMap.get(fileName));
				fileNum++;
			} else {
				break;
			}
		}
		
		// 如果Map里面的文件数目不为三，则说明插件压缩文件有缺失。
		if(fileNum != 3) {
			return PLUGIN_FILE_MISS;
		} else {
			return PLUGIN_FILE_RIGHT;
		}
	}
	
	/**
	 * 校验MD5码
	 */
	public boolean compareMD5() {
		// 得到XML文件的MD5值。
		String XMLCode = patchXMLAnalysis.getJarXMLAnalysis().getMD5FromXML();

		// 算出Jar文件的MD5值。
		String fileCode = JarMD5Checkout.getHashCode();

		return fileCode.equals(XMLCode);
	}
	
	/**
	 *  初步检查补丁配置文件
	 */
	public int checkPluginXML() {
		Document document = patchXMLAnalysis.getDocument();
		if(document == null) {
			return INIT_DOCUMENT_FAIL;
		}
		
		// 补丁配置文件中插件类型是否为“P”（表示补丁）
		if(!"P".equals(patchXMLAnalysis.getJarXMLAnalysis().getPluginType().trim())) {
			return XML_TYPE_ERROR;
		}
		
		return XML_CHECK_SUCCESS;
	}
	
	/**
	 *  更新补丁及其相关控件信息到数据库。
	 * @return boolean
	 * @throws BundleException
	 * @throws IOException
	 */
	public boolean startInstallPatch() throws BundleException, IOException {
		// 通过XML中的插件ID从数据库中找到对应的插件数据。
		dbPlugin = bsPluginBiz.findByPluginId(patchXMLAnalysis.getParentPluginId());
		
		if(dbPlugin == null) {
			shell.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {	
					MessageDialog.openError(shell, I18nUtil.getMessage("PATCH_ERROR"), I18nUtil.getMessage("PATCH_PARENT_ID_ERROR"));
				}
			});	

			return false;
		}
		
		String pluginVersion = dbPlugin.getPluginVer();
		List<String> list = patchXMLAnalysis.getSuitableVersions();
		if(!list.contains(pluginVersion)) {
			shell.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {	
					MessageDialog.openError(shell, I18nUtil.getMessage("PATCH_ERROR"), I18nUtil.getMessage("PATCH_VERSION_NOT_SUIT"));
				}
			});	

			return false;
		}
		
		// 用配置文件的数据来创建补丁对象
		BsPatchId bsPatchId = new BsPatchId(bankorgId, pcId,  
				patchXMLAnalysis.getJarXMLAnalysis().getPluginId());
		bsPatch = new BsPatch(bsPatchId);
		bsPatch.setPatchVer(patchXMLAnalysis.getJarXMLAnalysis().getPluginVersion());
		bsPatch.setPluginId(patchXMLAnalysis.getParentPluginId());
		bsPatch.setPatchName(patchXMLAnalysis.getJarXMLAnalysis().getPluginName());
		
		// 从控件XML文件中解析信息，初始化控件对象列表
		controlXMLAnalysis.initControlList();
		
		bsPatchBiz.updatePatchDb(this);
		
		return true;		
	}
	
	// 备份插件Jar包
	public void backupJar() throws IOException {
		// 通过XML中的插件ID从数据库中找到对应的插件数据。
		dbPlugin = bsPluginBiz.findByPluginId(patchXMLAnalysis.getParentPluginId());
		
		// 在基础框架中找到该插件。
		String pluginName = dbPlugin.getPluginName();
		BundleContext bundleContext = Activator.getBundleContext();
		Bundle[] bundles = bundleContext.getBundles();
		updateBundle = null;
		for(Bundle bundle : bundles) {
			if(bundle.getHeaders().get("Bundle-Name").equals(pluginName)) {
				updateBundle = bundle;
				break;
			}
		}

		oldJarFile = FileLocator.getBundleFile(updateBundle);

		FileInputStream fileInputStream = new FileInputStream(oldJarFile);

		backFilePath = REPLACE_URL + pluginName + "/" + pluginName +
				"_" + patchXMLAnalysis.getJarXMLAnalysis().getPluginId() + "_" + 
				dbPlugin.getPluginVer() + ".jar";
		File backJarFile = new File(backFilePath);

		File parent = backJarFile.getParentFile();
		if(!parent.exists()) {
			parent.mkdirs();
		}
		
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
				new FileOutputStream(backJarFile));
	
		byte[] bytes = new byte[BUFFER_SIZE];
		int count = 0;
		while((count = fileInputStream.read(bytes, 0, BUFFER_SIZE)) != -1) {
			bufferedOutputStream.write(bytes, 0, count);
		}
		
		fileInputStream.close();
		bufferedOutputStream.close();
	}
	
	// 添加补丁文件到Jar包
	public void addClassToJar() throws IOException, BundleException {

		File file = new File(TEMP_FILE_AREA);
		if(!file.exists()) {
			file.mkdirs();
		}
		
		file = new File(TEMP_FILE_AREA1);
		if(!file.exists()) {
			file.mkdirs();
		}
		
		// 在TEMP_FILE_AREA目录下解压插件的Jar包
		sDEPluginZip.setFilePath(oldJarFile.getPath());
		sDEPluginZip.decompressionFile(TEMP_FILE_AREA);
		
		// 在TEMP_FILE_AREA目录下解压补丁的Jar包
		sDEPluginZip.setFilePath(filePath);
		sDEPluginZip.decompressionFile(TEMP_FILE_AREA1);
	
		String patchJarPath = null;
		// 在TEMP_FILE_AREA1目录下找到补丁的Jar文件。
		File patchFiles = new File(TEMP_FILE_AREA1) ;
		File[] files = patchFiles.listFiles();
		for(File fileTemp : files) {
			if(fileTemp.getPath().endsWith(".jar")) {
				patchJarPath = fileTemp.getPath();
				break;
			}
		}
			
		sDEPluginZip.setFilePath(patchJarPath);
		sDEPluginZip.decompressionFile(TEMP_FILE_AREA);

		File fileAb = new File(TEMP_FILE_AREA + "/");
		sDEPluginZip.setFolderPath(fileAb.getAbsolutePath());
		sDEPluginZip.compressFiles(oldJarFile.getName());
		
		// 定位新生成的Jar文件
		File jarFile = new File(oldJarFile.getName());
		if(!jarFile.exists()) {
			return;
		}
	
		String path = oldJarFile.getAbsolutePath();

		
//		// 删除原来的插件Jar。
//		oldJarFile.delete();
		// 把新生成的Jar包移动到原来的插件Jar包的位置。
//		jarFile.renameTo(new File(path));

		// 覆盖原来的Jar文件。
//		copyToOther(jarFile.getAbsolutePath(), path);
		FileDeal.moveFileToPath(jarFile.getAbsolutePath(), path);
		
		// 删除文件的临时处理目录
		FileDeal.deleteFile(TEMP_FILE_AREA);
		FileDeal.deleteFile(TEMP_FILE_AREA1);
		
		// 安装完成后需要更新此插件
		updateBundle.update(new FileInputStream(new File(path)));
		
	}
//	
//	public void copyToOther(String startPath, String endPath) throws IOException {
//		BufferedInputStream bi = null;
//		BufferedOutputStream bo = null;		
//		try {
//			bi = new BufferedInputStream(new FileInputStream(startPath));
//			bo = new BufferedOutputStream(new FileOutputStream(endPath));
//			byte[] bytes = new byte[BUFFER_SIZE];
//			int count;
//			while((count = bi.read(bytes, 0, BUFFER_SIZE)) >= 0) {
//				bo.write(bytes, 0, count);
//			}
//			
//
//		}finally {
//			if(bi != null) {
//				bi.close();				
//			}
//
//			if(bo != null) {
//				bo.close();
//			}
//		}
//		
//		new File(startPath).delete();
//	}
	
	public PatchXMLAnalysis getPatchXMLAnalysis() {
		return patchXMLAnalysis;
	}

	public Shell getShell() {
		return shell;
	}

	public BsPatch getBsPatch() {
		return bsPatch;
	}

	public BsPlugin getDbPlugin() {
		return dbPlugin;
	}

	public String getBackFilePath() {
		return backFilePath;
	}

	public ControlXMLAnalysis getControlXMLAnalysis() {
		return controlXMLAnalysis;
	}	
	
	
}
