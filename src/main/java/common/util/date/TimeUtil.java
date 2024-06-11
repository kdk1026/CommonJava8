package common.util.date;

import java.util.Date;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2022. 8. 23. kdk	최초작성
 * </pre>
 *
 *
 * @author kdk
 */
public class TimeUtil {

	private static class TIME_MAXIMUM {
		public static final int SEC = 60;
		public static final int MIN = 60;
		public static final int HOUR = 24;
		public static final int DAY = 30;
		public static final int MONTH = 12;
	}

	public static String calculateTime(Date date) {
		long curTime = System.currentTimeMillis();
		long regTime = date.getTime();
		long diffTime = (curTime - regTime) / 1000;

		String msg = null;

		if ( diffTime < TIME_MAXIMUM.SEC ) {
			msg = diffTime + "초전";
		} else if ( (diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN ) {
			msg = diffTime + "분전";
		} else if ( (diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR ) {
			msg = diffTime + "시간전";
		} else if ( (diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY ) {
			msg = diffTime + "일전";
		} else if ( (diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH ) {
			msg = diffTime + "달전";
		} else {
			msg = diffTime + "년전";
		}

		return msg;
	}

}
