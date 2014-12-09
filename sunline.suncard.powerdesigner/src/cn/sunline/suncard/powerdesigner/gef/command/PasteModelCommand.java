/* 文件名：     PasteModelCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	粘贴模型的Command
 * 修改人：     易振强
 * 修改时间：2011-11-21
 * 修改内容：创     建
 */
package cn.sunline.suncard.powerdesigner.gef.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorPart;

import cn.sunline.suncard.powerdesigner.gef.editpart.DatabaseDiagramModelEditPart;
import cn.sunline.suncard.powerdesigner.gef.figure.TableFigure;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractLineGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableGefModel;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.gef.xml.GefFigureSwitchXml;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.db.DatabaseTypeModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 粘贴模型的Command
 * 
 * @author 易振强
 * @version [1.0, 2011-11-01]
 * @see
 * @since 1.0
 */
public class PasteModelCommand extends Command {
	// 从剪切板中的数据
	private List<AbstractGefModel> pasteGefList;
	private List<AbstractGefModel> copyGefList;
	
	// 从数据库树拖过来的数据
	private List<TableModel> dragTableModelList;
	
	private Point point; // 当前鼠标指针的位置

	private DatabaseDiagramEditor databaseDiagramEditor;
	private Map<AbstractGefModel, AbstractGefModel> gefModelMap;
	public static Log logger = LogManager.getLogger(PasteModelCommand.class
			.getName());

	public PasteModelCommand() {
		// 从剪贴板获取要复制的内容，保存到copyList中，
		copyGefList = (ArrayList<AbstractGefModel>) Clipboard.getDefault()
				.getContents();
	}

