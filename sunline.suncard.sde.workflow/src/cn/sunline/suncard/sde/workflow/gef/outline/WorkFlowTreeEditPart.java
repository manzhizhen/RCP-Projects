/* 文件名：     WorkFlowTreeEditPart.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-9
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.outline;

import java.beans.PropertyChangeEvent;
import java.util.List;

import cn.sunline.suncard.sde.workflow.gef.model.WorkFlowModel;


/**
 * 描述
 * @author  易振强
 * @version 1.0, 2012-4-9
 * @see 
 * @since 1.0
 */
public class WorkFlowTreeEditPart  extends TreeEditPart {

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(WorkFlowModel.P_CHILDREN)){
			refreshChildren();
			
		} 
	}
	
	@Override
	protected List getModelChildren() {
		return  ((WorkFlowModel)getModel()).getOutlineChildren();
	}

}
