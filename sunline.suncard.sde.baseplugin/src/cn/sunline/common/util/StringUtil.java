package cn.sunline.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理工具
 * <p>
 * 常用的字符串处理方法。
 * </p>
 * 
 * @version 1.0
 * @author liangbl@sunline.cn
 */
public class StringUtil {
	/** Constant for the list delimiter as char. */
	static final char LIST_ESC_CHAR = '\\';

	/**
	 * 将对象转换为字符串. 若对象为NULL则返回缺省字符串. 相当于oracle的Nvl函数
	 * 
	 * @param obj
	 *            待转换对象
	 * @param defaultValue
	 *            缺省值
	 * @return 对象的字符串内容(调用toString()方法)
	 */
	public static String nullable(Object obj, String defaultValue) {
		return StringUtil.isEmpty(obj) ? defaultValue : obj.toString();
	}

	/**
	 * 将对象转换为字符串. 若对象为NULL则返回空(0长度)字符串
	 * 
	 * @param obj
	 *            待转换对象
	 * @return 对象的字符串内容(调用toString()方法)
	 */
	public static String nullable(Object obj) {
		return nullable(obj, "");
	}

	/**
	 * 判断对象是否为"空白"(NULL或仅包含空格字符).
	 * 
	 * @param obj
	 *            对象
	 * @return <ul>
	 *         <li><tt>true</tt> 如果对象为null.
	 *         <li><tt>true</tt> 如果对象是"空白"(长度为0或仅包含空格字符).
	 *         <li><tt>true</tt> 如果对象是数组或集合，但个数为0.
	 *         <li><tt>true</tt> 如果对象是数组或集合，且其中的每个元素都是"空白".
	 *         <li><tt>true</tt> 如果对象是Map，返回Map.isEmpty().
	 *         <li><tt>false</tt> 对象非字符串或字符串数组或集合.
	 *         <li><tt>false</tt> 其它情况.
	 *         </ul>
	 * @see #isEmpty(Object)
	 */
	public static boolean isBlank(Object obj) {
		return isEmptyOrBlank(obj, true);
	}

	/**
	 * 判断给定对象是否为"空"(NULL或空(0长度)字符串).
	 * 
	 * @param obj
	 *            对象
	 * @return <ul>
	 *         <li><tt>true</tt> 如果对象为null.
	 *         <li><tt>true</tt> 如果对象是空(0长度)字符串.
	 *         <li><tt>true</tt> 如果对象是数组或集合，但个数为0.
	 *         <li><tt>true</tt> 如果对象是数组或集合，且其中的每个元素都是"空".
	 *         <li><tt>true</tt> 如果对象是Map，返回Map.isEmpty().
	 *         <li><tt>false</tt> 对象非字符串或字符串数组或集合.
	 *         <li><tt>false</tt> 其它情况.
	 *         </ul>
	 */
	public static boolean isEmpty(Object obj) {
		return isEmptyOrBlank(obj, false);
	}

	/**
	 * 判断给定对象是否为"非空".
	 * 
	 * @see #isEmpty(Object)
	 * @param obj
	 *            对象
	 * @return
	 */
	public static boolean isNotEmpty(Object obj) {
		return !isEmptyOrBlank(obj, false);
	}

	/**
	 * 判断给定对象是否为"空"或"空白".
	 * 
	 * @param obj
	 *            对象
	 * @param trim
	 *            是否截除字符串后空格
	 * @return <ul>
	 *         <li><tt>true</tt> 如果对象为null.
	 *         <li><tt>true</tt> 如果对象是空(0长度)字符串.
	 *         <li><tt>true</tt> 如果对象是数组或集合，但个数为0.
	 *         <li><tt>true</tt> 如果对象是数组或集合，且其中的每个元素都是"空"或"空白".
	 *         <li><tt>true</tt> 如果对象是Map，返回Map.isEmpty().
	 *         <li><tt>false</tt> 其它情况.
	 *         </ul>
	 */
	private static boolean isEmptyOrBlank(Object obj, boolean trim) {
		if (obj == null)
			return true;
		if (obj instanceof String) {
			String ss = (String) obj;
			return (trim ? ss.trim() : ss).length() == 0;
		}
		if (obj instanceof Object[]) {
			Object[] oo = (Object[]) obj;
			for (int i = 0; i < oo.length; i++)
				if (!isEmptyOrBlank(oo[i], trim))
					return false;
			return true;
		}
		if (obj instanceof Collection) {
			Collection oo = (Collection) obj;
			for (Iterator i = oo.iterator(); i.hasNext();)
				if (!isEmptyOrBlank(i.next(), trim))
					return false;
			return true;
		}
		if (obj instanceof Map) {
			return ((Map) obj).isEmpty();
		}
		return false;
	}

