/*
 * 文件名：     PluginManager.java
 * 版权：      	Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	插件管理类
 * 修改人：     易振强
 * 修改时间：2011-9-21
 * 修改内容：创建
 */
package cn.sunline.suncard.sde.bs.ui.plugin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.dom4j.DocumentException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.ui.dailogs.LoginDialog;
import cn.sunline.suncard.sde.bs.ui.permission.WidgetPermission;
import cn.sunline.suncard.sde.bs.ui.plugin.PluginAdd;
import cn.sunline.suncard.sde.bs.biz.BsPatchBiz;
import cn.sunline.suncard.sde.bs.biz.BsPluginBiz;
import cn.sunline.suncard.sde.bs.biz.BsPluginlogBiz;
import cn.sunline.suncard.sde.bs.biz.BsWidgetBiz;
import cn.sunline.suncard.sde.bs.entity.BsPatch;
import cn.sunline.suncard.sde.bs.entity.BsPlugin;
import cn.sunline.suncard.sde.bs.entity.BsPluginlog;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.ui.plugin.gallery.PluginGalleryPage;
import cn.sunline.suncard.sde.bs.ui.plugin.patch.PatchManager;
import cn.sunline.suncard.sde.bs.ui.plugin.patch.history.PatchMessageContent;
import cn.sunline.suncard.sde.bs.ui.plugin.tools.ControlXMLAnalysis;
import cn.sunline.suncard.sde.bs.ui.plugin.tools.FileDeal;
import cn.sunline.suncard.sde.bs.ui.plugin.tree.PluginTreeContent;
import cn.sunline.suncard.sde.bs.ui.plugin.tree.PluginTreePage;
import cn.sunline.suncard.sde.bs.ui.resource.IAppconstans;
import cn.sunline.suncard.sde.bs.ui.resource.IWidgetId;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.bs.util.WidgetPermissionUtil;

/**
 * 插件管理类 可以对插件进行添加、刪除和查找等 操纵插件的各种状态
 * 
 * @author 易振强
 * @version 1.0, 2011-9-21
 * @see
 * @since 1.0
 */
public class PluginManager extends TrayDialog {
	
	// 主窗口的Shell对象
	private Shell shell;
	// private final static String ID = "PluginManager.ID";

	// 补丁管理对象
	private PatchManager patchManager = null;

	// 项目插件文件存放的位置
	public final static String PLUGIN_ROOT_PATH = "suncard/";
	// // 项目配置文件（config.ini）的相对位置
	// public final static String CONFIGINI_PATH = "configuration/config.ini";
	// 项目link文件存放位置
	public final static String LINK_FILE = "links/";

	// 文件夹选项卡
	private CTabFolder folder;

	// 树视图选项卡标签
	private CTabItem treeItem = null;

	//Gallery视图选项卡标签
	private CTabItem galleryItem = null;
	// private String lastSelectedTabId = null;

	// 对话框的宽度
	private final static int DIALOG_WIDTH = 770;

	// 对话框的高度
	private final static int DIALOG_HEIGHT = 700;

	// 插件树标签下的Page
	private PluginTreePage pluginTreePage = null;

	// Gallery标签下的Page
	private PluginGalleryPage pluginGalleryPage = null;
 
	// 插件树内容对象，用于提供给插件列表显示
	private PluginTreeContent pluginTreeContent = null;

	// 插件树标签下的TableViewer
	private TreeViewer pluginTreeViewer = null;

	// Gallery标签下的显示空间
	private Gallery gallery = null;

	// 当前选择标签下补丁历史表的TableViewer
	private TableViewer selectionTableViewer = null;
	// private Text treeText = null;

	// 插件添加对象
	private PluginAdd pluginAdd = null;

	// 插件的Biz
	private BsPluginBiz bsPluginBiz = new BsPluginBiz();

	// 补丁Biz
	private BsPatchBiz bsPatchBiz = new BsPatchBiz();

	// 插件日志Biz
	private BsPluginlogBiz bsPluginlogBiz = new BsPluginlogBiz();
	// private BsWidgetBiz bsWidgetBiz = new BsWidgetBiz();

	// 按钮组的一个Id，便于以后的扩展
	public final static String button1Index = "1";

	// 按钮组的Map
	public Map<String, List<Button>> buttonMap = new HashMap<String, 
			List<Button>>();

