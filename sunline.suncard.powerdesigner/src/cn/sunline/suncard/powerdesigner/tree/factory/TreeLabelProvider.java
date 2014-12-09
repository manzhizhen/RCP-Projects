/* 文件名：     TreeLabelProvider.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	树模板的标签提供者
 * 修改人：     易振强
 * 修改时间：2011-10-28
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.tree.factory;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.CompareObjectModel;
import cn.sunline.suncard.powerdesigner.model.DocumentCategoryModel;
import cn.sunline.suncard.powerdesigner.model.DocumentModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.PackageModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.ProductSpaceModel;
import cn.sunline.suncard.powerdesigner.model.ProjectGroupModel;
import cn.sunline.suncard.powerdesigner.model.ProjectModel;
import cn.sunline.suncard.powerdesigner.model.ProjectSpaceModel;
import cn.sunline.suncard.powerdesigner.model.SqlScriptModel;
import cn.sunline.suncard.powerdesigner.model.StoredProcedureModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.TableShortcutModel;
import cn.sunline.suncard.powerdesigner.model.WorkSpaceModel;
import cn.sunline.suncard.powerdesigner.models.CodesNodeModel;
import cn.sunline.suncard.powerdesigner.models.DefaultColumnsNodeModel;
import cn.sunline.suncard.powerdesigner.models.DictionarysNodeModel;
import cn.sunline.suncard.powerdesigner.models.DocumentsNodeModel;
import cn.sunline.suncard.powerdesigner.models.DomainsNodeModel;
import cn.sunline.suncard.powerdesigner.models.ModulesNodeModel;
import cn.sunline.suncard.powerdesigner.models.SqlsNodeModel;
import cn.sunline.suncard.powerdesigner.models.StoredProceduresNodeModel;
import cn.sunline.suncard.powerdesigner.models.TablesNodeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.sql.model.ConnectionModel;
import cn.sunline.suncard.powerdesigner.sql.model.ConnectionSpaceModel;
import cn.sunline.suncard.powerdesigner.sql.model.SqlColumnModel;
import cn.sunline.suncard.powerdesigner.sql.model.SqlDatabaseModel;
import cn.sunline.suncard.powerdesigner.sql.model.SqlTableModel;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProjectDefaultViewPart;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 树模板的标签提供者
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 */

public class TreeLabelProvider extends LabelProvider {
	@Override
	public Image getImage(Object element) {
		if(element instanceof TreeContent) {
			return ((TreeContent)element).getImage();
		}
		
		return null;
	}

