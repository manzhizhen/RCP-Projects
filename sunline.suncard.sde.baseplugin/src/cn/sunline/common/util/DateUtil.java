package cn.sunline.common.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Locale;

/**
 * <p>
 * Title: 日期实用类
 * </p>
 * <p>
 * Description: 提供处理日期时间的通用方法。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2010
 * </p>
 * <p>
 * Company: sunline Tech Ltd.
 * </p>
 * 
 * @author liangbl@sunline.cn
 */

public class DateUtil {
	public static final String TIME_FORMAT_Y_M_D = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 时间格式(年-月-日）
	 */
	public static final String DATE_FORMAT_YMD_LONG = "yyyy-MM-dd";

	/**
	 * 时间格式(年月日）
	 */
	public static final String DATE_FORMAT_YMD = "yyyyMMdd";

	/**
	 * 时间格式（年月）
	 */
	public static final String DATE_FORMAT_YM = "yyyyMM";

	/**
	 * 时间格式（年）
	 */
	public static final String DATE_FORMAT_Y = "yyyy";

	/**
	 * 将java.util.Date对象转换为java.sql.Timestamp对象
	 * 
	 * @param value
	 * @return java.sql.Timestamp。
	 */
	public static Timestamp getTimestamp(java.util.Date value) {
		if (value == null)
			return null;
		return new Timestamp(value.getTime());
	}

	/**
	 * 取得当前时间，返回Timestamp对象
	 * 
	 * @param
	 * @return Timestamp。
	 */
	public static Timestamp getNow() {
		return new Timestamp(System.currentTimeMillis());
	}

	/**
	 * 根据给定格式，返回字符串格式的当前时间
	 * 
	 * @param format
	 *            给定格式，若给定格式为空，则格式默认为"yyyy-MM-dd HH:mm:ss"
	 * @return String。
	 */
	public static String getNow(String format) {
		if (null == format || "".equals(format)) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		return formatDate(new Date(), format);
	}

	/**
	 * 截取日期部分
	 * <p>
	 * 如sDate为null,则返回当前日期
	 * 
	 * @param sDate
	 * @return
	 */
	public static Date getDate(Date sDate) {
		Date tDate = sDate == null ? new Date() : sDate;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(tDate);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 取得某月天数
	 * 
	 * @param year
	 *            int 年（例2004）
	 * @param month
	 *            int 月（1-12）
	 * @return int 当月天数
	 */
	public static int getDaysOfMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, 1);
		return calendar.getActualMaximum(Calendar.DATE);
	}

	/**
	 * 使用中文字符以复杂的形式（"年 月 日 上午 时 分 秒"）格式化时间串
	 * 
	 * @param _date
	 *            日期对象
	 * @return 格式化后的日期
	 */
	public static String complexFormatChineseDate(java.util.Date _date) {
		Calendar calendar = getCalendarInstance();
		calendar.setTime(_date);

		String timeStr = calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日";
		timeStr = timeStr + calendar.get(Calendar.HOUR_OF_DAY) + "时" + calendar.get(Calendar.MINUTE) + "分" + calendar.get(Calendar.SECOND) + "秒";
		calendar.clear();

		return timeStr;
	}

