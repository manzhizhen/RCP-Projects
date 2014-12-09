/*
 * 文件名：ParseHibernateConfig.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：hibernate配置文件操作类
 * 修改人： tpf
 * 修改时间：2011-10-17
 * 修改内容：新增
 * 
*/
package cn.sunline.suncard.sde.bs.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * hibernate配置文件操作类
 * 提供对hibernate配置文件及mapping文件操作
 * @author    tpf
 * @version   1.0, 2011-10-17
 * @see       
 * @since     1.0
 */
public class ParseHibernateConfig {

	//以xml方式读取hibernate配置文件并解析
	/*public static Document loadModel(String filePath) {
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(new FileInputStream(filePath));
			//getRootElements(doc);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("文件未找到！"+e.getMessage());
			//e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	private static List<Element> getRootElements(Document doc) {
		Element root = doc.getRootElement();
		List<Element> elements = root.elements();
		return elements;
	}
	
	public static Document getDocument(String hibernateCfgPath, String mappingPath) {
		System.out.println("hibernateCfgPath："+hibernateCfgPath+"==="+"mappingPath："+mappingPath);
		Document docHibernate = loadModel(hibernateCfgPath);
		//Document docMapping = loadModel("D:/config/mapping/base.xml");
		List<Element> hibernates = getRootElements(docHibernate);
		//List<Element> mappings = getRootElements(docMapping);
		for (Element e : hibernates) {
			if("session-factory".equals(e.getName())) {
				BufferedReader br = null;
				BufferedWriter bw = null;
				try {
					br = new BufferedReader(new FileReader(mappingPath));
				
					String str = null;
					while((str = br.readLine()) != null) {
						if(str.indexOf("<") == -1) {
							continue;
						}
						str = str.substring(str.indexOf("<")+1, str.indexOf("/>")-1);
						DOMElement dome = new DOMElement(str+"\r\n");
						e.add(dome);
					}
					if(br != null) {
						br.close();
					}
					String hibernateStr = docHibernate.asXML();
					File fileTemp = new File(new File(hibernateCfgPath).getParent()+"/hibernate.cfg.temp.xml");
					fileTemp.createNewFile();
					bw = new BufferedWriter(new FileWriter(fileTemp));
					bw.write(hibernateStr);
					bw.flush();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					try {
						if(br != null) {
							br.close();
						}
						if(bw != null) {
							bw.close();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		
		return docHibernate;
	}*/
	
	/**
	 * 解析hibernate配置文件与映射文件mapping，并合并为文件hibernate.cfg.temp.xml;
	 * @param hibernateCfgPath
	 * @param mappingPath
	 * @return
	 */
	public static String parseHibernateCfg(String hibernateCfgPath, String mappingPath) {
		StringBuffer sb = new StringBuffer();
		BufferedReader hibernateReader = null;
		try {
			hibernateReader = new BufferedReader(new FileReader(hibernateCfgPath));
			//BufferedReader mappingReader = new 
			String str = null;
			while((str = hibernateReader.readLine()) != null) {
				if(str.contains("</session-factory>")) {
					sb.append(readCfgFile(mappingPath));
				}
				sb.append(str);
			}
			String hibernateCfgTempPath = SystemParameters.getUserDir() +"/"+ SystemParameters.getHibernateTempXmlPath();
			writeFile(hibernateCfgTempPath,sb.toString());
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(hibernateReader != null) {
				try {
					hibernateReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * 读取mapping映射文件并返回读取的内容
	 * @param mappingPath
	 * @return
	 */
	public static String readCfgFile(String mappingPath) {
		StringBuffer sb = new StringBuffer();
		File dirFile = new File(mappingPath);
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
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				String str = null;
				while((str=br.readLine()) != null) {
					str = str+"\n";
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
	 * 将组合好的配置文件写入文件hibernate.cfg.temp.xml
	 * @param hibernateCfgTempPath
	 * @param hibernateStr
	 */
	public static void writeFile(String hibernateCfgTempPath, String hibernateStr) {
		File fileTemp = new File(hibernateCfgTempPath);
		BufferedWriter bw = null;
		try {
			if(!fileTemp.exists()) {
				File parentFile = new File(fileTemp.getParent());
				if(!parentFile.exists()) {
					parentFile.mkdirs();
				}
				fileTemp.createNewFile();
			}
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileTemp),"UTF-8"));
			bw.write(hibernateStr);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		//System.out.println(new ParseHibernateConfig().getDocument("D:/config/hibernate.cfg.xml", "D:/config/mapping/base.xml").asXML());
		//System.out.println(new File("D:/config/hibernate.cfg.xml").getParent());
		//System.out.println(readMappingCfg("D:/config/mapping"));
		//loadModel("D:/hibernate.cfg.xml");
		System.out.println(ParseHibernateConfig.parseHibernateCfg("D:/config/hibernate.cfg.xml", "D:/config/mapping"));
	}
}
