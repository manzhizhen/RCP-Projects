package cn.sunline.suncard.sde.bs.ui.plugin.patch;

import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.osgi.framework.BundleException;

import cn.sunline.suncard.sde.bs.biz.BsPatchBiz;
import cn.sunline.suncard.sde.bs.entity.BsPatch;
import cn.sunline.suncard.sde.bs.entity.BsPlugin;
import cn.sunline.suncard.sde.bs.ui.plugin.PluginAdd;
import cn.sunline.suncard.sde.bs.ui.plugin.tree.PluginTreeContent;
import cn.sunline.suncard.sde.bs.ui.plugin.tree.PluginTreePage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

public class PatchManager {
	private Shell shell;
	private PluginTreeContent pluginTreeContent = null;
	//设定插件的备份位置。
	public final static String REPLACE_URL = "backup/";
	
//	private final static int BUTTON_INSTALL = 4001;
//	private final static int BUTTON_COMEBACK = 4002;	
//	private Button buttonInstall;
//	private Button buttonComeback;
	
	private PatchAdd patchAdd = null;
//	private PatchMessageDialog patchMessageDialog = null;
	
//	private TreeViewer pluginTreeViewer = null;
	
//	private BsPatchBiz bsPatchBiz = new BsPatchBiz();
	
	public PatchManager(Shell shell) {
		this.shell = shell;
	}
	
//	public void setPluginTreeViewer(TreeViewer pluginTreeViewer) {
//		this.pluginTreeViewer = pluginTreeViewer;
//	}
	
	public void setPluginTreeContent(PluginTreeContent pluginTreeContent) {
		this.pluginTreeContent = pluginTreeContent;
	}

	
//	@Override
//	protected void configureShell(Shell newShell) {
//		super.configureShell(newShell);
//		newShell.setText(I18nUtil.getMessage("PATCH"));
//	}
	
//	@Override
//	protected Control createDialogArea(Composite parent) {
//		Composite container = (Composite) super.createDialogArea(parent);
//		container.setLayout(new GridLayout(1, false));
//		
//		pluginTreePage = new PluginTreePage(container);
//		pluginTreeViewer = pluginTreePage.getTreeViewer();
//		
////		createMenu(pluginTreeViewer.getTree());
//		createPluginTreeEvent();
//		
//		return container;
//	}
	


//	// 创建树的监听事件。
//	public void createPluginTreeEvent() {
//		pluginTreeViewer
//		.addSelectionChangedListener(new ISelectionChangedListener() {
//			@Override
//			public void selectionChanged(SelectionChangedEvent event) {
//				pluginTreeSelection(event);
//			}
//		});
//		
//		TreeViewer t = pluginTreePage.getTreeViewer();
//		t.getTree().addMouseListener(new MouseListener() {
//			@Override
//			public void mouseUp(MouseEvent e) {
//				//如果是鼠标右键，则为树创建一个右键菜单。
//				if(e.button == 3 && buttonComeback.getEnabled()){
//					createMenu(pluginTreeViewer.getTree());
//				}
//			}
//
//			@Override
//			public void mouseDoubleClick(MouseEvent e) {
//			}
//
//			@Override
//			public void mouseDown(MouseEvent e) {
//			}
//		});
//	}
	
//	// 当插件树的行被选择时的处理方法
//	public void pluginTreeSelection(SelectionChangedEvent event) {
//		ISelection selection = pluginTreeViewer.getSelection();
//		if (selection instanceof IStructuredSelection) {
//			pluginTreeContent = (PluginTreeContent) ((IStructuredSelection) selection)
//					.getFirstElement();
//		}
//
//		if (pluginTreeContent == null || pluginTreeContent.parent == null) {
//			buttonComeback.setEnabled(false);
//			return;
//		} else {
//			Bundle bundle = pluginTreeContent.getBundle();
//			List<BsPatch> patchList = bsPatchBiz.findByPluginName(bundle.getHeaders().get("Bundle-Name"));
//			
//			// 如果该插件没安装过补丁，则“恢 复”按钮为不可用，否则为可用
//			if(patchList == null || patchList.size() == 0) {
//				buttonComeback.setEnabled(false);
//			}else {
//				buttonComeback.setEnabled(true);
//			}
//		}
//		
//		
//	}
	


//	//给树添加右键菜单
//	public void createMouseRightMenu() {
//		MenuManager menuManager = new MenuManager();
//		
//		if (pluginTreeContent == null || pluginTreeContent.parent == null) {
//			return;
//		}
//		
//		// 补丁信息的菜单条目
//		patchMessageDialog = new PatchMessageDialog(shell, pluginTreeContent);
//		Action patchMessageAction = new Action(I18nUtil.getMessage("PATCH_MESSAGE")) {
//				@Override
//				public void run() {
//					patchMessageDialog.open();
//				}
//
//				@Override
//				public boolean isEnabled() {
//					return  patchMessageDialog.canOpen();
//				}
//			};
			
//		Action patchComebackAction = new Action(I18nUtil.getMessage("PATCH_RETURN_TO")) {
//			@Override
//			public void run() {
//				patchComeback();
//			}
//
//			@Override
//			public boolean isEnabled() {
//				return  patchMessageDialog.canOpen();
//			}
//		};
			
//		menuManager.add(patchMessageAction);
//		
//		Tree tree = pluginTreeViewer.getTree();
//		Menu menu = menuManager.createContextMenu(tree);
//		tree.setMenu(menu);
//	}
	
//	@Override
//	protected void createButtonsForButtonBar(Composite parent) {
////		super.createButtonsForButtonBar(parent);
//		buttonInstall = createButton(parent, BUTTON_INSTALL, I18nUtil.getMessage("PATCH_INSTALL"), true);
//		buttonInstall.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				installPatch();
//			}
//		});
//		
//		buttonComeback = createButton(parent, BUTTON_INSTALL, I18nUtil.getMessage("PATCH_RETURN_TO"), true);
//		buttonComeback.setEnabled(false);
//		buttonComeback.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				patchComeback();
//				super.widgetSelected(e);
//			}
//		});
//
//		
//		createButton(parent, IDialogConstants.CANCEL_ID, I18nUtil.getMessage("PATCH_EXIT"), false);
//	}
	
	// 安装补丁
	public void installPatch() {
		int flag = 0;
		patchAdd = new PatchAdd(shell);
		
		// 调用PatchAdd类来安装补丁
		patchAdd.installPatch();
	}
//	
//	// 插件恢复
//	public void patchComeback() {
//		if (pluginTreeContent == null || pluginTreeContent.parent == null) {
//			return;
//		}
//		
//		patchMessageDialog = new PatchMessageDialog(shell, pluginTreeContent);
//		
//		if(patchMessageDialog.canOpen()) {
//			patchMessageDialog.open();
//		}
//	}
	
}
