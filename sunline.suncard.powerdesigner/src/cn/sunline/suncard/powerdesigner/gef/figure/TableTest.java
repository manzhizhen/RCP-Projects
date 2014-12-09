package cn.sunline.suncard.powerdesigner.gef.figure;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.db.DataTypeModel;

public class TableTest {
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		
		shell.setText("表格图形测试");
		shell.setSize(new Point(400, 400));
		shell.setLayout(new GridLayout());

		// 创建一个画布用来展现根图形
		Canvas canvas = new Canvas(shell, SWT.DOUBLE_BUFFERED);
		canvas.setBackground(ColorConstants.white);
		canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// 创建一个根图形和一个简单的层来包含所有其他图形
		Figure root = new Figure();
		root.setFont(shell.getFont());
		
		LightweightSystem lws = new LightweightSystem(canvas);
		lws.setContents(root);
		
		XYLayout xyLayout = new XYLayout();
		root.setLayoutManager(xyLayout);
		
		TableFigure tableFigure = new TableFigure(createTaskModel());
		root.add(tableFigure);
		xyLayout.setConstraint(tableFigure, new Rectangle(10, 10, 200, 200));
		
		shell.open();
		
		while(!shell.isDisposed()) {
			while (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		display.dispose();
	}
	
	private static TableModel createTaskModel() {
		TableModel tableModel = new TableModel();
		tableModel.setTableName("BS_ROLEMAPPING");
		tableModel.setTableDesc("角色功能映射表");
		
		List<ColumnModel> columnList = tableModel.getColumnList();
		
		ColumnModel columnModel1 = new ColumnModel();
		columnModel1.setColumnName("BANKORG_ID");
		columnModel1.setColumnDesc("银行机构号");
		DataTypeModel model1 = new DataTypeModel();
		model1.setName("numeric(10,0)");
		columnModel1.setDataTypeModel(model1);
		columnModel1.setPrimaryKey(true);
		columnModel1.setCanNotNull(true);
		
		ColumnModel columnModel2 = new ColumnModel();
		columnModel2.setColumnName("PC_ID");
		columnModel2.setColumnDesc("电脑编号");
		DataTypeModel model2 = new DataTypeModel();
		model2.setName("varchar(20)");
		columnModel2.setPrimaryKey(true);
		columnModel2.setCanNotNull(true);
		
		ColumnModel columnModel3 = new ColumnModel();
		columnModel3.setColumnName("ROLE_ID");
		columnModel3.setColumnDesc("角色ID");
		DataTypeModel model3 = new DataTypeModel();
		model3.setName("varchar(10)");
		columnModel3.setPrimaryKey(true);
		columnModel3.setCanNotNull(true);
		
		ColumnModel columnModel4 = new ColumnModel();
		columnModel4.setColumnName("FUNCTION_ID");
		columnModel4.setColumnDesc("功能ID");
		DataTypeModel model4 = new DataTypeModel();
		model4.setName("varchar(10)");
		columnModel4.setPrimaryKey(true);
		columnModel4.setCanNotNull(true);
		
		ColumnModel columnModel5 = new ColumnModel();
		columnModel5.setColumnName("MODI_DATE");
		columnModel5.setColumnDesc("修改日期");
		DataTypeModel model5 = new DataTypeModel();
		model5.setName("datetime");
		columnModel5.setDataTypeModel(model5);
		
		ColumnModel columnModel6 = new ColumnModel();
		columnModel6.setColumnName("MODI_USER");
		columnModel6.setColumnDesc("修改用户");
		DataTypeModel model6 = new DataTypeModel();
		model6.setName("varchar(64)");
		
		columnList.add(columnModel1);
		columnList.add(columnModel2);
		columnList.add(columnModel3);
		columnList.add(columnModel4);
		columnList.add(columnModel5);
		columnList.add(columnModel6);
		
		return tableModel;
	}
}
