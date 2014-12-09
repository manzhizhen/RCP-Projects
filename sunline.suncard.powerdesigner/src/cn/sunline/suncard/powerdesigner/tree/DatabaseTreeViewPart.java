package cn.sunline.suncard.powerdesigner.tree;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.DocumentException;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.part.ViewPart;


import cn.sunline.suncard.powerdesigner.action.DatabaseAction;
import cn.sunline.suncard.powerdesigner.action.DatabaseActionGroup;
import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.file.SwitchObjectAndFile;
import cn.sunline.suncard.powerdesigner.gef.command.CopyColumnModelCommand;
import cn.sunline.suncard.powerdesigner.gef.command.CopyModelCommand;
import cn.sunline.suncard.powerdesigner.gef.command.PasteColumnModelCommand;
import cn.sunline.suncard.powerdesigner.gef.figure.TableFigure;
import cn.sunline.suncard.powerdesigner.gef.model.TableGefModel;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditorInput;
import cn.sunline.suncard.powerdesigner.gef.xml.LineXmlPropertyFactory;
import cn.sunline.suncard.powerdesigner.handler.OpenHandler;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.DictionaryModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.PackageModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.TableShortcutModel;
import cn.sunline.suncard.powerdesigner.model.WorkSpaceModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.model.factory.PhysicalDiagramModelFactory;
import cn.sunline.suncard.powerdesigner.model.factory.TableModelFactory;
import cn.sunline.suncard.powerdesigner.model.factory.TableShortcutModelFactory;
import cn.sunline.suncard.powerdesigner.models.DefaultColumnsNodeModel;
import cn.sunline.suncard.powerdesigner.models.DictionarysNodeModel;
import cn.sunline.suncard.powerdesigner.models.DomainsNodeModel;
import cn.sunline.suncard.powerdesigner.models.TablesNodeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.transfer.TableModelDragSourceListener;
import cn.sunline.suncard.powerdesigner.transfer.TableModelTransfer;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeViewComposite;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 数据库树
 * @author  Manzhizhen
 * @version 1.0, 2012-11-23
 * @see 
 * @since 1.0
 */
public class DatabaseTreeViewPart extends DefaultViewPart{
	public final static String ID = "cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart";
	private static Log logger = LogManager.getLogger(DatabaseTreeViewPart.class.getName());
	private Tree tree;
	private TreeViewer treeViewer;
	
	private TreeViewComposite treeViewComposite;
	
	private static Object SEMAPHORE = new Object();
	private static DatabaseTreeViewPart instance;
	
	private static Map<PhysicalDiagramModel, CommandStack> editorCommandStackMap = new 
			HashMap<PhysicalDiagramModel, CommandStack>();
	
	/**
	 * @deprecated
	 */
	public DatabaseTreeViewPart() {
	}
	
	/**
	 * 单例
	 * @return
	 * @throws PartInitException 
	 */
	public static DatabaseTreeViewPart getInstance() throws PartInitException {
		if (instance == null) {
			IWorkbenchPage workbenchPage = (WorkbenchPage) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
			.getActivePage();
			instance = (DatabaseTreeViewPart) workbenchPage.findView(DatabaseTreeViewPart.ID);
			
			if(instance == null) {
				instance = (DatabaseTreeViewPart) workbenchPage.showView(DatabaseTreeViewPart.ID);
			}
		}
		
		return instance;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		treeViewComposite = new TreeViewComposite(parent, false);
		
		tree = treeViewComposite.getTree();
		treeViewer = treeViewComposite.getTreeViewer();
		
		// 创建树的内容
		createContent();
		
		// 创建树的右键菜单
		createTreeMenu();
		
		createEvent();
		
		// 创建树的拖拽事件
		createDndEvent();
		
	}
	
	/**
	 * 创建树的拖拽事件
	 */
	private void createDndEvent() {
		// 将dragLabel指定为DragSource(一个widget只能帮定在一个DragSource)，
        // 并允许数据可以从DragSource被MOVE或COPY 
       DragSource source = new DragSource(tree, DND.DROP_MOVE | DND.DROP_COPY);
       source.setTransfer(new Transfer[]{ TableModelTransfer.getInstance()}); //  指定允许的传输类型 
       source.addDragListener(new TableModelDragSourceListener(this));
	}

