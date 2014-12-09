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
	 * �ж����������Ƿ���ȣ�������ȱ���������������֮һ��
	 * <ul>
	 * <li>�������ͬΪ�գ�
	 * <li>���鳤������Ҷ�Ӧλ���ϵ�Ԫ����ȣ�
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
	 * �ж϶���o�Ƿ�����ڶ���arr�У�������������Ϊ���ϻ����顣
	 * <p>ע�⣬���������ж��ַ����Ƿ����ʱ�������trim������
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
	 * ������򼯺϶���ʹ�÷ָ�����Ĭ��Ϊ���ţ����ӳ�һ���ַ���
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
	 * ������ת��Ϊ���飬���ݶ������͵Ĳ�ͬ��ʹ�õ�ת���������£�
	 * <ul>
	 * <li>������Ϊnull����ת��Ϊ0����Object[]��
	 * <li>������Ϊ{@link java.util.Collection}����ʹ��{@link java.util.Collection#toArray}����ת����
	 * <li>������Ϊ�ַ������СΪ1���ַ������飬��ʹ��{@link StringUtil#split}����ת����
	 * <li>��������Ϊ���飬����ת����ΪObject[]��
	 * <li>�������ת��Ϊ����Ԫ��o��Object[]��
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
	 * ����{@link String#indexOf}����ȡ�����������е�����
	 * <p>�������ڣ��򷵻�-1
	 */
	public static int indexOf(Object[] array, Object match){
		int ret = -1;
		for(int i = 0; i < array.length; i++) if(array[i] != null && array[i].equals(match)) return i;
		return ret;
	}
}
