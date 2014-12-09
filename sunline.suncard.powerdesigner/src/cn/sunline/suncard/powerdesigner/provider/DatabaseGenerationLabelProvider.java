/* 文件名：     DatabaseGenerationLabelProvider.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-12-6
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.provider;

import java.io.File;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;

/**
 * 
 * @author Agree
 * @version 1.0, 2012-12-6
 * @see
 * @since 1.0
 */
public class DatabaseGenerationLabelProvider extends LabelProvider {

	private Image productImage = new Image(Display.getCurrent(), getClass()
			.getResourceAsStream(IDmImageKey.PRODUCT_IMAGE_16));

	private Image tableImage = new Image(Display.getCurrent(), getClass()
			.getResourceAsStream(IDmImageKey.TABLE_16));

	private Image moduleImage = new Image(Display.getCurrent(), getClass()
			.getResourceAsStream(IDmImageKey.MODULE_LABEL_16));
	@Override
	public Image getImage(Object element) {

		if (element instanceof ProductModel) {

			return productImage;
		}
		if (element instanceof TableModel) {

			return tableImage;
		}
		if (element instanceof ModuleModel) {

			return moduleImage;
		}
		return null;
	}

	@Override
	public String getText(Object element) {

		if (element instanceof ProductModel) {
			ProductModel prudutctModel = (ProductModel) element;
			return prudutctModel.getName();
		}
		if (element instanceof TableModel) {
			TableModel tableModel = (TableModel) element;
			return tableModel.getTableName();
		}
		if (element instanceof ModuleModel) {
			ModuleModel moduleModel = (ModuleModel) element;
			return moduleModel.getName();
		}
		
		return null;
	}

	@Override
	public void dispose() {
		productImage.dispose();
		tableImage.dispose();
		moduleImage.dispose();
		
		super.dispose();
	}
}
