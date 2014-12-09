/* 文件名：     WorkFlowCoreProcess.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	由gef生成xml的核心类
 * 修改人：     tpf
 * 修改时间：2012-3-28
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import cn.sunline.suncard.sde.workflow.gef.model.AbstractLineModel;
import cn.sunline.suncard.sde.workflow.gef.model.AbstractModel;
import cn.sunline.suncard.sde.workflow.gef.model.EndModel;
import cn.sunline.suncard.sde.workflow.gef.model.StartModel;
import cn.sunline.suncard.sde.workflow.gef.model.TaskModel;
import cn.sunline.suncard.sde.workflow.gef.model.WorkFlowModel;
import cn.sunline.suncard.sde.workflow.model.CommonNode;
import cn.sunline.suncard.sde.workflow.model.DecisionNode;
import cn.sunline.suncard.sde.workflow.model.EndNode;
import cn.sunline.suncard.sde.workflow.model.LineNode;
import cn.sunline.suncard.sde.workflow.model.DataObjectInterface;
import cn.sunline.suncard.sde.workflow.model.Task;
import cn.sunline.suncard.sde.workflow.model.TaskNode;
import cn.sunline.suncard.sde.workflow.tree.WorkFlowTreeViewPart;

/**
 * 由gef生成xml的核心类
 * @author  tpf
 * @version 1.0, 2012-3-28
 * @see 
 * @since 1.0
 */
public class WorkFlowCoreProcess {

	private Logger logger = Logger.getLogger(WorkFlowCoreProcess.class);
	
	private WorkFlowInfoProcess wfInfoProcess = new WorkFlowInfoProcess();
	
	/**
	 * 保存gef到xml
	 * 修改日期：2012-3-28
	 * @author: tpf
	 */
	public String saveGef2Xml(String processDefinitionName) {
		wfInfoProcess.initWorkFlowInfo();									//初始化workflow模型信息
		
		Document document = DocumentHelper.createDocument();				//创建document对象
		
		Element processDefinition = initDocument(document, processDefinitionName);					//初始化processDefinition元素
		
		Element nodes  = buildNode2Document(processDefinition);				//向document添加Node节点信息
		
		String logicXml = document.asXML().replaceAll("\"", "'");			//将双引号替换为单引号

		logger.info("GEF图形生成的xml："+logicXml);
		
		return logicXml;
	}

	/**
	 * 保存gef到xml
	 * 修改日期：2012-3-28
	 * @author: tpf
	 */
	public String saveGef2Xml(WorkFlowModel workFlowModel, String processDefinitionName) {
		
		wfInfoProcess.initWorkFlowInfo(workFlowModel);							//初始化workflow模型信息
		
		Document document = DocumentHelper.createDocument();				//创建document对象
		
		Element processDefinition = initDocument(document, processDefinitionName);					//初始化processDefinition元素
		
		Element nodes  = buildNode2Document(processDefinition);				//向document添加Node节点信息
		
		String logicXml = document.asXML().replaceAll("\"", "'");			//将双引号替换为单引号

		logger.info("GEF图形生成的xml："+logicXml);
		
		return logicXml;
	}
	
	/**
	 * 初始化document对象
	 * 修改日期：2012-3-28
	 * @author: tpf
	 */
	private Element initDocument(Document document, String processDefinitionName) {
		Element processDefinition = document.addElement("process-definition");
		processDefinition.addAttribute("name", processDefinitionName);
		return processDefinition;
	}
	
	/**
	 * 构建结点模型
	 * 修改日期：2012-3-28
	 * @author: tpf
	 */
	private Element buildNode2Document(Element processDefinition) {
		List<AbstractModel> nodeModels = wfInfoProcess.getNodeModels();
		for (AbstractModel nodeModel : nodeModels) {
			if(nodeModel instanceof StartModel) {
				StartModel startModel = (StartModel)nodeModel;
				//组装开始结点
				Element startState = processDefinition.addElement("start-state");
				startState.addAttribute("name", startModel.getStartNode().getName());
				Element description = startState.addElement("description");
				description.addText(startModel.getStartNode().getDescription());
				Element transition = startState.addElement("transition");
//				transition.addAttribute("to", startModel.getStartNode().getConnectLine().getTargetNodeName());
				List<AbstractLineModel> lines = startModel.getSourceConnections();
				if(lines != null && !lines.isEmpty()) {
					AbstractModel absModel = lines.get(0).getTarget();
					if(absModel instanceof TaskModel) {
						TaskNode taskNode = ((TaskModel)absModel).getTaskNode();
						String type = taskNode.getType() == null ? "" : taskNode.getType();
//						transition.addAttribute("to", ((TaskModel)absModel).getTaskNode().getName() + "-" + type);
						transition.addAttribute("to", ((TaskModel)absModel).getTaskNode().getName());
					} else {
//						transition.addAttribute("to", ((EndModel)absModel).getEndNode().getName() + "-");
						transition.addAttribute("to", ((EndModel)absModel).getEndNode().getName());
					}
				} else {
					transition.addAttribute("to", "");
				}
				
				transition.addAttribute("name", startModel.getStartNode().getConnectLine().getName());
			} else if(nodeModel instanceof TaskModel) {
				TaskModel taskModel = (TaskModel)nodeModel;
				Element taskNode = processDefinition.addElement("task-node");
				String type = taskModel.getTaskNode().getType() == null ? "" : taskModel.getTaskNode().getType();
//				taskNode.addAttribute("name", taskModel.getTaskNode().getName() + "-" + type);
				taskNode.addAttribute("name", taskModel.getTaskNode().getName());
				
				Element description = taskNode.addElement("description");
				description.addText(taskModel.getTaskNode().getDescription());
				
				//taskList模式
//				List<Task> tasks = taskModel.getTaskNode().getTaskList();
//				for (Task t : tasks) {
//					Element task = taskNode.addElement("task");
//					task.addAttribute("name", t.getName());
//					Element taskDesc = task.addElement("description");
//					taskDesc.addText(t.getDescription());
//					Element assignment = task.addElement("assignment");
//					assignment.addAttribute("class", t.getAssignmentClass());
//				}
				//单个task模式
				Element task = taskNode.addElement("task");
				task.addAttribute("name", taskModel.getTaskNode().getTask().getName());
				Element taskDesc = task.addElement("description");
				taskDesc.addText(taskModel.getTaskNode().getTask().getDescription());
				Element assignment = task.addElement("assignment");
				assignment.addAttribute("class", taskModel.getTaskNode().getTask().getAssignmentClass());
				
				Element transition = taskNode.addElement("transition");
				transition.addAttribute("to", taskModel.getTaskNode().getDecisionNode().getName());
				
				buildDecision(processDefinition,taskModel);				//组装decision结点
			} else if(nodeModel instanceof EndModel) {
				EndModel endModel = (EndModel)nodeModel;
				Element endState = processDefinition.addElement("end-state");
//				endState.addAttribute("name", endModel.getEndNode().getName() + "-");
				endState.addAttribute("name", endModel.getEndNode().getName());
				Element description = endState.addElement("description");
				description.addText(endModel.getEndNode().getDescription());
			}
		}
		return processDefinition;
	}
	
