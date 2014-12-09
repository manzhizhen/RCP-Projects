package cn.sunline.common.util;

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ����ת������
 * 
 * @author liangbl
 * 
 */
public class Convert {
	/** Constant for the prefix of hex numbers. */
	private static final String HEX_PREFIX = "0x";

	/** Constant for the radix of hex numbers. */
	private static final int HEX_RADIX = 16;
	/** Constant for the argument classes of the Number constructor that takes a String. */
	private static final Class[] CONSTR_ARGS = { String.class };

	/**
	 * ת��Ϊ��������
	 * 
	 * @param value
	 *            ��ת���Ķ���
	 * @param defaultVal
	 *            ȱʡֵ
	 * @return <ul>
	 *         <li><tt>ȱʡֵ</tt> �������Ϊnull.
	 *         <li><tt>true</tt> ������ַ�������������Ϊ"true"(�����ִ�Сд).
	 *         <li><tt>ԭֵ</tt> ����ǲ�������.
	 *         <li><tt>ȱʡֵ</tt> �������.
	 *         </ul>
	 */
	public static Boolean toBoolean(Object value, Boolean defaultVal) {
		if (value == null)
			return defaultVal;
		if (value instanceof Boolean)
			return (Boolean) value;
		if (value instanceof String) {
			return ("true".equalsIgnoreCase((String) value)) ? Boolean.TRUE : Boolean.FALSE;
		}
		return defaultVal;
	}

	/**
	 * ת��Ϊ��������
	 * 
	 * @param value
	 *            ��ת���Ķ���
	 * @return <ul>
	 *         <li><tt>null</tt> �������Ϊnull.
	 *         <li><tt>true</tt> ������ַ�������������Ϊ"true"(�����ִ�Сд).
	 *         <li><tt>ԭֵ</tt> ����ǲ�������.
	 *         <li><tt>false</tt> �������.
	 *         </ul>
	 */
	public static Boolean toBoolean(Object value) {
		return toBoolean(value, Boolean.FALSE);
	}

	/**
	 * ��ָ����ʽת��Ϊ����
	 * 
	 * @param value
	 *            ��ת���Ķ���
	 * @param pattern
	 *            ���ڸ�ʽ
	 * @return ת��������ڶ���
	 *         <ul>
	 *         <li><tt>null</tt> �������Ϊnull�������Ϊ���ַ����������Ϊȫ�ո��ַ���.
	 *         <li><tt>ԭֵ</tt> ��������ڶ���.
	 *         <li><tt>����ֵ</tt> �������������.
	 *         <li><tt>����ʽת���������</tt> ������ַ�������.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             ����ǳ����ڡ��������ַ�������Ķ���,�������ַ�������,��ת��ʧ��.
	 */
	public static Date toDate(Object value, String pattern) throws java.lang.IllegalArgumentException {
		if (value == null) {
			return null;
		} else if (value instanceof Date) {
			return (Date) value;
		} else if (value instanceof Calendar) {
			return ((Calendar) value).getTime();
		} else if (value instanceof String) {
			String strVal = (String) value;
			if (strVal.trim().length() == 0)
				return null;
			try {
				DateFormat _formater = new SimpleDateFormat(pattern);
				Date _date = _formater.parse((String) value);
				if (strVal.equals(_formater.format(_date))) {
					return _date;
				} else
					throw new java.lang.IllegalArgumentException("ģʽ:[" + pattern + "]��ʱ�䴮:[" + value + "]����");
			} catch (Exception e) {
				throw new java.lang.IllegalArgumentException("����ʹ��ģʽ:[" + pattern + "]��ʽ��ʱ�䴮:[" + value + "]");
			}
		} else {
			throw new java.lang.IllegalArgumentException("����ʹ��ģʽ:[" + pattern + "]��ʽ��δ֪����:[" + value + "]" + value.getClass().getName());
		}
	}

