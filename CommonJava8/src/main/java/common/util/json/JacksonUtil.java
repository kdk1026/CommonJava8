package common.util.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 큰 파일 파싱에 유리
 */
public class JacksonUtil {
	
	private JacksonUtil() {
		super();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(JacksonUtil.class);

	public static String prettyPrintString(String jsonStr) {
		ObjectMapper mapper = new ObjectMapper();
		String sortJson = "";
		try {
			Object json = mapper.readValue(jsonStr, Object.class);
			sortJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		} catch (Exception e) {
			logger.error("", e);
		}
		return sortJson;
	}

	public static String converterMapToJsonStr(Map<String, Object> map) {
		String jsonStr = "";

		ObjectMapper mapper = new ObjectMapper();
		try {
			jsonStr = mapper.writeValueAsString(map);
		} catch (Exception e) {
			logger.error("", e);
		}
		return jsonStr;
	}

	public static JsonNode converterMapToJsonNode(Map<String, Object> map) {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.valueToTree(map);
	}

	public static String converterListToJsonStr(List<Map<String, Object>> list) {
		String jsonStr = "";

		ObjectMapper mapper = new ObjectMapper();
		try {
			jsonStr = mapper.writeValueAsString(list);
		} catch (Exception e) {
			logger.error("", e);
		}
		return jsonStr;
	}

	public static JsonNode converterListToJsonNode(List<Map<String, Object>> list) {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.valueToTree(list);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> converterJsonStrToMap(String jsonStr) {
		Map<String, Object> map = new HashMap<>();

		ObjectMapper mapper = new ObjectMapper();
		try {
			map = mapper.readValue(jsonStr, Map.class);
		} catch (Exception e) {
			logger.error("", e);
		}
		return map;
	}

	public static JsonNode converterJsonStrToJsonNode(String jsonStr) {
		JsonNode jsonNode = null;
		ObjectMapper mapper = new ObjectMapper();

		try {
			jsonNode = mapper.readTree(jsonStr);
		} catch (Exception e) {
			logger.error("", e);
		}
		return jsonNode;
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> converterJsonStrToList(String jsonArrStr) {
		List<Map<String, Object>> list = new ArrayList<>();

		ObjectMapper mapper = new ObjectMapper();
		try {
			list = mapper.readValue(jsonArrStr, List.class);
		} catch (Exception e) {
			logger.error("", e);
		}
		return list;
	}

}