	/**
	 * 组装decision结点
	 * 修改日期：2012-3-28
	 * @author: tpf
	 */
	private void buildDecision(Element processDefinition, TaskModel taskModel) {
		DecisionNode decisionNode = taskModel.getTaskNode().getDecisionNode();
		Element decision = processDefinition.addElement("decision");
		decision.addAttribute("name", decisionNode.getName());
		Element description = decision.addElement("description");
		description.addText(decisionNode.getDescription());
//		description.addText();
		Element handler = decision.addElement("handler");
		handler.addAttribute("class", decisionNode.getHandlerClass().trim());
		
		List<AbstractLineModel> lineList = taskModel.getSourceConnections();
		
		for (AbstractLineModel line : lineList) {
			CommonNode commonNode = ((LineNode)line.getDataObject()).getCommonNode();
			
			Element transition = decision.addElement("transition");
			transition.addAttribute("to", commonNode.getName());	// 按照需求，to和name都是对应自动节点的ID值（也就是name）
			transition.addAttribute("name", commonNode.getName());
			Element transDesc = transition.addElement("description");
			transDesc.addText(commonNode.getDescription());
			// 为了获取最新的Action描述
//			String newActionDesc = WorkFlowTreeViewPart.getDescFromActionId(commonNode.getName());
//			transDesc.addText(newActionDesc == null ? "" : newActionDesc);
		}
		buildNode(processDefinition, taskModel);
	}
	
	/**
	 * 组装node结点
	 * 修改日期：2012-3-28
	 * @author: tpf
	 */
	private void buildNode(Element processDefinition, TaskModel taskModel) {
//		List<CommonNode> nodes = decisionNode.getCommonNodes();
//		for (CommonNode commonNode : nodes) {
//			Element node = processDefinition.addElement("node");
//			node.addAttribute("name", commonNode.getName());
//			Element action = node.addElement("action");
//			action.addAttribute("class", commonNode.getActionClass());
//			Element description = node.addElement("description");
////			description.addText(commonNode.getDescription());
//			// 为了获取最新的Action描述
//			description.addText(WorkFlowTreeViewPart.getDescFromActionId(commonNode.getName()));
//			Element transition = node.addElement("transition");
//			transition.addAttribute("to", commonNode.getConnectLine().getTargetNodeName());
//		}
		
		List<AbstractLineModel> lineList = taskModel.getSourceConnections();
		
		for (AbstractLineModel line : lineList) {
			CommonNode commonNode = ((LineNode)line.getDataObject()).getCommonNode();
			
			Element node = processDefinition.addElement("node");
			node.addAttribute("name", commonNode.getName());
			Element action = node.addElement("action");
			action.addAttribute("class", commonNode.getActionClass());
			Element description = node.addElement("description");
			description.addText(commonNode.getDescription());
			// 为了获取最新的Action描述
//			description.addText(WorkFlowTreeViewPart.getDescFromActionId(commonNode.getName()));
			Element transition = node.addElement("transition");
//			transition.addAttribute("to", commonNode.getConnectLine().getTargetNodeName());
			
			DataObjectInterface obj = line.getTarget().getDataObject();
			if(obj instanceof TaskNode) {
				String type = ((TaskNode)obj).getType() == null ? "" : ((TaskNode)obj).getType();
//				transition.addAttribute("to", ((TaskNode)obj).getName() + "-" + type);
				transition.addAttribute("to", ((TaskNode)obj).getName());
				
			} else if(obj instanceof EndNode) {
				transition.addAttribute("to", ((EndNode)obj).getName());
			}
		}
	}
	
}
