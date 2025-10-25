package common.util.date;

import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author 김대광
 * @Description	JODA-TIME 필요
 * 	- 1.8 이하에서 사용
 * <pre>
 * 개정이력
 * -----------------------------------
 * 21.07.31	CalcDate 메소드 일부 추가
 * 21.08.13 plusMinusYear 포맷으로 처리를 안해놨네
 * </pre>
 */
public class JodaTimeDateUtil {

	private static final String YYYYMMDD = "yyyyMMdd";
	public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

	private JodaTimeDateUtil() {
		super();
	}

	/**
	 * 현재 날짜 및 시간 반환
	 */
	public static class Today {

		private Today() {
			super();
		}

		/**
		 * 현재 날짜를 yyyyMMdd 형식의 String 타입으로 반환
		 * @return
		 */
		public static String getTodayString() {
			return DateTime.now().toString(YYYYMMDD);
		}

		/**
		 * 현재 날짜를 해당 포맷의 String 타입로 반환
		 * @param dateFormat
		 * @return
		 */
		public static String getTodayString(String dateFormat) {
			return DateTime.now().toString(dateFormat);
		}

		/**
		 * 현재 시간을 HHmmss 형식의 String 타입으로 반환
		 * @return
		 */
		public static String getCurrentTime() {
			return DateTime.now().toString("HHmmss");
		}

		/**
		 * 현재 연도 반환
		 * @return
		 */
		public static int getYear() {
			return DateTime.now().getYear();
		}

		/**
		 * 현재 월 반환
		 * @return
		 */
		public static int getMonth() {
			return DateTime.now().getMonthOfYear();
		}

		/**
		 * 현재 일 반환
		 * @return
		 */
		public static int getDayOfMonth() {
			return DateTime.now().getDayOfMonth();
		}

		/**
		 * 현재 시간 반환
		 * @return
		 */
		public static int getHour() {
			return DateTime.now().getHourOfDay();
		}

		/**
		 * 현재 분 반환
		 * @return
		 */
		public static int getMinute() {
			return DateTime.now().getMinuteOfHour();
		}
	}

	/**
	 * String 타입 형식의 포맷 변환
	 */
	public static class StringFormat {

		private StringFormat() {
			super();
		}

		/**
		 * yyyyMMdd 형식의 String 타입을 해당 포맷의 String 타입으로 반환
		 * @param strDate
		 * @param dateFormat
		 * @return
		 */
		public static String getStringDate(String strDate, String dateFormat) {
			return DateTime.parse(strDate, DateTimeFormat.forPattern(YYYYMMDD)).toString(dateFormat);
		}

		/**
		 * yyyyMMddHHmmss 형식의 String 타입을 해당 포맷의 String 타입으로 반환
		 * @param strDate
		 * @param dateFormat
		 * @return
		 */
		public static String getStringDateTime(String strDate, String dateFormat) {
			return DateTime.parse(strDate, DateTimeFormat.forPattern(YYYYMMDDHHMMSS)).toString(dateFormat);
		}
	}

	/**
	 * 타입 변환
	 */
	public static class Convert {

		private Convert() {
			super();
		}

		/**
		 * yyyyMMdd(HHmmss) 형식의 String 타입을 Date 타입으로 반환
		 * @param strDate
		 * @return
		 */
		public static Date getStringToDate(String strDate) {
			Date date = null;
			if (strDate.length() == 14) {
				date = DateTime.parse(strDate, DateTimeFormat.forPattern(YYYYMMDDHHMMSS)).toDate();
			} else {
				date = DateTime.parse(strDate, DateTimeFormat.forPattern(YYYYMMDD)).toDate();
			}
			return date;
		}

		/**
		 * Date 타입 객체를 yyyyMMdd 형식의 String 타입으로 반환
		 * @param date
		 * @return
		 */
		public static String getDateToString(Date date) {
			return DateTimeFormat.forPattern(YYYYMMDD).print(date.getTime());
		}

		/**
		 * Date 타입 객체를 해당 포맷의 String 타입으로 반환
		 * @param date
		 * @param dateFormat
		 * @return
		 */
		public static String getDateToString(Date date, String dateFormat) {
			return DateTimeFormat.forPattern(dateFormat).print(date.getTime());
		}

		/**
		 * DateTime 타입 객체를 해당 포맷의 String 타입으로 반환
		 * @param dateTime
		 * @param dateFormat
		 * @return
		 */
		public static String getDateTimeToString(DateTime dateTime, String dateFormat) {
			return dateTime.toString(dateFormat);
		}
	}

