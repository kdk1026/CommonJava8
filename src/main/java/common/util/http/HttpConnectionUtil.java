package common.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 성능 및 SSL 성공률이 다소 뒤처지므로 부득이한 경우가 아니면 Apache HttpClient 사용 권장
 */
/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 13. 김대광	SonarLint 지시에 따른 주저리 주저리 (Complexity 어쩔 수 없고, try-with-resources 로 바꾸기에는 좀 크다...)
 * 2024.10. 21. 김대광	Java 17 이상 deprecated 대응
 * </pre>
 *
 *
 * @author 김대광
 */
public class HttpConnectionUtil {

	private static final Logger logger = LoggerFactory.getLogger(HttpConnectionUtil.class);

	private HttpConnectionUtil() {
		super();
	}

	/**
	 * UTF-8
	 * @since 1.7
	 */
	private static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.toString();

	public static final String STATUS_KEY = "status";
	public static final String BODY_KEY = "body";
	public static final String HEADERS_KEY = "headers";

	private static final String GET_METHOD = "GET";
	private static final String POST_METHOD = "POST";
	private static final String CONTENT_TYPE = "Content-Type";

	private static final int DEFAULT_TIMEOUT_MILLIS = 5000;

	private static final String URL_IS_NULL = "URL is null";
	private static final String PAYLOAD_IS_NULL = "payload is null";
	private static final String TIMEOUT_IS_NULL = "timeout is null";
	private static final String CHARSET_IS_NULL = "charset is null";

	private static final String LOG_INVALID_URL = "잘못된 URL 또는 URI: {}";
	private static final String LOG_IO_EXCEPTION1 = "{} 에서 응답을 읽는 중 오류 발생: {}";
	private static final String LOG_IO_EXCEPTION2 = "{} 에 대한 오류 응답 본문: {}";
	private static final String LOG_IO_EXCEPTION3 = "{} 에서 오류 스트림을 읽는 중 오류 발생: {}";

    /**
     * URLConnection 객체를 생성하고 연결 및 읽기 타임아웃을 설정합니다.
     * @param url
     * @param timeoutMillis
     * @return
     * @throws IOException
     */
    private static URLConnection getURLConnection(URL url, int timeoutMillis) throws IOException {
        URLConnection httpConn = url.openConnection();
        httpConn.setConnectTimeout(timeoutMillis); // 연결 타임아웃 설정
        httpConn.setReadTimeout(timeoutMillis);    // 읽기 타임아웃 설정
        return httpConn;
    }

	public static class GetRequest {
		private GetRequest() {
			super();
		}

		/**
		 * <pre>
		 * StatusCode, Body, Headers
		 *  - 키 : HttpConnectionUtil.STATUS_KEY, HttpConnectionUtil.BODY_KEY, HttpConnectionUtil.HEADERS_KEY
		 * </pre>
		 * @param isSSL
		 * @param sUrl
		 * @param header
		 * @param timeoutMillis
		 * @return
		 */
		private static Map<String, Object> getMap(boolean isSSL, String sUrl, Map<String, Object> header, int timeoutMillis) {
			Objects.requireNonNull(sUrl.trim(), URL_IS_NULL);

			Map<String, Object> resMap = new HashMap<>();
			URL url = null;

            try {
                URI uri = new URI(sUrl);
                url = uri.toURL();
            } catch (URISyntaxException | MalformedURLException e) {
                logger.error(LOG_INVALID_URL, sUrl, e);
                return resMap;
            }

            // AutoCloseable 대상이 아님
            HttpURLConnection httpConn = null;
			try {
				httpConn = (HttpURLConnection) getURLConnection(url, timeoutMillis);

				httpConn.setRequestMethod(GET_METHOD);

				if (isSSL) {
					@SuppressWarnings("unused")
					HttpsURLConnection httpsConn = (HttpsURLConnection) httpConn;
				}

				if (header != null) {
					for ( Map.Entry<String, Object> entry : header.entrySet() ) {
                        httpConn.setRequestProperty(entry.getKey(), String.valueOf(entry.getValue()));
                    }
				}

				int nStatus = httpConn.getResponseCode();
                logger.info("{} 에 대한 GET 상태: {}", sUrl, nStatus);

                String sResponse = getResponseBody(sUrl, httpConn);

                Map<String, List<String>> resHeader = httpConn.getHeaderFields();

                resMap.put(STATUS_KEY, nStatus);
                resMap.put(BODY_KEY, sResponse);
                resMap.put(HEADERS_KEY, resHeader);

			} catch (IOException e) {
				logger.error("{} 에 대한 GET 요청 중 IO 오류 발생: {}", sUrl, e.getMessage(), e);
			} finally {
				if (httpConn != null) {
                    httpConn.disconnect();
                }
			}

			return resMap;
		}

