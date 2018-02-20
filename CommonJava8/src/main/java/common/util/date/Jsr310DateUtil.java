package common.util.date;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Locale;

public class Jsr310DateUtil {

	private Jsr310DateUtil() {
		super();
	}
	
	private static final String YYYYMMDD = "yyyyMMdd";
	private static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	
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
			return LocalDateTime.now().format(DateTimeFormatter.ofPattern(YYYYMMDD));
		}		
		
		/**
		 * 현재 날짜를 해당 포맷의 String 타입로 반환
		 * @param dateFormat
		 * @return
		 */
		public static String getTodayString(String dateFormat) {
			return LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateFormat));
		}
		
		/**
		 * 현재 시간을 HHmmss 형식의 String 타입으로 반환
		 * @return
		 */
		public static String getCurrentTime() {
			return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
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
			LocalDate localDate = LocalDate.parse(strDate, DateTimeFormatter.ofPattern(YYYYMMDD));
			return localDate.format(DateTimeFormatter.ofPattern(dateFormat));
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
				LocalDateTime localDateTime = LocalDateTime.parse(strDate, DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS));
				date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
			} else {
				LocalDate localDate = LocalDate.parse(strDate, DateTimeFormatter.ofPattern(YYYYMMDD));
				date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			}
			return date;
		}

		/**
		 * Date 타입 객체를 yyyyMMdd 형식의 String 타입으로 반환
		 * @param date
		 * @return
		 */
		public static String getDateToString(Date date) {
			LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			return localDate.format(DateTimeFormatter.ofPattern(YYYYMMDD));
		}

		/**
		 * Date 타입 객체를 해당 포맷의 String 타입으로 반환
		 * @param date
		 * @param dateFormat
		 * @return
		 */
		public static String getDateToString(Date date, String dateFormat) {
			LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			return localDate.format(DateTimeFormatter.ofPattern(dateFormat));
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
			LocalDate localDate = LocalDate.now();
			if (days > 0) {
				strDateRes = localDate.plusDays(days).format(DateTimeFormatter.ofPattern(YYYYMMDD));
			} else {
				strDateRes = localDate.minusDays((days*-1)).format(DateTimeFormatter.ofPattern(YYYYMMDD));
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
			LocalDate localDate = LocalDate.parse(strDate, DateTimeFormatter.ofPattern(YYYYMMDD));
			if (days > 0) {
				strDateRes = localDate.plusDays(days).format(DateTimeFormatter.ofPattern(YYYYMMDD));
			} else {
				strDateRes = localDate.minusDays((days*-1)).format(DateTimeFormatter.ofPattern(YYYYMMDD));
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
			LocalDate localDate = LocalDate.now();
			if (months > 0) {
				strDateRes = localDate.plusMonths(months).format(DateTimeFormatter.ofPattern(YYYYMMDD));
			} else {
				strDateRes = localDate.minusMonths((months*-1)).format(DateTimeFormatter.ofPattern(YYYYMMDD));
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
			LocalDate localDate = LocalDate.parse(strDate, DateTimeFormatter.ofPattern(YYYYMMDD));
			if (months > 0) {
				strDateRes = localDate.plusMonths(months).format(DateTimeFormatter.ofPattern(YYYYMMDD));
			} else {
				strDateRes = localDate.minusMonths((months*-1)).format(DateTimeFormatter.ofPattern(YYYYMMDD));
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
			LocalDate localDate = LocalDate.now();
			if (years > 0) {
				strDateRes = localDate.plusYears(years).format(DateTimeFormatter.ofPattern(YYYYMMDD));
			} else {
				strDateRes = localDate.minusYears((years*-1)).format(DateTimeFormatter.ofPattern(YYYYMMDD));
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
			LocalDate localDate = LocalDate.parse(strDate, DateTimeFormatter.ofPattern(YYYYMMDD));
			if (years > 0) {
				strDateRes = localDate.plusYears(years).format(DateTimeFormatter.ofPattern(YYYYMMDD));
			} else {
				strDateRes = localDate.minusYears((years*-1)).format(DateTimeFormatter.ofPattern(YYYYMMDD));
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
			LocalDateTime localDateTime = LocalDateTime.now();
			if (hours > 0) {
				strDateRes = localDateTime.plusHours(hours).format(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS));
			} else {
				strDateRes = localDateTime.minusHours((hours*-1)).format(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS));
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
			LocalDateTime localDateTime = LocalDateTime.parse(strDate, DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS));
			if (hours > 0) {
				strDateRes = localDateTime.plusHours(hours).format(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS));
			} else {
				strDateRes = localDateTime.minusHours((hours*-1)).format(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS));
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
			LocalDateTime localDateTime = LocalDateTime.now();
			if (minutes > 0) {
				strDateRes = localDateTime.plusMinutes(minutes).format(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS));
			} else {
				strDateRes = localDateTime.minusMinutes((minutes*-1)).format(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS));
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
			LocalDateTime localDateTime = LocalDateTime.parse(strDate, DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS));
			if (minutes > 0) {
				strDateRes = localDateTime.plusMinutes(minutes).format(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS));
			} else {
				strDateRes = localDateTime.minusMinutes((minutes*-1)).format(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS));
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
			LocalDateTime localDateTime = LocalDateTime.now();
			if (seconds > 0) {
				strDateRes = localDateTime.plusSeconds(seconds).format(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS));
			} else {
				strDateRes = localDateTime.minusSeconds((seconds*-1)).format(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS));
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
			LocalDate fixDate = LocalDate.parse(strFixDate, DateTimeFormatter.ofPattern(YYYYMMDD));
			LocalDate targetDate = LocalDate.now();
			return fixDate.until(targetDate).getYears();
		}
		
		/**
		 * 현재 날짜와 월 간격 구하기
		 * 	- 0:같다, 양수:크다, 음수:작다
		 * @param strFixDate (yyyyMMdd)
		 * @return
		 */
		public static int intervalMonths(String strFixDate) {
			LocalDate fixDate = LocalDate.parse(strFixDate, DateTimeFormatter.ofPattern(YYYYMMDD));
			LocalDate targetDate = LocalDate.now();
			return fixDate.until(targetDate).getMonths();
		}
		
		/**
		 * 현재 날짜와 일자 간격 구하기
		 * 	- 0:같다, 양수:크다, 음수:작다
		 * @param strFixDate (yyyyMMdd)
		 * @return
		 */
		public static int intervalDays(String strFixDate) {
			LocalDate fixDate = LocalDate.parse(strFixDate, DateTimeFormatter.ofPattern(YYYYMMDD));
			LocalDate targetDate = LocalDate.now();
			return fixDate.until(targetDate).getDays();
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
			LocalDateTime fixTime = LocalDateTime.parse(strFixDate, DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS));
			LocalDateTime targetTime = LocalDateTime.now();
			return (int) fixTime.until(targetTime, ChronoUnit.HOURS);
		}
		
		/**
		 * 현재 날짜와 분 간격 구하기
		 * 	- 0:같다, 양수:크다, 음수:작다
		 * @param strFixDate (yyyyMMddHHmmss)
		 * @return
		 */
		public static int intervalMinutes(String strFixDate) {
			LocalDateTime fixTime = LocalDateTime.parse(strFixDate, DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS));
			LocalDateTime targetTime = LocalDateTime.now();
			return (int) fixTime.until(targetTime, ChronoUnit.MINUTES);
		}
		
		/**
		 * 현재 날짜와 초 간격 구하기
		 * 	- 0:같다, 양수:크다, 음수:작다
		 * @param strFixDate (yyyyMMddHHmmss)
		 * @return
		 */
		public static int intervalSeconds(String strFixDate) {
			LocalDateTime fixTime = LocalDateTime.parse(strFixDate, DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS));
			LocalDateTime targetTime = LocalDateTime.now();
			return (int) fixTime.until(targetTime, ChronoUnit.SECONDS);
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
			return LocalDate.now().getDayOfWeek().getValue();
		}
		
		/**
		 * yyyyMMdd 형식의 String 타입 날짜의 요일 구하기
		 * @param strDate
		 * @return
		 */
		public static int getDayOfWeek(String strDate) {
			LocalDate localDate = LocalDate.parse(strDate, DateTimeFormatter.ofPattern(YYYYMMDD));
			return localDate.getDayOfWeek().getValue();
		}

		/**
		 * 현재 날짜의 1일의 요일 반환
		 * @return
		 */
		public static int getFirstDayOfWeek() {
			String strDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM01"));
			LocalDate localDate = LocalDate.parse(strDate, DateTimeFormatter.ofPattern(YYYYMMDD));
			return localDate.getDayOfWeek().getValue();
		}

		/**
		 * yyyyMMdd 형식의 String 타입에 해당하는 1일의 요일 반환
		 * @param strDate
		 * @return
		 */
		public static int getFirstDayOfWeek(String strDate) {
			LocalDate localDate = LocalDate.parse(strDate, DateTimeFormatter.ofPattern(YYYYMMDD));
			return localDate.withDayOfMonth(1).getDayOfWeek().getValue();
		}
		
		/**
		 * 현재 날짜의 로케일 요일 구하기
		 * @param locale
		 * @return
		 */
		public static String getDayOfWeekLocale(Locale locale) {
			LocalDate localDate = LocalDate.now();
			return localDate.format(DateTimeFormatter.ofPattern("E", locale));
		}

		/**
		 * yyyyMMdd 형식의 String 타입 날짜의 한글 요일 구하기
		 * @param strDate
		 * @param locale
		 * @return
		 */
		public static String getDayOfWeekLocale(String strDate, Locale locale) {
			LocalDate localDate = LocalDate.parse(strDate, DateTimeFormatter.ofPattern(YYYYMMDD));
			return localDate.format(DateTimeFormatter.ofPattern("E", locale));
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
			LocalDate localDate = LocalDate.now();
			return localDate.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
		}
		
		/**
		 * yyyyMMdd 형식의 String 타입에 해당하는 월의 마지막 일자를 반환
		 * @param strDate
		 * @return
		 */
		public static int getLastDayOfMonth(String strDate) {
			LocalDate localDate = LocalDate.parse(strDate, DateTimeFormatter.ofPattern(YYYYMMDD));
			return localDate.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
		}
	}
	
}
