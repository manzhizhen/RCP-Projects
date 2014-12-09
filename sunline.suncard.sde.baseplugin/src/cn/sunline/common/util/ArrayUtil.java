/**
 * 2010-3-8
 */
package cn.sunline.common.util;

import java.lang.reflect.Array;
import java.util.Collection;


/**
 * @author jxb
 *
 */
public class ArrayUtil {
	
	/**
	 * 判断两个数组是否相等？数据相等必须满足以下条件之一：
	 * <ul>
	 * <li>数组对象同为空；
	 * <li>数组长度相等且对应位置上的元素相等；
	 * </ul>
	 */
	public static boolean arrayEquals(Object[] a1, Object[] a2) {
		return arrayEquals(a1, a2, null);
	}
	public static boolean arrayEquals(Object[] a1, Object[] a2, Object wildcardValue) {
		if (a1 == null && a2 == null) return true;
		if (a1 == null || a2 == null) return false;
		if (a1.length != a2.length) return false;
		for (int i = 0; i < a1.length; i++) {
			if (a1[i] == null && a2[i] == null) continue;
			if (a1[i] != null && a1[i].equals(wildcardValue)) continue;
			if (a1[i] == null || a2[i] == null) return false;
			if (!a1[i].equals(a2[i])) return false;
		}
		return true;
	}
	
	/**
	 * 判断对象o是否出现在对象arr中，两个参数可以为集合或数组。
	 * <p>注意，本方法在判断字符串是否相等时，会进行trim操作！
	 * @see #asArray(Object)
	 */
	public static boolean notIn(Object o, Object arr) {
		return !in(o, arr);
	}
	public static boolean in(Object o, Object arr){
		if (o == null || arr == null)
			return false;
		Object[] os = asArray(o);
		Object[] os2 = asArray(arr);
		for (int i = 0; i < os.length; i++) {
			for(int j=0;j<os2.length;j++){
				if (os[i] instanceof String && os2[j] instanceof String) {
					if (((String)os[i]).trim().equals(((String)os2[j]).trim()))
						return true;
				}
				else if(os[i] instanceof String && os2[j] instanceof Boolean) {
					if (os[i].equals(os2[j].toString())) return true;
				}
				else {
					if (os[i].equals(os2[j])) return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 将数组或集合对象使用分隔符（默认为逗号）连接成一个字符串
	 * @see #asArray(Object)
	 */
	public static String join(Object o) {
		return join(o, ",");
	}
	public static String join(Object o, String split) {
		if (o == null) return null;
		Object[] arr = asArray(o);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			sb.append(arr[i]).append(split);
		}
		if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	/**
	 * 将对象转换为数组，根据对象类型的不同，使用的转换方法如下：
	 * <ul>
	 * <li>若对象为null，则转换为0长的Object[]；
	 * <li>若对象为{@link java.util.Collection}，则使用{@link java.util.Collection#toArray}进行转换；
	 * <li>若对象为字符串或大小为1的字符串数组，则将使用{@link StringUtil#split}进行转换；
	 * <li>若对象本身为数组，则将其转换成为Object[]；
	 * <li>其它情况转换为包含元素o的Object[]；
	 * </ul>
	 */
	public static Object[] asArray(Object o) {
		return asArray(o, true);
	}
	public static Object[] asArray(Object o, boolean splitString) {
		if (o == null) return new Object[0];
		
		if (o instanceof Collection) {
			Collection c = (Collection)o;
			return c.toArray(new Object[c.size()]);
		}
		else if (o instanceof String[] && ((String[])o).length == 1) {
			if (splitString)
				return ((String[])o)[0].split(",");
			else
				return (String[])o;
		}
		else if (o.getClass().isArray()) {
			Object[] ret = new Object[Array.getLength(o)];
			for (int i = 0; i < ret.length; i++)
				ret[i] = Array.get(o, i);
			return ret;
		}
		else if (o instanceof String && splitString) {
			String s = (String)o;
			return StringUtil.split(s).toArray();
		}
		else {
			return new Object[]{o};
		}
	}
	
	/**
	 * 类似{@link String#indexOf}方法取对象在数组中的索引
	 * <p>若不存在，则返回-1
	 */
	public static int indexOf(Object[] array, Object match){
		int ret = -1;
		for(int i = 0; i < array.length; i++) if(array[i] != null && array[i].equals(match)) return i;
		return ret;
	}
}
