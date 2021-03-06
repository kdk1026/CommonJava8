package common.util.push;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * <pre>
 * FCM 푸시 발송하는 유틸리티 클래스
 *   - Gson 이용
 *   - 옵션 및 페이로드 참고
 *     See <a href=
"https://firebase.google.com/docs/cloud-messaging/http-server-ref?hl=ko">Firebase 클라우드 메시징 HTTP 프로토콜</a>
 * </pre>
 * 
 * @see Gson
 * 
 * @since 2019. 3. 11.
 * @author 김대광
 * 
 *         <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2019. 3. 11. 김대광	최초작성
 *         </pre>
 */
public class FcmUtil {

	private static final Logger logger = LoggerFactory.getLogger(FcmUtil.class);

	/** 유니코드 지원 가능하도록 GsonBuilder로 생성 */
	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	/** Byte 버퍼 사이즈 */
	private static final int BYTE_BUFFER_SIZE = 8192;

	/** FCM HTTP 구성 */
	private final class FcmHttpConstants {
		static final String REQUEST_URL = "https://fcm.googleapis.com/fcm/send";
		static final String REQUEST_METHOD_POST = "POST";
		static final String CONTENT_TYPE = "Content-Type";
		static final String AUTHORIZATION = "Authorization";

		private final class Header {
			private final class ContentType {
				static final String APPLICATION_JSON_VALUE = "application/json";
			}

			private final class Authorization {
				static final String PREFIX = "key=";
			}
		}

		/** 대량 발송 시, 1회 최대 토큰 개수 */
		static final int ONE_REQUEST_MULTI_REGIST_TOKEN = 1000;
	}

	/** 메시지 구성 */
	public final class FcmMessageConfig {
		/** 수신 구분 */
		private final class Receiver {
			/** 단일 메시지 수신자 */
			static final String SINGLE = "to";

			/** 멀티캐스트 메시지 수신자 */
			static final String MULTI = "registration_ids";
		}

		/** 페이로드 구분 */
		private final class Payload {
			/** 백그라운드 및 포그라운드 상태 수신 (title, body) */
			static final String NOTIFICATION = "notification";

			/** 포그라운드 상태에서만 수신 (임의 Key-Value) */
			static final String DATA = "data";
		}

		/** notification 기본 키 */
		public final class NotoficationBasicField {
			private NotoficationBasicField() {
				super();
			}

			/** 알림 제목 */
			public static final String TITLE = "title";

			/** 알림 내용 */
			public static final String BODY = "body";
		}
	}

	/** FCM 주요 응답 메시지 */
	private final class FcmResponseMessage {
		static final String SUCCESS = "success";
		static final String FAILURE = "failure";
		static final String RESULTS = "results";
		static final String RESULTS_ERROR = "error";
	}

	/** 외부에서 객체 인스턴스화 불가 */
	private FcmUtil() {
		super();
	}

	/**
	 * Singleton 인스턴스 생성
	 * 
	 * @return
	 */
	public static FcmUtil getInstance() {
		return LazyHolder.INSTANCE;
	}

	/**
	 * LazyHolder Singleton 패턴
	 * 
	 * @return
	 */
	private static class LazyHolder {
		private static final FcmUtil INSTANCE = new FcmUtil();
	}

	/** FCM Server key (API key) */
	private String serverKey;

	public void setServerKey(String serverKey) {
		this.serverKey = serverKey;
	}

	/** FCM 전송 결과 */
	public static class FcmSendResult {
		/** 대상 토큰 */
		public String token;

		/** 성공 여부 */
		public boolean isSuccess;

