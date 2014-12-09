/* 文件名：     TreeViewPart.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	树模板的Composite
 * 修改人：     易振强
 * 修改时间：2011-10-28
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.tree.oldfactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;



/**
 * 树模板的Composite
 * 老版本的树模板
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 * @deprecated
 */

public class TreeViewComposite {
	private Text text;
	private Tree tree;
	private TreeViewer treeViewer;
	
	private List<IPropertyChangeListener> myListeners = new ArrayList<IPropertyChangeListener>();
	private TreeContent rootContent = null;
	
	public TreeViewComposite(Composite composite, boolean isSingle) {
		composite.setLayout(new FormLayout());
		
		text = new Text(composite, SWT.BORDER);
		FormData textData = new FormData();
		textData.right = new FormAttachment(100, -5);
		textData.top = new FormAttachment(0, 5);
		textData.left = new FormAttachment(0, 5);
		text.setLayoutData(textData);
		
		if(isSingle) {
			tree = new Tree(composite, SWT.BORDER);
		} else {
			tree = new Tree(composite, SWT.BORDER | SWT.MULTI);
		}
		
		FormData treeData = new FormData();
		treeData.top = new FormAttachment(text, 5);
		treeData.left = new FormAttachment(0, 5);
		treeData.right = new FormAttachment(100, -5);
		treeData.bottom = new FormAttachment(100);
		tree.setLayoutData(treeData);
		
		tree.setVisible(true);
		treeViewer = new TreeViewer(tree);
		
		treeEvent();
		
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				textEvent();
			}
		});
		
		treeViewer.setLabelProvider(new TreeLabelProvider());
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setSorter(new TreeSorter());
	}
	
	/**
	 * 文本框事件
	 */
	private void textEvent() {
		String textStr = text.getText();

		if (textStr == null) {
			return;
		}

		treeViewer.setFilters(new ViewerFilter[] { 
				new TreeFilter(textStr.trim())
		});
		
		treeViewer.expandAll();
	}
	
	/**
	 * 添加一个树的根节点
	 * 想当于添加一颗新树
	 * @param  String 树节点ID
	 * @param  String 数据对象在树节点中显示的名字
	 * @param  Object 树节点绑定的数据对象
	 * @param  Image 树节点的图像
	 */
	public void createTreeRootNode(String id, String nodeName, Object node, Image image) {
		rootContent = new TreeContent(id, nodeName, node, image);
		rootContent.setParent(null);
		
		createTreeContent();
		
		treeViewer.expandAll();
	}
	
	/**
	 * 添加一个子节点(自定义图标)
	 * 不是根节点的节点都称为子节点，添加子节点必须制定父节点ID或父节点对象
	 * @param  String 父节点的id
	 * @param  String 节点的id，一般值为绑定对象的id
	 * @param  String 数据对象在树节点中显示的名字
	 * @param  Object   数据对象
	 * @param  Image  在节点上显示的图标
	 * @throws CanNotFoundNodeIDException 
	 * @return TreeContent 添加时创建树节点
	 */
	public TreeContent addNode(String parentId, String id, String nodeName, Object node, Image image) 
			throws FoundTreeNodeNotUniqueException, CanNotFoundNodeIDException {
		
		if(isEmtpy()) {
			return null;
		}
		
		TreeContent parentContent = findNodeById(parentId);
		
		TreeContent handlerTreeContent = null;
		List<TreeContent> childList = parentContent.getChildrenList();
		
		// 如果该树节点下已经有该节点，只进行更新
		for(TreeContent treeContent : childList) {
			if(treeContent.getId().equals(id)) {
				treeContent.setImage(image);
				treeContent.setNodeName(nodeName);
				treeContent.setObj(node);
				
				treeViewer.refresh();
				return treeContent;
			}
		}
		
		handlerTreeContent = new TreeContent(id, nodeName, node, image);
		handlerTreeContent.setParent(parentContent);
		parentContent.getChildrenList().add(handlerTreeContent);

		treeViewer.refresh();
		
		if(handlerTreeContent != null) {
			treeViewer.expandToLevel(handlerTreeContent, AbstractTreeViewer.ALL_LEVELS);
			treeViewer.setSelection(new StructuredSelection(handlerTreeContent));
		}
		
		return handlerTreeContent;
	}
	
	/**
	 * 添加一个子节点(使用和父节点一样的图标)
	 * 不是根节点的节点都称为子节点，添加子节点必须制定父节点ID或父节点对象
	 * @param  String 父节点的id
	 * @param  String 节点的id，一般值为绑定对象的id
	 * @param  String 数据对象在树节点中显示的名字
	 * @param  Object   数据对象
	 * @return TreeContent 添加时创建树节点
	 * @throws CanNotFoundNodeIDException 
	 * @throws FoundTreeNodeNotUniqueException 
	 */
	public TreeContent addNode(String parentId, String id, String nodeName, Object node) 
			throws FoundTreeNodeNotUniqueException, CanNotFoundNodeIDException {
		
		if(isEmtpy()) {
			return null;
		}
		
		TreeContent parentContent = findNodeById(parentId);
		
		return addNode(parentId, id, nodeName, node, parentContent.getImage());
		
	}
	
	/**
	 * 删除一个节点
	 * 这样会删除该树上所有ID为它的节点
	 * @param  String 要删除的节点的id
	 * @throws CanNotFoundNodeIDException 
	 * @throws FoundTreeNodeNotUniqueException 
	 */
	public void removeNode(String id) throws FoundTreeNodeNotUniqueException, CanNotFoundNodeIDException{
		if(isEmtpy()) {
			return ;
		}
		
		TreeContent delTreeContent = findNodeById(id);
			
		// 说明要删除的是父节点，也就是删除整棵树
		if(delTreeContent.getParent() == null) {
			rootContent = null;
			
		} else {
			delTreeContent.getParent().getChildrenList().remove(delTreeContent);
		}
		
		treeViewer.refresh();
		
	}
	
	/**
	 * 更新一个节点（采用新图标）
	 * 根据传入对象的类型自动更新相应节点添加到树中并更新
	 * @param  String   需要更新的树节点ID
	 * @param  String   新数据对象在树节点上的显示名字
	 * @param  Object   需要更新的数据对象
	 * @throws FoundTreeNodeNotUniqueException 
	 * @throws CanNotFoundNodeIDException 
	 */
	public void updateNode(String id, String nodeName, Object node, Image image) 
			throws FoundTreeNodeNotUniqueException, CanNotFoundNodeIDException {
		
		if(isEmtpy()) {
			return ;
		}
		
		TreeContent modifyContent = findNodeById(id);
		
		modifyContent.setNodeName(nodeName);
		modifyContent.setObj(node);
		modifyContent.setImage(image);
		
		treeViewer.refresh();
		
//		if(modifyContent != null) {
//			treeViewer.expandToLevel(modifyContent, AbstractTreeViewer.ALL_LEVELS);
//			treeViewer.setSelection(new StructuredSelection(modifyContent));
//		}
	}
	
	/**
	 * 更新一个节点（默认采用原图标）
	 * 根据传入对象的类型自动更新相应节点添加到树中并更新
	 * @param  Object   需要更新的数据对象
	 * @param  String   新数据对象在树节点上的显示名字
	 * @throws CanNotFoundNodeIDException 
	 * @throws FoundTreeNodeNotUniqueException 
	 */
	public void updateNode(String id, String nodeName, Object node) throws CanNotFoundNodeIDException, FoundTreeNodeNotUniqueException {
		if(isEmtpy()) {
			return ;
		}
		
		TreeContent modifyContent = findNodeById(id);
		updateNode(id, nodeName, node, modifyContent.getImage());
	}
	
	
	/**
	 *  清空树的所有数据
	 *  清除掉所有数据，包括父节点
	 */
	public void removeAll() {
		rootContent.setChildrenList(new ArrayList<TreeContent>());
	}
	
	/**
	 *  创建和更新树
	 * 	重新创建或刷新一棵树
	 */
	public void createTreeContent() {
		List<TreeContent> list = new ArrayList<TreeContent>();
		list.add(rootContent);
		treeViewer.setInput(list);
	}
	
	/**
	 * 创建树的事件
	 */
	public void treeEvent() {
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				mouseDoubleEvent();
			}
		});
	}

	/**
	 * 树的鼠标双击事件
	 */
	private void mouseDoubleEvent() {
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		if(selection.size() == 1) {
			Object obj = selection.getFirstElement();
			if(obj instanceof TreeContent) {
				TreeContent treeContent = (TreeContent) obj;

				if(treeContent.hasChildren()) {
					treeViewer.setExpandedState(treeContent, 
							!treeViewer.getExpandedState(treeContent));
				}
			}
		}
		
	}
	
	/**
	 * 通过树节点Id来查找树节点对象
	 * @param String 树节点ID
	 * @throws FoundTreeNodeNotUniqueException 
	 * @throws CanNotFoundNodeIDException 
	 */
	public TreeContent findNodeById(String id) throws FoundTreeNodeNotUniqueException, CanNotFoundNodeIDException {
		if(id == null) {
			return null;
		}
	
		Set<TreeContent> set = new HashSet<TreeContent>();
		
		findNodeById(id, rootContent, set);
		if(set.size() > 1) {
			throw new FoundTreeNodeNotUniqueException(id);
		} else if(set.size() == 0) {
			throw new CanNotFoundNodeIDException(id);
		}
		
		return set.toArray(new TreeContent[0])[0];
	}
	
	
	private void findNodeById(String id, TreeContent treeContent, Set<TreeContent> resultSet) {
		if(id == null) {
			return ;
		}
	
		if(id.equals(treeContent.getId())) {
			resultSet.add(treeContent);
		} 

		if(treeContent.getChildrenList().size() > 0){
			List<TreeContent> list = treeContent.getChildrenList();
			for(TreeContent childTreeContent : list) {
				findNodeById(id, childTreeContent, resultSet);
			}
		}
	}
	
	// A public method that allows listener registration
	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		if(!myListeners.contains(listener)) {
			myListeners.add(listener);
		}
	}
	
	// A public method that allows listener registration
	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		myListeners.remove(listener);
	}
	
	public Text getText() {
		return text;
	}

	public Tree getTree() {
		return tree;
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}
	
	public boolean isEmtpy() {
		return rootContent == null;
	}

	public TreeContent getRootContent() {
		return rootContent;
	}
	
}
