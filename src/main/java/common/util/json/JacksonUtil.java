package common.util.json;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import common.util.ExceptionMessage;

 /**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 5. 21. kdk	정리
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

	private static JacksonUtil instance;
    private ObjectMapper mapper;

	private static final Logger logger = LoggerFactory.getLogger(JacksonUtil.class);

	private static synchronized JacksonUtil getInstance() {
        if (instance == null) {
			instance = new JacksonUtil();
			instance.mapper = new ObjectMapper();
        }

        return instance;
    }

	public static class ToJson {
		private ToJson() {
			super();
		}

		public static String converterObjToJsonStr(Object obj, boolean isPretty) {
			if ( obj == null ) {
				throw new IllegalArgumentException(ExceptionMessage.isNull("obj"));
			}

			String jsonStr = "";

			try {
				getInstance();
				if (!isPretty) {
					jsonStr = instance.mapper.writeValueAsString(obj);
				} else {
					jsonStr = instance.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
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
					jsonStr = instance.mapper.writeValueAsString(map);
				} else {
					jsonStr = instance.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
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
					jsonStr = instance.mapper.writeValueAsString(list);
				} else {
					jsonStr = instance.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
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
			return instance.mapper.valueToTree(map);
		}

		public static JsonNode converterListToJsonNode(List<?> list) {
			if ( list == null || list.isEmpty() ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("list"));
			}

			getInstance();
			return instance.mapper.valueToTree(list);
		}
	}

	public static class FromJson {
		private FromJson() {
			super();
		}

		@SuppressWarnings("unchecked")
		public static Map<String, Object> converterJsonStrToMap(String jsonStr) {
			if ( StringUtils.isBlank(jsonStr) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNull("jsonStr"));
			}

			Map<String, Object> map = new HashMap<>();

			try {
				getInstance();
				map = instance.mapper.readValue(jsonStr, Map.class);
			} catch (IOException e) {
				logger.error("", e);
			}
			return map;
		}

		public static JsonNode converterJsonStrToJsonNode(String jsonStr) {
			if ( StringUtils.isBlank(jsonStr) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNull("jsonStr"));
			}

			JsonNode jsonNode = null;

			try {
				getInstance();
				jsonNode = instance.mapper.readTree(jsonStr);
			} catch (IOException e) {
				logger.error("", e);
			}
			return jsonNode;
		}

		@SuppressWarnings("unchecked")
		public static <T> List<T> converterJsonStrToList(String jsonArrStr) {
			if ( StringUtils.isBlank(jsonArrStr) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNull("jsonArrStr"));
			}

			List<T> list = new ArrayList<>();

			try {
				getInstance();
				list = instance.mapper.readValue(jsonArrStr, List.class);
			} catch (IOException e) {
				logger.error("", e);
			}
			return list;
		}

		public static ArrayNode converterJsonStrToArayNode(String jsonArrStr) {
			if ( StringUtils.isBlank(jsonArrStr) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNull("jsonArrStr"));
			}

			ArrayNode arrayNode = null;

			try {
				getInstance();
				arrayNode = (ArrayNode) instance.mapper.readTree(jsonArrStr);
			} catch (IOException e) {
				logger.error("", e);
			}
			return arrayNode;
		}

		public static <T> T converterJsonStrToClass(String jsonStr, Class<T> clazz) {
			if ( StringUtils.isBlank(jsonStr) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNull("jsonStr"));
			}

			if ( clazz == null ) {
				throw new IllegalArgumentException(ExceptionMessage.isNull("clazz"));
			}

			try {
				getInstance();
				Object result = instance.mapper.readValue(jsonStr, clazz);
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

		public static Object readJsonFileObject(String sfileName, TypeReference<?> typeReference) {
			if ( StringUtils.isBlank(sfileName) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNull("sfileName"));
			}

		    if (typeReference == null) {
		    	throw new IllegalArgumentException(ExceptionMessage.isNull("typeReference"));
		    }

		    Object obj = null;

		    try {
		    	getInstance();
		        obj = instance.mapper.readValue(new File(sfileName), typeReference);
		    } catch (IOException e) {
		        logger.error("Error reading JSON file", e);
		    }

		    return obj;
		}

		public static <T> List<T> readJsonFileArray(String sfileName, TypeReference<List<T>> typeReference) {
			if ( StringUtils.isBlank(sfileName) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNull("sfileName"));
			}

		    if (typeReference == null) {
		    	throw new IllegalArgumentException(ExceptionMessage.isNull("typeReference"));
		    }

		    List<T> obj = null;

		    try {
		    	getInstance();
		        obj = instance.mapper.readValue(new File(sfileName), typeReference);
		    } catch (IOException e) {
		        logger.error("Error reading JSON file", e);
		    }

		    return obj;
		}
	}

}
