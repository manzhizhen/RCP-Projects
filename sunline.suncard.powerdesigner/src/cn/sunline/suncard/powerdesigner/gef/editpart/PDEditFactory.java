/* 文件名：     EditFactory.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.editpart;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import cn.sunline.suncard.powerdesigner.gef.model.DatabaseDiagramGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.LineGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableShortcutGefModel;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 控制器工厂
 * 
 * @author 易振强
 * @version 1.0, 2012-3-26
 * @see
 * @since 1.0
 */
public class PDEditFactory implements EditPartFactory {
	private Log logger = LogManager.getLogger(PDEditFactory.class.getName());
	
	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = getPartForElement(model);
		part.setModel(model);
		return part;
	}

	public EditPart getPartForElement(Object modelElement) {
		if(modelElement instanceof DatabaseDiagramGefModel) {
			return new DatabaseDiagramModelEditPart();

		} else if(modelElement instanceof TableGefModel) {
			return new TableGefModelEditPart();

		} else if(modelElement instanceof TableShortcutGefModel) {
			return new TableShortcutGefModelEditPart();

		} else if(modelElement instanceof LineGefModel) {
			return new LineGefModelEditPart((LineGefModel) modelElement);
			
		} else {
			logger.error("没有找到该模型对应的控制器：" + modelElement.getClass().toString());
			return null;
		}

	}

}