	/**
	 * 이전/이후 날짜 반환
	 */
	public static class CalcDate {

		private CalcDate() {
			super();
		}

		/**
		 * 현재 날짜의 이전/이후 날짜를 yyyyMMdd 형식의 String 타입으로 반환<br/>
		 * 	- 인자 값이 음수 인 경우,이전 날짜 반환<br/>
		 * 	- 인자 값이 양수 인 경우, 이후 날짜 반환
		 * @param days
		 * @return
		 */
		public static String plusMinusDay(int days) {
			String strDateRes = "";
			if (days > 0) {
				strDateRes = DateTime.now().plusDays(days).toString(YYYYMMDD);
			} else {
				strDateRes = DateTime.now().minusDays(days*-1).toString(YYYYMMDD);
			}
			return strDateRes;
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
			String strDateRes = "";
			DateTimeFormatter formatter = DateTimeFormat.forPattern(YYYYMMDD);
			if (days > 0) {
				strDateRes = DateTime.parse(strDate, formatter).plusDays(days).toString(YYYYMMDD);
			} else {
				strDateRes = DateTime.parse(strDate, formatter).minusDays(days*-1).toString(YYYYMMDD);
			}
			return strDateRes;
		}

		/**
		 * 현재 날짜의 이전/이후 날짜를 yyyyMMdd 형식의 String 타입으로 반환<br/>
		 * 	- 인자 값이 음수 인 경우,이전 날짜 반환<br/>
		 * 	- 인자 값이 양수 인 경우, 이후 날짜 반환
		 * @param months
		 * @return
		 */
		public static String plusMinusMonth(int months) {
			String strDateRes = "";
			if (months > 0) {
				strDateRes = DateTime.now().plusMonths(months).toString(YYYYMMDD);
			} else {
				strDateRes = DateTime.now().minusMonths(months*-1).toString(YYYYMMDD);
			}
			return strDateRes;
		}
		/**
		 * 현재 날짜의 이전/이후 날짜를 해당 포맷 형식의 String 타입으로 반환<br/>
		 * 	- 인자 값이 음수 인 경우,이전 날짜 반환<br/>
		 * 	- 인자 값이 양수 인 경우, 이후 날짜 반환
		 * @param months
		 * @param dateFormat
		 * @return
		 */
		public static String plusMinusMonth(int months, String dateFormat) {
			String strDateRes = "";
			if (months > 0) {
				strDateRes = DateTime.now().plusMonths(months).toString(dateFormat);
			} else {
				strDateRes = DateTime.now().minusMonths(months*-1).toString(dateFormat);
			}
			return strDateRes;
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
			String strDateRes = "";
			DateTimeFormatter formatter = DateTimeFormat.forPattern(YYYYMMDD);
			if (months > 0) {
				strDateRes = DateTime.parse(strDate, formatter).plusDays(months).toString(YYYYMMDD);
			} else {
				strDateRes = DateTime.parse(strDate, formatter).minusDays(months*-1).toString(YYYYMMDD);
			}
			return strDateRes;
		}
		/**
		 * 해당 포맷 형식의 String 타입 날짜의 이전/이후 날짜를 해당 포맷 형식의 String 타입으로 반환<br/>
		 * 	- 인자 값이 음수 인 경우,이전 날짜 반환<br/>
		 * 	- 인자 값이 양수 인 경우, 이후 날짜 반환
		 * @param strDate
		 * @param months
		 * @param dateFormat
		 * @return
		 */
		public static String plusMinusMonth(String strDate, int months, String dateFormat) {
			String strDateRes = "";
			DateTimeFormatter formatter = DateTimeFormat.forPattern(dateFormat);
			if (months > 0) {
				strDateRes = DateTime.parse(strDate, formatter).plusDays(months).toString(dateFormat);
			} else {
				strDateRes = DateTime.parse(strDate, formatter).minusDays(months*-1).toString(dateFormat);
			}
			return strDateRes;
		}

