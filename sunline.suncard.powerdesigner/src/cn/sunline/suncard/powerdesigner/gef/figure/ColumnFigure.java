package cn.sunline.suncard.powerdesigner.gef.figure;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.widgets.Display;

import cn.sunline.suncard.powerdesigner.manager.SystemSetManager;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.sde.bs.resource.SWTResourceManager;

public class ColumnFigure extends NoBorderFigure{
	private ColumnModel columnModel;
	private Label descLabel;
	private List<ColumnModel> columnModelList;
	private Font boldFont;	// 加粗字体
	
	private int showColumnNum = 0; // 记录下表格图形中显示的行数
	
	public ColumnFigure(List<ColumnModel> columnModelList, Font parentFont) {
		this.columnModelList = columnModelList;
		
		FontData[] fontDataArray = parentFont.getFontData().clone();
		for(FontData fontData : fontDataArray) {
			fontData.setStyle(SWT.BOLD);
		}
		
		boldFont = new Font(Display.getCurrent(), fontDataArray);
		
		createFigure();
	}
	
	private void createFigure() {
		showColumnNum = 0;
		
		ToolbarLayout toolbarLayout = new ToolbarLayout(true);
		toolbarLayout.setSpacing(5);
		setLayoutManager(toolbarLayout);
		
		// 描述图形
		RectangleFigure descFigure = new NoBorderFigure();
		descFigure.setBorder(new ColumnBorder());
		ToolbarLayout descToolbarLayout = new ToolbarLayout(false);
		descFigure.setLayoutManager(descToolbarLayout);
		
		// 数据类型图形
		RectangleFigure typeFigure = new NoBorderFigure();
		ToolbarLayout typeToolbarLayout = new ToolbarLayout(false);
		typeFigure.setLayoutManager(typeToolbarLayout);
		
		// 键信息图形
		RectangleFigure keyFigure = new NoBorderFigure();
		ToolbarLayout keyToolbarLayout = new ToolbarLayout(false);
		keyFigure.setLayoutManager(keyToolbarLayout);
		
		for(ColumnModel columnModel : columnModelList) {
			if(SystemConstants.TABLE_SHOW_TYPE_P.equals(SystemSetManager
					.getTableModelGefShowType()) && !columnModel.isPrimaryKey()) {
				continue ;
			} else if(SystemConstants.TABLE_SHOW_TYPE_PF.equals(SystemSetManager
					.getTableModelGefShowType()) && !columnModel.isPrimaryKey() 
					&& !columnModel.isForeignKey()) {
				continue ;
			}
			
			// 键信息
			StringBuffer isKey = new StringBuffer("");
			if(columnModel.isPrimaryKey()) {
				isKey.append("<pk");
			}
			
			if(columnModel.isForeignKey()) {
				if(isKey.length() == 0) {
					isKey.append("<fk>");
				} else {
					isKey.append(", fk>");
				}
			} else if(isKey.length() != 0){
				isKey.append(">");
			}
			
			Label descLabel = new Label(columnModel.getColumnDesc());
			String typeName = columnModel.getDataTypeModel().getName();
			if(typeName == null || "".equals(typeName.trim())) {
				typeName = DmConstants.UNDEFINED;
			}
			Label typeLabel = new Label(typeName);
			Label keyLabel = new Label(isKey.toString());
			
			if(columnModel.isPrimaryKey()) {
				// 主键需要加粗
				descLabel.setFont(boldFont);
				typeLabel.setFont(boldFont);
				keyLabel.setFont(boldFont);
			}
			
			keyFigure.add(keyLabel);
			typeFigure.add(typeLabel);
			descFigure.add(descLabel);
			
			showColumnNum++;
		}
		
		add(descFigure);
		add(typeFigure);
		add(keyFigure);
	}

	public int getShowColumnNum() {
		return showColumnNum;
	}
	
	
}
