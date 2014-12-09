/* 文件名：     DataFormatComposite.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-24
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog.composite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.KeyValueModel;
import cn.sunline.suncard.powerdesigner.provider.UserDefineTableLabelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.ui.dialog.UserDefineTableCellModifier;
import cn.sunline.suncard.sde.bs.resource.CacheImage;

/**
 * 列属性对话框数据格式标签
 * 
 * @author Manzhizhen
 * @version 1.0, 2013-1-24
 * @see
 * @since 1.0
 */
public class DataFormatComposite extends Composite implements ISubComposite {
	private ColumnModel columnModel;
	private LinkedHashMap<String, KeyValueModel> customDataMap = new LinkedHashMap<String, KeyValueModel>();
	//数据格式里的默认转换
	private LinkedHashMap<String, KeyValueModel> defaultChangeMap = new LinkedHashMap<String, KeyValueModel>();
	
	private Button buttonChiniseDescription;
	private Button buttonUnitDescription;
	private Text textUnitDescription;
	private Button buttonDatasourceTransform;
	private Button buttonDefaultTransform;
	private Button dataFormatUpButton;
	private Button dataFormatDownButton;
	private Button dataFormatDelButton;
	private Button dataFormatAddButton;
	private TableViewer dataFormatUserDefineTableViewer;
	private Table dataFormatTableUserDefine;

