/*
 * 文件名：XMLUtil.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：XML工具类
 * 修改人： xcc
 * 修改时间：2011-11-9
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.bs.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.DOMReader;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

/**
 * XML工具类
 * 提供XML文件操作的基本操作
 * @author    xcc
 * @version   1.0, 2011-11-9
 * @since     1.0
 */
public class XMLUtil {
	
	/**
	 * 基于SAX从字符串解析XML文件，如果只是读取请选用此方法
	 * @param xmlStr XML文件字符串
	 * @return XML基于DOM4J的文档结构
	 * @throws DocumentException 
	 * @throws UnsupportedEncodingException 
	 */
	public static Document getDocumentBsSax(String xmlStr) throws DocumentException, UnsupportedEncodingException{
		SAXReader reader = new SAXReader();
		return reader.read(new ByteArrayInputStream(xmlStr.getBytes("UTF-8")));
	}
	
	/**
	 * 基于DOM从字符串解析XML文件，如果修改XML请选用此方法
	 * @param xmlStr XML文件字符串
	 * @return XML基于DOM4J的文档结构
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public static Document getDocumentBsDom(String xmlStr) throws ParserConfigurationException, SAXException, IOException{
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder builder = factory.newDocumentBuilder();
		org.w3c.dom.Document document = builder.parse(new ByteArrayInputStream(xmlStr.getBytes()));
		
		DOMReader reader = new DOMReader();
		return reader.read(document);
	}
	
}