	@Override
	public String getText(Object element) {
		if(element instanceof TreeContent) {
			TreeContent treeContent = (TreeContent) element;
			Object obj = treeContent.getObj();
			
			if(obj instanceof FileModel) {
				String isDirty = "";
				if(DatabaseTreeViewPart.fileModelIsDirty((FileModel)obj)) {
					isDirty = " *";
				}
				treeContent.setNodeName(((FileModel)obj).getFileName() + isDirty);
				
			} else if(obj instanceof PhysicalDataModel) {
				treeContent.setNodeName(((PhysicalDataModel)obj).getName());
				
			} else if(obj instanceof PhysicalDiagramModel) {
				treeContent.setNodeName(((PhysicalDiagramModel)obj).getName());
				
			} else if(obj instanceof DomainsNodeModel) {
				treeContent.setNodeName(DmConstants.DOMAINS_NODE_NAME);
				
			} else if(obj instanceof TableModel) {
				treeContent.setNodeName(((TableModel)obj).getTableDesc());
				
			} else if(obj instanceof WorkSpaceModel) {
				treeContent.setNodeName(I18nUtil.getMessage("WORKSPACE"));
				
			} else if(obj instanceof ProductSpaceModel) {
				treeContent.setNodeName(I18nUtil.getMessage("PRODUCTSPACE"));
				
			} else if(obj instanceof ProductModel) {
				treeContent.setNodeName(((ProductModel)obj).getName());
				
			} else if(obj instanceof ColumnModel) {
				treeContent.setNodeName(((ColumnModel)obj).getColumnDesc());
				
			} else if(obj instanceof PackageModel) {
				treeContent.setNodeName(((PackageModel)obj).getName());
				
			} else if(obj instanceof ModuleModel) {
				treeContent.setNodeName(((ModuleModel)obj).getName());
				
			} else if(obj instanceof TableShortcutModel) {
				treeContent.setNodeName(((TableShortcutModel)obj).getTargetTableModel()
						.getTableDesc());
				
			} else if(obj instanceof TablesNodeModel) {
				treeContent.setNodeName(DmConstants.TABLES_NODE_NAME);
				
			} else if(obj instanceof ModulesNodeModel) {
				treeContent.setNodeName(DmConstants.MODULES_NODE_NAME);
				
			} else if(obj instanceof SqlsNodeModel) {
				treeContent.setNodeName(DmConstants.SQLS_NODE_NAME);
				
			} else if(obj instanceof StoredProceduresNodeModel) {
				treeContent.setNodeName(DmConstants.STOREDPROCEDURES_NODE_NAME);
				
			} else if(obj instanceof DocumentsNodeModel) {
				treeContent.setNodeName(DmConstants.DOC_NODE_NAME);
				
			} else if(obj instanceof CodesNodeModel) {
				treeContent.setNodeName(DmConstants.CODES_NODE_NAME);
				
			} else if(obj instanceof DefaultColumnsNodeModel) {
				treeContent.setNodeName(DmConstants.DEFAULTS_NODE_NAME);
				
			} else if(obj instanceof SqlScriptModel) {
				treeContent.setNodeName(((SqlScriptModel)obj).getName());
				
			} else if(obj instanceof StoredProcedureModel) {
				treeContent.setNodeName(((StoredProcedureModel)obj).getName());
				
			} else if(obj instanceof ProjectSpaceModel) {
				treeContent.setNodeName(I18nUtil.getMessage("PROJECTSPACE"));
				
			} else if(obj instanceof ProjectGroupModel) {
				treeContent.setNodeName(((ProjectGroupModel)obj).getName());
				
			} else if(obj instanceof ProjectModel) {
				String isDirty = "";
				if(ProjectDefaultViewPart.projectModelIsDirty((ProjectModel)obj)) {
					isDirty = " *";
				}
				treeContent.setNodeName(((ProjectModel)obj).getName() + isDirty);
				
			} else if(obj instanceof ConnectionModel) {
				treeContent.setNodeName(((ConnectionModel)obj).getDriverName());
				
			} else if(obj instanceof ConnectionSpaceModel) {
				treeContent.setNodeName(I18nUtil.getMessage("DB_CONNECTION_TREE"));
				
			} else if(obj instanceof SqlDatabaseModel) {
				treeContent.setNodeName(((SqlDatabaseModel)obj).getName());
				
			} else if(obj instanceof SqlTableModel) {
				treeContent.setNodeName(((SqlTableModel)obj).getName());
				
			} else if(obj instanceof SqlColumnModel) {
				treeContent.setNodeName(((SqlColumnModel)obj).getName());
				
			} else if(obj instanceof DocumentCategoryModel) {
				treeContent.setNodeName(((DocumentCategoryModel)obj).getName());
				
			} else if(obj instanceof DocumentModel) {
				treeContent.setNodeName(((DocumentModel)obj).getFileName());
				
			} else if(obj instanceof DictionarysNodeModel) {
				treeContent.setNodeName(I18nUtil.getMessage("BUSINESS_DICTIONARY"));
				
			} else {
				treeContent.setNodeName("Undefined:" + obj.getClass().getSimpleName());
			} 
			
			return treeContent.getNodeName();
		}
		
		return null;
	}

}