	private void createEvent() {
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				mouseDoubleEvent();
			}
		});
		
		//Ctrl+S来保存某个文件
		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.stateMask & SWT.CTRL) != 0 && e.keyCode == (int)'s') {
					TreeContent treeContent = getSelection();
					if(treeContent != null ) {
						saveFileModel(FileModel.getFileModelFromObj(treeContent.getObj()));
					}
				} 
			}
		});
		
		tree.addKeyListener(new KeyAdapter() {
			//Ctrl+C来复制某些对象
			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.stateMask & SWT.CTRL) != 0 && e.keyCode == (int)'c') {
					IStructuredSelection select = (IStructuredSelection) treeViewer.getSelection();
					if(!select.isEmpty()) {
						// 通过选择的第一个元素来判断是复制表格还是复制列对象
						TreeContent firstTreeContent = (TreeContent) select.getFirstElement();
						List<TreeContent> selectTreeContent = select.toList();
						// 如果是复制表格
						if(firstTreeContent.getObj() instanceof TableModel) {
							List<Object> selectTableGefModel = new ArrayList<Object>();
							int index = 0;
							for(TreeContent treeContent : selectTreeContent) {
								if(treeContent.getObj() instanceof TableModel) {
									TableGefModel tableGefModel = new TableGefModel();
									tableGefModel.setDataObject((TableModel)treeContent.getObj());
									tableGefModel.getConstraint().setBounds(index * TableFigure.FIGURE_WIDTH, 
											index * TableFigure.FIGURE_WIDTH, TableFigure.FIGURE_WIDTH
											, TableFigure.FIGURE_WIDTH);
									tableGefModel.getNodeXmlProperty().setRectangle(tableGefModel.getConstraint());
									selectTableGefModel.add(tableGefModel);
									
									index++;
								}
							}
							
							CopyModelCommand command = new CopyModelCommand(selectTableGefModel);
							command.execute();
						
						// 如果是复制Domains
						} else if(firstTreeContent.getObj() instanceof ColumnModel) {
							List<ColumnModel> columnModelList = new ArrayList<ColumnModel>();
							for(TreeContent treeContent : selectTreeContent) {
								if(treeContent.getObj() instanceof ColumnModel) {
									ColumnModel columnModel = (ColumnModel) treeContent.getObj();
									if(columnModel.isDomainColumnModel()) {
										columnModelList.add(columnModel);
									}
								}
							}
							
							CopyColumnModelCommand command = new CopyColumnModelCommand();
							command.setCopyColumnModelList(columnModelList);
							command.execute();
						}
					}
					
				//Ctrl+O来打开某些对象
				} else if((e.stateMask & SWT.CTRL) != 0 && e.keyCode == (int)'o') {
					try {
						new OpenHandler().execute(null);
					} catch (ExecutionException e1) {
						logger.debug("打开文件失败！" + e1.getMessage());
						MessageDialog.openError(getSite().getShell()
								, I18nUtil.getMessage("MESSAGE"), "打开文件失败！" + e1.getMessage());
						e1.printStackTrace();
					}
				// 在Domains节点上粘贴ColumnModel
				} else if((e.stateMask & SWT.CTRL) != 0 && e.keyCode == (int)'v') {
					TreeContent selectTreeContent = getSelection();
					if(selectTreeContent != null && selectTreeContent.getObj() instanceof DomainsNodeModel) {
						DomainsNodeModel domainsNodeModel = (DomainsNodeModel) selectTreeContent.getObj();
						CommandStack commandStack = DefaultViewPart.getFileCommandFromObj(domainsNodeModel.getPhysicalDataModel());
						if(commandStack != null) {
							PasteColumnModelCommand command = new PasteColumnModelCommand();
							command.setDomainsTreeContent(selectTreeContent);
							commandStack.execute(command);
						} else {
							logger.debug("无法获取对应的CommandStack，执行PasteColumnModelCommand失败！");
						}
					}
				}
			}
		});
	}
	
	/**
	 * 获得数据库树上所选择的节点
	 * @return
	 */
	public TreeContent getSelection() {
		IStructuredSelection select = (IStructuredSelection) treeViewer.getSelection();
		if(select.isEmpty()) {
			return null;
		} else {
			return (TreeContent)select.getFirstElement();
		}
	}

	/**
	 * 创建树的内容
	 */
	public void createContent() {
		Image image = CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, 
				IDmImageKey.WORK_SPACE_IMAGE);
		
		// 添加一棵树
		treeViewComposite.createTreeRootNode(DmConstants.WORK_SPACE_TREE_ID, 
				new WorkSpaceModel(), image);
	}
	
	/** 
	 * 创建菜单
	 */
	private void createTreeMenu() {
		DatabaseActionGroup databaseActionGroup = new DatabaseActionGroup(treeViewer);
		databaseActionGroup.fillContextMenu(new MenuManager());
	}
	
	/**
	 * 添加一个文件
	 * @throws CanNotFoundNodeIDException 
	 * @throws FoundTreeNodeNotUniqueException 
	 * @throws AddFileModelToWorkSpaceModelFail 
	 */
	public void addFileModel(FileModel model) throws CanNotFoundNodeIDException, FoundTreeNodeNotUniqueException {
		if(model == null) {
			logger.error("传入的FileModel为空，无法在DatabaseTreeViewPart上添加该文件！");
			return;
		}
		
		// 把该文件节点添加到工作空间
		WorkSpaceModel.getFileModelSet().add(model);
		ColumnModelFactory.addFileModel(model);
		TableModelFactory.addFileModel(model);
		TableShortcutModelFactory.addFileModel(model);
		LineXmlPropertyFactory.addFileModel(model);
		DefaultViewPart.addFileCommandStack(model, new CommandStack());
		
		// 添加文件节点
		TreeContent fileContent = treeViewComposite.addNode(DmConstants.WORK_SPACE_TREE_ID, 
				model.getFile().getAbsolutePath(), model, 
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, IDmImageKey.SPD_FILE_IMAGE_16));
		
		Set<PhysicalDataModel> physicalDataSet = model.getPhysicalDataSet();
		// 添加该文件下的所有物理模型节点
		for(PhysicalDataModel physicalDataModel : physicalDataSet) {
			physicalDataModel.setFileModel(model);
			addPhysicalDataModel(physicalDataModel, fileContent);
		}
		
	}
	
	/**
	 * 关闭一个文件
	 * @throws CanNotFoundNodeIDException 
	 * @throws FoundTreeNodeNotUniqueException 
	 * @throws PartInitException 
	 * @throws AddFileModelToWorkSpaceModelFail 
	 */
	public void closeFileModel(TreeContent fileModelTreeContent) throws CanNotFoundNodeIDException, 
			FoundTreeNodeNotUniqueException, PartInitException {
		
		if(fileModelTreeContent == null || !(fileModelTreeContent.getObj() instanceof FileModel)) {
			logger.error("传入的树节点新不完整，无法关闭该文件！");
			return;
		}
		
		FileModel fileModel = (FileModel) fileModelTreeContent.getObj();
		
		// 把该文件节点从工作空间移除
		WorkSpaceModel.getFileModelSet().remove(fileModel);
		ColumnModelFactory.removeFileModel(fileModel);
		DefaultViewPart.removeFileCommandStack(fileModel);
		LineXmlPropertyFactory.removeFileModel(fileModel);
		
		// 移除文件节点
		TreeContent fileContent = treeViewComposite.removeNode(fileModelTreeContent.getId());
		
		// 如果此时有打开相关的FileModel的Editor，则需要关闭。
		IEditorReference[] editorReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
		for(IEditorReference editorReference : editorReferences) {
			if(editorReference.getEditor(false) instanceof DatabaseDiagramEditor) {
				if(editorReference.getEditorInput() instanceof DatabaseDiagramEditorInput) {
					DatabaseDiagramEditorInput databaseDiagramEditorInput = (DatabaseDiagramEditorInput) 
							editorReference.getEditorInput();
					PhysicalDiagramModel physicalDiagramModel = (PhysicalDiagramModel) 
							databaseDiagramEditorInput.getPhysicalDiagramTreeContent().getObj();
					if(fileModel.equals(FileModel.getFileModelFromObj(physicalDiagramModel.getPackageModel()))) {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().
							closeEditor(editorReference.getEditor(false), false);
					}
					
				}
			}
		}
		
	}
	
	/**
	 * 添加一个物理数据模型
	 * @param PhysicalDataModel 需要添加的物理数据模型
	 * @param String 父级节点ID
	 * @throws CanNotFoundNodeIDException 
	 * @throws FoundTreeNodeNotUniqueException 
	 */
	public TreeContent addPhysicalDataModel(PhysicalDataModel model, TreeContent parentTreeContent) 
			throws CanNotFoundNodeIDException, FoundTreeNodeNotUniqueException {
		
		if(model == null || parentTreeContent == null || parentTreeContent.getObj() == null) {
			logger.error("传入的PhysicalDataModel或父亲节点为空，无法添加该物理数据模型！");
			return null;
		}
		
		FileModel fileModel = (FileModel) parentTreeContent.getObj();
		model.setFileModel(fileModel);
		fileModel.getPhysicalDataSet().add(model);
		
		// 在文件节点下添加一个物理数据模型
		TreeContent physicalDataContent = treeViewComposite.addNode(parentTreeContent.getId(), 
				parentTreeContent.getId() + DmConstants.SEPARATOR + model.getId(), 
				model, CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, 
						IDmImageKey.PHYSICAL_DATA_IMAGE_16));
		
		// 在物理数据模型下添加一个表格集合节点
		String id = physicalDataContent.getId() + DmConstants.SEPARATOR + DmConstants.TABLE_NODE_ID_TAIL;
		treeViewComposite.addNode(physicalDataContent.getId(), id, 
				new TablesNodeModel(), 
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, IDmImageKey.TABLES_16));
		
		// 添加包模型
		Set<PackageModel> packageModelList = model.getPackageModelSet();
		for(PackageModel packageModel : packageModelList) {
			addPackageModel(packageModel, physicalDataContent);
		}
		
		// 在物理数据模型下添加一个默认列的集合节点
		DefaultColumnsNodeModel defaultColumnsNodeModel = new DefaultColumnsNodeModel();
		defaultColumnsNodeModel.setPhysicalDataModel(model);
		id = physicalDataContent.getId() + DmConstants.SEPARATOR + DmConstants.DEFAULT_NODE_ID_TAIL;
		treeViewComposite.addNode(physicalDataContent.getId(), id, 
				defaultColumnsNodeModel, CacheImage.getCacheImage().getImage(DmConstants
						.APPLICATION_ID, IDmImageKey.A_DEFAULT_COLUMN));
		List<ColumnModel> defaultColumnModelList = model.getDefaultColumnList();
		for(ColumnModel columnModel : defaultColumnModelList) {
			addCommonColumnModel(columnModel, id);
		}
		
		// 在物理数据模型下添加一个Domains节点
		DomainsNodeModel domainsNodeModel = new DomainsNodeModel();
		domainsNodeModel.setPhysicalDataModel(model);
		id = physicalDataContent.getId() + DmConstants.SEPARATOR + DmConstants.DOMAIN_NODE_ID_TAIL;
		treeViewComposite.addNode(physicalDataContent.getId(), id, 
				domainsNodeModel, CacheImage.getCacheImage().getImage(DmConstants
						.APPLICATION_ID, IDmImageKey.DOMAINS_16));
		List<ColumnModel> columnModelList = model.getDomainList();
		for(ColumnModel columnModel : columnModelList) {
			addCommonColumnModel(columnModel, id);
		}
		
		// 在物理数据模型下添加一个业务字典节点
		DictionarysNodeModel dictionarysNodeModel = new DictionarysNodeModel();
		dictionarysNodeModel.setPhysicalDataModel(model);
		id = physicalDataContent.getId() + DmConstants.SEPARATOR + DmConstants.DICTIONARY_NODE_ID_TAIL;
		treeViewComposite.addNode(physicalDataContent.getId(), id, 
				dictionarysNodeModel, CacheImage.getCacheImage().getImage(DmConstants
						.APPLICATION_ID, IDmImageKey.DICTIONARYS_16));
		List<DictionaryModel> dictionaryModelList = model.getDictonaryModelList();
		for(DictionaryModel dictionaryModel : dictionaryModelList) {
			addDictionaryModel(dictionaryModel, id);
		}
		
		return physicalDataContent;
		
	}
	
	/**
	 * 添加一个业务字典模型
	 * @param dictionaryModel
	 * @param id2
	 */
	private void addDictionaryModel(DictionaryModel dictionaryModel, String id2) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 删除一个物理数据模型
	 * @param TreeContent 要删除的物理数据模型
	 * @throws CanNotFoundNodeIDException 
	 * @throws FoundTreeNodeNotUniqueException 
	 */
	public void removePhysicalDataModel(TreeContent physicalDataModelTreeContent) throws FoundTreeNodeNotUniqueException, 
			CanNotFoundNodeIDException {
		if(physicalDataModelTreeContent == null || physicalDataModelTreeContent.getObj() == null || 
				((PhysicalDataModel)physicalDataModelTreeContent.getObj()).getFileModel() == null) {
			logger.error("传入的PhysicalDataModel的树节点为空或信息不完整，无法删除该物理数据模型！");
			return ;
		}
		
		// 在文件节点下删除该物理数据模型
		treeViewComposite.removeNode(physicalDataModelTreeContent.getId());
		
		// 在该FileModel中删除该物理数据模型
		PhysicalDataModel physicalDataModel = (PhysicalDataModel) physicalDataModelTreeContent.getObj();
		physicalDataModel.getFileModel().getPhysicalDataSet().remove(physicalDataModel);
		
	}
	
	/**
	 * 添加一个包模型
	 * @param PackageModel 需要添加的包模型
	 * @param String 父级节点ID
	 * @throws CanNotFoundNodeIDException 
	 * @throws FoundTreeNodeNotUniqueException 
	 */
	public TreeContent addPackageModel(PackageModel model, TreeContent parentTreeContent) 
			throws CanNotFoundNodeIDException, FoundTreeNodeNotUniqueException {
		if(model == null || parentTreeContent == null || !(parentTreeContent.getObj() instanceof PhysicalDataModel)) {
			logger.error("传入的PackageModel或父节点不正确为空，无法添加该包模型！");
			return null;
		}
		
		PhysicalDataModel physicalDataModel = (PhysicalDataModel) parentTreeContent.getObj();
		model.setPhysicalDataModel(physicalDataModel);
		physicalDataModel.getPackageModelSet().add(model);
		
		// 在物理数据模型下添加一个包模型
		TreeContent packageModelTreeContent = treeViewComposite.addNode(parentTreeContent.getId(), 
				parentTreeContent.getId() + DmConstants.SEPARATOR + model.getId(), 
				model, CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, 
						IDmImageKey.PACKAGE_IMAGE_16));
		
		// 在下面添加物理图形模型节点
		Set<PhysicalDiagramModel> physicalDiagramModels = model.getPhysicalDiagramModelSet();
		for(PhysicalDiagramModel physicalDiagramModel : physicalDiagramModels) {
			 addPhysicalDiagramModel(physicalDiagramModel, packageModelTreeContent);
		}
		
		return packageModelTreeContent;
		
	}
	
	/**
	 * 删除一个包模型
	 * @param TreeContent 需要删除的包模型
	 * @param String 父级节点ID
	 * @throws CanNotFoundNodeIDException 
	 * @throws PartInitException 
	 * @throws FoundTreeNodeNotUniqueException 
	 */
	public void removePackageModel(TreeContent packageModelTreeContent) 
			throws CanNotFoundNodeIDException, FoundTreeNodeNotUniqueException, PartInitException {
		
		if(packageModelTreeContent == null || !(packageModelTreeContent.getObj() 
				instanceof PackageModel)) {
			logger.error("传入的PackageModel的树节点为空或信息不完整，无法删除该包模型！");
			return ;
		}

		List<TreeContent> physicalDiagramTreeContentList = packageModelTreeContent.getChildrenList();
		for(TreeContent physicalDiagramModelTreeContent : physicalDiagramTreeContentList) {
			removePhysicalDiagramModel(physicalDiagramModelTreeContent);
		}
		
		// 删除包节点模型
		treeViewComposite.removeNode(packageModelTreeContent.getId());
		
		// 从物理数据模型中删除自己
		PackageModel packageModel = (PackageModel) packageModelTreeContent.getObj();
		packageModel.getPhysicalDataModel().getPackageModelSet().remove(packageModel);
		
	}
	
	/**
	 * 添加一个物理图形模型
	 * @param PhysicalDataModel 需要添加的物理数据模型
	 * @param String 父级节点ID
	 * @throws CanNotFoundNodeIDException 
	 * @throws FoundTreeNodeNotUniqueException 
	 */
	public TreeContent addPhysicalDiagramModel(PhysicalDiagramModel model, TreeContent parentTreeContent) throws CanNotFoundNodeIDException, FoundTreeNodeNotUniqueException {
		if(model == null || parentTreeContent == null || !(parentTreeContent.getObj() instanceof PackageModel)) {
			logger.error("传入的PhysicalDragramModel或父节点不正确，无法添加该物理图形模型！");
			return null;
		}
		
		PackageModel packageModel = (PackageModel) parentTreeContent.getObj();
		model.setPackageModel(packageModel);
		packageModel.getPhysicalDiagramModelSet().add(model);
		
		// 在物理数据模型下添加一个物理图形模型
		TreeContent diagramModelTreeContent = treeViewComposite.addNode(parentTreeContent.getId(), 
				parentTreeContent.getId() + DmConstants.SEPARATOR + model.getId(), 
				model, CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, 
						IDmImageKey.PHYSICAL_DRAGRAM_IMAGE_16));
		
		// 物理图形模型工厂中添加该对象
		PhysicalDiagramModelFactory.addPhysicalDiagramModel(FileModel.getFileModelFromObj(model)
				, model.getId(), model);
		
		// 表格集合节点ID
		String id = diagramModelTreeContent.getId() + DmConstants.SEPARATOR + DmConstants.TABLE_NODE_ID_TAIL;
		treeViewComposite.addNode(diagramModelTreeContent.getId(), id, 
				new TablesNodeModel(), 
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, IDmImageKey.TABLES_16));
		
		// 在表格集合节点中添加该物理图形模型的所有表格和表格快捷方式
		Collection<TableModel> tableModelSet = (Collection<TableModel>) model.getTableMap()
				.values();
		for(TableModel tableModel : tableModelSet) {
			addTableModel(tableModel, diagramModelTreeContent.getId());
		}
		Collection<TableShortcutModel> tableShortcutModelSet = (Collection<TableShortcutModel>) 
				model.getTableShortcutMap().values();
		for(TableShortcutModel tableShortcutModel : tableShortcutModelSet) {
			addTableShortcutModel(tableShortcutModel, diagramModelTreeContent.getId());
		}
		
		return diagramModelTreeContent;

	}
	
	/**
	 * 删除一个物理图形模型
	 * @param TreeContent 需要删除的物理图形模型
	 * @param String 父级节点ID
	 * @throws CanNotFoundNodeIDException 
	 * @throws PartInitException 
	 * @throws FoundTreeNodeNotUniqueException 
	 */
	public void removePhysicalDiagramModel(TreeContent physicalDiagramModelTreeContent) 
			throws CanNotFoundNodeIDException, PartInitException, FoundTreeNodeNotUniqueException {
		
		if(physicalDiagramModelTreeContent == null || !(physicalDiagramModelTreeContent.getObj() 
				instanceof PhysicalDiagramModel)) {
			logger.error("传入的PhysicalDragramModel的树节点为空或信息不完整，无法删除该物理图形模型！");
			return ;
		}

		PhysicalDiagramModel physicalDiagramModel = (PhysicalDiagramModel) physicalDiagramModelTreeContent.getObj();
		physicalDiagramModel.getPackageModel().getPhysicalDiagramModelSet().remove(physicalDiagramModel);
		
		// 在找到该物理数据模型下面的Tables节点下删除相关表格节点
		Collection<TableModel> tableModels = physicalDiagramModel.getTableMap().values();
		// 获得物理数据模型的树节点
		TreeContent physicalDataModelTreeContent = physicalDiagramModelTreeContent
				.getParent().getParent(); 
		// 找到该物理数据模型下面包含该表格对象的树节点
		TreeContent tablesTreeContent = treeViewComposite.findNodeById(physicalDataModelTreeContent
				.getId() + DmConstants.SEPARATOR + DmConstants.TABLE_NODE_ID_TAIL);
		if(tablesTreeContent == null) {
			logger.error("无法找到物理数据模型下面的Tables节点，删除物理图形模型失败！");
		} else {
			for(TableModel tableModel : tableModels) {
				removeTableModel(tableModel);
			}
		}
		
		
		// 在物理数据模型下删除该物理图形模型
		treeViewComposite.removeNode(physicalDiagramModelTreeContent.getId());
		
		// 物理图形模型工厂中移除该对象
		PhysicalDiagramModelFactory.removePhysicalDiagramModel(FileModel.getFileModelFromObj(physicalDiagramModel)
				, physicalDiagramModel.getId());
		
		// 如果此时有打开的该物理图形模型的Editor，则需要关闭。
		IEditorReference[] editorReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
		for(IEditorReference editorReference : editorReferences) {
			if(editorReference.getEditor(false) instanceof DatabaseDiagramEditor) {
				if(editorReference.getEditorInput() instanceof DatabaseDiagramEditorInput) {
					DatabaseDiagramEditorInput databaseDiagramEditorInput = (DatabaseDiagramEditorInput) editorReference.getEditorInput();
					if(physicalDiagramModelTreeContent.equals(databaseDiagramEditorInput.getPhysicalDiagramTreeContent())) {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().
							closeEditor(editorReference.getEditor(false), false);
						break;
					}
					
				}
			}
		}
	}
	
	/**
	 * 给物理图形模型下面的Tables节点和物理数据模型下面的Tables节点上添加该表格模型
	 * @param TableModel 需要添加的TableModel
	 * @param physicalDiagramTablesTreeContentId 物理图形模型树节点的ID
	 * @throws CanNotFoundNodeIDException 
	 * @throws FoundTreeNodeNotUniqueException 
	 */
	public void addTableModel(TableModel tableModel, String physicalDiagramTreeContentId) 
			throws CanNotFoundNodeIDException, FoundTreeNodeNotUniqueException {
		
		if(tableModel != null && physicalDiagramTreeContentId != null) {
			TreeContent phyiscalDiagramTreeContent = treeViewComposite.findNodeById(physicalDiagramTreeContentId);
			if(phyiscalDiagramTreeContent == null) {
				logger.error("找不到该ID对应的物理图形模型树节点，添加表格节点失败:" + physicalDiagramTreeContentId );
			} else {
				addTableModel(tableModel, phyiscalDiagramTreeContent) ;
			}
		} else {
			logger.error("TableModel或父节点ID为空，无法添加表格节点！");
			return ;
		}
	}
	
	/**
	 * 给物理图形模型下面的Tables节点和物理数据模型下面的Tables节点上添加该表格模型
	 * @param TableModel 需要添加的TableModel
	 * @param physicalDiagramTreeContent 物理图形模型树节点
	 * @param tableModel
	 * @param physicalDiagramTreeContent
	 * @throws CanNotFoundNodeIDException
	 * @throws FoundTreeNodeNotUniqueException
	 */
	public void addTableModel(TableModel tableModel, TreeContent physicalDiagramTreeContent) 
			throws CanNotFoundNodeIDException, FoundTreeNodeNotUniqueException {
		
		if(tableModel != null && physicalDiagramTreeContent != null && physicalDiagramTreeContent.getObj() instanceof PhysicalDiagramModel) {
			// 给物理图形模型下面的Tables节点添加该表格
			String tablesId = physicalDiagramTreeContent.getId() + DmConstants.SEPARATOR + DmConstants.TABLE_NODE_ID_TAIL;
			treeViewComposite.addNode(tablesId, 
					tablesId + DmConstants.SEPARATOR + tableModel.getId(), 
					tableModel, 
					CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, IDmImageKey.TABLE_16));
			
			// 给物理图形数据模型下面的Tables节点添加该表格
			String physicalDataTreeContentId = physicalDiagramTreeContent.getParent().getParent().getId();
			tablesId = physicalDataTreeContentId + DmConstants.SEPARATOR + DmConstants.TABLE_NODE_ID_TAIL;
			treeViewComposite.addNode(tablesId, 
					tablesId + DmConstants.SEPARATOR + tableModel.getId(), 
					tableModel, 
					CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, IDmImageKey.TABLE_16));
		} else {
			logger.error("TableModel或父节点ID为空，无法添加表格节点！");
			return ;
		}
	}
	
	/**
	 * 从树上删除该包含该表格对象的树节点
	 * @param String 需要删除的表格名称
	 * @throws CanNotFoundNodeIDException 
	 * @throws FoundTreeNodeNotUniqueException 
	 * @return List<TreeContent> 返回被删除的树节点
	 */
	public List<TreeContent> removeTableModel(TableModel tableModel) throws FoundTreeNodeNotUniqueException, CanNotFoundNodeIDException {
		if(tableModel == null) {
			logger.error("传入的TableModel为空，removeTableModel执行失败，无法删除该表格节点！");
			return null;
		}
		
		List<TreeContent> tableModelList = getTreeContentFromTableModel(tableModel);
		if(tableModelList == null) {
			logger.error("在数据库上无法找到该表格相关的树节点，removeTableModel执行失败，无法删除该表格节点！" 
					+ tableModel.getTableName());
			return null;
		}
		
		for(TreeContent treeContent : tableModelList) {
			treeViewComposite.removeTreeContent(treeContent);
		}
		
		tableModel.getPhysicalDiagramModel().getTableMap().remove(tableModel);
		
		return tableModelList;
	}

	
	/**
	 * 给树上添加一个表格快捷方式模型
	 * @param TableShortcutModel 需要添加的TableShortcutModel
	 * @param String 父级节点ID
	 * @throws CanNotFoundNodeIDException 
	 * @throws FoundTreeNodeNotUniqueException 
	 */
	public TreeContent addTableShortcutModel(TableShortcutModel tableShortcutModel, String physicalDiagramTreeContentId) throws CanNotFoundNodeIDException, FoundTreeNodeNotUniqueException {
		if(tableShortcutModel != null && physicalDiagramTreeContentId != null) {
			String tablesNodeId = physicalDiagramTreeContentId + DmConstants.SEPARATOR + DmConstants.TABLE_NODE_ID_TAIL;
			return treeViewComposite.addNode(tablesNodeId, 
					tablesNodeId + DmConstants.SEPARATOR + tableShortcutModel
					.getId(), 
					tableShortcutModel, CacheImage.getCacheImage().getImage(
							DmConstants.APPLICATION_ID, IDmImageKey.TABLE_SHORTCUT_16));
		} else {
			logger.error("TableShortcutModel或父节点ID为空，无法添加表格节点！");
			return null;
		}
	}
	
	/**
	 * 从树上删除该表格快捷方式节点
	 * @param tableShortcutModel
	 * @throws CanNotFoundNodeIDException 
	 * @throws FoundTreeNodeNotUniqueException 
	 */
	public TreeContent removeTableShortcutModel(TableShortcutModel tableShortcutModel) throws FoundTreeNodeNotUniqueException, CanNotFoundNodeIDException {
		if(tableShortcutModel != null) {
			// 在物理图形模型下删除该快捷方式
			tableShortcutModel.getPhysicalDiagramModel().getTableShortcutMap().remove(tableShortcutModel);
			
			// 从树上删除该节点
			Set<TreeContent> delTreeContent = treeViewComposite.removeObject(tableShortcutModel);
			if(!delTreeContent.isEmpty()) {
				logger.info("删除一个表格快捷方式成功！");
				refreshTree();
				return (TreeContent) delTreeContent.toArray()[0];
			} else {
				logger.info("删除一个表格快捷方式失败！");
				return null;
			}
		} else {
			logger.error("传入的表格树节点数据不完整，无法删除该表格节点！");
			return null;
		}
	}
	
	/**
	 * 刷新默认字段集合节点下面的节点数据
	 * @throws CanNotFoundNodeIDException 
	 * @throws FoundTreeNodeNotUniqueException 
	 */
	public void refreshDefaultColumns(TreeContent defaultColumnsNodeTreeContent) throws FoundTreeNodeNotUniqueException, CanNotFoundNodeIDException {
		if(defaultColumnsNodeTreeContent == null || !(defaultColumnsNodeTreeContent
				.getObj() instanceof DefaultColumnsNodeModel)) {
			logger.debug("传入的列模型的List为空，刷新默认集合节点失败！");
			return ;
		}
		
		// 先获得需要添加的默认列的List
		PhysicalDataModel physicalDataModel = ((DefaultColumnsNodeModel)defaultColumnsNodeTreeContent
				.getObj()).getPhysicalDataModel();
		List<ColumnModel> defaultColumnModelList = physicalDataModel.getDefaultColumnList();
		
		// 删除该集合节点下的所有子节点
		defaultColumnsNodeTreeContent.getChildrenList().clear();
		
		for(ColumnModel columnModel : defaultColumnModelList) {
			// 注意，这里构建的树节点ID不能使用columnModel.getiId()，因为默认列模型的ID都是空的
			treeViewComposite.addNode(defaultColumnsNodeTreeContent.getId()
					, defaultColumnsNodeTreeContent.getId() + DmConstants.SEPARATOR + defaultColumnModelList.indexOf(columnModel)
					, columnModel, CacheImage.getCacheImage().getImage(
							DmConstants.APPLICATION_ID, IDmImageKey.COLUMN_ITEM_16));
		}
		
	}

	
	/**
	 * 从Domains节点下添加一个公共列节点
	 * @param tableModelTreeContent
	 * @throws CanNotFoundNodeIDException 
	 * @throws FoundTreeNodeNotUniqueException 
	 */
	public TreeContent addCommonColumnModel(ColumnModel columnModel, String parentNodeId) 
			throws FoundTreeNodeNotUniqueException, CanNotFoundNodeIDException {
		if(columnModel != null && parentNodeId != null) {
			return treeViewComposite.addNode(parentNodeId, 
					parentNodeId + DmConstants.SEPARATOR + columnModel.getColumnName(), 
					columnModel, 
					CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID
							, IDmImageKey.COLUMN_ITEM_16));
		} else {
			logger.error("ColumnModel或父节点ID为空，无法添加公共列节点！");
			return null;
		}
	}
	
	/**
	 * 从Domains节点下移除一个公共列节点
	 * @param tableModelTreeContent
	 * @throws CanNotFoundNodeIDException 
	 * @throws FoundTreeNodeNotUniqueException 
	 */
	public TreeContent removeCommonColumnModel(TreeContent commonColumnModelTreeContent) 
			throws FoundTreeNodeNotUniqueException, CanNotFoundNodeIDException {
		if(commonColumnModelTreeContent != null && commonColumnModelTreeContent.getObj() instanceof ColumnModel) {
			return treeViewComposite.removeNode(commonColumnModelTreeContent.getId());
		} else {
			logger.error("传入的公共ColumnModel树节点为空或不正确，无法删除公共列节点！");
			return null;
		}
	}
	
	/**
	 * 鼠标双击事件
	 * 如果是物理图形模型，则打开相应的Editor
	 */
	public void mouseDoubleEvent() {
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		if(selection.size() == 1) {
			Object obj = selection.getFirstElement();
			if(obj instanceof TreeContent) {
				TreeContent treeContent = (TreeContent) obj;
				Object selObj = treeContent.getObj();
				if(selObj instanceof PhysicalDiagramModel) {
					DatabaseAction databaseAction = new DatabaseAction(DmConstants.OPEN_PHYSICAL_DIAGRAM_FLAG, treeViewer);
					databaseAction.run();
				} else if(selObj instanceof TableModel || selObj instanceof TableShortcutModel) {
					DatabaseAction databaseAction = new DatabaseAction(DmConstants.ATTRI_TABLE_MODEL, treeViewer);
					databaseAction.run();
				} else if(selObj instanceof ColumnModel) {
					DatabaseAction databaseAction = new DatabaseAction(DmConstants.MODIFY_COLUMN_MODEL, treeViewer);
					databaseAction.run();
				} else if(selObj instanceof DefaultColumnsNodeModel) {
					DatabaseAction databaseAction = new DatabaseAction(DmConstants.DEFAULT_COLUMN_FLAG, treeViewer);
					databaseAction.run();
				}
			}
		}
	}
	
	/**
	 * 刷新树
	 */
	public void refreshTree() {
		treeViewer.refresh();
	}
	
	@Override
	public void setFocus() {
		if(tree != null) {
			tree.setFocus();
		}
	}

	/**
	 * 通过一个物理图形模型，找到对应的已经打开的Editor的CommandStack
	 * @param model
	 * @return
	 */
	public static CommandStack getEditorCommandStack(PhysicalDiagramModel model) {
		if(model == null) {
			logger.warn("传入的PhysicalDiagramModel为空，无法找到对应的CommandStack！");
			return null;
		}
		
		CommandStack commandStack = editorCommandStackMap.get(model);
		if(commandStack == null) {
//			logger.debug("找不到该物理数据模型对应的Editor的CommandStack：" + model.getId());
		}
		
		return commandStack;
	}
	
	/**
	 * 通过一个物理图形模型，删除对应的CommandStack
	 * @param model
	 * @return
	 */
	public static void removeEditorCommandStack(PhysicalDiagramModel model) {
		if(model == null) {
			logger.warn("传入的PhysicalDiagramModel为空，无法删除对应的CommandStack！");
			return ;
		}
		
		editorCommandStackMap.remove(model);
	}
	
	/**
	 * 添加一个物理图形模型，及其对应的CommandStack
	 * @param model
	 * @return
	 */
	public static boolean addEditorCommandStack(PhysicalDiagramModel model, CommandStack commandStack) {
		if(model == null || commandStack == null) {
			logger.warn("传入的PhysicalDiagramModel或CommandStack为空，无法添加！");
			return false;
		}
		
		CommandStack stack = editorCommandStackMap.get(model);
		if(stack != null) {
			logger.warn("该物理图形模型已经存在，添加失败！ " + model.getId());
			return false;
		}
		
		editorCommandStackMap.put(model, commandStack);
		return true;
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}
	

	
	/**
	 * 根据文件模型来获取对应的树节点
	 * @return
	 */
	public TreeContent getTreeContentFromFileModel(FileModel fileModel) {
		if(fileModel == null) {
			logger.error("传入的文件模型为空，无法获取对应的树节点！");
			return null;
		}
		
		TreeContent rootTreeContent = treeViewComposite.getRootContentList().get(0);
		List<TreeContent> fileTreeContentList = rootTreeContent.getChildrenList();
		for(TreeContent treeContent : fileTreeContentList) {
			if(treeContent.getObj().equals(fileModel)) {
				return treeContent;
			}
		}
		
		logger.warn("没有找到该文件对应的树节点:" + fileModel.getFileName());
		return null;
	}
	
	/**
	 * 根据表格模型或者表格快捷方式模型来获取对应物理图形模型的树节点
	 * @return
	 */
	public TreeContent getPhysicalDiagramTreeContentFromTableModelOrTableShortcutModel(Object findModel) {
		if(findModel == null || (!(findModel instanceof TableModel) && !(findModel instanceof TableShortcutModel))) {
			logger.error("传入的表格模型或在表格快捷方式模型为空，无法获取对应的树节点！");
			return null;
		}
		
		PhysicalDiagramModel physicalDiagramModel;
		if(findModel instanceof TableModel) {
			physicalDiagramModel = ((TableModel)findModel).getPhysicalDiagramModel();
		} else {
			physicalDiagramModel = ((TableShortcutModel)findModel).getPhysicalDiagramModel();
		}
		
		if(physicalDiagramModel == null) {
			logger.error("传入的表格模向上遍历失败，无法获取对应的物理图形模型" +
					"，getPhysicalDiagramTreeContentFromTableModel执行失败！");
			return null;
		}
		
		PackageModel packageModel = physicalDiagramModel.getPackageModel();
		if(packageModel == null) {
			logger.error("传入的表格模向上遍历失败，无法获取对应的包模型" +
					"，getPhysicalDiagramTreeContentFromTableModel执行失败！");
			return null;
		}
		
		PhysicalDataModel physicalDataModel = packageModel.getPhysicalDataModel();
		if(physicalDataModel == null) {
			logger.error("传入的表格模向上遍历失败，无法获取对应的物理数据模型" +
					"，getPhysicalDiagramTreeContentFromTableModel执行失败！");
			return null;
		}
		
		FileModel fileModel = FileModel.getFileModelFromObj(findModel);
		TreeContent fileTreeContent = getTreeContentFromFileModel(fileModel);
		if(fileTreeContent != null) {
			List<TreeContent> phyiscalDataTreeContentList = fileTreeContent.getChildrenList();
			for(TreeContent phyiscalDataTreeContent : phyiscalDataTreeContentList) {
				if(physicalDataModel.equals(phyiscalDataTreeContent.getObj())) {
					List<TreeContent> packageTreeContentList = phyiscalDataTreeContent.getChildrenList();
					for(TreeContent packageTreeContent : packageTreeContentList) {
						if(packageModel.equals(packageTreeContent.getObj())) {
							List<TreeContent> diagramTreeContentList = packageTreeContent.getChildrenList();
							for(TreeContent diagramTreeContent : diagramTreeContentList) {
								if(physicalDiagramModel.equals(diagramTreeContent.getObj())) {
									return diagramTreeContent;
								}
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 根据表格模型来获取对应的树节点
	 * @return
	 */
	public List<TreeContent> getTreeContentFromTableModel(TableModel tableModel) {
		if(tableModel == null) {
			logger.error("传入的表格模型为空，无法获取对应的树节点！");
			return null;
		}
		
		// 用于储存找到的表格的树节点，正常情况下是两个，一个在物理数据节点的Tables节点下，一个是在对应的物理图形模型下面的Tables节点下
		List<TreeContent> tableModels = new ArrayList<TreeContent>();
		
		FileModel fileModel = FileModel.getFileModelFromObj(tableModel);
		
		PhysicalDiagramModel physicalDiagramModel = tableModel.getPhysicalDiagramModel();
		PackageModel packageModel = physicalDiagramModel.getPackageModel();
		PhysicalDataModel physicalDataModel = packageModel.getPhysicalDataModel();
		
		
		TreeContent fileTreeContent = getTreeContentFromFileModel(fileModel);
		if(fileTreeContent != null) {
			List<TreeContent> physicalDataTreeContentList = fileTreeContent.getChildrenList();
			for(TreeContent physicalDataTreeContent : physicalDataTreeContentList) {
				if(physicalDataTreeContent.getObj().equals(physicalDataModel)) {
					List<TreeContent> packageTreeContentList = physicalDataTreeContent.getChildrenList();
					
					// 将物理数据模型下面的Tables节点中的该表格的树节点返回
					for(TreeContent treeContent : packageTreeContentList) {
						// 找到该物理数据模型下面的Tables节点
						if(treeContent.getObj() instanceof TablesNodeModel) {
							List<TreeContent> tablesTreeContentList = treeContent.getChildrenList();
							for(TreeContent tableModelTreeContent : tablesTreeContentList) {
								if(tableModel.equals(tableModelTreeContent.getObj())) {
									tableModels.add(tableModelTreeContent);
									break ;
								}
							}
							
							break ;
						}
					}
					
					
					// 将物理数据模型下面的Tables节点中的该表格的树节点返回
					for(TreeContent treeContent : packageTreeContentList) {
						// 先遍历该物理数据模型下面的包节点
						if(treeContent.getObj().equals(packageModel)) {
							List<TreeContent> physicalDiagramTreeContentList = treeContent.getChildrenList();
							// 遍历该包模型下面的物理图形模型节点
							for(TreeContent physicalDiagramTreeContent : physicalDiagramTreeContentList) {
								if(physicalDiagramTreeContent.getObj().equals(physicalDiagramModel)) {
									List<TreeContent> treeContentList = physicalDiagramTreeContent.getChildrenList();
									// 找到该物理图形模型下面的Tables节点
									for(TreeContent tablesTreeContent : treeContentList){
										if(tablesTreeContent.getObj() instanceof TablesNodeModel) {
											List<TreeContent> tableTreeContentList = tablesTreeContent.getChildrenList();
											for(TreeContent tableTreeContent : tableTreeContentList) {
												if(tableModel.equals(tableTreeContent.getObj())) {
													tableModels.add(tableTreeContent);
												}
											}
											break ;
										}
									}
									
									break ;
								}
							}
							
							break ;
						}
					}
					
					break ;
				} 
			}
		}
		
		return tableModels;
	}

	public TreeViewComposite getTreeViewComposite() {
		return treeViewComposite;
	}
}
