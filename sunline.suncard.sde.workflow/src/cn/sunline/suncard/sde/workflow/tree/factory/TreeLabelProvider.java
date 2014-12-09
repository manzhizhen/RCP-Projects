/* 文件名：     TreeLabelProvider.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	树模板的标签提供者
 * 修改人：     易振强
 * 修改时间：2011-10-28
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.tree.factory;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

/**
 * 树模板的标签提供者
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 */

public class TreeLabelProvider implements ILabelProvider{
	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}
	
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
