/* 文件名：     TreeViewPart.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	树模板的Composite
 * 修改人：     易振强
 * 修改时间：2011-10-28
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.tree.factory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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

import cn.sunline.suncard.sde.workflow.exception.CanNotFoundNodeIDException;




/**
 * 树模板的Composite
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 */

public class TreeViewComposite {
	private Text text;
	private Tree tree;
	private TreeViewer treeViewer;
	
	private ISelection selection;
	private List<IPropertyChangeListener> myListeners = new ArrayList<IPropertyChangeListener>();
	
	private List<TreeContent> containNeedDelNodeList;
	private TreeContent needDelNode;

	
	private TreeContent rootContent = null;
	
	public TreeViewComposite(Composite composite) {
		composite.setLayout(new FormLayout());
		
		text = new Text(composite, SWT.BORDER);
		FormData textData = new FormData();
		textData.right = new FormAttachment(100, -5);
		textData.top = new FormAttachment(0, 5);
		textData.left = new FormAttachment(0, 5);
		text.setLayoutData(textData);
		
		tree = new Tree(composite, SWT.BORDER);
		FormData treeData = new FormData();
		treeData.top = new FormAttachment(text, 5);
		treeData.left = new FormAttachment(0, 5);
		treeData.right = new FormAttachment(100, -5);
		treeData.bottom = new FormAttachment(100);
		tree.setLayoutData(treeData);
		
		tree.setVisible(true);
		treeViewer = new TreeViewer(tree);
		
		createTreeEvent();
		
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				textEvent();
			}
		});
		
		treeViewer.setLabelProvider(new TreeLabelProvider());
		treeViewer.setContentProvider(new TreeContentProvider());
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
	 */
	public void addNode(String parentId, String id, String nodeName, Object node, Image image) throws CanNotFoundNodeIDException {
		if(isEmtpy()) {
			return ;
		}
		
		TreeContent parentContent = findNodeById(parentId, rootContent);
		
		if(parentContent == null) {
			throw new CanNotFoundNodeIDException();
		}
		
		TreeContent treeContent = new TreeContent(id, nodeName, node, image);
		treeContent.setParent(parentContent);
		parentContent.getChildrenList().add(treeContent);

		treeViewer.refresh();
	}
	
	/**
	 * 添加一个子节点(使用和父节点一样的图标)
	 * 不是根节点的节点都称为子节点，添加子节点必须制定父节点ID或父节点对象
	 * @param  String 父节点的id
	 * @param  String 节点的id，一般值为绑定对象的id
	 * @param  String 数据对象在树节点中显示的名字
	 * @param  Object   数据对象
	 * @throws CanNotFoundNodeIDException 
	 */
	public void addNode(String parentId, String id, String nodeName, Object node) throws CanNotFoundNodeIDException {
		if(isEmtpy()) {
			return ;
		}
		
		TreeContent parentContent = findNodeById(parentId, rootContent);
		
		if(parentContent == null) {
			throw new CanNotFoundNodeIDException();
		}
		
		TreeContent treeContent = new TreeContent(id, nodeName, node, parentContent.getImage());
		treeContent.setParent(parentContent);
		parentContent.getChildrenList().add(treeContent);

		treeViewer.refresh();
	}
	
	/**
	 * 删除一个节点
	 * 根据传入对象的类型自动构造相应节点添加到树中并更新
	 * @param  String 要删除的节点的id
	 * @throws CanNotFoundNodeIDException 
	 */
	public void removeNode(String id) throws CanNotFoundNodeIDException {
		if(isEmtpy()) {
			return ;
		}
		
		TreeContent needDelContent = findNodeById(id);
		if(needDelContent == null) {
			throw new CanNotFoundNodeIDException();
		} else {
			// 说明要删除的是父节点，也就是删除整棵树
			if(needDelContent.getParent() == null) {
				rootContent = null;
				
			} else {
				needDelContent.getParent().getChildrenList().remove(needDelContent);
			}
		}
		
		
		treeViewer.refresh();
	}
	
	/**
	 * 更新一个节点（采用新图标）
	 * 根据传入对象的类型自动更新相应节点添加到树中并更新
	 * @param  String   需要更新的树节点ID
	 * @param  String   新数据对象在树节点上的显示名字
	 * @param  Object   需要更新的数据对象
	 * @throws CanNotFoundNodeIDException 
	 */
	public void updateNode(String id, String nodeName, Object node, Image image) throws CanNotFoundNodeIDException {
		if(isEmtpy()) {
			return ;
		}
		
		TreeContent modifyContent = findNodeById(id, rootContent);
		
		if(modifyContent == null) {
			throw new CanNotFoundNodeIDException();
		}
		
		modifyContent.setNodeName(nodeName);
		modifyContent.setObj(node);
		modifyContent.setImage(image);
		
		treeViewer.refresh();
	}
	
	/**
	 * 更新一个节点（默认采用原图标）
	 * 根据传入对象的类型自动更新相应节点添加到树中并更新
	 * @param  Object   需要更新的数据对象
	 * @param  String   新数据对象在树节点上的显示名字
	 * @throws CanNotFoundNodeIDException 
	 */
	public void updateNode(String id, String nodeName, Object node) throws CanNotFoundNodeIDException {
		if(isEmtpy()) {
			return ;
		}
		
		TreeContent modifyContent = findNodeById(id);
		
		if(modifyContent == null) {
			throw new CanNotFoundNodeIDException();
		}
		
		modifyContent.setNodeName(nodeName);
		modifyContent.setObj(node);
		if(modifyContent.getParent() != null) {
			modifyContent.setImage(modifyContent.getParent().getImage());
		}
		
		treeViewer.refresh();
	}
	
	
	/**
	 *  清空树的所有数据
	 *  清除掉所有数据，包括父节点
	 */
	public void removeAll() {
		if(rootContent != null) {
			rootContent.setChildrenList(new ArrayList<TreeContent>());
		}
	}
	
	/**
	 *  创建和更新树
	 * 	重新创建或刷新一棵树
	 */
	public void createTreeContent() {
		List<TreeContent> list = new ArrayList<TreeContent>();
		list.add(rootContent);
		treeViewer.setInput(list);
		treeViewer.refresh();
		
		treeViewer.expandAll();
	}
	
	/**
	 * 创建树的事件
	 */
	public void createTreeEvent() {
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				mouseDoubleEvent();
			}
			
			@Override
			public void mouseUp(MouseEvent e) {
			}
		});
		
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
			      ISelection selection2 = event.getSelection();
			      setSelection(selection2);

			      IStructuredSelection structuredSelection = (IStructuredSelection)selection;
			      Object obj = structuredSelection.getFirstElement();
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
	 */
	public TreeContent findNodeById(String id) {
		if(id == null) {
			return null;
		}
	
		return findNodeById(id, rootContent);
	}
	
	private TreeContent findNodeById(String id, TreeContent treeContent) {
		if(id == null) {
			return null;
		}
	
		if(id.equals(treeContent.getId())) {
			return treeContent;
		} 

		if(treeContent.getChildrenList().size() > 0){
			List<TreeContent> list = treeContent.getChildrenList();
			for(TreeContent childTreeContent : list) {
				if(id.equals(childTreeContent.getId())) {
					return childTreeContent;
				} else {
					TreeContent tempContent = findNodeById(id, childTreeContent);
					if(tempContent == null) {
						continue;
					}else {
						return tempContent;
					}
				} 
			}
			
			return null;
			
		} else {
			return null;
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

	public void setSelection(ISelection selection) {
		this.selection = selection;
	}

	public TreeContent getRootContent() {
		return rootContent;
	}
	
}
