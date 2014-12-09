/*
 * 文件名：
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：周兵
 * 修改时间：2011-10-13
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.provider;

import java.util.List;


import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import cn.sunline.suncard.sde.bs.tree.FunctionTree;
@SuppressWarnings("rawtypes")
public class FunctionTreeContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof List){
			List input = (List)inputElement;
			return input.toArray();
		}else if (inputElement instanceof FunctionTree){
			FunctionTree tree = (FunctionTree) inputElement;
			return tree.getChildren().toArray();
		}
		return new Object[0];
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof FunctionTree){
			FunctionTree functionTree = (FunctionTree)parentElement;
			List list = functionTree.getChildren();
			if(list==null) return new Object[0];
			return list.toArray();
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof FunctionTree){
			FunctionTree functionTree = (FunctionTree)element;
			List list = functionTree.getChildren();
			return !(list==null||list.isEmpty());
		}
		return false;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
