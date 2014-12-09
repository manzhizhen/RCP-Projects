/*
 * 文件名：FuncManagerDialog.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：功能增加对话框
 * 修改人：heyong
 * 修改时间：2011-9-26
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.dailogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.actions.ActionGroup;

import cn.sunline.suncard.sde.bs.biz.BsFuncmappingBiz;
import cn.sunline.suncard.sde.bs.biz.BsWidgetBiz;
import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsFuncmapping;
import cn.sunline.suncard.sde.bs.entity.BsFuncmappingId;
import cn.sunline.suncard.sde.bs.entity.BsFunction;
import cn.sunline.suncard.sde.bs.entity.BsFunctionId;
import cn.sunline.suncard.sde.bs.entity.BsWidget;
import cn.sunline.suncard.sde.bs.entity.WidgetMappingType;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.ui.actions.SelectActionGroup;
import cn.sunline.suncard.sde.bs.ui.provider.BsWidgetTvLabelProvider;
import cn.sunline.suncard.sde.bs.ui.provider.TableViewerContentProvider;
import cn.sunline.suncard.sde.bs.ui.resource.IAppconstans;
import cn.sunline.suncard.sde.bs.ui.resource.IImageKey;
import cn.sunline.suncard.sde.bs.ui.resource.IWidgetId;
import cn.sunline.suncard.sde.bs.ui.resource.SWTResourceManager;
import cn.sunline.suncard.sde.bs.ui.sorter.BsWidgetViewerSorter;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.bs.util.WidgetPermissionUtil;

/**
 * 弹出功能编辑对话框
 * 该对话框用于功能的添加和修改操作
 * @author heyong
 * @version 1.0, 2011-9-26
 * @see 
 * @since 1.0
 */
public class FuncManagerDialog extends TitleAreaDialog {
	
	private Text txtBankorgId;
	private Text txtFuncId;
	private Text txtPcId;
	private Text txtFuncName;

	private BsFunction function;
	private boolean editable;
	private String info;
	private Table table;
	
	private GC gc;
	
	private CheckboxTableViewer checkboxTableViewer;
	
	private TableColumn tblclmnbankorgId;
	private TableColumn tblclmnpcId;
	private TableColumn tblclmnwidgetId;
	private TableColumn tblclmnwidgetName;
	private TableColumn tblclmnwidgetType;
	private TableColumn tblclmnparWidgetId;
	private TableColumn tblclmnpluginId;
	private TableColumn tblclmnmappingType;
	
	private String[] items = WidgetMappingType.names();
	/**
	 * @wbp.parser.constructor
	 */
	public FuncManagerDialog(Shell parentShell) {
		super(parentShell);
	}
	
