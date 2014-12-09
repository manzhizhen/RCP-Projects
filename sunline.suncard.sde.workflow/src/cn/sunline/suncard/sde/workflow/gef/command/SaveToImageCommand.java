/* 文件名：     SaveToImageCommand.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-2-21
 * 修改内容：创     建
 */
package cn.sunline.suncard.sde.workflow.gef.command;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.workflow.common.DmConstants;
import cn.sunline.suncard.sde.workflow.file.FileDeal;
import cn.sunline.suncard.sde.workflow.gef.service.SaveImageToXml;
import cn.sunline.suncard.sde.workflow.gef.service.WorkFlowCoreProcess;
import cn.sunline.suncard.sde.workflow.gef.ui.editor.WorkFlowEditor;

/**
 * 将策略保存为图片的Command
 * 
 * @author 易振强
 * @version 1.0, 2012-2-21
 * @see
 * @since 1.0
 */
public class SaveToImageCommand extends Command {
	public final static String ID = "sunline.suncard.sde.dm.commands.SaveToImageCommand";

	public  String FILE_IMAGE_EXTENSION = "*.png";
	
	private Log logger = LogManager.getLogger(SaveToImageCommand.class
			.getName());

	private WorkFlowEditor workFlowEditor;

	public SaveToImageCommand(WorkFlowEditor workFlowEditor) {
		this.workFlowEditor = workFlowEditor;
	}

	@Override
	public boolean canExecute() {
		return super.canExecute();
	}

	@Override
	public void execute() {
		logger.info("void execute() start...");
		
		String result = WorkFlowEditor.checkXML(workFlowEditor.getWorkFlowModel());
		if(result != null) {
			if(!MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), I18nUtil.getMessage("MESSAGE"), 
					result + I18nUtil.getMessage("SURE_TO_EXPORT"))) {
				return ;
			}
		}
		
		ScalableFreeformRootEditPart rootPart = workFlowEditor
				.getRootEditPart();
		IFigure figure = rootPart
				.getLayer(ScalableFreeformRootEditPart.PRINTABLE_LAYERS);
	
		SaveImageToXml saveImageToXml = new SaveImageToXml();
		saveImageToXml.setWorkFlowTreeNode(workFlowEditor.getTreeNode());
		
		byte[] data = createImage(figure, SWT.IMAGE_JPEG, saveImageToXml);
		try {
			FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
					SWT.SAVE);
			fileDialog.setFilterExtensions(new String[] {"*.xml" });
			fileDialog.setText(I18nUtil.getMessage("ACTION_EXPORT"));
			
			String filePath = fileDialog.open();
			if(filePath == null) {
				logger.info("用户取消了导出的操作！");
				return ;
			}
			
			String fileName = fileDialog.getFileName();
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
			
			
			// 写图片
			FileOutputStream fos = new FileOutputStream(new File(new File(filePath).getParent(), fileName + ".jpg"));
			fos.write(data);
			fos.close();
			
			// 写图片XML文件
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(new File(new File(filePath).getParent(), 
					fileName + I18nUtil.getMessage("PIC_XML") + ".xml")), "UTF-8");
			out.write(saveImageToXml.getPointXmlFormModel());
			out.close();
			
			// 写XML文件
			String xml = new WorkFlowCoreProcess().saveGef2Xml(workFlowEditor.getWorkFlowModel(), workFlowEditor.getTreeNode().getName());
			OutputStreamWriter xmlOut = new OutputStreamWriter(new FileOutputStream(new File(new File(filePath).getParent(), 
					fileName + ".xml")), "UTF-8");
			xmlOut.write(xml.replace("\'", "\""));
			xmlOut.close();
			
			// 导出工作目录下的文件，以便日后导入
			String id = workFlowEditor.getTreeNode().getId();
			FileDeal.copyFileToPath(new File(DmConstants.WORK_FLOW_DATA_FILE_PATH, id + ".xml").getAbsolutePath(), 
					new File(new File(filePath).getParent(), workFlowEditor.getTreeNode().getName() + "_All.xml").getAbsolutePath());
			
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), I18nUtil.getMessage("MESSAGE"), 
					I18nUtil.getMessage("EXPORT_WORKFLOW_SUCCESS"));
			
		} catch (IOException e) {
			logger.info("保存图片失败：" + e.getMessage());
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
					I18nUtil.getMessage("MESSAGE"), I18nUtil.getMessage("SAVE_IMAGE_FAIL"));
			e.printStackTrace();
		}
	}

	public static byte[] createImage(IFigure figure, int format, SaveImageToXml saveImageToXml) {
		Rectangle r = figure.getBounds();
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		Image image = null;
		GC gc = null;
		Graphics graphics = null;
		try {
			image = new Image(null, r.width, r.height);
			saveImageToXml.setImageX(r.width);
			saveImageToXml.setImageY(r.height);
			
			gc = new GC(image);
			graphics = new SWTGraphics(gc);
			graphics.translate(r.x * -1, r.y * -1);
			figure.paint(graphics);
			ImageLoader imageLoader = new ImageLoader();
			imageLoader.data = new ImageData[] { image.getImageData() };
			imageLoader.save(result, format);
			
		} finally {
			if (graphics != null) {
				graphics.dispose();
			}
			if (gc != null) {
				gc.dispose();
			}
			if (image != null) {
				image.dispose();
			}
		}
		
		return result.toByteArray();
	}

}
