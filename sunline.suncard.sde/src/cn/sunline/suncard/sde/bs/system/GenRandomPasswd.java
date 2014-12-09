/*
 * 文件名：GenRandomPasswd.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：随机生成密码
 * 修改人：heyong
 * 修改时间：2011-10-8
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.system;

/**
 * 随机生成密码 此类用于随机生成密码，可指定生成密码的长度或形式
 * 根据数字和字母的ASCII码来进行生成，0-9的ASCII码为48-57、A-Z的ASCII码为65-90、a-z的ASCII码为97-122
 * @author heyong
 * @version 1.0, 2011-10-8
 * @see
 * @since 1.0
 */
public class GenRandomPasswd {
	/**
	 * 生成的密码为数字形式
	 */
	public static final int INT_WORD = 1;
	/**
	 * 生成的密码为字母形式
	 */
	public static final int STRING_WORD = 2;
	/**
	 * 生成的密码为数字与字母的混合形式
	 */
	public static final int MIX_WORD = 3;

	public static String getPassWord(int style, int length) {
		if (style == INT_WORD) {
			return getIntRandom(length);
		} else if (style == STRING_WORD) {
			return getCharRandom(length);
		} else if (style == MIX_WORD) {
			return getMixRandom(length);
		}

		return getMixRandom(length);
	}

	/**
	 * 生成只包含数字的密码
	 * @param length 生成的密码长度
	 * @return String 返回生成的密码
	 */
	private static String getIntRandom(int length) {
		int[] array = new int[length];
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < length; i++) {
			array[i] = (int) (Math.random() * 10);
			str.append(array[i]);
		}
		return str.toString();
	}

	/**
	 * 生成只包含字母的密码
	 * @param length 生成的密码长度
	 * @return String 返回生成的密码
	 */
	private static String getCharRandom(int length) {
		int[] array = new int[length];
		char[] chars = new char[length];
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < length; i++) {
			while (true) {
				array[i] = (int) (Math.random() * 1000);
				if ((array[i] > 64 && array[i] < 91)
						|| (array[i] > 96 && array[i] < 123))
					break;
			}
			chars[i] = (char) array[i];
			str.append(chars[i]);
		}
		return str.toString();
	}

	/**
	 * 生成包含数字和字母的密码
	 * @param length 生成的密码长度
	 * @return String 返回生成的密码
	 */
	private static String getMixRandom(int length) {
		int[] array = new int[length];
		char[] chars = new char[length];
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < length; i++) {
			while (true) {
				array[i] = (int) (Math.random() * 1000);
				if ((array[i] > 47 && array[i] < 58) || (array[i] > 64 && array[i] < 91)
						|| (array[i] > 96 && array[i] < 123))
					break;
			}
			chars[i] = (char) array[i];
			str.append(chars[i]);
		}
		return str.toString();
	}

	/**
	 * 测试随机生成密码类 分别将style赋值为INT_WORD,STRING_WORD,MIX_WORD，测试其生成的密码
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(GenRandomPasswd.getPassWord(
				GenRandomPasswd.INT_WORD, 6));
		System.out.println(GenRandomPasswd.getPassWord(
				GenRandomPasswd.STRING_WORD, 6));
		System.out.println(GenRandomPasswd.getPassWord(
				GenRandomPasswd.MIX_WORD, 6));
	}
}
