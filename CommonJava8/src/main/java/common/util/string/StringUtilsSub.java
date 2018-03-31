package common.util.string;

import java.util.UUID;

public class StringUtilsSub {
	
	private StringUtilsSub() {
		super();
	}

	/**
	 * UUID 문자열에서 '-'를 제외한 32자리 문자열 반환
	 * @return
	 */
	public static String getRandomString() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * 각 요소 사이에 지정된 구분 기호를 사용하여 문자열 배열의 모든 요소를 연결
	 * @param delim
	 * @param args
	 * @return
	 */
	public static String join(String delim, String... args) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i < args.length; i++) {
			sb.append(args[i]);
			if (i < args.length-1) {
				sb.append(delim);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 캐리지 리턴, 라인피드 문자열을 <br/> 태그로 변환
	 * @param str
	 * @return
	 */
	public static String replaceCRLFToHtmlTag(String str) {
		String sRes = "";
		final String sCr = "<br/>";
		
		if ( str.indexOf('\r') > -1) {
			sRes = str.replaceAll("\r", sCr);
		}
		
		if ( str.indexOf('\n') > -1) {
			sRes = str.replaceAll("\n", sCr);
		}
		
		if ( str.indexOf("\r\n") > -1 ) {
			sRes = str.replaceAll("\r\n", sCr);
		}
		
		return sRes;
	}

	/**
	 * <br/> 태그를 캐리지 리턴, 라인피드로 변환
	 * @param str
	 * @return
	 */
	public static String replaceHtmlTagToCRLF(String str) {
		String sRes = str;
		
		sRes = sRes.replaceAll("<br>", "\r\n");
		sRes = sRes.replaceAll("<br/>", "\r\n");
		
		return sRes;
	}
	
	/**
	 * XSS 공격 대상 HTML 특수문자를 아스키 코드로 변환
	 * @param str
	 * @return
	 */
	public static String escapeXss(String str) {
		String sRes = str;
		
		sRes = sRes.replaceAll("\"", "&quot;").replaceAll("&", "&amp;");
		sRes = sRes.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		sRes = sRes.replaceAll("'", "\\'").replaceAll("\"", "\\\"");
		
		return sRes;
	}

	/**
	 * XSS 공격 대상 아스키 코드를 HTML 특수문자로 변환
	 * @param str
	 * @return
	 */
	public static String unescapeXss(String str) {
		String sRes = str;
		
		sRes = sRes.replaceAll("&quot;", "\"").replaceAll("&amp;", "&");
		sRes = sRes.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		sRes = sRes.replaceAll("\\'", "'").replaceAll("\\\"", "\"");
		
		return sRes;
	}

	/**
	 * <pre>
	 * 별점 반환
	 *   - 0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0
	 *   - 소수점 첫째자리 이하는 반올림 후 계산
	 * </pre>
	 * @param strDoubleScore
	 * @return
	 */
	public static String getStarRating(String strDoubleScore) {
		String strStarRating = "";
		double dScore = Double.parseDouble(strDoubleScore);
		dScore = (double) Math.round(dScore * 10.0) / 10.0;

		String strScoreRound = Double.toString(dScore);

		if ( !"5".equals(strScoreRound.substring(2,3)) ) {
			strStarRating = Double.toString(Math.floor(Double.parseDouble(strScoreRound)));
		} else{
			strStarRating = strScoreRound;
		}

		if ("0.0".equals(strStarRating)) {
			strStarRating = "0";
		}
		return strStarRating;
	}

	/**
	 * 길이만큼 공백 채우기
	 * @param nSize
	 * @return
	 */
	public static String space(int nSize) {
		StringBuilder sb = new StringBuilder();
		
		for (int i=0; i < nSize; i++) {
			sb.append(" ");
		}
		
		return sb.toString();
	}
	
}
