/* 文件名：     EndTreeEditPart.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-9
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.outline;

import java.beans.PropertyChangeEvent;

import org.eclipse.gef.EditPolicy;

import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.workflow.gef.model.EndModel;
import cn.sunline.suncard.sde.workflow.gef.policy.DelModelEditPolicy;
import cn.sunline.suncard.sde.workflow.resource.IDmAppConstants;
import cn.sunline.suncard.sde.workflow.resource.IDmImageKey;

/**
 * 描述
 * @author  易振强
 * @version 1.0, 2012-4-9
 * @see 
 * @since 1.0
 */
public class EndTreeEditPart  extends TreeEditPart{
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(EndModel.TEXT)) {
			refreshVisuals();
		} 
	}
	
	@Override
	public void refreshVisuals() {
		EndModel model = (EndModel)getModel();
		setWidgetText(model.getLabelName());
		setWidgetImage(CacheImage.getCacheImage().getImage(IDmAppConstants.APPLICATION_ID, IDmImageKey.O_MODEL_START));
	}
	
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new DelModelEditPolicy());
	}
}