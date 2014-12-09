/*
 * 文件名：BizDictManager.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：业务字典管理类
 * 修改人：tpf
 * 修改时间：2011-10-26
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.dict;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.sunline.suncard.sde.bs.util.ParseHibernateConfig;
import cn.sunline.suncard.sde.bs.util.SystemParameters;

/**
 * 业务字典配置文件操作类
 * 提供对业务字典配置文件的操作
 * @author    tpf
 * @version   1.0, 2011-10-26
 * @see       
 * @since     1.0
 */
public class BizDictManager {

	private static Document doc = null;
	private static Map<String, DictType> DictMap;
	
	//业务字典文件关键字符串
	private static String dictXmlHead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	private static String dictXmlStart = "<dicts locale=\"zh_CN\">";
	private static String dictXmlEnd = "</dicts>";
	
	public static void initAndParseDictXmlFile() throws DocumentException {
		//业务字典配置文件路径
		String dictConfigXml = SystemParameters.getUserDir() +"/"+ SystemParameters.getDictConfigXml();
		//业务字典临时文件路径
		String dictConfigXmlTemp = SystemParameters.getUserDir() +"/"+ SystemParameters.getDictConfigXmlTemp();
		
		//合并所有业务字典文件为一个临时文件
		parseDictXmlFile(dictConfigXml, dictConfigXmlTemp);
		
		//初始化业务字典文件为doc
		initDocument(dictConfigXmlTemp);
		
		//缓存业务字典文件解析出来的map
		DictMap = getDictTypes(doc);
	}
	
	/**
	 * 
	 * @param bizDictCfgPath
	 * @param mappingPath
	 * @return 返回业务字典所有文件内容
	 */
	public static String parseDictXmlFile(String dictConfigPath, String dictCfgTempPath) {
		StringBuffer sb = new StringBuffer();
		sb.append(dictXmlHead);
		sb.append("\n");
		sb.append(dictXmlStart);
		sb.append("\n");
		sb.append(readDictCfgFile(dictConfigPath));
		sb.append(dictXmlEnd);
		ParseHibernateConfig.writeFile(dictCfgTempPath,sb.toString());
		return sb.toString();
	}
	
	/**
	 * 读取业务字典文件并返回读取后处理过的内容
	 * @param configPath
	 * @return
	 */
	public static String readDictCfgFile(String configPath) {
		StringBuffer sb = new StringBuffer();
		File dirFile = new File(configPath);
		File[] files = null;
		if(dirFile.isDirectory()) {
			files = dirFile.listFiles();
		} else {
			files = new File[]{dirFile};
		}
		BufferedReader br = null;
		try {
			for (File file : files) {
				//System.out.println(file.getPath());
				if(file.isDirectory()) {
					continue;
				}
				InputStreamReader isr = new InputStreamReader(new FileInputStream(file),"UTF-8");
				br = new BufferedReader(isr);
				String str = null;
				while((str=br.readLine()) != null) {
					str = str+"\n";
					if(str.contains(dictXmlHead) || str.contains(dictXmlStart) || str.contains(dictXmlEnd)) {
						continue;
					}
					sb.append(str);
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
	
	
	/**
	 * 解释业务字典XML文件，读取文件中的所有业务字典
	 * 
	 * @param dictFilePath
	 * @return doc
	 * @throws DocumentException 
	 */
	private static Document initDocument(String dictFilePath) throws DocumentException {
		SAXReader saxReader = new SAXReader();
		doc = saxReader.read(new File(dictFilePath));
		return doc;
	}
	
	/**
	 * 解析业务字典文件得到业务字典类型的map
	 * @param doc
	 * @return
	 */
	private static Map<String, DictType> getDictTypes(Document doc) {
		Element root = doc.getRootElement();
		List<Element> dictTypeNodes = root.elements();
		Map<String, DictType> dictTypeMap = new LinkedHashMap<String, DictType>();
		
		for (Element dictTypeNode : dictTypeNodes) {
			DictType dictType = new DictType();
			List<DictEntry> dictEntries = new ArrayList<DictEntry>();
			dictType.setDictTypeId(dictTypeNode.attributeValue("dictTypeId"));
			dictType.setDictTypeName(dictTypeNode.attributeValue("dictTypeName"));
			List<Element> dictEntryNodes = dictTypeNode.elements();
			for (Element dictEntryNode : dictEntryNodes) {
				DictEntry dictEntry = new DictEntry();
				dictEntry.setDictId(dictEntryNode.attributeValue("dictId"));
				dictEntry.setDictName(dictEntryNode.attributeValue("dictName"));
				dictEntries.add(dictEntry);
			}
			dictType.setDictEntries(dictEntries);
			dictTypeMap.put(dictType.getDictTypeId(), dictType);
		}
		return dictTypeMap;
	}
	
	/**
	 * 通过业务字典dictTypeId获取其下的业务字典项
	 * 返回值为value
	 * @param dictTypeId 业务字典id
	 * @return dictMap 业务字典map
	 * @throws DocumentException
	 */
	public static Map<String, String> getDictValue(String dictTypeId) throws DocumentException {
		Map<String, DictType> dictTypeMap = DictMap;
		DictType dictType = dictTypeMap.get(dictTypeId);
		Map<String, String> dictMap = new LinkedHashMap<String, String>();
		if(dictType != null) {
			for (DictEntry entry : dictType.getDictEntries()) {
				dictMap.put(entry.getDictId(), entry.getDictName());
			}
		}
		return dictMap;
	}
	
	/**
	 * 通过业务字典dictTypeId获取其下的业务字典项
	 * 返回值为key-value
	 * @param dictTypeId 业务字典id
	 * @return dictMap 业务字典map
	 * @throws DocumentException
	 */
	public static Map<String, String> getDictIdValue(String dictTypeId) throws DocumentException {
		Map<String, DictType> dictTypeMap = DictMap;
		DictType dictType = dictTypeMap.get(dictTypeId);
		Map<String, String> dictMap = new LinkedHashMap<String, String>();
		if(dictType != null) {
			for (DictEntry entry : dictType.getDictEntries()) {
				if("".equals(entry.getDictId()) && "".equals(entry.getDictName())) {
					dictMap.put(entry.getDictId(), entry.getDictId() + entry.getDictName());
					continue;
				}
				dictMap.put(entry.getDictId(), entry.getDictId() + "-" + entry.getDictName());
			}
		}
		return dictMap;
	}
	
	public static void main(String[] args) throws DocumentException {
		//System.out.println(getDictValue("FreqDay"));
		System.out.println(initDocument("D:/dictConfig_zh_CN.xml").asXML());
	}
}
