/* 文件名：     PackageModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-5
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;

import cn.sunline.suncard.powerdesigner.model.factory.PhysicalDiagramModelFactory;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 包模型，一个包属于一个物理数据模型，下面可以包含多个物理图形模型
 * @author  Manzhizhen
 * @version 1.0, 2012-12-5
 * @see 
 * @since 1.0
 */
public class PackageModel implements DataObjectInterface{
	private String id; 
	private String name;
	private String note;
	
	private PhysicalDataModel physicalDataModel = new PhysicalDataModel(); // 该包模型所属的物理数据模型
	private LinkedHashSet<PhysicalDiagramModel> physicalDiagramModelSet = new LinkedHashSet<PhysicalDiagramModel>(); //一个包下面的所有物理图形模型
	
	private static String elementName = "packageModel"; // 保存为document时候的顶节点name
	
	private Log logger = LogManager.getLogger(PackageModel.class
			.getName());
	
	public Set<PhysicalDiagramModel> getPhysicalDiagramModelSet() {
		return physicalDiagramModelSet;
	}

	public void setPhysicalDiagramModelSet(
			LinkedHashSet<PhysicalDiagramModel> physicalDiagramModelSet) {
		this.physicalDiagramModelSet = physicalDiagramModelSet;
	}

	public PhysicalDataModel getPhysicalDataModel() {
		return physicalDataModel;
	}

	public void setPhysicalDataModel(PhysicalDataModel physicalDataModel) {
		this.physicalDataModel = physicalDataModel;
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

	/**
	 * 获取一个包模型下面的所有表格
	 * @return
	 */
	public Set<TableModel> getAllTableModel() {
		Set<TableModel> tableModelSet = new HashSet<TableModel>();
		for(PhysicalDiagramModel physicalDiagramModel : physicalDiagramModelSet) {
			tableModelSet.addAll(physicalDiagramModel.getTableMap().values());
		}
		
		return tableModelSet;
	}
	
	@Override
	public Element getElementFromObject(Element parent) {
		Element moduleModelElement = parent.addElement(elementName);

		moduleModelElement.addAttribute("id", id == null ? "" : id);
		moduleModelElement.addAttribute("name", name == null ? "" : name);
		moduleModelElement.addAttribute("note", note == null ? "" : note);
		
		Element physicalDiagramModelElement;
		for(PhysicalDiagramModel physicalDiagramModel : physicalDiagramModelSet) {
			physicalDiagramModelElement = moduleModelElement.addElement(PhysicalDiagramModel.getElementName());
			physicalDiagramModelElement.addAttribute("id", physicalDiagramModel.getId());
//			physicalDiagramModel.getElementFromObject(moduleModelElement);
		}
		
		return moduleModelElement;
	}

	@Override
	public PackageModel getObjectFromElement(Element element, Object...obj) {
		if(element == null ) {
			logger.warn("PackageModel的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("PackageModel的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		setId(element.attributeValue("id").trim());
		setName(element.attributeValue("name").trim());
		setNote(element.getText().trim());
		
		physicalDiagramModelSet.clear();
		List<Element> physicalDiagramModelElementList = element.elements(PhysicalDiagramModel
				.getElementName());
		if(physicalDiagramModelElementList != null) {
			for(Element physicalElement : physicalDiagramModelElementList) {
//				PhysicalDiagramModel physicalDiagramModel = new PhysicalDiagramModel();
				PhysicalDiagramModel physicalDiagramModel = PhysicalDiagramModelFactory
						.getPhysicalDiagramModel(FileModel.getFileModelFromObj(this)
								, physicalElement.attributeValue("id").trim());
				
				if(physicalDiagramModel != null) {
					physicalDiagramModel.setPackageModel(this);
					physicalDiagramModelSet.add(physicalDiagramModel);
//					physicalDiagramModelSet.add(physicalDiagramModel.getObjectFromElement(physicalElement));
					
				} else {
					logger.error("一个物理图形模型添加到包模型失败，物理图形模型工厂找不到对应的物理图形ID:" 
							+ physicalElement.attributeValue("id").trim());
				}
			}
		}
		
		return this;
	}

}
