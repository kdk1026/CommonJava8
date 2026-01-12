package common.util.date;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.focus_shift.jollyday.core.Holiday;
import de.focus_shift.jollyday.core.HolidayManager;
import de.focus_shift.jollyday.core.ManagerParameters;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2026. 1. 12. 김대광	최초작성
 * </pre>
 *
 * <pre>
 * JDK 11 이상에서만 동작
 *
 * 한국은 지원 안함, 중국도 지원 안함
 * </pre>
 *
 * @author 김대광
 */
public class GlobalHolidayUtil {

	private GlobalHolidayUtil() {
		super();
	}

	/**
	 * 지원하는 국가 코드 목록
	 * @return
	 */
	public static List<String> getSupportedCalendarCodes() {
		Set<String> calendarCode = HolidayManager.getSupportedCalendarCodes();
		return new ArrayList<>(calendarCode);
	}

	/**
	 * 글로벌 공휴일 목록 가져오기
	 * @param country (주요 국가 코드: 일본=JP, 미국=US, 영국=GB, 독일=DE, 프랑스=FR, 캐나다=CA, 홍콩=HK, 싱가포르=SG)
	 * @param year
	 * @return
	 */
	public static List<Holiday> getHolidayList(String country, int year) {
		HolidayManager manager = HolidayManager.getInstance(ManagerParameters.create(country));

		Set<Holiday> holidays = manager.getHolidays(Year.of(year));
		return new ArrayList<>(holidays);
	}

	public static boolean isHoliday(String country, String targetDay) {
		HolidayManager manager = HolidayManager.getInstance(ManagerParameters.create(country));

		LocalDate lunarNewYear = LocalDate.parse(targetDay, DateTimeFormatter.ofPattern("yyyyMMdd"));

		return manager.isHoliday(lunarNewYear);
	}

}
