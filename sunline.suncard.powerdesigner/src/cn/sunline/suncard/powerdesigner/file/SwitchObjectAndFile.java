/* 文件名：     SwitchObjectAndFile.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	对象和文件互转类
 * 修改人：     易振强
 * 修改时间：2012-3-28
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.eclipse.gef.commands.CommandStack;

import cn.sunline.suncard.powerdesigner.gef.xml.GefFigureSwitchXml;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.ProjectModel;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProjectDefaultViewPart;
import cn.sunline.suncard.powerdesigner.xml.SwitchObjAndXml;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;


/**
 * 对象和文件互转类
 * @author  易振强
 * @version 1.0, 2012-3-28
 * @see 
 * @since 1.0
 */
public class SwitchObjectAndFile {
	private static Log logger = LogManager.getLogger(SwitchObjectAndFile.class.getName());
	
	/**
	 * 将工作流树节点对象保存到指定目录下
	 * 文件名为该对象的id
	 */
	public static void SaveFileModelToFile(FileModel fileModel) throws IOException {
		if(fileModel == null) {
			logger.error("传入的FileModel的为空，保存失败！");
			return ;
		}
		
//		File file = fileModel.getFile();
		// 数据库设计文件
		File spdFile = new File(SystemConstants.WORKSPACEPATH, 
				fileModel.getFolderName() + File.separator + SystemConstants.ZIP_FILE_SUNCARDDESIGNER);
		if(!spdFile.exists()) {
			spdFile.createNewFile();
		}
		
		// 表格初始化信息文件 
		File spddFile = new File(SystemConstants.WORKSPACEPATH, 
				fileModel.getFolderName() + File.separator + SystemConstants.ZIP_FILE_SUNCARDDESIGNER_DATA);
		if(!spddFile.exists()) {
			spddFile.createNewFile();
		}
//		// 存放表格初始化信息的文件夹 
//		File spddFolder = new File(SystemConstants.WORKSPACEPATH, 
//				fileModel.getFolderName() + File.separator + SystemConstants.ZIP_FILE_SUNCARDDESIGNER_DATA_FOLDER + File.separator);
//		if(!spddFolder.exists()) {
//			spddFolder.mkdirs();
//		}
		
		Document document = SwitchObjAndXml.getDocFromFileModel(fileModel);
		// 写入设计文件信息
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(spdFile), "UTF-8");
		out.write(document.asXML());
		out.close();
		
		// 写入表格初始化信息
		document = SwitchObjAndXml.getInitTableDataDocFromFileModel(fileModel);
		out = new OutputStreamWriter(new FileOutputStream(spddFile), "UTF-8");
		out.write(document.asXML());
		out.close();
		
		// 给文件模型对应的CommandStack设定保存点
		CommandStack commandStack = DefaultViewPart.getFileCommandFromObj(fileModel);
		if(commandStack != null) {
			commandStack.markSaveLocation();
		}
	}
	
	/**
	 * 将工作流树节点对象保存到指定目录下
	 * 文件名为该对象的id
	 */
	public static void SaveProjectModelToFile(ProjectModel projectModel) throws IOException {
		if(projectModel == null) {
			logger.error("传入的FileModel的为空，保存失败！");
			return ;
		}
		
		// 数据库设计文件
		File ppdFile = new File(SystemConstants.PROJECTSPACEPATH, 
				projectModel.getFolderName() + File.separator + SystemConstants.ZIP_FILE_PROJECT);
		if(!ppdFile.exists()) {
			ppdFile.createNewFile();
		}
		
		// 表格初始化信息文件 
		File spddFile = new File(SystemConstants.PROJECTSPACEPATH, 
				projectModel.getFolderName() + File.separator + SystemConstants.ZIP_FILE_SUNCARDDESIGNER_DATA);
		if(!spddFile.exists()) {
			spddFile.createNewFile();
		}
		
		Document document = SwitchObjAndXml.getDocFromProjectModel(projectModel);
		// 写入项目文件信息
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(ppdFile), "UTF-8");
		out.write(document.asXML());
		out.close();
		
		// 写入表格初始化信息
		document = SwitchObjAndXml.getInitTableDataDocFromProjectModel(projectModel);
		out = new OutputStreamWriter(new FileOutputStream(spddFile), "UTF-8");
		out.write(document.asXML());
		out.close();
		
		// 给文件模型对应的CommandStack设定保存点
		CommandStack commandStack = ProjectDefaultViewPart.getCommandStackFromProjectModel(projectModel);
		if(commandStack != null) {
			commandStack.markSaveLocation();
		}
	}
	
}
