/* 文件名：     linkModel.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	 普通连接线的Model
 * 修改人：     易振强
 * 修改时间：2011-12-15
 * 修改内容：创     建
 */
package cn.sunline.suncard.powerdesigner.gef.model;

import java.util.List;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;

import cn.sunline.suncard.powerdesigner.gef.command.PasteModelCommand;
import cn.sunline.suncard.powerdesigner.model.DataObjectInterface;
import cn.sunline.suncard.powerdesigner.model.LineModel;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;


/**
 * 普通连接线的Model
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 */
public class LineGefModel extends AbstractLineGefModel {
	public static final String PROP_ID_LABEL = "linemodel_label_text";
	private Label label =  new Label("");
	
	private LineModel lineModel;
	
	public static Log logger = LogManager.getLogger(LineGefModel.class
			.getName());

	
	public LineGefModel() {
		getLineXmlProperty().setLabel("");
		lineModel = new LineModel();
	}
	
	@Override
	public Object clone() {
		LineGefModel linkGefModel = new LineGefModel();
		linkGefModel.setSource(this.getSource());
		linkGefModel.setTarget(this.getTarget());
		
		try {
			linkGefModel.setDataObject((DataObjectInterface) lineModel.clone());
		} catch (CloneNotSupportedException e) {
			logger.error("克隆LineGefModel的LineModel失败！");
			e.printStackTrace();
		}
		
		// 注意：克隆时，暂时没有拷贝LineXmlProperty 和 List<Point>信息。
		
		return linkGefModel;
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

	@Override
	public LineModel getDataObject() {
		return lineModel;
	}

	@Override
	public void setDataObject(DataObjectInterface object) {
		lineModel = (LineModel) object;
	}

}
