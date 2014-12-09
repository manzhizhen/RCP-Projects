package cn.sunline.suncard.sde.bs.ui.plugin.tree;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import cn.sunline.suncard.sde.bs.biz.BsPluginlogBiz;
import cn.sunline.suncard.sde.bs.entity.BsPlugin;
import cn.sunline.suncard.sde.bs.entity.BsPluginlog;
import cn.sunline.suncard.sde.bs.ui.plugin.patch.history.PatchMessageContent;
import cn.sunline.suncard.sde.bs.ui.plugin.patch.history.PatchMessageLabelProvider;
import cn.sunline.suncard.sde.bs.util.I18nUtil;


public class PluginTreePage extends Composite {
	private TreeViewer treeViewer = null;
	private Text treeText = null;
	private Table table = null;
	private TableViewer tableViewer = null;
	
	private BsPluginlogBiz bsPluginlogBiz = new BsPluginlogBiz();
	
	public PluginTreePage(Composite parent) {
		super(parent, SWT.NONE );
		setLayout(new FormLayout());
		createContents();
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	public Text getTreeText() {
		return treeText;
	}

	public void createContents() {
		FormData textData = new FormData();
		textData.right = new FormAttachment(100, -5);
		textData.top = new FormAttachment(0, 5);
		textData.left = new FormAttachment(0, 5);
		
		treeText = new Text(this, SWT.BORDER);
		treeText.setLayoutData(textData);
		
		createPluginTree();
	}

	public void createPluginTree() {
		layout(true, true);
		
		treeViewer = new TreeViewer(this, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER );
		
		FormData treeData = new FormData();
		treeData.top = new FormAttachment(treeText, 5);
		treeData.right = new FormAttachment(100, -5);
		treeData.left = new FormAttachment(0, 5);
		
		Tree tree = treeViewer.getTree();

		tree.setLayoutData(treeData);
		tree.setVisible(true);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		
		TreeColumn pluginName = new TreeColumn(tree, SWT.NONE);
		pluginName.setWidth(250);
		pluginName.setText(I18nUtil.getMessage("PLUGIN_NAME"));
		
		TreeColumn pluginId = new TreeColumn(tree, SWT.NONE);
		pluginId.setWidth(100);
		pluginId.setText(I18nUtil.getMessage("PLUGIN_ID"));

		TreeColumn pluginVersion = new TreeColumn(tree, SWT.NONE);
		pluginVersion.setWidth(100);
		pluginVersion.setText(I18nUtil.getMessage("PLUGIN_VERSION"));
		
		TreeColumn pluginInstallDate = new TreeColumn(tree, SWT.NONE);
		pluginInstallDate.setWidth(150);
		pluginInstallDate.setText(I18nUtil.getMessage("PLUGIN_INSTALL_DATE"));

		treeViewer.setContentProvider(new PluginTreeContentProvider());
		treeViewer.setInput(PluginTreeContent.output());
		treeViewer.setLabelProvider(new PluginTreeLabelProvider());
		treeViewer.expandAll();
		
		treeText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String textStr = treeText.getText();

				if (textStr == null) {
					return;
				}

				treeViewer
						.setFilters(new ViewerFilter[] { new PluginTreeFilter(
								textStr.trim()) });
				treeViewer.expandAll();
			}
		});
		
		FormData tableData = new FormData();
		tableData.right = new FormAttachment(100, -5);
		tableData.bottom = new FormAttachment(100, 0);
		tableData.top = new FormAttachment(80, 6);
		tableData.left = new FormAttachment(0, 5);
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(tableData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		treeData.bottom = new FormAttachment(table, -5);
		
		TableColumn patchId = new TableColumn(table, SWT.NONE);
		patchId.setWidth(150);
		patchId.setText(I18nUtil.getMessage("PATCH_ID"));
		
		TableColumn patchName = new TableColumn(table, SWT.NONE);
		patchName.setWidth(150);
		patchName.setText(I18nUtil.getMessage("PATCH_NAME"));
		
		TableColumn patchVersion = new TableColumn(table, SWT.NONE);
		patchVersion.setWidth(150);
		patchVersion.setText(I18nUtil.getMessage("PATCH_VERSION"));
		
		TableColumn patchInstallVersion = new TableColumn(table, SWT.NONE);
		patchInstallVersion.setWidth(150);
		patchInstallVersion.setText(I18nUtil.getMessage("PLUGIN_VERSION"));
		
		TableColumn installDate = new TableColumn(table, SWT.NONE);
		installDate.setWidth(120);
		installDate.setText(I18nUtil.getMessage("PATCH_INSTALL_DATE"));
		
		tableViewer = new TableViewer(table);
		tableViewer.setLabelProvider(new PatchMessageLabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());

	}
	
	// 添加内容到下面的补丁表格
	public void addContentToTable(List<PatchMessageContent> contentList, PluginTreeContent pluginTreeContent) {
//		if(contentList == null || contentList.size() == 0) {
//			return;
//		}
//		
//		Iterator<PatchMessageContent> iterator = contentList.iterator();
//		
//		while(iterator.hasNext()) {
//			PatchMessageContent patchMessageContent = iterator.next();
//			BsPatch bsPatch = patchMessageContent.getBsPatch();
//			
//			String patchId = bsPatch.getId().getPatchId();
//			String patchName = bsPatch.getPatchName();
//			String patchVersion = bsPatch.getPatchVer();
//			String plguinVersion = patchMessageContent.getBsPluginlog().getPluginVer();
//			String patchInstallDate = patchMessageContent.getBsPluginlog().getProcessDate().toString();
//			
//			TableItem tableItem = new TableItem(table, SWT.BORDER);	
//			tableItem.setText(new String[]{patchId, patchName, patchVersion,
//					plguinVersion, patchInstallDate});
//		}

		tableViewer.setInput(contentList);
		tableViewer.refresh();
		
		BsPlugin bsPluginTemp = pluginTreeContent.getBsPlugin();
		
		// 把插件的安装日期加进去
		BsPluginlog bsPluginlog = bsPluginlogBiz.findRecentlyInstallLog(bsPluginTemp);
		
		// 增加原始版本这条记录
		TableItem tableItemSrc = new TableItem(table, SWT.BORDER);
		tableItemSrc.setText(new String[]{bsPluginTemp.getId().getPluginId(), bsPluginTemp.getPluginName(),
				"", bsPluginlog.getPluginVer(), 
				bsPluginlog.getProcessDate().toString()});
		
	}

	// 清除表中的所有数据
	public void clearTable() {
//		table.clearAll();
		table.removeAll();
	}
	
	
	
	
	public TableViewer getTableViewer() {
		return tableViewer;
	}

	public static void main(String[] args) {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		// shell.setLayout(new FillLayout());

		TabFolder t = new TabFolder(shell, SWT.NONE);
		t.setLayout(new FillLayout());
		TabItem i = new TabItem(t, SWT.NONE);
		i.setText("aa");
		i.setControl(new PluginTreePage(t));
		// i.setControl(new Test(t));
		t.setBounds(20, 20, 500, 500);
		// new PluginTreePage(shell);

		shell.open();
		shell.setSize(400, 400);
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();
	}
}
