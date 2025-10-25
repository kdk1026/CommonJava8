package common.util.json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

/**
* <pre>
* -----------------------------------
* 개정이력
* -----------------------------------
* 2025. 5. 21. kdk	정리
* 2025. 5. 27. 김대광	유틸은 Singleton 패턴을 사용하지 않는 것이 좋다는 의견 반영, 제미나이에 의한 일부 코드 개선
* </pre>
*
* 작은 파일 파싱에 유리
*
* @author kdk
*/
public class GsonUtil {

	private static final Logger logger = LoggerFactory.getLogger(GsonUtil.class);

	private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final Gson NORMAL_GSON = new GsonBuilder().disableHtmlEscaping().create();

	private GsonUtil() {
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

	public static class ToJson {
		private ToJson() {
			super();
		}

		public static String converterObjToJsonStr(Object obj, boolean isPretty) {
			Objects.requireNonNull(obj, ExceptionMessage.isNull("obj"));

			return isPretty ? PRETTY_GSON.toJson(obj) : NORMAL_GSON.toJson(obj);
		}

		public static String converterMapToJsonStr(Map<String, Object> map, boolean isPretty) {
			if ( map == null || map.isEmpty() ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("map"));
			}

			return converterObjToJsonStr(map, isPretty);
		}

		public static String converterListToJsonStr(List<?> list, boolean isPretty) {
			if ( list == null || list.isEmpty() ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("list"));
			}

			return converterObjToJsonStr(list, isPretty);
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
		public static Map<String, Object> converterJsonStrToMap(String sJson) {
			validateJsonString(sJson);

			return NORMAL_GSON.fromJson(sJson, Map.class);
		}

		public static JsonObject converterJsonStrToJsonObj(String sJson) {
			validateJsonString(sJson);

			return NORMAL_GSON.fromJson(sJson, JsonObject.class);
		}

		@SuppressWarnings("unchecked")
		public static <T> List<T> converterJsonStrToList(String sJsonArr) {
			validateJsonArrayString(sJsonArr);

			return NORMAL_GSON.fromJson(sJsonArr, List.class);
		}

		public static JsonArray converterJsonStrToJsonArray(String sJsonArr) {
			validateJsonArrayString(sJsonArr);

			return NORMAL_GSON.fromJson(sJsonArr, JsonArray.class);
		}

		public static <T> T converterJsonStrToClass(String jsonStr, Class<T> clazz) {
			validateJsonString(jsonStr);

			Objects.requireNonNull(clazz, ExceptionMessage.isNull("clazz"));

			return NORMAL_GSON.fromJson(jsonStr, clazz);
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

		public static Object readJsonFileObject(String sfileName, Type type) {
			validateFileName(sfileName);

			Objects.requireNonNull(type, ExceptionMessage.isNull("type"));

			Object obj = null;

			try (JsonReader reader = new JsonReader(new FileReader(sfileName))) {
                obj = NORMAL_GSON.fromJson(reader, type);
            } catch (FileNotFoundException e) {
                logger.error("JSON 파일을 찾을 수 없습니다: {}", sfileName, e);
            } catch (IOException e) {
                logger.error("JSON 파일 읽기 중 오류 발생: {}", sfileName, e);
            }

			return obj;
		}

		@SuppressWarnings("unchecked")
		public static <T> List<T> readJsonFileArray(String sfileName, Type type) {
			validateFileName(sfileName);

			Objects.requireNonNull(type, ExceptionMessage.isNull("type"));

			Object obj = null;

            try (JsonReader reader = new JsonReader(new FileReader(sfileName))) {
                obj = NORMAL_GSON.fromJson(reader, type);
            } catch (FileNotFoundException e) {
                logger.error("JSON 파일을 찾을 수 없습니다: {}", sfileName, e);
            } catch (IOException e) {
                logger.error("JSON 파일 읽기 중 오류 발생: {}", sfileName, e);
            }
            return (List<T>) obj;
		}
	}

}
