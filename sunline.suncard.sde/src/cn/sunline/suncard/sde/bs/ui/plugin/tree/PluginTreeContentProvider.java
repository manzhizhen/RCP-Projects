
package cn.sunline.suncard.sde.bs.ui.plugin.tree;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
/**
 * 文件名：     PluginTreeContentProvider.java
 * 版权：      	Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.	
 * 描述：          插件树的内容提供者
 * 修改人：     易振强
 * 修改时间：2011-9-23
 * 修改内容：创建
 */
public class PluginTreeContentProvider extends ArrayContentProvider implements
		ITreeContentProvider {

	@Override
	public Object[] getChildren(Object parentElement) {
		// TODO Auto-generated method stub
		if(parentElement instanceof PluginTreeContent) {
			return ((PluginTreeContent)parentElement).
					getChildren().toArray();
		}
		
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		if(element instanceof PluginTreeContent) {
			return ((PluginTreeContent)element).parent;
		}
		
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		if(element instanceof PluginTreeContent) {
			return ((PluginTreeContent)element).hasChild();
		}
		
		return false;
	}

}
