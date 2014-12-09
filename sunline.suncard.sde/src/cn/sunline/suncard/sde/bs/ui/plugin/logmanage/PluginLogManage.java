/**
 * 文件名：     PluginLogManage.java
 * 版权：      
 * 描述：
 * 修改人：     易振强
 * 修改时间：2011-9-28
 * 修改内容: 创建
 */
package cn.sunline.suncard.sde.bs.ui.plugin.logmanage;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;


import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;

import org.hibernate.Query;

import cn.sunline.suncard.sde.bs.biz.BsPluginlogBiz;
import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsPluginlog;
import cn.sunline.suncard.sde.bs.entity.BsPluginlogId;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.ui.plugin.PluginAdd;
import cn.sunline.suncard.sde.bs.ui.resource.IAppconstans;
import cn.sunline.suncard.sde.bs.ui.resource.IWidgetId;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.bs.util.WidgetPermissionUtil;

public class PluginLogManage extends Dialog {
//	private final static String REPLACE_URL = PluginAdd.REPLACE_URL;
	private BsPluginlogBiz bsPluginlogBiz = new BsPluginlogBiz();
	private Shell shell;

	private DateTime fromDate;
	private DateTime toDate;
	private Button queryButton;
//	private Button deleteButton;
	private Table table;
	private TableViewer tableViewer;
	private List<BsPluginlog> bsPluginLogList;
	
	private TableColumn logNum;
	private TableColumn pluginId;
	private TableColumn operationType;
	private TableColumn srcPluginVersion;
	private TableColumn newPluginVersion;
	private TableColumn patchNum;
	private TableColumn backupUrl;
	private TableColumn operationDate;

	private final static int BUTTON_DELETE = 3101;

	public PluginLogManage(Shell shell) {
		super(shell);
//		setShellStyle(SWT.RESIZE);
		this.shell = shell;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(I18nUtil.getMessage("PLUGIN_LOG_TITLE"));
		
		Image pluginManagerImage = CacheImage.getCacheImage().getImage(
				IAppconstans.APPLICATION_ID, "icons/logView.png");
		newShell.setImage(pluginManagerImage);
	}
	
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(10, false));

		Label from = new Label(container, SWT.NONE);
		from.setText(I18nUtil.getMessage("PLUGIN_LOG_FROM"));
		fromDate = new DateTime(container, SWT.BORDER);

		Label to = new Label(container, SWT.NONE);
		to.setText(I18nUtil.getMessage("PLUGIN_LOG_TO"));

		toDate = new DateTime(container, SWT.BORDER);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		queryButton = new Button(container, SWT.NONE);
		GridData queryButtonData = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		queryButtonData.widthHint = 82;
		queryButton.setLayoutData(queryButtonData);
		queryButton.setText(I18nUtil.getMessage("PLUGIN_LOG_QUERY"));
		queryButton.setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_B_LOG_EXAMINE_SEARCH));
		queryButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				queryPluginLog();
			}
		});

		createTable(container);

//		// 创建Table的表头排序监听事件
//		createTableSortEvent();
		
		return container;
	}

	public void queryPluginLog() {
//		String startDate = "" + fromDate.getYear() + "-"
//				+ (fromDate.getMonth() + 1) + "-" + fromDate.getDay();
//		String endDate = "" + toDate.getYear() + "-" + (toDate.getMonth() + 1)
//				+ "-" + toDate.getDay();

		Calendar calendarStart  = Calendar.getInstance(); 
		calendarStart.set(fromDate.getYear(), fromDate.getMonth() + 1, fromDate.getDay()); 
		Date startDate = calendarStart.getTime();
		
		Calendar calendarEnd  = Calendar.getInstance(); 
		calendarEnd.set(toDate.getYear(), toDate.getMonth() + 1, toDate.getDay()); 
		Date endDate = calendarEnd.getTime();
		
		bsPluginLogList = bsPluginlogBiz.getDateToDate(startDate,
				endDate);
		
		tableViewer.setInput(bsPluginLogList);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
//		deleteButton = createButton(parent, BUTTON_DELETE, I18nUtil.getMessage("PLUGIN_LOG_DELETE"), true);
//		deleteButton.setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_B_LOG_EXAMINE_DELETE));
//		deleteButton.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				deletePluginLog();
//			}
//		});

		Button quitButton = createButton(parent, IDialogConstants.CANCEL_ID, I18nUtil.getMessage("PLUGIN_LOG_QUIT"), false);
		quitButton.setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_B_LOG_EXAMINE_QUIT));
	}

//	public void deletePluginLog() {
//		List<BsPluginlog> newlist=new ArrayList<BsPluginlog>();
//		int[] index = tableViewer.getTable().getSelectionIndices();
//		List list = (List)tableViewer.getInput();
//		for(int i : index) {
//			BsPluginlog d = (BsPluginlog) list.get(i);
//			newlist.add(d);
//		}
//		bsPluginlogBiz.delete(newlist);

//		TableItem[] items = tableViewer.getTable().getSelection();
//		for(TableItem item : items) {
//			BsPluginlog bsPluginlog = (BsPluginlog) item.getData();
//			bsPluginlogBiz.deleteEntity(bsPluginlog.getId(),
//					BsPluginlog.class);
//		}
		