		private static String getResponseBody(String sUrl, HttpURLConnection httpConn) {
			String sResponse = "";

			try (
            		InputStream is = httpConn.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is, DEFAULT_CHARSET);
                    BufferedReader br = new BufferedReader(isr)
            ) {

               StringBuilder responseBuilder = new StringBuilder();
               String line;
               // 응답 본문 읽기
               while ((line = br.readLine()) != null) {
                   responseBuilder.append(line);
               }
               sResponse = responseBuilder.toString();
			} catch (IOException e) {
				logger.error(LOG_IO_EXCEPTION1, sUrl, e);

				// 오류 스트림이 있다면 읽어오기 시도
				try (
				   InputStream errorStream = httpConn.getErrorStream();
				   InputStreamReader errorIsr = new InputStreamReader(errorStream, DEFAULT_CHARSET);
				   BufferedReader errorBr = new BufferedReader(errorIsr)
				) {
					StringBuilder errorResponseBuilder = new StringBuilder();
					String errorLine;
					while ( (errorLine = errorBr.readLine()) != null ) {
						errorResponseBuilder.append(errorLine);
					}
					sResponse = errorResponseBuilder.toString();
					logger.error(LOG_IO_EXCEPTION2, sUrl, sResponse);
				} catch (IOException innerE) {
					logger.error(LOG_IO_EXCEPTION3, sUrl, innerE);
				}
           	}
			return sResponse;
		}

		public static String get(boolean isSSL, String url, Map<String, Object> header, int timeoutMillis) {
			Objects.requireNonNull(url.trim(), URL_IS_NULL);

			if ( header == null || header.isEmpty() ) {
				throw new IllegalArgumentException("Header map cannot be null or empty when making a GET request.");
			}

			Objects.requireNonNull(timeoutMillis, TIMEOUT_IS_NULL);

            Map<String, Object> resMap = getMap(isSSL, url, header, timeoutMillis);
            return (resMap.isEmpty() || resMap.get(BODY_KEY) == null) ? "" : resMap.get(BODY_KEY).toString();
        }

		public static String get(boolean isSSL, String url, Map<String, Object> header) {
			Objects.requireNonNull(url.trim(), URL_IS_NULL);

			if ( header == null || header.isEmpty() ) {
				throw new IllegalArgumentException("Header map cannot be null or empty when making a GET request.");
			}

            return get(isSSL, url, header, DEFAULT_TIMEOUT_MILLIS);
        }

