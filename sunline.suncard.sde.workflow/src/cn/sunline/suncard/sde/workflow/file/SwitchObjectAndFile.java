/* 文件名：     SwitchObjectAndFile.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	对象和文件互转类
 * 修改人：     易振强
 * 修改时间：2012-3-28
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.file;

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

import cn.sunline.suncard.sde.workflow.common.DmConstants;
import cn.sunline.suncard.sde.workflow.gef.xml.GefFigureSwitchXml;
import cn.sunline.suncard.sde.workflow.model.ActionTreeNode;
import cn.sunline.suncard.sde.workflow.model.WorkFlowTreeNode;

/**
 * 对象和文件互转类
 * @author  易振强
 * @version 1.0, 2012-3-28
 * @see 
 * @since 1.0
 */
public class SwitchObjectAndFile {
	
	/**
	 * 将工作流树节点对象保存到指定目录下
	 * 文件名为该对象的id
	 */
	public static void SaveWorkFlowToFile(WorkFlowTreeNode node) throws IOException {
		File file = new File(DmConstants.WORK_FLOW_DATA_FILE_PATH);
		if(!file.exists() || !file.isDirectory()) {
			file.mkdirs();
		}
		
//		String filePath = new File(file.getAbsolutePath(), node.getId() + 
//				DmConstants.WORK_FLOW_DATA_FILE_EXTNAME).getAbsolutePath();
//		WFObjectSerializeUtil.saveObjToFile(node, filePath);
		
		String filePath = new File(file.getAbsolutePath(), node.getId() + 
				".xml").getAbsolutePath();
		
		Document document = GefFigureSwitchXml.getDocFromWorkFlowTreeModel(node);
		
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8");
		out.write(document.asXML());
		out.close();
	}
	
	/**
	 * 将Action树节点对象保存到指定目录下
	 * 文件名为该对象的id
	 */
	public static void SaveActionToFile(ActionTreeNode node) throws IOException {
		File file = new File(DmConstants.ACTION_DATA_FILE_PATH);
		if(!file.exists() || !file.isDirectory()) {
			file.mkdirs();
		}
		
		String filePath = new File(file.getAbsolutePath(), node.getName() + 
				DmConstants.ACTION_DATA_FILE_EXTNAME).getAbsolutePath();
//		WFObjectSerializeUtil.saveObjToFile(node, filePath);
		String xml = SwitchActionAndXml.getXmlFromAction(node);
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8");
		writer.write(xml);
		writer.close();
		
	}
	
	/**
	 * 将指定目录下的xml文件解析为工作流对象List返回
	 * @throws DocumentException 
	 */
	public static List<WorkFlowTreeNode> getWorkFlowListFromFile() throws IOException, ClassNotFoundException, DocumentException {
		File file = new File(DmConstants.WORK_FLOW_DATA_FILE_PATH);
		if(!file.exists() || !file.isDirectory()) {
			file.mkdirs();
			return null;
		}
		
		List<WorkFlowTreeNode> nodeList = new ArrayList<WorkFlowTreeNode>();
		File[] files = file.listFiles();
		for(File nodeFile : files) {
			if(nodeFile.getName().endsWith(DmConstants.WORK_FLOW_DATA_FILE_EXTNAME)) {
//				nodeList.add((WorkFlowTreeNode) WFObjectSerializeUtil.readObjFromFile(nodeFile.getAbsolutePath()));
				nodeList.add(GefFigureSwitchXml.getWorkFlowTreeNodeFromXml(nodeFile.getAbsolutePath()));
			}
		}
		
		return nodeList;
	}
	
	/**
	 * 将指定目录下的xml文件解析为Action对象List返回
	 * @throws DocumentException 
	 */
	public static List<ActionTreeNode> getActionListFromFile() throws IOException, ClassNotFoundException, DocumentException {
		File file = new File(DmConstants.ACTION_DATA_FILE_PATH);
		if(!file.exists() || !file.isDirectory()) {
			file.mkdirs();
			return null;
		}
		
		List<ActionTreeNode> nodeList = new ArrayList<ActionTreeNode>();
		File[] files = file.listFiles();
		for(File nodeFile : files) {
			if(nodeFile.getName().endsWith(DmConstants.ACTION_DATA_FILE_EXTNAME)) {
//				nodeList.add((ActionTreeNode) WFObjectSerializeUtil.readObjFromFile(nodeFile.getAbsolutePath()));
				nodeList.add(SwitchActionAndXml.getActionFromXml(nodeFile.getAbsolutePath()));
			}
		}
		
		return nodeList;
	}
	
	
	/**
	 * 删除文件或文件夹
	 * 
	 * @param filePath
	 */
	public static void deleteFile(String filePath) {
		
		File file = new File(filePath);
		if (!file.exists()) {
			return;
		}

		if (file.isFile()) {
			file.delete();
			return;
		}

		if (!filePath.endsWith(File.separator)) {
			filePath = filePath + File.separator;
		}

		File[] files = file.listFiles();
		for (File childFile : files) {
			if (childFile.isFile()) {
				childFile.delete();
			} else {
				deleteFile(childFile.getAbsolutePath());
			}
		}

		file.delete();
	}
	
}
