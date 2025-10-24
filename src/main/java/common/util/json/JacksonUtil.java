package common.util.json;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

 /**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 5. 21. kdk		정리
 * 2025. 5. 27. 김대광	제미나이에 의한 일부 코드 개선 (ObjectMapper static final로 처리)
 * </pre>
 *
 * 큰 파일 파싱에 유리
 *
 * @author kdk
 */
public class JacksonUtil {

	private JacksonUtil() {
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

	private static JacksonUtil instance;
    private static final ObjectMapper MAPPER = new ObjectMapper();

	private static final Logger logger = LoggerFactory.getLogger(JacksonUtil.class);

	private static synchronized JacksonUtil getInstance() {
        if (instance == null) {
			instance = new JacksonUtil();
        }

        return instance;
    }

	public static class ToJson {
		private ToJson() {
			super();
		}

		public static String converterObjToJsonStr(Object obj, boolean isPretty) {
			Objects.requireNonNull(obj, ExceptionMessage.isNull("obj"));

			String jsonStr = "";

			try {
				getInstance();
				if (!isPretty) {
					jsonStr = MAPPER.writeValueAsString(obj);
				} else {
					jsonStr = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
				}
			} catch (JsonProcessingException e) {
				logger.error("", e);
			}
			return jsonStr;
		}

		public static String converterMapToJsonStr(Map<String, Object> map, boolean isPretty) {
			if ( map == null || map.isEmpty() ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("map"));
			}

			String jsonStr = "";

			try {
				getInstance();
				if (!isPretty) {
					jsonStr = MAPPER.writeValueAsString(map);
				} else {
					jsonStr = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(map);
				}
			} catch (JsonProcessingException e) {
				logger.error("", e);
			}
			return jsonStr;
		}

		public static String converterListToJsonStr(List<?> list, boolean isPretty) {
			if ( list == null || list.isEmpty() ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("list"));
			}

			String jsonStr = "";

			try {
				getInstance();
				if (!isPretty) {
					jsonStr = MAPPER.writeValueAsString(list);
				} else {
					jsonStr = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(list);
				}
			} catch (JsonProcessingException e) {
				logger.error("", e);
			}
			return jsonStr;
		}

		public static JsonNode converterMapToJsonNode(Map<String, Object> map) {
			if ( map == null || map.isEmpty() ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("map"));
			}

			getInstance();
			return MAPPER.valueToTree(map);
		}

		public static JsonNode converterListToJsonNode(List<?> list) {
			if ( list == null || list.isEmpty() ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("list"));
			}

			getInstance();
			return MAPPER.valueToTree(list);
		}
	}

	public static class FromJson {
		private FromJson() {
			super();
		}

		private static void validateJsonString(String jsonStr) {
			if ( StringUtils.isBlank(jsonStr) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("jsonStr"));
			}
		}

		private static void validateJsonArrayString(String jsonArrayStr) {
			if ( StringUtils.isBlank(jsonArrayStr) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("jsonArrayStr"));
			}
		}

		@SuppressWarnings("unchecked")
		public static Map<String, Object> converterJsonStrToMap(String jsonStr) {
			validateJsonString(jsonStr);

			Map<String, Object> map = new HashMap<>();

			try {
				getInstance();
				map = MAPPER.readValue(jsonStr, Map.class);
			} catch (IOException e) {
				logger.error("", e);
			}
			return map;
		}

		public static JsonNode converterJsonStrToJsonNode(String jsonStr) {
			validateJsonString(jsonStr);

			JsonNode jsonNode = null;

			try {
				getInstance();
				jsonNode = MAPPER.readTree(jsonStr);
			} catch (IOException e) {
				logger.error("", e);
			}
			return jsonNode;
		}

		@SuppressWarnings("unchecked")
		public static <T> List<T> converterJsonStrToList(String jsonArrStr) {
			validateJsonArrayString(jsonArrStr);

			List<T> list = new ArrayList<>();

			try {
				getInstance();
				list = MAPPER.readValue(jsonArrStr, List.class);
			} catch (IOException e) {
				logger.error("", e);
			}
			return list;
		}

		public static ArrayNode converterJsonStrToArayNode(String jsonArrStr) {
			validateJsonArrayString(jsonArrStr);

			ArrayNode arrayNode = null;

			try {
				getInstance();
				arrayNode = (ArrayNode) MAPPER.readTree(jsonArrStr);
			} catch (IOException e) {
				logger.error("", e);
			}
			return arrayNode;
		}

		public static <T> T converterJsonStrToClass(String jsonStr, Class<T> clazz) {
			validateJsonString(jsonStr);

			Objects.requireNonNull(clazz, ExceptionMessage.isNull("clazz"));

			try {
				getInstance();
				Object result = MAPPER.readValue(jsonStr, clazz);
				return clazz.cast(result);
			} catch (IOException e) {
				logger.error("", e);
			}
			return null;
		}
	}

	public static class ReadJsonFile {
		private ReadJsonFile() {
			super();
		}

		private static void validateFileName(String fileName) {
			if ( StringUtils.isBlank(fileName) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("fileName"));
			}
		}

		public static Object readJsonFileObject(String sfileName, TypeReference<?> typeReference) {
			validateFileName(sfileName);

			Objects.requireNonNull(typeReference, ExceptionMessage.isNull("typeReference"));

		    Object obj = null;

		    try {
		    	getInstance();
		        obj = MAPPER.readValue(new File(sfileName), typeReference);
		    } catch (IOException e) {
		        logger.error("Error reading JSON file", e);
		    }

		    return obj;
		}

		public static <T> List<T> readJsonFileArray(String sfileName, TypeReference<List<T>> typeReference) {
			validateFileName(sfileName);

			Objects.requireNonNull(typeReference, ExceptionMessage.isNull("typeReference"));

		    List<T> obj = null;

		    try {
		    	getInstance();
		        obj = MAPPER.readValue(new File(sfileName), typeReference);
		    } catch (IOException e) {
		        logger.error("Error reading JSON file", e);
		    }

		    return obj;
		}
	}

}