	// 按钮Id
	private final static int BUTTON_PATCH_INSTALL_ID = 3001;
	private final static int BUTTON_PLUGIN_INSTALL_ID = 3002;
//	private final static int BUTTON_START_ID = 3003;
//	private final static int BUTTON_STOP_ID = 3004;
	private final static int BUTTON_UNINSTALL_ID = 3005;
	
	// 添加日志
	public static Log logger = LogManager.getLogger(PluginManager.class.getName());

	// 文件夹标签的标记，默认第一次显示树视图标签
	private String tabFlag = "tree";

	// 用于存储补丁历史记录表里面的数据
	private List<PatchMessageContent> contentList;
	
	// 控件配置文件名
	private final static String WIDGET_XML_NAME = "widgets.xml";

	public PluginManager(Shell shell) {
		super(shell);
		patchManager = new PatchManager(shell);
		this.shell = shell;
		
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		// 设置对话框的标题
		shell.setText(I18nUtil.getMessage("PLUGIN_MANAGE"));
		Image pluginManagerImage = CacheImage.getCacheImage().getImage(
				IAppconstans.APPLICATION_ID, "icons/pluginManager.png");
		shell.setImage(pluginManagerImage);
	}

	public CTabFolder getFolder() {
		return folder;
	}

	/**
	 * 设置对话框大小
	 */
	protected Point getInitialSize() {
		return new Point(DIALOG_WIDTH, DIALOG_HEIGHT);
	}

	/**
	 * 设置初始的位置
	 */
	protected Point getInitialLocation(Point initialSize) {
		return super.getInitialLocation(initialSize);
	}

	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);

		// // 设置最开始选择的标签
		// if (folder.getItemCount() > 0) {
		// // 如果以前有过选择，则保持以前的选择
		// if (lastSelectedTabId != null) {
		// CTabItem[] items = folder.getItems();
		// for (int i = 0; i < items.length; i++)
		// if (items[i].getData(ID).equals(lastSelectedTabId)) {
		// folder.setSelection(i);
		// tabSelected(items[i]);
		//
		// break;
		// }
		// } else {// 如果没有，则默认选择第一个标签
		// folder.setSelection(0);
		// tabSelected(folder.getItem(0));
		// }
		// }

		Dialog.applyDialogFont(folder);

		return control;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		composite.setLayout(new FillLayout(SWT.HORIZONTAL));

		// 创建选项卡（标签页面）部件。
		folder = new CTabFolder(composite, SWT.NONE | SWT.BORDER);

		// 设置标签栏的高度
		folder.setTabHeight(30);
		folder.marginHeight = 2;
		folder.marginWidth = 2;
		folder.setMaximizeVisible(true);
		folder.setMinimizeVisible(true);

		// 设置圆角
		folder.setSimple(false);

		// 创建标签页
		createFolderItems(folder);

		// 添加选择事件
		folder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tabSelected((CTabItem) e.item);
			}
		});

		return composite;
	}

	// 创建按钮条
	protected void createButtonsForButtonBar(Composite parent) {
		// super.createButtonsForButtonBar(parent);
		// 按钮List，所有按钮都存在其中，便于管理。
		List<Button> buttonList = new ArrayList<Button>();

		// 补丁安装按钮
		Button buttonPatchInstall = createButton(parent,
				BUTTON_PATCH_INSTALL_ID, I18nUtil.getMessage("PATCH_INSTALL"),
				true);
		buttonPatchInstall.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				patchManager.installPatch();
				refreshPlguinTree();
				refreshPlguinGallery();
			}
		});
		buttonList.add(buttonPatchInstall);

		// 插件安装按钮
		Button buttonPluginInstall = createButton(parent,
				BUTTON_PLUGIN_INSTALL_ID,
				I18nUtil.getMessage("PLUGIN_INSTALL"), true);
//		buttonPluginInstall.setEnabled(WidgetPermissionUtil
//				.checkPermission(IWidgetId.SDE_B_PLUGIN_PLUGIN_INSTALL));
		buttonPluginInstall.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				installPlugin();
				refreshPlguinTree();
				refreshPlguinGallery();
			}
		});
		buttonList.add(buttonPluginInstall);

