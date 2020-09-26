package common.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
 * </pre>
 * 
 * @apiNote Jackson Databind
 * @author 김대광
 */
public class AddrSerchApi {
	
	private static final String CONFM_KEY = "U01TX0FVVEgyMDE4MTAxNzEzMTcwMDEwODI0MDM=";
	private static final String RESULT_TYPE = "json";
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getJusoApi(int currentPage, int countPerPage, String keyword) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		
		String sApiUrl = "http://www.juso.go.kr/addrlink/addrLinkApi.do";
		sApiUrl += "?currentPage=" + currentPage;
		sApiUrl += "&countPerPage=" + countPerPage;
		sApiUrl += "&keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8.name());
		sApiUrl += "&confmKey=" + CONFM_KEY;
		sApiUrl += "&resultType=" + RESULT_TYPE;
		
		URL url = new URL(sApiUrl);
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8.name()));

		StringBuilder sb = new StringBuilder();
		String line;
		while ( (line = br.readLine()) != null ) {
			sb.append(line);
		}
		
		br.close();
		
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
