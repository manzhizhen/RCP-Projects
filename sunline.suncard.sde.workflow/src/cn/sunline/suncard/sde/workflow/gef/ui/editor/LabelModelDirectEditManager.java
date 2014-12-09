/* 文件名：     LabelModelDirectEditManager.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	LableModel模型的直接编辑
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.ui.editor;


import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.swt.widgets.Text;

import cn.sunline.suncard.sde.workflow.gef.model.AbstractModel;

/**
 * LableModel模型的直接编辑
 * @author    易振强
 * @version   1.0  2011-12-1
 * @see       
 * @since     WF  1.0 
 */
public class LabelModelDirectEditManager extends DirectEditManager {
	
	//要修改该模型的文本属性
	private AbstractModel model;

	public LabelModelDirectEditManager(GraphicalEditPart source, Class editorType,
			CellEditorLocator locator) {
		super(source, editorType, locator);
		
		//获得moduleModel模型
		model = (AbstractModel) source.getModel();
	}

	@Override
	protected void initCellEditor() {
		//在显示一个cell editor之前，先给他设置一个值
		//这里的值是获得图形模型的文本
		getCellEditor().setValue(model.getLabelName() == null ? "" : model.getLabelName());
		
		//在所选中的TextCellEditor的Text控件中的所有文本都显示为选择态
		Text text = (Text) getCellEditor().getControl();
		text.selectAll();
	}

}