	/**
	 * 分割多行字符串到字符串数组.
	 * <p>
	 * 即分融符为：换行，或回车加换行.
	 * 
	 * @param str
	 *            待分割字符串
	 * @return an array of lines that comprise the string, or null if the string specified was null
	 */
	public static String[] splitIntoLines(String str) {
		if (str == null)
			return null;
		BufferedReader br = new BufferedReader(new StringReader(str));

		ArrayList linesList = new ArrayList();

		try {
			String line = br.readLine();
			while (line != null) {
				linesList.add(line);
				line = br.readLine();
			}
		} catch (IOException notGoingToHappenWithAnInMemoryStringReaderEx) {
		}

		return (String[]) linesList.toArray(new String[linesList.size()]);
	}

	/**
	 * 分割字符串(用单个字符). 不使用正则表达式，提高性能。
	 * 
	 * @param s
	 *            待分割字符串
	 * @param delimiter
	 *            分融符
	 * @param trim
	 *            是否截去前后空格
	 * @return List
	 *         <ul>
	 *         <li><tt>空列表</tt> 如果对象为null.
	 *         <li><tt>含一个空字符串的列表</tt> 如果对象是空字符串.
	 *         </ul>
	 */
	public static List split(String s, char delimiter, boolean trim) {
		List ret = new ArrayList();
		if (s == null) {
			return ret;
		}
		StringBuffer token = new StringBuffer();
		int begin = 0;
		while (begin < s.length()) {
			char c = s.charAt(begin);
			if (c == delimiter) {
				// found a list delimiter -> add token and reset buffer
				String t = token.toString();
				if (trim) {
					t = t.trim();
				}
				ret.add(t);
				token = new StringBuffer();
			} else {
				token.append(c);
			}
			begin++;
		}
		// Add last token
		String t = token.toString();
		if (trim) {
			t = t.trim();
		}
		ret.add(t);
		return ret;
	}

	/**
	 * 分割字符串(用单个字符)并截去前后空格. 不使用正则表达式，提高性能。 相当于<code>split(s, delimiter, true)</code>的快捷方式.
	 * <table border="1">
	 * <tr>
	 * <td><strong>源串</strong></td>
	 * <td><strong>结果</strong></td>
	 * </tr>
	 * <tr>
	 * <td>a,&nbsp;,&nbsp;&nbsp;,,&nbsp;b</td>
	 * <td>[a,,,,b]</td>
	 * </tr>
	 * <tr>
	 * <td>a,b&nbsp;&nbsp;&nbsp;c&nbsp;,d</td>
	 * <td>[a,b&nbsp;&nbsp;&nbsp;c,d]</td>
	 * </tr>
	 * </table>
	 * 
	 * @param s
	 *            待分割字符串
	 * @param delimiter
	 *            分融符
	 * @param trim
	 *            是否截去前后空格
	 * @return List
	 *         <ul>
	 *         <li><tt>空列表</tt> 如果对象为null.
	 *         <li><tt>含一个空字符串的列表</tt> 如果对象是空字符串或全空格字符串.
	 *         </ul>
	 */
	public static List split(String s, char delimiter) {
		return split(s, delimiter, true);
	}

	/**
	 * 按空白字符(空格)或逗号分隔字符串. 本方法的返回结果中不包括零长字符串，示例如下：
	 * <table border="1">
	 * <tr>
	 * <td><strong>源串</strong></td>
	 * <td><strong>结果</strong></td>
	 * </tr>
	 * <tr>
	 * <td>a,,,,b</td>
	 * <td>[a, b]</td>
	 * </tr>
	 * <tr>
	 * <td>a,b&nbsp;&nbsp;&nbsp;c,d</td>
	 * <td>[a, b, c, d]</td>
	 * </tr>
	 * </table>
	 * <b>使用了正则表达式，注意性能问题，不能在调用频繁度的地方使用</b>
	 * 
	 * @param input
	 *            源串
	 * @return 字符串列表. <tt>null</tt> 如果源串为null
	 */
	public static List split(String input) {
		return split(input, "[\\s,]+");
	}