	/**
	 * ת��Ϊ����(����ʱʹ��ȱʡֵ)
	 * 
	 * @param value
	 *            ��ת���Ķ���
	 * @param pattern
	 *            ���ڸ�ʽ
	 * @param defaultVal
	 *            ȱʡֵ
	 * @return ת��������ڶ���
	 *         <ul>
	 *         <li><tt>ȱʡֵ</tt> �������Ϊnull�������Ϊ���ַ����������Ϊȫ�ո��ַ�������ת��ʧ�ܣ�������������ڡ��ַ�������.
	 *         <li><tt>ԭֵ</tt> ��������ڶ���.
	 *         <li><tt>����ֵ</tt> �������������.
	 *         <li><tt>����ʽת���������</tt> ������ַ�������.
	 *         </ul>
	 */
	public static Date toDate(Object value, String pattern, Date defaultVal) {
		Date ret = null;
		try {
			ret = toDate(value, pattern);
		} catch (java.lang.IllegalArgumentException e) {
			ret = defaultVal;
		}
		return (ret == null ? defaultVal : ret);
	}

	/**
	 * ת��Ϊ���ڣ�ʹ��ȱʡ��ʽ��yyyyMMdd��. �൱��:toDate(value, "yyyyMMdd", null)
	 * 
	 * @param value
	 *            ��ת���Ķ���
	 * @return ת��������ڶ���
	 *         <ul>
	 *         <li><tt>null</tt> �������Ϊnull�������Ϊ���ַ����������Ϊȫ�ո��ַ�������ת��ʧ�ܣ�������������ڡ��ַ�������.
	 *         <li><tt>ԭֵ</tt> ��������ڶ���.
	 *         <li><tt>����ֵ</tt> �������������.
	 *         <li><tt>����ʽת���������</tt> ������ַ�������.
	 *         </ul>
	 */
	public static Date toDate(Object value) {
		return toDate(value, DateUtil.DATE_FORMAT_YMD, null);
	}

