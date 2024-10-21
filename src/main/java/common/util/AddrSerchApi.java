package common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2020. 9. 26. 김대광	최초작성
 * 2021. 8. 13. 김대광	SonarLint 지시에 따른 수정 (하지만... 가급적 try-with-resources 익숙해지기 위해~)
 * 2024.10. 21. 김대광	Java 17 이상 deprecated 대응
 * </pre>
 *
 * @apiNote Jackson Databind
 * @author 김대광
 */
public class AddrSerchApi {

	private AddrSerchApi() {
		super();
	}

	// API라 상관없을거 같기는 하지만... 클래스에 키가 있다는건 시큐어 코딩 상 무진상 큰일이다.....
	private static final String CONFM_KEY = "U01TX0FVVEgyMDE4MTAxNzEzMTcwMDEwODI0MDM=";
	private static final String RESULT_TYPE = "json";

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getJusoApi(int currentPage, int countPerPage, String keyword) throws IOException {
		Map<String, Object> resultMap = new HashMap<>();

		String sApiUrl = "http://www.juso.go.kr/addrlink/addrLinkApi.do";
		sApiUrl += "?currentPage=" + currentPage;
		sApiUrl += "&countPerPage=" + countPerPage;
		sApiUrl += "&keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8.name());
		sApiUrl += "&confmKey=" + CONFM_KEY;
		sApiUrl += "&resultType=" + RESULT_TYPE;

//		~ Java 11
//		URL url = new URL(sApiUrl);

//		Java 17 ~
		URI uri = null;

		try {
			uri = new URI(sApiUrl);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		URL url = uri.toURL();

		StringBuilder sb = new StringBuilder();

		try( BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8.name()))) {
			String line;
			while ( (line = br.readLine()) != null ) {
				sb.append(line);
			}
		}

		Map<String, Object> dataMap;

		ObjectMapper mapper = new ObjectMapper();
		dataMap = mapper.readValue(sb.toString(), HashMap.class);

		LinkedHashMap<String, Object> inquiryMap = (LinkedHashMap<String, Object>) dataMap.get("results");
		LinkedHashMap<String, Object> commonMap = (LinkedHashMap<String, Object>) inquiryMap.get("common");

		List<LinkedHashMap<String, Object>> josoList = (List<LinkedHashMap<String, Object>>) inquiryMap.get("juso");

		List<Map<String, Object>> jusoList = new ArrayList<>();

		for ( LinkedHashMap<String, Object> map : josoList ) {
			Map<String, Object> tempMap = new HashMap<>();

			tempMap.put("zipNo", 		map.get("zipNo"));
			tempMap.put("jibunAddr", 	map.get("jibunAddr"));
			tempMap.put("roadAddr", 	map.get("roadAddr"));

			tempMap.put("siNm", 		map.get("siNm"));
			tempMap.put("sggNm", 		map.get("sggNm"));
			tempMap.put("emdNm", 		map.get("emdNm"));
			tempMap.put("liNm", 		map.get("liNm"));

			jusoList.add(tempMap);
		}

		resultMap.put("countPerPage", commonMap.get("countPerPage"));
		resultMap.put("currentPage", commonMap.get("currentPage"));
		resultMap.put("totalCount", commonMap.get("totalCount"));
		resultMap.put("jusoList", jusoList);

		return resultMap;
	}

}
