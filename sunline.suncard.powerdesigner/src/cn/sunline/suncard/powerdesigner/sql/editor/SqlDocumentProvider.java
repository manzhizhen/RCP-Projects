/* 文件名：     SqlDocumentProvider.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-13
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.sql.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;


/**
 * 
 * @author  Manzhizhen
 * @version 1.0, 2013-1-13
 * @see 
 * @since 1.0
 */
public class SqlDocumentProvider extends FileDocumentProvider{
	public SqlDocumentProvider() {
		super();
	}
	
	@Override
	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner =
				new FastPartitioner(
					new SqlPartitionScanner(),
					new String[] { SqlPartitionScanner.SQL_KEYWORD, SqlPartitionScanner.SQL_COMMENT });
			
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		
		return document;
	}

}