		/** 에러 메시지 */
		public String errorMessage;

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
		}
	}

	/** FCM 대량 전송 결과 */
	public static class FcmMultiSendResult {
		/** 성공 개수 */
		public int successCnt;

		/** 실패 개수 */
		public int failureCnt;

		/** 처리 개수 */
		public int processCnt;

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
		}
	}

	/**
	 * Gson 을 이용하여 FCM 단일 전송
	 * 
	 * @param sToken
	 * @param sJsonNoti
	 * @param sJsonData
	 * @return
	 */
	public FcmSendResult sendMessage(String sToken, String sJsonNoti, String sJsonData) {
		FcmSendResult fcmSendResult = new FcmSendResult();

		// 페이로드 Json String을 Json Object로 변환
		JsonObject jNoti = gson.fromJson(sJsonNoti, JsonObject.class);
		JsonObject jData = gson.fromJson(sJsonData, JsonObject.class);

		JsonObject jObject = new JsonObject();

		// 메시지 설정
		jObject.add(FcmMessageConfig.Payload.NOTIFICATION, jNoti);
		jObject.add(FcmMessageConfig.Payload.DATA, jData);

		// 대상 설정
		jObject.addProperty(FcmMessageConfig.Receiver.SINGLE, sToken);

		// HTTP 요청
		String sResult = this.sendJsonHttpPost(jObject.toString());

		if (logger.isDebugEnabled()) {
			logger.debug("FCM Result is {}", sResult);
		}

		if (!sResult.equals("")) {
			JsonObject jResult = gson.fromJson(sResult, JsonObject.class);

			int iSuccess = jResult.get(FcmResponseMessage.SUCCESS).getAsInt();
			fcmSendResult.isSuccess = (iSuccess != 0);

			if (iSuccess == 0) {
				JsonArray results = jResult.getAsJsonArray(FcmResponseMessage.RESULTS);
				JsonObject result = results.get(0).getAsJsonObject();
				fcmSendResult.errorMessage = result.get(FcmResponseMessage.RESULTS_ERROR).toString();
			}

			fcmSendResult.token = sToken;
		}

		return fcmSendResult;
	}

	/**
	 * Gson 을 이용하여 FCM 대량 전송
	 * 
	 * @param sTokenList
	 * @param sJsonNoti
	 * @param sJsonData
	 * @return
	 */
	public FcmMultiSendResult sendMessage(List<String> sTokenList, String sJsonNoti, String sJsonData) {
		FcmMultiSendResult fcmMultiSendResult = new FcmMultiSendResult();

		// 페이로드 Json String을 Json Object로 변환
		JsonObject jNoti = gson.fromJson(sJsonNoti, JsonObject.class);
		JsonObject jData = gson.fromJson(sJsonData, JsonObject.class);

		JsonObject jObject = new JsonObject();

		// 메시지 설정
		jObject.add(FcmMessageConfig.Payload.NOTIFICATION, jNoti);
		jObject.add(FcmMessageConfig.Payload.DATA, jData);

		// 1회 최대 토큰 개수 단위로 나누어 처리
		int iTotalCnt = sTokenList.size();
		double dTotalIdx = Math.ceil((double) iTotalCnt / FcmHttpConstants.ONE_REQUEST_MULTI_REGIST_TOKEN);
		int iTotalIdx = (int) dTotalIdx;

		int iProcCnt = 0;
		int iMaxCnt = 0;
		JsonArray jArray = null;

		for (int i = 0; i <= iTotalIdx; i++) {
			jArray = new JsonArray();

			if (iTotalCnt - (FcmHttpConstants.ONE_REQUEST_MULTI_REGIST_TOKEN
					* i) >= FcmHttpConstants.ONE_REQUEST_MULTI_REGIST_TOKEN) {
				iMaxCnt = FcmHttpConstants.ONE_REQUEST_MULTI_REGIST_TOKEN;
			} else {
				iMaxCnt = iTotalCnt;
			}

			for (int j = 0; j < iMaxCnt; j++) {
				jArray.add(sTokenList.get(j));
				iProcCnt++;
			}

			// 대상 설정
			jObject.add(FcmMessageConfig.Receiver.MULTI, jArray);

			// HTTP 요청
			String sResult = this.sendJsonHttpPost(jObject.toString());

			if (logger.isDebugEnabled()) {
				logger.debug("FCM Result is {}", sResult);
			}

			if (!sResult.equals("")) {
				JsonObject jResult = gson.fromJson(sResult, JsonObject.class);

				int iSuccess = jResult.get(FcmResponseMessage.SUCCESS).getAsInt();
				int iFailure = jResult.get(FcmResponseMessage.FAILURE).getAsInt();

				fcmMultiSendResult.successCnt += iSuccess;
				fcmMultiSendResult.failureCnt += iFailure;

				// 1회 전송 후, 1초간 대기
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error("", e);
					Thread.currentThread().interrupt();
				}
			}

			fcmMultiSendResult.processCnt = iProcCnt;
		}

		return fcmMultiSendResult;
	}

	/**
	 * HTTP 요청
	 * 
	 * @param sJson
	 * @return
	 */
	private String sendJsonHttpPost(String sJson) {
		String sResult = "";
		HttpURLConnection conn = null;

		try {
			URL url = new URL(FcmHttpConstants.REQUEST_URL);

			conn = (HttpURLConnection) url.openConnection();
			conn.setUseCaches(false);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod(FcmHttpConstants.REQUEST_METHOD_POST);
			conn.setRequestProperty(FcmHttpConstants.CONTENT_TYPE,
					FcmHttpConstants.Header.ContentType.APPLICATION_JSON_VALUE);
			conn.setRequestProperty(FcmHttpConstants.AUTHORIZATION,
					FcmHttpConstants.Header.Authorization.PREFIX + this.serverKey);

			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
			osw.write(sJson);
			osw.flush();
			osw.close();

			int iStatus = conn.getResponseCode();

			if (iStatus == 200) {
				InputStream is = new BufferedInputStream(conn.getInputStream());

				int iRead = 0;
				byte[] buffer = new byte[BYTE_BUFFER_SIZE];

				while ((iRead = is.read(buffer)) != -1) {
					sResult = new String(buffer, 0, iRead);
				}

				is.close();

			} else {
				logger.info("FCM ResponseCode is {}", iStatus);
			}

		} catch (IOException e) {
			logger.error("", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return sResult;
	}

}