	public DataFormatComposite(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	@Override
	public void createControl() {
		buttonUnitDescription = new Button(this, SWT.CHECK);
		FormData fd_button = new FormData();
		fd_button.top = new FormAttachment(0, 10);
		fd_button.left = new FormAttachment(0, 10);
		buttonUnitDescription.setLayoutData(fd_button);
		buttonUnitDescription.setText("是否自动附加单位描述：");

		textUnitDescription = new Text(this, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.top = new FormAttachment(buttonUnitDescription, -3, SWT.TOP);
		fd_text.left = new FormAttachment(buttonUnitDescription, 6);
		textUnitDescription.setLayoutData(fd_text);
		textUnitDescription.setEnabled(false);

		buttonChiniseDescription = new Button(this, SWT.CHECK);
		FormData fd_button_1 = new FormData();
		fd_button_1.top = new FormAttachment(buttonUnitDescription, 6);
		fd_button_1.left = new FormAttachment(0, 10);
		buttonChiniseDescription.setLayoutData(fd_button_1);
		buttonChiniseDescription.setText("是否自动转换中文描述：");

		buttonDatasourceTransform = new Button(this, SWT.RADIO);
		FormData fd_button_2 = new FormData();
		fd_button_2.left = new FormAttachment(buttonChiniseDescription, 6);
		fd_button_2.bottom = new FormAttachment(buttonChiniseDescription, 0,
				SWT.BOTTOM);
		buttonDatasourceTransform.setLayoutData(fd_button_2);
		buttonDatasourceTransform.setText("根据数据来源转换");
		buttonDatasourceTransform.setEnabled(false);

		buttonDefaultTransform = new Button(this, SWT.RADIO);
		FormData fd_button_3 = new FormData();
		fd_button_3.bottom = new FormAttachment(buttonChiniseDescription, 0,
				SWT.BOTTOM);
		fd_button_3.left = new FormAttachment(buttonDatasourceTransform, 6);
		buttonDefaultTransform.setLayoutData(fd_button_3);
		buttonDefaultTransform.setText("默认转换");
		buttonDefaultTransform.setEnabled(false);

		// -----------------------默认转换Group--------------------------------
		Group customGroupUserDefine = new Group(this, SWT.NONE);
		fd_text.right = new FormAttachment(customGroupUserDefine, 0, SWT.RIGHT);
		customGroupUserDefine.setLayout(new FormLayout());
		FormData fd_groupUserDefine = new FormData();
		fd_groupUserDefine.top = new FormAttachment(buttonChiniseDescription, 6);
		fd_groupUserDefine.left = new FormAttachment(0, 6);
		fd_groupUserDefine.bottom = new FormAttachment(100, -6);
		customGroupUserDefine.setLayoutData(fd_groupUserDefine);
		customGroupUserDefine.setText("默认转换:");

		// 删除按钮
		dataFormatDelButton = new Button(customGroupUserDefine, SWT.NONE);
		FormData fd_delButton = new FormData();
		fd_delButton.width = 30;
		fd_delButton.top = new FormAttachment(0, 0);
		fd_delButton.right = new FormAttachment(100, -10);
		dataFormatDelButton.setLayoutData(fd_delButton);
		dataFormatDelButton.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.COLUMN_DEL));
		dataFormatDelButton.setToolTipText("删除");
		dataFormatDelButton.setEnabled(false);

		// 下移按钮
		dataFormatDownButton = new Button(customGroupUserDefine, SWT.NONE);
		FormData fd_downButton = new FormData();
		fd_downButton.width = 30;
		fd_downButton.top = new FormAttachment(dataFormatDelButton, 0, SWT.TOP);
		fd_downButton.right = new FormAttachment(dataFormatDelButton, -6);
		dataFormatDownButton.setLayoutData(fd_downButton);
		dataFormatDownButton.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.COLUMN_DOWN));
		dataFormatDownButton.setToolTipText("下移一行");
		dataFormatDownButton.setEnabled(false);

		// 上移按钮
		dataFormatUpButton = new Button(customGroupUserDefine, SWT.NONE);
		FormData fd_upButton = new FormData();
		fd_upButton.width = 30;
		fd_upButton.top = new FormAttachment(dataFormatDelButton, 0, SWT.TOP);
		fd_upButton.right = new FormAttachment(dataFormatDownButton, -6);
		dataFormatUpButton.setLayoutData(fd_upButton);
		dataFormatUpButton.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.COLUMN_UP));
		dataFormatUpButton.setToolTipText("上移一行");
		dataFormatUpButton.setEnabled(false);

		// 插入按钮
		dataFormatAddButton = new Button(customGroupUserDefine, SWT.NONE);
		FormData fd_addButton = new FormData();
		fd_addButton.width = 30;
		fd_addButton.top = new FormAttachment(dataFormatDelButton, 0, SWT.TOP);
		fd_addButton.right = new FormAttachment(dataFormatUpButton, -6);
		dataFormatAddButton.setLayoutData(fd_addButton);
		dataFormatAddButton.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.A_NEW_IMAGE));
		dataFormatAddButton.setToolTipText("插入一行");
		dataFormatAddButton.setEnabled(false);

		// 用户自定义数据表格输入框
		dataFormatUserDefineTableViewer = new TableViewer(
				customGroupUserDefine, SWT.BORDER | SWT.FULL_SELECTION);
		dataFormatTableUserDefine = dataFormatUserDefineTableViewer.getTable();
		FormData fd_tableUserDefine = new FormData();
		fd_tableUserDefine.top = new FormAttachment(dataFormatDelButton, 6);
		fd_tableUserDefine.left = new FormAttachment(0, 6);
		fd_tableUserDefine.bottom = new FormAttachment(100, -6);
		fd_tableUserDefine.right = new FormAttachment(100, -6);
		dataFormatTableUserDefine.setLayoutData(fd_tableUserDefine);
		dataFormatTableUserDefine.setTouchEnabled(true);
		dataFormatTableUserDefine.setLinesVisible(true);
		dataFormatTableUserDefine.setHeaderVisible(true);
		dataFormatTableUserDefine.setEnabled(false);

		// 让该表格可以编辑
		String[] columnProperties = new String[] {
				DmConstants.COLUMN_PROPERTY_INDEX,
				DmConstants.COLUMN_PROPERTY_KEY,
				DmConstants.COLUMN_PROPERTY_VALUE };
		dataFormatUserDefineTableViewer.setColumnProperties(columnProperties);

		CellEditor[] cellEditors = new CellEditor[] {
				new TextCellEditor(dataFormatTableUserDefine),
				new TextCellEditor(dataFormatTableUserDefine),
				new TextCellEditor(dataFormatTableUserDefine) };
		dataFormatUserDefineTableViewer.setCellEditors(cellEditors);
		dataFormatUserDefineTableViewer
				.setCellModifier(new UserDefineTableCellModifier(
						dataFormatUserDefineTableViewer));

		dataFormatUserDefineTableViewer
				.setLabelProvider(new UserDefineTableLabelProvider(
						customDataMap));
		dataFormatUserDefineTableViewer
				.setContentProvider(new ArrayContentProvider());

		// 序号显示列
		TableViewerColumn tvColumnId = new TableViewerColumn(
				dataFormatUserDefineTableViewer, SWT.NONE);
		TableColumn columnId = tvColumnId.getColumn();
		columnId.setWidth(50);
		columnId.setText("");

		// 键ID列
		TableViewerColumn tvcKey = new TableViewerColumn(
				dataFormatUserDefineTableViewer, SWT.NONE);
		TableColumn columnKey = tvcKey.getColumn();
		columnKey.setWidth(250);
		columnKey.setText("键ID");

		// 键值列
		TableViewerColumn tvcValue = new TableViewerColumn(
				dataFormatUserDefineTableViewer, SWT.NONE);
		TableColumn columnValue = tvcValue.getColumn();
		columnValue.setWidth(250);
		columnValue.setText("键值");

		dataFormatUserDefineTableViewer
				.setContentProvider(new ArrayContentProvider());
		dataFormatUserDefineTableViewer
				.setLabelProvider(new UserDefineTableLabelProvider(
						defaultChangeMap));

		// 让该表格可以编辑
		String[] dataFormatColumnProperties = new String[] {
				DmConstants.COLUMN_PROPERTY_INDEX,
				DmConstants.COLUMN_PROPERTY_KEY,
				DmConstants.COLUMN_PROPERTY_VALUE };
		dataFormatUserDefineTableViewer
				.setColumnProperties(dataFormatColumnProperties);

		CellEditor[] dataFormatCellEditors = new CellEditor[] {
				new TextCellEditor(dataFormatTableUserDefine),
				new TextCellEditor(dataFormatTableUserDefine),
				new TextCellEditor(dataFormatTableUserDefine) };
		dataFormatUserDefineTableViewer.setCellEditors(dataFormatCellEditors);
		dataFormatUserDefineTableViewer
				.setCellModifier(new UserDefineTableCellModifier(
						dataFormatUserDefineTableViewer));
	}

	@Override
	public void initControlData(){
		// 自动附加单位描述
		if(!columnModel.getUnitDesc().trim().isEmpty()){
			buttonUnitDescription.setSelection(true);
			textUnitDescription.setEnabled(true);
			textUnitDescription.setText(columnModel.getUnitDesc());
		}
		// 自动转换成中文描述
		if(columnModel.isAutoChangeChDesc()){
			buttonChiniseDescription.setSelection(true);
			
			if(columnModel.isPassDataSourceChange()){
				buttonDatasourceTransform.setSelection(true);
				buttonDatasourceTransform.setEnabled(true);
				buttonDefaultTransform.setEnabled(true);
			} else {
				buttonDefaultTransform.setSelection(true);
				buttonDatasourceTransform.setEnabled(true);
				buttonDefaultTransform.setEnabled(true);
				dataFormatDelButton.setEnabled(true);
				dataFormatDownButton.setEnabled(true);
				dataFormatUpButton.setEnabled(true);
				dataFormatAddButton.setEnabled(true);
				dataFormatTableUserDefine.setEnabled(true);
				
				List<KeyValueModel> keyValueModels = new ArrayList<KeyValueModel>();
				Collection<KeyValueModel> list = (Collection<KeyValueModel>) defaultChangeMap.values();
				for (KeyValueModel keyValueModel : list) {
					keyValueModels.add(keyValueModel);
				}
				dataFormatUserDefineTableViewer.setInput(keyValueModels);
				
			}
		}
	}

	@Override
	public void createControlEvent() {
		buttonUnitDescription.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				textUnitDescription.setEnabled(!buttonUnitDescription.getSelection());
			}
		});
		
		buttonChiniseDescription.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				buttonDatasourceTransform.setEnabled(buttonChiniseDescription.getSelection());
				buttonDefaultTransform.setEnabled(buttonChiniseDescription.getSelection());
				if(buttonChiniseDescription.getSelection()){
					buttonDatasourceTransform.setSelection(columnModel.isPassDataSourceChange());
					buttonDefaultTransform.setSelection(!columnModel.isPassDataSourceChange());
					
					dataFormatDelButton.setEnabled(!columnModel.isPassDataSourceChange());
					dataFormatDownButton.setEnabled(!columnModel.isPassDataSourceChange());
					dataFormatUpButton.setEnabled(!columnModel.isPassDataSourceChange());
					dataFormatAddButton.setEnabled(!columnModel.isPassDataSourceChange());
					dataFormatTableUserDefine.setEnabled(!columnModel.isPassDataSourceChange());
					
				}
				super.widgetSelected(e);
			}
		});
		
		buttonDefaultTransform.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				dataFormatDelButton.setEnabled(true);
				dataFormatDownButton.setEnabled(true);
				dataFormatUpButton.setEnabled(true);
				dataFormatAddButton.setEnabled(true);
				dataFormatTableUserDefine.setEnabled(true);
			}
			
		});	
		buttonDatasourceTransform.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				dataFormatDelButton.setEnabled(false);
				dataFormatDownButton.setEnabled(false);
				dataFormatUpButton.setEnabled(false);
				dataFormatAddButton.setEnabled(false);
				dataFormatTableUserDefine.setEnabled(false);
			}
			
		});
		
		dataFormatDownButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				dataFormatMoveButtonEvent(false);
			}
		});
		
		dataFormatUpButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				dataFormatMoveButtonEvent(true);
			}
		});
		
		dataFormatAddButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				dataFormatAddButtonEvent();
			}
		});
		
		dataFormatDelButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				dataFormatDelButtonEvent();
			}
		});
	}

	/**
	 * 创建移动按钮事件 -- 数据格式
	 * 
	 * @param boolean 如果为ture，表示上移，false表示下移
	 */
	private void dataFormatMoveButtonEvent(boolean isUpMove) {
		List<KeyValueModel> list = getDataFormatTableSelection();
		if (list == null) {
			return;
		}

		List<KeyValueModel> keyValueModelList = (List<KeyValueModel>)dataFormatUserDefineTableViewer.getInput();
		
		Map<Integer, KeyValueModel> sortMap = new HashMap<Integer, KeyValueModel>();
		for (KeyValueModel key : list) {
				sortMap.put(new Integer(keyValueModelList.indexOf(key)),
						key);
		}
		

		if (isUpMove) {
			for (int i = 0; i < keyValueModelList.size(); i++) {
				KeyValueModel obj = sortMap.get(new Integer(i));

				if (obj == null) {
					continue;
				} else if (i == 0) {
					// 最小的行号为0，无法上移
					break;
				}
				// 把该元素和前面的元素交换位置
				keyValueModelList.set(i, keyValueModelList.get(i - 1));
				keyValueModelList.set(i - 1, (KeyValueModel) obj);

			}
		} else {
			for (int i = keyValueModelList.size() - 1; i > -1; i--) {
				KeyValueModel obj = sortMap.get(new Integer(i));

				if (obj == null) {
					continue;
				} else if (i == keyValueModelList.size() - 1) {
					// 最大的行号为columnModelList.size() - 1，无法下移
					break;
				}

				// 把该元素和前面的元素交换位置
				keyValueModelList.set(i, keyValueModelList.get(i + 1));
				keyValueModelList.set(i + 1, (KeyValueModel) obj);
			}
		}

		dataFormatUserDefineTableViewer.setInput(keyValueModelList);
		dataFormatUserDefineTableViewer.setSelection(new StructuredSelection(list));
	}
	
	/**
	 * 获取表格所选择的行 -- 数据格式
	 * 
	 * @return
	 */
	private List<KeyValueModel> getDataFormatTableSelection() {
		IStructuredSelection select = (IStructuredSelection) dataFormatUserDefineTableViewer
				.getSelection();

		if (select.isEmpty()) {
			return null;
		}

		List<KeyValueModel> list = select.toList();

		return list;
	}
	
	/**
	 * 插入一个自定义数据 --数据格式
	 */
	private void dataFormatAddButtonEvent() {
		int index = 1;

		KeyValueModel keyValueModel = new KeyValueModel();
		while(true){
			if(!defaultChangeMap.keySet().contains("key" + index)){
				break;
			}
			index ++;
		}
		
		keyValueModel.setKey("key" + index);
		keyValueModel.setValue("value" + index);
		defaultChangeMap.put((String) keyValueModel.getKey(), keyValueModel);

 		dataFormatUserDefineTableViewer.setInput(Arrays.asList(defaultChangeMap.values().toArray(new KeyValueModel[]{})));
	}
	
	/**
	 * 删除自定义数据 数据格式
	 */
	private void dataFormatDelButtonEvent() {
		List<KeyValueModel> list = getDataFormatTableSelection();

		if (list == null) {
			return;
		}

		List<KeyValueModel> keyValueModels = (List<KeyValueModel>) dataFormatUserDefineTableViewer.getInput();
		
		defaultChangeMap.clear();
		for(KeyValueModel keyValueModel : keyValueModels){
			defaultChangeMap.put((String)keyValueModel.getKey(), keyValueModel);
		}
		
		for (KeyValueModel keyValueModel : list) {
			defaultChangeMap.remove(keyValueModel.getKey());
		}

		dataFormatUserDefineTableViewer.setInput(Arrays.asList(defaultChangeMap.values().toArray(new KeyValueModel[]{})));
		
	}
	
	@Override
	public String checkData() {
		return null;
	}

	public void setColumnModel(ColumnModel columnModel) {
		this.columnModel = columnModel;
		
		Set<String> setDataSource = columnModel.getCustomDataMap().keySet();
		for (String key : setDataSource) {
			KeyValueModel keyValueModel = new KeyValueModel();
			keyValueModel.setKey(key);
			keyValueModel.setValue(columnModel.getCustomDataMap().get(key));
			this.customDataMap.put(key, keyValueModel);
		}
		
		Set<String> setDataFormat = columnModel.getDefaultSwitchMap().keySet();
		for(String key : setDataFormat){
			KeyValueModel keyValueModel = new KeyValueModel();
			keyValueModel.setKey(key);
			keyValueModel.setValue(columnModel.getDefaultSwitchMap().get(key));
			this.defaultChangeMap.put(key, keyValueModel);
		}
	}
	
	/**
	 * 给使用Domains的该字段设置数据格式内容
	 * @return
	 */
	public void setDomainsContent(ColumnModel domainsColumnModel) {
		buttonUnitDescription.setSelection(!(domainsColumnModel.getUnitDesc() == null 
		|| domainsColumnModel.getUnitDesc().isEmpty()));
		textUnitDescription.setText(domainsColumnModel.getUnitDesc() == null ? "" 
				: domainsColumnModel.getUnitDesc());
		buttonChiniseDescription.setSelection(domainsColumnModel.isAutoChangeChDesc());
		buttonDatasourceTransform.setSelection(domainsColumnModel.isPassDataSourceChange());
		buttonDefaultTransform.setSelection(!domainsColumnModel.isPassDataSourceChange());
		dataFormatUserDefineTableViewer.setInput(domainsColumnModel.getDefaultSwitchMap().clone());
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		buttonUnitDescription.setEnabled(enabled);
		textUnitDescription.setEditable(enabled);
		buttonChiniseDescription.setEnabled(enabled);
		buttonDatasourceTransform.setEnabled(enabled);
		buttonDefaultTransform.setEnabled(enabled);
		dataFormatDelButton.setEnabled(enabled);
		dataFormatDownButton.setEnabled(enabled);
		dataFormatUpButton.setEnabled(enabled);
		dataFormatAddButton.setEnabled(enabled);
		super.setEnabled(enabled);
	}

	public Button getButtonUnitDescription() {
		return buttonUnitDescription;
	}

	public Text getTextUnitDescription() {
		return textUnitDescription;
	}

	public Button getButtonChiniseDescription() {
		return buttonChiniseDescription;
	}

	public Button getButtonDatasourceTransform() {
		return buttonDatasourceTransform;
	}

	public TableViewer getDataFormatUserDefineTableViewer() {
		return dataFormatUserDefineTableViewer;
	}
	
	

	
}
