package common.test;

import org.junit.Test;

import common.util.date.Jsr310DateUtil;

public class TestCreateCalander {

	@Test
	public void test() {
		String now_date = "";
		now_date = Jsr310DateUtil.Today.getTodayString("yyyyMM") + "01";

		int now_start_day = Jsr310DateUtil.GetDayOfWeek.getFirstDayOfWeek(now_date);
		int now_last_day = Jsr310DateUtil.GetDayOfMonth.getLastDayOfMonth(now_date);

		StringBuilder sb = new StringBuilder();
		sb.append(Jsr310DateUtil.StringFormat.getStringDate(now_date, "yyyy.MM"));
		sb.append("\r\n");
		sb.append("일").append("\t").append("월").append("\t").append("화").append("\t");
		sb.append("수").append("\t").append("목").append("\t").append("금").append("\t").append("토");

		for (int i=1; i < 42; i++) {
			if (i % 7 == 1) {
				sb.append("\r\n");
			}

			int now_day = (i - now_start_day + 1);

			if ( (now_day > 0) && (now_day <= now_last_day) ) {
				if (i % 7 == 0) {
					sb.append("(").append(now_day).append(")").append("\t");
				}
				else if (i % 7 == 1) {
					sb.append("{").append(now_day).append("}").append("\t");
				}
				else {
					sb.append(now_day).append("\t");
				}
			} else {
				sb.append("\t");
			}
		}
		System.out.println(sb.toString());
	}

}
