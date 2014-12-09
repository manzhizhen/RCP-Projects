/* 文件名：     IndexManagerCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-12-16
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.commands.Command;

import cn.sunline.suncard.powerdesigner.model.IndexSqlModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.SqlScriptModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;

/**
 * 
 * @author  Agree
 * @version 1.0, 2012-12-16
 * @see 
 * @since 1.0
 */
public class IndexManagerCommand extends Command {
	private TableModel tableModel;
	private List<IndexSqlModel> newSqlList;
	
	public IndexManagerCommand(TableModel tableModel, List<IndexSqlModel> sqlList) {
		
		this.tableModel = tableModel;
		this.newSqlList = sqlList;
	}

	@Override
	public void execute() {
//		IndexSqlModel indexSqlModel = new IndexSqlModel();
//		indexSqlModel.setIndexSqlList(newSqlList);
		LinkedHashSet<IndexSqlModel> newSqlSet = new LinkedHashSet<IndexSqlModel>();
		newSqlSet.addAll(newSqlList);
		
		tableModel.setIndexSqlModelSet(newSqlSet);
//		tableModel.setIndexSqlModel(indexSqlModel);
		super.execute();
	}
	
	@Override
	public boolean canUndo() {
		// TODO Auto-generated method stub
		return false;
	}
}
