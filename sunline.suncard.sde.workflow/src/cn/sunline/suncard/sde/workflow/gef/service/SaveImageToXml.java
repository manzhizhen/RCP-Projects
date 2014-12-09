/* 文件名：     SaveImageToXML.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	将工作流保存为图片时，记录各节点的坐标
 * 修改人：     易振强
 * 修改时间：2012-3-30
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.service;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.runner.Description;

import cn.sunline.suncard.sde.workflow.gef.model.AbstractLineModel;
import cn.sunline.suncard.sde.workflow.gef.model.AbstractModel;
import cn.sunline.suncard.sde.workflow.gef.model.BaseFigure;
import cn.sunline.suncard.sde.workflow.gef.model.EndModel;
import cn.sunline.suncard.sde.workflow.gef.model.StartModel;
import cn.sunline.suncard.sde.workflow.gef.model.TaskModel;
import cn.sunline.suncard.sde.workflow.gef.model.WorkFlowModel;
import cn.sunline.suncard.sde.workflow.model.CommonNode;
import cn.sunline.suncard.sde.workflow.model.DecisionNode;
import cn.sunline.suncard.sde.workflow.model.EndNode;
import cn.sunline.suncard.sde.workflow.model.LineNode;
import cn.sunline.suncard.sde.workflow.model.StartNode;
import cn.sunline.suncard.sde.workflow.model.TaskNode;
import cn.sunline.suncard.sde.workflow.model.WorkFlowTreeNode;

/**
 * 将工作流保存为图片时，记录各节点的坐标
 * @author  易振强
 * @version 1.0, 2012-3-30
 * @see 
 * @since 1.0
 */
public class SaveImageToXml {
	private  WorkFlowTreeNode workFlowTreeNode;
	private  long imageX;
	private  long imageY;

	public String getPointXmlFormModel() {
		if(workFlowTreeNode == null) {
			return null;
		}
		
		WorkFlowModel model = workFlowTreeNode.getModel();
		if(model == null) {
			return null;
		}
		
		Document doc = DocumentHelper.createDocument();
		
		Element rootElement = doc.addElement("root-container");
		rootElement.addAttribute("name", workFlowTreeNode.getName());
		rootElement.addAttribute("width", imageX + "");
		rootElement.addAttribute("height", imageY + "");
		
		List<AbstractModel> chileModels = model.getChildren();
		for(AbstractModel childModel : chileModels) {
			
			if(childModel instanceof StartModel) {
				StartNode startNode = ((StartModel)childModel).getStartNode();
				
				Element nodeElement = rootElement.addElement("node");
				
				nodeElement.addAttribute("name", startNode.getName());
				nodeElement.addAttribute("x", childModel.getConstraint().getLocation().x() + "");
				nodeElement.addAttribute("y", childModel.getConstraint().getLocation().y() + "");
				nodeElement.addAttribute("width", BaseFigure.FIGURE_WIDTH + "");
				nodeElement.addAttribute("height", BaseFigure.FIGURE_HIGHT + "");
				
				Element edgeElement = nodeElement.addElement("edge");
				Element labelElement = edgeElement.addElement("label");
				labelElement.addAttribute("x", "0");
				labelElement.addAttribute("y", "0");
				
			} else if(childModel instanceof TaskModel) {
				TaskModel taskModel = (TaskModel) childModel;
				TaskNode taskNode = taskModel.getTaskNode();
				
				Element taskElement = rootElement.addElement("node");
				
				String type = taskNode.getType() == null ? "" : taskNode.getType();
//				taskElement.addAttribute("name", taskNode.getName() + "-" + type);
				taskElement.addAttribute("name", taskNode.getName());
				taskElement.addAttribute("x", childModel.getConstraint().getLocation().x() + "");
				taskElement.addAttribute("y", childModel.getConstraint().getLocation().y() + "");
				taskElement.addAttribute("width", BaseFigure.FIGURE_WIDTH + "");
				taskElement.addAttribute("height", BaseFigure.FIGURE_HIGHT + "");
				
				Element edgeElement = taskElement.addElement("edge");
				Element labelElement = edgeElement.addElement("label");
				labelElement.addAttribute("x", "0");
				labelElement.addAttribute("y", "0");
				
				DecisionNode decisionNode = taskNode.getDecisionNode();
				Element deciElement = rootElement.addElement("node");
				
				deciElement.addAttribute("name", decisionNode.getName());
				deciElement.addAttribute("x", childModel.getConstraint().getLocation().x() + "");
				deciElement.addAttribute("y", childModel.getConstraint().getLocation().y() + "");
				deciElement.addAttribute("width", BaseFigure.FIGURE_WIDTH + "");
				deciElement.addAttribute("height", BaseFigure.FIGURE_HIGHT + "");
				
				List<AbstractLineModel> lineList = taskModel.getSourceConnections();
				int size = lineList.size();
				for(int i = 0; i < size; i++) {
					Element edgeElement1 = deciElement.addElement("edge");
					Element labelElement1 = edgeElement1.addElement("label");
					labelElement1.addAttribute("x", "0");
					labelElement1.addAttribute("y", "0");
					
					
				}
				
				
//				List<CommonNode> nodeList =  decisionNode.getCommonNodes();
//				for(CommonNode commonNode : nodeList) {
//					node += "<node name=\"" + commonNode.getName() + "\"" + chileModelXY;
//					node += labelXY + "</node>";
//				}
				
				for(AbstractLineModel line : lineList) {
					Element lineElement = rootElement.addElement("node");
					lineElement.addAttribute("name", ((LineNode)line.getDataObject()).getCommonNode().getName());
					lineElement.addAttribute("x", childModel.getConstraint().getLocation().x() + "");
					lineElement.addAttribute("y", childModel.getConstraint().getLocation().y() + "");
					lineElement.addAttribute("width", BaseFigure.FIGURE_WIDTH + "");
					lineElement.addAttribute("height", BaseFigure.FIGURE_HIGHT + "");
					
					Element edgeElement2 = lineElement.addElement("edge");
					Element labelElement2 = edgeElement2.addElement("label");
					labelElement2.addAttribute("x", "0");
					labelElement2.addAttribute("y", "0");
				}
				
			} else if(childModel instanceof EndModel) {
				EndNode endNode = ((EndModel) childModel).getEndNode();
				
				Element endElement = rootElement.addElement("node");
				
//				endElement.addAttribute("name", endNode.getName() + "-");
				endElement.addAttribute("name", endNode.getName());
				endElement.addAttribute("x", childModel.getConstraint().getLocation().x() + "");
				endElement.addAttribute("y", childModel.getConstraint().getLocation().y() + "");
				endElement.addAttribute("width", BaseFigure.FIGURE_WIDTH + "");
				endElement.addAttribute("height", BaseFigure.FIGURE_HIGHT + "");
			}
		}
		
		return doc.asXML();
	}

	public void setWorkFlowTreeNode(WorkFlowTreeNode workFlowTreeNode) {
		this.workFlowTreeNode = workFlowTreeNode;
	}

	public void setImageX(long imageX) {
		this.imageX = imageX;
	}

	public void setImageY(long imageY) {
		this.imageY = imageY;
	}
	
}