		/**
		 * 현재 날짜의 이전/이후 날짜를 yyyyMMdd 형식의 String 타입으로 반환<br/>
		 * 	- 인자 값이 음수 인 경우,이전 날짜 반환<br/>
		 * 	- 인자 값이 양수 인 경우, 이후 날짜 반환
		 * @param years
		 * @return
		 */
		public static String plusMinusYear(int years) {
			String strDateRes = "";
			if (years > 0) {
				strDateRes = DateTime.now().plusYears(years).toString(YYYYMMDD);
			} else {
				strDateRes = DateTime.now().minusYears(years*-1).toString(YYYYMMDD);
			}
			return strDateRes;
		}
		/**
		 * 현재 날짜의 이전/이후 날짜를 해당 포맷 형식의 String 타입으로 반환<br/>
		 * 	- 인자 값이 음수 인 경우,이전 날짜 반환<br/>
		 * 	- 인자 값이 양수 인 경우, 이후 날짜 반환
		 * @param years
		 * @param dateForamt
		 * @return
		 */
		public static String plusMinusYear(int years, String dateForamt) {
			String strDateRes = "";
			if (years > 0) {
				strDateRes = DateTime.now().plusYears(years).toString(dateForamt);
			} else {
				strDateRes = DateTime.now().minusYears(years*-1).toString(dateForamt);
			}
			return strDateRes;
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
			String strDateRes = "";
			DateTimeFormatter formatter = DateTimeFormat.forPattern(YYYYMMDD);
			if (years > 0) {
				strDateRes = DateTime.parse(strDate, formatter).plusYears(years).toString(YYYYMMDD);
			} else {
				strDateRes = DateTime.parse(strDate, formatter).minusYears(years*-1).toString(YYYYMMDD);
			}
			return strDateRes;
		}
		/**
		 * 해당 포맷 형식의 String 타입 날짜의 이전/이후 날짜를 해당 포맷 형식의 String 타입으로 반환<br/>
		 * 	- 인자 값이 음수 인 경우,이전 날짜 반환<br/>
		 * 	- 인자 값이 양수 인 경우, 이후 날짜 반환
		 * @param strDate
		 * @param years
		 * @param dateForamt
		 * @return
		 */
		public static String plusMinusYear(String strDate, int years, String dateForamt) {
			String strDateRes = "";
			DateTimeFormatter formatter = DateTimeFormat.forPattern(dateForamt);
			if (years > 0) {
				strDateRes = DateTime.parse(strDate, formatter).plusYears(years).toString(dateForamt);
			} else {
				strDateRes = DateTime.parse(strDate, formatter).minusYears(years*-1).toString(dateForamt);
			}
			return strDateRes;
		}
	}

	/**
	 * 이전/이후 시간각반환
	 */
	public static class CalcTime {

		private CalcTime() {
			super();
		}

		/**
		 * 현재 날짜의 이전/이후 날짜를 yyyyMMdd 형식의 String 타입으로 반환<br/>
		 * 	- 인자 값이 음수 인 경우,이전 시간 반환<br/>
		 * 	- 인자 값이 양수 인 경우, 이후 시간 반환
		 * @param hours
		 * @return
		 */
		public static String plusMinusHour(int hours) {
			String strDateRes = "";
			if (hours > 0) {
				strDateRes = DateTime.now().plusHours(hours).toString(YYYYMMDDHHMMSS);
			} else {
				strDateRes = DateTime.now().minusHours(hours*-1).toString(YYYYMMDDHHMMSS);
			}
			return strDateRes;
		}

		/**
		 * yyyyMMddHHmmss 형식의 String 타입 날짜의 이전/이후 날짜를 yyyyMMddHHmmss 형식의 String 타입으로 반환<br/>
		 * 	- 인자 값이 음수 인 경우,이전 시간 반환<br/>
		 * 	- 인자 값이 양수 인 경우, 이후 시간 반환
		 * @param strDate
		 * @param hours
		 * @return
		 */
		public static String plusMinusHour(String strDate, int hours) {
			String strDateRes = "";
			DateTimeFormatter formatter = DateTimeFormat.forPattern(YYYYMMDDHHMMSS);
			if (hours > 0) {
				strDateRes = DateTime.parse(strDate, formatter).plusHours(hours).toString(YYYYMMDDHHMMSS);
			} else {
				strDateRes = DateTime.parse(strDate, formatter).minusHours(hours*-1).toString(YYYYMMDDHHMMSS);
			}
			return strDateRes;
		}

		/**
		 * 현재 날짜의 이전/이후 날짜를 yyyyMMddHHmmss 형식의 String 타입으로 반환<br/>
		 * 	- 인자 값이 음수 인 경우,이전 시간 반환<br/>
		 * 	- 인자 값이 양수 인 경우, 이후 시간 반환
		 * @param minutes
		 * @return
		 */
		public static String plusMinusMinute(int minutes) {
			String strDateRes = "";
			if (minutes > 0) {
				strDateRes = DateTime.now().plusMinutes(minutes).toString(YYYYMMDDHHMMSS);
			} else {
				strDateRes = DateTime.now().minusMinutes(minutes*-1).toString(YYYYMMDDHHMMSS);
			}
			return strDateRes;
		}

