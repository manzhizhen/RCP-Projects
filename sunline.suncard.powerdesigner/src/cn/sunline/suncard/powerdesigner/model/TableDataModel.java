/**
 * 文件名：TableDataModel.java
 * 版权：
 * 描述：
 * 修改人： Manzhizhen
 * 修改时间：2013-4-11
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Element;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 表格初始化数据模型
 * @author Manzhizhen
 * @version 1.0 2013-4-11
 * @see
 * @since 1.0
 */
public class TableDataModel  implements DataObjectInterface{
	private Map<ColumnModel, String> dataMap = new HashMap<ColumnModel, String>();
	private boolean canModify = true; // 标记该初始化模型是否能在IDE上修改
	
	private static String elementName = "tableDataModel"; // 保存为document时候的顶节点name
	
	private Log logger = LogManager.getLogger(TableDataModel.class
			.getName());

	public Map<ColumnModel, String> getDataMap() {
		return dataMap;
	}

	public boolean isCanModify() {
		return canModify;
	}

	public void setCanModify(boolean canModify) {
		this.canModify = canModify;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getObjectFromElement(Element element, Object... obj) {
		if(element == null ) {
			logger.warn("TableDataModel的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("InitTableDataModel的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		return null;
	}

	
	
}
