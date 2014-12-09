/* 文件名：     KeyWords.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-11-20
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model.db;

import org.eclipse.swt.graphics.Color;

/**
 * 
 * @author  Agree
 * @version 1.0, 2012-11-20
 * @see 
 * @since 1.0
 */
public class KeyWords {
	private String keyWords;
	private int keyWordsStart;
	private Color keyWordsColor;
	public String getKeyWords() {
		return keyWords;
	}
	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}
	public int getKeyWordsStart() {
		return keyWordsStart;
	}
	public void setKeyWordsStart(int keyWordsStart) {
		this.keyWordsStart = keyWordsStart;
	}
	public Color getKeyWordsColor() {
		return keyWordsColor;
	}
	public void setKeyWordsColor(Color keyWordsColor) {
		this.keyWordsColor = keyWordsColor;
	}
}