	public FuncManagerDialog(Shell parentShell, BsFunction function){
		this(parentShell);
		this.function = (function == null? new BsFunction(new BsFunctionId()) : function);
		this.editable = (function == null ? true : false);
		this.info = (function == null ? "CONFIRMADD" : "CONFIRMMODIFY");
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(null);
		
		setTitleImage(CacheImage.getCacheImage().getImage(IAppconstans.APPLICATION_ID, IImageKey.FUNCTION_MANAGER_ICON));
		setTitle(I18nUtil.getMessage("FUNCTION_MANAGER"));
		setMessage(I18nUtil.getMessage("functioninfo"));
		
		Group group = new Group(container, SWT.NONE);
		group.setBounds(10, 0, 605, 70);
		
		Group group1 = new Group(container, SWT.NONE);
		group1.setText(I18nUtil.getMessage("widgetlist"));
		group1.setBounds(10, 79, 605, 243);
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setBounds(22, 21, 60, 12);
		lblNewLabel.setText(I18nUtil.getMessage("BANKORG_ID"));
		
		txtBankorgId = new Text(group, SWT.BORDER);
		txtBankorgId.setBounds(96, 18, 166, 18);
		String bankorgId = String.valueOf(Context.getSessionMap().get(Constants.BANKORG_ID));
		txtBankorgId.setText((function.getId().getBankorgId()==null?  bankorgId : function.getId().getBankorgId().toString()));
		txtBankorgId.setEditable(false);
		
		
		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setBounds(22, 45, 48, 12);
		lblNewLabel_1.setText(I18nUtil.getMessage("PC_ID"));
		
		txtPcId = new Text(group, SWT.BORDER);
		txtPcId.setBounds(96, 42, 166, 18);
		txtPcId.setText((String) (function.getId().getPcId()==null? Context.getSessionMap().get(Constants.PC_ID) : function.getId().getPcId()));
		txtPcId.setEditable(false);
		
		
		Label lblNewLabel_2 = new Label(group, SWT.NONE);
		lblNewLabel_2.setBounds(317, 21, 36, 12);
		lblNewLabel_2.setText(I18nUtil.getMessage("FUNCTION_ID"));
		
		txtFuncId = new Text(group, SWT.BORDER);
		txtFuncId.setBounds(401, 18, 166, 18);
		txtFuncId.setText(function.getId().getFunctionId()==null? "":function.getId().getFunctionId());
		txtFuncId.setTextLimit(10);
		txtFuncId.setEditable(this.editable);
		txtFuncId.setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_T_FUNC_FUNID_TEXT));
		
		Label lblNewLabel_3 = new Label(group, SWT.NONE);
		lblNewLabel_3.setBounds(319, 46, 48, 12);
		lblNewLabel_3.setText(I18nUtil.getMessage("FUNCTION_NAME"));
		
		txtFuncName = new Text(group, SWT.BORDER);
		txtFuncName.setBounds(400, 41, 166, 18);
		txtFuncName.setText(function.getFunctionName()==null? "":function.getFunctionName());
		txtFuncName.setTextLimit(40);
		txtFuncName.setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_T_FUNC_FUNNAME_TEXT));
		
		checkboxTableViewer = CheckboxTableViewer.newCheckList(group1, SWT.BORDER | SWT.FULL_SELECTION);
		table = checkboxTableViewer.getTable();
		table.setBounds(10, 23, 585, 210);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		tblclmnbankorgId = new TableColumn(table, SWT.NONE);
		tblclmnbankorgId.setWidth(100);
		tblclmnbankorgId.setText(I18nUtil.getMessage("BANKORG_ID"));
		
		tblclmnpcId = new TableColumn(table, SWT.NONE);
		tblclmnpcId.setWidth(100);
		tblclmnpcId.setText(I18nUtil.getMessage("PC_ID"));
		
		tblclmnwidgetId = new TableColumn(table, SWT.NONE);
		tblclmnwidgetId.setWidth(100);
		tblclmnwidgetId.setText(I18nUtil.getMessage("widgetId"));
		
		tblclmnwidgetName = new TableColumn(table, SWT.NONE);
		tblclmnwidgetName.setWidth(100);
		tblclmnwidgetName.setText(I18nUtil.getMessage("widgetName"));
		
		tblclmnwidgetType = new TableColumn(table, SWT.NONE);
		tblclmnwidgetType.setWidth(100);
		tblclmnwidgetType.setText(I18nUtil.getMessage("widgetType"));
		
		tblclmnparWidgetId = new TableColumn(table, SWT.NONE);
		tblclmnparWidgetId.setWidth(100);
		tblclmnparWidgetId.setText(I18nUtil.getMessage("parWidget"));
		
		tblclmnpluginId = new TableColumn(table, SWT.NONE);
		tblclmnpluginId.setWidth(100);
		tblclmnpluginId.setText(I18nUtil.getMessage("plugin"));
		
		tblclmnmappingType = new TableColumn(table, SWT.NONE);
		tblclmnmappingType.setWidth(100);
		tblclmnmappingType.setText(I18nUtil.getMessage("mappingType"));
		
		checkboxTableViewer.setContentProvider(new TableViewerContentProvider());
		checkboxTableViewer.setLabelProvider(new BsWidgetTvLabelProvider(this.function.getId().getFunctionId()));
		checkboxTableViewer.setInput(new BsWidgetBiz().findAll());
		//添加排序器
		addViewerSorter();
		checkboxTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if(!selection.isEmpty()){
					Object element = selection.getFirstElement();
					boolean flag = true;
					if(checkboxTableViewer.getChecked(element)){
						flag = false;
					}
					checkboxTableViewer.setChecked(element, flag);
				}
			}
		});
		
		ActionGroup actionGroup = new SelectActionGroup(checkboxTableViewer);
		actionGroup.fillContextMenu(new MenuManager());
		
		initWidgetList();
		//添加可编辑表格
		addCellEditor();
		//创建gc,用于重画边框
		gc = new GC(group);
				
		return container;
	}

	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("submit"),
				true).setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_B_FUNC_COMMIT));
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("cancle"), false).setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_B_FUNC_CANCEL));
	}

	
	@Override
	protected void okPressed() {
		if(!validate()){
			return ;
		}
		
		this.function.getId().setBankorgId(new Long(txtBankorgId.getText().trim()));
		this.function.getId().setFunctionId(txtFuncId.getText().trim());
		this.function.getId().setPcId(txtPcId.getText().trim());
		
		this.function.setFunctionName(txtFuncName.getText().trim());
		
		List<BsFuncmapping> bsFuncmappings = new ArrayList<BsFuncmapping>();
		Object[] elements = this.checkboxTableViewer.getCheckedElements();
		BsFuncmappingId id = null;
		BsFuncmapping bsFuncmapping = null;
		for(int i=0; i<elements.length; i++){
			BsWidget widget = (BsWidget) elements[i];
			id = new BsFuncmappingId();
			id.setBankorgId(widget.getId().getBankorgId());
			id.setFunctionId(txtFuncId.getText().trim());
			id.setPcId(widget.getId().getPcId());
			id.setWidgetId(widget.getId().getWidgetId());
			
			bsFuncmapping = new BsFuncmapping(id);
			bsFuncmapping.setMappingType(checkboxTableViewer.getTable().getItem(i).
					getText(checkboxTableViewer.getTable().getColumnCount() - 1).substring(0, 1));
			
			bsFuncmappings.add(bsFuncmapping);
		}
		function.setBsFuncmappings(bsFuncmappings);
		
		if (MessageDialog.openConfirm(getShell(), I18nUtil.getMessage("CONFIRMINFO"), 
				I18nUtil.getMessage(info))){
			super.okPressed();
		}
	}
		

	private boolean validate(){
		//先用原色画框
		drawColorBorder(txtFuncId.getBounds(), txtFuncId.getParent().getBackground());
		drawColorBorder(txtFuncName.getBounds(), txtFuncName.getParent().getBackground());
		
		if (txtFuncId.getText() == null || txtFuncId.getText().trim().length() == 0){
			setErrorMessage(I18nUtil.getMessage("FUNCTION_ID") + I18nUtil.getMessage("notnull"));
			drawColorBorder(txtFuncId.getBounds(), SWTResourceManager.getColor(255, 0, 0));
			return false;
		}else if (txtFuncName.getText() == null || txtFuncName.getText().trim().length() == 0){
			setErrorMessage(I18nUtil.getMessage("FUNCTION_NAME") + I18nUtil.getMessage("notnull"));
			drawColorBorder(txtFuncName.getBounds(), SWTResourceManager.getColor(255, 0, 0));
			return false;
		}
		setErrorMessage(null);
		return true;
	}
	
	/**
	 * 返回构造的实体类
	 * 
	 * @return BsFunction
	 */
	public BsFunction getResult(){
		return this.function;
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(632, 453);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(I18nUtil.getMessage("FUNCTION_MANAGER"));
		newShell.setImage(CacheImage.getCacheImage().getImage(IAppconstans.APPLICATION_ID, IImageKey.FUNCTION_MANAGER_ICON));
	}
	
	private void initWidgetList(){
		List<BsFuncmapping> funcmappings = new BsFuncmappingBiz().getAllByFunction(this.function);
		for (int i=0; i<checkboxTableViewer.getTable().getItemCount(); i++){
			BsWidget widget = (BsWidget) checkboxTableViewer.getElementAt(i);
			
			BsFuncmappingId id = new BsFuncmappingId();
			id.setBankorgId(widget.getId().getBankorgId());
			id.setPcId(widget.getId().getPcId());
			id.setWidgetId(widget.getId().getWidgetId());
			id.setFunctionId(this.function.getId().getFunctionId());
			BsFuncmapping funcmapping = new BsFuncmapping(id);
			//如果存在，则标记为选中
			if (funcmappings.contains(funcmapping)){
				checkboxTableViewer.setChecked(widget, true);
			}
		}
	}
	//为表格添加可编辑列
	private void addCellEditor(){
		checkboxTableViewer.setColumnProperties(new String[]{"bankorgId", "pcId", "widgetId", "widgetName",
				"widgetType", "parWidgetId", "pluginId", "mappingType"});
		CellEditor[] cellEditors = new CellEditor[]{null, null, null, null, null,
				null, null, new ComboBoxCellEditor(checkboxTableViewer.getTable(), items, SWT.READ_ONLY)};
		checkboxTableViewer.setCellEditors(cellEditors);
		checkboxTableViewer.setCellModifier(new CustomerCellModifer(checkboxTableViewer));
		
		
	}
	
	private class CustomerCellModifer implements ICellModifier{

		private CheckboxTableViewer cTableViewer;
		
		public CustomerCellModifer(CheckboxTableViewer cTableViewer){
			this.cTableViewer = cTableViewer;
		}
		
		@Override
		public boolean canModify(Object element, String property) {
			return true;
		}

		@Override
		public Object getValue(Object element, String property) {
			BsWidget widget = (BsWidget) element;
			if (property.equals("mappingType")){
				for(int i=0; i<items.length; i++){
					if (items[i].substring(0, 1).equals(widget.getMappingType())){
						return i;
					}
				}
			}
			return -1;
		}

		@Override
		public void modify(Object element, String property, Object value) {
			TableItem item = (TableItem) element;
			if (property.equals("mappingType")){
				Integer columnIndex = (Integer) value;
				item.setText(cTableViewer.getTable().getColumnCount() - 1, items[columnIndex]);
			}
		}
	}
	
	/**
	 * 重画边框
	 * @param rect 
	 * @param color
	 */
	private void drawColorBorder(Rectangle rect, Color color){
		gc.setForeground(color);  
		gc.drawRectangle(new Rectangle(rect.x - 1, rect.y - 1, rect.width + 1, rect.height + 1)); 
	}
	
	
	//添加排序器
	private void addViewerSorter(){
		tblclmnbankorgId.addSelectionListener(new SelectionAdapter() {
		//记录上一次排序是升序还是降序，默认为升序
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkboxTableViewer.setSorter(asc? BsWidgetViewerSorter.BANKORG_ID_ASC 
						: BsWidgetViewerSorter.BANKORG_ID_DESC);
				asc = !asc;
			}
		});
		tblclmnpcId.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkboxTableViewer.setSorter(asc? BsWidgetViewerSorter.PC_ID_ASC 
						: BsWidgetViewerSorter.PC_ID_DESC);
				asc = !asc;
			}
		});
		tblclmnwidgetId.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkboxTableViewer.setSorter(asc? BsWidgetViewerSorter.WIDGET_ID_ASC 
						: BsWidgetViewerSorter.WIDGET_ID_DESC);
				asc = !asc;
			}
		});
		tblclmnwidgetName.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkboxTableViewer.setSorter(asc? BsWidgetViewerSorter.WIDGET_NAME_ASC 
						: BsWidgetViewerSorter.WIDGET_NAME_DESC);
				asc = !asc;
			}
		});
		tblclmnwidgetType.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkboxTableViewer.setSorter(asc? BsWidgetViewerSorter.WIDGET_TYPE_ASC 
						: BsWidgetViewerSorter.WIDGET_TYPE_DESC);
				asc = !asc;
			}
		});
		tblclmnparWidgetId.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkboxTableViewer.setSorter(asc? BsWidgetViewerSorter.PARENT_WIDGET_ID_ASC 
						: BsWidgetViewerSorter.PARENT_WIDGET_ID_DESC);
				asc = !asc;
			}
		});
		tblclmnpluginId.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkboxTableViewer.setSorter(asc? BsWidgetViewerSorter.PLUGIN_ID_ASC 
						: BsWidgetViewerSorter.PLUGIN_ID_DESC);
				asc = !asc;
			}
		});
	}
}
