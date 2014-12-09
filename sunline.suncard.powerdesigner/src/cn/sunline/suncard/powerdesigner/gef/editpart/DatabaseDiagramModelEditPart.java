/* 文件名：     WorkFlowModelEditPart.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.editpart;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Animation;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.gef.CompoundSnapToHelper;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.gef.model.AbstractGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.DatabaseDiagramGefModel;
import cn.sunline.suncard.powerdesigner.gef.policy.PDXYLayoutEditPolicy;
import cn.sunline.suncard.powerdesigner.gef.router.MyManhattanConnectionRouter;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;



/**
 * 集合模型的控制器
 * @author  易振强
 * @version 1.0, 2012-3-26
 * @see 
 * @since 1.0
 */
public class DatabaseDiagramModelEditPart extends EditPartWithListener {

	@Override
	protected IFigure createFigure() {
		Figure figure = new FreeformLayer();
		figure.setLayoutManager(new FreeformLayout());
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new PDXYLayoutEditPolicy());
	}

	@Override
	protected List getModelChildren() {
		return ((DatabaseDiagramGefModel) getModel()).getChildren();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// 模型改变通知
		if (evt.getPropertyName().equals(DatabaseDiagramGefModel.P_CHILDREN)) {
			// 因此子模型改变，要刷新子模型的EditPart来显示其改变
			refreshChildren();
		} 
	}
	
	@Override
	protected void refreshVisuals() {
		ConnectionLayer connectionLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
//		connectionLayer.setConnectionRouter(new BendpointConnectionRouter());
		connectionLayer.setConnectionRouter(new MyManhattanConnectionRouter());
		connectionLayer.setFocusTraversable(true);
		// connectionLayer.setConnectionRouter(null);
		// if ((getViewer().getControl().getStyle() & SWT.MIRRORED ) == 0) {
		connectionLayer.setAntialias(SWT.ON);
		connectionLayer.setValid(true);
		// }

		Animation.run(400);

		super.refreshVisuals();
	}
	
	@Override
	public Object getAdapter(Class adapter) {
		// 设置网格对齐
		if (adapter == SnapToHelper.class) {
			List snapStrategies = new ArrayList();
			Boolean val = (Boolean) getViewer().getProperty(RulerProvider.
					PROPERTY_RULER_VISIBILITY);
			
			if (val != null && val.booleanValue()) {
				snapStrategies.add(new SnapToGuides(this));
			}
			
			val = (Boolean) getViewer().getProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED);
			
			if (val != null && val.booleanValue()) {
				snapStrategies.add(new SnapToGeometry(this));
			}
			
			val = (Boolean) getViewer().getProperty(SnapToGrid.PROPERTY_GRID_ENABLED);
			
			if (val != null && val.booleanValue()) {
				snapStrategies.add(new SnapToGrid(this));
			}

			if (snapStrategies.size() == 0) {
				return null;
			}
			
			if (snapStrategies.size() == 1) {
				return snapStrategies.get(0);
			}
			
			SnapToHelper ss[] = new SnapToHelper[snapStrategies.size()];
			for (int i = 0; i < snapStrategies.size(); i++) {
				ss[i] = (SnapToHelper) snapStrategies.get(i);
			}
			
			return new CompoundSnapToHelper(ss);
		}
		
		return super.getAdapter(adapter);
	}
	
	/**
	 * 刷新当前所有的DatabaseDiagramEditor
	 */
	public static void refreshEditor() {
		 IEditorReference[] editorRefs = PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				 getActivePage().getEditorReferences();
		 
		 for(IEditorReference editorReference : editorRefs) {
			 if(editorReference.getEditor(false) instanceof DatabaseDiagramEditor) {
				 DatabaseDiagramEditor databaseDiagramEditor = (DatabaseDiagramEditor) 
						 editorReference.getEditor(false);
				 
//			 databaseDiagramEditor.getDatabaseDiagramGefModel().refreshChildren();
				 List<AbstractGefModel> childList = databaseDiagramEditor.getDatabaseDiagramGefModel().
						 getChildren();
				 
				 for(AbstractGefModel abstractGefModel : childList) {
					 abstractGefModel.setDataObject(abstractGefModel.getDataObject());
				 }
			 }
			 
		 }
	}

}
