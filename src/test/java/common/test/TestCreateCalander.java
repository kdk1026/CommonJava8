package common.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import common.util.date.Jsr310DateUtil;

public class TestCreateCalander {

	@Test
	public void test() {
		String yearMonth = "202011";
		String yearMonthDay = yearMonth + "01";

		int dayOfWeek = Jsr310DateUtil.GetDayOfWeek.getWeekStartDayFromDate(yearMonthDay);
		int lastDayOfMonth = Jsr310DateUtil.GetDayOfMonth.getEndOfMonthFromDate(yearMonthDay);

		// Joda-Time 과 Java 8의 JSR-310은 Calendar와 다르게 일요일이 7이므로 1로 변경
		if ( dayOfWeek == 7 ) {
			dayOfWeek = 1;
		} else {
			dayOfWeek = dayOfWeek + 1;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(Jsr310DateUtil.StringFormat.getStringDate(yearMonthDay, "yyyy.MM"));
		sb.append("\r\n");
		sb.append("일").append("\t").append("월").append("\t").append("화").append("\t");
		sb.append("수").append("\t").append("목").append("\t").append("금").append("\t").append("토");
		sb.append("\r\n");

		for (int i=1; i <= lastDayOfMonth; i++) {
			if (i == 1) {
				for (int j=1; j < dayOfWeek; j++) {
					sb.append("\t");
				}
			}

			if ( dayOfWeek % 7 == 0 ) {
				sb.append("(").append(i).append(")").append("\t");
			}
			else if ( dayOfWeek % 7 == 1 ) {
				sb.append("{").append(i).append("}").append("\t");
			}
			else {
				sb.append(i).append("\t");
			}

			if ( dayOfWeek % 7 == 0 ) {
				sb.append("\r\n");
			}

			dayOfWeek ++;
		}

		System.out.println(sb.toString());
		assertTrue(true);
	}

}
