/**
 * 文件名：     PluginTreeLabelProvider.java
 * 版权：          Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.	
 * 描述：	
 * 修改人：     易振强
 * 修改时间：2011-9-23
 * 修改内容：
 */
package cn.sunline.suncard.sde.bs.ui.plugin.tree;

import java.sql.Timestamp;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import cn.sunline.suncard.sde.bs.biz.BsPluginBiz;
import cn.sunline.suncard.sde.bs.biz.BsPluginlogBiz;
import cn.sunline.suncard.sde.bs.entity.BsPlugin;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.ui.resource.IAppconstans;
import cn.sunline.suncard.sde.bs.ui.resource.IImageKey;

public class PluginTreeLabelProvider extends LabelProvider implements
		ITableLabelProvider {
	private BsPluginBiz bsPluginBiz = new BsPluginBiz();
	private BsPluginlogBiz bsPluginlogBiz = new BsPluginlogBiz();

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0)
			return CacheImage.getCacheImage().getImage(
					IAppconstans.APPLICATION_ID, IImageKey.PLUGIN_TREE_ELEMENT);
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		String str = "";
		if (element instanceof PluginTreeContent) {
			PluginTreeContent pluginTreeContent = (PluginTreeContent) element;
			Bundle bundle = pluginTreeContent.getBundle();

			if (bundle != null) {
				String bundleName = bundle.getHeaders().get("Bundle-Name");
				BsPlugin bsPlugin = bsPluginBiz.findByPluginName(bundleName);

				switch (columnIndex) {
				case 0:
					str = bundle.getHeaders().get("Bundle-Name");
					// bundle.getBundleId();
					break;

				case 1:
					if (bsPlugin != null) {
						str = bsPlugin.getId().getPluginId();
					} else {
						str = new Long(bundle.getBundleId()).toString();
					}
					break;

				case 2:
//					str = bundle.getSymbolicName();
//					str = bundle.getVersion().toString();
					str = bundle.getHeaders().get("Bundle-Version");
					break;

				case 3:
					if (bsPlugin != null) {
						str = bsPluginlogBiz.findDateByBsPlugin(bsPlugin) + new Timestamp(bundle.getLastModified()).toString();
//						if(str == null) {
//							str = "null";
//						}
					}
					break;
				}
			} else {
				if (columnIndex == 0) {
					str = pluginTreeContent.getName();
				}
			}
		}

		return str;
	}

}
