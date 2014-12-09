
package cn.sunline.suncard.sde.bs.ui.plugin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import cn.sunline.suncard.sde.bs.ui.plugin.tools.XMLAnalysis;

public class JarXMLAnalysis {
	private XMLAnalysis xMLAnalysis;
	private Document document;
	
	public JarXMLAnalysis(String filePath) throws DocumentException {
		xMLAnalysis = new XMLAnalysis(filePath);
		xMLAnalysis.initDocument();
		document = xMLAnalysis.getDocument();
	}
	
	public JarXMLAnalysis(byte[] bytes) throws UnsupportedEncodingException, DocumentException {
		xMLAnalysis = new XMLAnalysis(bytes);
		document = xMLAnalysis.getDocument();
	}
	
	/**
	 * 从XML文件中获得插件ID。
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public String getPluginId() {
		if(document == null) {
			return null;
		}
		
		List<Element> pluginList = document.selectNodes("/Suncard/plugin");
		if(pluginList == null) {
			return null;
		}
		
		Iterator<Element> iterator = pluginList.iterator();
		if(iterator.hasNext()) {
			Element element = iterator.next();
			return element.attribute("id").getValue();			
		}

		return null;
	}
	
	/**
	 * 从XML文件中获得插件类型。
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public String getPluginType() {
		if(document == null) {
			return null;
		}
		
		List<Element> pluginList = document.selectNodes("/Suncard/plugin");
		if(pluginList == null) {
			return null;
		}
		
		Iterator<Element> iterator = pluginList.iterator();
		if(iterator.hasNext()) {
			Element element = iterator.next();
			return element.attribute("pluginType").getValue();			
		}

		return null;
	}
	
	/**
	 * 从XML文件中获得插件名称。
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public String getPluginName() {
		if(document == null) {
			return null;
		}
		
		List<Element> pluginList = document.selectNodes("/Suncard/plugin/name");
		if(pluginList == null) {
			return null;
		}
		
		Iterator<Element> iterator = pluginList.iterator();
		if(iterator.hasNext()) {
			Element element = iterator.next();
			return element.getText();			
		}

		return null;
	}
	
	/**
	 * 从XML文件中获得插件版本。
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public String getPluginVersion() {
		if(document == null) {
			return null;
		}
		
		List<Element> pluginList = document.selectNodes("/Suncard/plugin/version");
		if(pluginList == null) {
			return null;
		}
		
		Iterator<Element> iterator = pluginList.iterator();
		if(iterator.hasNext()) {
			Element element = iterator.next();
			return element.getText();			
		}

		return null;
	}
	
	/**
	 * 从XML文件中获得父插件ID。
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public String getParentPluginId() {
		if(document == null) {
			return null;
		}
		
		List<Element> parentPlugininList = document.selectNodes("/Suncard/plugin/parentPlugin");
		if(parentPlugininList == null) {
			return null;
		}
		
		Iterator<Element> iterator = parentPlugininList.iterator();
		if(iterator.hasNext()) {
			Element element = iterator.next();
			return element.attributeValue("id");			
		}

		return null;
	}

	/**
	 * 从XML文件中获得补丁需要更新的插件适用版本。
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public List<String> getSuitableVersion() {
		if(document == null) {
			return null;
		}
		
		List<Element> parentPlugininList = document.selectNodes("/Suncard/plugin/parentPlugin/suitableVersions/suitableVersion");
		if(parentPlugininList == null) {
			return null;
		}
		List<String> suitableList = new ArrayList<String>();
		Iterator<Element> iterator = parentPlugininList.iterator();
		while(iterator.hasNext()) {
			Element element = iterator.next();
			suitableList.add(element.getText());		
		}

		return suitableList;
	}
	
	/**
	 * 从XML文件中获得MD5码。
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public String getMD5FromXML() {
		if(document == null) {
			return null;
		}
		
		List<Element> pluginMD5List = document.selectNodes("/Suncard/plugin/MD5");
		if(pluginMD5List == null) {
			return null;
		}
		
		Iterator<Element> iterator = pluginMD5List.iterator();
		if(iterator.hasNext()) {
			Element element = iterator.next();
			return element.getText();
		}else {
			return null;
		}
	}
	

	public Document getDocument() {
		return document;
	}

	public static void main(String[] args) throws IOException {
//
//		BufferedInputStream bufferedInputStream = new BufferedInputStream(
//				new FileInputStream("d:\\123\\Test.xml"));
//		
//		byte data[] = new byte[2048];	
//		int count;
//		while ((count = bufferedInputStream.read(data, 0, 2048)) != -1) {
//		}
//		
//		String str = new String(data).trim();
////		JarXMLAnalysis jarXMLAnalysis = new JarXMLAnalysis("d:\\Test.xml");
////		System.out.println(jarXMLAnalysis.getSuitableVersion().get(0));
////		
//		
//		System.out.println(str);

	}

}
