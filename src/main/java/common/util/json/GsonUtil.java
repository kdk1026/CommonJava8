package common.util.json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

/**
 * 작은 파일에 유리
 */
public class GsonUtil {

	private GsonUtil() {
		super();
	}

	private static GsonUtil instance;
	private Gson gson;

	private static final Logger logger = LoggerFactory.getLogger(GsonUtil.class);

	private static synchronized GsonUtil getInstance(boolean isPretty) {
        if (instance == null) {
			instance = new GsonUtil();
			// 유니코드 지원안함
//			instance.gson = new Gson();

			if (isPretty) {
				instance.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
			} else {
				instance.gson = new GsonBuilder().disableHtmlEscaping().create();
			}
        }

        return instance;
    }

	public static class ToJson {
		public static String converterObjToJsonStr(Object obj, boolean isPretty) {
			if ( obj == null ) {
				throw new IllegalArgumentException("obj is null");
			}

			String sJson = "";

			try {
				getInstance(isPretty);
				sJson = instance.gson.toJson(obj);
			} catch (Exception e) {
				logger.error("", e);
			}

			return sJson;
		}

		public static String converterMapToJsonStr(Map<String, Object> map, boolean isPretty) {
			if ( map == null || map.isEmpty() ) {
				throw new IllegalArgumentException("map is null");
			}

			return converterObjToJsonStr(map, isPretty);
		}

		public static String converterListToJsonStr(List<?> list, boolean isPretty) {
			if ( list == null || list.isEmpty() ) {
				throw new IllegalArgumentException("list is null");
			}

			return converterObjToJsonStr(list, isPretty);
		}
	}

	public static class FromJson {
		@SuppressWarnings("unchecked")
		public static Map<String, Object> converterJsonStrToMap(String sJson) {
			if ( StringUtils.isBlank(sJson) ) {
				throw new IllegalArgumentException("sJson is null");
			}

			Map<String, Object> map = new HashMap<>();

			try {
				getInstance(false);
				map = instance.gson.fromJson(sJson, Map.class);
			} catch (Exception e) {
				logger.error("", e);
			}

			return map;
		}

		public static JsonObject converterJsonStrToJsonObj(String sJson) {
			if ( StringUtils.isBlank(sJson) ) {
				throw new IllegalArgumentException("sJson is null");
			}

			JsonObject jsonObj = null;

			try {
				getInstance(false);
				jsonObj = instance.gson.fromJson(sJson, JsonObject.class);
			} catch (Exception e) {
				logger.error("", e);
			}

			return jsonObj;
		}

		public static List<?> converterJsonStrToList(String sJsonArr) {
			if ( StringUtils.isBlank(sJsonArr) ) {
				throw new IllegalArgumentException("sJsonArr is null");
			}

			List<?> list = new ArrayList<>();

			try {
				getInstance(false);
				list = instance.gson.fromJson(sJsonArr, List.class);
			} catch (Exception e) {
				logger.error("", e);
			}

			return list;
		}

		public static JsonArray converterJsonStrToJsonArray(String sJsonArr) {
			if ( StringUtils.isBlank(sJsonArr) ) {
				throw new IllegalArgumentException("sJsonArr is null");
			}

			JsonArray jsonArray = null;

			try {
				getInstance(false);
				jsonArray = instance.gson.fromJson(sJsonArr, JsonArray.class);
			} catch (Exception e) {
				logger.error("", e);
			}

			return jsonArray;
		}

		public static <T> T converterJsonStrToClass(String jsonStr, Class<T> clazz) {
			if ( StringUtils.isBlank(jsonStr) ) {
				throw new IllegalArgumentException("jsonStr is null");
			}

			if ( clazz == null ) {
				throw new IllegalArgumentException("clazz is null");
			}

			try {
				getInstance(false);
				Object result = instance.gson.fromJson(jsonStr, clazz);
				return clazz.cast(result);
			} catch (Exception e) {
				logger.error("", e);
			}
			return null;
		}
	}

	public static class ReadJsonFile {
		public static Object readJsonFileObject(String sfileName, Type type) {
			if ( StringUtils.isBlank(sfileName) ) {
				throw new IllegalArgumentException("sfileName is null");
			}

			if ( type == null ) {
				throw new IllegalArgumentException("type is null");
			}

			Object obj = null;

			try {
				getInstance(false);
				JsonReader reader = new JsonReader(new FileReader(sfileName));
				obj = instance.gson.fromJson(reader, type);
			} catch (FileNotFoundException e) {
				logger.error("", e);
			}

			return obj;
		}

		@SuppressWarnings("unchecked")
		public static <T> List<T> readJsonFileArray(String sfileName, Type type) {
			if ( StringUtils.isBlank(sfileName) ) {
				throw new IllegalArgumentException("sfileName is null");
			}

			if ( type == null ) {
				throw new IllegalArgumentException("type is null");
			}

			Object obj = null;

			try {
				getInstance(false);
				JsonReader reader = new JsonReader(new FileReader(sfileName));
				obj = instance.gson.fromJson(reader, type);
			} catch (FileNotFoundException e) {
				logger.error("", e);
			}

			return (List<T>) obj;
		}
	}

}
