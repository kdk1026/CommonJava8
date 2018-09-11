package common.util.http;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
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
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpClient version 4.4 Standard
 */
public class HttpClientUtil {

	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
	
	private static HttpClient httpClient;
	private static HttpResponse response;
	
	private HttpClientUtil() {
		super();
	}
	
	/**
	 * @since 1.7
	 */
	private static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.toString();
	
	public static final String STATUS_KEY = "status";
	public static final String BODY_KEY = "body";
	public static final String HEADERS_KEY = "headers";
	
	private static final int TIMEOUT = 5;
	
	/**
	 * <pre>
	 * SSL의 경우, 제한된 네트워크 환경에서는 불가
	 *  - Java KeyStore에 SSL 인증서 등록 후 사용
	 *  - Apache HttpClient에서 인증서 import 방법은 따로 알아봐야함
	 * </pre>
	 * @param isSSL
	 * @return
	 */
	private static HttpClient getHttpClient(boolean isSSL) {
		httpClient = null;
		
		if (isSSL) {
			SSLContextBuilder builder = new SSLContextBuilder();
			try {
				builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
				httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
				
			} catch (Exception e) {
				logger.error("", e);
			}
			
		} else {
			httpClient = HttpClients.createDefault();
		}
		
		return httpClient;
	}
	
	private static RequestConfig getConfigWithTimeout(int timeoutInMilliseconds) {
		return RequestConfig.custom()
                .setSocketTimeout(timeoutInMilliseconds)
                .setConnectTimeout(timeoutInMilliseconds)
                .setConnectionRequestTimeout(timeoutInMilliseconds)
                .build();
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
		 * @return
		 */
		public static Map<String, Object> getMap(boolean isSSL, String url, Map<String, Object> header) {
			Map<String, Object> resMap = new HashMap<>();
			String sResponse = "";
			
			httpClient = getHttpClient(isSSL);
			if (httpClient == null) {
				return resMap;
				
			} else {
				HttpGet httpGet = new HttpGet(url);
				httpGet.setConfig(getConfigWithTimeout(TIMEOUT));
				
				if (header != null) {
			        Iterator<String> it = header.keySet().iterator();
			        String key = "";
			        
			        while(it.hasNext()) {
			        	 key = it.next();
			        	 httpGet.setHeader(key, String.valueOf(header.get(key)));
			        }
				}
				
				try {
					response = httpClient.execute(httpGet);
					int nStatus = response.getStatusLine().getStatusCode();
					
					logger.info("Get Status : {}", nStatus);
					sResponse = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
					
					Map<String, Object> resHeader = new HashMap<>();
					Header[] headers = response.getAllHeaders();
					for (Header h : headers) {
						resHeader.put(h.getName(), h.getValue());
					}
					
					resMap.put(STATUS_KEY, nStatus);
					resMap.put(BODY_KEY, sResponse);
					resMap.put(HEADERS_KEY, resHeader);
					
				} catch (IOException e) {
					logger.error("", e);
				}
			}
			
			return resMap;
		}
		
		public static String get(boolean isSSL, String url, Map<String, Object> header) {
			Map<String, Object> resMap = getMap(isSSL, url, header);
			return (resMap.isEmpty()) ? "" : resMap.get(BODY_KEY).toString();
		}
		
		public static String get(boolean isSSL, String url) {
			return get(isSSL, url, null);
		}
	}
	
	public static class PostRequest {
		private static List<NameValuePair> listParam;
		
		private PostRequest() {
			super();
		}
		
