package common.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.StringUtils;

 /**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2026. 1. 12. 김대광	최초작성
 * </pre>
 *
 * <pre>
 * 만나이, 한국식 나이, 보험 나이 등을 계산하는 기능 제공
 * - 구식 Calendar 걷어냄
 * </pre>
 *
 * @author 김대광
 */
public class AgeUtil {

	private static final String YYYYMMDD = "yyyyMMdd";
	private static final String BIRTH_DAY = "birthDay";
	private static final String FIX_DAY = "fixDay";

	private AgeUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	/**
	 * 현재일을 기준으로 만나이 계산
	 * @param birthDay
	 * @return
	 */
	public static int getAge(String birthDay) {
		if ( StringUtils.isBlank(birthDay) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(BIRTH_DAY));
		}

		LocalDate birth = LocalDate.parse(birthDay, DateTimeFormatter.ofPattern(YYYYMMDD));
		LocalDate now = LocalDate.now();

		int age = now.getYear() - birth.getYear();

		if (birth.getDayOfYear() >= now.getDayOfYear()) {
			age = age -1;
		}

		return age;
	}

	/**
	 * 기준일을 기준으로 만나이 계산
	 * @param birthDay
	 * @param fixDay
	 * @return
	 */
	public static int getAge(String birthDay, String fixDay) {
		if ( StringUtils.isBlank(birthDay) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(BIRTH_DAY));
		}

		if ( StringUtils.isBlank(fixDay) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(FIX_DAY));
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYYMMDD);

		LocalDate birth = LocalDate.parse(birthDay, formatter);
		LocalDate fix = LocalDate.parse(fixDay, formatter);

		int age = fix.getYear() - birth.getYear();

		if (birth.getDayOfYear() >= fix.getDayOfYear()) {
			age = age -1;
		}

		return age;
	}

	/**
	 * 현재일을 기준으로 한국 나이 계산
	 * @param birthDay
	 * @return
	 */
	public static int getKoreanAge(String birthDay) {
		if ( StringUtils.isBlank(birthDay) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(BIRTH_DAY));
		}

		LocalDate birth = LocalDate.parse(birthDay, DateTimeFormatter.ofPattern(YYYYMMDD));
		LocalDate now = LocalDate.now();

		return now.getYear() - birth.getYear() + 1;
	}

	/**
	 * 기준일을 기준으로 한국 나이 계산
	 * @param birthDay
	 * @param fixDay
	 * @return
	 */
	public static int getKoreanAge(String birthDay, String fixDay) {
		if ( StringUtils.isBlank(birthDay) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(BIRTH_DAY));
		}

		if ( StringUtils.isBlank(fixDay) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(FIX_DAY));
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYYMMDD);

		LocalDate birth = LocalDate.parse(birthDay, formatter);
		LocalDate fix = LocalDate.parse(fixDay, formatter);

		return fix.getYear() - birth.getYear() + 1;
	}

	/**
	 * 현재일을 기준으로 보험나이 계산<br/>
	 * 	- 생년월일 기준으로 6개월 되는 날부터 한 살 더 올라간다.
	 * @param birthDay
	 * @return
	 */
	public static int getInsurAge(String birthDay) {
		if ( StringUtils.isBlank(birthDay) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(BIRTH_DAY));
		}

		LocalDate birth = LocalDate.parse(birthDay, DateTimeFormatter.ofPattern(YYYYMMDD));
		LocalDate now = LocalDate.now();
		LocalDate targetDate = now.minusMonths(6);

		// 만 나이 계산 (ChronoUnit.YEARS는 생일이 지났는지 여부를 자동으로 계산함)
		return (int) ChronoUnit.YEARS.between(birth, targetDate);
	}

	/**
	 * 기준일을 기준으로 보험나이 계산<br/>
	 * 	- 생년월일 기준으로 6개월 되는 날부터 한 살 더 올라간다.
	 * @param birthDay
	 * @return
	 */
	public static int getInsurAge(String birthDay, String fixDay) {
		if ( StringUtils.isBlank(birthDay) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(BIRTH_DAY));
		}

		if ( StringUtils.isBlank(fixDay) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(FIX_DAY));
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYYMMDD);

		LocalDate birth = LocalDate.parse(birthDay, formatter);
		LocalDate fix = LocalDate.parse(fixDay, formatter);

		LocalDate insuranceStandardDate = fix.minusMonths(6);

		// 만 나이 계산 (ChronoUnit.YEARS는 생일이 지났는지 여부를 자동으로 계산함)
		return (int) ChronoUnit.YEARS.between(birth, insuranceStandardDate);
	}

}
