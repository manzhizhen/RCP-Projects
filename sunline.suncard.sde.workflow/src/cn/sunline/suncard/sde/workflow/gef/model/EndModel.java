/* 文件名：     EndModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.model;

import cn.sunline.suncard.sde.workflow.gef.model.props.PropertySourceAdapter;
import cn.sunline.suncard.sde.workflow.model.EndNode;
import cn.sunline.suncard.sde.workflow.model.DataObjectInterface;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 结束节点
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 */

public class EndModel extends AbstractModel {
	public static final String PROP_ID_LABEL = "endmodel_label_text";
	public static final String PROP_ID_NAME = "endmodel_name_text";
	
	
	private EndNode endNode;

	public EndModel() {
		labelModel = new LabelModel(this);
		labelModel.setLabelName(I18nUtil.getMessage("END_MODEL"));
		nodeXmlProperty.setLabel(I18nUtil.getMessage("END_MODEL"));
		endNode = new EndNode();
	}
	

	@Override
	public void refreshName() {
	}

	@Override
	public void updateImage() {
	}

	public EndNode getEndNode() {
		return endNode;
	}


	public void setEndNode(EndNode endNode) {
		this.endNode = endNode;
	}
	
	public void setNodeName(String name) {
		endNode.setName(name);
		firePropertyListenerChange(PROP_ID_NAME, null, name);
	}
	
	public void setNodeDesc(String desc) {
		endNode.setDescription(desc);
		firePropertyListenerChange(PROP_ID_LABEL, null, desc);
	}

	@Override
	public DataObjectInterface getDataObject() {
		return endNode;
	}

	@Override
	public void setDataObject(DataObjectInterface object) {
		endNode = (EndNode) object;
	}

}
