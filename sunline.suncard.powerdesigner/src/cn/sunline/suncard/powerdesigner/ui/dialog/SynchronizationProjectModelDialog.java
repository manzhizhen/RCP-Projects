/* 文件名：     SynchronizationProjectModelDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-5
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.powerdesigner.command.SynchronizationProjectModelCommand;
import cn.sunline.suncard.powerdesigner.command.UpdateCommonColumnModelCommand;
import cn.sunline.suncard.powerdesigner.manager.CompareObjectManager;
import cn.sunline.suncard.powerdesigner.manager.ProductSpaceManager;
import cn.sunline.suncard.powerdesigner.manager.ProjectSpaceManager;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.CompareObjectModel;
import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.ProductSpaceModel;
import cn.sunline.suncard.powerdesigner.model.ProjectModel;
import cn.sunline.suncard.powerdesigner.model.ProjectSpaceModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.db.DatabaseTypeModel;
import cn.sunline.suncard.powerdesigner.provider.SynProjectModelLabelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.tree.ProjectDefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProjectTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.CheckboxComposite;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;

/**
 * 将项目模型和产品模型同步的对话框
 * 
 * @author Manzhizhen
 * @version 1.0, 2013-1-5
 * @see
 * @since 1.0
 */
public class SynchronizationProjectModelDialog extends TitleAreaDialog {
	private ProjectModel projectModel;

	private Composite composite;
	private CheckboxComposite leftModuleComposite;
	private CheckboxComposite rightTableComposite;
	
	private ProjectTreeViewPart projectTreeViewPart;

	Log logger = LogManager.getLogger(SynchronizationProjectModelDialog.class);

