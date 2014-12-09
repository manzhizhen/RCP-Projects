/* 文件名：     WorkFlowTreeNode.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.model;

import cn.sunline.suncard.sde.workflow.gef.model.WorkFlowModel;

/**
 * 树节点数据对象
 * 里面保存了工作流的全部信息
 * @author 易振强
 * @version 1.0, 2012-3-26
 * @see
 * @since 1.0
 */
public class WorkFlowTreeNode implements java.io.Serializable{
	private String id = null;
	private String name = null;
	private String desc = null;
	
	private WorkFlowModel model;
	
	public WorkFlowTreeNode() {
		model = new WorkFlowModel();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public WorkFlowModel getModel() {
		return model;
	}

	public void setModel(WorkFlowModel model) {
		this.model = model;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
