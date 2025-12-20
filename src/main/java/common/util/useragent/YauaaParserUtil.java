package common.util.useragent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 12. 20. 김대광	최초작성
 * </pre>
 *
 * <pre>
 * User Agent 문자열의 형식은 언제든지 바뀔 수 있으므로 라이브러리를 이용하면
 * 라이브러리 제작사에게 맡기고 최신 버전으로 업데이트만 처리
 * </pre>
 *
 * <pre>
 * 라이브러리 내부적으로 Log4j 2를 사용
 *  - Logback(SLF4J의 구현체)을 사용 중이라면,
 *    Yauaa가 보내는 로그 호출을 SLF4J로 가로채서 Logback이 처리하도록 연결해주는 '브릿지' 라이브러리 필요
 *    : Log4j-to-Slf4j
 * </pre>
 *
 * @author 김대광
 */
public class YauaaParserUtil {

	private static final String USER_AGENT = "User-Agent";
	private static final String REQUEST = "request";
	private static final String UA_STRING = "uaString";
	private static final String FIELD_NAME = "fieldName";

	private YauaaParserUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNull(String paramName) {
	        return String.format("'%s' is null", paramName);
	    }

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }
	}

	// 분석기 생성 비용이 크므로 static으로 한 번만 생성
	// Java 11 이상
//    private static final UserAgentAnalyzer ANALYZER = UserAgentAnalyzer
//            .newBuilder()
//            .hideMatcherLoadStats()
//            .withCache(10000)
//            .build();

	// Java 8
	private static final UserAgentAnalyzer ANALYZER = UserAgentAnalyzer
			.newBuilder()
			.useJava8CompatibleCaching()
			.withCache(10000)
			.build();

	/**
	 * User-Agent 문자열을 분석하여 주요 정보를 반환
	 * @param uaString
	 * @return
	 */
	public static Map<String, String> parse(String uaString) {
		if ( StringUtils.isBlank(uaString) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(UA_STRING));
		}

        UserAgent agent = ANALYZER.parse(uaString);
        Map<String, String> result = new HashMap<>();

        /*
         * 기기 분류
         * - Desktop, Phone, Tablet, Watch, TV, Robot
         */
        result.put("DeviceClass", agent.getValue(UserAgent.DEVICE_CLASS));

        /*
         * 기기 명칭 (모델명)
         * - Apple iPhone, Samsung SM-G991N (S21 모델명), Google Pixel 6
         * - PC(Desktop) 환경에서는 하드웨어 정보를 알 수 없기 때문에 주로 Unknown이나 Macintosh, Desktop 정도로 나옴
         */
        result.put("DeviceName", agent.getValue(UserAgent.DEVICE_NAME));

        /**
         * 브라우저 이름
         * - Chrome, Safari, Firefox, Edge, SamsungBrowser
         */
        result.put("AgentName", agent.getValue(UserAgent.AGENT_NAME));

        /**
         * 브라우저 버전
         */
        result.put("AgentVersion", agent.getValue(UserAgent.AGENT_VERSION));

        /**
         * 운영체제 이름
         * - Windows NT, Android, iOS, Mac OS X, Linux
         */
        result.put("OperatingSystemName", agent.getValue(UserAgent.OPERATING_SYSTEM_NAME));

        /**
         * 운영체제 버전
         */
        result.put("OperatingSystemVersion", agent.getValue(UserAgent.OPERATING_SYSTEM_VERSION));

        return result;
    }

	/**
	 * User-Agent 문자열을 분석하여 주요 정보를 반환
	 * @param request
	 * @return
	 */
	public static Map<String, String> parse(HttpServletRequest request) {
		Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));

		String uaString = request.getHeader(USER_AGENT);

		UserAgent agent = ANALYZER.parse(uaString);
		Map<String, String> result = new HashMap<>();

		/*
		 * 기기 분류
		 * - Desktop, Phone, Tablet, Watch, TV, Robot
		 */
		result.put("DeviceClass", agent.getValue(UserAgent.DEVICE_CLASS));

		/*
		 * 기기 명칭 (모델명)
		 * - Apple iPhone, Samsung SM-G991N (S21 모델명), Google Pixel 6
		 * - PC(Desktop) 환경에서는 하드웨어 정보를 알 수 없기 때문에 주로 Unknown이나 Macintosh, Desktop 정도로 나옴
		 */
		result.put("DeviceName", agent.getValue(UserAgent.DEVICE_NAME));

		/**
		 * 브라우저 이름
		 * - Chrome, Safari, Firefox, Edge, SamsungBrowser
		 */
		result.put("AgentName", agent.getValue(UserAgent.AGENT_NAME));

		/**
		 * 브라우저 버전
		 */
		result.put("AgentVersion", agent.getValue(UserAgent.AGENT_VERSION));

		/**
		 * 운영체제 이름
		 * - Windows NT, Android, iOS, Mac OS X, Linux
		 */
		result.put("OperatingSystemName", agent.getValue(UserAgent.OPERATING_SYSTEM_NAME));

		/**
		 * 운영체제 버전
		 */
		result.put("OperatingSystemVersion", agent.getValue(UserAgent.OPERATING_SYSTEM_VERSION));

		return result;
	}

	/**
	 * 특정 필드 하나만 반환
	 * @param uaString
	 * @param fieldName
	 * @return
	 */
	public static String getField(String uaString, String fieldName) {
		if ( StringUtils.isBlank(uaString) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(UA_STRING));
		}

		if ( StringUtils.isBlank(fieldName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(FIELD_NAME));
		}

        return ANALYZER.parse(uaString).getValue(fieldName);
    }

	/**
	 * 특정 필드 하나만 반환
	 * @param request
	 * @param fieldName
	 * @return
	 */
	public static String getField(HttpServletRequest request, String fieldName) {
		Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));

		String uaString = request.getHeader(USER_AGENT);

		if ( StringUtils.isBlank(fieldName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(FIELD_NAME));
		}

		return ANALYZER.parse(uaString).getValue(fieldName);
	}

}
