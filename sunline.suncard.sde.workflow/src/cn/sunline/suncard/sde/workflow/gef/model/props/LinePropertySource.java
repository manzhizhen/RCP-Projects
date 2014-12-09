/* 文件名：     LinePropertySource.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-6
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.model.props;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.workflow.gef.model.LineModel;


/**
 * 连接线的属性视图
 * @author  易振强
 * @version 1.0, 2012-4-6
 * @see 
 * @since 1.0
 */
public class LinePropertySource  extends AbstractPropertySource{
	private LineModel lineModel;
	
	public LinePropertySource(LineModel lineModel) {
		this.lineModel = lineModel;
	}
	
	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		IPropertyDescriptor[] props = new IPropertyDescriptor[]{new TextPropertyDescriptor(
				LineModel.PROP_ID_LABEL, I18nUtil.getMessage("LINE_NAME"))};
		
		return props;
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (id.equals(LineModel.PROP_ID_LABEL)) { 
		      // 这里取得 Property view 中文本属性的值 
		      return lineModel.getLabelText();
		} 
		
		return null;
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (id.equals(LineModel.PROP_ID_LABEL)) { 
		      return true; 
		} 
		
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (id.equals(LineModel.PROP_ID_LABEL)) { 
		      lineModel.setLabelText((String) value); 
		} 
	}

}
