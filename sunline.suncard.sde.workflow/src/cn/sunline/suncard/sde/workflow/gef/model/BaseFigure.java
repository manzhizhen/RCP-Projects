/* 文件名：     CommonFigure.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	图元的展示模型
 * 修改人：     易振强
 * 修改时间：2011-11-16
 * 修改内容：创    建
 */
package cn.sunline.suncard.sde.workflow.gef.model;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.sde.workflow.resource.IDmAppConstants;
import cn.sunline.suncard.sde.workflow.resource.IDmImageKey;

import cn.sunline.suncard.sde.bs.resource.CacheImage;

/**
 * 图元的展示模型
 * @author    易振强
 * @version   [1.0, 2011-11-15]
 * @since     1.0
 */
public class BaseFigure extends Figure {
	public final static int FIGURE_WIDTH = 178;
	public final static int FIGURE_HIGHT = 35;
	
	// 图标和Label的垂直距离
	public final static int SPACE = -18;
	
	// 正常图标的状态
	public final static String FIGURE_STATE_NORMAL = "FIGURE_STATE_NORMAL";
	
	// 错误图标的状态
	public final static String FIGURE_STATE_ERROR = "FIGURE_STATE_ERROR";
	
	
	// 用于显示的图标
	protected ImageFigure imageFigure; 
	protected ImageFigure errorImageFigure;
	
	public BaseFigure(AbstractModel model) {
		initImage(model);
		
		// 设置背景色
		setBackgroundColor(ColorConstants.tooltipBackground);

		// 设置前景色
		setForegroundColor(ColorConstants.tooltipForeground);

		// 设置Figure的边框,并给边框加上颜色
		// setBorder(new LineBorder(ColorConstants.cyan));

//		// 设置label中文字的对齐方式
//		label.setTextAlignment(PositionConstants.LEFT);

		// 设置Figure的布局方式
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setHorizontal(false);
		setLayoutManager(flowLayout);
		
		setBorder(new MarginBorder(0));
		
		imageFigure.setLayoutManager(new FlowLayout());
		errorImageFigure.setLayoutManager(new FlowLayout());
		
		add(imageFigure);

	}

	/**
	 * 根据模型类别来初始化图标
	 */
	private void initImage(AbstractModel model) {
		Image image = null;
		if(model instanceof StartModel) {
			image = CacheImage.getCacheImage().getImage(IDmAppConstants.APPLICATION_ID, 
					IDmImageKey.MODEL_START);
			imageFigure = new ImageFigure(image);
			
			image = CacheImage.getCacheImage().getImage(IDmAppConstants.APPLICATION_ID, 
					IDmImageKey.MODEL_START);
			errorImageFigure = new ImageFigure(image);
			
		} else if(model instanceof EndModel) {
			image = CacheImage.getCacheImage().getImage(IDmAppConstants.APPLICATION_ID, 
					IDmImageKey.MODEL_END);
			imageFigure = new ImageFigure(image);
			
			image = CacheImage.getCacheImage().getImage(IDmAppConstants.APPLICATION_ID, 
					IDmImageKey.MODEL_END);
			errorImageFigure = new ImageFigure(image);
			
		}  else if(model instanceof TaskModel) {
			image = CacheImage.getCacheImage().getImage(IDmAppConstants.APPLICATION_ID, 
					IDmImageKey.MODEL_TASK);
			imageFigure = new ImageFigure(image);
			
			
			image = CacheImage.getCacheImage().getImage(IDmAppConstants.APPLICATION_ID, 
					IDmImageKey.MODEL_TASK);
			errorImageFigure = new ImageFigure(image);
			
		}
		
	}
	
	/**
	 *  改变图标的状态
	 */
	public void changeFigure(String state) {
		removeAll();
		
		if(FIGURE_STATE_NORMAL.equals(state)) {
			
			add(imageFigure);
			
		} else if(FIGURE_STATE_ERROR.equals(state)) {
			add(errorImageFigure);
		}
		
	}

}
