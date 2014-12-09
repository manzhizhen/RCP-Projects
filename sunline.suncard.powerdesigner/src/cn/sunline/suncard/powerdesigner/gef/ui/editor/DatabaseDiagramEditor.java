/* 文件名：     DatabaseDiagramEditor.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-6
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.ui.editor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.AlignmentAction;
import org.eclipse.gef.ui.actions.DirectEditAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.SaveAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import cn.sunline.suncard.powerdesigner.file.SwitchObjectAndFile;
import cn.sunline.suncard.powerdesigner.gef.action.CopyModelAction;
import cn.sunline.suncard.powerdesigner.gef.action.PasteAsShortcutAction;
import cn.sunline.suncard.powerdesigner.gef.action.PasteModelAction;
import cn.sunline.suncard.powerdesigner.gef.command.PasteModelCommand;
import cn.sunline.suncard.powerdesigner.gef.editpart.LineGefModelEditPart;
import cn.sunline.suncard.powerdesigner.gef.editpart.PDEditFactory;
import cn.sunline.suncard.powerdesigner.gef.editpart.TableGefModelEditPart;
import cn.sunline.suncard.powerdesigner.gef.editpart.TableShortcutGefModelEditPart;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractLineGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.DatabaseDiagramGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.LineGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableShortcutGefModel;
import cn.sunline.suncard.powerdesigner.gef.outline.TreeEditPartFactory;
import cn.sunline.suncard.powerdesigner.gef.ui.dialog.ReferencePropertiesDialog;
import cn.sunline.suncard.powerdesigner.gef.ui.dialog.TableGefModelDialog;
import cn.sunline.suncard.powerdesigner.gef.xml.LineXmlProperty;
import cn.sunline.suncard.powerdesigner.gef.xml.NodeXmlProperty;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.LineModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.TableShortcutModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.transfer.GefTableModelTransferDropTargetListener;
import cn.sunline.suncard.powerdesigner.transfer.TableModelTransfer;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.powerdesigner.xml.SwitchObjAndXml;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 数据库画图Editor
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-9-6
 * @see
 * @since 1.0
 */
