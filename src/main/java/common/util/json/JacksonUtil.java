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

import com.fasterxml.jackson.core.type.TypeReference;
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
		if ( StringUtils.isBlank(jsonStr) ) {
			throw new NullPointerException("jsonStr is null");
		}

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
			if ( obj == null ) {
				throw new NullPointerException("obj is null");
			}

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
			if ( map == null || map.isEmpty() ) {
				throw new NullPointerException("map is null");
			}

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
			if ( list == null || list.isEmpty() ) {
				throw new NullPointerException("list is null");
			}

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
			if ( map == null || map.isEmpty() ) {
				throw new NullPointerException("map is null");
			}

			ObjectMapper mapper = new ObjectMapper();
			return mapper.valueToTree(map);
		}

		public static JsonNode converterListToJsonNode(List<?> list) {
			if ( list == null || list.isEmpty() ) {
				throw new NullPointerException("list is null");
			}

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
			if ( StringUtils.isBlank(jsonStr) ) {
				throw new NullPointerException("jsonStr is null");
			}

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
			if ( StringUtils.isBlank(jsonStr) ) {
				throw new NullPointerException("jsonStr is null");
			}

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
			if ( StringUtils.isBlank(jsonArrStr) ) {
				throw new NullPointerException("jsonArrStr is null");
			}

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
			if ( StringUtils.isBlank(jsonArrStr) ) {
				throw new NullPointerException("jsonArrStr is null");
			}

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
			if ( StringUtils.isBlank(jsonStr) ) {
				throw new NullPointerException("jsonStr is null");
			}

			if ( clazz == null ) {
				throw new NullPointerException("clazz is null");
			}

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

	public static class ReadJsonFile {

		protected ReadJsonFile() {
			super();
		}

		public static Object readJsonFileObject(String sfileName, TypeReference<?> typeReference) {
			if ( StringUtils.isBlank(sfileName) ) {
				throw new NullPointerException("sfileName is null");
			}

		    if (typeReference == null) {
		        throw new NullPointerException("typeReference is null");
		    }

		    Object obj = null;
		    ObjectMapper objectMapper = new ObjectMapper();

		    try {
		        obj = objectMapper.readValue(new File(sfileName), typeReference);
		    } catch (IOException e) {
		        logger.error("Error reading JSON file", e);
		    }

		    return obj;
		}

		public static <T> List<T> readJsonFileArray(String sfileName, TypeReference<List<T>> typeReference) {
			if ( StringUtils.isBlank(sfileName) ) {
				throw new NullPointerException("sfileName is null");
			}

		    if (typeReference == null) {
		        throw new NullPointerException("typeReference is null");
		    }

		    List<T> obj = null;
		    ObjectMapper objectMapper = new ObjectMapper();

		    try {
		        obj = objectMapper.readValue(new File(sfileName), typeReference);
		    } catch (IOException e) {
		        logger.error("Error reading JSON file", e);
		    }

		    return obj;
		}

	}

}
