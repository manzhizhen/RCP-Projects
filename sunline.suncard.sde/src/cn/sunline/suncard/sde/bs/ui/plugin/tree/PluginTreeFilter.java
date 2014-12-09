package cn.sunline.suncard.sde.bs.ui.plugin.tree;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * 文件名：     PluginTreeFilter.java
 * 版权：          Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.	
 * 描述:		插件树的过滤器
 * 修改人：     易振强
 * 修改时间：2011-9-23
 * 修改内容：插件
 */
public class PluginTreeFilter extends ViewerFilter {
	private String filter;

	public PluginTreeFilter(String filter) {
		this.filter = filter.trim();
	}

	public void setFilter(String filter) {
		this.filter = filter.trim();
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof PluginTreeContent) {
			PluginTreeContent pluginTreeContent = (PluginTreeContent) element;
			if (pluginTreeContent.hasChild()) {
				boolean parentFlag = false;
				if (pluginTreeContent.toString().toUpperCase()
						.contains(filter.toUpperCase())) {
					parentFlag = true;
				}

				List<PluginTreeContent> plguinTreeContentChildren = pluginTreeContent
						.getChildren();

				boolean childrenFlag = false;
				for (PluginTreeContent temp : plguinTreeContentChildren) {
					childrenFlag = childrenFlag
							|| select(viewer, plguinTreeContentChildren, temp);
				}

				return parentFlag || childrenFlag;

			} else
				return pluginTreeContent.toString().toUpperCase()
						.contains(filter.toUpperCase());
		}

		return false;

	}

}
