/* 文件名：     TreeContentProvider.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2011-10-28
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.tree.oldfactory;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;


/**
 * 树模板的内容提供者
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 * @deprecated
 */

public class TreeContentProvider extends ArrayContentProvider implements
	ITreeContentProvider{

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof TreeContent) {
			return ((TreeContent)parentElement).
					getChildrenList().toArray();
		}
		
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof TreeContent) {
			return ((TreeContent)element).
					getParent();
		}
		
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof TreeContent) {		
			return ((TreeContent)element).hasChildren();
		}
		
		return false;
	}

}