//		// 开启按钮
//		Button buttonStart = createButton(parent, BUTTON_START_ID,
//				I18nUtil.getMessage("PLUGIN_START"), false);
////		buttonStart.setEnabled(WidgetPermissionUtil
////				.checkPermission(IWidgetId.SDE_B_PLUGUN_START));
//		buttonStart.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				startPlugin();
//				refreshPlguinTree();
//				refreshPlguinGallery();
//			}
//		});
//		buttonStart.setEnabled(false);
//		buttonList.add(buttonStart);
//
//		// 关闭按钮
//		Button buttonStop = createButton(parent, BUTTON_STOP_ID,
//				I18nUtil.getMessage("PLUGIN_STOP"), false);
//		buttonStop.setEnabled(WidgetPermissionUtil
//				.checkPermission(IWidgetId.SDE_B_PLUGUN_CLOSE));
//		buttonStop.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				stopPlugin();
//				refreshPlguinTree();
//				refreshPlguinGallery();
//			}
//		});
//		buttonStop.setEnabled(false);
//		buttonList.add(buttonStop);

		// 拆卸按钮
		Button buttonUninstall = createButton(parent, BUTTON_UNINSTALL_ID,
				I18nUtil.getMessage("PLUGIN_UNINSTALL"), false);
		buttonUninstall.setEnabled(WidgetPermissionUtil
				.checkPermission(IWidgetId.SDE_B_PLUGUN_UNINSTALL));
		buttonUninstall.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!MessageDialog.openConfirm(shell,
						I18nUtil.getMessage("PLUGIN_PROMPT"),
						I18nUtil.getMessage("PLUGIN_UNINSTALL_MESSAGE"))) {
					
					// 写日志
					logger.info("用户取消拆卸插件！");
					return;
				}

				try {
					uninstallPlugin();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				refreshPlguinTree();
				refreshPlguinGallery();
				clearTableData();
			}
		});
		buttonUninstall.setEnabled(false);
		buttonList.add(buttonUninstall);

		// 退出按钮
		Button quitButton = createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("PLUGIN_DIALOG_CANCEL"), false);
//		quitButton.setEnabled(WidgetPermissionUtil
//				.checkPermission(IWidgetId.SDE_B_PLUGUN_QUIT));

		// 把该按钮组添加到Map。
		buttonMap.put(button1Index, buttonList);
	}

	/**
	 * 创建文件夹项目
	 * 
	 * @param folder
	 */
	public void createFolderItems(CTabFolder folder) {
		treeItem = new CTabItem(folder, SWT.NONE);
		treeItem.setText(I18nUtil.getMessage("TREE_VIEW"));
		Image treeImage = CacheImage.getCacheImage().getImage(
				IAppconstans.APPLICATION_ID, "icons/treeViewItem.png");
		treeItem.setImage(treeImage);

		pluginTreePage = new PluginTreePage(folder);
		treeItem.setControl(pluginTreePage);

		galleryItem = new CTabItem(folder, SWT.NONE);
		galleryItem.setText(I18nUtil.getMessage("ICON_VIEW"));
		Image gallleryImage = CacheImage.getCacheImage().getImage(
				IAppconstans.APPLICATION_ID, "icons/galleryViewItem.png");
		galleryItem.setImage(gallleryImage);

		pluginGalleryPage = new PluginGalleryPage(folder);
		pluginGalleryPage.setButtonMap(buttonMap);
		gallery = pluginGalleryPage.getGallery();
		galleryItem.setControl(pluginGalleryPage);

		// 给插件管理绑定树视图
		pluginTreeViewer = pluginTreePage.getTreeViewer();

		// 创建插件树的监听程序
		createPluginTreeEvent();

		// 创建插件Gallery的监听程序
		createPluginGalleryEvent();
	}

	/**
	 * 刷新整课树,并展开所有节点.
	 */
	public void refreshPlguinTree() {
		pluginTreeViewer.setInput(PluginTreeContent.output());
		pluginTreeViewer.expandAll();
	}

	/**
	 * 刷新整个Gallery，并展开所有节点。
	 */
	public void refreshPlguinGallery() {
		pluginGalleryPage.clearAllGalleryItem();
		pluginGalleryPage.createGalleryGroups();
		pluginGalleryPage.initContentList();
		pluginGalleryPage.clearSuperfluousItem();
		// pluginGalleryPage.expandAll();
	}

	/**
	 * 当某一选项标签被选择时，更新标签标记和标签的当前补丁历史表的TableViewer
	 * 
	 * @param tabItem
	 */
	public void tabSelected(CTabItem tabItem) {
		if (I18nUtil.getMessage("TREE_VIEW").equals(tabItem.getText())) {
			tabFlag = "tree";
			selectionTableViewer = pluginTreePage.getTableViewer();
		} else {
			tabFlag = "gallery";
			selectionTableViewer = pluginGalleryPage.getTableViewer();
		}
	}

