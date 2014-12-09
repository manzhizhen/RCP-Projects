/* 文件名：     EndTreeEditPart.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-6-6
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.outline;

import java.beans.PropertyChangeEvent;

import org.eclipse.gef.EditPolicy;

import cn.sunline.suncard.powerdesigner.gef.model.TableGefModel;
import cn.sunline.suncard.powerdesigner.gef.policy.DelModelEditPolicy;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.sde.bs.resource.CacheImage;

/**
 * 
 * @author  易振强
 * @version 1.0, 2012-6-6
 * @see 
 * @since 1.0
 */
public class TableModelTreeEditPart extends TreeEditPart {
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(TableGefModel.TEXT)) {
			refreshVisuals();
		} 
	}
	
	@Override
	public void refreshVisuals() {
		TableGefModel model = (TableGefModel)getModel();
		setWidgetText(((TableModel)model.getDataObject()).getTableName());
		setWidgetImage(CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, IDmImageKey.TABLE_16));
	}
	
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new DelModelEditPolicy());
	}

}
