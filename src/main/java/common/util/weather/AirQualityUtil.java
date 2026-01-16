package common.util.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2026. 1. 16. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public class AirQualityUtil {

	private AirQualityUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static final String API_1_URL = "https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMinuDustFrcstDspth";
	private static final String API_2_URL = "https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty";

	private static final String PM_10 = "PM10";
	private static final String PM_25 = "PM25";

	@Getter
	@Builder
	@ToString
	public static class AirQualityData {
		private String pm10Grade;		// 예보통보 조회 (informGrade)
		private String pm25Grade;		// 예보통보 조회 (informGrade)
		private String pm10Value;		// 실시간 측정정보
		private String pm25Value;		// 실시간 측정정보
		private String sidoName;		// 실시간 측정정보
		private String stationName;		// 실시간 측정정보
		private String dataTime;		// 실시간 측정정보
	}

	/**
	 * 미세먼지(PM10), 초미세먼지(PM2.5) 조회
	 * @param serviceEncodingKey
	 * @param sidoName
	 *  - 시도 이름(전국, 서울, 부산, 대구, 인천, 광주, 대전, 울산, 경기, 강원, 충북, 충남, 전북, 전남, 경북, 경남, 제주, 세종)
	 * @param stationName
	 *  - 측정소명
	 * @return
	 * @throws IOException
	 * @see <a href="https://www.airkorea.or.kr/web/stationInfo?pMENU_NO=93">측정소명</a>
	 */
	public static AirQualityData getAirQualityData(String serviceEncodingKey, String sidoName, String stationName) throws IOException {
		if ( StringUtils.isBlank(serviceEncodingKey) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("serviceEncodingKey"));
		}

		LocalDateTime localDateTime = LocalDateTime.now();
		String searchDate = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		String pm10Grade = getForecastGrade(serviceEncodingKey, searchDate, PM_10, sidoName);
		String pm25Grade = getForecastGrade(serviceEncodingKey, searchDate, PM_25, sidoName);

		Map<String, Object> valueMap = getRealtimeStationData(serviceEncodingKey, sidoName, stationName);

		String pm10Value = "";
		String pm25Value = "";
		String dataTime = "";

		if (!valueMap.isEmpty()) {
			pm10Value = String.valueOf(valueMap.get("pm10Value"));
			pm25Value = String.valueOf(valueMap.get("pm25Value"));
			dataTime = String.valueOf(valueMap.get("dataTime"));
		}

		return AirQualityData.builder()
				.pm10Grade(pm10Grade)
				.pm25Grade(pm25Grade)
				.pm10Value(pm10Value)
				.pm25Value(pm25Value)
				.sidoName(sidoName)
				.stationName(stationName)
				.dataTime(dataTime)
				.build();
	}

	private static String getForecastGrade(String serviceEncodingKey, String searchDate, String informCode, String sidoName) throws IOException {
		String grade = "";
		Map<String, Object> gradeMap = getForecastGradeMap(serviceEncodingKey, searchDate);

		if (!gradeMap.isEmpty()) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) gradeMap.get(informCode);
			grade = String.valueOf(map.get(sidoName));
		}

		return grade;
	}

	private static Map<String, Object> getForecastGradeMap(String serviceEncodingKey, String searchDate) throws IOException {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> informGradeMap = fetchForecastTextMap(serviceEncodingKey, searchDate);

		if (!informGradeMap.isEmpty()) {
			String pm10InformGrade = String.valueOf(informGradeMap.get(PM_10));
			Map<String, Object> pm10Map = parseRegionGradeText(pm10InformGrade);
			map.put(PM_10, pm10Map);

			String pm25InformGrade = String.valueOf(informGradeMap.get(PM_25));
			Map<String, Object> pm25Map = parseRegionGradeText(pm25InformGrade);
			map.put(PM_25, pm25Map);
		}

		return map;
	}

	private static Map<String, Object> fetchForecastTextMap(String serviceEncodingKey, String searchDate) throws IOException {
		Map<String, Object> map = new HashMap<>();

		List<Map<String, Object>> pm10List = callForecastApi(serviceEncodingKey, searchDate, PM_10);
		List<Map<String, Object>> pm25List = callForecastApi(serviceEncodingKey, searchDate, PM_25);

		if (pm10List != null) {
			String pm10Grade = extractGradeTextByDate(pm10List, searchDate);
			map.put(PM_10, pm10Grade);
		}

		if (pm25List != null) {
			String pm25Grade = extractGradeTextByDate(pm25List, searchDate);
			map.put(PM_25, pm25Grade);
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	private static List<Map<String, Object>> callForecastApi(String serviceEncodingKey, String searchDate, String informCode) throws IOException {
		StringBuilder urlBuilder = new StringBuilder(API_1_URL);
		urlBuilder.append("?" + "serviceKey=" + serviceEncodingKey);
		urlBuilder.append("&" + "returnType=" + "json");
		urlBuilder.append("&" + "searchDate=" + searchDate);
		urlBuilder.append("&" + "InformCode=" + informCode);

		URL url = new URL(urlBuilder.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");

		BufferedReader rd;
		if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}

		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		conn.disconnect();

        Map<String, Object> resultMap = MAPPER.readValue(sb.toString(), Map.class);
        return extractItemsFromResponse(resultMap);
	}

	@SuppressWarnings("unchecked")
	private static List<Map<String, Object>> extractItemsFromResponse(Map<String, Object> resultMap) {
		List<Map<String, Object>> list = null;

		Map<String, Object> responseMap = null;
		Map<String, Object> bodyMap = null;

		if ( resultMap.containsKey("response") ) {
			responseMap = (Map<String, Object>) resultMap.get("response");
		}

		if (responseMap != null) {
			bodyMap = (Map<String, Object>) responseMap.get("body");
		}

		if (bodyMap != null) {
			list = (List<Map<String, Object>>) bodyMap.get("items");
		}

		return list;
	}

	private static String extractGradeTextByDate(List<Map<String, Object>> list, String searchDate) {
		String informGrade = "";
		for (Map<String, Object> map : list) {
			if (String.valueOf(map.get("informData")).equals(searchDate) ) {
				informGrade = String.valueOf(map.get("informGrade"));
			}
		}

		return informGrade;
	}

	private static Map<String, Object> parseRegionGradeText(String informGrade) {
        Map<String, Object> gradeMap = new HashMap<>();

        if (informGrade == null || informGrade.isEmpty()) return gradeMap;

        String[] regions = informGrade.split(",");
        for (String region : regions) {
            String[] keyValue = region.split(":");
            if (keyValue.length == 2) {
                gradeMap.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }

        return gradeMap;
    }

	@SuppressWarnings("unchecked")
	private static List<Map<String, Object>> callRealtimeAirQualityApi(String serviceEncodingKey, String sidoName) throws IOException {
		StringBuilder urlBuilder = new StringBuilder(API_2_URL);
		urlBuilder.append("?" + "serviceKey=" + serviceEncodingKey);
		urlBuilder.append("&" + "returnType=" + "json");
		urlBuilder.append("&" + "numOfRows=" + 100);
		urlBuilder.append("&" + "pageNo=" + 1);
		urlBuilder.append("&" + "sidoName=" + URLEncoder.encode(sidoName, StandardCharsets.UTF_8.name()));
		urlBuilder.append("&" + "ver=" + "1.0");

		URL url = new URL(urlBuilder.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");

		BufferedReader rd;
		if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}

		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		conn.disconnect();

        Map<String, Object> resultMap = MAPPER.readValue(sb.toString(), Map.class);
        return extractItemsFromResponse(resultMap);
	}

	private static Map<String, Object> getRealtimeStationData(String serviceEncodingKey, String sidoName, String stationName) throws IOException {
		Map<String, Object> resultMap = new HashMap<>();

		List<Map<String, Object>> valueList = callRealtimeAirQualityApi(serviceEncodingKey, sidoName);

		if (valueList != null) {
			for (Map<String, Object> map : valueList) {
				if (String.valueOf(map.get("stationName")).equals(stationName) ) {
					resultMap = map;
				}
			}
		}

		return resultMap;
	}

}
