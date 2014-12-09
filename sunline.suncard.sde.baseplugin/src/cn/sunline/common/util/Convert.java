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
 * 对象转换工具
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
	 * 转换为布尔类型
	 * 
	 * @param value
	 *            待转换的对象
	 * @param defaultVal
	 *            缺省值
	 * @return <ul>
	 *         <li><tt>缺省值</tt> 如果对象为null.
	 *         <li><tt>true</tt> 如果是字符串对象，且内容为"true"(不区分大小写).
	 *         <li><tt>原值</tt> 如果是布尔对象.
	 *         <li><tt>缺省值</tt> 其它情况.
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
	 * 转换为布尔类型
	 * 
	 * @param value
	 *            待转换的对象
	 * @return <ul>
	 *         <li><tt>null</tt> 如果对象为null.
	 *         <li><tt>true</tt> 如果是字符串对象，且内容为"true"(不区分大小写).
	 *         <li><tt>原值</tt> 如果是布尔对象.
	 *         <li><tt>false</tt> 其它情况.
	 *         </ul>
	 */
	public static Boolean toBoolean(Object value) {
		return toBoolean(value, Boolean.FALSE);
	}

	/**
	 * 按指定格式转换为日期
	 * 
	 * @param value
	 *            待转换的对象
	 * @param pattern
	 *            日期格式
	 * @return 转换后的日期对象
	 *         <ul>
	 *         <li><tt>null</tt> 如果对象为null，或对象为空字符串，或对象为全空格字符串.
	 *         <li><tt>原值</tt> 如果是日期对象.
	 *         <li><tt>日期值</tt> 如果是日历对象.
	 *         <li><tt>按格式转换后的日期</tt> 如果是字符串对象.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             如果是除日期、日历、字符串以外的对象,或者是字符串对象,但转换失败.
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
					throw new java.lang.IllegalArgumentException("模式:[" + pattern + "]与时间串:[" + value + "]不符");
			} catch (Exception e) {
				throw new java.lang.IllegalArgumentException("不能使用模式:[" + pattern + "]格式化时间串:[" + value + "]");
			}
		} else {
			throw new java.lang.IllegalArgumentException("不能使用模式:[" + pattern + "]格式化未知对象:[" + value + "]" + value.getClass().getName());
		}
	}

	/**
	 * 转换为日期(出错时使用缺省值)
	 * 
	 * @param value
	 *            待转换的对象
	 * @param pattern
	 *            日期格式
	 * @param defaultVal
	 *            缺省值
	 * @return 转换后的日期对象
	 *         <ul>
	 *         <li><tt>缺省值</tt> 如果对象为null，或对象为空字符串，或对象为全空格字符串，或转换失败，或非日历、日期、字符串对象.
	 *         <li><tt>原值</tt> 如果是日期对象.
	 *         <li><tt>日期值</tt> 如果是日历对象.
	 *         <li><tt>按格式转换后的日期</tt> 如果是字符串对象.
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
	 * 转换为日期（使用缺省格式：yyyyMMdd）. 相当于:toDate(value, "yyyyMMdd", null)
	 * 
	 * @param value
	 *            待转换的对象
	 * @return 转换后的日期对象
	 *         <ul>
	 *         <li><tt>null</tt> 如果对象为null，或对象为空字符串，或对象为全空格字符串，或转换失败，或非日历、日期、字符串对象.
	 *         <li><tt>原值</tt> 如果是日期对象.
	 *         <li><tt>日期值</tt> 如果是日历对象.
	 *         <li><tt>按格式转换后的日期</tt> 如果是字符串对象.
	 *         </ul>
	 */
	public static Date toDate(Object value) {
		return toDate(value, DateUtil.DATE_FORMAT_YMD, null);
	}

	/**
	 * 转为Calendar对象.
	 * 
	 * @param value
	 *            待转换的对象(可以是Calendar,Date,String)
	 * @param pattern
	 *            解析日期字符串的格式
	 * @return 转换后的对象
	 *         <ul>
	 *         <li><tt>null</tt> 如果对象为null，或对象为空字符串，或对象为全空格字符串.
	 *         <li><tt>原值</tt> 如果是Calendar对象.
	 *         <li><tt>Calendar值</tt> 如果是Date对象.
	 *         <li><tt>按格式转换后的Calendar</tt> 如果是字符串对象.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             如果对象不能转换
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
	 * 转换为java.sql.Timestamp对象
	 * 
	 * @param value
	 *            待转换的对象
	 * @return 转换后的java.sql.Timestamp对象
	 *         <ul>
	 *         <li><tt>null</tt> 如果对象为null，或对象为空字符串，或对象为全空格字符串，或转换失败，或非日历、日期、字符串对象.
	 *         <li><tt>原值</tt> 如果是java.sql.Timestamp对象.
	 *         <li><tt>java.sql.Timestamp值</tt> 如果是Date对象.
	 *         <li><tt>按yyyy-MM-dd HH:mm:ss格式转换后的Timestamp</tt> 如果是字符串对象.
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
	 * 转换为java.sql.Date对象
	 * 
	 * @param value
	 *            待转换的对象
	 * @return 转换后的java.sql.Date对象
	 *         <ul>
	 *         <li><tt>null</tt> 如果对象为null，或对象为空字符串，或对象为全空格字符串，或转换失败，或非日历、日期、字符串对象.
	 *         <li><tt>原值</tt> 如果是java.sql.Date对象.
	 *         <li><tt>java.sql.Date值</tt> 如果是Date对象.
	 *         <li><tt>按yyyy-MM-dd格式转换后的Date</tt> 如果是字符串对象.
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
	 * 转换为java.sql.Time对象
	 * 
	 * @param value
	 *            待转换的对象
	 * @return 转换后的java.sql.Time对象
	 *         <ul>
	 *         <li><tt>null</tt> 如果对象为null，或对象为空字符串，或对象为全空格字符串，或转换失败，或非日历、日期、字符串对象.
	 *         <li><tt>原值</tt> 如果是java.sql.Date对象.
	 *         <li><tt>java.sql.Time值</tt> 如果是Date对象.
	 *         <li><tt>按yyyy-MM-dd HH:mm:ss格式转换后的java.sql.Time</tt> 如果是字符串对象.
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
	 * 将对象转换为数字对象. This method is used by the conversion methods for number types. Note that the return value is not in always of the specified target
	 * class, but only if a new object has to be created.
	 * 
	 * @param value
	 *            待转换对象
	 * @param targetClass
	 *            目标类(必须是继承自<code>java.lang.Number</code>)
	 * @return 转换后的数字对象
	 *         <ul>
	 *         <li><tt>null</tt> 如果对象为null.
	 *         <li><tt>原值</tt> 如果是Number对象.
	 *         <li><tt>BigInteger</tt> 如果是16进制字符串(以0x开关的字符串)，如0x1A0F.
	 *         <li><tt>Number</tt> 其它情况.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             如果对象不能转换
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
	 * 转换为Byte型.
	 * 
	 * @param value
	 *            待转换对象
	 * @return 转换后的Byte对象
	 *         <ul>
	 *         <li><tt>null</tt> 如果对象为null.
	 *         <li><tt>Byte</tt> 如果是Number对象.
	 *         <li><tt>Byte</tt> 如果是16进制字符串(以0x开关的字符串)，如0x1A0F.
	 *         <li><tt>Byte</tt> 其它情况.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             如果对象不能转换
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
	 * 转换为Byte型.
	 * 
	 * @param value
	 *            待转换对象
	 * @param defaultVal
	 *            缺省值
	 * @return 转换后的数字对象
	 *         <ul>
	 *         <li><tt>缺省值</tt> 如果对象为null.
	 *         <li><tt>缺省值</tt> 如果对象不能转换.
	 *         <li><tt>Byte</tt> 如果是Number对象.
	 *         <li><tt>Byte</tt> 如果是16进制字符串(以0x开关的字符串)，如0x1A0F.
	 *         <li><tt>Byte</tt> 其它情况.
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
	 * 转换为Short型.
	 * 
	 * @param value
	 *            待转换对象
	 * @return 转换后的Byte对象
	 *         <ul>
	 *         <li><tt>null</tt> 如果对象为null.
	 *         <li><tt>Short</tt> 如果是Number对象.
	 *         <li><tt>Short</tt> 如果是16进制字符串(以0x开关的字符串)，如0x1A0F.
	 *         <li><tt>Short</tt> 其它情况.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             如果对象不能转换
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
	 * 转换为Short型.
	 * 
	 * @param value
	 *            待转换对象
	 * @param defaultVal
	 *            缺省值
	 * @return 转换后的数字对象
	 *         <ul>
	 *         <li><tt>缺省值</tt> 如果对象为null.
	 *         <li><tt>缺省值</tt> 如果对象不能转换.
	 *         <li><tt>Short</tt> 如果是Number对象.
	 *         <li><tt>Short</tt> 如果是16进制字符串(以0x开关的字符串)，如0x1A0F.
	 *         <li><tt>Short</tt> 其它情况.
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
	 *转换为Float型.
	 * 
	 * @param value
	 *            待转换对象
	 * @return 转换后的Byte对象
	 *         <ul>
	 *         <li><tt>null</tt> 如果对象为null.
	 *         <li><tt>Float</tt> 如果是Number对象.
	 *         <li><tt>Float</tt> 如果是16进制字符串(以0x开关的字符串)，如0x1A0F.
	 *         <li><tt>Float</tt> 其它情况.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             如果对象不能转换
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
	 * 转换为Float型.
	 * 
	 * @param value
	 *            待转换对象
	 * @param defaultVal
	 *            缺省值
	 * @return 转换后的数字对象
	 *         <ul>
	 *         <li><tt>缺省值</tt> 如果对象为null.
	 *         <li><tt>缺省值</tt> 如果对象不能转换.
	 *         <li><tt>Float</tt> 如果是Number对象.
	 *         <li><tt>Float</tt> 如果是16进制字符串(以0x开关的字符串)，如0x1A0F.
	 *         <li><tt>Float</tt> 其它情况.
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
	 * 转换为长整型.
	 * 
	 * @param value
	 *            待转换对象
	 * @return 转换后的数字对象
	 *         <ul>
	 *         <li><tt>null</tt> 如果对象为null.
	 *         <li><tt>Long</tt> 如果是Number对象.
	 *         <li><tt>Long</tt> 如果是16进制字符串(以0x开关的字符串)，如0x1A0F.
	 *         <li><tt>Long</tt> 其它情况.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             如果对象不能转换
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
	 * 转换为长整型.
	 * 
	 * @param value
	 *            待转换对象
	 * @param defaultVal
	 *            缺省值
	 * @return 转换后的数字对象
	 *         <ul>
	 *         <li><tt>缺省值</tt> 如果对象为null.
	 *         <li><tt>缺省值</tt> 如果对象不能转换.
	 *         <li><tt>Long</tt> 如果是Number对象.
	 *         <li><tt>Long</tt> 如果是16进制字符串(以0x开关的字符串)，如0x1A0F.
	 *         <li><tt>Long</tt> 其它情况.
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
	 * 转换为整型.
	 * 
	 * @param value
	 *            待转换对象
	 * @return 转换后的数字对象
	 *         <ul>
	 *         <li><tt>null</tt> 如果对象为null.
	 *         <li><tt>Integer</tt> 如果是Number对象.
	 *         <li><tt>Integer</tt> 如果是16进制字符串(以0x开关的字符串)，如0x1A0F.
	 *         <li><tt>Integer</tt> 其它情况.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             如果对象不能转换
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
	 * 转换为整型.
	 * 
	 * @param value
	 *            待转换对象
	 * @param defaultVal
	 *            缺省值
	 * @return 转换后的数字对象
	 *         <ul>
	 *         <li><tt>缺省值</tt> 如果对象为null.
	 *         <li><tt>缺省值</tt> 如果对象不能转换.
	 *         <li><tt>Integer</tt> 如果是Number对象.
	 *         <li><tt>Integer</tt> 如果是16进制字符串(以0x开关的字符串)，如0x1A0F.
	 *         <li><tt>Integer</tt> 其它情况.
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
	 * 转换为Double型.
	 * 
	 * @param value
	 *            待转换对象
	 * @return 转换后的数字对象
	 *         <ul>
	 *         <li><tt>null</tt> 如果对象为null.
	 *         <li><tt>Double</tt> 如果是Number对象.
	 *         <li><tt>Double</tt> 如果是16进制字符串(以0x开关的字符串)，如0x1A0F.
	 *         <li><tt>Double</tt> 其它情况.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             如果对象不能转换
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
	 * 转换为Double型.
	 * 
	 * @param value
	 *            待转换对象
	 * @param defaultVal
	 *            缺省值
	 * @return 转换后的数字对象
	 *         <ul>
	 *         <li><tt>缺省值</tt> 如果对象为null.
	 *         <li><tt>缺省值</tt> 如果对象不能转换.
	 *         <li><tt>Double</tt> 如果是Number对象.
	 *         <li><tt>Double</tt> 如果是16进制字符串(以0x开关的字符串)，如0x1A0F.
	 *         <li><tt>Double</tt> 其它情况.
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
	 * 转换为BigDecimal型.
	 * 
	 * @param value
	 *            待转换对象
	 * @return 转换后的数字对象
	 *         <ul>
	 *         <li><tt>null</tt> 如果对象为null.
	 *         <li><tt>BigDecimal</tt> 如果是Number对象.
	 *         <li><tt>BigDecimal</tt> 如果是16进制字符串(以0x开关的字符串)，如0x1A0F.
	 *         <li><tt>BigDecimal</tt> 其它情况.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             如果对象不能转换
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
	 * 转换为BigDecimal型. 提供缺省值。
	 * 
	 * @param value
	 *            待转换对象
	 * @param defaultVal
	 *            缺省值
	 * @return 转换后的数字对象
	 *         <ul>
	 *         <li><tt>缺省值</tt> 如果对象为null.
	 *         <li><tt>缺省值</tt> 如果对象不能转换.
	 *         <li><tt>BigDecimal</tt> 如果是Number对象.
	 *         <li><tt>BigDecimal</tt> 如果是16进制字符串(以0x开关的字符串)，如0x1A0F.
	 *         <li><tt>BigDecimal</tt> 其它情况.
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
	 * 转换为BigDecimal型. 可以指定数字格式。
	 * 
	 * @param value
	 *            待转换对象
	 * @param pattern
	 *            数字格式
	 * @return 转换后的数字对象
	 *         <ul>
	 *         <li><tt>null</tt> 如果对象为null.
	 *         <li><tt>BigDecimal</tt> 如果是Number对象.
	 *         <li><tt>BigDecimal</tt> 其它情况.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             如果对象不能转换
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
			throw new java.lang.IllegalArgumentException("不能使用模式:[" + pattern + "]解析数字串:[" + value + "]");
		}
		if (num instanceof BigDecimal)
			ret = (BigDecimal) num;
		else
			ret = new BigDecimal(num.doubleValue());

		return ret;
	}

	/**
	 * 转换为BigDecimal型. 可以指定数字格式及缺省值。
	 * 
	 * @param value
	 *            待转换对象
	 * @param pattern
	 *            数字格式
	 * @param defaultVal
	 *            缺省值
	 * @return 转换后的数字对象
	 *         <ul>
	 *         <li><tt>缺省值</tt> 如果对象为null.
	 *         <li><tt>缺省值</tt> 如果对象不能转换.
	 *         <li><tt>BigDecimal</tt> 如果是Number对象.
	 *         <li><tt>BigDecimal</tt> 其它情况.
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
	 * 按金额格式(#,##0.##)转换为数字类型.
	 * 
	 * @param value
	 *            待转换对象
	 * @return 转换后的数字对象
	 *         <ul>
	 *         <li><tt>null</tt> 如果对象为null.
	 *         <li><tt>null</tt> 如果对象不能转换.
	 *         <li><tt>BigDecimal</tt> 如果是Number对象.
	 *         <li><tt>BigDecimal</tt> 其它情况.
	 *         </ul>
	 */
	public static BigDecimal toAmount(Object value) {
		return toBigDecimal(value, "#,##0.##", null);
	}

	/**
	 * 将字符数据组转为字节数组.
	 * 
	 * @param source
	 *            源字符数据组
	 * @param srclen
	 *            转换的最大长度
	 * @return 最大长度为srclen的字节数组
	 *         <ul>
	 *         <li><tt>null</tt> 如果源字符数据组为null.
	 *         <li><tt>byte[]</tt> 其它情况.
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
	 * 将字节数据组转为字符数组.
	 * 
	 * @param source
	 *            源字节数据组
	 * @param srclen
	 *            转换的最大长度
	 * @return 最大长度为srclen的字符数组
	 *         <ul>
	 *         <li><tt>null</tt> 如果源字节数据组为null.
	 *         <li><tt>byte[]</tt> 其它情况.
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
	 * 转换为Locale对象.
	 * 
	 * @param value
	 *            待转换对象,如"zh_CN","en_US"
	 * @return Locale
	 *         <ul>
	 *         <li><tt>null</tt> 如果源字节数据组为null.
	 *         <li><tt>Locale</tt> 其它情况.
	 *         </ul>
	 * @throws java.lang.IllegalArgumentException
	 *             如果对象不能转换(非字符串对象或非Locale对象，字符串格式不符)
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
	 * 转换为Color对象. 如果是字符串, 合法格式是 (#)?[0-9A-F]{6}([0-9A-F]{2})?. 例如:
	 * <ul>
	 * <li>FF0000 (red)</li>
	 * <li>0000FFA0 (semi transparent blue)</li>
	 * <li>#CCCCCC (gray)</li>
	 * <li>#00FF00A0 (semi transparent green)</li>
	 * </ul>
	 * 
	 * @param value
	 *            待转换对象(可以是Color,String对象)
	 * @return Color对象
	 *         <ul>
	 *         <li><tt>null</tt> 如果待转换对象为null.
	 *         <li><tt>原值</tt> 如果待转换对象为Color对象.
	 *         <li><tt>Color</tt> 其它情况.
	 *         </ul>
	 *@throws java.lang.IllegalArgumentException
	 *             如果对象不能转换(非字符串对象或非Color对象，字符串格式非合法的色彩格式)
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
