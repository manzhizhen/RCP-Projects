/* 文件名：     CheckboxComposite.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-6
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.tree.factory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * 带复选框的树的Composite
 * @author  Manzhizhen
 * @version 1.0, 2013-1-6
 * @see 
 * @since 1.0
 */
public class CheckboxComposite extends Composite{
	private Tree tree;
	private CheckboxTreeViewer treeViewer;
	
	public CheckboxComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		createControl();
	}
	
	private void createControl() {
		tree = new Tree(this, SWT.CHECK | SWT.BORDER);
		tree.setLayoutData(new FormData());
		treeViewer = new CheckboxTreeViewer(tree);
		
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 0);
		formData.right = new FormAttachment(100, 0);
		formData.top = new FormAttachment(0, 0);
		formData.bottom = new FormAttachment(100, 0);
		tree.setLayoutData(formData);

	}
	
	/**
	 * 初始控件的值
	 * @param inputObject
	 */
	public void initControlData(List<TreeContent> treeContentList, Object...objects) {
		treeViewer.setContentProvider(new TreeContentProvider());
		if(objects.length == 1 && objects[0] instanceof LabelProvider) {
			treeViewer.setLabelProvider((IBaseLabelProvider) objects[0]);
		} else {
			treeViewer.setLabelProvider(new TreeLabelProvider());
		}

		treeViewer.setInput(treeContentList);
	}
	
	/**
	 * 获得一该Class为对象类型的被选中的树节点对象
	 * @param clazz
	 * @return
	 */
	public Set<TreeContent> getCheckedTreeContent(Class clazz) {
		Set<TreeContent> checkedTreeContentSet = new HashSet<TreeContent>();
		Object[] objects = treeViewer.getCheckedElements();
		for(Object obj : objects) {
			if(obj instanceof TreeContent) {
				if(((TreeContent)obj).getObj().getClass() == clazz) {
					checkedTreeContentSet.add((TreeContent) obj);
				}
			}
		}
		
		return checkedTreeContentSet;
	}
	
	/**
	 * 创建控件事件
	 * @param inputObject
	 */
	public void createEvent() {
		tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK) {
					TreeItem item = (TreeItem) event.item;
					boolean checked = item.getChecked();
					checkChildren(item.getItems(), checked);

					// 触发这个的Item的grayed = false，因为这是个CHECK事件，要么全选，要么全不选。
					checkParent(item.getParentItem(), checked, false);
				}
			}
		});
	}
	
	private void checkParent(TreeItem parent, boolean checked, boolean grayed) {
		// 递归退出条件：父亲为空。
		if (parent == null) {
			return ;
		}
		
		for (TreeItem child : parent.getItems()) {
			if (child.getGrayed() || checked != child.getChecked()) {
				// 1，子节点有一个为【部分选中的】，直接设置父节点为【部分选中的】。
				// 2，子节点不完全相同，说明【部分选中的】。
				checked = grayed = true;
				break;
			}
		}
		
		parent.setChecked(checked);
		parent.setGrayed(grayed);
		checkParent(parent.getParentItem(), checked, grayed);
	}

	private void checkChildren(TreeItem[] children, boolean checked) {
		// 递归退出条件：孩子为空。
		if (children.length == 0) {
			return;
		}
		
		for (TreeItem child : children) {
			child.setGrayed(false);// 必须设置这个，因为本来节点可能【部分选中的】。
			child.setChecked(checked);
			checkChildren(child.getItems(), checked);
		}
	}

	public CheckboxTreeViewer getTreeViewer() {
		return treeViewer;
	}
}
