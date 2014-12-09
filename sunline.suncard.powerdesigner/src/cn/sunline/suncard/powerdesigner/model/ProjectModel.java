/* 文件名：     ProjectModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-28
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;

import cn.sunline.suncard.powerdesigner.model.db.DatabaseTypeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 项目模型
 * @author  Manzhizhen
 * @version 1.0, 2012-12-28
 * @see 
 * @since 1.0
 */
public class ProjectModel extends ProductModel{
	private String id = ""; // 项目ID，一个文件中的项目ID不能重复，项目ID是加入时自动生成的，不保存
	private String name = "新建项目名称"; // 项目名称
	private String note = "项目备注"; // 项目备注
	
	private boolean isAlreadyImport = false; // 标记该项目是否已经导入过模块
	
	private boolean isCanModifyTable = false; 	// 是否允许修改表数据
	private boolean isCanModifySql = false;		// 是否允许修改SQL脚本
	private boolean isCanModifyStored = false;	// 是否允许修改存储过程
	private boolean isCanModifyCode = false;	// 是否允许修改程序代码
	private boolean isCanModifyDoc = false;		// 是否允许修改文档
	
	private String fileName;	// 用于文件节点上显示的名字，默认时是文件的名称
	private File file; // 该项目所对应的zip文件
	private String folderName; // 打开项目文件其实是个解压过程，将该项目文件解压到工作空间下面的某个文件夹下面
	private ProjectGroupModel projectGroupModel = new ProjectGroupModel(); // 该项目所属的项目群
	private DatabaseTypeModel databaseTypeModel; // 所属的数据库类型
	
	private static String elementName = "projectModel"; // 保存为document时候的顶节点name
	private Log logger = LogManager.getLogger(ProjectModel.class
			.getName());
	
	public ProjectModel() {
		// 写入未定义的数据库类型
		databaseTypeModel = new DatabaseTypeModel();
		databaseTypeModel.setDatabaseName(DmConstants.UNDEFINED);
		databaseTypeModel.setType(DmConstants.UNDEFINED);
		databaseTypeModel.setVersion(DmConstants.UNDEFINED);
	}
	
	public ProjectGroupModel getProjectGroupModel() {
		return projectGroupModel;
	}

