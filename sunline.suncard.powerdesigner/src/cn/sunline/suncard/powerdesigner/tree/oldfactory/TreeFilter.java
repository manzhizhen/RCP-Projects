/* 文件名：     TreeFilter.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2011-10-28
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.tree.oldfactory;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;


/**
 * 树模板的过滤器
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 * @deprecated
 */

public class TreeFilter extends ViewerFilter{
	private String filter;

	public TreeFilter(String filter) {
		this.filter = filter.trim();
	}

	public void setFilter(String filter) {
		this.filter = filter.trim();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof TreeContent) {
			TreeContent treeContent = (TreeContent) element;
			if (treeContent.hasChildren()) {
				boolean parentFlag = false;
				if (treeContent.toString().toUpperCase()
						.contains(filter.toUpperCase())) {
					parentFlag = true;
				}

				List<TreeContent> treeContentChildren = treeContent
						.getChildrenList();

				boolean childrenFlag = false;
				for (TreeContent temp : treeContentChildren) {
					childrenFlag = childrenFlag
							|| select(viewer, treeContentChildren, temp);
				}

				return parentFlag || childrenFlag;

			} else
				return treeContent.toString().toUpperCase()
						.contains(filter.toUpperCase());
		}

		return false;
	}

}
