/*
 * 文件名：
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：heyong
 * 修改时间：2011-10-17
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.plugin.gallery;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.util.Iterator;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.imageio.ImageIO;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.nebula.jface.galleryviewer.GalleryTreeViewer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import cn.sunline.suncard.sde.bs.Activator;
import cn.sunline.suncard.sde.bs.biz.BsPluginlogBiz;
import cn.sunline.suncard.sde.bs.entity.BsPatch;
import cn.sunline.suncard.sde.bs.entity.BsPlugin;
import cn.sunline.suncard.sde.bs.entity.BsPluginlog;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.ui.plugin.PluginManager;
import cn.sunline.suncard.sde.bs.ui.plugin.patch.history.PatchMessageContent;
import cn.sunline.suncard.sde.bs.ui.plugin.patch.history.PatchMessageLabelProvider;
import cn.sunline.suncard.sde.bs.ui.plugin.tools.FileDeal;
import cn.sunline.suncard.sde.bs.ui.plugin.tools.XMLAnalysis;
import cn.sunline.suncard.sde.bs.ui.plugin.tree.PluginTreeContent;
import cn.sunline.suncard.sde.bs.ui.plugin.tree.PluginTreeContentProvider;
import cn.sunline.suncard.sde.bs.ui.plugin.tree.PluginTreeLabelProvider;
import cn.sunline.suncard.sde.bs.ui.resource.IAppconstans;
import cn.sunline.suncard.sde.bs.ui.resource.IImageKey;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.osgi.framework.Bundle;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class PluginGalleryPage extends Composite {
	private GalleryTreeViewer galleryTreeViewer;
	private Text treeText = null;

	private Gallery gallery;

	private GalleryItem active;
	private GalleryItem installed;
	private GalleryItem resolved;
	private GalleryItem starting;
	private GalleryItem other;

	private PluginTreeContent pluginTreeContent = null;
	private Table table;
	private TableViewer tableViewer;

	private List<Bundle> bundleList = null;
	private static Image activeImg = CacheImage.getCacheImage().getImage(
			IAppconstans.APPLICATION_ID, IImageKey.ACTIVE_IMAGE);
	private static Image installedImg = CacheImage.getCacheImage().getImage(
			IAppconstans.APPLICATION_ID, IImageKey.INSTALL_IMAGE);
	private static Image resolvedImg = CacheImage.getCacheImage().getImage(
			IAppconstans.APPLICATION_ID, IImageKey.RESOLVED_IMAGE);
	private static Image startingImg = CacheImage.getCacheImage().getImage(
			IAppconstans.APPLICATION_ID, IImageKey.STARTING_IMAGE);
	private static Image otherImg = CacheImage.getCacheImage().getImage(
			IAppconstans.APPLICATION_ID, IImageKey.OTHERS_IMAGE);

	private List<PluginTreeContent> pluginTreeContentList;
	public final static String button1Index = PluginManager.button1Index;
	public Map<String, List<Button>> buttonMap = new HashMap<String, List<Button>>();

	private BsPluginlogBiz bsPluginlogBiz = new BsPluginlogBiz();
	private final static String PLUGIN_XML_NAME = "plugin.xml";
	
	
	public PluginGalleryPage(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new FormLayout());
		createContents();
	}

	public void setButtonMap(Map<String, List<Button>> buttonMap) {
		this.buttonMap = buttonMap;
	}

	public Gallery getGallery() {
		return gallery;
	}

	public void createContents() {
//		treeText = new Text(this, SWT.BORDER);
//		treeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
//				1, 1));

		createGallery();
		// createTable();
	}

	public void createGallery() {
		gallery = new Gallery(this, SWT.V_SCROLL | SWT.VIRTUAL);
		gallery.setVertical(true);
		
		FormData galleryData = new FormData();
		galleryData.right = new FormAttachment(100, -5);
		galleryData.top = new FormAttachment(0, 5);
		galleryData.left = new FormAttachment(0, 5);
		
		gallery.setLayoutData(galleryData);

		DefaultGalleryGroupRenderer galleryGroupRenderer = new DefaultGalleryGroupRenderer();

		// 设置组条目的颜色
		galleryGroupRenderer.setTitleBackground(new Color(Display.getDefault(),
				new RGB(255, 200, 100)));

		// 设置组条目字体颜色。
		galleryGroupRenderer.setTitleForeground(new Color(Display.getDefault(),
				new RGB(0, 0, 0)));

		galleryGroupRenderer.setFillIfSingle(false);

		// 设置图标大小
		galleryGroupRenderer.setItemSize(164, 64);
		galleryGroupRenderer.setMinMargin(3);

		// Item Renderer
		DefaultGalleryItemRenderer galleryItemRenderer = new DefaultGalleryItemRenderer();

		gallery.setGroupRenderer(galleryGroupRenderer);
		gallery.setItemRenderer(galleryItemRenderer);
//		gallery.addSelectionListener(new SelectionListener() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				selectGalleryItem();
//			}
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//			}
//
//		});

		Listener ttListener = new ToolTipListener(gallery);
		gallery.addListener(32, ttListener);
		gallery.addListener(5, ttListener);
		gallery.addListener(4, ttListener);
		gallery.addListener(3, ttListener);
//		gallery.addMouseTrackListener(new MouseTrackAdapter() {
//			public void mouseHover(MouseEvent e) {
//				System.out.println("mouseHover");
//			}
//		});

		createGalleryGroups();

		initContentList();

		clearSuperfluousItem();
		
		FormData tableData = new FormData();
		tableData.right = new FormAttachment(100, -5);
		tableData.bottom = new FormAttachment(100, 0);
		tableData.top = new FormAttachment(80, 6);
		tableData.left = new FormAttachment(0, 5);
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(tableData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		galleryData.bottom = new FormAttachment(table, -5);
		
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

	/**

	 */
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

	
//	public void selectGalleryItem() {
//		pluginTreeContent = null;
//
//		GalleryItem[] galleryItems = gallery.getSelection();
//		if (galleryItems == null || galleryItems.length == 0) {
//			return;
//		}
//
//		pluginTreeContent = (PluginTreeContent) galleryItems[0].getData();
//
//		if (pluginTreeContent == null || pluginTreeContent.parent == null) {
//			setButtonsState(button1Index, "NULL");
//		} else {
//			setButtonsState(button1Index, pluginTreeContent.parent.toString());
//		}
//
//	}

	// 创建Gallery组
	public void createGalleryGroups() {
		active = new GalleryItem(gallery, 0);
		active.setText("Active");
		active.setExpanded(true);

		installed = new GalleryItem(gallery, 0);
		installed.setText("Installed");
		installed.setExpanded(false);

		resolved = new GalleryItem(gallery, 0);
		resolved.setText("Resolved");
		resolved.setExpanded(false);

		starting = new GalleryItem(gallery, 0);
		starting.setText("Starting");
		starting.setExpanded(false);

		other = new GalleryItem(gallery, 0);
		other.setText("Other");
		other.setExpanded(false);

	}

	// 如果某个GalleryItem下没有Bundle，则除去此GalleryItem;
	public void clearSuperfluousItem() {
		if (0 == active.getItemCount()) {
			gallery.remove(active);
		}

		if (0 == installed.getItemCount()) {
			gallery.remove(installed);
		}

		if (0 == resolved.getItemCount()) {
			gallery.remove(resolved);
		}

		if (0 == starting.getItemCount()) {
			gallery.remove(starting);
		}

		if (0 == other.getItemCount()) {
			gallery.remove(other);
		}

	}

	// 除去Group中所有的GalleryItem
	public void clearAllGalleryItem() {
		gallery.removeAll();
	}

	// 初始化BundleList和Gallery的Item
	public void initContentList() {
		pluginTreeContentList = PluginTreeContent.output();

		PluginTreeContent[] pluginTreeContents = pluginTreeContentList
				.toArray(new PluginTreeContent[0]);

		if (bundleList != null) {
			bundleList.clear();
		}

		bundleList = new ArrayList<Bundle>();

		for (PluginTreeContent pluginTreeContent : pluginTreeContents) {
			List<PluginTreeContent> list = pluginTreeContent.getChildren();
			if (list != null && list.size() > 0) {
				Iterator it = list.iterator();
				while (it.hasNext()) {
					PluginTreeContent pContent = (PluginTreeContent) it.next();
					addBundleToGalleryItem(pContent);
					bundleList.add(pContent.getBundle());
				}

			}
		}
	}

	// 把一个Bundle添加到相应的Gallery组中。
	private void addBundleToGalleryItem(PluginTreeContent pluginTreeContent) {
		switch (pluginTreeContent.getBundle().getState()) {
		case 32:
			createGalleryItem(active, pluginTreeContent, activeImg);
			break;
		case 2:
			createGalleryItem(installed, pluginTreeContent, installedImg);
			break;
		case 4:
			createGalleryItem(resolved, pluginTreeContent, resolvedImg);
			break;
		case 8:
			createGalleryItem(starting, pluginTreeContent, startingImg);
			break;
		default:
			createGalleryItem(other, pluginTreeContent, otherImg);
		}
	}

	// 创建一个Gallery的Item
	public void createGalleryItem(GalleryItem parentItem,
			PluginTreeContent pluginTreeContent, Image image)  {
		GalleryItem item = new GalleryItem(parentItem, 0);
		
		// 从插件Jar包中获取插件图标。
		Image bundleImage = null;
		try {
			bundleImage = getImageFromBundle(pluginTreeContent.getBundle());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 如果找不到插件的图标，则用默认图标，否则则用插件图标。
		if(bundleImage == null) {
			item.setImage(image);	
		} else {
			item.setImage(bundleImage);
		}
		
		item.setData(pluginTreeContent);
		item.setText(pluginTreeContent.getBundle().getSymbolicName() != null ? pluginTreeContent
				.getBundle().getSymbolicName() : pluginTreeContent.getBundle()
				.getLocation());
		// item.setDescription(b.getBundleId());
	}

	// 获取插件图标(如果插件想用自己的图标显示，则请在icons文件夹下保存一个名为插件名的png图像)
	public Image getImageFromBundle(Bundle bundle) throws IOException {
		String bundleName = bundle.getHeaders().get("Bundle-Name");
		File bundleFile = FileLocator.getBundleFile(bundle);
		
		JarFile jarFile = new JarFile(bundleFile.getAbsolutePath());
		Enumeration emu = jarFile.entries();
		Image image = null;
		while (emu.hasMoreElements()) {
			ZipEntry entry = (ZipEntry)emu.nextElement();

			String fileName = entry.getName();
			
			if(fileName.endsWith(bundleName + ".png")) {
				image = new Image(getDisplay(),
			             jarFile.getInputStream(entry));
				break;
			}
		}
		
		return image;
		
	}
	
//	// 获取插件的图标（从plugin.xml文件中获取产品的图标）
//	public Image getImageFromBundle(Bundle bundle) throws IOException {
//		if(bundle == null) {
//			return null;
//		}
//		File bundleFile = FileLocator.getBundleFile(bundle);
//		
//		if(bundleFile == null || !bundleFile.isFile()) {
//			return null;
//		}
//		
//		JarFile jarFile = new JarFile(bundleFile.getAbsolutePath());
//		Enumeration emu = jarFile.entries();
//		
//		BufferedInputStream bufferedInputStream = null;
//		String imagePath = null;
//		while (emu.hasMoreElements()) {
//			ZipEntry entry = (ZipEntry)emu.nextElement();
//
//			String fileName = entry.getName();
//
//			
//			if(fileName.endsWith(PLUGIN_XML_NAME)) {
//				bufferedInputStream = new BufferedInputStream(
//					jarFile.getInputStream(entry));
//				
//				byte[] bytes = new byte[FileDeal.BUFFER_SIZE];
//				int count = bufferedInputStream.read(bytes, 0, FileDeal.BUFFER_SIZE);
//				
//
//				XMLAnalysis xMLAnalysis = new XMLAnalysis(bytes);
//				Document document = xMLAnalysis.getDocument();
//				
//				Element root = document.getRootElement();
//				Iterator iterator = root.elementIterator();
//				while(iterator.hasNext()) {
//					Element element = (Element) iterator.next();
//					Attribute attribute = element.attribute("id");
//					
//					if(attribute != null && "product".equals(attribute.getText())) {
//						Iterator<Element> elementProductIterator = element.elementIterator();
//						while(elementProductIterator.hasNext()) {
//							Element e = elementProductIterator.next();
//							Iterator<Element> ee = e.elementIterator();
//							while(ee.hasNext()) {
//								Element eTemp = ee.next();
//								Attribute aa = eTemp.attribute("name");
//								if(aa != null && "windowImages".equals(aa.getText())) {
//									imagePath = eTemp.attributeValue("value");
//									break;
//								}
//							}
//							if(imagePath != null) {
//								break;
//							}
//						}
//						
//						if(imagePath != null) {
//							break;
//						}
//					}
//				}
//
//				if(bufferedInputStream != null) {
//					bufferedInputStream.close();
//				}
//				
//				jarFile.close();
//				break;
//			}
//		}
//		
//		if(imagePath == null) {
//			return null;
//		}
//		
//		List<String> imagePaths = new ArrayList();	
//		String[] stringImages = imagePath.split(",");
//		for(String str : stringImages) {
//			imagePaths.add(str);
//		}
//
//	 	
//		jarFile = new JarFile(bundleFile.getAbsolutePath());
//		emu = jarFile.entries();
//		Image image = null;
//		while (emu.hasMoreElements()) {
//			ZipEntry entry = (ZipEntry)emu.nextElement();
//
//			String fileName = entry.getName();
//			
//			if(fileName.endsWith(imagePaths.get(0))) {
//				image = new Image(getDisplay(),
//			             jarFile.getInputStream(entry));
//				break;
//			}
//		}
//		
//		return image;
		
//	}
	
	
	// 初始化按钮Map
	private void initButtonMap(Map<String, List<Button>> buttonMap) {
		this.buttonMap = buttonMap;
	}

	// 通过Id值来修改对应的按钮列表的状态。
	public void setButtonsState(String id, String param) {
		if (buttonMap == null) {
			return;
		}

		List<Button> buttonList = buttonMap.get(id);

		if (buttonList == null && buttonList.size() == 0) {
			return;
		}

		if ("ACTIVE".equals(param)) {
			buttonList.get(2).setEnabled(false);
			buttonList.get(3).setEnabled(true);
			buttonList.get(4).setEnabled(true);

		} else if ("RESOLVED".equals(param) || "INSTALL".equals(param)
				|| "STARTING".equals(param)) {
			buttonList.get(2).setEnabled(true);
			buttonList.get(3).setEnabled(false);
			buttonList.get(4).setEnabled(true);

		} else if ("NULL".equals(param)) {
			buttonList.get(2).setEnabled(false);
			buttonList.get(3).setEnabled(false);
			buttonList.get(4).setEnabled(false);

		} else {
			buttonList.get(2).setEnabled(true);
			buttonList.get(3).setEnabled(true);
			buttonList.get(4).setEnabled(true);
		}
	}

	// 展开所有节点
	public void expandAll() {
//		GalleryTreeViewer galleryTreeViewer = new GalleryTreeViewer(gallery);
//		galleryTreeViewer.expandAll();
	}

	
	
	public static void main(String[] args) throws IOException, DocumentException {
		JarFile jarFile = new JarFile("d:/sunline.jar");
		Enumeration emu = jarFile.entries();
		
		BufferedInputStream bufferedInputStream = null;
		String imagePath = null;
		while (emu.hasMoreElements()) {
			ZipEntry entry = (ZipEntry)emu.nextElement();

			String fileName = entry.getName();

			if(fileName.endsWith(PLUGIN_XML_NAME)) {
				bufferedInputStream = new BufferedInputStream(
					jarFile.getInputStream(entry));
				
				byte[] bytes = new byte[FileDeal.BUFFER_SIZE];
				int count = bufferedInputStream.read(bytes, 0, FileDeal.BUFFER_SIZE);
				

				XMLAnalysis xMLAnalysis = new XMLAnalysis(bytes);
				Document document = xMLAnalysis.getDocument();
				
				Element root = document.getRootElement();
				Iterator iterator = root.elementIterator();
				while(iterator.hasNext()) {
					Element element = (Element) iterator.next();
					Attribute attribute = element.attribute("id");
					
					if(attribute != null && "product".equals(attribute.getText())) {
						Iterator<Element> elementProductIterator = element.elementIterator();
						while(elementProductIterator.hasNext()) {
							Element e = elementProductIterator.next();
							Iterator<Element> ee = e.elementIterator();
							while(ee.hasNext()) {
								Element eTemp = ee.next();
								Attribute aa = eTemp.attribute("name");
								if(aa != null && "windowImages".equals(aa.getText())) {
									imagePath = eTemp.attributeValue("value");
									break;
								}
							}
							if(imagePath != null) {
								break;
							}
						}
						
						if(imagePath != null) {
							break;
						}
					}
				}

				if(bufferedInputStream != null) {
					bufferedInputStream.close();
				}
				
				jarFile.close();
				break;
			}
		}
		

	}

}