	public void setProjectGroupModel(ProjectGroupModel projectGroupModel) {
		this.projectGroupModel = projectGroupModel;
	}
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
		if(file != null) {
			fileName = file.getName();
		}
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public static String getElementName() {
		return elementName;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	public boolean isAlreadyImport() {
		return isAlreadyImport;
	}

	public void setAlreadyImport(boolean isAlreadyImport) {
		this.isAlreadyImport = isAlreadyImport;
	}

	public DatabaseTypeModel getDatabaseTypeModel() {
		return databaseTypeModel;
	}

	public void setDatabaseTypeModel(DatabaseTypeModel databaseTypeModel) {
		this.databaseTypeModel = databaseTypeModel;
	}

	public boolean isCanModifyTable() {
		return isCanModifyTable;
	}

	public void setCanModifyTable(boolean isCanModifyTable) {
		this.isCanModifyTable = isCanModifyTable;
	}

	public boolean isCanModifySql() {
		return isCanModifySql;
	}

	public void setCanModifySql(boolean isCanModifySql) {
		this.isCanModifySql = isCanModifySql;
	}

	public boolean isCanModifyStored() {
		return isCanModifyStored;
	}

	public void setCanModifyStored(boolean isCanModifyStored) {
		this.isCanModifyStored = isCanModifyStored;
	}

	public boolean isCanModifyCode() {
		return isCanModifyCode;
	}

	public void setCanModifyCode(boolean isCanModifyCode) {
		this.isCanModifyCode = isCanModifyCode;
	}

	public boolean isCanModifyDoc() {
		return isCanModifyDoc;
	}

	public void setCanModifyDoc(boolean isCanModifyDoc) {
		this.isCanModifyDoc = isCanModifyDoc;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element projectModelElement = parent.addElement(elementName);
		
		projectModelElement.addAttribute("id", id == null ? "" : id);
		projectModelElement.addAttribute("name", id == null ? "" : name);
		projectModelElement.addAttribute("note", id == null ? "" : note);
		projectModelElement.addAttribute("isAlreadyImport", isAlreadyImport + "");	
		projectModelElement.addAttribute("isCanModifyTable", isCanModifyTable + "");	
		projectModelElement.addAttribute("isCanModifySql", isCanModifySql + "");	
		projectModelElement.addAttribute("isCanModifyStored", isCanModifyStored + "");	
		projectModelElement.addAttribute("isCanModifyCode", isCanModifyCode + "");	
		projectModelElement.addAttribute("isCanModifyDoc", isCanModifyDoc + "");	
		
		databaseTypeModel.getElementFromObject(projectModelElement);
		
		projectGroupModel.getElementFromObject(projectModelElement);
		
		for(ModuleModel moduleModel : moduleModelSet) {
			moduleModel.getElementFromObject(projectModelElement);
		}
		
		Element sqlListE = projectModelElement.addElement("sqlList");
		for(SqlScriptModel sqlModel : sqlSet) {
			sqlModel.getElementFromObject(sqlListE);
		}
		
		Element storedProcedureListE = projectModelElement.addElement("storedProcedureList");
		for(StoredProcedureModel storedProcedureModel : storedProcedureSet) {
			storedProcedureModel.getElementFromObject(storedProcedureListE);
		}
		
		return projectModelElement;
	}
	
	@Override
	public ProjectModel getObjectFromElement(Element element, Object... obj) {
		if(element == null ) {
			logger.warn("ProjectModel的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("ProjectModel的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		if(obj.length != 1 || !(obj[0] instanceof HashMap)) {
			logger.warn("传入的参数列表为空或不正确，无法将xml转换为ProjectModel对象！");
			return null;
		}
		
		setId(element.attributeValue("id").trim());
		setName(element.attributeValue("name").trim());
		setNote(element.attributeValue("note").trim());
		setAlreadyImport(new Boolean(element.attributeValue("isAlreadyImport").trim()));
		setCanModifyCode(new Boolean(element.attributeValue("isCanModifyCode").trim()));
		setCanModifyDoc(new Boolean(element.attributeValue("isCanModifyDoc").trim()));
		setCanModifySql(new Boolean(element.attributeValue("isCanModifySql").trim()));
		setCanModifyStored(new Boolean(element.attributeValue("isCanModifyStored").trim()));
		setCanModifyTable(new Boolean(element.attributeValue("isCanModifyTable").trim()));
		
		databaseTypeModel.getObjectFromElement(element.element(DatabaseTypeModel.getElementName()));
		
		projectGroupModel.getObjectFromElement(element.element(projectGroupModel.getElementName()));
		
		moduleModelSet.clear();
		List<Element> moduleElements = element.elements(ModuleModel.getElementName());
		if(moduleElements != null) {
			for(Element moduleElement : moduleElements) {
				ModuleModel moduleModel = new ModuleModel();
				moduleModel.getObjectFromElement(moduleElement, obj[0]);
				
				moduleModelSet.add(moduleModel);
			}
		}
		
		sqlSet.clear();
		List<Element> elementList = element.element("sqlList").elements();
		if(elementList != null) {
			for(Element sqlElement : elementList) {
				SqlScriptModel sqlModel = new SqlScriptModel();
				sqlModel.setProductModel(this);
				sqlModel.getObjectFromElement(sqlElement);
				
				sqlSet.add(sqlModel);
			}
		}
		
		storedProcedureSet.clear();
		elementList = element.element("storedProcedureList").elements();
		if(elementList != null) {
			for(Element storedProcedureElement : elementList) {
				StoredProcedureModel storedModel = new StoredProcedureModel();
				storedModel.setProductModel(this);
				storedModel.getObjectFromElement(storedProcedureElement);
				
				storedProcedureSet.add(storedModel);
			}
		}
		
		return this;
	}
	
	
}
