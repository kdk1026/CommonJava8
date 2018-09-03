package common.libTest.commons.lang;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

public class UsageDateUtils {

	//----------------------------------------------------
	// 현재 날짜 및 시간 반환
	//----------------------------------------------------

	/**
	 * 현재 날짜를 yyyyMMdd 형식의 String 타입으로 반환
	 * @return
	 */
	public static String getTodayString() {
		return DateFormatUtils.format(new Date(), "yyyyMMdd");
	}

	/**
	 * 현재 날짜를 해당 포맷의 String 타입로 반환
	 * @param dateFormat
	 * @return
	 */
	public static String getTodayString(String dateFormat) {
		return DateFormatUtils.format(new Date(), dateFormat);
	}
	
	/**
	 * 현재 시간을 HHmmss 형식의 String 타입으로 반환
	 * @return
	 */
	public static String getCurrentTime() {
		return DateFormatUtils.format(new Date(), "HHmmss");
	}

	//----------------------------------------------------
	// String 타입 형식의 포맷 변환
	//----------------------------------------------------

	/**
	 * yyyyMMdd 형식의 String 타입을 해당 포맷의 String 타입으로 반환
	 * @param strDate
	 * @param dateFormat
	 * @return
	 */
	public static String getStringDate(String strDate, String dateFormat) {
		Date date = null;
		try {
			date = DateUtils.parseDate(strDate, "yyyyMMdd");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return DateFormatUtils.format(date, dateFormat);
	}

	//----------------------------------------------------
	// 타입 변환
	//----------------------------------------------------

	/**
	 * yyyyMMdd(HHmmss) 형식의 String 타입을 Date 타입으로 반환<br/>
	 * @param strDate
	 * @return
	 */
	public static Date getStringToDate(String strDate) {
		Date date = null;
		try {
			if (strDate.length() == 14) {
				date = DateUtils.parseDate(strDate, "yyyyMMddHHmmss");
			} else {
				date = DateUtils.parseDate(strDate, "yyyyMMdd");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * Date 타입 객체를 yyyyMMdd 형식의 String 타입으로 반환
	 * @param date
	 * @return
	 */
	public static String getDateToString(Date date) {
		return DateFormatUtils.format(date, "yyyyMMdd");
	}

	/**
	 * Date 타입 객체를 해당 포맷의 String 타입으로 반환
	 * @param date
	 * @param dateFormat
	 * @return
	 */
	public static String getDateToString(Date date, String dateFormat) {
		return DateFormatUtils.format(date, dateFormat);
	}

	//----------------------------------------------------
	// 이전/이후 날짜 반환
	//----------------------------------------------------

	/**
	 * 현재 날짜의 이전/이후 날짜를 yyyyMMdd 형식의 String 타입으로 반환<br/>
	 * 	- 인자 값이 음수 인 경우,이전 날짜 반환<br/>
	 * 	- 인자 값이 양수 인 경우, 이후 날짜 반환
	 * @param days
	 * @return
	 */
	public static String plusMinusDay(int days) {
		Date date = DateUtils.addDays(new Date(), days);
		return DateFormatUtils.format(date, "yyyyMMdd");
	}

	/**
	 * yyyyMMdd 형식의 String 타입 날짜의 이전/이후 날짜를 yyyyMMdd 형식의 String 타입으로 반환<br/>
	 * 	- 인자 값이 음수 인 경우,이전 날짜 반환<br/>
	 * 	- 인자 값이 양수 인 경우, 이후 날짜 반환
	 * @param strDate
	 * @param days
	 * @return
	 */
	public static String plusMinusDay(String strDate, int days) {
		Date date = null;
		try {
			date = DateUtils.parseDate(strDate, "yyyyMMdd");
			date = DateUtils.addDays(date, days);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return DateFormatUtils.format(date, "yyyyMMdd");
	}

	/**
	 * 현재 날짜의 이전/이후 날짜를 yyyyMMdd 형식의 String 타입으로 반환<br/>
	 * 	- 인자 값이 음수 인 경우,이전 날짜 반환<br/>
	 * 	- 인자 값이 양수 인 경우, 이후 날짜 반환
	 * @param months
	 * @return
	 */
	public static String plusMinusMonth(int months) {
		Date date = DateUtils.addMonths(new Date(), months);
		return DateFormatUtils.format(date, "yyyyMMdd");
	}

	/**
	 * yyyyMMdd 형식의 String 타입 날짜의 이전/이후 날짜를 yyyyMMdd 형식의 String 타입으로 반환<br/>
	 * 	- 인자 값이 음수 인 경우,이전 날짜 반환<br/>
	 * 	- 인자 값이 양수 인 경우, 이후 날짜 반환
	 * @param strDate
	 * @param months
	 * @return
	 */
	public static String plusMinusMonth(String strDate, int months) {
		Date date = null;
		try {
			date = DateUtils.parseDate(strDate, "yyyyMMdd");
			date = DateUtils.addMonths(date, months);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return DateFormatUtils.format(date, "yyyyMMdd");
	}

	/**
	 * 현재 날짜의 이전/이후 날짜를 yyyyMMdd 형식의 String 타입으로 반환<br/>
	 * 	- 인자 값이 음수 인 경우,이전 날짜 반환<br/>
	 * 	- 인자 값이 양수 인 경우, 이후 날짜 반환
	 * @param years
	 * @return
	 */
	public static String plusMinusYear(int years) {
		Date date = DateUtils.addYears(new Date(), years);
		return DateFormatUtils.format(date, "yyyyMMdd");
	}

	/**
	 * yyyyMMdd 형식의 String 타입 날짜의 이전/이후 날짜를 yyyyMMdd 형식의 String 타입으로 반환<br/>
	 * 	- 인자 값이 음수 인 경우,이전 날짜 반환<br/>
	 * 	- 인자 값이 양수 인 경우, 이후 날짜 반환
	 * @param strDate
	 * @param years
	 * @return
	 */
	public static String plusMinusYear(String strDate, int years) {
		Date date = null;
		try {
			date = DateUtils.parseDate(strDate, "yyyyMMdd");
			date = DateUtils.addYears(date, years);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return DateFormatUtils.format(date, "yyyyMMdd");
	}

	//----------------------------------------------------
	// 이전/이후 시각 반환
	//----------------------------------------------------

	/**
	 * 현재 날짜의 이전/이후 날짜를 yyyyMMddHHmmss 형식의 String 타입으로 반환<br/>
	 * 	- 인자 값이 음수 인 경우,이전 시각 반환<br/>
	 * 	- 인자 값이 양수 인 경우, 이후 시각 반환
	 * @param hours
	 * @return
	 */
	public static String plusMinusHour(int hours) {
		Date date = DateUtils.addHours(new Date(), hours);
		return DateFormatUtils.format(date, "yyyyMMddHHmmss");
	}

	/**
	 * yyyyMMddHHmmss 형식의 String 타입 날짜의 이전/이후 날짜를 yyyyMMddHHmmss 형식의 String 타입으로 반환<br/>
	 * 	- 인자 값이 음수 인 경우,이전 시각 반환<br/>
	 * 	- 인자 값이 양수 인 경우, 이후 시각 반환
	 * @param strDate
	 * @param hours
	 * @return
	 */
	public static String plusMinusHour(String strDate, int hours) {
		Date date = null;
		try {
			date = DateUtils.parseDate(strDate, "yyyyMMddHHmmss");
			date = DateUtils.addHours(date, hours);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return DateFormatUtils.format(date, "yyyyMMddHHmmss");
	}

	/**
	 * 현재 날짜의 이전/이후 날짜를 yyyyMMddHHmmss 형식의 String 타입으로 반환<br/>
	 * 	- 인자 값이 음수 인 경우,이전 시각 반환<br/>
	 * 	- 인자 값이 양수 인 경우, 이후 시각 반환
	 * @param minutes
	 * @return
	 */
	public static String plusMinusMinute(int minutes) {
		Date date = DateUtils.addMinutes(new Date(), minutes);
		return DateFormatUtils.format(date, "yyyyMMddHHmmss");
	}

	/**
	 * yyyyMMddHHmmss 형식의 String 타입 날짜의 이전/이후 날짜를 yyyyMMddHHmmss 형식의 String 타입으로 반환<br/>
	 * 	- 인자 값이 음수 인 경우,이전 날짜 반환<br/>
	 * 	- 인자 값이 양수 인 경우, 이후 날짜 반환
	 * @param strDate
	 * @param minutes
	 * @return
	 */
	public static String plusMinusMinute(String strDate, int minutes) {
		Date date = null;
		try {
			date = DateUtils.parseDate(strDate, "yyyyMMddHHmmss");
			date = DateUtils.addMinutes(date, minutes);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return DateFormatUtils.format(date, "yyyyMMddHHmmss");
	}

	/**
	 * 현재 날짜의 이전/이후 날짜를 yyyyMMddHHmmss 형식의 String 타입으로 반환<br/>
	 * 	- 인자 값이 음수 인 경우,이전 날짜 반환<br/>
	 * 	- 인자 값이 양수 인 경우, 이후 날짜 반환
	 * @param seconds
	 * @return
	 */
	public static String plusMinusSecond(int seconds) {
		Date date = DateUtils.addSeconds(new Date(), seconds);
		return DateFormatUtils.format(date, "yyyyMMddHHmmss");
	}

	/**
	 * yyyyMMddHHmmss 형식의 String 타입 날짜의 이전/이후 날짜를 yyyyMMddHHmmss 형식의 String 타입으로 반환<br/>
	 * 	- 인자 값이 음수 인 경우,이전 날짜 반환<br/>
	 * 	- 인자 값이 양수 인 경우, 이후 날짜 반환
	 * @param strDate
	 * @param seconds
	 * @return
	 */
	public static String plusMinusSecond(String strDate, int seconds) {
		Date date = null;
		try {
			date = DateUtils.parseDate(strDate, "yyyyMMddHHmmss");
			date = DateUtils.addSeconds(date, seconds);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return DateFormatUtils.format(date, "yyyyMMddHHmmss");
	}

}