	/**
	 * 使用格式 <b>_pattern </b>格式化日期输出
	 * 
	 * @param _date
	 *            日期对象
	 * @param _pattern
	 *            日期格式
	 * @return 格式化后的日期
	 */
	public static String formatDate(java.util.Date _date, String _pattern) {
		if (_date == null) {
			return "";
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(_pattern);
		String stringDate = simpleDateFormat.format(_date);

		return stringDate;
	}

	/**
	 * 使用格式 {@link #DATE_FORMAT_YMD}格式化日期输出
	 * 
	 * @param _date
	 *            日期对象
	 * @return 格式化后的日期
	 */
	public static String formatDate(java.util.Date _date) {
		return formatDate(_date, DATE_FORMAT_YMD);
	}

	/**
	 * 获得以参数_fromDate为基数的年龄
	 * 
	 * @param _birthday
	 *            生日
	 * @param _fromDate
	 *            起算时间
	 * @return 年龄（起算年－出生年）
	 */
	public static int getAgeFromBirthday(java.util.Date _birthday, java.util.Date _fromDate) {

		if (_fromDate == null)
			_fromDate = new java.util.Date(System.currentTimeMillis());

		Calendar calendar = getCalendarInstance();
		calendar.setTime(_birthday);

		int birthdayYear = calendar.get(Calendar.YEAR);
		int birthdayMonth = calendar.get(Calendar.MONTH);
		int birthdayDay = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.clear();
		calendar.setTime(_fromDate);

		int currentYear = calendar.get(Calendar.YEAR);
		int currentMonth = calendar.get(Calendar.MONTH);
		int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.clear();

		int age = currentYear - birthdayYear;

		if (!((currentMonth >= birthdayMonth) && (currentDay >= birthdayDay))) {
			age--;
		}

		return age;
	}

	/**
	 * 获得当前年龄
	 * 
	 * @param _birthday
	 *            生日
	 * @return 年龄（起算年－出生年）
	 */
	public static int getAgeFromBirthday(java.util.Date _birthday) {
		return getAgeFromBirthday(_birthday, new java.util.Date(System.currentTimeMillis()));
	}

	/**
	 * 获得当前年龄
	 * 
	 * @param _birthday
	 *            生日
	 * @return 年龄（起算年－出生年）
	 */
	public static int getAgeFromBirthday(java.sql.Timestamp _birthday) {
		return getAgeFromBirthday(new java.util.Date(_birthday.getTime()), new java.util.Date(System.currentTimeMillis()));
	}

	/**
	 * 获得日期的天，以月为基
	 * 
	 * @param _date
	 *            日期对象
	 * @return 日期的天
	 */
	public static int getDay(java.util.Date _date) {
		Calendar calendar = getCalendarInstance();
		calendar.setTime(_date);

		int day = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.clear();

		return day;
	}

	/**
	 * 计算两个日期间相隔的天数
	 * 
	 * @param _startDate
	 *            起始日期
	 * @param _endDate
	 *            终止日期
	 * @return 相隔天数, 如果结果为正表示 <b>_endDate </b>在 <b>_startDate </b>之后；如果结果为负表示 <b>_endDate </b>在 <b>_startDate </b>之前；如果结果为0表示 <b>_endDate </b>和
	 *         <b>_startDate </b>是同一天。
	 */
	public static int getDayCount(java.util.Date _startDate, java.util.Date _endDate) {
		Calendar calendar = getCalendarInstance();
		calendar.setTime(_startDate);

		int startDay = calendar.get(Calendar.DAY_OF_YEAR);
		int startYear = calendar.get(Calendar.YEAR);
		calendar.clear();
		calendar.setTime(_endDate);

		int endDay = calendar.get(Calendar.DAY_OF_YEAR);
		int endYear = calendar.get(Calendar.YEAR);
		calendar.clear();

		return ((endYear - startYear) * 365) + (endDay - startDay);
	}

	public static int getDayCount(String _startDate, String sdf, String _endDate, String edf) {
		return getDayCount(Convert.toDate(_startDate, sdf), Convert.toDate(_endDate, edf));
	}

	public static int getDayCount(String _startDate, String _endDate) {
		return getDayCount(Convert.toDate(_startDate), Convert.toDate(_endDate));
	}

	/**
	 * 获得日期的小时
	 * 
	 * @param _date
	 *            日期对象
	 * @return 日期的小时
	 */
	public static int getHours(java.util.Date _date) {
		Calendar calendar = getCalendarInstance();
		calendar.setTime(_date);

		int value = calendar.get(Calendar.HOUR);
		calendar.clear();

		return value;
	}

	/**
	 * 获得日期的分钟
	 * 
	 * @param _date
	 *            日期对象
	 * @return 日期的分钟
	 */
	public static int getMinutes(java.util.Date _date) {
		Calendar calendar = getCalendarInstance();
		calendar.setTime(_date);

		int value = calendar.get(Calendar.MINUTE);
		calendar.clear();

		return value;
	}

	/**
	 * 获得日期的月
	 * 
	 * @param _date
	 *            日期对象
	 * @return 日期的月
	 */
	public static int getMonth(java.util.Date _date) {
		Calendar calendar = getCalendarInstance();
		calendar.setTime(_date);

		// 以0开始
		int month = calendar.get(Calendar.MONTH);
		calendar.clear();

		return (month + 1);
	}

	/**
	 * 计算两个日期间相隔的月数, 每隔一月，其相隔天数必>30
	 * 
	 * @param _startDate
	 *            起始日期
	 * @param _endDate
	 *            终止日期
	 * @return 相隔月数, 如果结果为正表示 <b>_endDate </b>在 <b>_startDate </b>之后；如果结果为负表示 <b>_endDate </b>在 <b>_startDate </b>之前；如果结果为0表示 <b>_endDate </b>和
	 *         <b>_startDate </b>是同一天。
	 */
	public static int getMonthCount(java.util.Date _startDate, java.util.Date _endDate) {
		java.util.Date startDate = _startDate;
		java.util.Date endDate = _endDate;
		boolean afterFlag = false;

		if (_startDate.after(_endDate)) {
			startDate = _endDate;
			endDate = _startDate;
			afterFlag = true;
		}

		Calendar calendar = getCalendarInstance();
		calendar.setTime(startDate);

		int startDay = calendar.get(Calendar.DAY_OF_MONTH);
		int startMonth = calendar.get(Calendar.MONTH);
		int startYear = calendar.get(Calendar.YEAR);
		int countDayOfStartMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar.clear();
		calendar.setTime(endDate);

		int endDay = calendar.get(Calendar.DAY_OF_MONTH);
		int endMonth = calendar.get(Calendar.MONTH);
		int endYear = calendar.get(Calendar.YEAR);
		calendar.clear();

		int result = ((endYear - startYear) * 12) + (endMonth - (startMonth + 1))
				+ (int) ((endDay + (countDayOfStartMonth - startDay)) / countDayOfStartMonth);

		if (afterFlag) {
			return -result;
		} else {
			return result;
		}
	}

	/**
	 * 获得日期的小秒
	 * 
	 * @param _date
	 *            日期对象
	 * @return 日期的秒
	 */
	public static int getSeconds(java.util.Date _date) {
		Calendar calendar = getCalendarInstance();
		calendar.setTime(_date);

		int value = calendar.get(Calendar.SECOND);
		calendar.clear();

		return value;
	}

	/**
	 * 获得日期的年
	 * 
	 * @param _date
	 *            日期对象
	 * @return 日期的年
	 */
	public static int getYear(java.util.Date _date) {
		Calendar calendar = getCalendarInstance();
		calendar.setTime(_date);

		int year = calendar.get(Calendar.YEAR);
		calendar.clear();

		return year;
	}

	/**
	 * 使用中文字符以简单的形式（"年 月 日"）格式化时间串
	 * 
	 * @param _date
	 *            日期对象
	 * @return 格式化后的日期
	 */
	public static String simpleFormatChineseDate(java.util.Date _date) {
		Calendar calendar = getCalendarInstance();
		calendar.setTime(_date);

		String timeStr = calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日";
		calendar.clear();

		return timeStr;
	}

	/**
	 * <p>
	 * Title :雅普兰Web开发框架
	 * </p>
	 * <p>
	 * Description: 得到当前时间（Timestamp）
	 * </p>
	 */
	public static Timestamp getCurTime() {
		java.sql.Timestamp sdate = new java.sql.Timestamp(System.currentTimeMillis());

		return sdate;
	}

	/**
	 * 取期末日期
	 * 
	 * @param thedate
	 * @param period
	 *            <ul>
	 *            <li>M 月末
	 *            <li>Q 季末
	 *            <li>Y 年末
	 *            <li>H 半年末
	 *            <li>T 旬末
	 *            <li>m 上月末
	 *            <li>q 上季末
	 *            <li>y 上年末
	 *            <li>h 上半年末
	 *            <li>t 上一旬末
	 *            </ul>
	 * @return
	 */
	public static java.util.Date getPeriodEnd(java.util.Date thedate, char period) {
		Calendar cal = getCalendarInstance();
		cal.setTime(thedate);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);// 1月=0,12月=11
		// int week = cal.get(Calendar.DAY_OF_WEEK);
		switch (period) {
		case 'M':
			// cal.add(Calendar.MONTH, 1);// to next month
			// day = cal.get(Calendar.DAY_OF_MONTH);
			// cal.add(Calendar.DAY_OF_MONTH, 0 - day);// to last month end
			int monthendday = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			cal.set(Calendar.DATE, monthendday);
			break;
		case 'Q':
			cal.add(Calendar.MONTH, 3 - month % 3);// to next quarter
			// first month
			day = cal.get(Calendar.DAY_OF_MONTH);
			cal.add(Calendar.DAY_OF_MONTH, 0 - day);// to last month end
			break;
		case 'Y':
			cal.add(Calendar.MONTH, 12 - month);// to next year first month
			day = cal.get(Calendar.DAY_OF_MONTH);
			cal.add(Calendar.DAY_OF_MONTH, 0 - day);// to last month end
			break;
		case 'H':
			cal.add(Calendar.MONTH, 6 - (month) % 6);// to next halfyear
			// first month
			day = cal.get(Calendar.DAY_OF_MONTH);
			cal.add(Calendar.DAY_OF_MONTH, 0 - day);// to last month end
			break;
		case 'T':
			if (day < 11)
				cal.set(Calendar.DATE, 10);
			else if (day < 21)
				cal.set(Calendar.DATE, 20);
			else {
				cal.add(Calendar.MONTH, 1);// to next month
				day = cal.get(Calendar.DAY_OF_MONTH);
				cal.add(Calendar.DAY_OF_MONTH, 0 - day);// to last month end
			}
			break;
		case 'm':
			cal.add(Calendar.DAY_OF_MONTH, 0 - day);// to last month end
			break;
		case 'q':
			cal.add(Calendar.MONTH, 0 - (month) % 3);// to this quarter
			// first month
			day = cal.get(Calendar.DAY_OF_MONTH);
			cal.add(Calendar.DAY_OF_MONTH, 0 - day);// to last month end
			break;
		case 'y':
			cal.add(Calendar.MONTH, 0 - month);// to this year first month
			day = cal.get(Calendar.DAY_OF_MONTH);
			cal.add(Calendar.DAY_OF_MONTH, 0 - day);// to last month end
			break;
		case 'h':
			cal.add(Calendar.MONTH, 0 - (month) % 6);// to this halfyear
			// first month
			day = cal.get(Calendar.DAY_OF_MONTH);
			cal.add(Calendar.DAY_OF_MONTH, 0 - day);// to last month end
			break;
		case 't':
			if (day < 11)
				cal.add(Calendar.DAY_OF_MONTH, 0 - day);// to last month end
			else if (day < 21)
				cal.set(Calendar.DATE, 10);
			else {
				cal.set(Calendar.DATE, 20);
			}
			break;
		default:
			break;
		}
		return cal.getTime();
	}

