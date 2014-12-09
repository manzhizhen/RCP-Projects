/* 文件名：     LabelModelCellEditorLocator.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	定位LableModel的编辑区域
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.ui.editor;


import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Text;

/**
 * 定位LableModel的编辑区域
 * @author    易振强
 * @version   1.0  2011-12-1
 * @see       
 * @since     WF  1.0 
 */
public class LabelModelCellEditorLocator implements CellEditorLocator {
	private final static int iconHight = 50;
	private final static int spacing = 10;
	private final static int textHight = 20;
	
	//Text控件要放在Figure所在的位置
	private IFigure figure;
	
	public LabelModelCellEditorLocator(IFigure f){
	//在构造函数中得到为那个Figure设置Text控件
		figure = f;
	}

	@Override
	public void relocate(CellEditor celleditor) {
		Text text = (Text) celleditor.getControl();
		//text控件尺寸和figure一样
		Rectangle rectangle = figure.getBounds().getCopy();
		figure.translateToAbsolute(rectangle);
		
		text.setBounds(rectangle.x, rectangle.y, 
				rectangle.width, textHight);
	}

}