		public static String get(boolean isSSL, String url) {
			Objects.requireNonNull(url.trim(), URL_IS_NULL);

            return get(isSSL, url, null, DEFAULT_TIMEOUT_MILLIS);
        }
	}

	public static class PostRequest {
		private PostRequest() {
			super();
		}

		private static String convertParam(Map<String, Object> param) {
			if ( param == null || param.isEmpty() ) {
                return "";
            }

			StringBuilder sb = new StringBuilder();
            boolean first = true;

            for ( Map.Entry<String, Object> entry : param.entrySet() ) {
                if (!first) {
                    sb.append("&");
                }
                first = false;

                String key = entry.getKey();
                Object value = entry.getValue();

                // 값이 배열인 경우 (예: String[]), 각 요소를 파라미터로 추가
                if (value.getClass().isArray()) {
                    // String[] 배열이라고 가정 (다른 배열 타입이 필요하면 확장)
                    String[] arr = (String[]) value;
                    for (int i = 0; i < arr.length; i++) {
                        if (i > 0) {
                            sb.append("&");
                        }
                        sb.append(key).append("=").append(arr[i]);
                    }
                } else {
                    sb.append(key).append("=").append(value);
                }
            }
            return sb.toString();
		}

		/**
		 * <pre>
		 * StatusCode, Body, Headers
		 *  - 키 : HttpConnectionUtil.STATUS_KEY, HttpConnectionUtil.BODY_KEY, HttpConnectionUtil.HEADERS_KEY
		 * </pre>
		 * @param isSSL
		 * @param sUrl
		 * @param header
		 * @param param
		 * @param charset
		 * @param timeoutMillis
		 * @return
		 */
		private static Map<String, Object> postMap(boolean isSSL, String sUrl, Map<String, Object> header, Map<String, Object> param, Charset charset, int timeoutMillis) {
			Objects.requireNonNull(sUrl.trim(), URL_IS_NULL);

            Map<String, Object> resMap = new HashMap<>();
            URL url = null;

            try {
                URI uri = new URI(sUrl);
                url = uri.toURL();
            } catch (URISyntaxException | MalformedURLException e) {
                logger.error(LOG_INVALID_URL, sUrl, e);
                return resMap;
            }

            HttpURLConnection httpConn = null;
            try {
                httpConn = (HttpURLConnection) getURLConnection(url, timeoutMillis);

                httpConn.setRequestMethod(POST_METHOD);

                if (isSSL) {
					@SuppressWarnings("unused")
					HttpsURLConnection httpsConn = (HttpsURLConnection) httpConn;
				}

                // POST 요청의 기본 Content-Type 설정
                httpConn.setRequestProperty(CONTENT_TYPE, "application/x-www-form-urlencoded");
                httpConn.setDoOutput(true); // POST 요청을 위해 출력 스트림 활성화

                // 요청 헤더 설정
                if (header != null) {
                    for (Map.Entry<String, Object> entry : header.entrySet()) {
                        httpConn.setRequestProperty(entry.getKey(), String.valueOf(entry.getValue()));
                    }
                }

                // 파라미터가 있다면 출력 스트림을 통해 전송
                if (param != null && !param.isEmpty()) {
                    String sParam = convertParam(param);
                    Charset effectiveCharset = (charset != null) ? charset : StandardCharsets.UTF_8; // 문자셋 선택
                    // try-with-resources를 사용하여 OutputStream 자동 닫기
                    try (OutputStream os = httpConn.getOutputStream()) {
                        os.write(sParam.getBytes(effectiveCharset)); // 파라미터 바이트로 변환하여 쓰기
                    }
                }

                // 응답 상태 코드 가져오기
                int nStatus = httpConn.getResponseCode();
                logger.info("{} 에 대한 POST 상태: {}", sUrl, nStatus);

                String sResponse = postResponseBody(sUrl, httpConn);

                // 응답 헤더 가져오기
                Map<String, List<String>> resHeader = httpConn.getHeaderFields();

                // 결과 맵에 상태 코드, 본문, 헤더 추가
                resMap.put(STATUS_KEY, nStatus);
                resMap.put(BODY_KEY, sResponse);
                resMap.put(HEADERS_KEY, resHeader);

            } catch (IOException e) {
                logger.error("{} 에 대한 POST 요청 중 IO 오류 발생: {}", sUrl, e.getMessage(), e);
            } finally {
                // HttpURLConnection 연결 해제
                if (httpConn != null) {
                    httpConn.disconnect();
                }
            }
            return resMap;
        }

		private static String postResponseBody(String sUrl, HttpURLConnection httpConn) {
			String sResponse = "";

			try (
					InputStream is = httpConn.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is, DEFAULT_CHARSET);
                    BufferedReader br = new BufferedReader(isr)
			) {
               StringBuilder responseBuilder = new StringBuilder();
               String line;
               // 응답 본문 읽기
               while ((line = br.readLine()) != null) {
                   responseBuilder.append(line);
               }
               sResponse = responseBuilder.toString();
			} catch (IOException e) {
               logger.error(LOG_IO_EXCEPTION1, sUrl, e);
               // 오류 스트림이 있다면 읽어오기 시도
               try (InputStream errorStream = httpConn.getErrorStream();
                    InputStreamReader errorIsr = new InputStreamReader(errorStream, DEFAULT_CHARSET);
                    BufferedReader errorBr = new BufferedReader(errorIsr)) {
                   StringBuilder errorResponseBuilder = new StringBuilder();
                   String errorLine;
                   while ((errorLine = errorBr.readLine()) != null) {
                       errorResponseBuilder.append(errorLine);
                   }
                   sResponse = errorResponseBuilder.toString();
                   logger.error(LOG_IO_EXCEPTION2, sUrl, sResponse);
               } catch (IOException innerE) {
                   logger.error(LOG_IO_EXCEPTION3, sUrl, innerE);
               }
			}
			return sResponse;
		}

		public static Map<String, Object> postMap(boolean isSSL, String url, Map<String, Object> header, Map<String, Object> param, Charset charset) {
			Objects.requireNonNull(url.trim(), URL_IS_NULL);

			if (header == null || header.isEmpty()) {
				throw new IllegalArgumentException("Header map cannot be null or empty when making a POST request.");
			}

			if ( param == null || param.isEmpty() ) {
				throw new IllegalArgumentException("Parameter map cannot be null or empty when making a POST request.");
			}

			Objects.requireNonNull(charset, CHARSET_IS_NULL);

            return postMap(isSSL, url, header, param, charset, DEFAULT_TIMEOUT_MILLIS);
        }

		public static String post(boolean isSSL, String url, Map<String, Object> header, Map<String, Object> param, Charset charset, int timeoutMillis) {
			Objects.requireNonNull(url.trim(), URL_IS_NULL);

			if (header == null || header.isEmpty()) {
				throw new IllegalArgumentException("Header map cannot be null or empty when making a POST request.");
			}

			if ( param == null || param.isEmpty() ) {
				throw new IllegalArgumentException("Parameter map cannot be null or empty when making a POST request.");
			}

			Objects.requireNonNull(charset, CHARSET_IS_NULL);
			Objects.requireNonNull(timeoutMillis, TIMEOUT_IS_NULL);

            Map<String, Object> resMap = postMap(isSSL, url, header, param, charset, timeoutMillis);
            return (resMap.isEmpty() || resMap.get(BODY_KEY) == null) ? "" : resMap.get(BODY_KEY).toString();
        }

		public static String post(boolean isSSL, String url, Map<String, Object> header, Map<String, Object> param, Charset charset) {
			Objects.requireNonNull(url.trim(), URL_IS_NULL);

			if (header == null || header.isEmpty()) {
				throw new IllegalArgumentException("Header map cannot be null or empty when making a POST request.");
			}

			if ( param == null || param.isEmpty() ) {
				throw new IllegalArgumentException("Parameter map cannot be null or empty when making a POST request.");
			}

			Objects.requireNonNull(charset, CHARSET_IS_NULL);

            return post(isSSL, url, header, param, charset, DEFAULT_TIMEOUT_MILLIS);
        }

		public static String post(boolean isSSL, String url, Map<String, Object> param, Charset charset) {
			Objects.requireNonNull(url.trim(), URL_IS_NULL);

			if ( param == null || param.isEmpty() ) {
				throw new IllegalArgumentException("Parameter map cannot be null or empty when making a POST request.");
			}

			Objects.requireNonNull(charset, CHARSET_IS_NULL);

            return post(isSSL, url, null, param, charset, DEFAULT_TIMEOUT_MILLIS);
        }

		public static String post(boolean isSSL, String url, Map<String, Object> param) {
			Objects.requireNonNull(url.trim(), URL_IS_NULL);

			if ( param == null || param.isEmpty() ) {
				throw new IllegalArgumentException("Parameter map cannot be null or empty when making a POST request.");
			}

            return post(isSSL, url, null, param, null, DEFAULT_TIMEOUT_MILLIS);
        }

		public static String post(boolean isSSL, String url) {
			Objects.requireNonNull(url.trim(), URL_IS_NULL);

            return post(isSSL, url, null, null, null, DEFAULT_TIMEOUT_MILLIS);
        }
	}

	public static class RawRequest {
		private RawRequest() {
			super();
		}

		/**
		 * <pre>
		 * StatusCode, Body, Headers
		 *  - 키 : HttpConnectionUtil.STATUS_KEY, HttpConnectionUtil.BODY_KEY, HttpConnectionUtil.HEADERS_KEY
		 * </pre>
		 * @param isJson
		 * @param isSSL
		 * @param sUrl
		 * @param header
		 * @param payload
		 * @param timeoutMillis
		 * @return
		 */
		private static Map<String, Object> rawMap(boolean isJson, boolean isSSL, String sUrl, Map<String, Object> header, String payload, int timeoutMillis) {
			Objects.requireNonNull(sUrl.trim(), URL_IS_NULL);
			Objects.requireNonNull(payload.trim(), PAYLOAD_IS_NULL);

			Map<String, Object> resMap = new HashMap<>();
            URL url = null;

            try {
                URI uri = new URI(sUrl);
                url = uri.toURL();
            } catch (URISyntaxException | MalformedURLException e) {
                logger.error(LOG_INVALID_URL, sUrl, e);
                return resMap;
            }

            HttpURLConnection httpConn = null;
			try {
				httpConn = (HttpURLConnection) getURLConnection(url, timeoutMillis);

				httpConn.setRequestMethod(POST_METHOD);

                if (isSSL) {
					@SuppressWarnings("unused")
					HttpsURLConnection httpsConn = (HttpsURLConnection) httpConn;
				}

				if (isJson) {
                    httpConn.setRequestProperty(CONTENT_TYPE, "application/json");
                } else {
                    httpConn.setRequestProperty(CONTENT_TYPE, "application/xml");
                }

				// Default : Get - true, Post - false
				httpConn.setDoOutput(true); // POST 요청을 위해 출력 스트림 활성화

				if (header != null) {
                    for (Map.Entry<String, Object> entry : header.entrySet()) {
                        httpConn.setRequestProperty(entry.getKey(), String.valueOf(entry.getValue()));
                    }
                }

				try ( OutputStream os = httpConn.getOutputStream() ) {
                    os.write(payload.getBytes(DEFAULT_CHARSET)); // 페이로드 바이트로 변환하여 쓰기
                }

				int nStatus = httpConn.getResponseCode();
				logger.info("{} 에 대한 Raw POST 상태: {}", sUrl, nStatus);

				String sResponse = rawResponseBody(sUrl, httpConn);

                // 응답 헤더 가져오기
                Map<String, List<String>> resHeader = httpConn.getHeaderFields();

                // 결과 맵에 상태 코드, 본문, 헤더 추가
                resMap.put(STATUS_KEY, nStatus);
                resMap.put(BODY_KEY, sResponse);
                resMap.put(HEADERS_KEY, resHeader);
			} catch (IOException e) {
				logger.error("{} 에 대한 RAW POST 요청 중 IO 오류 발생: {}", sUrl, e.getMessage(), e);
			} finally {
				if (httpConn != null) {
                    httpConn.disconnect();
                }
			}

			return resMap;
		}

		private static String rawResponseBody(String sUrl, HttpURLConnection httpConn) {
			String sResponse = "";

			try (
					InputStream is = httpConn.getInputStream();
					InputStreamReader isr = new InputStreamReader(is, DEFAULT_CHARSET);
					BufferedReader br = new BufferedReader(isr)
			) {

                StringBuilder responseBuilder = new StringBuilder();
                String line;
                // 응답 본문 읽기
                while ((line = br.readLine()) != null) {
                    responseBuilder.append(line);
                }
                sResponse = responseBuilder.toString();
            } catch (IOException e) {
                logger.error(LOG_IO_EXCEPTION1, sUrl, e);
                // 오류 스트림이 있다면 읽어오기 시도
                try (InputStream errorStream = httpConn.getErrorStream();
                     InputStreamReader errorIsr = new InputStreamReader(errorStream, DEFAULT_CHARSET);
                     BufferedReader errorBr = new BufferedReader(errorIsr)) {
                    StringBuilder errorResponseBuilder = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorBr.readLine()) != null) {
                        errorResponseBuilder.append(errorLine);
                    }
                    sResponse = errorResponseBuilder.toString();
                    logger.error(LOG_IO_EXCEPTION2, sUrl, sResponse);
                } catch (IOException innerE) {
                    logger.error(LOG_IO_EXCEPTION3, sUrl, innerE);
                }
            }
			return sResponse;
		}

		private static String raw(boolean isJson, boolean isSSL, String url, Map<String, Object> header, String payload, int timeoutMillis) {
            Map<String, Object> resMap = rawMap(isJson, isSSL, url, header, payload, timeoutMillis);
            return (resMap.isEmpty() || resMap.get(BODY_KEY) == null) ? "" : resMap.get(BODY_KEY).toString();
        }

		public static String json(boolean isSSL, String url, Map<String, Object> header, String payload, int timeoutMillis) {
			Objects.requireNonNull(url.trim(), URL_IS_NULL);

			if (header == null || header.isEmpty()) {
				throw new IllegalArgumentException("Header map cannot be null or empty when making a JSON request.");
			}

			Objects.requireNonNull(payload.trim(), PAYLOAD_IS_NULL);
			Objects.requireNonNull(timeoutMillis, TIMEOUT_IS_NULL);

            return raw(true, isSSL, url, header, payload, timeoutMillis);
        }

		public static String json(boolean isSSL, String url, Map<String, Object> header, String payload) {
			Objects.requireNonNull(url.trim(), URL_IS_NULL);

			if (header == null || header.isEmpty()) {
				throw new IllegalArgumentException("Header map cannot be null or empty when making a JSON request.");
			}

			Objects.requireNonNull(payload.trim(), PAYLOAD_IS_NULL);

            return json(isSSL, url, header, payload, DEFAULT_TIMEOUT_MILLIS);
        }

		public static String xml(boolean isSSL, String url, Map<String, Object> header, String payload, int timeoutMillis) {
			Objects.requireNonNull(url.trim(), URL_IS_NULL);

			if (header == null || header.isEmpty()) {
				throw new IllegalArgumentException("Header map cannot be null or empty when making a XML request.");
			}

			Objects.requireNonNull(payload.trim(), PAYLOAD_IS_NULL);
			Objects.requireNonNull(timeoutMillis, TIMEOUT_IS_NULL);

            return raw(false, isSSL, url, header, payload, timeoutMillis);
        }

		public static String xml(boolean isSSL, String url, Map<String, Object> header, String payload) {
			Objects.requireNonNull(url.trim(), URL_IS_NULL);

			if (header == null || header.isEmpty()) {
				throw new IllegalArgumentException("Header map cannot be null or empty when making a XML request.");
			}

			Objects.requireNonNull(payload.trim(), PAYLOAD_IS_NULL);

            return xml(isSSL, url, header, payload, DEFAULT_TIMEOUT_MILLIS);
        }
	}

	public static class MultipartRequest {
		private MultipartRequest() {
			super();
		}

		// XXX - https://blog.morizyun.com/blog/android-httpurlconnection-post-multipart/
		// Multipart 요청 구현은 경계(boundary) 처리 및 파일 스트리밍 때문에 복잡합니다.
        // 복잡한 Multipart 요청이 자주 필요하다면 Apache HttpClient 또는 OkHttp와 같은
        // 전용 HTTP 클라이언트 라이브러리를 사용하는 것을 권장합니다.
        // 수동 구현 시 다음을 포함합니다:
        // 1. 고유한 경계 문자열 생성.
        // 2. "Content-Type" 헤더를 "multipart/form-data; boundary=<경계>"로 설정.
        // 3. 각 파트(폼 필드, 파일)를 OutputStream에 "--<경계>"로 시작하고 "\r\n"으로 끝내서 작성.
        // 4. 요청을 "--<경계>--\r\n"으로 종료.
	}

}
