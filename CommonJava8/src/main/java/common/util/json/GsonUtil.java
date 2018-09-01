package common.util.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * 작은 파일에 유리
 */
public class GsonUtil {
	
	private GsonUtil() {
		super();
	}

	private static final Logger logger = LoggerFactory.getLogger(GsonUtil.class);
	
	public static String prettyPrintString(String jsonStr) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(jsonStr);
	}
	
	public static String converterMapToJsonStr(Map<String, Object> map) {
		String jsonStr = "";

		/*
		 * XXX : <pre>유니코드 문제로 new Gson() 대신 new GsonBuilder().disableHtmlEscaping().create() 사용
		 */
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		try {
			jsonStr = gson.toJson(map);
		} catch (Exception e) {
			logger.error("", e);
		}
		return jsonStr;
	}

	public static String converterListToJsonStr(List<Map<String, Object>> list) {
		String jsonStr = "";

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		try {
			jsonStr = gson.toJson(list);
		} catch (Exception e) {
			logger.error("", e);
		}
		return jsonStr;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> converterJsonStrToMap(String jsonStr) {
		Map<String, Object> map = new HashMap<>();

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		try {
			map = gson.fromJson(jsonStr, Map.class);
		} catch (Exception e) {
			logger.error("", e);
		}
		return map;
	}
	
	public static JsonObject converterJsonStrToJsonObj(String jsonStr) {
		JsonObject jsonObj = null;
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		try {
			jsonObj = gson.fromJson(jsonStr, JsonObject.class);
		} catch (Exception e) {
			logger.error("", e);
		}
		return jsonObj;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> converterJsonStrToList(String jsonArrStr) {
		List<Map<String, Object>> list = new ArrayList<>();

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		try {
			list = gson.fromJson(jsonArrStr, List.class);
		} catch (Exception e) {
			logger.error("", e);
		}
		return list;
	}
	
	public static JsonArray converterJsonStrToJsonArray(String jsonArrStr) {
		JsonArray jsonArray = null;
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		try {
			jsonArray = gson.fromJson(jsonArrStr, JsonArray.class);
		} catch (Exception e) {
			logger.error("", e);
		}
		return jsonArray;
	}

}
