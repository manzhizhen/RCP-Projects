/* 文件名：     TreeLabelProvider.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	树模板的标签提供者
 * 修改人：     易振强
 * 修改时间：2011-10-28
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.tree.oldfactory;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * 树模板的标签提供者
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 * @deprecated
 */

public class TreeLabelProvider extends LabelProvider {
	@Override
	public Image getImage(Object element) {
		if(element instanceof TreeContent) {
			return ((TreeContent)element).getImage();
		}
		
		return null;
	}

	@Override
	public String getText(Object element) {
		if(element instanceof TreeContent) {
			return ((TreeContent)element).getNodeName();
		}
		
		return null;
	}

}
