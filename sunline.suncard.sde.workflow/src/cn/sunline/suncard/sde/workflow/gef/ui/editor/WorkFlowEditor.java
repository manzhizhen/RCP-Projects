/* 文件名：     WorkFlowEditor.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.ui.editor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.AlignmentAction;
import org.eclipse.gef.ui.actions.DirectEditAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import cn.sunline.suncard.sde.workflow.Activator;
import cn.sunline.suncard.sde.workflow.action.WorkFlowActionGroup;
import cn.sunline.suncard.sde.workflow.file.SwitchObjectAndFile;
import cn.sunline.suncard.sde.workflow.gef.action.SaveToImageAction;
import cn.sunline.suncard.sde.workflow.gef.editpart.EndModelEditPart;
import cn.sunline.suncard.sde.workflow.gef.editpart.LineEditPart;
import cn.sunline.suncard.sde.workflow.gef.editpart.StartModelEditPart;
import cn.sunline.suncard.sde.workflow.gef.editpart.TaskModelEditPart;
import cn.sunline.suncard.sde.workflow.gef.editpart.WorkFlowEditFactory;
import cn.sunline.suncard.sde.workflow.gef.menu.ModelContextMenu;
import cn.sunline.suncard.sde.workflow.gef.model.AbstractModel;
import cn.sunline.suncard.sde.workflow.gef.model.EndModel;
import cn.sunline.suncard.sde.workflow.gef.model.LineModel;
import cn.sunline.suncard.sde.workflow.gef.model.StartModel;
import cn.sunline.suncard.sde.workflow.gef.model.TaskModel;
import cn.sunline.suncard.sde.workflow.gef.model.WorkFlowModel;
import cn.sunline.suncard.sde.workflow.gef.outline.TreeEditPartFactory;
import cn.sunline.suncard.sde.workflow.gef.xml.GefFigureSwitchXml;
import cn.sunline.suncard.sde.workflow.model.TaskNode;
import cn.sunline.suncard.sde.workflow.model.WorkFlowTreeNode;
import cn.sunline.suncard.sde.workflow.ui.dialog.EndModelDialog;
import cn.sunline.suncard.sde.workflow.ui.dialog.LineModelDialog;
import cn.sunline.suncard.sde.workflow.ui.dialog.StartModelDialog;
import cn.sunline.suncard.sde.workflow.ui.dialog.TaskModelDialog;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 画图的Editor
 * @author  易振强
 * @version 1.0, 2012-3-26
 * @see 
 * @since 1.0
 */
public class WorkFlowEditor extends GraphicalEditorWithFlyoutPalette implements IPropertyChangeListener{
	public final static String ID = "cn.sunline.suncard.sde.workflow.gef.ui.editor.WorkFlowEditor";
	private Log logger = LogManager.getLogger(WorkFlowEditor.class.getName());
	
	private GraphicalViewer viewer;
	
	private WorkFlowTreeNode treeNode;	  // 对应的树节点对象
	private WorkFlowModel workFlowModel;  // 被绑定的集合模型
	
	private ScalableFreeformRootEditPart rootEditPart; 	// 设定所用的层结构
	
	private String editorState;	 // Editor的状态标记
	
	private PaletteRoot paletteRoot; // 调色板
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setEditDomain(new DefaultEditDomain(this));
		
		setPartName(((WorkFlowEditorInput)input).getName()); // 更新编辑器的标题
		setSite(site);
		setInput(input);
		
		treeNode = ((WorkFlowEditorInput)input).getTreeModel();
		workFlowModel = GefFigureSwitchXml.getWorkFlowTreeNodeClone(treeNode.getModel());
		
