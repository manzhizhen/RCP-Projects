/*
 * 文件名：     XMLAnalysis.java
 * 版权：           Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：           XML文件解析类
 * 修改人：     易振强
 * 修改时间：2011-9-21
 * 修改内容：创建
 */
package cn.sunline.suncard.sde.bs.ui.plugin.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.ui.plugin.PluginAdd;

/**
 * XML文件解析类
 * 负责对XML文件或XML文件流进行解析
 * @author 易振强
 * @version 1.0, 2011-9-21
 * @see JarMD5Checkout
 * @since 1.0
 */
public class XMLAnalysis {
	/**
	 * 插件压缩文件路径
	 */
	private String filePath = null;
	
	/**
	 * 用于解析XML文件的文档对象
	 */
	private Document document = null;

	/**
	 * 添加日志
	 */
	public static Log logger = LogManager.getLogger(XMLAnalysis.class.getName());
	
	/**
	 * 获得文件来初始化Document对象。
	 * @param filePath String 插件压缩文件路径
	 * @throws DocumentException 
	 */
	public XMLAnalysis(String filePath) throws DocumentException {
		logger.info("public XMLAnalysis(String filePath) ....");
		
		this.filePath = filePath;
		initDocument();
	}

	/**
	 * 通过字节流来初始化Document对象。
	 * @param bytes byte[] 解析XML文件得到的字节流
	 * @throws UnsupportedEncodingException
	 * @throws DocumentException 
	 */
	public XMLAnalysis(byte[] bytes) throws UnsupportedEncodingException, DocumentException {
		logger.info("public XMLAnalysis(byte[] bytes) ....");
		logger.info(bytes.length);
		
		String str = new String(bytes, 0, SDEPluginZip.BUFFER_SIZE, "UTF-8");

		String temp = str.trim();
		
		string2Document(temp.substring(temp.indexOf("<")));
	}
	
	/**
	 * 设置文件路径
	 * @param  filePath String 文件路径
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * 获得文件路径
	 * @param  filePath String 文件路径
	 */
	public String getFilePath() {
		return filePath;
	}
	
	/**
	 * doc2XmlFile 将Document对象保存为一个xml文件到本地 
	 * @return true:保存成功 flase:失败
	 * @param filename String 保存的文件名
	 * @param document Document 需要保存的document对象
	 */
	public static boolean doc2XmlFile(Document document, String filename) {
		boolean flag = true;
		try {
			/* 将document中的内容写入文件中 */
			// 默认为UTF-8格式，指定为"UTF-8"
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			XMLWriter writer = new XMLWriter(
					new FileWriter(new File(filename)), format);
			writer.write(document);
			writer.close();
		} catch (Exception ex) {
			flag = false;
			ex.printStackTrace();
		}
		return flag;
	}

	/**
	 * 载入一个XML文档
	 * @return 成功返回Document对象，失败返回null
	 * @throws DocumentException 
	 */
	public void initDocument() throws DocumentException {
		logger.info("public void initDocument() ....");

		SAXReader saxReader = new SAXReader();
		document = saxReader.read(new File(filePath));
		


	}

	/**
	 * string2Document 将字符串转为Document
	 * @param string String 符合XML格式的字符串
	 * @throws DocumentException 
	 */
	public void string2Document(String string) throws DocumentException {	
		logger.info("public void string2Document(String string) ....");
		logger.info(string);
		
		document = DocumentHelper.parseText(string);

	}

	public Document getDocument() {
		return document;
	}

	public static void main(String[] args) throws IOException {
//		XMLAnalysis x = new XMLAnalysis("d:/1.xml");
//		List<Element> pluginList = x.getDocument().selectNodes("/Suncard/plugin");
//		
//		Iterator<Element> iterator = pluginList.iterator();
//		if(iterator.hasNext()) {
//			Element element = iterator.next();
//			System.out.println(element.attribute("id").getValue()); ;			
//		}
//		
//		
//		byte[] bytes = new byte[0x000fffff];
//		BufferedInputStream b = new BufferedInputStream(new FileInputStream("d:/1.xml"));
//		int count = b.read(bytes, 0, 0x000fffff);
//		b.close();
//		XMLAnalysis x1 = new XMLAnalysis(bytes);
//		pluginList = x1.getDocument().selectNodes("/Suncard/plugin");
//		
//		iterator = pluginList.iterator();
//		if(iterator.hasNext()) {
//			Element element = iterator.next();
//			System.out.println(element.attribute("id").getValue()); ;			
//		}
		
	}
	
}