	/**
	 * 使用指定的正则表达式分隔字符串. 本方法的返回结果中不包括零长字符串（这是与String.split方法的不同之处）
	 * 
	 * @param input
	 *            源串
	 * @param sep
	 *            正则表达式
	 * @return 字符串列表. <tt>null</tt> 如果源串为null
	 */
	public static List split(String input, String sep) {
		if (input == null)
			return null;
		int index = 0;
		List matchList = new ArrayList();
		Matcher m = Pattern.compile(sep).matcher(input);

		// Add segments before each match found
		while (m.find()) {
			if (index < m.start()) {
				String match = input.subSequence(index, m.start()).toString();
				matchList.add(match);
			}
			index = m.end();
		}

		if (index < input.length())
			matchList.add(input.subSequence(index, input.length()).toString());

		return matchList;
	}

	/**
	 * 将源字符串中所有匹配的字符串替换为提供的新字符串(不使用正则表达式).
	 * 
	 * @param input
	 *            源字符串
	 * @param matchString
	 *            匹配字符串
	 * @param newString
	 *            新字符串
	 * @return 替换后的新字符串
	 */
	public static final String replace(String input, String matchString, String newString) {
		int i = 0;
		if ((i = input.indexOf(matchString, i)) >= 0) {
			char[] line2 = input.toCharArray();
			char[] newString2 = newString.toCharArray();
			int oLength = matchString.length();
			StringBuffer buf = new StringBuffer(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;
			int j = i;
			while ((i = input.indexOf(matchString, i)) > 0) {
				buf.append(line2, j, i - j).append(newString2);
				i += oLength;
				j = i;
			}
			buf.append(line2, j, line2.length - j);
			return buf.toString();
		}
		return input;
	}

	/**
	 * 编码HTML标记.
	 * <p>
	 * 将字符串中的HTML关键字符用HTML标记替换 .
	 * <ul>
	 * <li>&amp; &nbsp; &amp;amp;
	 * <li>&lt; &nbsp; &amp;lt;
	 * <li>&gt; &nbsp; &amp;gt;
	 * <li>" &nbsp; &amp;quot;
	 * <li>' &nbsp; &amp;apos;
	 * <li>\ &nbsp; \\
	 * <li>换行 &nbsp; \r
	 * <li>回车 &nbsp; \n
	 * <li>\ &nbsp; \\
	 * </ul>
	 * 
	 * @param input
	 *            待编码字符串.
	 * @return 编码后的字符串.
	 */
	public static final String escapeHTMLTags(String input) {
		// Check if the string is null or zero length -- if so, return
		// what was sent in.
		if (input == null || input.length() == 0) {
			return input;
		}
		// Use a StringBuffer in lieu of String concatenation -- it is
		// much more efficient this way.
		StringBuffer buf = new StringBuffer(input.length());
		char ch = ' ';
		for (int i = 0; i < input.length(); i++) {
			ch = input.charAt(i);
			if (ch == '<') {
				buf.append("&lt;");
			} else if (ch == '>') {
				buf.append("&gt;");
			} else if (ch == '"') {
				buf.append("&quot;");
			} else if (ch == '\'') {
				buf.append("&apos;");
			} else if (ch == '&') {
				buf.append("&amp;");
			} else if (ch == '\\') {
				buf.append('\\');
				buf.append(ch);
			} else if (ch == '\r') {
				buf.append("\\r");
			} else if (ch == '\n') {
				buf.append("\\n");
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	/**
	 * 编码HTML标记为浏览器可显示格式.
	 * <p>
	 * 将字符串中的HTML关键字符用HTML标记替换 .将换行符转为&lt;br&gt;
	 * <ul>
	 * <li>&amp; &nbsp; &amp;amp;
	 * <li>&lt; &nbsp; &amp;lt;
	 * <li>&gt; &nbsp; &amp;gt;
	 * <li>" &nbsp; &amp;quot;
	 * <li>' &nbsp; &amp;apos;
	 * <li>换行 &nbsp; &lt;br&gt;
	 * <li>回车 &nbsp;
	 * </ul>
	 * 
	 * @see StringUtil#escapeHTMLTags(String).
	 * @param input
	 *            待编码字符串.
	 * @return 编码后的字符串.
	 */
	public static final String viewHTMLTags(String input) {
		// Check if the string is null or zero length -- if so, return
		// what was sent in.
		if (input == null || input.length() == 0) {
			return input;
		}
		// Use a StringBuffer in lieu of String concatenation -- it is
		// much more efficient this way.
		StringBuffer buf = new StringBuffer(input.length());
		char ch = ' ';
		for (int i = 0; i < input.length(); i++) {
			ch = input.charAt(i);
			if (ch == '<') {
				buf.append("&lt;");
			} else if (ch == '>') {
				buf.append("&gt;");
			} else if (ch == '"') {
				buf.append("&quot;");
			} else if (ch == '\'') {
				buf.append("&apos;");
			} else if (ch == '&') {
				buf.append("&amp;");
			} else if (ch == '\\') {
				// buf.append('\\');
				buf.append(ch);
			} else if (ch == '\r') {
				buf.append("<br>");
			} else if (ch == '\n') {
				// buf.append("\\n");
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	/**
	 * Used by the hash method.
	 */
	private static MessageDigest digest = null;

	/**
	 * 用MD5算法加密字符串.
	 * <p>
	 * Hashes a String using the Md5 algorithm and returns the result as a String of hexadecimal numbers. This method is synchronized to avoid
	 * excessive MessageDigest object creation. If calling this method becomes a bottleneck in your code, you may wish to maintain a pool of
	 * MessageDigest objects instead of using this method.
	 * <p>
	 * A hash is a one-way function -- that is, given an input, an output is easily computed. However, given the output, the input is almost
	 * impossible to compute. This is useful for passwords since we can store the hash and a hacker will then have a very hard time determining the
	 * original password.
	 * <p>
	 * In Jive, every time a user logs in, we simply take their plain text password, compute the hash, and compare the generated hash to the stored
	 * hash. Since it is almost impossible that two passwords will generate the same hash, we know if the user gave us the correct password or not.
	 * The only negative to this system is that password recovery is basically impossible. Therefore, a reset password method is used instead.
	 * 
	 * @param data
	 *            the String to compute the hash of.
	 * @return a hashed version of the passed-in String
	 */
	public synchronized static final String hash(String data) {
		if (digest == null) {
			try {
				digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException nsae) {
				System.err.println("Failed to load the MD5 MessageDigest. " + "SCU will be unable to function normally.");
				nsae.printStackTrace();
			}
		}
		// Now, compute hash.
		digest.update(data.getBytes());
		return toHex(digest.digest());
	}

	/**
	 * 将字节转换为16进制字符串显示. Turns an array of bytes into a String representing each byte as an unsigned hex number.
	 * 
	 * @param hash
	 *            待转换字节数组
	 * @return 16进制表示的字符串
	 */
	public static final String toHex(byte hash[]) {
		StringBuffer buf = new StringBuffer(hash.length * 2);
		String stmp = "";

		for (int i = 0; i < hash.length; i++) {
			stmp = (java.lang.Integer.toHexString(hash[i] & 0XFF));
			if (stmp.length() == 1) {
				buf.append(0).append(stmp);
			} else {
				buf.append(stmp);
			}
		}
		return buf.toString();
	}

	/**
	 * 将16进制表示的字符串转换为字节数组.
	 * 
	 * @param hex
	 *            待转换16进制表示的字符串
	 * @return 字节数组
	 */
	public static final byte[] hexToBytes(String hex) {
		if (null == hex) {
			return new byte[0];
		}
		int len = hex.length();
		byte[] bytes = new byte[len / 2];
		String stmp = null;
		try {
			for (int i = 0; i < bytes.length; i++) {
				stmp = hex.substring(i * 2, i * 2 + 2);
				bytes[i] = (byte) Integer.parseInt(stmp, 16);
			}
		} catch (Exception e) {
			return new byte[0];
		}

		return bytes;
	}

	/**
	 * 编码字符串内容以便放到XML文档对象中.
	 * 
	 * @param input
	 *            待编码字符串
	 * @return 编码后的字符串.
	 */
	public static final String escapeForXML(String input) {
		// Check if the string is null or zero length -- if so, return
		// what was sent in.
		if (input == null || input.length() == 0) {
			return input;
		}
		char[] sArray = input.toCharArray();
		StringBuffer buf = new StringBuffer(sArray.length);
		char ch;
		for (int i = 0; i < sArray.length; i++) {
			ch = sArray[i];
			if (ch == '<') {
				buf.append("&lt;");
			} else if (ch == '>') {
				buf.append("&gt;");
			} else if (ch == '"') {
				buf.append("&quot;");
			} else if (ch == '&') {
				buf.append("&amp;");
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	/**
	 * 反编码XML内容字符串. 以得到编码前的内容. Unescapes the String by converting XML escape sequences back into normal characters.
	 * 
	 * @see StringUtil#escapeForXML(String)
	 * @param input
	 *            待反编码字符串
	 * @return 反编码后的字符串.
	 */
	public static final String unescapeFromXML(String input) {
		input = replace(input, "&lt;", "<");
		input = replace(input, "&gt;", ">");
		input = replace(input, "&quot;", "\"");
		return replace(input, "&amp;", "&");
	}

	/**
	 * 转换为紧凑格式的字节数表示
	 * 
	 * @param number
	 *            字节数(单位:B)
	 * @return
	 */
	public static final String compactSizeFormat(String number) {
		String[] end = new String[] { "B", "kB", "MB", "GB" };
		double num = 0;
		int i = 0;
		try {
			num = (double) Integer.parseInt(number);
		} catch (Exception e) {
			num = 0;
		}

		while (num > 1024 && i < end.length) {
			num /= 1024;
			i++;
		}
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);

		return nf.format(num) + " " + end[i];
	}

	/**
	 * 将带字符串按Byte位长度取子字符串. 防止带汉字的字符串长度取错 防止出现Exception
	 * 
	 * @param String
	 *            src 源字符串
	 * @param int beginIndex 起始位置,0为第一位
	 * @param int endIndex 终止位置,1为第一位
	 * @return String
	 * 
	 * @auth LBL
	 * @since 2002.11.27
	 */
	public static String substr(String src, int beginIndex, int endIndex) {
		String dest = "";
		if (src == null) {
			return dest;
		}

		byte[] srcByte = src.getBytes();
		byte[] destByte = null;
		int srclen = srcByte.length;
		if (srclen <= beginIndex || beginIndex >= endIndex) {
			return "";
		}

		if (srclen >= endIndex) {
			destByte = new byte[endIndex - beginIndex];
			System.arraycopy(srcByte, beginIndex, destByte, 0, endIndex - beginIndex);
			dest = new String(destByte);
			return dest;
		} else {
			destByte = new byte[srclen - beginIndex];
			System.arraycopy(srcByte, beginIndex, destByte, 0, srclen - beginIndex);
			dest = new String(destByte);
			return dest;
		}
	}

	/**
	 * 处理汉字字串的substr. 将带字符串按Byte位长度取子字符串 防止带汉字的字符串长度取错 防止出现Exception 防止出现半个汉字
	 * 
	 * @param String
	 *            src 源字符串
	 * @param int beginIndex 起始位置,0为第一位
	 * @param int endIndex 终止位置,1为第一位
	 * @param boolean ifAdd ==true 如果最后是半个汉字，返回长度加一 ==false 如果最后是半个汉字，返回长度减一
	 * 
	 * @return String
	 * 
	 * @auth LBL
	 * @since 2002.11.27
	 */
	public static String gbsubstr(String src, int beginIndex, int endIndex, boolean ifAdd) {
		String dest = "";
		dest = substr(src, beginIndex, endIndex);
		if (dest.length() == 0) {
			if (ifAdd) {
				dest = substr(src, beginIndex, endIndex + 1);
			} else {
				dest = substr(src, beginIndex, endIndex - 1);
			}
		}
		return dest;
	}

	/**
	 * 处理汉字字串的substr 将带字符串按Byte位长度取子字符串 防止带汉字的字符串长度取错 防止出现Exception 防止出现半个汉字
	 * 
	 * @param String
	 *            src 源字符串
	 * @param int beginIndex 起始位置,0为第一位
	 * @param int endIndex 终止位置,1为第一位 如果最后是半个汉字，返回长度减一
	 * 
	 * @return String
	 * 
	 * @auth LBL
	 * @since 2002.11.27
	 */
	public static String gbsubstr(String src, int beginIndex, int endIndex) {
		return gbsubstr(src, beginIndex, endIndex, false);
	}

	/**
	 * 取带汉字字串的length 将带字符串按Byte位长度取子字符串 防止带汉字的字符串长度取错
	 * 
	 * @param String
	 *            str 源字符串
	 * 
	 * @return int Byte位长度
	 * 
	 * @auth LBL
	 * @since 2002.11.27
	 */
	public static int gbStrLen(String str) {
		if (str == null) {
			return 0;
		}
		byte[] strByte;
		try {
			strByte = str.getBytes("GB18030");
		} catch (UnsupportedEncodingException e) {
			strByte = str.getBytes();
		}
		return strByte.length;
	}

	/** 产生重复字符串 */
	public static String replicateStr(char ch, int len) {
		String tmpstr = null;
		char[] tmparr = null;

		if (len <= 0) {
			return "";
		}

		tmparr = new char[len];
		for (int i = 0; i < len; i++) {
			tmparr[i] = ch;
		}
		tmpstr = new String(tmparr);

		return tmpstr;
	}

	/**
	 * 左对齐填充定长字符串 向字符串尾部添加字符填充长度 可以有汉字
	 * */
	public static String lFillStr(String src, char ch, int len) {
		String dest = src;

		int srclen = gbStrLen(src);
		if (srclen > len) {
			dest = gbsubstr(src, 0, len);
			srclen = gbStrLen(dest);
		}

		dest += replicateStr(ch, len - srclen);

		return dest;
	}

	/**
	 * 右对齐填充定长字符串 向字符串前部添加字符 不处理汉字
	 * */
	public static String rFillStr(String src, char ch, int len) {
		return rFillStr(src, ch, len, false);
	}

	public static String rFillStr(String src, char ch, int len, boolean gb) {
		String dest = src;
		int srclen = gb ? gbStrLen(src) : src.length();
		if (srclen > len) {
			dest = gbsubstr(src, 0, len);
			srclen = gb ? gbStrLen(dest) : dest.length();
		}

		dest = replicateStr(ch, len - srclen) + dest;

		return dest;
	}

	public static String maxstr(String s, int maxlength) {
		return maxstr(s, "utf8", maxlength);
	}

	/**
	 * 截取小于指定长度部分的字符串(长度按字节计算)
	 * 
	 * @param s
	 *            源字符串
	 * @param encoding
	 *            编码
	 * @param maxlength
	 *            指定最大长度
	 * @return 字符串
	 *         <ul>
	 *         <li><tt>null</tt> 如果指定的编码不支持.
	 *         <li><tt>原值</tt> 如果小于或等于指定长度.
	 *         <li><tt>""</tt> 如果字符串为null.
	 *         <li><tt>新字符串</tt> 字节长度为指定长度，且最后三位替换为"...".
	 *         </ul>
	 */
	public static String maxstr(String s, String encoding, int maxlength) {
		if (s == null)
			return "";
		try {
			byte[] bytes = encoding == null ? s.getBytes() : s.getBytes(encoding);
			if (bytes.length <= maxlength)
				return s;
			return new String(bytes, 0, maxlength - 3, encoding) + "...";
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * 比较两个字符串是否相等
	 * 
	 * @param val1
	 * @param val2
	 * @return <ul>
	 *         <li>val1,val2都为null时，true</li>
	 *         <li>val1为null,val2不为null,false</li>
	 *         <li>其它,val1.equals(val2)</li>
	 *         </ul>
	 */
	public static boolean equals(String val1, String val2) {
		if (val1 == null) {
			if (val2 == null)
				return true;
			else
				return false;
		}
		return val1.equals(val2);
	}

	/**
	 * 获取平台缺省的字符集
	 * 
	 * @return JVM中配置使用的字符集
	 */
	public static String getDefaultCharacterEncoding() {
		// Not available on all platforms
		String charEnc = System.getProperty("file.encoding");
		if (charEnc != null)
			return charEnc;

		// JDK1.4 onwards
		charEnc = new java.io.OutputStreamWriter(new java.io.ByteArrayOutputStream()).getEncoding();

		// jdk1.5
		// charEnc = Charset.defaultCharset().name();
		return charEnc != null ? charEnc : "<unknown charset encoding>";
	}
}