	@Override
	public boolean canExecute() {
		if (dragTableModelList == null && (copyGefList == null || copyGefList.isEmpty() 
				|| !(copyGefList.get(0).getDataObject() instanceof TableModel))) {
			return false;
		}
		
		databaseDiagramEditor = null;
		IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActiveEditor();

		if (part instanceof DatabaseDiagramEditor) {
			databaseDiagramEditor = (DatabaseDiagramEditor) part;
		} else {
			logger.error("无法获取当前活跃的DatabaseDiagramEditor，PasteModelCommand执行失败！");
			return false;
		}
		
		DatabaseTypeModel currentDatabaseTypeModel = databaseDiagramEditor
				.getPhysicalDiagramModel().getPackageModel().getPhysicalDataModel().getDatabaseTypeModel();
		
		DatabaseTypeModel pasteDatabaseTypeModel;
		if(dragTableModelList != null) {
			pasteDatabaseTypeModel = dragTableModelList.get(0).getPhysicalDiagramModel()
					.getPackageModel().getPhysicalDataModel().getDatabaseTypeModel();
		} else {
			pasteDatabaseTypeModel = ((TableModel)copyGefList.get(0).getDataObject())
					.getPhysicalDiagramModel().getPackageModel().getPhysicalDataModel().getDatabaseTypeModel();
		}
			
		
		if(!currentDatabaseTypeModel.equals(pasteDatabaseTypeModel)) {
			logger.info("粘贴的物体图形模型对应的数据库类型和剪切板的数据对应的数据库类型不同，无法粘贴！");
			return false;
		}
		
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute() {
		logger.info("void execute() start...");

		if (!canExecute()) {
			return;
		}

		// 克隆对象，保存到pasteList中去，
		pasteGefList = new ArrayList<AbstractGefModel>();

		// 如果是从树上拖拽表格过来
		if(dragTableModelList != null) {
			int x, y;
			if(point == null) {
				x = y = 100;
			} else {
				x = point.x;
				y = point.y;
			}
			
			int num = 0;
			for(TableModel tableModel : dragTableModelList) {
				TableGefModel tableGefModel = new  TableGefModel();
				try {
					TableModel cloneTableModel = tableModel.clone(true);
					// 设置为自动改变大小
					cloneTableModel.setAutoSize(true);
					tableGefModel.setDataObject(cloneTableModel);
					tableGefModel.getConstraint().setLocation(x + num * TableFigure.FIGURE_WIDTH
							, y + num * TableFigure.FIGURE_HIGHT);
					
					pasteGefList.add(tableGefModel);
					num ++;
				} catch (CloneNotSupportedException e) {
					logger.error("克隆表格模型错误，PasteModelCommand执行失败！" + e.getMessage());
					e.printStackTrace();
					return ;
				}
			}
			
		// 如果是直接在Editor上复制并在Editor上粘贴
		} else {
			gefModelMap = new HashMap<AbstractGefModel, AbstractGefModel>();
			Map<TableModel, TableModel> tableModelMap = new HashMap<TableModel, TableModel>();
			
			// 先把新模型克隆出来放进Map
			for (AbstractGefModel oldModel : copyGefList) {
				AbstractGefModel newModel = (AbstractGefModel) oldModel.clone();
				Rectangle rect = oldModel.getConstraint();
				
				// 设置新模型出现的位置，在旧模型的右上方
				newModel.setConstraint(new Rectangle(rect.x + 100, rect.y - 100,
						rect.width, rect.height));
				
				gefModelMap.put(oldModel, newModel);
				tableModelMap.put((TableModel) oldModel.getDataObject(),
						(TableModel) newModel.getDataObject());
			}
			
			databaseDiagramEditor = null;
			IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().getActiveEditor();
			
			if (part instanceof DatabaseDiagramEditor) {
				databaseDiagramEditor = (DatabaseDiagramEditor) part;
			} else {
				logger.error("无法获取当前活跃的DatabaseDiagramEditor，PasteModelCommand执行失败！");
				return;
			}
			
			Set<AbstractGefModel> set = gefModelMap.keySet();
			
			for (AbstractGefModel model : set) {
				pasteGefList.add(gefModelMap.get(model));
			}
			
			// 由于TableModel用的是克隆，所以里面的外键信息还是指向旧表格的ColumnModel的ID，所以这里需要进行更新
			// 当然还包括拷贝的表格中有拷贝表格外的表格的外键，则需要剔除
			Set<TableModel> tableModelSet = tableModelMap.keySet();
			for (AbstractGefModel model : pasteGefList) {
				if (model instanceof TableGefModel) {
					TableGefModel newModel = (TableGefModel) model;
					List<ColumnModel> columnList = newModel.getDataObject()
							.getColumnList();
					
					// 遍历新表的ColumnModel的List，来一个个修正其外键
					for (ColumnModel columnModel : columnList) {
						if (columnModel.isForeignKey()) {
							ColumnModel parentColumnModel = ColumnModelFactory
									.getColumnModel(FileModel
											.getFileModelFromObj(newModel
													.getDataObject()), columnModel
													.getParentTableColumnId());
							if (parentColumnModel != null
									&& parentColumnModel.getTableModel() != null) {
								TableModel oldTableModel = parentColumnModel
										.getTableModel();
								// 如果在copyList中找的到，则需要改其外键ID指向新表的对应字段
								if (tableModelSet.contains(oldTableModel)) {
									String columnName = columnModel.getColumnName();
									TableModel newTableModel = tableModelMap
											.get(oldTableModel);
									// 遍历新表，找到对应的名称相同的ColumnModel
									List<ColumnModel> newColumnList = newTableModel
											.getColumnList();
									for (ColumnModel newColumnModel : newColumnList) {
										if(columnName.equals(newColumnModel.getColumnName())) {
											columnModel.setParentTableColumnId(newColumnModel.getId());
											break ;
										}
									}
									// 如果不包含，说明是其他表的外键，则取消其外键资格
								} else {
									columnModel.setParentTableColumnId(null);
								}
							} else {
								logger.error("在ColumnModelFactory中找不到该ID的列对象：" + columnModel
										.getParentTableColumnId());
								continue ;
							}
						}
					}
					
				}
			}
		}
		
		// 为了实现高度统一，模型创建还是采用本来的创建命令来创建
		for (AbstractGefModel node : pasteGefList) {
			CreateModelCommand createCommand = new CreateModelCommand();

			createCommand
					.setDatabaseDiagramGefModel(((DatabaseDiagramEditor) databaseDiagramEditor)
							.getDatabaseDiagramGefModel());
			createCommand.setModel(node);
			createCommand.setPaste(true); // 标记是粘贴调用的该Command
			if(node.getDataObject() instanceof TableModel) {
				createCommand.setNewTableName(((TableModel)node.getDataObject()).getTableName());
				createCommand.setNewTableDesc(((TableModel)node.getDataObject()).getTableDesc());
			}

			((DatabaseDiagramEditor) databaseDiagramEditor).getCommandStack()
					.execute(createCommand);
		}

		// 如果是从剪切板取的数据，还需要对连接线进行处理
		if(dragTableModelList == null) {
			// 再次遍历，来创建模型之间的连线
			for (AbstractGefModel oldModel : copyGefList) {
				AbstractGefModel newModel = gefModelMap.get(oldModel);
				
				List<AbstractLineGefModel> sourceConnection = oldModel
						.getSourceConnections();
				
				for (AbstractLineGefModel temp : sourceConnection) {
					// 如果该连线两端的对象都在需要复制的模型中，则复制该连接线
					if (copyGefList.contains(temp.getTarget())) {
						AbstractLineGefModel newConnection = (AbstractLineGefModel) temp
								.clone();
						newConnection.setSource(newModel);
						newConnection.setTarget(gefModelMap.get(temp.getTarget()));
						
						// 需要重新初始化连接线的LineXmlPropertyFactory信息
						GefFigureSwitchXml.initLineProperty(newConnection,
								FileModel.getFileModelFromObj(databaseDiagramEditor
										.getPhysicalDiagramModel()));
						
						newModel.addSourceConnection(newConnection);
						gefModelMap.get(temp.getTarget()).addTargetConnection(
								newConnection);
					}
				}
				
			}
		}
		
	}
	

	@Override
	public boolean canUndo() {
		return !(pasteGefList == null || pasteGefList.isEmpty());
	}

	@Override
	public void undo() {
		if (databaseDiagramEditor != null) {
			Collection<AbstractGefModel> list = gefModelMap.values();
			for(AbstractGefModel abstractGefModel : list) {
				if(abstractGefModel instanceof TableGefModel) {
					TableGefModel tableGefModel = (TableGefModel) abstractGefModel;
					DelTableModelCommand command = new DelTableModelCommand();
					command.setDatabaseDiagramGefModel(tableGefModel.getParentGefModel());
					command.setGefModel(tableGefModel);
					command.execute();
				}
			}
		}
	}

	public void setDragTableModelList(List<TableModel> dragTableModelList) {
		this.dragTableModelList = dragTableModelList;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	
}