	/**
	 * תΪCalendar����.
	 * 
	 * @param value
	 *            ��ת���Ķ���(������Calendar,Date,String)
	 * @param pattern
	 *            ���������ַ����ĸ�ʽ
	 * @return ת����Ķ���
	 *         <ul>
	 *         <li><tt>null</tt> �������Ϊnull�������Ϊ���ַ����������Ϊȫ�ո��ַ���.
	 *         <li><tt>ԭֵ</tt> �����Calendar����.
	 *         <li><tt>Calendarֵ</tt> �����Date����.
	 *         <li><tt>����ʽת�����Calendar</tt> ������ַ�������.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             ���������ת��
	 */
	public static Calendar toCalendar(Object value, String pattern) throws java.lang.IllegalArgumentException {
		if (value == null)
			return null;
		if (value instanceof Calendar) {
			return (Calendar) value;
		} else if (value instanceof Date) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime((Date) value);
			return calendar;
		} else if (value instanceof String) {
			String strVal = (String) value;
			if (strVal.trim().length() == 0)
				return null;
			try {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new SimpleDateFormat(pattern).parse(strVal));
				return calendar;
			} catch (ParseException e) {
				throw new java.lang.IllegalArgumentException("The value " + value + " can't be converted to a Calendar");
			}
		} else {
			throw new java.lang.IllegalArgumentException("The value " + value + " can't be converted to a Calendar");
		}
	}

	/**
	 * ת��Ϊjava.sql.Timestamp����
	 * 
	 * @param value
	 *            ��ת���Ķ���
	 * @return ת�����java.sql.Timestamp����
	 *         <ul>
	 *         <li><tt>null</tt> �������Ϊnull�������Ϊ���ַ����������Ϊȫ�ո��ַ�������ת��ʧ�ܣ�������������ڡ��ַ�������.
	 *         <li><tt>ԭֵ</tt> �����java.sql.Timestamp����.
	 *         <li><tt>java.sql.Timestampֵ</tt> �����Date����.
	 *         <li><tt>��yyyy-MM-dd HH:mm:ss��ʽת�����Timestamp</tt> ������ַ�������.
	 *         </ul>
	 */
	public static java.sql.Timestamp toTimestamp(Object value) {
		if (value == null)
			return null;
		if (value instanceof java.sql.Timestamp) {
			return (Timestamp) value;
		}
		java.util.Date _date = toDate(value, "yyyy-MM-dd HH:mm:ss", null);
		if (_date == null)
			return null;
		return new java.sql.Timestamp(_date.getTime());
	}

	/**
	 * ת��Ϊjava.sql.Date����
	 * 
	 * @param value
	 *            ��ת���Ķ���
	 * @return ת�����java.sql.Date����
	 *         <ul>
	 *         <li><tt>null</tt> �������Ϊnull�������Ϊ���ַ����������Ϊȫ�ո��ַ�������ת��ʧ�ܣ�������������ڡ��ַ�������.
	 *         <li><tt>ԭֵ</tt> �����java.sql.Date����.
	 *         <li><tt>java.sql.Dateֵ</tt> �����Date����.
	 *         <li><tt>��yyyy-MM-dd��ʽת�����Date</tt> ������ַ�������.
	 *         </ul>
	 */
	public static java.sql.Date toSqlDate(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof java.sql.Date) {
			return (java.sql.Date) value;
		}
		java.util.Date _date = toDate(value, "yyyy-MM-dd", null);
		if (_date == null)
			return null;
		return new java.sql.Date(_date.getTime());
	}

	/**
	 * ת��Ϊjava.sql.Time����
	 * 
	 * @param value
	 *            ��ת���Ķ���
	 * @return ת�����java.sql.Time����
	 *         <ul>
	 *         <li><tt>null</tt> �������Ϊnull�������Ϊ���ַ����������Ϊȫ�ո��ַ�������ת��ʧ�ܣ�������������ڡ��ַ�������.
	 *         <li><tt>ԭֵ</tt> �����java.sql.Date����.
	 *         <li><tt>java.sql.Timeֵ</tt> �����Date����.
	 *         <li><tt>��yyyy-MM-dd HH:mm:ss��ʽת�����java.sql.Time</tt> ������ַ�������.
	 *         </ul>
	 */
	public static java.sql.Time toSqlTime(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof java.sql.Time) {
			return (java.sql.Time) value;
		}
		java.util.Date _date = toDate(value, "yyyy-MM-dd HH:mm:ss", null);
		if (_date == null)
			return null;
		return new java.sql.Time(_date.getTime());
	}

	/**
	 * ������ת��Ϊ���ֶ���. This method is used by the conversion methods for number types. Note that the return value is not in always of the specified target
	 * class, but only if a new object has to be created.
	 * 
	 * @param value
	 *            ��ת������
	 * @param targetClass
	 *            Ŀ����(�����Ǽ̳���<code>java.lang.Number</code>)
	 * @return ת��������ֶ���
	 *         <ul>
	 *         <li><tt>null</tt> �������Ϊnull.
	 *         <li><tt>ԭֵ</tt> �����Number����.
	 *         <li><tt>BigInteger</tt> �����16�����ַ���(��0x���ص��ַ���)����0x1A0F.
	 *         <li><tt>Number</tt> �������.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             ���������ת��
	 */
	static Number toNumber(Object value, Class targetClass) throws java.lang.IllegalArgumentException {
		if (value == null)
			return null;
		if (value instanceof Number) {
			return (Number) value;
		} else {
			String str = value.toString();
			if (str.startsWith(HEX_PREFIX)) {
				try {
					return new BigInteger(str.substring(HEX_PREFIX.length()), HEX_RADIX);
				} catch (NumberFormatException nex) {
					throw new java.lang.IllegalArgumentException("Could not convert '" + str + "' to " + targetClass.getName()
							+ "! Invalid hex number.");
				}
			}

			try {
				Constructor constr = targetClass.getConstructor(CONSTR_ARGS);
				return (Number) constr.newInstance(new Object[] { str });
			} catch (InvocationTargetException itex) {
				throw new java.lang.IllegalArgumentException("Could not convert '" + str + "' to " + targetClass.getName());
			} catch (Exception ex) {
				// Treat all possible exceptions the same way
				throw new java.lang.IllegalArgumentException("Conversion error when trying to convert " + str + " to " + targetClass.getName());
			}
		}
	}

	/**
	 * ת��ΪByte��.
	 * 
	 * @param value
	 *            ��ת������
	 * @return ת�����Byte����
	 *         <ul>
	 *         <li><tt>null</tt> �������Ϊnull.
	 *         <li><tt>Byte</tt> �����Number����.
	 *         <li><tt>Byte</tt> �����16�����ַ���(��0x���ص��ַ���)����0x1A0F.
	 *         <li><tt>Byte</tt> �������.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             ���������ת��
	 */
	public static Byte toByte(Object value) throws java.lang.IllegalArgumentException {
		if (value == null)
			return null;
		Number n = toNumber(value, Byte.class);
		if (n instanceof Byte) {
			return (Byte) n;
		} else {
			return new Byte(n.byteValue());
		}
	}

	/**
	 * ת��ΪByte��.
	 * 
	 * @param value
	 *            ��ת������
	 * @param defaultVal
	 *            ȱʡֵ
	 * @return ת��������ֶ���
	 *         <ul>
	 *         <li><tt>ȱʡֵ</tt> �������Ϊnull.
	 *         <li><tt>ȱʡֵ</tt> ���������ת��.
	 *         <li><tt>Byte</tt> �����Number����.
	 *         <li><tt>Byte</tt> �����16�����ַ���(��0x���ص��ַ���)����0x1A0F.
	 *         <li><tt>Byte</tt> �������.
	 *         </ul>
	 */
	public static Byte toByte(Object value, Byte defaultVal) {
		Byte ret = null;
		try {
			ret = toByte(value);
		} catch (Exception e) {
			ret = defaultVal;
		}
		return (ret == null ? defaultVal : ret);
	}

	/**
	 * ת��ΪShort��.
	 * 
	 * @param value
	 *            ��ת������
	 * @return ת�����Byte����
	 *         <ul>
	 *         <li><tt>null</tt> �������Ϊnull.
	 *         <li><tt>Short</tt> �����Number����.
	 *         <li><tt>Short</tt> �����16�����ַ���(��0x���ص��ַ���)����0x1A0F.
	 *         <li><tt>Short</tt> �������.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             ���������ת��
	 */
	public static Short toShort(Object value) throws java.lang.IllegalArgumentException {
		if (value == null)
			return null;
		Number n = toNumber(value, Short.class);
		if (n instanceof Short) {
			return (Short) n;
		} else {
			return new Short(n.shortValue());
		}
	}

	/**
	 * ת��ΪShort��.
	 * 
	 * @param value
	 *            ��ת������
	 * @param defaultVal
	 *            ȱʡֵ
	 * @return ת��������ֶ���
	 *         <ul>
	 *         <li><tt>ȱʡֵ</tt> �������Ϊnull.
	 *         <li><tt>ȱʡֵ</tt> ���������ת��.
	 *         <li><tt>Short</tt> �����Number����.
	 *         <li><tt>Short</tt> �����16�����ַ���(��0x���ص��ַ���)����0x1A0F.
	 *         <li><tt>Short</tt> �������.
	 *         </ul>
	 */
	public static Short toShort(Object value, Short defaultVal) {
		Short ret = null;
		try {
			ret = toShort(value);
		} catch (Exception e) {
			ret = defaultVal;
		}
		return (ret == null ? defaultVal : ret);
	}

	/**
	 *ת��ΪFloat��.
	 * 
	 * @param value
	 *            ��ת������
	 * @return ת�����Byte����
	 *         <ul>
	 *         <li><tt>null</tt> �������Ϊnull.
	 *         <li><tt>Float</tt> �����Number����.
	 *         <li><tt>Float</tt> �����16�����ַ���(��0x���ص��ַ���)����0x1A0F.
	 *         <li><tt>Float</tt> �������.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             ���������ת��
	 */
	public static Float toFloat(Object value) throws java.lang.IllegalArgumentException {
		if (value == null)
			return null;
		Number n = toNumber(value, Float.class);
		if (n instanceof Float) {
			return (Float) n;
		} else {
			return new Float(n.floatValue());
		}
	}

	/**
	 * ת��ΪFloat��.
	 * 
	 * @param value
	 *            ��ת������
	 * @param defaultVal
	 *            ȱʡֵ
	 * @return ת��������ֶ���
	 *         <ul>
	 *         <li><tt>ȱʡֵ</tt> �������Ϊnull.
	 *         <li><tt>ȱʡֵ</tt> ���������ת��.
	 *         <li><tt>Float</tt> �����Number����.
	 *         <li><tt>Float</tt> �����16�����ַ���(��0x���ص��ַ���)����0x1A0F.
	 *         <li><tt>Float</tt> �������.
	 *         </ul>
	 */
	public static Float toFloat(Object value, Float defaultVal) {
		Float ret = null;
		try {
			ret = toFloat(value);
		} catch (Exception e) {
			ret = defaultVal;
		}
		return (ret == null ? defaultVal : ret);
	}

	/**
	 * ת��Ϊ������.
	 * 
	 * @param value
	 *            ��ת������
	 * @return ת��������ֶ���
	 *         <ul>
	 *         <li><tt>null</tt> �������Ϊnull.
	 *         <li><tt>Long</tt> �����Number����.
	 *         <li><tt>Long</tt> �����16�����ַ���(��0x���ص��ַ���)����0x1A0F.
	 *         <li><tt>Long</tt> �������.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             ���������ת��
	 */
	public static Long toLong(Object value) throws java.lang.IllegalArgumentException {
		if (value == null)
			return null;
		Number n = toNumber(value, Long.class);
		if (n instanceof Long) {
			return (Long) n;
		} else {
			return new Long(n.longValue());
		}
	}

	/**
	 * ת��Ϊ������.
	 * 
	 * @param value
	 *            ��ת������
	 * @param defaultVal
	 *            ȱʡֵ
	 * @return ת��������ֶ���
	 *         <ul>
	 *         <li><tt>ȱʡֵ</tt> �������Ϊnull.
	 *         <li><tt>ȱʡֵ</tt> ���������ת��.
	 *         <li><tt>Long</tt> �����Number����.
	 *         <li><tt>Long</tt> �����16�����ַ���(��0x���ص��ַ���)����0x1A0F.
	 *         <li><tt>Long</tt> �������.
	 *         </ul>
	 */
	public static Long toLong(Object value, Long defaultVal) {
		Long ret = null;
		try {
			ret = toLong(value);
		} catch (Exception e) {
			ret = defaultVal;
		}
		return (ret == null ? defaultVal : ret);
	}

	/**
	 * ת��Ϊ����.
	 * 
	 * @param value
	 *            ��ת������
	 * @return ת��������ֶ���
	 *         <ul>
	 *         <li><tt>null</tt> �������Ϊnull.
	 *         <li><tt>Integer</tt> �����Number����.
	 *         <li><tt>Integer</tt> �����16�����ַ���(��0x���ص��ַ���)����0x1A0F.
	 *         <li><tt>Integer</tt> �������.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             ���������ת��
	 */
	public static Integer toInteger(Object value) throws java.lang.IllegalArgumentException {
		if (value == null)
			return null;
		Number n = toNumber(value, Integer.class);
		if (n instanceof Integer) {
			return (Integer) n;
		} else {
			return new Integer(n.intValue());
		}
	}

	/**
	 * ת��Ϊ����.
	 * 
	 * @param value
	 *            ��ת������
	 * @param defaultVal
	 *            ȱʡֵ
	 * @return ת��������ֶ���
	 *         <ul>
	 *         <li><tt>ȱʡֵ</tt> �������Ϊnull.
	 *         <li><tt>ȱʡֵ</tt> ���������ת��.
	 *         <li><tt>Integer</tt> �����Number����.
	 *         <li><tt>Integer</tt> �����16�����ַ���(��0x���ص��ַ���)����0x1A0F.
	 *         <li><tt>Integer</tt> �������.
	 *         </ul>
	 */
	public static Integer toInteger(Object value, Integer defaultVal) {
		Integer ret = null;
		try {
			ret = toInteger(value);
		} catch (Exception e) {
			ret = defaultVal;
		}
		return (ret == null ? defaultVal : ret);
	}

	/**
	 * ת��ΪDouble��.
	 * 
	 * @param value
	 *            ��ת������
	 * @return ת��������ֶ���
	 *         <ul>
	 *         <li><tt>null</tt> �������Ϊnull.
	 *         <li><tt>Double</tt> �����Number����.
	 *         <li><tt>Double</tt> �����16�����ַ���(��0x���ص��ַ���)����0x1A0F.
	 *         <li><tt>Double</tt> �������.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             ���������ת��
	 */
	public static Double toDouble(Object value) throws java.lang.IllegalArgumentException {
		if (value == null)
			return null;
		Number n = toNumber(value, Double.class);
		if (n instanceof Double) {
			return (Double) n;
		} else {
			return new Double(n.doubleValue());
		}
	}

	/**
	 * ת��ΪDouble��.
	 * 
	 * @param value
	 *            ��ת������
	 * @param defaultVal
	 *            ȱʡֵ
	 * @return ת��������ֶ���
	 *         <ul>
	 *         <li><tt>ȱʡֵ</tt> �������Ϊnull.
	 *         <li><tt>ȱʡֵ</tt> ���������ת��.
	 *         <li><tt>Double</tt> �����Number����.
	 *         <li><tt>Double</tt> �����16�����ַ���(��0x���ص��ַ���)����0x1A0F.
	 *         <li><tt>Double</tt> �������.
	 *         </ul>
	 */
	public static Double toDouble(Object value, Double defaultVal) {
		Double ret = null;
		try {
			ret = toDouble(value);
		} catch (Exception e) {
			ret = defaultVal;
		}
		return (ret == null ? defaultVal : ret);
	}

	/**
	 * ת��ΪBigDecimal��.
	 * 
	 * @param value
	 *            ��ת������
	 * @return ת��������ֶ���
	 *         <ul>
	 *         <li><tt>null</tt> �������Ϊnull.
	 *         <li><tt>BigDecimal</tt> �����Number����.
	 *         <li><tt>BigDecimal</tt> �����16�����ַ���(��0x���ص��ַ���)����0x1A0F.
	 *         <li><tt>BigDecimal</tt> �������.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             ���������ת��
	 */
	public static BigDecimal toBigDecimal(Object value) throws java.lang.IllegalArgumentException {
		if (value == null)
			return null;
		Number n = toNumber(value, BigDecimal.class);
		if (n instanceof BigDecimal) {
			return (BigDecimal) n;
		} else {
			return new BigDecimal(n.doubleValue());
		}
	}

	/**
	 * ת��ΪBigDecimal��. �ṩȱʡֵ��
	 * 
	 * @param value
	 *            ��ת������
	 * @param defaultVal
	 *            ȱʡֵ
	 * @return ת��������ֶ���
	 *         <ul>
	 *         <li><tt>ȱʡֵ</tt> �������Ϊnull.
	 *         <li><tt>ȱʡֵ</tt> ���������ת��.
	 *         <li><tt>BigDecimal</tt> �����Number����.
	 *         <li><tt>BigDecimal</tt> �����16�����ַ���(��0x���ص��ַ���)����0x1A0F.
	 *         <li><tt>BigDecimal</tt> �������.
	 *         </ul>
	 */
	public static BigDecimal toBigDecimal(Object value, BigDecimal defaultVal) {
		BigDecimal ret = null;
		try {
			ret = toBigDecimal(value);
		} catch (Exception e) {
			ret = defaultVal;
		}
		return (ret == null ? defaultVal : ret);
	}

	/**
	 * ת��ΪBigDecimal��. ����ָ�����ָ�ʽ��
	 * 
	 * @param value
	 *            ��ת������
	 * @param pattern
	 *            ���ָ�ʽ
	 * @return ת��������ֶ���
	 *         <ul>
	 *         <li><tt>null</tt> �������Ϊnull.
	 *         <li><tt>BigDecimal</tt> �����Number����.
	 *         <li><tt>BigDecimal</tt> �������.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             ���������ת��
	 */
	public static BigDecimal toBigDecimal(Object value, String pattern) throws java.lang.IllegalArgumentException {
		BigDecimal ret;
		if (value == null)
			return null;
		if (value instanceof BigDecimal)
			return (BigDecimal) value;
		if (value instanceof Number)
			return new BigDecimal(((Number) value).doubleValue());

		String str = value.toString();
		DecimalFormat df = new DecimalFormat(pattern);
		Number num;
		try {
			num = df.parse(str);
		} catch (ParseException e) {
			throw new java.lang.IllegalArgumentException("����ʹ��ģʽ:[" + pattern + "]�������ִ�:[" + value + "]");
		}
		if (num instanceof BigDecimal)
			ret = (BigDecimal) num;
		else
			ret = new BigDecimal(num.doubleValue());

		return ret;
	}

	/**
	 * ת��ΪBigDecimal��. ����ָ�����ָ�ʽ��ȱʡֵ��
	 * 
	 * @param value
	 *            ��ת������
	 * @param pattern
	 *            ���ָ�ʽ
	 * @param defaultVal
	 *            ȱʡֵ
	 * @return ת��������ֶ���
	 *         <ul>
	 *         <li><tt>ȱʡֵ</tt> �������Ϊnull.
	 *         <li><tt>ȱʡֵ</tt> ���������ת��.
	 *         <li><tt>BigDecimal</tt> �����Number����.
	 *         <li><tt>BigDecimal</tt> �������.
	 *         </ul>
	 */
	public static BigDecimal toBigDecimal(Object value, String pattern, BigDecimal defaultVal) {
		BigDecimal ret = null;
		try {
			ret = toBigDecimal(value, pattern);
		} catch (Exception e) {
			ret = defaultVal;
		}
		return (ret == null ? defaultVal : ret);
	}

	/**
	 * ������ʽ(#,##0.##)ת��Ϊ��������.
	 * 
	 * @param value
	 *            ��ת������
	 * @return ת��������ֶ���
	 *         <ul>
	 *         <li><tt>null</tt> �������Ϊnull.
	 *         <li><tt>null</tt> ���������ת��.
	 *         <li><tt>BigDecimal</tt> �����Number����.
	 *         <li><tt>BigDecimal</tt> �������.
	 *         </ul>
	 */
	public static BigDecimal toAmount(Object value) {
		return toBigDecimal(value, "#,##0.##", null);
	}

	/**
	 * ���ַ�������תΪ�ֽ�����.
	 * 
	 * @param source
	 *            Դ�ַ�������
	 * @param srclen
	 *            ת������󳤶�
	 * @return ��󳤶�Ϊsrclen���ֽ�����
	 *         <ul>
	 *         <li><tt>null</tt> ���Դ�ַ�������Ϊnull.
	 *         <li><tt>byte[]</tt> �������.
	 *         </ul>
	 */
	public static byte[] charsToBytes(char[] source, int srclen) {
		if (source == null) {
			return null;
		}

		int len = source.length;
		if (len > srclen) {
			len = srclen;
		}
		byte[] dest = new byte[len];
		for (int i = 0; i < len; i++) {
			dest[i] = (byte) source[i];
		}
		return dest;
	}

	/**
	 * ���ֽ�������תΪ�ַ�����.
	 * 
	 * @param source
	 *            Դ�ֽ�������
	 * @param srclen
	 *            ת������󳤶�
	 * @return ��󳤶�Ϊsrclen���ַ�����
	 *         <ul>
	 *         <li><tt>null</tt> ���Դ�ֽ�������Ϊnull.
	 *         <li><tt>byte[]</tt> �������.
	 *         </ul>
	 */
	public static char[] bytesToChars(byte[] source, int srclen) {
		if (source == null) {
			return null;
		}
		int len = source.length;
		if (len > srclen) {
			len = srclen;
		}
		char[] destChar = new char[len];
		for (int i = 0; i < len; i++) {
			if (source[i] >= 0) {
				destChar[i] = (char) source[i];
			} else {
				destChar[i] = (char) (256 + source[i]);
			}
		}
		return destChar;
	}

	/**
	 * ת��ΪLocale����.
	 * 
	 * @param value
	 *            ��ת������,��"zh_CN","en_US"
	 * @return Locale
	 *         <ul>
	 *         <li><tt>null</tt> ���Դ�ֽ�������Ϊnull.
	 *         <li><tt>Locale</tt> �������.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             ���������ת��(���ַ���������Locale�����ַ�����ʽ����)
	 */
	public static Locale toLocale(Object value) throws java.lang.IllegalArgumentException {
		if (value == null)
			return null;
		if (value instanceof Locale) {
			return (Locale) value;
		} else if (value instanceof String) {
			List elements = StringUtil.split((String) value, '_');
			int size = elements.size();

			if (size >= 1 && (((String) elements.get(0)).length() == 2 || ((String) elements.get(0)).length() == 0)) {
				String language = (String) elements.get(0);
				String country = (String) ((size >= 2) ? elements.get(1) : "");
				String variant = (String) ((size >= 3) ? elements.get(2) : "");

				return new Locale(language, country, variant);
			} else {
				throw new java.lang.IllegalArgumentException("The value " + value + " can't be converted to a Locale");
			}
		} else {
			throw new java.lang.IllegalArgumentException("The value " + value + " can't be converted to a Locale");
		}
	}

	/**
	 * ת��ΪColor����. ������ַ���, �Ϸ���ʽ�� (#)?[0-9A-F]{6}([0-9A-F]{2})?. ����:
	 * <ul>
	 * <li>FF0000 (red)</li>
	 * <li>0000FFA0 (semi transparent blue)</li>
	 * <li>#CCCCCC (gray)</li>
	 * <li>#00FF00A0 (semi transparent green)</li>
	 * </ul>
	 * 
	 * @param value
	 *            ��ת������(������Color,String����)
	 * @return Color����
	 *         <ul>
	 *         <li><tt>null</tt> �����ת������Ϊnull.
	 *         <li><tt>ԭֵ</tt> �����ת������ΪColor����.
	 *         <li><tt>Color</tt> �������.
	 *         </ul>
	 *@throws java.lang.IllegalArgumentException
	 *             ���������ת��(���ַ���������Color�����ַ�����ʽ�ǺϷ���ɫ�ʸ�ʽ)
	 */
	public static Color toColor(Object value) throws java.lang.IllegalArgumentException {
		if (value == null)
			return null;
		if (value instanceof Color) {
			return (Color) value;
		} else if (value instanceof String && !StringUtil.isBlank((String) value)) {
			String color = ((String) value).trim();

			int[] components = new int[3];

			// check the size of the string
			int minlength = components.length * 2;
			if (color.length() < minlength) {
				throw new java.lang.IllegalArgumentException("The value " + value + " can't be converted to a Color");
			}

			// remove the leading #
			if (color.startsWith("#")) {
				color = color.substring(1);
			}

			try {
				// parse the components
				for (int i = 0; i < components.length; i++) {
					components[i] = Integer.parseInt(color.substring(2 * i, 2 * i + 2), HEX_RADIX);
				}

				// parse the transparency
				int alpha;
				if (color.length() >= minlength + 2) {
					alpha = Integer.parseInt(color.substring(minlength, minlength + 2), HEX_RADIX);
				} else {
					alpha = Color.black.getAlpha();
				}

				return new Color(components[0], components[1], components[2], alpha);
			} catch (Exception e) {
				throw new java.lang.IllegalArgumentException("The value " + value + " can't be converted to a Color");
			}
		} else {
			throw new java.lang.IllegalArgumentException("The value " + value + " can't be converted to a Color");
		}
	}
}
