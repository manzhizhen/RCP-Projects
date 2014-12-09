/* 文件名：     SqlPartitionScanner.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-13
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.sql.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * Sql的分割扫描器
 * 通过给扫描器添加各种Rule就可以控制扫描器的分割行为，
 * 而每一种分割类型都会有对应的一个Rule， JFace提供了多种Rule，
 * 在创建一个rule实例时，需要指定该rule使用的token，
 * 对应的字符序列特征，在扫描的时候，当rule找到匹配的文档的时候， 
 * 将使用该rule所附带的token对匹配文字进行标识。
 * 要对文档进行分割，需要先创建一个分割扫描器，
 * 然后再将分割扫描器作为参数创建文档分割器，
 * 接着将文档的分割器设置为前面创建的文档分割器(此时将对文档进行分割操作)，
 * 最后将分割器与文档关联。
 * @author  Manzhizhen
 * @version 1.0, 2013-1-13
 * @see 
 * @since 1.0
 */
public class SqlPartitionScanner extends RuleBasedPartitionScanner{
	public final static String SQL_COMMENT = "_SQL_COMMENT";
	public final static String SQL_KEYWORD = "_SQL_KEYWORD";
	
	public SqlPartitionScanner() {
		List rules = new ArrayList();

		IToken sqlComment = new Token(SQL_COMMENT);

        /** @TODO: Change hardcoded sql comment keyword */
		rules.add(new EndOfLineRule("--", sqlComment));
		//rules.add(new EndOfLineRule("#", sqlComment));
		//rules.add(new SQLKeyword(tag));

		IPredicateRule[] result = new IPredicateRule[rules.size()];
		rules.toArray(result);
		setPredicateRules(result);
	}

}