		super.init(site, input);
	}
	
	@Override
	protected void configureGraphicalViewer() {
		viewer = getGraphicalViewer();
	
		viewer.setEditPartFactory(new WorkFlowEditFactory());	// 设置控制器工厂
		
		rootEditPart = new ScalableFreeformRootEditPart();		// 设定所用的层结构
		viewer.setRootEditPart(rootEditPart);
		
		createKeyHandler();	// 注册快捷键
		
		// 给视图添加鼠标双击和单击事件
		viewer.getControl().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				editorParSet();
			}
		});
		
		// 设置网格不可见
		viewer.setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, false);
		
		// 设置网格吸附
		viewer.setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, true);
		
		// 设置网格吸附
		viewer.setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED, true);	
		
		// 添加上下文菜单
		viewer.setContextMenu(new ModelContextMenu(viewer, getActionRegistry()));
		
		// 获得ZoomManager
		ZoomManager manager = rootEditPart.getZoomManager();

		double[] zoomlevels = new double[] {0.25, 0.5, 0.75, 1.0, 1.25, 1.5, 1.75, 2.0, 2.25,
				2.5, 3.0, 4.0, 5.0};
		// 添加放大比例
		manager.setZoomLevels(zoomlevels);

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
		
		super.configureGraphicalViewer();
	}	
	
	@Override
	protected void initializeGraphicalViewer() {
		if(workFlowModel == null ) {
			if(treeNode != null) {
				treeNode = ((WorkFlowEditorInput)getEditorInput()).getTreeModel();
				workFlowModel = treeNode.getModel();
			}
			
			// 如果还为空，则新建一个集合对象
			if(workFlowModel == null) {
				workFlowModel = new WorkFlowModel();
			}
		}
		
		viewer.setContents(workFlowModel);
		checkModel(workFlowModel);
		
		getGraphicalViewer().addDropTargetListener(createTransferDropTargetListener());

		
		super.initializeGraphicalViewer();
	}
	
	/**
	 * 给调色板Viewer添加作为拖拽源的监听器
	 */
	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain()) {
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
			}
		};
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
	 * 检查画图的正确性
	 */
	public static void checkModel(WorkFlowModel model) {
		
	}
	
	@Override
	public void setPartName(String partName) {
		if(WorkFlowActionGroup.MODIFY_FLAG.equals(editorState)) {
			partName += "-" + I18nUtil.getMessage("MODIFY_EDITOR");
			
		} else if(WorkFlowActionGroup.VIEW_FLAG.equals(editorState)) {
			partName += "-" + I18nUtil.getMessage("MODIFY_EDITOR");
		}
		super.setPartName(partName);
	}
	
	/**
	 * 导出工作流xml之前检查正确性
	 * @param String 返回的错误信息
	 */
	public static String checkXML(WorkFlowModel model) {
		List<AbstractModel> modelList = model.getChildren();
		for(AbstractModel modelTemp : modelList) {
			if(modelTemp instanceof StartModel) {
				String name = ((StartModel)modelTemp).getStartNode().getName();
				if(name == null || "".equals(name)) {
					return "开始节点的名称为空！";
				}
				
			} else if(modelTemp instanceof TaskModel) {
				TaskNode taskNode = ((TaskModel)modelTemp).getTaskNode();
				String name = taskNode.getName();
				String type = taskNode.getType();
				if(name == null || "".equals(name.trim()) || type == null || "".equals(type.trim())) {
					return "任务节点的名称或分配方式为空！";
				}
				
				String taskName = taskNode.getTask().getName();
				if(taskName == null || "".equals(taskName.trim())) {
					return "任务节点" + name + "的任务名称为空！";
				}
				
			} else if (modelTemp instanceof EndModel) {
				String name = ((EndModel)modelTemp).getEndNode().getName();
				if(name == null || "".equals(name)) {
					return "结束节点的名称为空！";
				}
				
			}
		}
		
		return null;
	} 
	
	@Override
	public Object getAdapter(Class type) {
		// 试图的放大和缩小的下拉列表
		if (type == ZoomManager.class) {
			return ((ScalableFreeformRootEditPart) getGraphicalViewer()
					.getRootEditPart()).getZoomManager();
			
		} else if (type == IContentOutlinePage.class) {
			return new WorkFlowContentOutlinePage();
			
		}

		return super.getAdapter(type);
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
		
		// 保存为图片的Action
		IAction saveToImageAction = new SaveToImageAction(this);
		getActionRegistry().registerAction(saveToImageAction);
		getSelectionActions().add(SaveToImageAction.ID);

	}
	
	/**
	 * 视图鼠标双击事件
	 */
	public void editorParSet() {

		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		Object obj = selection.getFirstElement();
		
		if(obj instanceof StartModelEditPart) {
			StartModelDialog dialog = new StartModelDialog(getSite().getShell());
			
			dialog.setStartModel((StartModel) ((StartModelEditPart)obj).getModel());
			dialog.setCommandStack(getCommandStack());
			dialog.open();
			 
		} else if(obj instanceof TaskModelEditPart) {
			TaskModelDialog dialog = new TaskModelDialog(getSite().getShell());
			
			dialog.setTaskModel((TaskModel) ((TaskModelEditPart)obj).getModel());
			dialog.setCommandStack(getCommandStack());
			dialog.open();
			
		} else if(obj instanceof EndModelEditPart) {
			EndModelDialog dialog = new EndModelDialog(getSite().getShell());
			
			dialog.setEndModel((EndModel) ((EndModelEditPart)obj).getModel());
			dialog.setCommandStack(getCommandStack());
			dialog.open();
			
		} else if(obj instanceof LineEditPart) {
			AbstractModel model = ((LineModel) ((LineEditPart)obj).getModel()).getSource();
			
			if(model instanceof TaskModel) {
				LineModelDialog dialog = new LineModelDialog(getSite().getShell());
				TaskModel taskModel = (TaskModel) model;
				dialog.setTaskModel(taskModel);
				dialog.setLineModel((LineModel) ((LineEditPart)obj).getModel());
				dialog.setCommandStack(getCommandStack());
				dialog.open();
			}
		}
			
		
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void commandStackChanged(EventObject event) {
		// 这样在调用Command时，该Editor才会变脏
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}
	
	@Override
	public boolean isDirty() {
		return super.isDirty();
	}
	
	@Override
	protected PaletteRoot getPaletteRoot() {
		if (paletteRoot == null) {
			paletteRoot =  new PaletteFactory().createPalette();
		}
		return paletteRoot;
	}

	
	
	public ScalableFreeformRootEditPart getRootEditPart() {
		return rootEditPart;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			treeNode.setModel(workFlowModel);
			SwitchObjectAndFile.SaveWorkFlowToFile(treeNode);
		} catch (IOException e) {
			logger.error("保存对象到文件失败！" + e.getMessage());
			e.printStackTrace();
		}
		
		getCommandStack().markSaveLocation();
	}

	public WorkFlowModel getWorkFlowModel() {
		return workFlowModel;
	}

	public void setWorkFlowModel(WorkFlowModel workFlowModel) {
		this.workFlowModel = workFlowModel;
	}

	public String getEditorState() {
		return editorState;
	}

	public void setEditorState(String editorState) {
		this.editorState = editorState;
	}

	public WorkFlowTreeNode getTreeNode() {
		return treeNode;
	}

	public void setTreeNode(WorkFlowTreeNode treeNode) {
		this.treeNode = treeNode;
	}
	
	/**
	 * outline导航
	 * @author    易振强
	 * @version   1.0  2011-12-1
	 * @see       
	 * @since     WF  1.0
	 */
	class WorkFlowContentOutlinePage extends ContentOutlinePage {

		// 使用SashFrom把OutLine分为两部分：显示大纲和显示鹰眼
		private SashForm sashForm;

		// 实现鹰眼的图形
		private ScrollableThumbnail thumbnail;

		private DisposeListener disposeListener;

		// 表示通过树的形式显示图形大纲
		public WorkFlowContentOutlinePage() {
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
			getViewer().setContents(workFlowModel);
			
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
}



