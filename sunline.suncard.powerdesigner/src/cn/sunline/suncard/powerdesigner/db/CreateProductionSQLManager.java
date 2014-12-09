/* 文件名：     CreateProductionSQLManager.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-11-30
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.db;

import java.util.ArrayList;
import java.util.List;

import cn.sunline.suncard.powerdesigner.manager.ProductSpaceManager;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.ProductSpaceModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;

/**
 * 
 * @author Agree
 * @version 1.0, 2012-11-30
 * @see
 * @since 1.0
 */
public class CreateProductionSQLManager {

	/**
	 * 得到单表的SQL的方法
	 * 
	 * @param childTableModel
	 */
	public String createTableSQL(TableModel childTableModel) {
		DatabaseManager databaseManager = new DatabaseManager();
		DatabaseGeneration databaseGeneration = new DatabaseGeneration();

		StringBuffer previewText = new StringBuffer();
		if (databaseGeneration.isKeyDropForegin()) {
			previewText.append(databaseManager
					.getSqlDropForeignKeyModel(childTableModel));
		}

		if (databaseGeneration.isTableDrop()) {// 表格选项框的drop表格按钮
			previewText.append(databaseManager
					.getSqlDropTableModel(childTableModel));// 键选项的drop外键按钮
		}

		if (databaseGeneration.isTableCreate()) {// 表格选项框的create表格按钮
			previewText.append(databaseManager
					.getSqlCreateTableAndPrimaryKeyModel(childTableModel,
							databaseGeneration.isColumnDefault(),// 列选项框的默认按钮
							databaseGeneration.isKeyCreatePrimary(),// 键选项的create主键按钮
							databaseGeneration.isColumnCheck()));// 列选项框的检查按钮
		}

		if (databaseGeneration.isTableComment()) {// 表格选项框的注释按钮
			previewText.append(databaseManager
					.getSqlCommentTableModel(childTableModel));
		}

		if (databaseGeneration.isInitializeSQL()) {// 列选项框的初始化SQL按钮
			previewText.append(databaseManager
					.getSqlInitializeSQLModel(childTableModel));
		}

		if (databaseGeneration.isKeyCreateForegin()) {// 表格选项框的createFogegin按钮
			previewText.append(databaseManager
					.getSqlCreateForeignKeyModel(childTableModel));
		}

//		if (databaseGeneration.isPhysicalModelSQL()) {// 物理模型选项框的物理模型SQL按钮
//			previewText.append(databaseManager
//					.getProductModelSql(childTableModel));
//		}
//		if (databaseGeneration.isStoredProcedure()) {// 物理模型选项框的存储过程按钮
//			previewText.append(databaseManager
//					.getStoredProcedureFromProduct(childTableModel));
//		}

		return previewText.toString();
	}

	/**
	 * 根据传入的表的集合创造SQL语句
	 */
	public String createTableSQLs(List<TableModel> listTableModel) {
		// TODO Auto-generated method stub
		DatabaseManager databaseManager = new DatabaseManager();

		StringBuffer text = new StringBuffer();

		DatabaseGeneration databaseGeneration  = new DatabaseGeneration();
		
		for (TableModel selectedTableModel : listTableModel) {
			
			if (databaseGeneration.isKeyDropForegin()) {
				text.append(databaseManager
						.getSqlDropForeignKeyModel(selectedTableModel));
			}
		}
		
		for (TableModel selectedTableModel : listTableModel) {
			if (databaseGeneration.isTableDrop()) {// 表格选项框的drop表格按钮
				text.append(databaseManager
						.getSqlDropTableModel(selectedTableModel));// 键选项的drop外键按钮
			}
		}
		
		for (TableModel selectedTableModel : listTableModel) {
			if (databaseGeneration.isTableCreate()) {// 表格选项框的create表格按钮
				text.append(databaseManager
						.getSqlCreateTableAndPrimaryKeyModel(
								selectedTableModel,
								databaseGeneration.isColumnDefault(),// 列选项框的默认按钮
								databaseGeneration.isKeyCreatePrimary(),// 键选项的create主键按钮
								databaseGeneration.isColumnCheck()));// 列选项框的检查按钮
			}

			if (databaseGeneration.isTableComment()) {// 表格选项框的注释按钮
				text.append(databaseManager
						.getSqlCommentTableModel(selectedTableModel));
			}
			
			if (databaseGeneration.isInitializeSQL()) {// 列选项框的初始化SQL按钮
				text.append(databaseManager
						.getSqlInitializeSQLModel(selectedTableModel));
			}

			if (databaseGeneration.isKeyCreateForegin()) {// 表格选项框的createFogegin按钮
				text.append(databaseManager
						.getSqlCreateForeignKeyModel(selectedTableModel));
			}
			
			if(databaseGeneration.isIndexSQL()){//表格选项索引按钮
				text.append(databaseManager.getIndexSql(selectedTableModel.getIndexSqlModelSet()));
			}
		}
		
		FileModel fileModel = listTableModel.get(0).getPhysicalDiagramModel().getPackageModel()
				.getPhysicalDataModel().getFileModel();
		ProductSpaceModel productSpaceModel = new ProductSpaceModel();
		List<ProductModel> productModels = ProductSpaceManager
				.getProductModelList(fileModel);
		// 得到一个同一物理图形模型下的产品模型
		ProductModel product = new ProductModel();
				for (ProductModel productModel : productModels) {
					if (productModel.getPhysicalDataModel() == listTableModel.get(0).getPhysicalDiagramModel()
							.getPackageModel().getPhysicalDataModel()) {
						product = productModel;
						break;
					}
				}
		
		if (databaseGeneration.isPhysicalModelSQL()) {// 物理模型选项框的物理模型SQL按钮
			text.append(databaseManager.getProductModelSql(product));
		}
		
		if (databaseGeneration.isStoredProcedure()) {// 物理模型选项框的存储过程按钮
			text.append(databaseManager.getStoredProcedureFromProduct(product));
		}
		
		

		return text.toString().trim();
	}
}