//		IStructuredSelection selection = (IStructuredSelection) tableViewer
//				.getSelection();	    		 
//		Iterator it = selection.iterator();
//
//		HibernateUtil.openSession();
//		
//		while (it.hasNext()) {
//			Object object = it.next();
//			if (object instanceof BsPluginlog) {
//				bsPluginlogBiz.delete((BsPluginlog) object);
//			}
//		}
//		
//		HibernateUtil.closeSession();
//		
//		queryPluginLog();
//	}

	public void createTable(Composite container) {
		table = new Table(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		GridData gdTable = new GridData(SWT.FILL, SWT.FILL, true, true, 10, 4);
		gdTable.heightHint = 363;
		table.setLayoutData(gdTable);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);


		logNum = new TableColumn(table, SWT.NONE);
		logNum.setWidth(50);
		logNum.setText(I18nUtil.getMessage("PLUGIN_LOG_SEQ"));
		
		// 设置默认的排序标记,true表示升序
		logNum.setData(true);

		pluginId = new TableColumn(table, SWT.NONE);
		pluginId.setWidth(100);
		pluginId.setText(I18nUtil.getMessage("PLUGIN_LOG_PLUGIN_ID"));
		
		// 设置默认的排序标记,true表示升序
		pluginId.setData(true);

		operationType = new TableColumn(table, SWT.NONE);
		operationType.setWidth(70);
		operationType.setText(I18nUtil.getMessage("PLUGIN_LOG_PROCESS_TYPE"));
		
		// 设置默认的排序标记,true表示升序
		operationType.setData(true);

		srcPluginVersion = new TableColumn(table, SWT.NONE);
		srcPluginVersion.setWidth(100);
		srcPluginVersion.setText(I18nUtil.getMessage("PLUGIN_LOG_SRCVERSION"));
		
		// 设置默认的排序标记,true表示升序
		srcPluginVersion.setData(true);

		newPluginVersion = new TableColumn(table, SWT.NONE);
		newPluginVersion.setWidth(100);
		newPluginVersion.setText(I18nUtil.getMessage("PLUGIN_LOG_VERSION"));
		
		// 设置默认的排序标记,true表示升序
		newPluginVersion.setData(true);

		patchNum = new TableColumn(table, SWT.NONE);
		patchNum.setWidth(100);
		patchNum.setText(I18nUtil.getMessage("PLUGIN_LOG_PATCH_ID"));
		
		// 设置默认的排序标记,true表示升序
		patchNum.setData(true);

		backupUrl = new TableColumn(table, SWT.NONE);
		backupUrl.setWidth(100);
		backupUrl.setText(I18nUtil.getMessage("PLUGIN_LOG_REPLACE_URL"));
		
		// 设置默认的排序标记,true表示升序
		backupUrl.setData(true);

		operationDate = new TableColumn(table, SWT.NONE);
		operationDate.setWidth(100);
		operationDate.setText(I18nUtil.getMessage("PLUGIN_LOG_HANDLE_DATE"));
		
		// 设置默认的排序标记,true表示升序
		operationDate.setData(true);

		tableViewer = new TableViewer(table);
		tableViewer.setLabelProvider(new PluginLogLabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setSorter(new PluginLogSorter());

		queryPluginLog();
	}

//	public void createTableSortEvent() {
//		logNum.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				super.widgetSelected(e);
//			}
//		});
//	}
	
	
	// 根据日志的备份文件记录来删除备份文件。
	public static void deleteBackJarFiles(List<BsPluginlog> list) {
		if(list == null || list.size() == 0) {
			return;
		}
		
		for(BsPluginlog bsPluginlog : list) {
			String jarFilePath = bsPluginlog.getReplaceUrl();
			if(jarFilePath == null || "".equals(jarFilePath.trim())) {
				continue;
			}
			
			File file = new File(jarFilePath);
			if(file.exists() && file.isFile()) {
				file.delete();
			}
		}
	}
	
//	public static void addPluginLog(String pluginId, String processType,
//			String srcPluginVer, String pluginVer, String patchId,
//			String repFileName) {
//
//		BsPluginlog bsPluginlog = new BsPluginlog();	
//		BsPluginlogId bsPluginlogId = new BsPluginlogId(
//				new Long((String) Context.getSessionMap().get(
//						Constants.BANKORG_ID)).longValue(), (String) Context
//						.getSessionMap().get(Constants.BANKORG_ID), getMaxSeq());
//		bsPluginlog = new BsPluginlog(bsPluginlogId);
//		bsPluginlog.setPluginId(pluginId);
//		bsPluginlog.setProcessType(processType);
//		bsPluginlog.setSrcPluginVer(srcPluginVer);
//		bsPluginlog.setPluginVer(pluginVer);
//		bsPluginlog.setPatchId(patchId);
//
//		if (repFileName != null && !"".equals(repFileName.trim())) {
//			bsPluginlog.setReplaceUrl(REPLACE_URL + repFileName.trim());
//		}
//		
//		BsPluginlogBiz bsPluginlogBizTem = new BsPluginlogBiz();
//		bsPluginlogBizTem.add(bsPluginlog);
//	}



}
