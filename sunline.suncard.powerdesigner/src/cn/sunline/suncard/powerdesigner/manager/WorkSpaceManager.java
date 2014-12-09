/* 文件名：     WorkSpaceManager.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-26
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.action.DatabaseAction;
import cn.sunline.suncard.powerdesigner.exception.CanNotFoundFolderFromWorkSpaceException;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.WorkSpaceModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.powerdesigner.tools.FileDeal;
import cn.sunline.suncard.powerdesigner.tools.ZipManager;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.powerdesigner.xml.SwitchObjAndXml;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 工作空间管理类
 * 包括打开、保存或关闭所做的文件操作
 * @author  Manzhizhen
 * @version 1.0, 2012-12-26
 * @see 
 * @since 1.0
 */
public class WorkSpaceManager {
	private static List<String> filesName = new ArrayList<String>();
	private static Log logger = LogManager.getLogger(WorkSpaceManager.class
			.getName());
	
	static {
		filesName.add(SystemConstants.ZIP_FILE_CODE);
		filesName.add(SystemConstants.ZIP_FILE_DOC);
		filesName.add(SystemConstants.ZIP_FILE_SUNCARDDESIGNER);
		filesName.add(SystemConstants.ZIP_FILE_SUNCARDDESIGNER_DATA);
	}
	
	/**
	 * 创建一个符合条件的新的设计文件
	 * @param zipFilePath
	 * @throws IOException 
	 */
	public static File createNewFile(String zipFilePath) throws IOException {
		if(zipFilePath == null) {
			logger.debug("传入的文件路径为空，无法创建一个新的设计文件！");
			return null;
		}
		
		File zipFile = new File(zipFilePath);
		zipFile.createNewFile();
		
		FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
		CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream,
				new CRC32()); // 不加CRC32，一样可以生成文件。关于数据如何校验，请高手指点

		ZipOutputStream zipOutputStream = new ZipOutputStream(cos);
		zipOutputStream.setEncoding(SystemConstants.FILE_CODE_GBK);
		
		// 需要向里面写入四个文件
		ZipEntry codeEntry = new ZipEntry(SystemConstants.ZIP_FILE_CODE + "/");
		zipOutputStream.putNextEntry(codeEntry);
		
		ZipEntry docEntry = new ZipEntry(SystemConstants.ZIP_FILE_DOC + "/");
		zipOutputStream.putNextEntry(docEntry);
		
		// 数据库设计spd文件的Doc
		Document spdDocument = DocumentHelper.createDocument();
		Element sdpRootE = spdDocument.addElement(DmConstants.SPD_XML_ROOT_ELEMENT_NAME);
		sdpRootE.addElement(FileModel.getElementName());
		sdpRootE.addElement(DmConstants.SPD_XML_COLUMNS_ELEMENT_NAME);
		sdpRootE.addElement(DmConstants.SPD_XML_TABLES_ELEMENT_NAME);
		sdpRootE.addElement(DmConstants.SPD_XML_SHORTCUTS_ELEMENT_NAME);
		sdpRootE.addElement(DmConstants.SPD_XML_PHYSICALDIAGRAMS_ELEMENT_NAME);
		Element gefE = sdpRootE.addElement(DmConstants.SPD_XML_GEFINFO_ROOT_ELEMENT_NAME);	
		gefE.addElement(DmConstants.SPD_XML_GEFINFO_NODES_ELEMENT_NAME);
		gefE.addElement(DmConstants.SPD_XML_GEFINFO_LINES_ELEMENT_NAME);
		sdpRootE.addElement(DmConstants.SPD_XML_INDEX_SQL_ELEMENT_NAME);
		sdpRootE.addElement(DmConstants.SPD_XML_PRODUCTS_ELEMENT_NAME);
		
		// 表格初始化spdd文件的Doc
		Document spddDocument = DocumentHelper.createDocument();
		spddDocument.addElement(DmConstants.SPDD_XML_ROOT_ELEMENT_NAME);
		
		zipOutputStream.putNextEntry(new ZipEntry(SystemConstants.ZIP_FILE_SUNCARDDESIGNER));
		zipOutputStream.write(spdDocument.asXML().trim().getBytes(SystemConstants.FILE_CODE_GBK));
		
		zipOutputStream.putNextEntry(new ZipEntry(SystemConstants.ZIP_FILE_SUNCARDDESIGNER_DATA));
		zipOutputStream.write(spddDocument.asXML().trim().getBytes(SystemConstants.FILE_CODE_GBK));
		
		zipOutputStream.close();
		