	/**
	 * 取期末日期
	 * 
	 * @param thedate
	 * @param pattern
	 * @param period
	 *            <ul>
	 *            <li>M 月末
	 *            <li>Q 季末
	 *            <li>Y 年末
	 *            <li>H 半年末
	 *            <li>T 旬末
	 *            <li>m 上月末
	 *            <li>q 上季末
	 *            <li>y 上年末
	 *            <li>h 上半年末
	 *            <li>t 上一旬末
	 *            </ul>
	 * @return
	 */
	public static String getPeriodEnd(String thedate, String pattern, char period) {
		if (DateUtil.DATE_FORMAT_YMD.equals(pattern)) {
			switch (period) {
			case 'Y':
				return thedate.substring(0, 4) + "0101";
			}
		}
		java.util.Date tdate = Convert.toDate(thedate, pattern);
		tdate = getPeriodEnd(tdate, period);
		return formatDate(tdate, pattern);
	}

	/**
	 * 取期初日期
	 * 
	 * @param thedate
	 * @param period
	 *            <ul>
	 *            <li>M 月初
	 *            <li>Q 季初
	 *            <li>Y 年初
	 *            <li>H 半年初
	 *            <li>T 旬初
	 *            <li>m 上月初
	 *            <li>q 上季初
	 *            <li>y 上年初
	 *            <li>h 上一半年初
	 *            <li>t 上一旬初
	 *            </ul>
	 * @return
	 */
	public static java.util.Date getPeriodBegin(java.util.Date thedate, char period) {
		Calendar cal = getCalendarInstance();
		cal.setTime(thedate);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);// 1月=0,12月=11
		// int week = cal.get(Calendar.DAY_OF_WEEK);
		switch (period) {
		case 'M':
			cal.add(Calendar.DAY_OF_MONTH, 1 - day);// 到月初
			break;
		case 'Q':
			cal.add(Calendar.MONTH, 0 - (month - 1) % 3);// 到季初月份
			day = cal.get(Calendar.DAY_OF_MONTH);
			cal.add(Calendar.DAY_OF_MONTH, 1 - day);// 到月初
			break;
		case 'Y':
			cal.add(Calendar.MONTH, 1 - month);// 到年初月份
			day = cal.get(Calendar.DAY_OF_MONTH);
			cal.add(Calendar.DAY_OF_MONTH, 1 - day);// 到月初
			break;
		case 'H':
			cal.add(Calendar.MONTH, 0 - (month - 1) % 6);// 到半年初月份
			day = cal.get(Calendar.DAY_OF_MONTH);
			cal.add(Calendar.DAY_OF_MONTH, 1 - day);// 到月初
			break;
		case 'T':
			if (day < 11)
				cal.set(Calendar.DATE, 1);
			else if (day < 21)
				cal.set(Calendar.DATE, 11);
			else
				cal.set(Calendar.DATE, 21);
			break;
		case 'm':
			cal.add(Calendar.MONTH, -1);// 到上月份
			day = cal.get(Calendar.DAY_OF_MONTH);
			cal.add(Calendar.DAY_OF_MONTH, 1 - day);// 到月初
			break;
		case 'q':
			cal.add(Calendar.MONTH, -3 - (month - 1) % 3);// 到上季初月份
			day = cal.get(Calendar.DAY_OF_MONTH);
			cal.add(Calendar.DAY_OF_MONTH, 1 - day);// 到月初
			break;
		case 'y':
			cal.add(Calendar.MONTH, 1 - 12 - month);// 到上年初月份
			day = cal.get(Calendar.DAY_OF_MONTH);
			cal.add(Calendar.DAY_OF_MONTH, 1 - day);// 到月初
			break;
		case 'h':
			cal.add(Calendar.MONTH, -6 - (month - 1) % 6);// 到上一半年初月份
			day = cal.get(Calendar.DAY_OF_MONTH);
			cal.add(Calendar.DAY_OF_MONTH, 1 - day);// 到月初
			break;
		case 't':
			if (day < 11) {
				cal.add(Calendar.MONTH, -1);
				cal.set(Calendar.DATE, 21);
			} else if (day < 21)
				cal.set(Calendar.DATE, 1);
			else
				cal.set(Calendar.DATE, 11);
			break;
		default:
			break;
		}
		return cal.getTime();
	}

	/**
	 * 取期初日期
	 * 
	 * @param thedate
	 * @param pattern
	 * @param period
	 *            <ul>
	 *            <li>M 月初
	 *            <li>Q 季初
	 *            <li>Y 年初
	 *            <li>H 半年初
	 *            <li>T 旬初
	 *            <li>m 上月初
	 *            <li>q 上季初
	 *            <li>y 上年初
	 *            <li>h 上一半年初
	 *            <li>t 上一旬初
	 *            </ul>
	 * @return
	 */
	public static String getPeriodBegin(String thedate, String pattern, char period) {
		if (DateUtil.DATE_FORMAT_YMD.equals(pattern)) {
			switch (period) {
			case 'M':
				return thedate.substring(0, 6) + "01";
			case 'Y':
				return thedate.substring(0, 4) + "0101";
			}
		}
		java.util.Date tdate = Convert.toDate(thedate, pattern);
		tdate = getPeriodBegin(tdate, period);
		return formatDate(tdate, pattern);
	}

	/**
	 * Description: 根据Timestamp得到Time
	 * 
	 * @param _timesmp
	 * @return
	 */
	public static java.sql.Time getTimeByTimestamp(Timestamp _timesmp) {
		Calendar cal = getCalendarInstance();
		cal.setTimeInMillis(_timesmp.getTime());
		String str = String.valueOf(cal.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(cal.get(Calendar.MINUTE)) + ":"
				+ String.valueOf(cal.get(Calendar.SECOND));

		return java.sql.Time.valueOf(str);
	}

	public static Calendar getCalendarInstance() {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.SIMPLIFIED_CHINESE);
		return cal;

	}

	/**
	 * 按精度比较日期. <br>
	 * 
	 * @param date1
	 * @param date2
	 * @param precision
	 *            精度=整数： <br>
	 *            Calendar.YEAR <br>
	 *            Calendar.MONTH <br>
	 *            Calendar.DAY_OF_MONTH <br>
	 *            Calendar.MINUTE <br>
	 *            Calendar.SECOND <br>
	 *            Calendar.MILLISECOND
	 * @return the value 0 if the argument Date is equal to this Date; a value less than 0 if this Date is before the Date argument; and a value
	 *         greater than 0 if this Date is after the Date argument
	 */
	public static int compareDate(final Date date1, final Date date2, int precision) {
		Calendar c = Calendar.getInstance();

		List fields = new ArrayList();
		fields.add(new Integer(Calendar.YEAR));
		fields.add(new Integer(Calendar.MONTH));
		fields.add(new Integer(Calendar.DAY_OF_MONTH));
		fields.add(new Integer(Calendar.MINUTE));
		fields.add(new Integer(Calendar.SECOND));
		fields.add(new Integer(Calendar.MILLISECOND));

		Date d1 = date1;
		Date d2 = date2;
		if (fields.contains(new Integer(precision))) {
			c.setTime(date1);
			for (int i = 0; i < fields.size(); i++) {
				int field = ((Integer) fields.get(i)).intValue();
				if (field > precision)
					c.set(field, 0);
			}
			d1 = c.getTime();
			c.setTime(date2);
			for (int i = 0; i < fields.size(); i++) {
				int field = ((Integer) fields.get(i)).intValue();
				if (field > precision)
					c.set(field, 0);
			}
			d2 = c.getTime();
		}
		return d1.compareTo(d2);
	}

	public static int compareDate(final String date1, String d1df, final String date2, String d2df, int precision) {
		return compareDate(Convert.toDate(date1, d1df), Convert.toDate(date2, d2df), precision);
	}

	public static int compareDate(final String date1, final String date2, int precision) {
		return compareDate(Convert.toDate(date1), Convert.toDate(date2), precision);
	}

	public static int compareDate(final String date1, final String date2) {
		return compareDate(Convert.toDate(date1), Convert.toDate(date2), 0);
	}

	public static Calendar convertTimeZone(Calendar cal, String zone) {
		Calendar ret = Calendar.getInstance();
		TimeZone toZone = TimeZone.getTimeZone(zone);
		long curmillis = cal.getTimeInMillis();
		ret.setTimeInMillis(curmillis + toZone.getRawOffset() - cal.getTimeZone().getRawOffset());

		return ret;
	}

	/**
	 * 在某个日期加指定的年或月或天
	 * 
	 * @param precision
	 *            -只能Calendar.YEAR/Calendar.MONTH/Calendar.DAY_OF_YEAR/Calendar.DAY_OF_MONTH否则返回null
	 * @param amount
	 * @param d1
	 * @return
	 */
	public static Date dateAdd(int precision, int amount, Date d1) {
		if( d1 == null ) return null;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(d1.getTime());
		cal.add(precision, amount);
		return cal.getTime();
	}

	public static String dateAdd(int precision, int amount, String d1, String format) {
		return formatDate(dateAdd(precision, amount, Convert.toDate(d1, format)), format);
	}

	public static String dateAdd(int precision, int amount, String d1) {
		return formatDate(dateAdd(precision, amount, Convert.toDate(d1)));
	}

}