package cn.sunline.suncard.sde.bs.ui.plugin.patch;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import cn.sunline.suncard.sde.bs.ui.plugin.JarXMLAnalysis;

public class PatchXMLAnalysis {
	private JarXMLAnalysis jarXMLAnalysis;
	private Document document;
	
	public PatchXMLAnalysis(String filePath) throws DocumentException {
		jarXMLAnalysis = new JarXMLAnalysis(filePath);
		document = jarXMLAnalysis.getDocument();
	}
	
	public PatchXMLAnalysis(byte[] bytes) throws UnsupportedEncodingException, DocumentException {
		jarXMLAnalysis = new JarXMLAnalysis(bytes);
		document = jarXMLAnalysis.getDocument();
	}
	
	public JarXMLAnalysis getJarXMLAnalysis() {
		return jarXMLAnalysis;
	}

	//获得补丁的插件ID
	public String getParentPluginId() {
		if(document == null) {
			return null;
		}
		
		List<Element> parentPluginList = document.selectNodes("/Suncard/plugin/parentPlugin");
		if(parentPluginList == null) {
			return null;
		}
		
		Iterator<Element> iterator = parentPluginList.iterator();
		if(iterator.hasNext()) {
			Element element = iterator.next();
			return element.attributeValue("id");
		}else {
			return null;
		}
	}
	
	// 获得合适的父插件版本号
	public List<String> getSuitableVersions() {
		if(document == null) {
			return null;
		}
		
		List<Element> parentPluginList = document.selectNodes("/Suncard/plugin/parentPlugin/" +
				"suitableVersions/suitableVersion");
		
		if(parentPluginList == null) {
			return null;
		}
		
		List<String> list = new ArrayList<String>();
		Iterator<Element> iterator = parentPluginList.iterator();
		while(iterator.hasNext()) {
			Element element = iterator.next();
			list.add(element.getTextTrim());
		}

		return list;
	}
	
	// 获得安装后的父插件版本。
	public String getInstalledVersion() {
		if(document == null) {
			return null;
		}
		
		List<Element> parentPluginList = document.selectNodes("/Suncard/plugin/parentPlugin/" +
				"nextVersion");
		
		List<String> list = new ArrayList<String>();
		Iterator<Element> iterator = parentPluginList.iterator();
		if(iterator.hasNext()) {
			Element element = iterator.next();
			return element.getTextTrim();
		}else {
			return null;
		}
	}

	public Document getDocument() {
		return document;
	}
	
	
}
