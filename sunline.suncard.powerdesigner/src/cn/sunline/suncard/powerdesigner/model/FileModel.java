/* 文件名：     FileModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-5
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;
import org.eclipse.gef.commands.CommandStack;

import cn.sunline.suncard.powerdesigner.models.DocumentsNodeModel;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 文件模型，因为在我们的软件中可以同时打开多个文件。
 * 一个文件模型中可以建立多个物理数据模型(PhysicalDataModel)。
 * @author  Manzhizhen
 * @version 1.0, 2012-9-5
 * @see 
 * @since 1.0
 */
public class FileModel implements DataObjectInterface{
	private String fileName;	// 用于文件节点上显示的名字，默认时是文件的名称
	private File file;	//所属ZIP文件文件位置
	private String folderName; // 打开文件其实是个解压过程，将该文件解压到工作空间下面的某个文件夹下面
	private LinkedHashSet<PhysicalDataModel> physicalDataSet = new LinkedHashSet<PhysicalDataModel>();

	private static String elementName = "fileModel"; // 保存为document时候的顶节点name
	private static Log logger = LogManager.getLogger(FileModel.class
			.getName());
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
		
		if(file != null) {
			fileName = file.getName();
		}
	}

	public final Set<PhysicalDataModel> getPhysicalDataSet() {
		return physicalDataSet;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element fileElement = parent.addElement(elementName);
		for(PhysicalDataModel physicalDataModel : physicalDataSet) {
			physicalDataModel.getElementFromObject(fileElement);
		}
		
		return fileElement;
	}

	@Override
	public FileModel getObjectFromElement(Element element, Object...obj) {
		if(element == null ) {
			logger.warn("FileModel的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("FileModel的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		List<Element> physicalDataModelEList = element.elements();
		for(Element physicalDataModelE : physicalDataModelEList) {
			PhysicalDataModel physicalDataModel = new PhysicalDataModel();
			physicalDataModel.setFileModel(this);
			physicalDataSet.add(physicalDataModel.getObjectFromElement(physicalDataModelE));
		}
		
		
		return this;
	}
	
	/**
	 * 返回该文件中的所有TableModel
	 * @return
	 */
	public final Set<TableModel> getAllTableModel() {
		Set<TableModel> tableModelSet = new HashSet<TableModel>();
		for(PhysicalDataModel physicalDataModel : physicalDataSet) {
			tableModelSet.addAll(physicalDataModel.getAllTables());
		}
		
		return tableModelSet;
	}
	
	/**
	 * 返回该文件中的所有ColumnModel
	 * @return
	 */
	public final Set<ColumnModel> getAllColumnModel() {
		Set<ColumnModel> columnModelSet = new HashSet<ColumnModel>();
		Set<TableModel> tableModelSet = getAllTableModel();
		for(TableModel tableModel : tableModelSet) {
			columnModelSet.addAll(tableModel.getColumnList());
		}
		
		return columnModelSet;
	}

	/**
	 * 返回该文件中的所有TableShortcutModel
	 * @return
	 */
	public final Set<TableShortcutModel> getAllTableShortcutModel() {
		Set<TableShortcutModel> tableShortcutModelSet = new HashSet<TableShortcutModel>();
		for(PhysicalDataModel physicalDataModel : physicalDataSet) {
			tableShortcutModelSet.addAll(physicalDataModel.getAllTableShortcuts());
		}
		
		return tableShortcutModelSet;
	}
	
	/**
	 * 返回该文件中的所有PhysicalDiagramModel
	 * @return
	 */
	public final Set<PhysicalDiagramModel> getAllPhysicalDiagramModel() {
		Set<PhysicalDiagramModel> physicalDiagramModelSet = new HashSet<PhysicalDiagramModel>();
		for(PhysicalDataModel physicalDataModel : physicalDataSet) {
			physicalDiagramModelSet.addAll(physicalDataModel.getAllPhysicalDiagramModels());
		}
		
		return physicalDiagramModelSet;
	}
	
	/**
	 * 获取该文件中所用到的约束名称，避免重复
	 * @param fileModel
	 * @return
	 */
	public static Set<String> getAllConstraintStr(FileModel fileModel) {
		if(fileModel == null) {
			logger.warn("传入的FileModel为空，无法获取该文件中所用到的约束名称！");
			return null;
		}
		
		Set<String> allConstraintSet = new HashSet<String>();
		Set<TableModel> tableModels = fileModel.getAllTableModel();
		for(TableModel tableModel : tableModels) {
			List<LineModel> lineModels = tableModel.getLineModelList();
			for(LineModel lineModel : lineModels) {
				allConstraintSet.add(lineModel.getConstraintName());
			}
		}
		
		return allConstraintSet;
	}
	
	/**
	 * 通过一个对象返回他的FileModel
	 */
	public static FileModel getFileModelFromObj(Object object, Object...obj) {
		if(object == null) {
			logger.error("传入的对象为空，无法找到对应FileModel！");
			return null;
		}
		
		FileModel fileModel = null;
		
		if(object instanceof TreeContent) {
			object = ((TreeContent)object).getObj();
		}
		
		if(object instanceof FileModel) {
			fileModel = (FileModel) object;
			
		} else if(object instanceof PhysicalDataModel) {
			PhysicalDataModel physicalDataModel = (PhysicalDataModel) object;
			fileModel = physicalDataModel.getFileModel();
			
		} else if(object instanceof PackageModel) {
			PackageModel packageModel = (PackageModel) object;
			fileModel = packageModel.getPhysicalDataModel().getFileModel();
			
		}  else if(object instanceof PhysicalDiagramModel) {
			PhysicalDiagramModel physicalDiagramModel = (PhysicalDiagramModel) object;
			fileModel = physicalDiagramModel.getPackageModel().getPhysicalDataModel()
					.getFileModel();
			
		} else if(object instanceof TableModel) {
			TableModel tableModel = (TableModel) object;
			fileModel = tableModel.getPhysicalDiagramModel().getPackageModel().getPhysicalDataModel()
					.getFileModel();
			
		} else if(object instanceof TableShortcutModel) {
			TableShortcutModel tableShortcutModel = (TableShortcutModel) object;
			fileModel = tableShortcutModel.getPhysicalDiagramModel().getPackageModel().getPhysicalDataModel()
					.getFileModel();
			
		} else if(object instanceof ColumnModel) {
			ColumnModel columnModel = (ColumnModel) object;
			fileModel = columnModel.getTableModel().getPhysicalDiagramModel().getPackageModel()
					.getPhysicalDataModel().getFileModel();
			
		} else if(object instanceof ProductModel) {
			ProductModel productModel = (ProductModel) object;
			fileModel = productModel.getPhysicalDataModel().getFileModel();
			
		} else if(object instanceof ModuleModel) {
			ModuleModel moduleModel = (ModuleModel) object;
			fileModel = moduleModel.getProductModel().getPhysicalDataModel().getFileModel();
			
		} else if(object instanceof DocumentsNodeModel) {
			DocumentsNodeModel documentsNodeModel = (DocumentsNodeModel) object;
			ProductModel productModel = documentsNodeModel.getParentModel();
			if(!(productModel instanceof ProjectModel)) {
				fileModel = productModel.getPhysicalDataModel().getFileModel();
			}
			
		} else if(object instanceof DocumentCategoryModel) {
			DocumentCategoryModel documentCategoryModel = (DocumentCategoryModel) object;
			ProductModel productModel = documentCategoryModel.getDocumentsNodeModel().getParentModel();
			if(!(productModel instanceof ProjectModel)) {
				fileModel = productModel.getPhysicalDataModel().getFileModel();
			}
			
		}  else if(object instanceof DocumentModel) {
			DocumentModel documentModel = (DocumentModel) object;
			ProductModel productModel = documentModel.getDocumentCategoryModel().getDocumentsNodeModel().getParentModel();
			if(!(productModel instanceof ProjectModel)) {
				fileModel = productModel.getPhysicalDataModel().getFileModel();
			}
			
		} else {
			logger.error("未知对象：" + object.getClass().getSimpleName() + "无法返回对应的FileModel！");
		}
		
		if(fileModel == null) {
			logger.error("返回的FileModel为空！传入用来查找的对象类型为：" + object.getClass().getSimpleName());
		}
		
		return fileModel;
	}
	
	public static String getElementName() {
		return elementName;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof FileModel) {
			FileModel fileModel = (FileModel) obj;
			
			return file.getAbsolutePath().equals(fileModel.getFile() == null ? null : 
				fileModel.getFile().getAbsolutePath());
		}
		
		return super.equals(obj);
	}
}
