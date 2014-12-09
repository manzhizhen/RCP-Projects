///* 文件名：     DatabaseGenerationContentProvider.java
// * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
// * 描述：
// * 修改人：     Agree
// * 修改时间：2012-12-6
// * 修改内容：
// */
//package cn.sunline.suncard.powerdesigner.provider;
//
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Set;
//
//import org.eclipse.jface.viewers.ITreeContentProvider;
//import org.eclipse.jface.viewers.Viewer;
//
//import cn.sunline.suncard.powerdesigner.model.FileModel;
//import cn.sunline.suncard.powerdesigner.model.ModuleModel;
//import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
//import cn.sunline.suncard.powerdesigner.model.ProductModel;
//import cn.sunline.suncard.powerdesigner.model.ProductSpaceModel;
//import cn.sunline.suncard.powerdesigner.model.TableModel;
//
//
///**
// * 
// * @author  Agree
// * @version 1.0, 2012-12-6
// * @see 
// * @since 1.0
// */
//public class DatabaseGenerationContentProvider implements ITreeContentProvider{
//
//
//	@Override  
//    public void dispose() {  
//  
//    }  
//  
//    @Override  
//    public void inputChanged(Viewer arg0, Object arg1, Object arg2) {  
//  
//    }  
//  
//    @Override  
//    public Object[] getChildren(Object parentElement) {  
//        if (parentElement instanceof ProductModel) {  
//        	ProductModel productModel = (ProductModel) parentElement;  
////        	list = productModel.getPhysicalDataModel().getFileModel()
//            if (list == null || list.length <= 0) {  
//                return new Object[0];  
//            }  
//            return list;  
//        }  
//        return new Object[0];  
//    }  
//  
//    @Override  
//    public Object[] getElements(Object inputElement) {  
//        if (inputElement instanceof TableModel[]) {  
//            TableModel[] tableList = (TableModel[]) inputElement;  
//            if (tableList == null || tableList.length <= 0) {  
//                return new Object[0];  
//            }  
//            return tableList;  
//        }  
//        return new Object[0];  
//    }  
//  
//    @Override  
//    public Object getParent(Object arg0) {  
//    	if (arg0 instanceof PhysicalDiagramModel) {  
//    		List<ProductModel> productModelList = null;
//    		
//    		PhysicalDiagramModel physicalDiagramModel = (PhysicalDiagramModel) arg0;  
//    		FileModel fileModel = physicalDiagramModel.getPackageModel().getPhysicalDataModel().getFileModel();
//    		ProductSpaceModel productSpaceModel = new ProductSpaceModel();
//    		List<ProductModel> productModels = productSpaceModel.getProductModelList(fileModel);
//    		
//    		for(ProductModel productModel : productModels){
//    			if(productModel.getPhysicalDataModel() ==  physicalDiagramModel.getPackageModel().getPhysicalDataModel()){
//    				productModelList.add(productModel);
//    			}
//    		}
//    		 return productModelList;  
//        }  
//    	return null;
//    }  
//  
//    @Override  
//    public boolean hasChildren(Object inputElement) {  
//        if (inputElement instanceof ProductModel) {  
//        	ProductModel productModel = (ProductModel) inputElement;  
//        	Set<ModuleModel> moduleModellist =  productModel.getModuleModelSet();  
//        	for(ModuleModel model: moduleModellist){
//        		model.getTableModelSet()
//        	}
//            if (list == null || list.length <= 0) {  
//                return false;  
//            }  
//            return true;  
//        }  
//        return false;  
//    }  
//}