public class DatabaseDiagramEditor extends GraphicalEditorWithFlyoutPalette
		implements IPropertyChangeListener {
	public final static String ID = "cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor";

	private GraphicalViewer viewer;
	private ScalableFreeformRootEditPart rootEditPart; // 设定所用的层结构
	private TreeContent physicalDiagramTreeContent;
	private DatabaseDiagramGefModel databaseDiagramGefModel;
	private PhysicalDiagramModel physicalDiagramModel;
	private PaletteRoot paletteRoot; // 调色板
	private ZoomManager manager;

	private Log logger = LogManager.getLogger(DatabaseDiagramEditor.class
			.getName());

	private TransferDropTargetListener drop_listener = new TransferDropTargetListener() {
		@Override
		public Transfer getTransfer() {
			return TableModelTransfer.getInstance();
		}

		@Override
		public boolean isEnabled(DropTargetEvent event) {
			return true;
		}

		@Override
		public void dragEnter(DropTargetEvent event) {
		}

		@Override
		public void dragLeave(DropTargetEvent event) {
		}

		@Override
		public void dragOperationChanged(DropTargetEvent event) {
		}

		@Override
		public void dragOver(DropTargetEvent event) {
		}

		@Override
		public void drop(DropTargetEvent event) {
			if (!TableModelTransfer.getInstance().isSupportedType(
					event.currentDataType)) {
				return;
			}
			
			DatabaseTreeViewPart databaseTreeViewPart;
			try {
				databaseTreeViewPart = DatabaseTreeViewPart.getInstance();
			} catch (PartInitException e) {
				logger.debug("获得默认的数据库树失败！" + e.getMessage());
				e.printStackTrace();
				return ;
			}
			
			if(databaseTreeViewPart != null) {
				IStructuredSelection select = (IStructuredSelection) databaseTreeViewPart
						.getTreeViewer().getSelection();
				if(!select.isEmpty()) {
					List<TableModel> tableModelList = new ArrayList<TableModel>();
					PasteModelCommand command = new PasteModelCommand();
					Rectangle rectangle = event.display.getBounds();
					command.setPoint(new Point(event.x - rectangle.x, event.y - rectangle.y));
					
					List<TreeContent> treeList = select.toList();
					for(TreeContent treeContent : treeList) {
						if(treeContent.getObj() instanceof TableModel) {
							tableModelList.add((TableModel) treeContent.getObj());
						}
					}
					
					command.setDragTableModelList(tableModelList);
//					command.setDragTableModelList((List<TableModel>) event.data);
					getCommandStack().execute(command);
				}
				
			}
		}

		@Override
		public void dropAccept(DropTargetEvent event) {
		}
	};
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setEditDomain(new DefaultEditDomain(this));
		setSite(site);
		super.init(site, input);

		setPartName(((DatabaseDiagramEditorInput) input).getName()); // 更新编辑器的标题

		physicalDiagramTreeContent = ((DatabaseDiagramEditorInput) input)
				.getPhysicalDiagramTreeContent();
		physicalDiagramModel = (PhysicalDiagramModel) physicalDiagramTreeContent
				.getObj();

		// 把对应的CommandStack添加到Map
		DatabaseTreeViewPart.addEditorCommandStack(physicalDiagramModel,
				getEditDomain().getCommandStack());

		// 因为可以还原，所以使用的是克隆版本的物理图形模型
		try {
			databaseDiagramGefModel = SwitchObjAndXml
					.getDatabaseDiagramGefModel(physicalDiagramModel);
		} catch (CloneNotSupportedException e) {
			logger.error("克隆TableModel对象失败！" + e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	protected void configureGraphicalViewer() {
		viewer = getGraphicalViewer();

		viewer.setEditPartFactory(new PDEditFactory()); // 设置控制器工厂
		rootEditPart = new ScalableFreeformRootEditPart(); // 设定所用的层结构
		viewer.setRootEditPart(rootEditPart); // 设定所用的层结构

		viewer.setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, false); // 设置网格不可见
		viewer.setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, true); // 设置网格吸附
		viewer.setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED, true); // 设置网格吸附

		// 获得ZoomManager
		manager = rootEditPart.getZoomManager();
		double[] zoomlevels = new double[] { 0.01, 0.05, 0.1, 0.2, 0.25, 0.5,
				0.75, 1.0, 1.25, 1.5, 1.75, 2.0, 2.25, 2.5, 3.0, 4.0, 5.0 };
		manager.setZoomLevels(zoomlevels); // 添加放大比例

		ArrayList zoomContributions = new ArrayList();
		zoomContributions.add(ZoomManager.FIT_ALL);
		zoomContributions.add(ZoomManager.FIT_HEIGHT);
		zoomContributions.add(ZoomManager.FIT_WIDTH);
		manager.setZoomLevelContributions(zoomContributions);

		// 注册放大Action
		IAction action = new ZoomInAction(manager);
		getActionRegistry().registerAction(action);

		// 缩小Action
		action = new ZoomOutAction(manager);
		getActionRegistry().registerAction(action);

		action = new PasteAsShortcutAction(this);
		getActionRegistry().registerAction(action);
		getSelectionActions().add(PasteAsShortcutAction.ID);

		// 给视图添加鼠标双击和单击事件
		viewer.getControl().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				editorParSet();
			}
		});

		// 设置右键菜单
		viewer.setContextMenu(new ModelContextMenu(viewer, getActionRegistry()));

		createKeyHandler(); // 注册快捷键
		super.configureGraphicalViewer();
	}

	@Override
	protected void initializeGraphicalViewer() {
		if (databaseDiagramGefModel == null) {
			databaseDiagramGefModel = new DatabaseDiagramGefModel();
		}

		viewer.setContents(databaseDiagramGefModel);

		// 实现从调色板拖拽来画图
		getGraphicalViewer().addDropTargetListener(
				createTransferDropTargetListener());
		// 实现从数据库树来拖曳表格
//		getGraphicalViewer().addDropTargetListener(
//				new GefTableModelTransferDropTargetListener(getGraphicalViewer()));
		
			getGraphicalViewer().addDropTargetListener(drop_listener);	
		super.initializeGraphicalViewer();
	}

	/**
	 * 给Editor的Viewer添加作为拖拽目标的监听器
	 */
	private TransferDropTargetListener createTransferDropTargetListener() {
		return new TemplateTransferDropTargetListener(getGraphicalViewer()) {
			protected CreationFactory getFactory(Object template) {
				return new SimpleFactory((Class) template);
			}
		};
	}

	/**
	 * 注册快捷键
	 */
	public void createKeyHandler() {
		// 创建键盘句柄 keyhander
		KeyHandler keyHandler = new KeyHandler();

		// 按 DEL 键时执行删除 Action
		keyHandler.put(KeyStroke.getPressed(SWT.DEL, 127, 0),
				getActionRegistry().getAction(ActionFactory.DELETE.getId()));

		// 按 F2 键时执行直接编辑 Action
		keyHandler.put(KeyStroke.getPressed(SWT.F2, 0), getActionRegistry()
				.getAction(GEFActionConstants.DIRECT_EDIT));

		// 按 Ctrl+A 键时执行直接全选
		keyHandler
				.put(KeyStroke.getReleased((char) 1, 97, SWT.CTRL),
						getActionRegistry().getAction(
								ActionFactory.SELECT_ALL.getId()));

		// 撤销Ctrl+Z
		keyHandler.put(KeyStroke.getPressed((char) 26, 122, SWT.CTRL),
				getActionRegistry().getAction(ActionFactory.UNDO.getId()));

		// 重复Ctrl+Y
		keyHandler.put(KeyStroke.getPressed((char) 25, 121, SWT.CTRL),
				getActionRegistry().getAction(ActionFactory.REDO.getId()));

		// 重复Ctrl+C
		keyHandler.put(KeyStroke.getPressed((char) 3, 99, SWT.CTRL),
				getActionRegistry().getAction(ActionFactory.COPY.getId()));

		// 重复Ctrl+V
		keyHandler.put(KeyStroke.getPressed((char) 22, 118, SWT.CTRL),
				getActionRegistry().getAction(ActionFactory.PASTE.getId()));

		getGraphicalViewer().setKeyHandler(
				new GraphicalViewerKeyHandler(getGraphicalViewer())
						.setParent(keyHandler));
	}

	@Override
	public Object getAdapter(Class type) {
		// 试图的放大和缩小的下拉列表
		if (type == ZoomManager.class) {
			return ((ScalableFreeformRootEditPart) getGraphicalViewer()
					.getRootEditPart()).getZoomManager();

		} else if (type == IContentOutlinePage.class) {
			return new PDContentOutlinePage();

		}

		return super.getAdapter(type);
	}

	@Override
	protected void createActions() {
		super.createActions();
		ActionRegistry registry = getActionRegistry();

		IAction action = new DirectEditAction((IWorkbenchPart) this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		// 水平方向对齐
		action = new AlignmentAction((IWorkbenchPart) this,
				PositionConstants.LEFT);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		// 右对齐
		action = new AlignmentAction((IWorkbenchPart) this,
				PositionConstants.RIGHT);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		// 中心对齐
		action = new AlignmentAction((IWorkbenchPart) this,
				PositionConstants.CENTER);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		// 垂直方向对齐
		action = new AlignmentAction((IWorkbenchPart) this,
				PositionConstants.TOP);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		// 居中对齐
		action = new AlignmentAction((IWorkbenchPart) this,
				PositionConstants.MIDDLE);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		IAction copyAction;
		IAction pasteAction;

		copyAction = new CopyModelAction(this);
		registry.registerAction(copyAction);
		getSelectionActions().add(copyAction.getId());

		pasteAction = new PasteModelAction(this);
		registry.registerAction(pasteAction);
		getSelectionActions().add(pasteAction.getId());

	}

	@Override
	protected PaletteRoot getPaletteRoot() {
		if (paletteRoot == null) {
			paletteRoot = new PaletteFactory().createPalette();
		}
		return paletteRoot;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// DatabaseTreeViewPart.saveFileModel(FileModel.getFileModelFromObj(
		// physicalDiagramModel));
		updateGefDataToPhysicalDiagramModel();
		// try {
		// SwitchObjectAndFile.SaveFileModelToFile(FileModel.getFileModelFromObj(physicalDiagramModel));
		getCommandStack().markSaveLocation();
		// } catch (IOException e) {
		// logger.error("保存文件失败！" + e.getMessage());
		// MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().
		// getShell(), I18nUtil.getMessage("MESSAGE"),
		// "保存文件失败！" + e.getMessage());
		// e.printStackTrace();
		// return ;
		// }
	}

	public void updateGefDataToPhysicalDiagramModel() {
		if (physicalDiagramModel == null) {
			logger.error("传入的PhysicalDiagramModel模型为空，无法保存！");
			MessageDialog.openError(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(),
					I18nUtil.getMessage("MESSAGE"),
					"传入的PhysicalDiagramModel模型为空，无法保存！");
			return;
		}

		if (physicalDiagramModel.getPackageModel() == null) {
			logger.error("PhysicalDataModel模型为空，无法保存！");
			MessageDialog.openError(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(),
					I18nUtil.getMessage("MESSAGE"),
					"PhysicalDataModel模型为空，无法保存！");
			return;
		}

		if (FileModel.getFileModelFromObj(physicalDiagramModel
				.getPackageModel()) == null) {
			logger.error("FileModel模型为空，无法保存！");
			MessageDialog.openError(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(),
					I18nUtil.getMessage("MESSAGE"), "FileModel模型为空，无法保存！");
			return;
		}

		// Map<String, TableModel> tableMap = new HashMap<String, TableModel>();
		List<NodeXmlProperty> nodeList = new ArrayList<NodeXmlProperty>();
		List<LineXmlProperty> lineList = new ArrayList<LineXmlProperty>();
		List<AbstractGefModel> abstractGefList = databaseDiagramGefModel
				.getChildren();
		// 储存该图形中的所有连接线，便于更新TableModel中的List<LineModel>
		List<AbstractLineGefModel> lineGefList = new ArrayList<AbstractLineGefModel>();
		for (AbstractGefModel abstractGefModel : abstractGefList) {
			if (abstractGefModel instanceof TableGefModel) {
				TableModel tableModel = (TableModel) abstractGefModel
						.getDataObject();

				tableModel.setPhysicalDiagramModel(physicalDiagramModel);
				// tableMap.put(tableModel.getTableName(), tableModel);
				nodeList.add(abstractGefModel.getNodeXmlProperty());
				List<AbstractLineGefModel> lineGefModelList = abstractGefModel
						.getSourceConnections();
				lineGefList.addAll(lineGefModelList);

				// // 清空该表格的LineModel的List
				tableModel.getLineModelList().clear();
				for (AbstractLineGefModel abstractLineGefModel : lineGefModelList) {
					Object targetModel = abstractLineGefModel.getTarget()
							.getDataObject();
					String targetId = "";
					if (targetModel instanceof TableModel) {
						targetId = ((TableModel) targetModel).getId();
					} else if (targetModel instanceof TableShortcutModel) {
						targetId = ((TableShortcutModel) targetModel).getId();
					}

					// 更新连接线的XML信息
					abstractLineGefModel.getLineXmlProperty().setSourceNodeId(
							tableModel.getId());
					abstractLineGefModel.getLineXmlProperty().setTargetNodeId(
							targetId);
					lineList.add(abstractLineGefModel.getLineXmlProperty());

					// 更新表格中的List<LineModel>
					LineModel lineModel = (LineModel) abstractLineGefModel
							.getDataObject();
					lineModel.setParentTableModelId(targetId);
					tableModel.getLineModelList().add(lineModel);
				}

			} else if (abstractGefModel instanceof TableShortcutGefModel) {
				nodeList.add(abstractGefModel.getNodeXmlProperty());
			}
		}

		// // 往物理图形添加修改后的TableModel
		// physicalDiagramModel.setTableMap(tableMap);
		// 往物理图形添加修改后的节点GEF信息
		physicalDiagramModel.setNodeXmlPropertyList(nodeList);
		// 往物理图形添加修改后的连接线GEF信息
		physicalDiagramModel.setLineXmlPropertyList(lineList);

		getEditDomain().getCommandStack().markSaveLocation();
	}

	/**
	 * 视图鼠标双击事件
	 */
	public void editorParSet() {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		Object obj = selection.getFirstElement();

		if (obj instanceof TableGefModelEditPart) {
			TableGefModelEditPart editPart = (TableGefModelEditPart) obj;
			TableGefModelDialog dialog = new TableGefModelDialog(getSite()
					.getShell());
			dialog.setTableGefModel((TableGefModel) editPart.getModel());

			if (IDialogConstants.OK_ID == dialog.open()) {
				getCommandStack().execute(dialog.getCommand());
			}

		} else if (obj instanceof TableShortcutGefModelEditPart) {
			TableShortcutGefModelEditPart editPart = (TableShortcutGefModelEditPart) obj;
			TableGefModelDialog dialog = new TableGefModelDialog(getSite()
					.getShell());
			dialog.setTableModel(((TableShortcutGefModel) editPart.getModel())
					.getDataObject().getTargetTableModel());

			if (IDialogConstants.OK_ID == dialog.open()) {
				getCommandStack().execute(dialog.getCommand());
			}

		} else if (obj instanceof LineGefModelEditPart) {
			LineGefModelEditPart lineGefModelEditPart = (LineGefModelEditPart) obj;
			LineGefModel lineGefModel = (LineGefModel) lineGefModelEditPart
					.getModel();
			ReferencePropertiesDialog dialog = new ReferencePropertiesDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().getActivePart().getSite()
							.getShell());
			dialog.setLineGefModel(lineGefModel);
			if (IDialogConstants.OK_ID == dialog.open()) {
				getCommandStack().execute(dialog.getCommand());
			}
		}

	}

	/**
	 * 给调色板Viewer添加作为拖拽源的监听器
	 */
	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain()) {
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(
						viewer));
			}
		};
	}

	@Override
	public void dispose() {
		doSave(null);
		DatabaseTreeViewPart.removeEditorCommandStack(physicalDiagramModel);

		super.dispose();
	}

	public DatabaseDiagramGefModel getDatabaseDiagramGefModel() {
		return databaseDiagramGefModel;
	}

	public void setDatabaseDiagramGefModel(
			DatabaseDiagramGefModel databaseDiagramGefModel) {
		this.databaseDiagramGefModel = databaseDiagramGefModel;
	}

	@Override
	public void commandStackChanged(EventObject event) {
		// 这样在调用Command时，该Editor才会变脏
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);

		// GEF上的Command操作大多都会改变文件内容，所以需要使该文件节点在树上变脏
		DefaultViewPart.refreshFileModelTreeContent();

	}

	public PhysicalDiagramModel getPhysicalDiagramModel() {
		return physicalDiagramModel;
	}

	public TreeContent getPhysicalDiagramTreeContent() {
		return physicalDiagramTreeContent;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public boolean isSaveOnCloseNeeded() {
		return true;
	}

	@Override
	public CommandStack getCommandStack() {
		return super.getCommandStack();
	}

	// @Override
	// public boolean isDirty() {
	// return false;
	// }

	// /**
	// * 和PhysicalDiagramModel、TableModel同步GEF信息
	// */
	// public static void updateGefInof() {
	// IEditorPart editorPart =
	// PlatformUI.getWorkbench().getActiveWorkbenchWindow().
	// getActivePage().getActiveEditor();
	//
	// if(editorPart instanceof DatabaseDiagramEditor) {
	// DatabaseDiagramEditor databaseDiagramEditor = (DatabaseDiagramEditor)
	// editorPart;
	// databaseDiagramEditor.updateGefDataToPhysicalDiagramModel();
	// }
	// }

	/**
	 * outline导航
	 * 
	 * @author 易振强
	 * @version 1.0 2011-12-1
	 * @see
	 * @since WF 1.0
	 */
	class PDContentOutlinePage extends ContentOutlinePage {

		// 使用SashFrom把OutLine分为两部分：显示大纲和显示鹰眼
		private SashForm sashForm;

		// 实现鹰眼的图形
		private ScrollableThumbnail thumbnail;

		private DisposeListener disposeListener;

		// 表示通过树的形式显示图形大纲
		public PDContentOutlinePage() {
			super(new TreeViewer());
		}

		@Override
		public void init(IPageSite pageSite) {
			super.init(pageSite);
		}

		@Override
		public void createControl(Composite parent) {
			// 创建sashFrom
			sashForm = new SashForm(parent, SWT.VERTICAL);

			// 添加分隔条
			getViewer().createControl(sashForm);

			// 设置Edit Domain
			getViewer().setEditDomain(getEditDomain());

			// 设置editpartFactory
			getViewer().setEditPartFactory(new TreeEditPartFactory());

			// 设置内容
			getViewer().setContents(databaseDiagramGefModel);

			// 选择同步
			getSelectionSynchronizer().addViewer(getViewer());

			// -----------------------------------------------------------------------
			// 实现鹰眼
			Canvas canvas = new Canvas(sashForm, SWT.BORDER);
			// 使用LightweightSystem绘制小图形
			LightweightSystem lws = new LightweightSystem(canvas);

			// 获得RootEditPart,绘制图形
			thumbnail = new ScrollableThumbnail(
					(Viewport) ((ScalableFreeformRootEditPart) getGraphicalViewer()
							.getRootEditPart()).getFigure());
			thumbnail
					.setSource(((ScalableFreeformRootEditPart) getGraphicalViewer()
							.getRootEditPart())
							.getLayer(LayerConstants.PRINTABLE_LAYERS));
			lws.setContents(thumbnail);

			disposeListener = new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent e) {
					// 删除绘制的图形
					if (thumbnail != null) {
						thumbnail.deactivate();
						thumbnail = null;
					}
				}
			};

			// 当graphical viewer删除图形时，删除Outline中的图形
			getGraphicalViewer().getControl().addDisposeListener(
					disposeListener);

		}

		@Override
		public Control getControl() {
			// 当大纲视图是当前（active）视图时，返回聚焦的控件
			return sashForm;
		}

		@Override
		public void dispose() {
			// 从TreeViewer删除SelectionSynchronizer
			getSelectionSynchronizer().removeViewer(getViewer());
			if (getGraphicalViewer().getControl() != null
					&& !getGraphicalViewer().getControl().isDisposed()) {
				getGraphicalViewer().getControl().removeDisposeListener(
						disposeListener);
			}

			super.dispose();
		}

	}

	@Override
	public void setPartName(String partName) {
		// TODO Auto-generated method stub
		super.setPartName(partName);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// TODO Auto-generated method stub

	}

}
