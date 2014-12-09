/*
 * 文件名：     SDEPluginZip.java
 * 版权：          Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	压缩解压类
 * 修改人：     易振强
 * 修改时间：2011-9-21
 * 修改内容：创建
 */

package cn.sunline.suncard.sde.bs.ui.plugin.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;

import cn.sunline.suncard.sde.bs.exception.FileNumError;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.ui.plugin.PluginAdd;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 压缩解压类 可以对插件进行压缩、解压和读取压缩文件流
 * 
 * @author 易振强
 * @version 1.0, 2011-9-21
 * @since 1.0
 */
public class SDEPluginZip {
	// 将要被压缩的jar包和xml文件所在的文件夹路径。压缩后的插件文件也放入该目录中。
	private String folderPath = null;
	// 插件压缩包所在的路径。
	private String filePath = null;

	public final static String WIDGETS_FILE_NAME = "widgets.xml";

	// 该插件的Jar文件下可能会有resources文件夹（如决策制定系统（DM）），如果有，
	// 则需要拷贝里面的内容到基础框架下相应的文件夹下。
	public final static String RESOURCES = "resources/";

	// 该插件的Jar文件下可能会有config文件夹（如决策制定系统（DM）），如果有，
	// 则需要拷贝里面的内容到基础框架下相应的文件夹下。
	// public final static String CONFIG = "config" + File.separator;
	public final static String CONFIG = "config/mapping/";

	public final static int BUFFER_SIZE = 0x00ff86A0;

	private Shell shell;

	public static Log logger = LogManager.getLogger(SDEPluginZip.class
			.getName());

	// Jar文件的byte大小
	public static int JAR_SIZE = 0;
	// 插件配置文件的byte大小

	public static int XML_SIZE = 0;

	// 插件的控件配置文件的byte大小
	public static int WDIGET_XML_SIZE = 0;

	public static int MAP_SIZE = 0;

