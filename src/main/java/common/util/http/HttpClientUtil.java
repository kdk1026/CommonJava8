package common.util.http;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8.  6. 김대광	Javadoc 작성
 * 2021. 8. 13. 김대광	SonarLint 지시에 따른 주저리 주저리
 * 2025. 5. 30. 김대광	제미나이에 의한 코드 개선
 * </pre>
 *
 * <pre>
 * HttpClient version
 * -----------------------------------
 * 4.4 Standard
 * 4.5.13 까지는 이상 없음 확인
 * -----------------------------------
 *
 * MultipartEntityBuilder, FileBody, HttpMultipartMode
 * -----------------------------------
 * HttpClient 대신 httpmime 이 있어야 함
 *   - httpmime 디펜더시 하면 HttpClient 도 가지고 옴 (메모를 안했더니만 어느 순간 deprecated 되어서 어느 순간 삭제된줄 알았음...)
 * -----------------------------------
 * </pre>
 *
 * @author 김대광
 */
public class HttpClientUtil {

	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

	private HttpClientUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNull(String paramName) {
	        return String.format("'%s' is null", paramName);
	    }

	}

	/**
	 * UTF-8
	 * @since 1.7
	 */
	private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

	public static final String STATUS_KEY = "status";
	public static final String BODY_KEY = "body";
	public static final String HEADERS_KEY = "headers";

	private static final int DEFAULT_TIMEOUT_MS = 5000;

	private static final String URL_NOT_BE_NULL = ExceptionMessage.isNull("url");
	private static final String TIMEOUT_NOT_BE_NULL = ExceptionMessage.isNull("timeout");

	private static final String HEADER_NOT_BE_NULL = ExceptionMessage.isNull("header");
	private static final String PARAM_NOT_BE_NULL = ExceptionMessage.isNull("param");
	private static final String CHARSET_NOT_BE_NULL = ExceptionMessage.isNull("charset");
	private static final String PAYLOAD_NOT_BE_NULL = ExceptionMessage.isNull("payload");
	private static final String FILE_PARAM_KEY_NOT_BE_NULL = ExceptionMessage.isNull("fileParamKey");

	/**
     * CloseableHttpClient 인스턴스를 생성하여 반환
     *
     * @param isSSL SSL 지원 HttpClient가 필요한 경우 true
     * @return CloseableHttpClient SSL 컨텍스트 생성 실패 시 null
     */
    private static CloseableHttpClient getHttpClient(boolean isSSL) {
        if (isSSL) {
            try {
                SSLContextBuilder builder = new SSLContextBuilder();
                // 자체 서명된 인증서 신뢰. 프로덕션 환경에서는 특정 TrustStore 사용을 고려해야 합니다.
                builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                SSLContext sslContext = builder.build();
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
                return HttpClients.custom().setSSLSocketFactory(sslsf).build();
            } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
                logger.error("SSL HttpClient 생성 실패", e);
                return null;
            }
        } else {
            return HttpClients.createDefault();
        }
    }

    /**
     * 지정된 타임아웃으로 RequestConfig를 생성
     *
     * @param timeoutInMilliseconds
     * @return
     */
    private static RequestConfig getConfigWithTimeout(int timeoutInMilliseconds) {
        return RequestConfig.custom()
                .setSocketTimeout(timeoutInMilliseconds)
                .setConnectTimeout(timeoutInMilliseconds)
                .setConnectionRequestTimeout(timeoutInMilliseconds)
                .build();
    }

    /**
     * HttpResponse에서 응답 데이터를 Map으로 추출
     *
     * @param response HttpResponse 객체
     * @param charset 엔티티 변환에 사용할 문자셋
     * @return 상태, 본문, 헤더를 포함하는 Map
     * @throws IOException 엔티티 변환 중 I/O 오류 발생 시
     */
    private static Map<String, Object> extractResponseData(HttpResponse response, Charset charset) throws IOException {
        Map<String, Object> resMap = new HashMap<>();
        int nStatus = response.getStatusLine().getStatusCode();
        String sResponse = "";

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            sResponse = EntityUtils.toString(entity, charset != null ? charset : DEFAULT_CHARSET);
            // 연결 리소스를 해제하기 위해 엔티티를 완전히 소비합니다.
            EntityUtils.consume(entity);
        }

        Map<String, Object> resHeader = new HashMap<>();
        Header[] headers = response.getAllHeaders();
        for (Header h : headers) {
            resHeader.put(h.getName(), h.getValue());
        }

        resMap.put(STATUS_KEY, nStatus);
        resMap.put(BODY_KEY, sResponse);
        resMap.put(HEADERS_KEY, resHeader);

        return resMap;
    }

	public static class GetRequest {
		private GetRequest() {
			super();
		}

		/**
		 * <pre>
		 * StatusCode, Body, Headers
		 *  - 키 : HttpClientUtil.STATUS_KEY, HttpClientUtil.BODY_KEY, HttpClientUtil.HEADERS_KEY
		 * </pre>
		 * @param isSSL
		 * @param url
		 * @param header
		 * @param timeoutMs 요청 타임아웃 (밀리초)
		 * @return
		 */
		public static Map<String, Object> getMap(boolean isSSL, String url, Map<String, String> header, int timeoutMs) {
			Objects.requireNonNull(url, URL_NOT_BE_NULL);
			if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

			Objects.requireNonNull(timeoutMs, TIMEOUT_NOT_BE_NULL);

			Map<String, Object> resMap = new HashMap<>();

			try ( CloseableHttpClient httpClient = getHttpClient(isSSL) ) {
				if (httpClient == null) {
					return resMap;
				}

				HttpGet httpGet = new HttpGet(url);
                httpGet.setConfig(getConfigWithTimeout(timeoutMs));

                if (header != null) {
                	Iterator<String> it = header.keySet().iterator();
			        String key = "";

			        while(it.hasNext()) {
			        	 key = it.next();
			        	 httpGet.setHeader(key, String.valueOf(header.get(key)));
			        }
                }

                HttpResponse response = httpClient.execute(httpGet);
                logger.info("GET Status for {}: {}", url, response.getStatusLine().getStatusCode());
                resMap = extractResponseData(response, StandardCharsets.UTF_8);
			} catch (IOException e) {
				logger.error("Error during GET request to {}: {}", url, e.getMessage(), e);
			}
			return resMap;
		}

		public static Map<String, Object> getMap(boolean isSSL, String url, Map<String, String> header) {
			Objects.requireNonNull(url, URL_NOT_BE_NULL);
			if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

        	if ( header == null || header.isEmpty() ) {
        		throw new IllegalArgumentException(HEADER_NOT_BE_NULL);
        	}

            return getMap(isSSL, url, header, DEFAULT_TIMEOUT_MS);
        }

        public static String get(boolean isSSL, String url, Map<String, String> header, int timeoutMs) {
        	Objects.requireNonNull(url, URL_NOT_BE_NULL);
        	if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

        	if ( header == null || header.isEmpty() ) {
        		throw new IllegalArgumentException(HEADER_NOT_BE_NULL);
        	}

        	Objects.requireNonNull(timeoutMs, TIMEOUT_NOT_BE_NULL);

            Map<String, Object> resMap = getMap(isSSL, url, header, timeoutMs);
            return (resMap.isEmpty() || !resMap.containsKey(BODY_KEY)) ? "" : resMap.get(BODY_KEY).toString();
        }

        public static String get(boolean isSSL, String url, Map<String, String> header) {
        	Objects.requireNonNull(url, URL_NOT_BE_NULL);
        	if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

        	if ( header == null || header.isEmpty() ) {
        		throw new IllegalArgumentException(HEADER_NOT_BE_NULL);
        	}

            return get(isSSL, url, header, DEFAULT_TIMEOUT_MS);
        }

        public static String get(boolean isSSL, String url) {
        	Objects.requireNonNull(url, URL_NOT_BE_NULL);

            return get(isSSL, url, null, DEFAULT_TIMEOUT_MS);
        }
	}

	public static class PostRequest {
		private PostRequest() {
			super();
		}

		private static List<NameValuePair> convertParam(Map<String, Object> param) {
			List<NameValuePair> listParam = new ArrayList<>();
			if (param != null) {
				Iterator<String> it = param.keySet().iterator();
		        String key = "";

		        while(it.hasNext()) {
		            key = it.next();

		            if ( param.get(key).getClass().isArray() ) {
		            	String[] arr = (String[]) param.get(key);

		            	for (String s : arr) {
							listParam.add(new BasicNameValuePair(key, s));
						}
		            } else {
		            	listParam.add(new BasicNameValuePair(key, String.valueOf(param.get(key))));
		            }
		        }
			}
	        return listParam;
	    }

		/**
		 * <pre>
		 * StatusCode, Body, Headers
		 *  - 키 : HttpClientUtil.STATUS_KEY, HttpClientUtil.BODY_KEY, HttpClientUtil.HEADERS_KEY
		 * </pre>
		 * @param isSSL
		 * @param url
		 * @param header
		 * @param param
		 * @param charset
		 * @param timeoutMs 요청 타임아웃 (밀리초)
		 * @return
		 */
		public static Map<String, Object> postMap(boolean isSSL, String url, Map<String, String> header, Map<String, Object> param, Charset charset, int timeoutMs) {
			Objects.requireNonNull(url, URL_NOT_BE_NULL);
			if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

			Objects.requireNonNull(timeoutMs, TIMEOUT_NOT_BE_NULL);

			Map<String, Object> resMap = new HashMap<>();

			try ( CloseableHttpClient httpClient = getHttpClient(isSSL) ) {
				if (httpClient == null) {
                    return resMap;
                }

				HttpPost httpPost = new HttpPost(url);
                httpPost.setConfig(getConfigWithTimeout(timeoutMs));

                if (header != null) {
                	Iterator<String> it = header.keySet().iterator();
			        String key = "";

			        while(it.hasNext()) {
			        	 key = it.next();
			        	 httpPost.setHeader(key, String.valueOf(header.get(key)));
			        }
                }

                List<NameValuePair> listParam = convertParam(param);
                if ( !listParam.isEmpty() ) {
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(listParam, charset != null ? charset : DEFAULT_CHARSET);
                    httpPost.setEntity(entity);
                }

                HttpResponse response = httpClient.execute(httpPost);
                logger.info("POST Status for {}: {}", url, response.getStatusLine().getStatusCode());
                resMap = extractResponseData(response, StandardCharsets.UTF_8);
			} catch (IOException e) {
                logger.error("Error during POST request to {}: {}", url, e.getMessage(), e);
            }

			return resMap;
		}

		public static Map<String, Object> postMap(boolean isSSL, String url, Map<String, String> header, Map<String, Object> param, Charset charset) {
			Objects.requireNonNull(url, URL_NOT_BE_NULL);
			if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

			if ( header != null && header.isEmpty() ) {
				throw new IllegalArgumentException(HEADER_NOT_BE_NULL);
			}

        	if ( param == null || param.isEmpty() ) {
				throw new IllegalArgumentException(PARAM_NOT_BE_NULL);
        	}

        	Objects.requireNonNull(charset, CHARSET_NOT_BE_NULL);

            return postMap(isSSL, url, header, param, charset, DEFAULT_TIMEOUT_MS);
        }

        public static String post(boolean isSSL, String url, Map<String, String> header, Map<String, Object> param, Charset charset, int timeoutMs) {
        	Objects.requireNonNull(url, URL_NOT_BE_NULL);
        	if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

			if ( header != null && header.isEmpty() ) {
				throw new IllegalArgumentException(HEADER_NOT_BE_NULL);
			}

        	if ( param == null || param.isEmpty() ) {
				throw new IllegalArgumentException(PARAM_NOT_BE_NULL);
        	}

        	Objects.requireNonNull(charset, CHARSET_NOT_BE_NULL);
        	Objects.requireNonNull(timeoutMs, TIMEOUT_NOT_BE_NULL);

            Map<String, Object> resMap = postMap(isSSL, url, header, param, charset, timeoutMs);
            return (resMap.isEmpty() || !resMap.containsKey(BODY_KEY)) ? "" : resMap.get(BODY_KEY).toString();
        }

        public static String post(boolean isSSL, String url, Map<String, String> header, Map<String, Object> param, Charset charset) {
        	Objects.requireNonNull(url, URL_NOT_BE_NULL);
        	if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

        	if ( header != null && header.isEmpty() ) {
        		throw new IllegalArgumentException(HEADER_NOT_BE_NULL);
        	}

        	if ( param == null || param.isEmpty() ) {
				throw new IllegalArgumentException(PARAM_NOT_BE_NULL);
        	}

        	Objects.requireNonNull(charset, CHARSET_NOT_BE_NULL);

            return post(isSSL, url, header, param, charset, DEFAULT_TIMEOUT_MS);
        }

        public static String post(boolean isSSL, String url, Map<String, Object> param, Charset charset) {
        	Objects.requireNonNull(url, URL_NOT_BE_NULL);
        	if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

        	if ( param == null || param.isEmpty() ) {
				throw new IllegalArgumentException(PARAM_NOT_BE_NULL);
        	}

        	Objects.requireNonNull(charset, CHARSET_NOT_BE_NULL);

            return post(isSSL, url, null, param, charset, DEFAULT_TIMEOUT_MS);
        }

        public static String post(boolean isSSL, String url, Map<String, Object> param) {
        	Objects.requireNonNull(url, URL_NOT_BE_NULL);
        	if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

        	if ( param == null || param.isEmpty() ) {
				throw new IllegalArgumentException(PARAM_NOT_BE_NULL);
        	}

            return post(isSSL, url, param, null);
        }

        public static String post(boolean isSSL, String url) {
        	Objects.requireNonNull(url, URL_NOT_BE_NULL);

            return post(isSSL, url, null);
        }
	}

	public static class RawRequest {
		private RawRequest() {
			super();
		}

		/**
		 * <pre>
		 * StatusCode, Body, Headers
		 *  - 키 : HttpClientUtil.STATUS_KEY, HttpClientUtil.BODY_KEY, HttpClientUtil.HEADERS_KEY
		 * </pre>
		 * @param isJson
		 * @param isSSL
		 * @param url
		 * @param header
		 * @param payload
		 * @param timeoutMs 요청 타임아웃 (밀리초)
		 * @return
		 */
		public static Map<String, Object> rawMap(boolean isJson, boolean isSSL, String url, Map<String, String> header, String payload, int timeoutMs) {
			Objects.requireNonNull(url, URL_NOT_BE_NULL);
			if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

			Objects.requireNonNull(payload, PAYLOAD_NOT_BE_NULL);
			if ( payload.trim().isEmpty() ) {
			    throw new IllegalArgumentException(PAYLOAD_NOT_BE_NULL);
			}

			Objects.requireNonNull(timeoutMs, TIMEOUT_NOT_BE_NULL);

			Map<String, Object> resMap = new HashMap<>();

			try ( CloseableHttpClient httpClient = getHttpClient(isSSL) ) {
				if (httpClient == null) {
                    return resMap;
                }

				String contentType = isJson ? "application/json" : "application/xml";
                HttpPost httpPost = new HttpPost(url);
                httpPost.setConfig(getConfigWithTimeout(timeoutMs));

                if (header != null) {
                	Iterator<String> it = header.keySet().iterator();
			        String key = "";

			        while(it.hasNext()) {
			        	 key = it.next();
			        	 httpPost.setHeader(key, String.valueOf(header.get(key)));
			        }
                }

                StringEntity entity = new StringEntity(payload, DEFAULT_CHARSET);
                entity.setContentType(contentType);
                httpPost.setEntity(entity);

                HttpResponse response = httpClient.execute(httpPost);
                logger.info("POST Raw Status for {}: {}", url, response.getStatusLine().getStatusCode());
                resMap = extractResponseData(response, DEFAULT_CHARSET);
			} catch (IOException e) {
                logger.error("Error during Raw POST request to {}: {}", url, e.getMessage(), e);
            }

			return resMap;
		}

		public static Map<String, Object> rawMap(boolean isJson, boolean isSSL, String url, Map<String, String> header, String payload) {
			Objects.requireNonNull(url, URL_NOT_BE_NULL);
			if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

			if ( header != null && header.isEmpty() ) {
				throw new IllegalArgumentException(HEADER_NOT_BE_NULL);
			}

        	Objects.requireNonNull(payload, PAYLOAD_NOT_BE_NULL);
        	if ( payload.trim().isEmpty() ) {
			    throw new IllegalArgumentException(PAYLOAD_NOT_BE_NULL);
			}

            return rawMap(isJson, isSSL, url, header, payload, DEFAULT_TIMEOUT_MS);
        }

        private static String raw(boolean isJson, boolean isSSL, String url, Map<String, String> header, String payload, int timeoutMs) {
            Map<String, Object> resMap = rawMap(isJson, isSSL, url, header, payload, timeoutMs);
            return (resMap.isEmpty() || !resMap.containsKey(BODY_KEY)) ? "" : resMap.get(BODY_KEY).toString();
        }

        public static String json(boolean isSSL, String url, Map<String, String> header, String payload, int timeoutMs) {
        	Objects.requireNonNull(url, URL_NOT_BE_NULL);
        	if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

			if ( header != null && header.isEmpty() ) {
				throw new IllegalArgumentException(HEADER_NOT_BE_NULL);
			}

        	Objects.requireNonNull(payload, PAYLOAD_NOT_BE_NULL);
        	if ( payload.trim().isEmpty() ) {
			    throw new IllegalArgumentException(PAYLOAD_NOT_BE_NULL);
			}

        	Objects.requireNonNull(timeoutMs, TIMEOUT_NOT_BE_NULL);

            return raw(true, isSSL, url, header, payload, timeoutMs);
        }

        public static String json(boolean isSSL, String url, Map<String, String> header, String payload) {
        	Objects.requireNonNull(url, URL_NOT_BE_NULL);
        	if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

			if ( header != null && header.isEmpty() ) {
				throw new IllegalArgumentException(HEADER_NOT_BE_NULL);
			}

        	Objects.requireNonNull(payload, PAYLOAD_NOT_BE_NULL);
        	if ( payload.trim().isEmpty() ) {
			    throw new IllegalArgumentException(PAYLOAD_NOT_BE_NULL);
			}

            return json(isSSL, url, header, payload, DEFAULT_TIMEOUT_MS);
        }

        public static String xml(boolean isSSL, String url, Map<String, String> header, String payload, int timeoutMs) {
        	Objects.requireNonNull(url, URL_NOT_BE_NULL);
        	if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

			if ( header != null && header.isEmpty() ) {
				throw new IllegalArgumentException(HEADER_NOT_BE_NULL);
			}

        	Objects.requireNonNull(payload, PAYLOAD_NOT_BE_NULL);
        	if ( payload.trim().isEmpty() ) {
			    throw new IllegalArgumentException(PAYLOAD_NOT_BE_NULL);
			}

        	Objects.requireNonNull(timeoutMs, TIMEOUT_NOT_BE_NULL);

            return raw(false, isSSL, url, header, payload, timeoutMs);
        }

        public static String xml(boolean isSSL, String url, Map<String, String> header, String payload) {
        	Objects.requireNonNull(url, URL_NOT_BE_NULL);
        	if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

			if ( header != null && header.isEmpty() ) {
				throw new IllegalArgumentException(HEADER_NOT_BE_NULL);
			}

        	Objects.requireNonNull(payload, PAYLOAD_NOT_BE_NULL);
        	if ( payload.trim().isEmpty() ) {
			    throw new IllegalArgumentException(PAYLOAD_NOT_BE_NULL);
			}

            return xml(isSSL, url, header, payload, DEFAULT_TIMEOUT_MS);
        }
	}

	public static class MultipartRequest {
		private MultipartRequest() {
			super();
		}

		/**
		 * <pre>
		 * StatusCode, Body, Headers
		 *  - 키 : HttpClientUtil.STATUS_KEY, HttpClientUtil.BODY_KEY, HttpClientUtil.HEADERS_KEY
		 * </pre>
		 * @param isSSL
		 * @param url
		 * @param header
		 * @param param
		 * @param fileParamKey
		 * @param file
		 * @return
		 */
		public static Map<String, Object> multipartMap(boolean isSSL, String url, Map<String, String> header, Map<String, Object> param
				, String fileParamKey, File file, int timeoutMs) {

			Objects.requireNonNull(url, URL_NOT_BE_NULL);
			if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

			Objects.requireNonNull(fileParamKey, FILE_PARAM_KEY_NOT_BE_NULL);
			if ( fileParamKey.trim().isEmpty() ) {
			    throw new IllegalArgumentException(FILE_PARAM_KEY_NOT_BE_NULL);
			}

			Objects.requireNonNull(timeoutMs, TIMEOUT_NOT_BE_NULL);

			if (file == null) {
                throw new IllegalArgumentException("File cannot be null.");
            }

            if ( !file.exists() || !file.isFile() ) {
                throw new IllegalArgumentException("File does not exist or is not a regular file: " + file.getAbsolutePath());
            }

			Map<String, Object> resMap = new HashMap<>();

			try ( CloseableHttpClient httpClient = getHttpClient(isSSL) ) {
				if (httpClient == null) {
                    return resMap;
                }

				HttpPost httpPost = new HttpPost(url);
	            httpPost.setConfig(getConfigWithTimeout(timeoutMs));

	            if (header != null) {
	            	Iterator<String> it = header.keySet().iterator();
			        String key = "";

			        while(it.hasNext()) {
			        	 key = it.next();
			        	 httpPost.setHeader(key, String.valueOf(header.get(key)));
			        }
	            }

	            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

	            FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);
	            builder.addPart(fileParamKey, fileBody);

	            if (param != null) {
	            	Iterator<String> it = param.keySet().iterator();
			        String key = "";

			        while(it.hasNext()) {
			        	 key = it.next();
			        	 builder.addPart(key, new StringBody(String.valueOf(param.get(key)), ContentType.MULTIPART_FORM_DATA));
			        }
	            }

	            HttpEntity entity = builder.build();
	            httpPost.setEntity(entity);

	            HttpResponse response = httpClient.execute(httpPost);
	            logger.info("POST Multipart Status for {}: {}", url, response.getStatusLine().getStatusCode());
	            resMap = extractResponseData(response, DEFAULT_CHARSET);
			} catch (IOException e) {
				logger.error("Error during Multipart POST request to {}: {}", url, e.getMessage(), e);
            }

			return resMap;
		}

		public static Map<String, Object> multipartMap(boolean isSSL, String url, Map<String, String> header, Map<String, Object> param,
                String fileParamKey, File file) {

        	Objects.requireNonNull(url, URL_NOT_BE_NULL);
        	if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

			if ( header != null && header.isEmpty() ) {
				throw new IllegalArgumentException(HEADER_NOT_BE_NULL);
			}

			if ( param != null && param.isEmpty() ) {
				throw new IllegalArgumentException(PARAM_NOT_BE_NULL);
			}

			Objects.requireNonNull(fileParamKey, FILE_PARAM_KEY_NOT_BE_NULL);
			if ( fileParamKey.trim().isEmpty() ) {
			    throw new IllegalArgumentException(FILE_PARAM_KEY_NOT_BE_NULL);
			}

			Objects.requireNonNull(file, PAYLOAD_NOT_BE_NULL);

			return multipartMap(isSSL, url, header, param, fileParamKey, file, DEFAULT_TIMEOUT_MS);
		}

		public static String multipart(boolean isSSL, String url, Map<String, String> header, Map<String, Object> param,
				String fileParamKey, File file, int timeoutMs) {

			Objects.requireNonNull(url, URL_NOT_BE_NULL);
			if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

			if ( header != null && header.isEmpty() ) {
				throw new IllegalArgumentException(HEADER_NOT_BE_NULL);
			}

			if ( param != null && param.isEmpty() ) {
				throw new IllegalArgumentException(PARAM_NOT_BE_NULL);
			}

			Objects.requireNonNull(fileParamKey, FILE_PARAM_KEY_NOT_BE_NULL);
			if ( fileParamKey.trim().isEmpty() ) {
			    throw new IllegalArgumentException(FILE_PARAM_KEY_NOT_BE_NULL);
			}

			Objects.requireNonNull(file, PAYLOAD_NOT_BE_NULL);
			Objects.requireNonNull(timeoutMs, TIMEOUT_NOT_BE_NULL);

			Map<String, Object> resMap = multipartMap(isSSL, url, header, param, fileParamKey, file, timeoutMs);
			return (resMap.isEmpty() || !resMap.containsKey(BODY_KEY)) ? "" : resMap.get(BODY_KEY).toString();
		}

		public static String multipart(boolean isSSL, String url, Map<String, String> header, Map<String, Object> param,
				String fileParamKey, File file) {

			Objects.requireNonNull(url, URL_NOT_BE_NULL);
			if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

			if ( header != null && header.isEmpty() ) {
				throw new IllegalArgumentException(HEADER_NOT_BE_NULL);
			}

			if ( param != null && param.isEmpty() ) {
				throw new IllegalArgumentException(PARAM_NOT_BE_NULL);
			}

			Objects.requireNonNull(fileParamKey, FILE_PARAM_KEY_NOT_BE_NULL);
			if ( fileParamKey.trim().isEmpty() ) {
			    throw new IllegalArgumentException(FILE_PARAM_KEY_NOT_BE_NULL);
			}

			Objects.requireNonNull(file, PAYLOAD_NOT_BE_NULL);

			return multipart(isSSL, url, header, param, fileParamKey, file, DEFAULT_TIMEOUT_MS);
		}

		public static String multipart(boolean isSSL, String url, Map<String, Object> param, String fileParamKey, File file) {
			Objects.requireNonNull(url, URL_NOT_BE_NULL);
			if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

			if ( param != null && param.isEmpty() ) {
				throw new IllegalArgumentException(PARAM_NOT_BE_NULL);
			}

			Objects.requireNonNull(fileParamKey, FILE_PARAM_KEY_NOT_BE_NULL);
			if ( fileParamKey.trim().isEmpty() ) {
			    throw new IllegalArgumentException(FILE_PARAM_KEY_NOT_BE_NULL);
			}

			Objects.requireNonNull(file, PAYLOAD_NOT_BE_NULL);

			return multipart(isSSL, url, null, param, fileParamKey, file, DEFAULT_TIMEOUT_MS);
		}

		public static String multipart(boolean isSSL, String url, String fileParamKey, File file) {
			Objects.requireNonNull(url, URL_NOT_BE_NULL);
			if ( url.trim().isEmpty() ) {
			    throw new IllegalArgumentException(URL_NOT_BE_NULL);
			}

			Objects.requireNonNull(fileParamKey, FILE_PARAM_KEY_NOT_BE_NULL);
			if ( fileParamKey.trim().isEmpty() ) {
			    throw new IllegalArgumentException(FILE_PARAM_KEY_NOT_BE_NULL);
			}

			Objects.requireNonNull(file, PAYLOAD_NOT_BE_NULL);

			return multipart(isSSL, url, null, null, fileParamKey, file, DEFAULT_TIMEOUT_MS);
		}
	}

}
