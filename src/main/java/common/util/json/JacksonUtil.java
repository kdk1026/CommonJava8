package common.util.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

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

	public static class ToJson {

		protected ToJson() {
			super();
		}

		public static String converterObjToJsonStr(Object obj) {
			String jsonStr = "";

			ObjectMapper mapper = new ObjectMapper();
			try {
				jsonStr = mapper.writeValueAsString(obj);
			} catch (Exception e) {
				logger.error("", e);
			}
			return jsonStr;
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

		public static String converterListToJsonStr(List<?> list) {
			String jsonStr = "";

			ObjectMapper mapper = new ObjectMapper();
			try {
				jsonStr = mapper.writeValueAsString(list);
			} catch (Exception e) {
				logger.error("", e);
			}
			return jsonStr;
		}

		public static JsonNode converterMapToJsonNode(Map<String, Object> map) {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.valueToTree(map);
		}

		public static JsonNode converterListToJsonNode(List<?> list) {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.valueToTree(list);
		}
	}

	public static class FromJson {

		protected FromJson() {
			super();
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

		public static List<?> converterJsonStrToList(String jsonArrStr) {
			List<?> list = new ArrayList<>();

			ObjectMapper mapper = new ObjectMapper();
			try {
				list = mapper.readValue(jsonArrStr, List.class);
			} catch (Exception e) {
				logger.error("", e);
			}
			return list;
		}

		public static ArrayNode converterJsonStrToArayNode(String jsonArrStr) {
			ArrayNode arrayNode = null;
			ObjectMapper mapper = new ObjectMapper();

			try {
				arrayNode = (ArrayNode) mapper.readTree(jsonArrStr);
			} catch (Exception e) {
				logger.error("", e);
			}
			return arrayNode;
		}

		public static <T> T converterJsonStrToClass(String jsonStr, Class<T> clazz) {
			ObjectMapper mapper = new ObjectMapper();

			try {
				Object result = mapper.readValue(jsonStr, clazz);
				return clazz.cast(result);
			} catch (Exception e) {
				logger.error("", e);
			}
			return null;
		}
	}

}
