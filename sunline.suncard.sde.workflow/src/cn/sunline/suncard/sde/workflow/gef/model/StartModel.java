/* 文件名：     StartModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.model;

import cn.sunline.suncard.sde.workflow.gef.model.props.PropertySourceAdapter;
import cn.sunline.suncard.sde.workflow.model.DataObjectInterface;
import cn.sunline.suncard.sde.workflow.model.StartNode;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 开始模型
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 */
public class StartModel extends AbstractModel{
	public static final String PROP_ID_LABEL = "startmodel_label_text";
	public static final String PROP_ID_NAME = "startmodel_name_text";
	
	
	private StartNode startNode;

	public StartModel() {
		labelModel = new LabelModel(this);
		labelModel.setLabelName(I18nUtil.getMessage("START_MODEL"));
		nodeXmlProperty.setLabel(I18nUtil.getMessage("START_MODEL"));
		startNode = new StartNode();
	}
	
	@Override
	public void refreshName() {
	}

	@Override
	public void updateImage() {
	}

	public StartNode getStartNode() {
		return startNode;
	}

	public void setStartNode(StartNode startNode) {
		this.startNode = startNode;
	}
	
	public String getStartName() {
		return  startNode.getName();
	}
	
	public void setStartName(String name) {
		startNode.setName(name);
		firePropertyListenerChange(PROP_ID_NAME, null, name);
	}
	
	public void setStartDesc(String desc) {
		startNode.setDescription(desc);
		firePropertyListenerChange(PROP_ID_LABEL, null, desc);
	}

	@Override
	public DataObjectInterface getDataObject() {
		return startNode;
	}

	@Override
	public void setDataObject(DataObjectInterface object) {
		startNode = (StartNode) object;
	}
	
}