//	/**
//	 * 开启插件
//	 */
//	private void startPlugin() {
//		Bundle bundle = pluginTreeContent.getBundle();
//		try {
//			bundle.start();
//		} catch (BundleException e) {
//			MessageDialog.openError(shell, I18nUtil.getMessage("PLUGIN_ERROR"),
//					I18nUtil.getMessage("PLUGIN_START_FAIL"));
//			e.printStackTrace();
//
//		}
//	}
//
//	/**
//	 * 关闭插件
//	 */
//	private void stopPlugin() {
//		Bundle bundle = pluginTreeContent.getBundle();
//		try {
//			bundle.stop();
//
//		} catch (BundleException e) {
//			MessageDialog.openError(shell, I18nUtil.getMessage("PLUGIN_ERROR"),
//					I18nUtil.getMessage("PLUGIN_STOP_FAIL"));
//			e.printStackTrace();
//
//		}
//	}

	/**
	 * 安装插件
	 */
	private void installPlugin() {
		logger.info("private void installPlugin()...");
		
		pluginAdd = new PluginAdd(shell);

		// 如果返回值为1，则表示用户取消安装。
		pluginAdd.installPlugin();
	}

	/**
	 * 拆卸插件
	 * 
	 * @throws IOException
	 */
	private void uninstallPlugin() throws IOException {
		logger.info("private void uninstallPlugin() throws IOException...");
		
		try {
			bsPluginBiz.uninstallPlugin(this);
		} catch (BundleException e) {
			MessageDialog.openError(shell, I18nUtil.getMessage("PLUGIN_ERROR"),
					I18nUtil.getMessage("PLUGIN_UNINSTALL_FAIL"));
			e.printStackTrace();
			
			// 写日志
			logger.error(I18nUtil.getMessage("PLUGIN_UNINSTALL_FAIL") + " -- " + e.getMessage());
		}
		
		initTableData();
	}

	/**
	 * 创建插件树的监听器。
	 */
	private void createPluginTreeEvent() {
		pluginTreeViewer.getTree().addSelectionListener(
				new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						pluginTreeSelection();
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});

		pluginTreePage.getTableViewer().addDoubleClickListener(
				new IDoubleClickListener() {
					@Override
					public void doubleClick(DoubleClickEvent event) {
						try {
							patchHistoryDouble();
						} catch (Exception e) {
							logger.info(e.getMessage());
							e.printStackTrace();
						}
					}
				});

