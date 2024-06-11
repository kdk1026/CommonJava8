package common.util.date;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.ibm.icu.util.ChineseCalendar;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 13. 김대광	너무 옛날거이기도 하고... 뭔가 좋은 방법이 따로 있을 듯... 고로... Java 6 버전에 맞춰진 상태로 유지... 세상에 Calendar 라니...
 * </pre>
 *
 * 윤달인 경우, 석거탄신일이 하루 전...
 * @author 김대광
 * @Description Apache Commons Net 필요
 * 	- 공휴일 관련
 */
public class Icu4JHolidayUtil {

	private Icu4JHolidayUtil() {
		super();
	}

	/**
	 * 양력 공휴일
	 */
	private static final String[][] solaHoliday = {
		{"0101", "신정"},
		{"0301", "삼일절"},
		{"0505", "어린이날"},
		{"0606", "현충일"},
		{"0815", "광복절"},
		{"1003", "개천절"},
		{"1009", "한글날"},
		{"1225", "크리스마스"}
	};

	/**
	 * 음력 공휴일
	 */
	private static final String[][] lunarHoliday = {
		{"0101", "설날"},
		{"0102", "설날 연휴"},
		{"0408", "석가탄신일"},
		{"0814", "추석 연휴"},
		{"0815", "추석"},
		{"0816", "추석 연휴"}
	};

	/**
	 * 해당 일자가 공휴일인지 체크하여 공휴일 명칭 반환
	 * @param yyyyMMdd
	 * @return
	 */
	public static String getHoliday(String yyyyMMdd) {
		String sHoli1 = "";
		String sHoli2 = "";
		String sHoli3 = "";

		// 양력 공휴일 체크
		sHoli1 = getHolidaySola(yyyyMMdd);

		if ( !"".equals(sHoli1) ) {
			return sHoli1;
		} else {
			// 음력 공휴일 체크
			sHoli2 = getHolidayLunar(yyyyMMdd);
		}

		if ( !"".equals(sHoli2) ) {
			return sHoli2;
		} else {
			// XXX : 수작업 확인 ?
			// 대체 공휴일 체크
			sHoli3 = getHolidayAlternate(yyyyMMdd);
		}

		if ( !"".equals(sHoli3) ) {
			return sHoli3;
		}

		return "";
	}

	/**
	 * 해당 일자가 양력 공휴일인지 체크하여 공휴일 명칭 반환
	 * @param yyyyMMdd
	 * @return
	 */
	private static String getHolidaySola(String yyyyMMdd) {
		String resStr = "";
		String sMMdd = yyyyMMdd.substring(4);

		// 양력 공휴일 체크
		for (int i=0; i < solaHoliday.length; i++) {
			for (int j=0; j < solaHoliday[i].length; j++) {
				if (solaHoliday[i][j].equals(sMMdd)) {
					resStr = solaHoliday[i][j+1];
				}
			}
		}
		return resStr;
	}

	/**
	 * 해당 일자가 음력 공휴일인지 체크하여 공휴일 명칭 반환
	 * @param yyyyMMdd
	 * @return
	 */
	private static String getHolidayLunar(String yyyyMMdd) {
		String resStr = "";
		String sMMdd = convertSolarToLunar(yyyyMMdd).substring(4);

		for (int i=0; i < lunarHoliday.length; i++) {
			for (int j=0; j < lunarHoliday[i].length; j++) {
				if (lunarHoliday[i][j].equals(sMMdd)) {
					resStr = lunarHoliday[i][j+1];
				}
			}
		}

		if ( (resStr == null) || (resStr.trim().length() == 0) ) {
			resStr = getNewYearLunar(yyyyMMdd);
		}
		return resStr;
	}