		private static List<NameValuePair> convertParam(Map<String, Object> param) {
			listParam = new ArrayList<>();
	        Iterator<String> it = param.keySet().iterator();
	        String key = "";
	        
	        while(it.hasNext()) {
	            key = it.next();
	            listParam.add(new BasicNameValuePair(key, String.valueOf(param.get(key))));
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
		 * @return
		 */
		public static Map<String, Object> postMap(boolean isSSL, String url, Map<String, Object> header, Map<String, Object> param, Charset charset) {
			Map<String, Object> resMap = new HashMap<>();
			String sResponse = "";
			
			httpClient = getHttpClient(isSSL);
			if (httpClient == null) {
				return resMap;
				
			} else {
				HttpPost httpPost = new HttpPost(url);
				httpPost.setConfig(getConfigWithTimeout(TIMEOUT));
				
				if (header != null) {
			        Iterator<String> it = header.keySet().iterator();
			        String key = "";
			        
			        while(it.hasNext()) {
			        	 key = it.next();
			        	 httpPost.setHeader(key, String.valueOf(header.get(key)));
			        }
				}
				
				if (param != null) {
					listParam = convertParam(param);
				}
				
				try {
					if (listParam != null) {
						UrlEncodedFormEntity entity = null;
						
						if (charset != null) {
							entity = new UrlEncodedFormEntity(listParam, charset);
							httpPost.setEntity(entity);
						} else {
							entity = new UrlEncodedFormEntity(listParam, DEFAULT_CHARSET);
							httpPost.setEntity(entity);
						}
					}
					
					response = httpClient.execute(httpPost);
					int nStatus = response.getStatusLine().getStatusCode();
					
					logger.info("Post Status : {}", nStatus);
					sResponse = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
					
					Map<String, Object> resHeader = new HashMap<>();
					Header[] headers = response.getAllHeaders();
					for (Header h : headers) {
						resHeader.put(h.getName(), h.getValue());
					}
					
					resMap.put(STATUS_KEY, nStatus);
					resMap.put(BODY_KEY, sResponse);
					resMap.put(HEADERS_KEY, resHeader);
					
				} catch (IOException e) {
					logger.error("", e);
				}
			}
			
			return resMap;
		}
		
		public static String post(boolean isSSL, String url, Map<String, Object> header, Map<String, Object> param, Charset charset) {
			Map<String, Object> resMap = postMap(isSSL, url, header, param, charset);
			return (resMap.isEmpty()) ? "" : resMap.get(BODY_KEY).toString();
		}
		
		public static String post(boolean isSSL, String url, Map<String, Object> param, Charset charset) {
			return post(isSSL, url, null, param, charset);
		}
		
		public static String post(boolean isSSL, String url, Map<String, Object> param) {
			return post(isSSL, url, param, null);
		}
		
		public static String post(boolean isSSL, String url) {
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
		 * @return
		 */
		public static Map<String, Object> rawMap(boolean isJson, boolean isSSL, String url, Map<String, Object> header, String payload) {
			Map<String, Object> resMap = new HashMap<>();
			String sResponse = "";
			
			httpClient = getHttpClient(isSSL);
			if (httpClient == null) {
				return resMap;
				
			} else {
				String contentType = (isJson) ? "application/json" : "application/xml";
				HttpPost httpPost = new HttpPost(url);
				httpPost.setConfig(getConfigWithTimeout(TIMEOUT));
				
				if (header != null) {
			        Iterator<String> it = header.keySet().iterator();
			        String key = "";
			        
			        while(it.hasNext()) {
			        	 key = it.next();
			        	 httpPost.setHeader(key, String.valueOf(header.get(key)));
			        }
				}
				
				try {
					StringEntity entity = new StringEntity(payload);
					entity.setContentType(contentType);
					
					httpPost.setEntity(entity);
					
					response = httpClient.execute(httpPost);
					int nStatus = response.getStatusLine().getStatusCode();
					
					logger.info("Post Raw Status : {}", response.getStatusLine().getStatusCode());
					sResponse = EntityUtils.toString(response.getEntity());
					
					Map<String, Object> resHeader = new HashMap<>();
					Header[] headers = response.getAllHeaders();
					for (Header h : headers) {
						resHeader.put(h.getName(), h.getValue());
					}
					
					resMap.put(STATUS_KEY, nStatus);
					resMap.put(BODY_KEY, sResponse);
					resMap.put(HEADERS_KEY, resHeader);
					
				} catch (IOException e) {
					logger.error("", e);
				}
			}
			
			return resMap;
		}
		
		private static String raw(boolean isJson, boolean isSSL, String url, Map<String, Object> header, String payload) {
			Map<String, Object> resMap = rawMap(isJson, isSSL, url, header, payload);
			return (resMap.isEmpty()) ? "" : resMap.get(BODY_KEY).toString();
		}
		
		public static String json(boolean isSSL, String url, Map<String, Object> header, String payload) {
			return raw(true, isSSL, url, header, payload);
		}
		
		public static String xml(boolean isSSL, String url, Map<String, Object> header, String payload) {
			return raw(false, isSSL, url, header, payload);
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
		public static Map<String, Object> multipartMap(boolean isSSL, String url, Map<String, Object> header, Map<String, Object> param
				, String fileParamKey, File file) {
			Map<String, Object> resMap = new HashMap<>();
			String sResponse = "";
			
			httpClient = getHttpClient(isSSL);
			if (httpClient == null) {
				return resMap;
				
			} else {
				HttpPost httpPost = new HttpPost(url);
				httpPost.setConfig(getConfigWithTimeout(TIMEOUT));
				
				if (header != null) {
			        Iterator<String> it = header.keySet().iterator();
			        String key = "";
			        
			        while(it.hasNext()) {
			        	 key = it.next();
			        	 httpPost.setHeader(key, String.valueOf(header.get(key)));
			        }
				}
				
				try {
					MultipartEntityBuilder builder = MultipartEntityBuilder.create();
					FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);
					
					builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
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
					
					response = httpClient.execute(httpPost);
					int nStatus = response.getStatusLine().getStatusCode();
					
					logger.info("Post Multipart Status : {}", nStatus);
					sResponse = EntityUtils.toString(response.getEntity());
					
					Map<String, Object> resHeader = new HashMap<>();
					Header[] headers = response.getAllHeaders();
					for (Header h : headers) {
						resHeader.put(h.getName(), h.getValue());
					}
					
					resMap.put(STATUS_KEY, nStatus);
					resMap.put(BODY_KEY, sResponse);
					resMap.put(HEADERS_KEY, resHeader);
					
				} catch (IOException e) {
					logger.error("", e);
				}
			}
			
			return resMap;
		}
		
		public static String multipart(boolean isSSL, String url, Map<String, Object> header, Map<String, Object> param
				, String fileParamKey, File file) {
			Map<String, Object> resMap = multipartMap(isSSL, url, header, param, fileParamKey, file);
			return (resMap.isEmpty()) ? "" : resMap.get(BODY_KEY).toString();
		}
		
		public static String multipart(boolean isSSL, String url, Map<String, Object> param, String fileParamKey, File file) {
			return multipart(isSSL, url, null, param, fileParamKey, file);
		}
		
		public static String multipart(boolean isSSL, String url, String fileParamKey, File file) {
			return multipart(isSSL, url, null, fileParamKey, file);
		}
	}
	
}
