/*
 * 文件名：
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：易振强
 * 修改时间：2011-10-18
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.plugin.gallery;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;

import cn.sunline.suncard.sde.bs.ui.plugin.tree.PluginTreeContent;

class ToolTipListener implements Listener {
	private Shell tooltipShell = null;
	private Gallery gallery = null;

	public ToolTipListener(Gallery gallery) {
		this.gallery = gallery;
	}

	public void handleEvent(Event event) {
		switch (event.type) {
		case 32:
			handleMouseHover(event);
			break;
		case 3:
		case 4:
		case 5:
			if (this.tooltipShell == null)
				break;
			this.tooltipShell.dispose();
			this.tooltipShell = null;
		}
	}

	private void handleMouseHover(Event e) {
		GalleryItem item = this.gallery.getItem(new Point(e.x, e.y));
		if (item != null) {
			PluginTreeContent pluginTreeContent = (PluginTreeContent) item.getData();
			Bundle b = pluginTreeContent.getBundle();
			Display disp = this.gallery.getDisplay();

			if (this.tooltipShell != null) {
				this.tooltipShell.dispose();
				this.tooltipShell = null;
			}

			this.tooltipShell = new Shell(this.gallery.getShell(), 540676);

			final Shell tip = this.tooltipShell;
			tip.setBackground(disp.getSystemColor(29));
			FillLayout layout = new FillLayout();
			layout.marginWidth = 2;
			tip.setLayout(layout);
			Label label = new Label(tip, 0);
			label.setForeground(disp.getSystemColor(28));
			label.setBackground(disp.getSystemColor(29));

			Listener labelListener = new Listener() {
				public void handleEvent(Event event) {
					switch (event.type) {
					case 3:
					case 7:
						tip.dispose();
					case 4:
					case 5:
					case 6:
					}
				}
			};

			label.addListener(7, labelListener);
			label.addListener(3, labelListener);
			String stateDesc = "";
			switch (b.getState()) {
			case 32:
				stateDesc = "active";
				break;
			case 2:
				stateDesc = "installed";
				break;
			case 4:
				stateDesc = "resolved";
				break;
			case 8:
				stateDesc = "starting";
				break;
			default:
				stateDesc = "not defined";
			}

			label.setText(b.getSymbolicName() + "\n" + stateDesc);

			Point size = tip.computeSize(-1, -1);
			Point pt2 = this.gallery.toDisplay(e.x, e.y);
			tip.setBounds(pt2.x, pt2.y - size.y, size.x, size.y);
			tip.setVisible(true);
		}
	}
}
