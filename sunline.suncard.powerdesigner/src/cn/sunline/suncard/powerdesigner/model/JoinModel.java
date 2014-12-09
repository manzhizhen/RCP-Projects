/* 文件名：     KeyToKeyModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-10-18
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 保存了两个表之间主外键之间的信息
 * @author  Manzhizhen
 * @version 1.0, 2012-10-18
 * @see 
 * @since 1.0
 */
public class JoinModel implements DataObjectInterface{
	private String parentTableName = "";
	private String childTableName = "";
	private List<KeyToKeyModel> keyList = new ArrayList<KeyToKeyModel>();
	
	private static String elementName = "join";
	private Log logger = LogManager.getLogger(JoinModel.class
			.getName());

	public String getParentTableName() {
		return parentTableName;
	}

	public void setParentTableName(String parentTableName) {
		this.parentTableName = parentTableName;
	}

	public String getChildTableName() {
		return childTableName;
	}

	public void setChildTableName(String childTableName) {
		this.childTableName = childTableName;
	}

	public List<KeyToKeyModel> getKeyList() {
		return keyList;
	}

	public void setKeyList(List<KeyToKeyModel> keyList) {
		this.keyList = keyList;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element joinModelElement = parent.addElement(elementName);
		
		joinModelElement.addAttribute("parentTableName", parentTableName == null ? 
				"" : parentTableName);
		joinModelElement.addAttribute("childTableName", childTableName == null ? 
				"" : childTableName);
		
		for(KeyToKeyModel keyModel : keyList) {
			keyModel.getElementFromObject(joinModelElement);
		}
		
		return joinModelElement;
	}

	@Override
	public JoinModel getObjectFromElement(Element element, Object...obj) {
		if(element == null ) {
			logger.warn("JoinModel的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("JoinModel的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		setParentTableName(element.attributeValue("parentTableName").trim());
		setChildTableName(element.attributeValue("childTableName").trim());
		
		keyList.clear();
		List<Element> keyElementList = element.elements(KeyToKeyModel.getElementName());
		for(Element keyElement : keyElementList) {
			keyList.add(new KeyToKeyModel().getObjectFromElement(keyElement));
		}
		
		return this;
	}

	public static String getElementName() {
		return elementName;
	}
	

	@Override
	public JoinModel clone() throws CloneNotSupportedException {
		JoinModel newJoinModel = new JoinModel();
		newJoinModel.setParentTableName(parentTableName);
		newJoinModel.setChildTableName(childTableName);
		
		List<KeyToKeyModel> newKeyList = new ArrayList<KeyToKeyModel>();
		for(KeyToKeyModel keyModel : keyList) {
			newKeyList.add(keyModel.clone());
		}
		newJoinModel.setKeyList(newKeyList);
		
		return newJoinModel;
	}
}

