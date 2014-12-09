package cn.sunline.suncard.sde.bs.ui.plugin.tools;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import cn.sunline.suncard.sde.bs.biz.BsWidgetBiz;
import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsWidget;
import cn.sunline.suncard.sde.bs.entity.BsWidgetId;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.system.Context;

public class ControlXMLAnalysis {
	private BsWidgetBiz bsWidgetBiz = new BsWidgetBiz();
	private List<BsWidget> list;
	private List<String> widgetVersionList;
	private XMLAnalysis xMLAnalysis;
	private Document document;
	
	public static Log logger = LogManager.getLogger(ControlXMLAnalysis.class.getName());
	
	long bankorgId = (Long) Context.getSessionMap().get(Constants.BANKORG_ID);
	String pcId = (String) Context.getSessionMap().get(Constants.PC_ID);
	
	public ControlXMLAnalysis(byte[] bytes) throws UnsupportedEncodingException, DocumentException {
		logger.info("public ControlXMLAnalysis(byte[] bytes) ....");
		
		xMLAnalysis = new XMLAnalysis(bytes);
		document = xMLAnalysis.getDocument();
	}
	
	public void initControlList() {
		logger.info("public void initControlList() ....");
		
		List<Element> controlList = document.selectNodes("/SuncardControls/control");
		
		list = new ArrayList();
		widgetVersionList = new ArrayList();
		
		for(Element e : controlList) {
			DefaultElement element = (DefaultElement) e.selectObject("id");
			BsWidgetId bsWidgetId = new BsWidgetId(bankorgId, pcId,
					element.getTextTrim());
			
			logger.info("BsWidgetId:" + bsWidgetId);
			
			BsWidget bsWidget = new BsWidget(bsWidgetId);	
			
			element = (DefaultElement) e.selectObject("name");
			bsWidget.setWidgetName(element.getTextTrim());
			logger.info("bsWidget.setWidgetName:" + element.getTextTrim());
			
			element = (DefaultElement) e.selectObject("type");
			bsWidget.setWidgetType(element.getTextTrim());
			logger.info("bsWidget.setWidgetType:" + element.getTextTrim());
			
			element = (DefaultElement) e.selectObject("parentControlId");
			bsWidget.setParWidgetId(element.getTextTrim());	
			logger.info("bsWidget.setParWidgetId:" + element.getTextTrim());

			
			element = (DefaultElement) e.selectObject("pluginId");			
			bsWidget.setPluginId(element.getTextTrim());
			logger.info("bsWidget.setPluginId:" + element.getTextTrim());
			
			// 存储控件版本信息到列表。
			element = (DefaultElement) e.selectObject("rightVersion");			
			widgetVersionList.add(element.getTextTrim());
			
//			bsWidget.setModiDate(new Timestamp(System.currentTimeMillis()));
//			bsWidget.setModiUser((String) Context.getSessionMap().get(Constants.CURRENT_USER));
			
			list.add(bsWidget);

		}
	}

	public List<BsWidget> getList() {
		return list;
	}

	public List<String> getWidgetVersionList() {
		return widgetVersionList;
	}
	
//	public void addToDb() {
//		for(BsWidget bsWidget : list) {
//			bsWidgetBiz.add(bsWidget);
//		}
//	}
	
}