	public SDEPluginZip() {
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * 压缩插件文件 通过文件夹路径来压缩在文件夹下的三个个文件（Jar文件和配置文件）
	 * 
	 * @param fileName
	 *            String 压缩后的文件名
	 * @throws IOException
	 * @exception/throws [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public void compressFiles(String fileName) throws IOException {
		// if (folderPath == null) {
		// MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
		// I18nUtil.getMessage("PLUGIN_ERROR"),
		// I18nUtil.getMessage("PLUGIN_FILE_PATH_ERROR"));
		// return;
		// }
		//
		// byte data[] = new byte[BUFFER_SIZE];
		// File folder = new File(folderPath);
		// File files[] = folder.listFiles();
		//
		// // //一个插件文件下只应该有一个jar文件和两个个配置文件。
		// // if(files.length != 3) {
		// //
		// MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
		// // I18nUtil.getMessage("PLUGIN_ERROR"),
		// I18nUtil.getMessage("PLGUIN_FILE_NUM_ERROR"));
		// // return;
		// // }
		//
		// BufferedInputStream bufferedInputStream = null;
		// FileOutputStream fileOutputStream = new FileOutputStream(folderPath +
		// "/" + fileName + ".plugin");
		// ZipOutputStream out = new ZipOutputStream(new
		// BufferedOutputStream(fileOutputStream));
		//
		// //依次对这三个进行压缩
		// for (File file : files) {
		// FileInputStream fi = new FileInputStream(file);
		// bufferedInputStream = new BufferedInputStream(fi, BUFFER_SIZE);
		//
		// ZipEntry entry = new ZipEntry(file.getName());
		// //开始写入新的 ZIP 文件条目并将流定位到条目数据的开始处。
		// out.putNextEntry(entry);
		//
		// int count;
		// while ((count = bufferedInputStream.read(data, 0, BUFFER_SIZE)) !=
		// -1) {
		// out.write(data, 0, count);
		// }
		//
		// bufferedInputStream.close();
		// }
		//
		// out.close();

		File file = new File(folderPath);
		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(
				file.getParent() + "/" + fileName));
		zipFile(zipOut, file, null);

		zipOut.close();
	}

	public void zipFile(ZipOutputStream zipOut, File file, String path)
			throws IOException {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File fileTemp : files) {
				if (path == null || "".equals(path)) {
					zipFile(zipOut, fileTemp, fileTemp.getName());
				} else {
					zipFile(zipOut, fileTemp, path + "/" + fileTemp.getName());
				}
			}

		} else {
			zipOut.putNextEntry(new ZipEntry(path));

			FileInputStream fileInputStream = new FileInputStream(file);
			byte[] bytes = new byte[BUFFER_SIZE];
			int count;
			while ((count = fileInputStream.read(bytes, 0, BUFFER_SIZE)) != -1) {
				zipOut.write(bytes, 0, count);
			}

			fileInputStream.close();
		}
	}

	/**
	 * 解压插件文件。 解压到的文件会在filePath目录下。
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public void decompressionFile(String outPath) throws IOException {
		logger.info("void decompressionFile(String outPath) 正在解压文件：" + filePath);

		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String path;
		if (outPath == null) {
			path = new File(filePath).getParent();
		} else {
			path = outPath;
		}

		Enumeration emu = zipFile.entries();
		while (emu.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) emu.nextElement();
			// 会把目录作为一个file读出一次，所以只建立目录就可以，之下的文件还会被迭代到。
			if (entry.isDirectory()) {
				File file = new File(new File(path), entry.getName());

				// logger.info("正在解压：" + file.getAbsolutePath());

				if (!file.exists()) {
					file.mkdirs();
				}
				continue;
			}

			BufferedInputStream bufferedInputStream = new BufferedInputStream(
					zipFile.getInputStream(entry));

			File file = null;

			file = new File(new File(path), entry.getName());

			// logger.info("正在解压：" + file.getAbsolutePath());

			BufferedOutputStream bufferedOutputStream = null;
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(file);
				bufferedOutputStream = new BufferedOutputStream(
						fileOutputStream, BUFFER_SIZE);
			} catch (Exception e) {
				e.printStackTrace();
			}

			int count;
			byte data[] = new byte[BUFFER_SIZE];
			count = bufferedInputStream.read(data, 0, BUFFER_SIZE);
			bufferedOutputStream.write(data, 0, count);

			bufferedOutputStream.flush();
			bufferedOutputStream.close();
			bufferedInputStream.close();
		}

		zipFile.close();
	}

	/**
	 * 从压缩插件文件中获取流。 之所以有这个方法是因为如果MD5校验不对，就不必解压来安装这个插件了。
	 * 
	 * @param type
	 * @return Map<String, byte[]> 返回 文件名/文件字符流 Map
	 * @throws IOException
	 * @throws FileNumError
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, byte[]> getByteFromDecompressionFile() throws IOException, FileNumError {
		logger.info("Map<String, byte[]> getByteFromDecompressionFile() ");
		
		WorkbenchWindow workbenchWindow = (WorkbenchWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		shell = null;
		if(workbenchWindow != null) {
			shell = workbenchWindow.getShell();
		} else {
//			shell = PlatformUI.getWorkbench().getDisplay().getShells()[0];
		}
		
		Map<String, byte[]> byteMap = new HashMap<String, byte[]>();
		
		String decompressPath = new File(new File(filePath).getParent(), 
				File.separator + "%temp%" + File.separator).getAbsolutePath();
		
		File file = new File(decompressPath);
		if(!file.exists()) {
			file.mkdirs();
		}
		
		// 先解压插件文件。。。
		logger.info("先解压插件文件。。。");
		logger.info("插件文件路径：" + filePath);
		logger.info("插件目录：" + decompressPath);
		decompressionFile(decompressPath);
		
		File[] files = file.listFiles();
		
		if(files == null || files.length != 2) {
			logger.error("插件压缩文件里面的数量不为2（一个Jar文件和一个xml文件）。。。");
			throw new FileNumError();
		}
		
		for(File makeFile : files) {
			// 如果是插件的Jar文件。。。
			if(makeFile.getName().endsWith(".jar")) {
				logger.info("解压后的Jar文件：" + makeFile.getAbsolutePath());
//				JarInputStream jarInputStream = new JarInputStream(
//						new FileInputStream(makeFile));
				
				BufferedInputStream jarInputStream = new BufferedInputStream(
						new FileInputStream(makeFile));
				
				// 读取Jar数据到相应的Map
				logger.info("开始读取Jar的byte数据到相应的Map...");
				
				byte[] jarData = new byte[BUFFER_SIZE];			
				int count = jarInputStream.read(jarData, 0, BUFFER_SIZE);
				
				if(count >= BUFFER_SIZE) {
					logger.error("Jar包超过规定大小！");
					shell.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {	
							MessageDialog.openWarning(shell, I18nUtil.getMessage("PLUGIN_WARING"), 
									I18nUtil.getMessage("PLUGIN_MAX_FILE_SIZE_WARING"));
						}
					});	
				} else {
					JAR_SIZE = count;
				}
				
				// 将Jar包的字节流存储在Map中
				byteMap.put(makeFile.getName(), jarData);
				
				jarInputStream.close();

				JarFile jarFile = new JarFile(makeFile.getAbsolutePath());
				Enumeration emu = jarFile.entries();
				
				// 在Jar包里面寻找控件配置文件和resources、config文件夹。
				logger.info("在Jar包里面寻找控件配置文件和resources、config文件夹...");
				while(emu.hasMoreElements()) {
					ZipEntry zipEntry = (ZipEntry)emu.nextElement();
					
					// 说明找到控件的配置文件了（widgets.xml）
					if(zipEntry.getName().contains(WIDGETS_FILE_NAME)) {
						logger.info("该插件Jar中有控件配置文件：" + zipEntry.getName());
						
						byte[] byteWidget = new byte[BUFFER_SIZE];
						logger.info("该压缩条目的未压缩大小：" + zipEntry.getSize());
						long l =  zipEntry.getSize();
						
//						JarInputStream widgetInputStream = new JarInputStream(jarFile.getInputStream(zipEntry));
						BufferedInputStream bufferedInputStream = new BufferedInputStream(
								jarFile.getInputStream(zipEntry));
						int countWidget = bufferedInputStream.read(byteWidget, 0, (int)l);

						// 如果该配置文件超过尺寸，则警告
						if(countWidget >= BUFFER_SIZE) {
							logger.error("widget.xml文件超过规定大小！");
							shell.getDisplay().asyncExec(new Runnable() {
								@Override
								public void run() {	
									MessageDialog.openWarning(shell, I18nUtil.getMessage("PLUGIN_WARING"), 
											I18nUtil.getMessage("PLUGIN_MAX_FILE_SIZE_WARING"));
								}
							});	
						} else {
							WDIGET_XML_SIZE = countWidget;
						}
						
						// 将该控件的配置文件写入Map，以后会用到
						byteMap.put(WIDGETS_FILE_NAME, byteWidget);
						
						bufferedInputStream.close();
						
					} else if(zipEntry.getName().equals(RESOURCES)) {
						logger.info("该插件Jar中有resources文件夹");

						// 做个记号，表明有resources文件夹。
						byteMap.put(RESOURCES, new byte[1]);
						
					} else if(zipEntry.getName().startsWith("config")) {
						logger.info("该插件Jar中有config文件夹");
						
						// 做个记号，表明有config文件夹
						byteMap.put(CONFIG, new byte[1]);
					}
				}
				
				jarInputStream.close();
				
			// 如果是插件的配置文件
			} else if(makeFile.getName().endsWith(".xml")) {
				logger.info("插件配置文件：" + makeFile.getAbsolutePath());
				
				// 将插件配置文件的字节流存储在Map中，以便后面提取。
				BufferedInputStream bufferedInputStream = new BufferedInputStream(
						new FileInputStream(makeFile));
			
				byte[] data = new byte[BUFFER_SIZE];	
				int count = bufferedInputStream.read(data, 0, BUFFER_SIZE);
				if(count >= BUFFER_SIZE) {
					logger.error("插件配置文件超过规定大小！");
					MessageDialog.openWarning(shell, I18nUtil.getMessage("PLUGIN_WARING"), 
							I18nUtil.getMessage("PLUGIN_MAX_FILE_SIZE_WARING"));
				} else {
					XML_SIZE = count;
				}
				// 将Jar包的字节流存储在Map中
				byteMap.put(makeFile.getName(), data);
				bufferedInputStream.close();
			}

		}

		FileDeal.deleteFile(new File(file.getAbsolutePath(), 
				File.separator).getAbsolutePath());
		
		return byteMap;
	}

	public void test() throws IOException {
		JarFile jarFile = new JarFile("d:/456/Total2.jar");
		Enumeration emu = jarFile.entries();
		while (emu.hasMoreElements()) {

			JarEntry entry = (JarEntry) emu.nextElement();

			if (entry.isDirectory()) {
				new File("d:/456/" + entry.getName()).mkdirs();
				continue;
			}

			BufferedInputStream bufferedInputStream = new BufferedInputStream(
					jarFile.getInputStream(entry));

			File file = new File("d:/456/" + entry.getName());

			BufferedOutputStream bs = new BufferedOutputStream(
					new FileOutputStream(file));

			byte data[] = new byte[BUFFER_SIZE];
			int count = 0;
			while ((count = bufferedInputStream.read(data, 0, BUFFER_SIZE)) != -1) {
				bs.write(data, 0, count);
			}

			bs.flush();
			bs.close();
			bufferedInputStream.close();
		}

		jarFile.close();

	}

	public void deleteFolder() {

	}

	public static void main(String[] args) throws IOException {
		SDEPluginZip s = new SDEPluginZip();
		// s.setFolderPath("D:\\kkk");
		s.setFilePath("D:\\kkk\\kkk.zip");
		s.decompressionFile("D:\\kkk\\123\\");

		// ZipFile zipFile = new ZipFile("d:/Total2.jar");
		// Enumeration emu = zipFile.entries();
		// while (emu.hasMoreElements()) {
		// ZipEntry entry = (ZipEntry)emu.nextElement();
		// String fileName = entry.getName();
		// System.out.println(fileName);
		//
		// if(fileName.equals("Total2.jar")) {
		// ZipInputStream zis = new
		// ZipInputStream(zipFile.getInputStream(entry));
		//
		// ZipEntry z;
		// while((z = zis.getNextEntry()) != null) {
		// String name = z.getName();
		// System.out.println(name);
		// if(name.contains("xml")) {
		// byte[] b = new byte[100];
		// int count = zis.read(b, 0, 100);
		// System.out.println(count);
		// }
		// }
		// }
		// }

		// SDEPluginZip s = new SDEPluginZip();
		// s.setFolderPath("d:/dd/dd/");
		// s.compressFiles("kk.jar");

		// File file = new File("C:/Users/Manzhizhen/Desktop/cardsde4/@temp@/");
		// file.delete();
	}

}
