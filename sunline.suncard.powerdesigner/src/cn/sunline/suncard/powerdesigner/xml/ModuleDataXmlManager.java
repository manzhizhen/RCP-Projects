/* 文件名：     ModuleDataXmlManager.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-28
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.xml;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import cn.sunline.suncard.powerdesigner.command.UpdateModuleLabelCommand;
import cn.sunline.suncard.powerdesigner.model.xml.ModuleXmlModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 模块数据配置文件管理（ModuleDataConfig.xml）
 * @author  Manzhizhen
 * @version 1.0, 2012-12-28
 * @see 
 * @since 1.0
 */
public class ModuleDataXmlManager {
	private static Log logger = LogManager.getLogger(ModuleDataXmlManager.class.getName());
	
	/**
	 * 从配置文件中获取所有的ModuleXmlModel
	 * @return
	 * @throws DocumentException
	 */
	public static List<ModuleXmlModel> getAllModuleXmlModel() throws DocumentException {
		Document document = BaseXmlConfigManager.getDocument(SystemConstants.MODULE_DATA_CONFIG_FILE);
	
		if(document == null) {
			return null;
		}
		
		List<ModuleXmlModel> moduleXmlModels = new ArrayList<ModuleXmlModel>();
		
		Element rootElement = document.getRootElement();
		if(rootElement == null) {
			return moduleXmlModels;
		}
		
		List<Element> moduleXmlElementList = rootElement.elements(ModuleXmlModel.getElementName());
		for(Element moduleElement : moduleXmlElementList) {
			moduleXmlModels.add(new ModuleXmlModel().getObjectFromElement(moduleElement));
		}
		
		return moduleXmlModels;
	}
	
	/**
	 * 将新的模块标签内容写入配置文件中
	 * @param newModuleXmlModelList
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void setModuleXmlModel(List<ModuleXmlModel> newModuleXmlModelList) throws UnsupportedEncodingException, IOException {
		if(newModuleXmlModelList == null) {
			logger.debug("传入的List<ModuleXmlModel>为空，无法写入配置文件！");
			return ;
		}
		
		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement(DmConstants.MODULE_XML_ROOT_ELEMENT_NAME);
		for(ModuleXmlModel moduleXmlModel : newModuleXmlModelList) {
			moduleXmlModel.getElementFromObject(rootElement);
		}
		
		BaseXmlConfigManager.writeDocument(document, SystemConstants.MODULE_DATA_CONFIG_FILE);
	}
}
