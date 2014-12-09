package cn.sunline.suncard.powerdesigner.gef.figure;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;

public class ColumnBorder extends AbstractBorder {
	public static final int FOLD = 10;

	public Insets getInsets(IFigure figure) {
//		return new Insets(1, 2 + FOLD, 2, 2); // top,left,bottom,right
		return new Insets(0, 0, 0, 0);
	}

	public void paint(IFigure figure, Graphics graphics, Insets insets) {
//		Rectangle r = figure.getBounds().getCopy();
//		
//		// 收缩该矩形
//		r.shrink(insets);
//		graphics.setLineWidth(1);
//
//		// solid long edges around border
//		graphics.drawLine(r.x + FOLD, r.y, r.x + r.width - 1, r.y);
//		graphics.drawLine(r.x, r.y + FOLD, r.x, r.y + r.height - 1);
//		graphics.drawLine(r.x + r.width - 1, r.y, r.x + r.width - 1, r.y
//				+ r.height - 1);
//		graphics.drawLine(r.x, r.y + r.height - 1, r.x + r.width - 1, r.y
//				+ r.height - 1); // solid short edges4.5 Borders 47
//		graphics.drawLine(r.x + FOLD, r.y, r.x + FOLD, r.y + FOLD);
//		graphics.drawLine(r.x, r.y + FOLD, r.x + FOLD, r.y + FOLD);
//
//		// gray small triangle
//		graphics.setBackgroundColor(ColorConstants.lightGray);
//		graphics.fillPolygon(new int[] { r.x, r.y + FOLD, r.x + FOLD, r.y,
//				r.x + FOLD, r.y + FOLD });
//
//		// 划点线
//		graphics.setLineStyle(SWT.LINE_DOT);
//		graphics.drawLine(r.x, r.y + FOLD, r.x + FOLD, r.y);
	}
}
