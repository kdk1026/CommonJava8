package common.util.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
 * 2026. 1. 14. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public class WeatherUtil {

	private WeatherUtil() {
		super();
	}

	private static class LatLonConverter {
		// 기상청 지도 투영 설정값
		private static final double RE = 6371.00877; // 지구 반경(km)
	    private static final double GRID = 5.0;      // 격자 간격(km)
	    private static final double SLAT1 = 30.0;    // 투영 위도1(degree)
	    private static final double SLAT2 = 60.0;    // 투영 위도2(degree)
	    private static final double OLON = 126.0;    // 기준점 경도(degree)
	    private static final double OLAT = 38.0;     // 기준점 위도(degree)
	    private static final double XO = 43;         // 기준점 X좌표(GRID)
	    private static final double YO = 136;        // 기준점 Y좌표(GRID)

	    public static int[] convertToGrid(double lat, double lon) {
	    	final double DEGRAD = Math.PI / 180.0;

	        double re = RE / GRID;
	        double slat1 = SLAT1 * DEGRAD;
	        double slat2 = SLAT2 * DEGRAD;
	        double olon = OLON * DEGRAD;
	        double olat = OLAT * DEGRAD;

	        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
	        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
	        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
	        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
	        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
	        ro = re * sf / Math.pow(ro, sn);

	        double ra = Math.tan(Math.PI * 0.25 + (lat) * DEGRAD * 0.5);
	        ra = re * sf / Math.pow(ra, sn);
	        double theta = lon * DEGRAD - olon;
	        if (theta > Math.PI) theta -= 2.0 * Math.PI;
	        if (theta < -Math.PI) theta += 2.0 * Math.PI;
	        theta *= sn;

	        int nx = (int) Math.floor(ra * Math.sin(theta) + XO + 0.5);
	        int ny = (int) Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);

	        return new int[]{nx, ny};
	    }
	}

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static final String API_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";

	@Getter
	@Builder
	@ToString
	public static class WeatherData {
		private String baseDate;
		private String baseTime;
		private String category;
		private String categoryNm;
		private String fcstDate;
		private String fcstTime;
		private String fcstValue;
		private String fcstUnit;
	}

	public static List<WeatherData> getWeatherList(String serviceEncodingKey, double lat, double lon) throws IOException {
		if ( StringUtils.isBlank(serviceEncodingKey) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("serviceEncodingKey"));
		}

		LocalDateTime localDateTime = LocalDateTime.now();
		String baseDate = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String baseTime = getNearestBaseTime(localDateTime);

		int[] grid = LatLonConverter.convertToGrid(lat, lon);

		StringBuilder urlBuilder = new StringBuilder(API_URL);
		urlBuilder.append("?" + "serviceKey=" + serviceEncodingKey);
		urlBuilder.append("&" + "pageNo=" + 1);
		urlBuilder.append("&" + "numOfRows=" + 1000);
		urlBuilder.append("&" + "dataType=" + "JSON");
		urlBuilder.append("&" + "base_date=" + baseDate);
		urlBuilder.append("&" + "base_time=" + baseTime);
		urlBuilder.append("&" + "nx=" + grid[0]);
		urlBuilder.append("&" + "ny=" + grid[1]);

		List<Map<String, Object>> mapList = fetchWeatherDataFromApi(urlBuilder.toString());

		return convertToWeatherData(mapList);
	}

	private static String getNearestBaseTime(LocalDateTime localDateTime) {
		String[] baseTimes = {"0200", "0500", "0800", "1100", "1400", "1700", "2000", "2300"};

		int nowHour = localDateTime.getHour();
		String lastBaseTime = "";

		for (int i = baseTimes.length - 1; i >= 0; i--) {
			int hour = Integer.parseInt(baseTimes[i].substring(0, 2));
			if (hour <= nowHour) {
				lastBaseTime = baseTimes[i];
				break;
			}
		}

		if (lastBaseTime.equals("")) {
			lastBaseTime = baseTimes[baseTimes.length - 1];
		}

		return lastBaseTime;
	}

	@SuppressWarnings("unchecked")
	private static List<Map<String, Object>> fetchWeatherDataFromApi(String urlStr) throws IOException {
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

		if (responseMap != null) {
			bodyMap = (Map<String, Object>) responseMap.get("body");
		}

		if (bodyMap != null) {
			itemsMap = (Map<String, Object>) bodyMap.get("items");
		}

		if (itemsMap != null) {
			list = (List<Map<String, Object>>) itemsMap.get("item");
		}

		return list;
	}

	private static List<WeatherData> convertToWeatherData(List<Map<String, Object>> mapList) {
		List<WeatherData> weatherList = null;

		if (mapList != null) {
			final String FCST_VALUE = "fcstValue";
			weatherList = new ArrayList<>();

			int i = 0;
			for (Map<String, Object> map : mapList) {
				if (i < 12) {

					String category = String.valueOf(map.get("category"));
					String fcstValue = String.valueOf(map.get(FCST_VALUE));
					String categoryNm = "";
					String fcstUnit = "";

					Map<String, Object> mappingData = getMappingData(category, fcstValue);

					fcstValue = String.valueOf(mappingData.get(FCST_VALUE));
					categoryNm = String.valueOf(mappingData.get("categoryNm"));
					fcstUnit = String.valueOf(mappingData.get("fcstUnit"));

					WeatherData data = WeatherData.builder()
							.baseDate(String.valueOf(map.get("baseDate")))
							.baseTime(String.valueOf(map.get("baseTime")))
							.category(category)
							.categoryNm(categoryNm)
							.fcstDate(String.valueOf(map.get("fcstDate")))
							.fcstTime(String.valueOf(map.get("fcstTime")))
							.fcstValue(fcstValue)
							.fcstUnit(fcstUnit)
							.build();

					weatherList.add(data);
				}
				i++;
			}
		}

		return weatherList;
	}

	private static Map<String, Object> getMappingData(String category, String fcstValue) {
		Map<String, Object> map = new HashMap<>();

		String categoryNm = "";
		String fcstUnit = "";

		switch (category) {
		case "POP":
			categoryNm = "강수확률";
			fcstUnit = "%";
			break;
        case "PTY":
        	categoryNm = "강수형태";
        	fcstValue = getPrecipitationType(fcstValue);
        	break;
		case "PCP":
			categoryNm = "1시간 강수량";
			break;
		case "REH":
			categoryNm = "습도";
			fcstUnit = "%";
			break;
		case "SNO":
			categoryNm = "1시간 신적설";
			break;
		case "SKY":
			categoryNm = "하늘상태";
			fcstValue = getSkyCondition(fcstValue);
			break;
		case "TMP":
			categoryNm = "1시간 기온";
			fcstUnit = "℃";
			break;
		case "TMN":
			categoryNm = "일 최저기온";
			fcstUnit = "℃";
			break;
		case "TMX":
			categoryNm = "일 최고기온";
			fcstUnit = "℃";
			break;
		case "UUU":
			categoryNm = "풍속(동서성분)";
			fcstUnit = "m/s";
			break;
		case "VVV":
			categoryNm = "풍속(남북성분)";
			fcstUnit = "m/s";
			break;
		case "WAV":
			categoryNm = "파고";
			fcstUnit = "M";
			break;
		case "VEC":
			categoryNm = "풍향";
			fcstUnit = "deg";
			break;
		case "WSD":
			categoryNm = "풍속";
			fcstUnit = "m/s";
			break;

		default:
			break;
		}

		map.put("categoryNm", categoryNm);
		map.put("fcstUnit", fcstUnit);
		map.put("fcstValue", fcstValue);

		return map;
	}

	/**
	 * 강수형태 값 구하기
	 * @param fcstValue
	 * @return
	 */
	private static String getPrecipitationType(String fcstValue) {
		String value = "";

    	switch (fcstValue) {
		case "0":
			value = "없음";
			break;
		case "1":
			value = "비";
			break;
		case "2":
			value = "비/눈";
			break;
		case "3":
			value = "눈";
			break;
		case "4":
			value = "소나기";
			break;

		default:
			break;
		}

    	return value;
	}

	/**
	 * 하늘상태 값 구하기
	 * @param fcstValue
	 * @return
	 */
	private static String getSkyCondition(String fcstValue) {
		String value = "";

    	switch (fcstValue) {
			case "1":
				value = "맑음";
				break;
			case "3":
				value = "구름많음";
				break;
			case "4":
				value = "흐림";
				break;

			default:
				break;
			}

		return value;
	}

}
