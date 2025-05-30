package common.util.date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ChineseCalendar;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2022. 8. 23. kdk	최초작성
 * </pre>
 *
 * 윤달인 경우, 석거탄신일이 하루 전...
 * @author kdk
 */
public class HolidayCalendar {

	private HolidayCalendar() {
		super();
	}

	private static final int LD_SUNDAY = 7;
	private static final int LD_SATURDAY = 6;
	private static final int LD_MONDAY = 1;

	private static class Valid {
		private static void isValidFormat(String yyyyMMdd) {
			try {
				LocalDate.parse(yyyyMMdd, DateTimeFormatter.ofPattern("yyyyMMdd"));
			} catch (DateTimeParseException e) {
				throw new IllegalArgumentException("yyyyMMdd is invalid format");
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("yyyyMMdd is null");
			}
		}

		private static void isValidYear(String year) {
			try {
				Year.parse(year);
			} catch (DateTimeParseException e) {
				throw new IllegalArgumentException("yyyy is invalid format");
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("yyyy is null");
			}
		}
	}

    /**
     * 음력 날짜를 양력 날짜로 변환
     * @param yyyyMMdd
     * @return
     */
	public static String lunar2Solar(String yyyyMMdd) {
		Valid.isValidFormat(yyyyMMdd);

    	ChineseCalendar chinaCal = new ChineseCalendar();

    	if (yyyyMMdd == null) return "";

		String date = yyyyMMdd.trim();

		if (date.length() != 8) {
			if (date.length() == 4) {
				date = date + "0101";
			} else if (date.length() == 6) {
				date = date + "01";
			} else if (date.length() > 8) {
				date = date.substring(0, 8);
			} else {
				return "";
			}
		}

		chinaCal.set(Calendar.EXTENDED_YEAR, Integer.parseInt(date.substring(0, 4)) + 2637);
		chinaCal.set(Calendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
		chinaCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6)));

		LocalDate solar = Instant.ofEpochMilli(chinaCal.getTimeInMillis()).atZone(ZoneId.of("UTC")).toLocalDate();

		int y = solar.getYear();
		int m = solar.getMonth().getValue();
		int d = solar.getDayOfMonth();

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%04d", y));
		sb.append(String.format("%02d", m));
		sb.append(String.format("%02d", d));

		return sb.toString();
    }

    private static String solarDays(String yyyy, String date) {
    	return lunar2Solar(yyyy + date).substring(4);
    }

    public static List<String> holidayArray(String yyyy) {
    	Valid.isValidYear(yyyy);

    	Set<String> holidaysSet = new HashSet<>();
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    	// 양력 휴일
    	holidaysSet.add(yyyy + "0101");	// 신정
    	holidaysSet.add(yyyy + "0301");	// 삼일절
    	holidaysSet.add(yyyy + "0505");	// 어린이날
    	holidaysSet.add(yyyy + "0606");	// 현충일
    	holidaysSet.add(yyyy + "0815");	// 광복절
    	holidaysSet.add(yyyy + "1003");	// 개천절
    	holidaysSet.add(yyyy + "1009");	// 한글날
    	holidaysSet.add(yyyy + "1225");	// 성탄절

    	// 음력 휴일
    	String prevSeol = LocalDate.parse(lunar2Solar(yyyy + "0101"), formatter).minusDays(1).toString().replace("-","");
    	holidaysSet.add(yyyy + prevSeol.substring(4));		// ""
    	holidaysSet.add(yyyy + solarDays(yyyy, "0101"));  	// 설날
    	holidaysSet.add(yyyy + solarDays(yyyy, "0102"));  	// ""
    	holidaysSet.add(yyyy + solarDays(yyyy, "0408"));  	// 석가탄신일
    	holidaysSet.add(yyyy + solarDays(yyyy, "0814"));  	// ""
    	holidaysSet.add(yyyy + solarDays(yyyy, "0815"));  	// 추석
    	holidaysSet.add(yyyy + solarDays(yyyy, "0816"));  	// ""

		// 어린이날 대체공휴일 검사
		int childDayChk = LocalDate.parse(yyyy + "0505", formatter).getDayOfWeek().getValue();
		if (childDayChk == LD_SUNDAY) {
			holidaysSet.add(yyyy + "0506");
		}
		if (childDayChk == LD_SATURDAY) {
			holidaysSet.add(yyyy + "0507");
		}

		// 설날 대체공휴일 검사
		if (LocalDate.parse(lunar2Solar(yyyy + "0101"), formatter).getDayOfWeek().getValue() == LD_SUNDAY) {
			holidaysSet.add(lunar2Solar(yyyy + "0103"));
		}
		if (LocalDate.parse(lunar2Solar(yyyy + "0101"), formatter).getDayOfWeek().getValue() == LD_MONDAY) {
			holidaysSet.add(lunar2Solar(yyyy + "0103"));
		}
		if (LocalDate.parse(lunar2Solar(yyyy + "0102"), formatter).getDayOfWeek().getValue() == LD_SUNDAY) {
			holidaysSet.add(lunar2Solar(yyyy + "0103"));
		}

		// 광복절 대체공휴일 검사
		int liberationDayChk = LocalDate.parse(yyyy + "0815", formatter).getDayOfWeek().getValue();
		if (liberationDayChk == LD_SUNDAY) {
			holidaysSet.add(yyyy + "0816");
		}
		if (liberationDayChk == LD_SATURDAY) {
			holidaysSet.add(yyyy + "0817");
		}

		// 추석 대체공휴일 검사
		if (LocalDate.parse(lunar2Solar(yyyy + "0814"), formatter).getDayOfWeek().getValue() == LD_SUNDAY) {
			holidaysSet.add(lunar2Solar(yyyy + "0817"));
		}
		if (LocalDate.parse(lunar2Solar(yyyy + "0815"), formatter).getDayOfWeek().getValue() == LD_SUNDAY) {
			holidaysSet.add(lunar2Solar(yyyy + "0817"));
		}
		if (LocalDate.parse(lunar2Solar(yyyy + "0816"), formatter).getDayOfWeek().getValue() == LD_SUNDAY) {
			holidaysSet.add(lunar2Solar(yyyy + "0817"));
		}

		// 개전철 대체공휴일 검사
		int nationalFoundationDayChk = LocalDate.parse(yyyy + "1003", formatter).getDayOfWeek().getValue();
		if (nationalFoundationDayChk == LD_SUNDAY) {
			holidaysSet.add(yyyy + "1004");
		}
		if (nationalFoundationDayChk == LD_SATURDAY) {
			holidaysSet.add(yyyy + "1005");
		}

		// 한글날 대체공휴일 검사
		int hangulDayChk = LocalDate.parse(yyyy + "1009", formatter).getDayOfWeek().getValue();
		if (hangulDayChk == LD_SUNDAY) {
			holidaysSet.add(yyyy + "1010");
		}
		if (hangulDayChk == LD_SATURDAY) {
			holidaysSet.add(yyyy + "1011");
		}

    	List<String> holidaysList = new ArrayList<>(holidaysSet);
    	Collections.sort(holidaysList);

    	return holidaysList;
    }

}
