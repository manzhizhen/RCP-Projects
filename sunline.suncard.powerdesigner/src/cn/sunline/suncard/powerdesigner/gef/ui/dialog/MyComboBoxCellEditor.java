/* 文件名：     MyComboBoxCellEditor.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-26
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.ui.dialog;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


/**
 * 
 * @author  Manzhizhen
 * @version 1.0, 2012-11-26
 * @see 
 * @since 1.0
 */
public class MyComboBoxCellEditor extends CellEditor{
	private CCombo comboBox;
	private ComboViewer comboViewer;
	private LabelProvider labelProvider;
	
	private Object selectObject;
	private List<Object> itemList;
	
	private static final int defaultStyle = SWT.READ_ONLY;
	
	public MyComboBoxCellEditor() {
		setStyle(defaultStyle);
	}
	
	public MyComboBoxCellEditor(Composite parent, List itemList, LabelProvider labelProvider) {
		this(parent, itemList, labelProvider, defaultStyle);
	}
	
	public MyComboBoxCellEditor(Composite parent, List<Object> itemList, LabelProvider labelProvider, int style) {
		super(parent, style);
		this.labelProvider = labelProvider;
		setItemList(itemList);
		
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(labelProvider);
		
		updateComboBoxItems();
	}
	
	@Override
	protected Control createControl(Composite parent) {
		comboBox = new CCombo(parent, getStyle());
		comboViewer = new ComboViewer(comboBox);

		comboBox.addKeyListener(new KeyAdapter() {
			// hook key pressed - see PR 14201
			public void keyPressed(KeyEvent e) {
				keyReleaseOccured(e);
			}
		});

		comboBox.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent event) {
				applyEditorValueAndDeactivate();
			}

			public void widgetSelected(SelectionEvent event) {
				selectObject = getSelection();
			}
		});

		comboBox.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE
						|| e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
				}
			}
		});

		comboBox.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				MyComboBoxCellEditor.this.focusLost();
			}
		});
		
		return comboBox;
	}

	@Override
	protected Object doGetValue() {
		return selectObject;
	}

	@Override
	protected void doSetFocus() {
		comboBox.setFocus();
	}
	
	public LayoutData getLayoutData() {
		LayoutData layoutData = super.getLayoutData();
		if ((comboBox == null) || comboBox.isDisposed()) {
			layoutData.minimumWidth = 60;
		} else {
			// make the comboBox 10 characters wide
			GC gc = new GC(comboBox);
			layoutData.minimumWidth = (gc.getFontMetrics()
					.getAverageCharWidth() * 10) + 10;
			gc.dispose();
		}
		return layoutData;
	}

	@Override
	protected void doSetValue(Object value) {
		Assert.isTrue(comboBox != null);
		selectObject = value;
		comboViewer.setSelection(new StructuredSelection(value));
	}
	
	/**
	 * 更新CCombo中的值
	 */
	private void updateComboBoxItems() {
		if (comboBox != null && comboViewer != null && itemList != null) {
			comboBox.removeAll();

			comboViewer.setInput(itemList);
			
			setValueValid(true);
		}
	}
	
	/**
	 * Applies the currently selected value and deactivates the cell editor
	 */
	void applyEditorValueAndDeactivate() {
		// must set the selection before getting value
		selectObject = getSelection();
		Object newValue = doGetValue();
		markDirty();
		boolean isValid = isCorrect(newValue);
		setValueValid(isValid);

		if (!isValid) {
			int selection = comboBox.getSelectionIndex();
			if (itemList.size() > 0 && selection >= 0 && selection < itemList.size()) {
				// try to insert the current value into the error message.
				setErrorMessage(MessageFormat.format(getErrorMessage(),
						new Object[] { itemList.get(selection)}));
			} else {
				// Since we don't have a valid index, assume we're using an
				// 'edit'
				// combo so format using its text value
				setErrorMessage(MessageFormat.format(getErrorMessage(),
						new Object[] { comboBox.getText() }));
			}
		}

		fireApplyEditorValue();
		deactivate();
	}
	
	protected void focusLost() {
		if (isActivated()) {
			applyEditorValueAndDeactivate();
		}
	}
	
	/**
	 * 获得所选的对象
	 * @return
	 */
	private Object getSelection() {
		IStructuredSelection select = (IStructuredSelection) comboViewer
				.getSelection();
		
		if(select.isEmpty()) {
			return null;
			
		} else {
			return select.getFirstElement();
		}
	}
	
	protected void keyReleaseOccured(KeyEvent keyEvent) {
		if (keyEvent.character == '\u001b') { // Escape character
			fireCancelEditor();
		} else if (keyEvent.character == '\t') { // tab key
			applyEditorValueAndDeactivate();
		}
	}

	public List<Object> getItemList() {
		return itemList;
	}

	public void setItemList(List<Object> itemList) {
		Assert.isNotNull(itemList);
		this.itemList = itemList;
	}

}
