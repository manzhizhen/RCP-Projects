/* 文件名：     linkModel.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	 普通连接线的Model
 * 修改人：     易振强
 * 修改时间：2011-12-15
 * 修改内容：创     建
 */
package cn.sunline.suncard.sde.workflow.gef.model;

import org.eclipse.draw2d.Label;

import cn.sunline.suncard.sde.workflow.gef.model.props.PropertySourceAdapter;
import cn.sunline.suncard.sde.workflow.model.LineNode;
import cn.sunline.suncard.sde.workflow.model.DataObjectInterface;

/**
 * 普通连接线的Model
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 */
public class LineModel extends AbstractLineModel {
	public static final String PROP_ID_LABEL = "linemodel_label_text";
	private Label label =  new Label("");
	
	private LineNode lineNode;
	
	public LineModel() {
		getLineXmlProperty().setLabel("");
		lineNode = new LineNode();
	}
	
	@Override
	public Object clone() {
		LineModel linkModel = new LineModel();
		linkModel.setSource(this.getSource());
		linkModel.setTarget(this.getTarget());
		
		return linkModel;
	}


	public Label getLabel() {
		return label;
	}

	@Override
	public void setLabelText(String text) {
		this.label.setText(text);
		getLineXmlProperty().setLabel(text);
		firePropertyListenerChange(PROP_ID_LABEL, null, text);
	}
	
	public String getLabelText() {
		return label.getText();
	}

	public LineNode getLineNode() {
		return lineNode;
	}

	public void setLineNode(LineNode lineNode) {
		this.lineNode = lineNode;
	}

	@Override
	public LineNode getDataObject() {
		return lineNode;
	}

	@Override
	public void setDataObject(DataObjectInterface object) {
		lineNode = (LineNode) object;
	}

}