	public SynchronizationProjectModelDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}

	@Override
	protected void configureShell(Shell newShell) {
		// 模块模型对话框
		newShell.setText(I18nUtil.getMessage("MODULEMODEL_DIALOG"));
		setDefaultImage(CacheImage.getCacheImage().getImage(
				DmConstants.PD_APPLICATION_ID, IDmImageKey.MODULE_LABEL_16));
		super.configureShell(newShell);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(1000, 700);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// 同步项目
		setTitle("同步项目模块");
		setMessage("同步项目模块");
		setTitleImage(CacheImage.getCacheImage().getImage(
				DmConstants.PD_APPLICATION_ID, IDmImageKey.MODULE_LABEL_64));

		Control control = super.createDialogArea(parent);

		composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		composite.setLayout(new FormLayout());

		createControl();
		initControlValue();
		createEvent();

		return control;
	}

	private void createControl() {
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 0);
		formData.right = new FormAttachment(100, 0);
		formData.top = new FormAttachment(0, 0);
		formData.bottom = new FormAttachment(100, 0);
		
		SashForm sashForm = new SashForm(composite, SWT.HORIZONTAL | SWT.BORDER);
		sashForm.setLayoutData(formData);
		sashForm.setLayout(new FormLayout());
		
		leftModuleComposite = new CheckboxComposite(sashForm, SWT.BORDER);
	
		rightTableComposite = new CheckboxComposite(sashForm, SWT.BORDER);
		
		sashForm.setWeights(new int[] {1, 1});

	}

	/**
	 * 通过表格模型和列名称返回对应的列对象
	 */
	private ColumnModel getColumnModel(TableModel tableModel, String columnName) {
		for (ColumnModel columnModel : tableModel.getColumnList()) {
			if (columnModel.getColumnName().equals(columnName)) {
				return columnModel;
			}
		}

		return null;
	}

	private void initControlValue() {
		Map<String, ModuleModel> projectModuleModelMap = new HashMap<String, ModuleModel>();
		Map<ModuleModel, Set<String>> projectModuleTableNameMap = new HashMap<ModuleModel, Set<String>>(); // 储存了模块模型和模块下面的表格名称
		Map<String, TableModel> projectTableModelMap = new HashMap<String, TableModel>();
		Map<String, Set<String>> projectTableColumnModelMap = new HashMap<String, Set<String>>();

		Map<String, ModuleModel> productModuleModelMap = new HashMap<String, ModuleModel>();
		Map<ModuleModel, Set<String>> productModuleTableNameMap = new HashMap<ModuleModel, Set<String>>();
		Map<String, TableModel> productTableModelMap = new HashMap<String, TableModel>();
		Map<String, Set<String>> productTableColumnModelMap = new HashMap<String, Set<String>>();

		Set<String> totalModuleIdSet = new HashSet<String>();
		Map<String, Set<String>> totalModuleTableNameMap = new HashMap<String, Set<String>>();
		Map<String, Set<String>> totalTableColumnNameMap = new HashMap<String, Set<String>>();

		DatabaseTypeModel databaseTypeModel = projectModel
				.getDatabaseTypeModel();
		// 先构造产品树上的模块数据结构
		Set<ModuleModel> moduleModelSet = ProductSpaceManager
				.getAllModuleModels();
		for (ModuleModel moduleModel : moduleModelSet) {
			// 只有数据库类型相同的模块才能入选
			if (!moduleModel.getProductModel().getPhysicalDataModel()
					.getDatabaseTypeModel().equals(databaseTypeModel)) {
				continue;
			}

			if (productModuleModelMap.get(moduleModel.getId()) == null) {
				productModuleModelMap.put(moduleModel.getId(), moduleModel);
				productModuleTableNameMap.put(moduleModel,
						new HashSet<String>());
			}

			for (TableModel tableModel : moduleModel.getTableModelSet()) {
				productTableModelMap.put(tableModel.getTableName(), tableModel);
				if (productTableColumnModelMap.get(tableModel.getTableName()) == null) {
					productTableColumnModelMap.put(tableModel.getTableName(),
							new HashSet<String>());
					for (ColumnModel columnModel : tableModel.getColumnList()) {
						productTableColumnModelMap.get(
								tableModel.getTableName()).add(
								columnModel.getColumnName());
					}

				}

				productModuleTableNameMap.get(moduleModel).add(
						tableModel.getTableName());
			}
		}

		// 获取项目树上的模块
		Set<ModuleModel> projectModuleModelSet = projectModel
				.getModuleModelSet();
		for (ModuleModel moduleModel : projectModuleModelSet) {
			projectModuleModelMap.put(moduleModel.getId(), moduleModel);
			projectModuleTableNameMap.put(moduleModel, new HashSet<String>());

			Set<String> tableNameSet = new HashSet<String>();
			for (TableModel tableModel : moduleModel.getTableModelSet()) {
				tableNameSet.add(tableModel.getTableName());
				projectTableModelMap.put(tableModel.getTableName(), tableModel);

				if (projectTableColumnModelMap.get(tableModel.getTableName()) == null) {
					projectTableColumnModelMap.put(tableModel.getTableName(),
							new HashSet<String>());
					for (ColumnModel columnModel : tableModel.getColumnList()) {
						projectTableColumnModelMap.get(
								tableModel.getTableName()).add(
								columnModel.getColumnName());
					}
				}

				projectModuleTableNameMap.get(moduleModel).add(
						tableModel.getTableName());
			}
		}

		// 总的数据结构，包含产品树和项目树
		totalModuleIdSet.addAll(productModuleModelMap.keySet());
		totalModuleIdSet.addAll(projectModuleModelMap.keySet());
		for (String moduleId : totalModuleIdSet) {
			Set<String> tableNameSet = new HashSet<String>();
			if (projectModuleModelMap.get(moduleId) != null) {
				tableNameSet.addAll(projectModuleTableNameMap
						.get(projectModuleModelMap.get(moduleId)));
			}
			if (productModuleModelMap.get(moduleId) != null) {
				tableNameSet.addAll(productModuleTableNameMap
						.get(productModuleModelMap.get(moduleId)));
			}
			totalModuleTableNameMap.put(moduleId, tableNameSet);

			for (String tableName : tableNameSet) {
				if (totalTableColumnNameMap.get(tableName) == null) {
					totalTableColumnNameMap.put(tableName,
							new HashSet<String>());
				}
				if (projectTableColumnModelMap.get(tableName) != null) {
					totalTableColumnNameMap.get(tableName).addAll(
							projectTableColumnModelMap.get(tableName));
				}
				if (productTableColumnModelMap.get(tableName) != null) {
					totalTableColumnNameMap.get(tableName).addAll(
							productTableColumnModelMap.get(tableName));
				}
			}
		}

		// 创建左边树内容——只包含模板节点和表格节点
		List<TreeContent> moduleTreeContentList = new ArrayList<TreeContent>();
		for (String moduleId : totalModuleIdSet) {
			TreeContent moduleTreeContent = new TreeContent();
			CompareObjectModel compareModuleModel = new CompareObjectModel();

			ModuleModel projectModuleModel = projectModuleModelMap
					.get(moduleId);
			ModuleModel productModuleModel = productModuleModelMap
					.get(moduleId);

			if (projectModuleModel == null && productModuleModel != null) {
				compareModuleModel
						.setCompareFlag(CompareObjectManager.COMPARE_ADD);
				compareModuleModel.setLeftObject(productModuleModel);

			} else if (projectModuleModel != null && productModuleModel == null) {
				compareModuleModel
						.setCompareFlag(CompareObjectManager.COMPARE_REMOVE);
				compareModuleModel.setLeftObject(projectModuleModel);

			} else if (projectModuleModel != null && productModuleModel != null) {
				compareModuleModel
						.setCompareFlag(CompareObjectManager.COMPARE_SAME);
				compareModuleModel.setLeftObject(projectModuleModel);
				compareModuleModel.setRightObject(productModuleModel);
			}

			moduleTreeContent.setId(moduleId);
			moduleTreeContent.setObj(compareModuleModel);
			moduleTreeContentList.add(moduleTreeContent);

			for (String tableName : totalModuleTableNameMap.get(moduleId)) {
				TreeContent tableTreeContent = new TreeContent();
				CompareObjectModel compareTableModel = new CompareObjectModel();

				TableModel projectTableModel = null;
				TableModel productTableModel = null;

				// 检查项目树下该模块下是否有这个表格
				if (projectModuleModel != null) {
					Set<String> tableNameSet = projectModuleTableNameMap
							.get(projectModuleModel);
					if (tableNameSet.contains(tableName)) {
						projectTableModel = projectTableModelMap.get(tableName);
					}
				}
				// 检查产品树下该模块下是否有这个表格
				if (productModuleModel != null) {
					Set<String> tableNameSet = productModuleTableNameMap
							.get(productModuleModel);
					if (tableNameSet.contains(tableName)) {
						productTableModel = productTableModelMap.get(tableName);
					}
				}

				if (projectTableModel == null && productTableModel != null) {
					compareTableModel
							.setCompareFlag(CompareObjectManager.COMPARE_ADD);
					compareTableModel.setLeftObject(productTableModel);
					// 如果项目树上没有改表格，需要把项目对应的模块放进去。(不过项目树上可能没有该模块)
					compareTableModel.setRightObject(projectModuleModelMap
							.get(moduleId));

				} else if (projectTableModel != null
						&& productTableModel == null) {
					compareTableModel
							.setCompareFlag(CompareObjectManager.COMPARE_REMOVE);
					compareTableModel.setLeftObject(projectTableModel);
					compareTableModel.setRightObject(projectModuleModel);

				} else if (projectTableModel != null
						&& productTableModel != null) {
					String compareStr = CompareObjectManager.compareTableModel(
							projectTableModel, productTableModel);
					compareTableModel.setCompareFlag(compareStr);
					compareTableModel.setLeftObject(projectTableModel);
					compareTableModel.setRightObject(productTableModel);
				}

				tableTreeContent.setId(moduleId + tableName);
				tableTreeContent.setObj(compareTableModel);
				moduleTreeContent.getChildrenList().add(tableTreeContent);
				tableTreeContent.setParent(moduleTreeContent);
			}

		}
		// 左边树构造完成
		leftModuleComposite.initControlData(moduleTreeContentList,
				new SynProjectModelLabelProvider());

		// 创建右边的表格树
		List<TreeContent> tableTreeContentList = new ArrayList<TreeContent>();
		for (String tableName : totalTableColumnNameMap.keySet()) {

			TableModel projectTableModel = projectTableModelMap.get(tableName);
			TableModel productTableModel = productTableModelMap.get(tableName);

			if (projectTableModel != null && productTableModel != null) {
				TreeContent tableTreeContent = new TreeContent();
				CompareObjectModel compareTableModel = new CompareObjectModel();

				compareTableModel
						.setCompareFlag(CompareObjectManager.COMPARE_MODIFY);
				compareTableModel.setLeftObject(projectTableModel);
				compareTableModel.setRightObject(productTableModel);

				tableTreeContent.setId(tableName);
				tableTreeContent.setObj(compareTableModel);
				tableTreeContentList.add(tableTreeContent);

				for (String columnName : totalTableColumnNameMap.get(tableName)) {
					TreeContent columnTreeContent = new TreeContent();
					CompareObjectModel compareColumnModel = new CompareObjectModel();

					ColumnModel projectColumnModel = getColumnModel(
							projectTableModel, columnName);
					ColumnModel productColumnModel = getColumnModel(
							productTableModel, columnName);

					if (projectColumnModel == null
							&& productColumnModel != null) {
						compareColumnModel
								.setCompareFlag(CompareObjectManager.COMPARE_ADD);
						compareColumnModel.setLeftObject(productColumnModel);
						// 如果是项目树上没有的列对象，需要把项目树上对应的TableModel传过去（不过也有可能项目树上没有改表格）
						compareColumnModel.setRightObject(projectTableModel);

					} else if (projectColumnModel != null
							&& productColumnModel == null) {
						compareColumnModel
								.setCompareFlag(CompareObjectManager.COMPARE_REMOVE);
						compareColumnModel.setLeftObject(projectColumnModel);

					} else if (projectColumnModel != null
							&& productColumnModel != null) {
						compareColumnModel.setCompareFlag(CompareObjectManager
								.compareColumnModel(projectColumnModel,
										productColumnModel));
						compareColumnModel.setLeftObject(projectColumnModel);
						compareColumnModel.setRightObject(productColumnModel);
					}

					columnTreeContent.setId(tableName + columnName);
					columnTreeContent.setObj(compareColumnModel);
					tableTreeContent.getChildrenList().add(columnTreeContent);
					columnTreeContent.setParent(tableTreeContent);

				}
			}
		}

		// 左边树构造完成
		rightTableComposite.initControlData(tableTreeContentList,
				new SynProjectModelLabelProvider());

	}

	@Override
	protected void okPressed() {
		 Set<TreeContent> moduleCheckedTreeContentList = leftModuleComposite.getCheckedTreeContent(CompareObjectModel.class);
		 Set<TreeContent> tableCheckedTreeContentList = rightTableComposite.getCheckedTreeContent(CompareObjectModel.class);
		 SynchronizationProjectModelCommand command = new SynchronizationProjectModelCommand();
		 command.setProjectModel(projectModel);
		 command.setProjectTreeViewPart(projectTreeViewPart);
		 command.setModuleCheckedTreeContentSet(moduleCheckedTreeContentList);
		 command.setTableCheckedTreeContentSet(tableCheckedTreeContentList);
		 
		 CommandStack commandStack = ProjectDefaultViewPart.getCommandStackFromProjectModel(projectModel);
		 if(commandStack != null) {
			 commandStack.execute(command);
		 } else {
			 logger.error("SynchronizationProjectModelCommand执行失败，找不到对应的CommandStack!");
		 }
		 
		super.okPressed();
	}

	private void createEvent() {
		leftModuleComposite.createEvent();
		rightTableComposite.createEvent();
	}

	public void setProjectModel(ProjectModel projectModel) {
		this.projectModel = projectModel;
	}

	public void setProjectTreeViewPart(ProjectTreeViewPart projectTreeViewPart) {
		this.projectTreeViewPart = projectTreeViewPart;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("CANCEL"), false);
	}
	
	
}