		/**
		 * yyyyMMddHHmmss 형식의 String 타입 날짜의 이전/이후 날짜를 yyyyMMddHHmmss 형식의 String 타입으로 반환<br/>
		 * 	- 인자 값이 음수 인 경우,이전 시간 반환<br/>
		 * 	- 인자 값이 양수 인 경우, 이후 시간 반환
		 * @param strDate
		 * @param minutes
		 * @return
		 */
		public static String plusMinusMinute(String strDate, int minutes) {
			String strDateRes = "";
			DateTimeFormatter formatter = DateTimeFormat.forPattern(YYYYMMDDHHMMSS);
			if (minutes > 0) {
				strDateRes = DateTime.parse(strDate, formatter).plusMinutes(minutes).toString(YYYYMMDDHHMMSS);
			} else {
				strDateRes = DateTime.parse(strDate, formatter).minusMinutes(minutes*-1).toString(YYYYMMDDHHMMSS);
			}
			return strDateRes;
		}

		/**
		 * 현재 날짜의 이전/이후 날짜를 yyyyMMddHHmmss 형식의 String 타입으로 반환<br/>
		 * 	- 인자 값이 음수 인 경우,이전 시간 반환<br/>
		 * 	- 인자 값이 양수 인 경우, 이후 시간 반환
		 * @param seconds
		 * @return
		 */
		public static String plusMinusSecond(int seconds) {
			String strDateRes = "";
			if (seconds > 0) {
				strDateRes = DateTime.now().plusSeconds(seconds).toString(YYYYMMDDHHMMSS);
			} else {
				strDateRes = DateTime.now().minusSeconds(seconds*-1).toString(YYYYMMDDHHMMSS);
			}
			return strDateRes;
		}
	}

	/**
	 * 기간 간격 구하기
	 */
	public static class GetDateInterval {

		private GetDateInterval() {
			super();
		}

		/**
		 * 현재 날짜와 년 간격 구하기
		 * 	- 0:같다, 양수:크다, 음수:작다
		 * @param strFixDate (yyyyMMdd)
		 * @return
		 */
		public static int intervalYears(String strFixDate) {
			DateTime fixDate = DateTime.parse(strFixDate, DateTimeFormat.forPattern(YYYYMMDD));
			DateTime targetDate = DateTime.now();
			return Years.yearsBetween(targetDate, fixDate).toPeriod().getYears();
		}

		/**
		 * 현재 날짜와 월 간격 구하기
		 * 	- 0:같다, 양수:크다, 음수:작다
		 * @param strFixDate (yyyyMMdd)
		 * @return
		 */
		public static int intervalMonths(String strFixDate) {
			DateTime fixDate = DateTime.parse(strFixDate, DateTimeFormat.forPattern(YYYYMMDD));
			DateTime targetDate = DateTime.now();
			return Months.monthsBetween(targetDate, fixDate).toPeriod().getMonths();
		}

		/**
		 * 현재 날짜와 일자 간격 구하기
		 * 	- 0:같다, 양수:크다, 음수:작다
		 * @param strFixDate (yyyyMMdd)
		 * @return
		 */
		public static int intervalDays(String strFixDate) {
			DateTime fixDate = DateTime.parse(strFixDate, DateTimeFormat.forPattern(YYYYMMDD));
			DateTime targetDate = DateTime.now();
			return Days.daysBetween(targetDate, fixDate).toPeriod().getDays();
		}
	}

	/**
	 * 시간 간격 구하기
	 */
	public static class GetTimeInterval {

		private GetTimeInterval() {
			super();
		}

		/**
		 * 현재 날짜와 시간 간격 구하기
		 * 	- 0:같다, 양수:크다, 음수:작다
		 * @param strFixDate (yyyyMMddHHmmss)
		 * @return
		 */
		public static int intervalHours(String strFixDate) {
			DateTime fixTime = DateTime.parse(strFixDate, DateTimeFormat.forPattern(YYYYMMDDHHMMSS));
			DateTime targetTime = DateTime.now();
			return Hours.hoursBetween(targetTime, fixTime).toPeriod().getHours();
		}

		/**
		 * 현재 날짜와 분 간격 구하기
		 * 	- 0:같다, 양수:크다, 음수:작다
		 * @param strFixDate (yyyyMMddHHmmss)
		 * @return
		 */
		public static int intervalMinutes(String strFixDate) {
			DateTime fixTime = DateTime.parse(strFixDate, DateTimeFormat.forPattern(YYYYMMDDHHMMSS));
			DateTime targetTime = DateTime.now();
			return Minutes.minutesBetween(targetTime, fixTime).toPeriod().getMinutes();
		}