//		 // 鼠标右键菜单
//		 pluginTreeViewer.getTree().addMouseListener(new MouseListener() {
//		 @Override
//		 public void mouseDoubleClick(MouseEvent e) {
//
//		 }
//		
//		 @Override
//		 public void mouseDown(MouseEvent e) {
//			 //如果是鼠标右键，则为树创建一个右键菜单。
//			 if(e.button == 3){
//			 initSelectionPluginTreeContent();
//			
//			 patchManager.setPluginTreeContent(pluginTreeContent);
//			 patchManager.createMouseRightMenu();
//			 }
//		 }
//		
//		 @Override
//		 public void mouseUp(MouseEvent e) {
//		 }
//		
//		 });
	}

	/**
	 * 创建插件Gallery的监听器。
	 */
	private void createPluginGalleryEvent() {
		gallery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				pluginTreeSelection();
			}
		});

		pluginGalleryPage.getTableViewer().addDoubleClickListener(
				new IDoubleClickListener() {
					@Override
					public void doubleClick(DoubleClickEvent event) {
						try {
							patchHistoryDouble();
						} catch (DocumentException e) {
							e.printStackTrace();
							logger.info(e.getMessage());
						}
					}
				});

		// // 鼠标右键菜单
		// gallery.addMouseListener(new MouseListener() {
		// @Override
		// public void mouseDoubleClick(MouseEvent e) {
		// //如果是鼠标右键，则为树创建一个右键菜单。
		// if(e.button == 3){
		// initSelectionPluginTreeContent();
		//
		// patchManager.setPluginTreeContent(pluginTreeContent);
		// patchManager.createMouseRightMenu();
		// }
		// }
		//
		// @Override
		// public void mouseDown(MouseEvent e) {
		// }
		//
		// @Override
		// public void mouseUp(MouseEvent e) {
		// }
		//
		// });
	}

	/**
	 * 当插件树的行被选择时的处理方法，包括设置按钮状态和初始化选择的插件对象。
	 */
	public void pluginTreeSelection() {
		initSelectionPluginTreeContent();

		clearTableData();

		if (pluginTreeContent == null || pluginTreeContent.parent == null) {
			setButtonsState(button1Index, "NULL");

			return;
		} else {
			setButtonsState(button1Index, pluginTreeContent.parent.toString());

			initTableData();
		}
	}

	/**
	 * 当补丁历史记录表被双击后的处理方法
	 * @throws DocumentException 
	 */
	public void patchHistoryDouble() throws DocumentException {
		initSelectionPluginTreeContent();

		if (pluginTreeContent == null || pluginTreeContent.parent == null) {
			setButtonsState(button1Index, "NULL");

			return;
		}

		int selectionIndex = selectionTableViewer.getTable()
				.getSelectionIndex();
		
		if (-1 == selectionIndex) {
			return;
		}
		
		if(0 == selectionIndex) {
			MessageDialog.openInformation(shell,
					I18nUtil.getMessage("PATCH_PROMPT"),
					I18nUtil.getMessage("PLUGIN_NOW_VERSION"));
			return;
		}

		if (!MessageDialog.openConfirm(shell,
				I18nUtil.getMessage("PATCH_PROMPT"),
				I18nUtil.getMessage("PATCH_BACK_QUESTION"))) {
			return;
		}

		String newVersion = null;
		try {
			newVersion = bsPatchBiz.backToPlugin(selectionTableViewer,
					pluginTreeContent.getBundle());
		} catch (IOException e) {
			MessageDialog.openError(shell, I18nUtil.getMessage("PATCH_ERROR"),
					I18nUtil.getMessage("PATCH_BACK_FAIL"));
			e.printStackTrace();
		}

		if (newVersion != null) {
			MessageDialog.openInformation(shell,
					I18nUtil.getMessage("PLUGIN_MESSAGE"),
					I18nUtil.getMessage("PATCH_BACK_SUCCESS") + " "
							+ newVersion);

			clearTableData();
			initTableData();
		}
	}

	/**
	 * 清除补丁安装历史表中的数据
	 */
	public void clearTableData() {
		if ("tree".equals(tabFlag)) {
			pluginTreePage.clearTable();
		} else {
			pluginGalleryPage.clearTable();
		}

	}

	/**
	 * 初始化补丁安装历史表中的数据
	 */
	public void initTableData() {
		initSelectionPluginTreeContent();
		Bundle bundle = pluginTreeContent.getBundle();
		if (bundle == null) {
			return;
		}

		// 通过插件名称找到对应的插件ID
		BsPlugin plugin = pluginTreeContent.getBsPlugin();

		// 如果数据库没有此记录，则说明是基础框架自身的插件
		if (plugin == null) {
			return;
		} else {
			BsPatch[] patchArray = bsPatchBiz.findByPluginId(
					plugin.getId().getPluginId()).toArray(new BsPatch[0]);
			if (patchArray == null || 0 == patchArray.length) {
				return;
			}

			String pluginId = plugin.getId().getPluginId();
			contentList = new ArrayList<PatchMessageContent>();
			// 遍历补丁数组，初始化表信息。
			for (BsPatch patch : patchArray) {
				// 通过插件Id和补丁编号找到对应的日志记录
				BsPluginlog bsPluginlog = bsPluginlogBiz.findByPluginPatchId(
						patch.getId().getPatchId(), pluginId);

				contentList.add(new PatchMessageContent(patch, bsPluginlog));
			}

			if ("tree".equals(tabFlag)) {
				pluginTreePage
						.addContentToTable(contentList, pluginTreeContent);
			} else {
				pluginGalleryPage.addContentToTable(contentList,
						pluginTreeContent);
			}
		}
	}

	/**
	 * 重新设置所选择的PluginTreeContent对象
	 * 
	 * @return
	 */
	public PluginTreeContent initSelectionPluginTreeContent() {
		pluginTreeContent = null;

		if ("tree".equals(tabFlag)) {
			ISelection selection = pluginTreeViewer.getSelection();
			if (selection instanceof IStructuredSelection) {
				pluginTreeContent = (PluginTreeContent) ((IStructuredSelection) selection)
						.getFirstElement();
			}

			return pluginTreeContent;
		} else {
			GalleryItem[] galleryItems = gallery.getSelection();
			if (galleryItems == null || galleryItems.length == 0) {
				return null;
			}

			pluginTreeContent = (PluginTreeContent) galleryItems[0].getData();

			return pluginTreeContent;
		}
	}

	/**
	 * 把高于还原版本的控件信息从数据库删除。
	 * 
	 * @param version
	 * @param bundle
	 * @throws IOException
	 * @throws DocumentException 
	 */
	public static void deleteWidgetFromDb(String version, Bundle bundle)
			throws IOException, DocumentException {

		if (bundle == null) {
			return;
		}

		File curJarFile = FileLocator.getBundleFile(bundle);
		JarFile jarFile = new JarFile(curJarFile);

		byte[] bytes = new byte[FileDeal.BUFFER_SIZE];
		int count = 0;
		Enumeration emu = jarFile.entries();
		while (emu.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) emu.nextElement();
			if (entry.getName().endsWith(WIDGET_XML_NAME)) {
				BufferedInputStream bufferedInputStream = new BufferedInputStream(
						jarFile.getInputStream(entry));
				count = bufferedInputStream
						.read(bytes, 0, FileDeal.BUFFER_SIZE);
			}
		}

		ControlXMLAnalysis controlXMLAnalysisTemp = new ControlXMLAnalysis(bytes);

		controlXMLAnalysisTemp.initControlList();
		List<String> versionList = controlXMLAnalysisTemp
				.getWidgetVersionList();

		int index = 0;
		BsWidgetBiz bsWidgetBizTemp = new BsWidgetBiz();
		for (String widgetVersion : versionList) {
			if (new Version(widgetVersion).compareTo(new Version(version)) > 0) {
				// 写日志
				logger.info("删除高于还原版本的控件信息！");
				
				bsWidgetBizTemp.delete(controlXMLAnalysisTemp.getList().get(
						index));
			}

			index++;
		}
	}

	/**
	 * 移动备份文件到插件所在处，覆盖原文件。
	 * 
	 * @param bsPluginlog
	 * @param curJarFile
	 * @throws IOException
	 */
	public static void moveJarToJar(BsPluginlog bsPluginlog, File curJarFile)
			throws IOException {
		if (bsPluginlog == null) {
			return;
		}

		String path = bsPluginlog.getReplaceUrl();
		if (path == null || "".equals(path.trim())) {
			return;
		}

		File backFile = new File(path);
		if (!backFile.exists() || !backFile.isFile()) {
			// 写日志
			logger.error("移动备份文件到插件所在处，覆盖原文件时备份文件不存在！" +
			" -- " + new FileNotFoundException().getMessage());
			throw new FileNotFoundException();
			
		}

		// 移动文件到指定目录
		FileDeal.moveFileToPath(backFile.getAbsolutePath(),
				curJarFile.getAbsolutePath());
		
		// 写日志
		logger.info("移动备份文件到指定目录成功！");

	}

	// 通过Id值来修改对应的按钮列表的状态。
	public void setButtonsState(String id, String param) {
		List<Button> buttonList = buttonMap.get(id);

		if ("ACTIVE".equals(param)) {
//			buttonList.get(2).setEnabled(false);
//			buttonList.get(3).setEnabled(true);
//			buttonList.get(4).setEnabled(true);
			
			buttonList.get(2).setEnabled(true);

		} else if ("RESOLVED".equals(param) || "INSTALL".equals(param)
				|| "STARTING".equals(param)) {
//			buttonList.get(2).setEnabled(true);
//			buttonList.get(3).setEnabled(false);
//			buttonList.get(4).setEnabled(true);
			
			buttonList.get(2).setEnabled(true);		

		} else if ("NULL".equals(param)) {
//			buttonList.get(2).setEnabled(false);
//			buttonList.get(3).setEnabled(false);
//			buttonList.get(4).setEnabled(false);
			
			buttonList.get(2).setEnabled(false);	

		} else {
//			buttonList.get(2).setEnabled(true);
//			buttonList.get(3).setEnabled(true);
//			buttonList.get(4).setEnabled(true);
			
			buttonList.get(2).setEnabled(true);
		}
	}

	public PluginTreeContent getPluginTreeContent() {
		return pluginTreeContent;
	}

	public static void main(String[] args) throws Exception {

		Display display = Display.getDefault();
		Shell shell = new Shell(display);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
