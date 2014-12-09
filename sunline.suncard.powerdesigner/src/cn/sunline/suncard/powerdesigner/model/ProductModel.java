/* 文件名：     ProductModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-22
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 产品模型
 * @author  Manzhizhen
 * @version 1.0, 2012-11-22
 * @see 
 * @since 1.0
 */
public class ProductModel implements DataObjectInterface{
	private String id = ""; // 产品ID，一个文件中的产品ID不能重复
	private String name = ""; // 产品名称
	private String note = ""; // 产品备注
	
	private PhysicalDataModel physicalDataModel = new PhysicalDataModel(); // 该产品属于哪个物理数据模型
	protected LinkedHashSet<ModuleModel> moduleModelSet = new LinkedHashSet<ModuleModel>();  // 模块模型的Set
	protected LinkedHashSet<SqlScriptModel> sqlSet = new LinkedHashSet<SqlScriptModel>(); // 储存SQL语句的Set；
	protected LinkedHashSet<StoredProcedureModel> storedProcedureSet = new LinkedHashSet<StoredProcedureModel>(); // 储存过程的Set
	protected LinkedHashSet<DocumentCategoryModel> documentCategorySet = new LinkedHashSet<DocumentCategoryModel>(); // 文档分类的Set
	
	private static String elementName = "productModel"; // 保存为document时候的顶节点name
	
	private Log logger = LogManager.getLogger(ProductModel.class
			.getName());
	
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

	public PhysicalDataModel getPhysicalDataModel() {
		return physicalDataModel;
	}

	public void setPhysicalDataModel(PhysicalDataModel physicalDataModel) {
		this.physicalDataModel = physicalDataModel;
	}
	
	public LinkedHashSet<SqlScriptModel> getSqlSet() {
		return sqlSet;
	}

	public void setSqlSet(LinkedHashSet<SqlScriptModel> sqlSet) {
		this.sqlSet = sqlSet;
	}

	public LinkedHashSet<StoredProcedureModel> getStoredProcedureSet() {
		return storedProcedureSet;
	}

	public void setStoredProcedureSet(LinkedHashSet<StoredProcedureModel> storedProcedureSet) {
		this.storedProcedureSet = storedProcedureSet;
	}

	public Set<ModuleModel> getModuleModelSet() {
		return moduleModelSet;
	}

	public void setModuleModelSet(LinkedHashSet<ModuleModel> moduleModelSet) {
		this.moduleModelSet = moduleModelSet;
	}
	
	public LinkedHashSet<DocumentCategoryModel> getDocumentCategorySet() {
		return documentCategorySet;
	}

	public void setDocumentCategorySet(
			LinkedHashSet<DocumentCategoryModel> documentCategorySet) {
		this.documentCategorySet = documentCategorySet;
	}

	/**
	 * 获取一个产品里面用到的所有表格
	 * @return
	 */
	public Set<TableModel> getAllTableModel() {
		Set<TableModel> tableModelSet = new HashSet<TableModel>();
		for(ModuleModel moduleModel : moduleModelSet) {
			tableModelSet.addAll(moduleModel.getTableModelSet());
		}
		
		return tableModelSet;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element productModelElement = parent.addElement(elementName);
		
		productModelElement.addAttribute("id", id == null ? "" : id);
		productModelElement.addAttribute("name", id == null ? "" : name);
		productModelElement.addAttribute("note", id == null ? "" : note);
		productModelElement.addAttribute("physicalDataModelId", physicalDataModel.getId());
		
		for(ModuleModel moduleModel : moduleModelSet) {
			moduleModel.getElementFromObject(productModelElement);
		}
		
		Element sqlListE = productModelElement.addElement("sqlList");
		for(SqlScriptModel sqlModel : sqlSet) {
			sqlModel.getElementFromObject(sqlListE);
		}
		
		Element storedProcedureListE = productModelElement.addElement("storedProcedureList");
		for(StoredProcedureModel storedProcedureModel : storedProcedureSet) {
			storedProcedureModel.getElementFromObject(storedProcedureListE);
		}
		
		return productModelElement;
	}

	@Override
	public ProductModel getObjectFromElement(Element element, Object...obj) {
		if(element == null ) {
			logger.warn("ProductModel的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("ProductModel的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		if(obj.length != 1 || !(obj[0] instanceof FileModel)) {
			logger.warn("传入的参数列表为空或不正确，无法将xml转换为ProductModel对象！");
			return null;
		}
		
		FileModel fileModel = (FileModel) obj[0];
		
		setId(element.attributeValue("id").trim());
		setName(element.attributeValue("name").trim());
		setNote(element.attributeValue("note").trim());
		
		moduleModelSet.clear();
		List<Element> moduleElements = element.elements(ModuleModel.getElementName());
		if(moduleElements != null) {
			for(Element moduleElement : moduleElements) {
				ModuleModel moduleModel = new ModuleModel();
				moduleModel.setProductModel(this);
				moduleModelSet.add(moduleModel.getObjectFromElement(moduleElement, fileModel));
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
		
		// 注意：PhysicalDataModel需要额外设置
		
		return this;
	}

	public static String getElementName() {
		return elementName;
	}
	
	

}
