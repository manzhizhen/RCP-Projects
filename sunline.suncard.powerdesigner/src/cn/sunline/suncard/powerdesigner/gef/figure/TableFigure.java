package cn.sunline.suncard.powerdesigner.gef.figure;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.widgets.Display;

import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.TableShortcutModel;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;


public class TableFigure extends RectangleFigure{
	public static int FIGURE_WIDTH = 50;
	public static int FIGURE_HIGHT = 60;
	
	private boolean isShortcut = false; // 标记是否是表格的快捷方式
	private TableModel tableModel; 
	private TableShortcutModel tableShortcutModel;
	
	private Label nameLabel;
	private ColumnFigure columnFigure;
	private Font boldFont;
	private Log logger = LogManager.getLogger(TableFigure.class
			.getName());
	
	/**
	 * 创建表格模型时调用的构造函数
	 * @param tableModel
	 */
	public TableFigure(TableModel tableModel) {
		this.tableModel = tableModel;
		
		Font font = new Font(Display.getCurrent(), new FontData(I18nUtil
				.getMessage("DEFAULT_FONT"), 8, SWT.NORMAL));
//		setFont(Display.getCurrent().getSystemFont());
		setFont(font);
		
		FontData[] fontDataArray = getFont().getFontData().clone();
		for(FontData fontData : fontDataArray) {
			fontData.setStyle(SWT.BOLD);
		}
		
		boldFont = new Font(Display.getCurrent(), fontDataArray);
		
		createFigure();
	}
	
	/**
	 * 创建表格快捷方式模型时调用的构造函数
	 * @param tableModel
	 */
	public TableFigure(TableShortcutModel tableShortcutModel) {
		isShortcut = true;
		this.tableShortcutModel = tableShortcutModel;
		this.tableModel = tableShortcutModel.getTargetTableModel();
		
		Font font = new Font(Display.getCurrent(), new FontData(I18nUtil
				.getMessage("DEFAULT_FONT"), 8, SWT.NORMAL));
//		setFont(Display.getCurrent().getSystemFont());
		setFont(font);
		
		FontData[] fontDataArray = getFont().getFontData().clone();
		for(FontData fontData : fontDataArray) {
			fontData.setStyle(SWT.BOLD);
		}
		
		boldFont = new Font(Display.getCurrent(), fontDataArray);
		
		createFigure();
	}
	
	/**
	 * 创建图形
	 */
	private void createFigure() {
		ToolbarLayout tolbarLayouy = new ToolbarLayout();
		tolbarLayouy.setSpacing(3);
		setLayoutManager(tolbarLayouy);
		
		setBorder(new CompoundBorder(new LineBorder(1), new MarginBorder(0, 5,
				0, 0)));
		
		
		nameLabel = new Label(tableModel.getTableDesc());
		nameLabel.setTextAlignment(PositionConstants.CENTER);
		nameLabel.setFont(boldFont);
		
		// 添加表格名称
		add(nameLabel);
		// 添加列信息
		columnFigure = new ColumnFigure(tableModel.getColumnList(), getFont());
		add(columnFigure);
		
		if(tableModel.getColumnList().size() == 0) {
			setPreferredSize(FIGURE_WIDTH, FIGURE_HIGHT);
		} else {
			Dimension dimension = getLayoutManager().getPreferredSize(this, -1, -1);
			setSize(dimension);
		}
		
	}
	
	@Override
	public void paintFigure(Graphics graphics) {
		Rectangle r = getBounds();
		
		if(!isShortcut) {
			graphics.setBackgroundPattern(new Pattern(Display.getCurrent(), r.x,
					r.y, r.x + r.width, r.y + r.height, ColorConstants.white,
					ColorConstants.lightGray));
			graphics.fillRectangle(r);
			
			graphics.setBackgroundColor(ColorConstants.black);
			
		} else {
			graphics.setBackgroundPattern(new Pattern(Display.getCurrent(), r.x,
					r.y, r.x + r.width, r.y + r.height, ColorConstants.white,
					ColorConstants.lightBlue));
			graphics.fillRectangle(r);
			
			graphics.setBackgroundColor(ColorConstants.blue);
		}
		
		graphics.drawLine(new Point(r.x, r.y + nameLabel.getBounds().height + 2), 
				new Point(r.x + r.width, r.y + nameLabel.getBounds().height + 2));
		
	}
	
	/**
	 * 根据新的内容来设定表格尺寸
	 * @param newTableModel
	 * @return
	 */
	public Dimension updateFigure(TableModel newTableModel) {
		if(newTableModel == null) {
			logger.error("传入的TableModel为空，无法更新表格图形！");
			return null;
		}
		
		removeAll();
		nameLabel.setText(newTableModel.getTableDesc());
		add(nameLabel);
		
		columnFigure = new ColumnFigure(newTableModel.getColumnList(), getFont());
		add(columnFigure);
		
		
		
		// 根据内容来重新设置图形尺寸
		if(newTableModel.getColumnList().size() > 0 && newTableModel.isAutoSize()) {
//			Dimension dimension = new Dimension(200, nameLabel.getBounds().height * 
//					(columnFigure.getShowColumnNum() + 1) + 10);
//			setSize(dimension.width, dimension.height);
			Dimension dimension = getLayoutManager().getPreferredSize(this, -1, -1);
			setSize(dimension);
			return dimension;
		}
		
		return null;
	}
}
