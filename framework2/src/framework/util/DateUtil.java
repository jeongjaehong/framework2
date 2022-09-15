/*
 * @(#)DateUtil.java
 */
package framework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * ��¥���� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class DateUtil {

	/**
	 * ��¥���� üũ
	 * @param year : ��
	 * @param month : ��
	 * @param day : ��
	 * @return boolean : ��¥����
	 */
	public static boolean isDate(int year, int month, int day) {
		return (toDate(year, month, day) != null);
	}

	/**
	 * ��¥���� üũ
	 * @param dateStr : �����
	 * @return boolean : ��¥����
	 */
	public static boolean isDate(String dateStr) {
		return (toDate(dateStr) != null);
	}

	/**
	 * ��¥���� üũ
	 * @param dateStr : �����
	 * @param format : ��¥����(ex : yyyyMMdd, yyyy-MM-dd...)
	 * @return boolean : ��¥����
	 */
	public static boolean isDate(String dateStr, String format) {
		return (toDate(dateStr, format) != null);
	}

	/**
	 * ���ϴ� ��¥ Date ����
	 * @param year : ��
	 * @param month : ��
	 * @param day : ��
	 * @return Date : ���ϴ� ��¥�� Date��ü
	 */
	public static Date toDate(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setLenient(false);
		cal.set(year, month - 1, day);
		return cal.getTime();
	}

	/**
	 * ���ϴ� ��¥ Date ����
	 * @param dateStr : �����(yyyy-MM-dd)
	 * @return Date : ���ϴ� ��¥�� Date��ü
	 */
	public static Date toDate(String dateStr) {
		return toDate(dateStr.replaceAll("[-|/|.]", ""), "yyyyMMdd");
	}

	/**
	 * ���ϴ� ��¥ Date ����
	 * @param dateStr : �����
	 * @param format : ��¥����(ex : yyyyMMdd, yyyy-MM-dd...)
	 * @return Date : ���ϴ� ��¥�� Date��ü
	 */
	public static Date toDate(String dateStr, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setLenient(false);
		try {
			return sdf.parse(dateStr);
		} catch (ParseException pe) {
			return null;
		}
	}

	/**
	 * Date ��ü�� String���� ��ȯ(yyyy-MM-dd)
	 * @param date : ��¥��ü
	 * @return String : ��¥�� String ��ü(yyyy-MM-dd)
	 */
	public static String toString(Date date) {
		return toString(date, "yyyy-MM-dd");
	}

	/**
	 * Date ��ü�� String���� ��ȯ
	 * @param date : ��¥��ü
	 * @param format : ���ϴ� ��¥����(ex : yyyyMMdd, yyyy-MM-dd...)
	 * @return String : ��¥�� String ��ü
	 */
	public static String toString(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * �� ��¥ ������ ��
	 * @param startday : ������
	 * @param endday : ������
	 * @return �� ��¥ ������ ��
	 */
	public static long getDateDiff(Date startday, Date endday) {
		long diff = endday.getTime() - startday.getTime();
		return (diff / (1000 * 60 * 60 * 24));
	}
}