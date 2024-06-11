package common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 만나이, 한국식 나이, 보험 나이 등을 계산하는 기능 제공
 */
public class AgeUtil {
	
	private AgeUtil() {
		super();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(AgeUtil.class);
	
	private static final String YYYYMMDD = "yyyyMMdd";
	
	/**
	 * 현재일을 기준으로 만나이 계산
	 * @param birthDay
	 * @return
	 */
	public static int getAge(String birthDay) {
		Calendar birth = Calendar.getInstance();
		Calendar now = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat(YYYYMMDD);

		try {
			Date date = formatter.parse(birthDay);
			birth.setTime(date);
		} catch (ParseException e) {
			logger.error("getAge ParseException", e);
		}

		int age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
		if (birth.get(Calendar.DAY_OF_YEAR) >= now.get(Calendar.DAY_OF_YEAR)) {
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
		int age = 0;
		Calendar birth = Calendar.getInstance();
		Calendar fix = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat(YYYYMMDD);

		try {
			birth.setTime(formatter.parse(birthDay));
			fix.setTime(formatter.parse(fixDay));
		} catch (ParseException e) {
			logger.error("getAge ParseException", e);
		}

		age = fix.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
		if (birth.get(Calendar.DAY_OF_YEAR) >= fix.get(Calendar.DAY_OF_YEAR)) {
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
		int age = 0;
		Calendar birth = Calendar.getInstance();
		Calendar now = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat(YYYYMMDD);

		try {
			birth.setTime(formatter.parse(birthDay));
		} catch (ParseException e) {
			logger.error("getKoreanAge ParseException", e);
		}

		age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR) + 1;
		return age;
	}

	/**
	 * 기준일을 기준으로 한국 나이 계산
	 * @param birthDay
	 * @param fixDay
	 * @return
	 */
	public static int getKoreanAge(String birthDay, String fixDay) {
		int age = 0;
		Calendar birth = Calendar.getInstance();
		Calendar fix = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat(YYYYMMDD);

		try {
			birth.setTime(formatter.parse(birthDay));
			fix.setTime(formatter.parse(fixDay));
		} catch (ParseException e) {
			logger.error("getKoreanAge ParseException", e);
		}

		age = fix.get(Calendar.YEAR) - birth.get(Calendar.YEAR) + 1;
		return age;
	}

	/**
	 * 현재일을 기준으로 보험나이 계산<br/>
	 * 	- 생년월일 기준으로 6개월 되는 날부터 한 살 더 올라간다.
	 * @param birthDay
	 * @return
	 */
	public static int getInsurAge(String birthDay) {
		int age = 0;
		Calendar birth = Calendar.getInstance();
		Calendar now = Calendar.getInstance();
		Calendar target = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat(YYYYMMDD);
		
		try {
			birth.setTime(formatter.parse(birthDay));
		} catch (ParseException e) {
			logger.error("getInsurAge ParseException", e);
		}

		target.setTime(birth.getTime());
		target.set(Calendar.YEAR, now.get(Calendar.YEAR));
		target.set(Calendar.MONTH, (birth.get(Calendar.MONTH) + 6));

		if (target.get(Calendar.YEAR) > now.get(Calendar.YEAR)) {
			target.add(Calendar.YEAR, -1);
		}

		age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
		if (birth.get(Calendar.DAY_OF_YEAR) >= now.get(Calendar.DAY_OF_YEAR)) {
			age = age -1;
		}

		if (target.get(Calendar.DAY_OF_YEAR) < now.get(Calendar.DAY_OF_YEAR)) {
			age = age +1;
		}
		return age;
	}

	/**
	 * 기준일을 기준으로 보험나이 계산<br/>
	 * 	- 생년월일 기준으로 6개월 되는 날부터 한 살 더 올라간다.
	 * @param birthDay
	 * @return
	 */
	public static int getInsurAge(String birthDay, String fixDay) {
		int age = 0;
		Calendar birth = Calendar.getInstance();
		Calendar fix = Calendar.getInstance();
		Calendar target = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat(YYYYMMDD);

		try {
			birth.setTime(formatter.parse(birthDay));
			fix.setTime(formatter.parse(fixDay));
		} catch (ParseException e) {
			logger.error("getInsurAge ParseException", e);
		}

		target.setTime(birth.getTime());
		target.set(Calendar.YEAR, fix.get(Calendar.YEAR));
		target.set(Calendar.MONTH, (birth.get(Calendar.MONTH) + 6));

		if (target.get(Calendar.YEAR) > fix.get(Calendar.YEAR)) {
			target.add(Calendar.YEAR, -1);
		}

		age = fix.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
		if (birth.get(Calendar.DAY_OF_YEAR) >= fix.get(Calendar.DAY_OF_YEAR)) {
			age = age -1;
		}

		if (target.get(Calendar.DAY_OF_YEAR) < fix.get(Calendar.DAY_OF_YEAR)) {
			age = age +1;
		}
		return age;
	}

}
