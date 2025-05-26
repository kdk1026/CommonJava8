package common.util.push;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import common.util.date.Jsr310DateUtil;
import common.util.http.HttpClientUtil;
import common.util.properties.PropertiesUtil;

// XXX - 1000건 이상 발송 확인
// XXX - iOS 발송 확인

 /**
  * <pre>
  *  firebase-admin 이용으로 변경되어 사용 불가
  * </pre>
 */
@Deprecated
public class FcmPushUtil {

	private static final Logger logger = LoggerFactory.getLogger(FcmPushUtil.class);

	private static String SERVER_KEY;
	private static List<String> regList;
	private static List<Map<String, Object>> rtnList;

	private FcmPushUtil() {
		super();
	}

	private static final String FCM_PROPERTIES_PATH = "fcm/fcm.properties";
	private static final String FCM_SERVER_KEY_NAME = "FCMserverAPIKey";

	private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
	private static final int MAX_SEND_CNT = 999;	// 1회 최대 전송 가능 수 (1000건까지 가능)

	private static ObjectMapper mapper = new ObjectMapper();

	/**
	 * Android
	 */
	public static final int PUSH_TYPE_0 = 0;
	/**
	 * iOS
	 */
	public static final int PUSH_TYPE_1 = 1;

	public static List<Map<String, Object>> sendPush(List<String> regIdList, String jsonStr, int pushType) {
		Properties prop = PropertiesUtil.getPropertiesClasspath(FCM_PROPERTIES_PATH);

		if ( (prop != null) && (prop.containsKey(FCM_SERVER_KEY_NAME)) ) {
			SERVER_KEY = prop.getProperty(FCM_SERVER_KEY_NAME);

			String sJson = setJson(regIdList, jsonStr, pushType);

			regList = regIdList;
			beforeSend(sJson);
		} else {
			rtnList = new ArrayList<>();
		}

		return rtnList;
	}

	private static String setJson(List<String> regIdList, String jsonStr, int pushType) {
		String sJson = "";

		String sRegIds = setRegIds(regIdList);
		String sMessage = setMessage(jsonStr, pushType);

		try {
			ObjectNode msgNode = (ObjectNode) mapper.readTree(sMessage);
			JsonNode idNode = mapper.readTree(sRegIds);

			msgNode.set("registration_ids", idNode);
			sJson = msgNode.toString();

		} catch (Exception e) {
			logger.error("", e);
		}

		return sJson;
	}

	private static String setRegIds(List<String> regIdList) {
		String sRegIds = "";

		try {
			ArrayNode array = mapper.createArrayNode();

			for (String str : regIdList) {
				array.add(str);
			}

			sRegIds = array.toString();

		} catch (Exception e) {
			logger.error("", e);
		}

		return sRegIds;
	}

	private static String setMessage(String jsonStr, int pushType) {
		String sMessage = "";

		try {
			ObjectNode data = mapper.createObjectNode();

			ObjectNode node = mapper.createObjectNode();
			node.put("message", jsonStr);

			if (pushType == 0) {
				data.set("data", node);
			} else {
				data.set("notification", node);
			}

			sMessage = data.toString();

		} catch (Exception e) {
			logger.error("", e);
		}

		return sMessage;
	}

	private static void beforeSend(String rawPayload) {
		if (regList != null) {
			if (regList.size() <= MAX_SEND_CNT) {
				sendMulticastResult(regList, rawPayload);
			} else {
				List<String> regListTemp = new ArrayList<>();
				for (int i=0; i < regList.size(); i++) {
					if ( ((i+1) % MAX_SEND_CNT) == 0 ) {
						sendMulticastResult(regListTemp, rawPayload);
						regListTemp.clear();
					}
					regListTemp.add(regList.get(i));
				}

				// 남은 것 보내기
				if ( !regListTemp.isEmpty() ) {
					sendMulticastResult(regListTemp, rawPayload);
				}
			}
		}
	}

	private static void sendMulticastResult(List<String> list, String rawPayload) {
		rtnList = new ArrayList<>();
		Map<String, Object> rtnMap = null;

		String sAuthorization = "key=" + SERVER_KEY;
		Map<String, Object> header = new HashMap<>();
		header.put("Authorization", sAuthorization);

		String sRes = HttpClientUtil.RawRequest.json(true, FCM_URL, header, rawPayload);
		String sendDate = Jsr310DateUtil.Today.getTodayString("yyyy-MM-dd HH:mm:ss");

		try {
			JsonNode rtnNode = mapper.readTree(sRes);
			ArrayNode results = (ArrayNode) rtnNode.get("results");

			for (int i=0; i < list.size(); i++) {
				JsonNode result = results.get(i);

				String messageId = "";
				String error = "";
				boolean isSuccess = false;

				if ( result.has("message_id") ) {
					messageId = result.get("message_id").asText();
					isSuccess = true;
				}

				if ( result.has("error") ) {
					error = result.get("error").asText();
				}

				rtnMap = new HashMap<>();
				rtnMap.put("regId", list.get(i));
				rtnMap.put("msgId", messageId);
				rtnMap.put("errMsg", error);
				rtnMap.put("sendDt", sendDate);
				rtnMap.put("resFlag", isSuccess);

				logger.info("[FCM PUSH] - {}", rtnMap);

				rtnList.add(rtnMap);
			}

		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
