package common.util.date;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
 * 2026. 1. 12. 김대광	최초작성
 * </pre>
 *
 * <pre>
 * www.data.go.kr
 * : 한국천문연구원_특일 정보
 * </pre>
 *
 * @author 김대광
 */
public class HolidayUtil {

	private HolidayUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static final String API_URL = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";

	@Getter
	@Builder
	@ToString
	public static class HolidayData {
		private String dateKind;
		private String dateName;
		private String isHoliday;
		private String locdate;
		private String seq;
	}

	public static List<HolidayData> getHolidayList(String serviceEncodingKey, int year) throws IOException {
		if ( StringUtils.isBlank(serviceEncodingKey) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("serviceEncodingKey"));
		}

		if (year < 1900 || year > 2100) {
	        throw new IllegalArgumentException("연도는 1900년에서 2100년 사이여야 합니다.");
	    }

		StringBuilder urlBuilder = new StringBuilder(API_URL);
		urlBuilder.append("?" + "serviceKey=" + serviceEncodingKey);
		urlBuilder.append("&" + "solYear=" + year);
		urlBuilder.append("&" + "_type=" + "json");

		List<Map<String, Object>> mapList = fetchHolidayDataFromApi(urlBuilder.toString());

		return convertToHolidayData(mapList);
	}

	public static List<HolidayData> getHolidayList(String serviceEncodingKey, int year, int month) throws IOException {
		if ( StringUtils.isBlank(serviceEncodingKey) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("serviceEncodingKey"));
		}

		if (year < 1900 || year > 2100) {
	        throw new IllegalArgumentException("연도는 1900년에서 2100년 사이여야 합니다.");
	    }

		if (month < 1 || month > 12) {
	        throw new IllegalArgumentException("월은 1~12 사이여야 합니다: " + month);
	    }

		StringBuilder urlBuilder = new StringBuilder(API_URL);
		urlBuilder.append("?" + "serviceKey=" + serviceEncodingKey);
		urlBuilder.append("&" + "solYear=" + year);
		urlBuilder.append("&" + "solMonth=" + String.format("%02d", month));
		urlBuilder.append("&" + "_type=" + "json");

		List<Map<String, Object>> mapList = fetchHolidayDataFromApi(urlBuilder.toString());

		return convertToHolidayData(mapList);
	}

	@SuppressWarnings("unchecked")
	private static List<Map<String, Object>> fetchHolidayDataFromApi(String urlStr) throws IOException {
		URL url = new URL(urlStr);
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
        return getItem(resultMap);
	}

	@SuppressWarnings("unchecked")
	private static List<Map<String, Object>> getItem(Map<String, Object> resultMap) {
		List<Map<String, Object>> list = null;

		Map<String, Object> responseMap = null;
		Map<String, Object> bodyMap = null;
		Map<String, Object> itemsMap = null;

		if ( resultMap.containsKey("response") ) {
			responseMap = (Map<String, Object>) resultMap.get("response");
		}

		if ( responseMap != null ) {
			bodyMap = (Map<String, Object>) responseMap.get("body");
		}

		if ( bodyMap != null ) {
			itemsMap = (Map<String, Object>) bodyMap.get("items");
		}

		if ( itemsMap != null ) {
			list = (List<Map<String, Object>>) itemsMap.get("item");
		}

		return list;
	}

	private static List<HolidayData> convertToHolidayData(List<Map<String, Object>> mapList) {
		List<HolidayData> holidayList = null;

		if (mapList != null) {
			holidayList = new ArrayList<>();

			for (Map<String, Object> map : mapList) {
				HolidayData data = HolidayData.builder()
						.dateKind(String.valueOf(map.get("dateKind")))
						.dateName(String.valueOf(map.get("dateName")))
						.isHoliday(String.valueOf(map.get("isHoliday")))
						.locdate(String.valueOf(map.get("locdate")))
						.seq(String.valueOf(map.get("seq")))
						.build();

				holidayList.add(data);
			}
		}

		return holidayList;
	}

}