		return zipFile;
	}
	
	/**
	 * 添加一个文件到工作空间目录中
	 * 涉及一个解压过程
	 * @throws Exception 
	 * @throws IOException 
	 */
	public static FileModel addFileToWorkSpace(File zipFile) throws IOException, Exception {
		if(!checkZipFile(zipFile)) {
			logger.error("文件内容不正确，无法添加到工作空间中!");
			return null;
		}
		
		String folderName = getNewFolderName(zipFile.getName().substring(0
				, zipFile.getName().lastIndexOf(".")));
		if(folderName == null) {
			logger.error("在工作空间新建文件目录失败！");
			return null;
		}
		
		// 解压该压缩文件到指定的文件夹下
		ZipManager.decompressFile(zipFile, new File(SystemConstants.WORKSPACEPATH
				, File.separator + folderName + File.separator));
		
		// 获得该文件对应在工作空间的文件夹目录
		File folderFile = new File(SystemConstants.WORKSPACEPATH
				, File.separator + folderName + File.separator);
		
		// 获得spd文件
		File spdFile = new File(folderFile, SystemConstants.ZIP_FILE_SUNCARDDESIGNER);
		if(!spdFile.isFile()) {
			logger.error("在工作空间中找不到对应的spd格式的文件：" + spdFile.getAbsolutePath());
			return null;
		}
		
		// 获得spdd文件
		File spddFile = new File(folderFile, SystemConstants.ZIP_FILE_SUNCARDDESIGNER_DATA);
		if(!spddFile.isFile()) {
			logger.error("在工作空间中找不到对应的spdd格式的文件：" + spddFile.getAbsolutePath());
			return null;
		}
		
		// 开始构建FileModel
		FileModel fileModel = SwitchObjAndXml
				.getFileModelFromFile(spdFile, spddFile, zipFile);
		
		fileModel.setFolderName(folderName);
		
		return fileModel;
	}
	
	/**
	 * 关闭一个文件时除了需要保存该文件，还需要把该文件的内容从工作空间中移除
	 * @throws CanNotFoundFolderFromWorkSpaceException 
	 * @throws IOException 
	 */
	public static void removeFromWorkSpace(FileModel fileModel) throws CanNotFoundFolderFromWorkSpaceException, IOException {
		if(fileModel == null) {
			logger.debug("传入的FileModel为空，无法将其对应文件夹其从工作空间中移除！");
			return ;
		}
		
		// 获取需要覆盖的压缩文件
		File targetFile = fileModel.getFile();
		FileDeal.deleteFile(targetFile.getAbsolutePath());
		
		File workSpaceFile = new File(SystemConstants.WORKSPACEPATH
				, fileModel.getFolderName() + File.separator);
		if(!workSpaceFile.exists()) {
			logger.error("该FileModel对应的在工作空间的文件夹不存在，关闭文件失败！");
			throw new CanNotFoundFolderFromWorkSpaceException(fileModel.getFolderName());
		}
		
		// 将最新的数据写入原压缩文件
		ZipManager.compressFiles(workSpaceFile, targetFile);
		
		// 从工作空间删除该文件夹
		FileDeal.deleteFile(workSpaceFile.getAbsolutePath());
	}
	
	/**
	 * 检查一个压缩文件中的内容是否正确
	 */
	public static boolean checkZipFile(File compressFile) {
		if(compressFile == null || !compressFile.isFile()) {
			logger.debug("传入的压缩文件为空或不正确！");
			return false;
		}
		
		// 将此压缩文件解压到一个临时目录，以便检查
		File tempFile = new File(SystemConstants.WORKSPACEPATH_TEMP);
		if(!tempFile.getParentFile().exists()) {
			tempFile.getParentFile().mkdirs();
		}
		
		try {
			// 解压文件
			ZipManager.decompressFile(compressFile, tempFile);
		} catch (IOException e) {
			logger.error("解压文件失败:" + e.getMessage());
			e.printStackTrace();
			FileDeal.deleteFile(tempFile.getAbsolutePath());
			return false;
		} catch (Exception e) {
			logger.error("解压文件失败:" + e.getMessage());
			e.printStackTrace();
			FileDeal.deleteFile(tempFile.getAbsolutePath());
			return false;
		}
		
		File[] files = tempFile.listFiles();
		
		// 一个完整的zip文件应该包含一个spd文件、一个spdd文件、一个文档文件夹和一个代码文件夹
		if(files.length != 4) {
			logger.debug("压缩文件中文件数量不正确！");
			FileDeal.deleteFile(tempFile.getAbsolutePath());
			return false;
		}
		
		for(File checkFile : files) {
			if(!filesName.contains(checkFile.getName())) {
				logger.debug("压缩文件中某个文件名称不正确！" + checkFile.getName());
				FileDeal.deleteFile(tempFile.getAbsolutePath());
				return false;
			}
		}
		
		FileDeal.deleteFile(tempFile.getAbsolutePath());
		return true;
	}
	
	/**
	 * 打开或新建一个文件时，需要在工作空间目录下新建一个文件夹来存放解压后的文件内容
	 * 
	 * @return 新的文件夹的名称
	 */
	public static String getNewFolderName(String fileName) {
		if(fileName == null || fileName.isEmpty()) {
			logger.error("传入的文件名称为空，无法根据其文件名称获得新文件夹名称！");
			return null;
		}
		
		File file = new File(SystemConstants.WORKSPACEPATH);
		if(!file.exists()) {
			file.mkdirs();
		}
		
		File[] folderFiles = file.listFiles();
		List<String> folderNameList = new ArrayList<String>();
		if(folderFiles != null && folderFiles.length > 0) {
			for(File tempFile : folderFiles) {
				if(tempFile.isDirectory()) {
					folderNameList.add(tempFile.getName());
				}
			}
			
			if(!folderNameList.contains(fileName)) {
				return fileName;
			}
			
			int num = 1;
			while(true) {
				if(folderNameList.contains(fileName + num)) {
					num ++;
				} else {
					return fileName + num;
				}
			}
			
			
		} else {
			return fileName;
		}
	}
	
	/**
	 * 系统退出前需要关闭和保存所有文件
	 */
	public static void closeAllSpdFile() {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow();
		DatabaseTreeViewPart databaseTreeViewPart = (DatabaseTreeViewPart) window.getActivePage().findView(DatabaseTreeViewPart.ID);
		
		if(databaseTreeViewPart != null) {
			TreeContent workSpaceTreeContent = databaseTreeViewPart.getTreeViewComposite()
					.getRootContentList().get(0);
			
			List<TreeContent> fileTreeContentList = workSpaceTreeContent.getChildrenList();
			while(fileTreeContentList.size() > 0) {
				DatabaseAction.closeFileTreeContent(window, fileTreeContentList.get(0));
			}
		}
	}
	
}
