/* 文件名：     IndexAllShowDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-12-24
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.IndexSqlModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.WorkSpaceModel;
import cn.sunline.suncard.powerdesigner.provider.IndexAllShowProvider;
import org.eclipse.jface.viewers.TableViewer;
/**
 * 用来展示和管理所有的索引，双击该索引进行修改
 * @author  Agree
 * @version 1.0, 2012-12-24
 * @see 
 * @since 1.0
 */
public class IndexAllShowDialog extends TitleAreaDialog{

	/**
	 * @param parentShell
	 */
	private TableViewer tableViewer;
	private Table table;
	private ArrayList<IndexSqlModel> indexSqlModels;
	public IndexAllShowDialog(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
		setShellStyle(SWT.MAX | SWT.MIN | SWT.Close | SWT.RESIZE | SWT.PRIMARY_MODAL );
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// TODO Auto-generated method stub
		setTitle("索引总览");
		setMessage("索引总览");

		Control control = (Control) super.createDialogArea(parent);
		Composite composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new FormLayout());

		Label label = new Label(composite, SWT.NONE);
		FormData fd_label = new FormData();
		fd_label.top = new FormAttachment(0, 10);
		fd_label.left = new FormAttachment(0, 10);
		label.setLayoutData(fd_label);
		label.setText("索引总览：");

		tableViewer = new TableViewer(composite,  SWT.BORDER | SWT.FULL_SELECTION
				| SWT.MULTI);
		
		table = tableViewer.getTable();
		FormData fd_table = new FormData();
		fd_table.top = new FormAttachment(label, 6);
		fd_table.left = new FormAttachment(0, 6);
		fd_table.right = new FormAttachment(100, -10);
		fd_table.bottom = new FormAttachment(100, -10);
		table.setLayoutData(fd_table);
		
		table.setHeaderVisible(true);
		table.setLinesVisible(true); // 设置表格线可见
		
		TableColumn columnChoice = new TableColumn(table, SWT.BORDER);
		columnChoice.setText("");
		columnChoice.setWidth(20);
		columnChoice.setAlignment(SWT.CENTER);

		TableColumn columnID = new TableColumn(table, SWT.BORDER);
		//表名
		columnID.setText("表名");
		columnID.setWidth(100);
		columnID.setAlignment(SWT.CENTER);

		TableColumn columnIndexName = new TableColumn(table, SWT.CENTER);
		//"索引名
		columnIndexName.setText("索引名");
		columnIndexName.setWidth(150);
		columnIndexName.setAlignment(SWT.CENTER);
		
		TableColumn columnIndexDesc = new TableColumn(table, SWT.CENTER);
		//描述
		columnIndexDesc.setText("索引描述");
		columnIndexDesc.setWidth(200);
		columnIndexDesc.setAlignment(SWT.CENTER);
		
		TableColumn columnIndexField = new TableColumn(table, SWT.CENTER);
		//描述
		columnIndexField.setText("索引字段");
		columnIndexField.setWidth(300);
		columnIndexField.setAlignment(SWT.CENTER);
		
		try {
			initControl();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		creatEvent();
		
		return composite;
	}
	
	/**
	 * 填充表格内容
	 * @throws CloneNotSupportedException 
	 */
	private void initControl() throws CloneNotSupportedException {
		//得到所有tableModel
		WorkSpaceModel workSpaceModel = new WorkSpaceModel();
		List<TableModel> allTableModel = new ArrayList<TableModel>();
		 Set<FileModel> fileModels = workSpaceModel.getFileModelSet();
		 for(FileModel model : fileModels){
			 allTableModel.addAll(model.getAllTableModel());
		 }
		//得到所有IndexModel的克隆
		 indexSqlModels = new ArrayList<IndexSqlModel>();
		for(TableModel tableModel : allTableModel){
			for(IndexSqlModel indexSqlModel : tableModel.getIndexSqlModelSet()){
				indexSqlModels.add(indexSqlModel.clone());
			}
		}
		
		// TODO Auto-generated method stub
		tableViewer.setLabelProvider(new IndexAllShowProvider(indexSqlModels));
		tableViewer.setContentProvider(new ArrayContentProvider());
		
		tableViewer.setInput(indexSqlModels);
		
	}
	
	/**
	 * 打开Column属性对话框
	 */
	protected void openIndexManagerDialog() {
		IStructuredSelection select = (IStructuredSelection) tableViewer
				.getSelection();
		if (select.isEmpty()) {
			return;
		}

		IndexSqlModel oldIndexSqlModel = (IndexSqlModel) select.getFirstElement();

//		IndexEditDialog sqlEditDialog = new IndexEditDialog(getShell(), oldSql,
//				columnModelList);
		IndexManagerDialog indexManagerDialog = new IndexManagerDialog(
				getShell(), oldIndexSqlModel.getTableModel());

		int returnCode = indexManagerDialog.open();
		if (returnCode == IDialogConstants.OK_ID) {
			
			IndexSqlModel newSql = indexManagerDialog.getNewIndexSqlModel();
			int index = indexSqlModels.indexOf(oldIndexSqlModel);
			indexSqlModels.set(index, newSql);
		}

		if (getShell() != null && !getShell().isDisposed()) {
			tableViewer.refresh();
		}
	}
	
	
	/**
	 * 
	 */
	private void creatEvent() {
		// TODO Auto-generated method stub
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				// TODO Auto-generated method stub
				openIndexManagerDialog();
			}
		});
	}
	
	/**
	 * 绘制窗口大小
	 */
	@Override
	protected Point getInitialSize() {
		// TODO Auto-generated method stub
		return new Point(800, 600);
	}
}