	/**
	 * 음력 12월의 마지막날 (설날 연휴1)이면 "설날 연휴" 반환
	 * @param chinaCalMM
	 * @param chinaCalDD
	 * @return
	 */
	private static String getNewYearLunar(String yyyyMMdd) {
		String resStr = "";
		Calendar cal = Calendar.getInstance();
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

		cal.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
        cal.set(Calendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6)));

        chinaCal.setTimeInMillis(cal.getTimeInMillis());

        int chinaCalMM = chinaCal.get(ChineseCalendar.MONTH) + 1;
        int chinaCalDD = chinaCal.get(ChineseCalendar.DAY_OF_MONTH);

        if (chinaCalMM == 12) {
            int lastDD = chinaCal.getActualMaximum(ChineseCalendar.DAY_OF_MONTH);
            if (chinaCalDD == lastDD) {
            	resStr = "설날 연휴";
            }
        }
		return resStr;
	}

	/**
	 * 양력 일자를 음력 일자로 반환
	 * @param yyyyMMdd
	 * @return
	 */
	private static String convertSolarToLunar(String yyyyMMdd) {
		StringBuilder sb = new StringBuilder();
		Calendar cal = Calendar.getInstance();
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

		cal.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
		cal.set(Calendar.MONTH, Integer.parseInt(date.substring(4, 6)) -1);
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6)));

		chinaCal.setTimeInMillis(cal.getTimeInMillis());

		int chinaCalYY = chinaCal.get(ChineseCalendar.EXTENDED_YEAR) - 2637;
        int chinaCalMM = chinaCal.get(ChineseCalendar.MONTH) + 1;
        int chinaCalDD = chinaCal.get(ChineseCalendar.DAY_OF_MONTH);

        if (chinaCalYY < 1000) sb.append("0");
        else if (chinaCalYY < 100) sb.append("00");
        else if (chinaCalYY < 10) sb.append("000");
        sb.append(chinaCalYY);

        if (chinaCalMM < 10) sb.append("0");
        sb.append(chinaCalMM);

        if (chinaCalDD < 10) sb.append("0");
        sb.append(chinaCalDD);

        return sb.toString();
	}

	/**
	 * 음력 일자를 양력 일자로 변환
	 * @param yyyyMMdd
	 * @return
	 */
	private static String convertLunarToSolar(String yyyyMMdd) {
		StringBuilder sb = new StringBuilder();
		Calendar cal = Calendar.getInstance();
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

		chinaCal.set(ChineseCalendar.EXTENDED_YEAR, Integer.parseInt(date.substring(0, 4)) + 2637);
		chinaCal.set(ChineseCalendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
		chinaCal.set(ChineseCalendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6)));

		cal.setTimeInMillis(chinaCal.getTimeInMillis());

		int nYY = cal.get(Calendar.YEAR);
		int nMM = cal.get(Calendar.MONTH) + 1;
		int nDD = cal.get(Calendar.DAY_OF_MONTH);

		if (nYY < 1000) sb.append("0");
		else if (nYY < 100) sb.append("00");
		else if (nYY < 10) sb.append("000");
		sb.append(nYY);

		if (nMM < 10) sb.append("0");
		sb.append(nMM);

		if (nDD < 10) sb.append("0");
		sb.append(nDD);

		return sb.toString();
	}

	/**
	 * 해당 일자의 요일 반환
	 * @param yyyyMMdd
	 * @return
	 */
	private static int getDayOfWeek(String yyyyMMdd) {
		int iYY = Integer.parseInt(yyyyMMdd.substring(0, 4));
		int iMM = Integer.parseInt(yyyyMMdd.substring(4, 6)) -1;
		int iDD = Integer.parseInt(yyyyMMdd.substring(6));

		Calendar cal = Calendar.getInstance();
		cal.set(iYY, iMM, iDD);

		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 해당 일자가 대체 공휴일인지 체크하여 "대체" 반환<br/>
	 * 	- 대상 : 설날, 추석, 어린이날
	 * @param yyyyMMdd
	 * @return
	 */
	private static String getHolidayAlternate(String yyyyMMdd) {
		String resStr = "";
		int iYY = Integer.parseInt(yyyyMMdd.substring(0, 4));

		/* 설날 */
		String dayFirst2 = convertLunarToSolar(iYY + "0101");
		String dayFirst3 = convertLunarToSolar(iYY + "0102");
		String dayFirst1 = String.valueOf(Integer.parseInt(dayFirst2) - 1);

		/* 추석 */
		String dayThanks1 = convertLunarToSolar(iYY + "0814");
		String dayThanks2 = convertLunarToSolar(iYY + "0815");
		String dayThanks3 = convertLunarToSolar(iYY + "0816");

		/* 어린이날 */
		String dayChild = iYY + "0505";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		// 해당 년도의 대체휴일 목록
		List<String> substituteHolidayList = new ArrayList<String>();
		Calendar cal;
		int iSyy = 0;
		int iSmm = 0;
		int iSdd = 0;

		// 설날
		if (getDayOfWeek(dayFirst1) == Calendar.SUNDAY || getDayOfWeek(dayFirst2) == Calendar.SUNDAY || getDayOfWeek(dayFirst3) == Calendar.SUNDAY
				|| ( !"".equals(getHolidaySola(dayFirst1))) || ( !"".equals(getHolidaySola(dayFirst2))) || ( !"".equals(getHolidaySola(dayFirst3)))) {

			iSyy = Integer.parseInt(dayFirst3.substring(0, 4));
			iSmm = Integer.parseInt(dayFirst3.substring(4, 6)) - 1;
			iSdd = Integer.parseInt(dayFirst3.substring(6)) + 1;

			cal = Calendar.getInstance();
			cal.set(iSyy, iSmm, iSdd);
			substituteHolidayList.add(sdf.format(cal.getTime()));
		}

		// 추석
		if (getDayOfWeek(dayThanks1) == Calendar.SUNDAY || getDayOfWeek(dayThanks2) == Calendar.SUNDAY || getDayOfWeek(dayThanks3) == Calendar.SUNDAY
				|| ( !"".equals(getHolidaySola(dayThanks1))) || ( !"".equals(getHolidaySola(dayThanks2))) || ( !"".equals(getHolidaySola(dayThanks3)))) {

			iSyy = Integer.parseInt(dayThanks3.substring(0, 4));
			iSmm = Integer.parseInt(dayThanks3.substring(4, 6)) - 1;
			iSdd = Integer.parseInt(dayThanks3.substring(6)) + 1;

			cal = Calendar.getInstance();
			cal.set(iSyy, iSmm, iSdd);
			substituteHolidayList.add(sdf.format(cal.getTime()));
		}

		// 어린이날
		int childWeek = getDayOfWeek(dayChild);
		if(childWeek != 0) {
			iSyy = Integer.parseInt(dayChild.substring(0, 4));
			iSmm = Integer.parseInt(dayChild.substring(4, 6)) - 1;

			if (childWeek == Calendar.SATURDAY) {
				iSdd = Integer.parseInt(dayChild.substring(6)) + 2;
			}
			if (childWeek == Calendar.SUNDAY) {
				iSdd = Integer.parseInt(dayChild.substring(6)) + 1;
			}

			cal = Calendar.getInstance();
			cal.set(iSyy, iSmm, iSdd);
			substituteHolidayList.add(sdf.format(cal.getTime()));
		}

		resStr = (substituteHolidayList.contains(yyyyMMdd)) ? "대체 휴일" : "";
		return resStr;
	}

}