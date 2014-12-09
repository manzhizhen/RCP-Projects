/* 文件名：     KeyWordsPropertyList.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-11-20
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.db;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import cn.sunline.suncard.powerdesigner.model.db.KeyWords;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.KeywordsConstants;

/**
 * 管理数据库生成的关键字
 * 
 * @author Agree
 * @version 1.0, 2012-11-20
 * @see
 * @since 1.0
 */
public class KeyWordsManager {
	// private static int keywordsStart = 0;//报错的原因，牢记

	/**
	 * 根据传入的字符串得到关键字变色所需要的关键字和关键字的起始点
	 * 
	 * @param str
	 * @return
	 */
	public List<KeyWords> getKeyWordsList(String str) {
		List<KeyWords> keywordsList = new ArrayList<KeyWords>();
		KeyWords keywords;
		int keywordsStart = 0;

		String[] arr = str.split(" ");

		for (String keywordsStrWrap : arr) {
			String[] arr2 = keywordsStrWrap.split(DmConstants.FILE_WRAP);
			for(String keywordsStr : arr2){
				if (isKeyWords(keywordsStr)) {
					keywords = new KeyWords();
					keywords.setKeyWords(keywordsStr);

					keywordsStart = str.indexOf(keywordsStr, keywordsStart);
					keywords.setKeyWordsStart(keywordsStart);
					keywordsStart += keywordsStr.length();
					keywords.setKeyWordsColor(Display.getCurrent().getSystemColor(
							SWT.COLOR_BLUE));
					keywordsList.add(keywords);
				}

				else if (keywordsStr.contains("'")) {
					keywords = new KeyWords();
					keywords.setKeyWords(keywordsStr);

					keywordsStart = str.indexOf(keywordsStr, keywordsStart);
					keywords.setKeyWordsStart(keywordsStart);
					keywordsStart += keywordsStr.length();
					keywords.setKeyWordsColor(Display.getCurrent().getSystemColor(
							SWT.COLOR_RED));
					keywordsList.add(keywords);
				}
			}
		}

		return keywordsList;
	}

	/**
	 * 判断传入的字符串是否是关键字
	 * 
	 * @param str
	 * @return
	 */
	private boolean isKeyWords(String str) {
		if (str.equals(KeywordsConstants.KEYWORDS_ADD)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_ALTER)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_AND)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_BETWEEN)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_CASCADE)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_ON)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_COMMENT)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_TABLE)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_CONSTRAINT)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_CREATE)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_DEFAULT)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_DROP)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_FOREIGN)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_IS)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_NOT)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_NULL)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_PRIMARY)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_REFERENCES)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_KEY)) {
			return true;
		}

		// 数据类型
		if (str.equals(KeywordsConstants.KEYWORDS_DELETE)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_RESTRICT)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_char)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_date)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_datetime)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_decimal)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_time)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_timestamp)) {
			return true;
		}
		if (str.equals(KeywordsConstants.KEYWORDS_varchar)) {
			return true;
		}

		return false;
	}

	// public static void main(String[] args) {
	// KeyWordsManager kwm = new KeyWordsManager();
	// String str = "you datetime varchar \n\n\r\n " +
	// " is varchar fdsa 我是一个测试";
	//
	// List<KeyWords> list = kwm.getKeyWordsList(str);
	// for(KeyWords key : list){
	// System.out.println(key.getKeyWords() + "   " + key.getKeyWordsStart());
	// }
	// }
}