		/**
		 * 현재 날짜와 초 간격 구하기
		 * 	- 0:같다, 양수:크다, 음수:작다
		 * @param strFixDate (yyyyMMddHHmmss)
		 * @return
		 */
		public static int intervalSeconds(String strFixDate) {
			DateTime fixTime = DateTime.parse(strFixDate, DateTimeFormat.forPattern(YYYYMMDDHHMMSS));
			DateTime targetTime = DateTime.now();
			return Seconds.secondsBetween(targetTime, fixTime).toPeriod().getSeconds();
		}
	}

	/**
	 * 요일 구하기
	 */
	public static class GetDayOfWeek {

		private GetDayOfWeek() {
			super();
		}

		/**
		 * 현재 날짜의 요일 구하기
		 * @param strDate
		 * @return
		 */
		public static int getDayOfWeek() {
			return DateTime.now().getDayOfWeek();
		}

		/**
		 * yyyyMMdd 형식의 String 타입 날짜의 요일 구하기
		 * @param strDate
		 * @return
		 */
		public static int getDayOfWeek(String strDate) {
			DateTime dt = DateTime.parse(strDate, DateTimeFormat.forPattern(YYYYMMDD));
			return dt.getDayOfWeek();
		}

		/**
		 * 현재 날짜의 1일의 요일 반환
		 * @return
		 */
		public static int getFirstDayOfWeek() {
			String strDate = DateTime.now().toString("yyyyMM01");
			DateTime dt = DateTime.parse(strDate, DateTimeFormat.forPattern(YYYYMMDD));
			return dt.getDayOfWeek();
		}

		/**
		 * yyyyMMdd 형식의 String 타입에 해당하는 1일의 요일 반환
		 * @param strDate
		 * @return
		 */
		public static int getFirstDayOfWeek(String strDate) {
			DateTime dt = DateTime.parse(strDate, DateTimeFormat.forPattern(YYYYMMDD));
			return dt.withDayOfMonth(1).getDayOfWeek();
		}

		/**
		 * 현재 날짜의 로케일 요일 구하기
		 * @param locale
		 * @return
		 */
		public static String getDayOfWeekLocale(Locale locale) {
			DateTime dt = DateTime.now();
			return DateTimeFormat.forPattern("E").withLocale(locale).print(dt);
		}

		/**
		 * yyyyMMdd 형식의 String 타입 날짜의 한글 요일 구하기
		 * @param strDate
		 * @param locale
		 * @return
		 */
		public static String getDayOfWeekLocale(String strDate, Locale locale) {
			DateTime dt = DateTime.parse(strDate, DateTimeFormat.forPattern(YYYYMMDD));
			return DateTimeFormat.forPattern("E").withLocale(locale).print(dt);
		}
	}

	/**
	 * 마지막 일자 반환
	 */
	public static class GetDayOfMonth {

		private GetDayOfMonth() {
			super();
		}

		/**
		 * 현재 날짜의 마지막 일자를 반환
		 * @return
		 */
		public static int getLastDayOfMonth() {
			return DateTime.now().dayOfMonth().getMaximumValue();
		}

		/**
		 * yyyyMMdd 형식의 String 타입에 해당하는 월의 마지막 일자를 반환
		 * @param strDate
		 * @return
		 */
		public static int getLastDayOfMonth(String strDate) {
			DateTime dt = DateTime.parse(strDate, DateTimeFormat.forPattern(YYYYMMDD));
			return dt.dayOfMonth().getMaximumValue();
		}
	}


	/**
	 * Unix Timestamp
	 */
	public static class UnixTimestamp {

		private UnixTimestamp() {
			super();
		}

		/**
		 * System.currentTimeMillis() 동일하나 ms에 미세한 차이 있음
		 * @return
		 */
		public static long currentMillis() {
			return new DateTime().getMillis();
		}

		/**
		 * milliseconds to String
		 * @param mills
		 * @return
		 */
		public static String millsToString(long mills) {
			DateTime dateTime = new DateTime(mills);
			return dateTime.toString(YYYYMMDDHHMMSS);
		}

		/**
		 * <pre>
		 * current Unix Timestamp
		 * https://www.epochconverter.com/
		 * </pre>
		 * @return
		 */
		public static long getUnixTimestamp() {
			return currentMillis() / 1000;
		}

		/**
		 * timestamp to String
		 * @param timestamp (sec)
		 * @return
		 */
		public static String timestampToString(long timestamp) {
			return millsToString(timestamp * 1000);
		}

	}

}
