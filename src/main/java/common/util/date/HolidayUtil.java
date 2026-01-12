package common.util.date;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

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

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static final String API_URL = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";
	private static final String SERVICE_ENCODING_KEY = "SzdfNA5AvS6G0ieulBk7j0sAKF5D2WYk41aIs8M9TTPZq28q2p2IYQtcAw7Zqv4lNDnm36ktOVldQaINovtzeQ%3D%3D";

	public static List<Map<String, Object>> getHolidayList(int year) throws IOException {
		StringBuilder urlBuilder = new StringBuilder(API_URL);
		urlBuilder.append("?" + "serviceKey=" + SERVICE_ENCODING_KEY);
		urlBuilder.append("&" + "solYear=" + year);
		urlBuilder.append("&" + "_type=" + "json");

		return getHolidayList(urlBuilder.toString());
	}

	public static List<Map<String, Object>> getHolidayList(int year, int month) throws IOException {
		StringBuilder urlBuilder = new StringBuilder(API_URL);
		urlBuilder.append("?" + "serviceKey=" + SERVICE_ENCODING_KEY);
		urlBuilder.append("&" + "solYear=" + year);
		urlBuilder.append("&" + "solMonth=" + String.format("%02d", month));
		urlBuilder.append("&" + "_type=" + "json");

        return getHolidayList(urlBuilder.toString());
	}

	@SuppressWarnings("unchecked")
	private static List<Map<String, Object>> getHolidayList(String urlStr) throws IOException {
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

}
