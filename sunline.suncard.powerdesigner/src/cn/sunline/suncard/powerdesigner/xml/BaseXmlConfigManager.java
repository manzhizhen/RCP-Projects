/* 文件名：     BaseXmlConfigManager.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-28
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.xml;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 对简单的XML配置文件进行维护
 * @author  Manzhizhen
 * @version 1.0, 2012-12-28
 * @see 
 * @since 1.0
 */
public class BaseXmlConfigManager {
	public static Log logger = LogManager.getLogger(BaseXmlConfigManager.class);
	
	/**
	 * 根据配置文件路径，来返回相应的Document对象
	 * @param filePath
	 * @return
	 * @throws DocumentException 
	 */
	public static Document getDocument(String filePath) throws DocumentException {
		if(filePath == null || filePath.trim().isEmpty()) {
			logger.debug("配置文件路径为空，返回Document失败！");
			return null;
		}
		
		File file = new File(filePath);
		
		if(!file.isFile()) {
			logger.error("找不到对应的配置文件：" + file.getAbsolutePath());
			return null;
		}
		
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		
		return document;
	}
	
	/**
	 * 将新的内容写入到配置文件中
	 * @param docuement
	 * @param filePath
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void writeDocument(Document document, String filePath) throws UnsupportedEncodingException, IOException {
		if(document == null || filePath == null || filePath.trim().isEmpty()) {
			logger.debug("传入的Document为空，写入配置文件失败！");
		}
		
		File file = new File(filePath);
		if(!file.isFile()) {
			logger.error("找不到对应的配置文件,写入配置文件失败：" + file.getAbsolutePath());
			return ;
		}
		
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(file));
			bos.write(document.asXML().getBytes(SystemConstants.FILE_CODE_UTF));
		} catch (UnsupportedEncodingException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if(bos != null) {
				bos.close();
			}
		}
	}
}
