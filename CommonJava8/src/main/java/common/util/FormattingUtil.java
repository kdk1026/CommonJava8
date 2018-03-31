package common.util;

import java.text.DecimalFormat;

public class FormattingUtil {
	
	private FormattingUtil() {
		super();
	}
	
	private static final String FORMAT = "$1-$2-$3";

	/**
	 * <pre>
	 * 전환번호 포맷
	 *   - 0x(x)-xxx(x)-xxxx
	 * </pre>
	 * @param str
	 * @return
	 */
	public static String makePhoneNumber(String str) {
		String sPattern = "^(0[2|3[1|2|3]|4[1|2|3|4]|5[1|2|3|4|5]|6[1|2|3|4]])-?(\\d{3,4})-?(\\d{4})+$";
		if ( !str.matches(sPattern) ) {
			return null;
		}
		return str.replaceAll(sPattern, FORMAT);
	}

	/**
	 * <pre>
	 * 휴대폰 번호 포맷
	 *   - 01x-xxx(x)-xxxx
	 * </pre>
	 * @param str
	 * @return
	 */
	public static String makeCellPhoneNumber(String str) {
		String sPattern = "^(01[016789])-?(\\d{3,4})-?(\\d{4})$";
		if ( !str.matches(sPattern) ) {
			return null;
		}
		return str.replaceAll(sPattern, FORMAT);
	}

	/**
	 * <pre>
	 * 사업자 등록번호 포맷
	 *   - xxx-xx-xxxxx
	 * </pre>
	 * @param sBusinessRegNum
	 * @return
	 */
	public static String makeBusinessRegNum(String str) {
		String sPattern = "^(\\d{3})-?(\\d{2})-?(\\d{5})$";
		if ( !str.matches(sPattern) ) {
			return null;
		}
		return str.replaceAll(sPattern, FORMAT);
	}
	
	/**
	 * <pre>
	 * 날짜 포맷
	 *   - YYYY-MM-DD
	 * </pre>
	 * @param str
	 * @return
	 */
	public static String makeYYYYMMDD(String str) {
		String sPattern = "^([0-9]{4})[- / .]?(0[1-9]|1[012])[- / .]?(0[1-9]|1[0-9]|2[0-9]|3[01])+$";
		if ( !str.matches(sPattern) ) {
			return null;
		}
		return str.replaceAll(sPattern, FORMAT);
	}

	/**
	 * <pre>
	 * 수치를 금액 표현으로 변환
	 *   - #,###
	 * </pre>
	 * @param num
	 * @return
	 */
	public static String convertMoneyFormat(int num) {
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(num);
	}
	
	/**
	 * 숫자금액 문자열을 한글 금액 표현으로 변환
	 * @param str
	 * @return
	 */
	public static String convertMoneyHangul(String str){
		String sMoney = str.replaceAll(",", "");

		String[] asHanNum1 = {"","일","이","삼","사","오","육","칠","팔","구"};
		String[] asHanNum2 = {"","십","백","천"};
		String[] asHanNum3 = {"","만","억","조"};

		StringBuilder sb = new StringBuilder();

		int nCnt = 0;
		int nLen = sMoney.length();
		for ( int i=0; i < nLen; i++ ) {
			int n = sMoney.charAt(i);
			sb.append( asHanNum1[n] );

			int nTemp = (nLen-1-i);
			if (n > 0) {
				sb.append( asHanNum2[nTemp % 4] );
			} else {
				nCnt++;
			}

			// 4의 배수
			if ( ((nTemp % 4) == 0)  && nCnt != 4) {
				// 만억조
				sb.append( asHanNum3[nTemp / 4] );
			}
			nCnt = 0;
		}
		sb.append("원");
		return sb.toString();
	}

}
