package common.util.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 성능 및 SSL 성공률이 다소 뒤처지므로 부득이한 경우가 아니면 Apache HttpClient 사용 권장
 */
public class HttpConnectionUtil {

	private static final Logger logger = LoggerFactory.getLogger(HttpConnectionUtil.class);
	
	private HttpConnectionUtil() {
		super();
	}
	
	/**
	 * @since 1.7
	 */
	private static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.toString();
	
	public static final String STATUS_KEY = "status";
	public static final String BODY_KEY = "body";
	public static final String HEADERS_KEY = "headers";
	
	private static final String GET_METHOD = "GET";
	private static final String POST_METHOD = "POST";
	private static final String CONTENT_TYPE = "Content-Type";
	
	private static final int BUFFER_SIZE = 8192;
	
	private static URLConnection getURLConnection(URL url) {
		URLConnection httpConn = null;

		try {
			httpConn = url.openConnection();
			
		} catch (IOException e) {
			logger.error("", e);
		}
		
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
		 * @return
		 */
		public static Map<String, Object> getMap(boolean isSSL, String sUrl, Map<String, Object> header) {
			Map<String, Object> resMap = new HashMap<>();
			String sResponse = "";
			
			URL url = null;
			try {
				url = new URL(sUrl);
				
			} catch (MalformedURLException e) {
				logger.error("", e);
			}
			
			if (url == null) {
				return resMap;
			}
			
			URLConnection httpConn = getURLConnection(url);
			try {
				if (httpConn == null) {
					return resMap;
				}
				
				if (isSSL) {
					((HttpsURLConnection) httpConn).setRequestMethod(GET_METHOD);					
				} else {
					((HttpURLConnection) httpConn).setRequestMethod(GET_METHOD);
				}
				
				if (header != null) {
			        Iterator<String> it = header.keySet().iterator();
			        String key = "";
			        
			        while(it.hasNext()) {
			        	 key = it.next();
			        	 httpConn.setRequestProperty(key, String.valueOf(header.get(key)));
			        }
				}
				
				int nStatus = 0;
				if (isSSL) {
					nStatus = ((HttpsURLConnection) httpConn).getResponseCode();
				} else {
					nStatus = ((HttpURLConnection) httpConn).getResponseCode();
				}
				logger.info("Get Status : {}", nStatus);
				
				InputStream is = new BufferedInputStream(httpConn.getInputStream());
				int nRead = 0;
				byte[] buffer = new byte[BUFFER_SIZE];
				
				while ( (nRead = is.read(buffer)) != -1) {
					sResponse = new String(buffer, 0, nRead);
				}
				
				is.close();
				
				Map<String, List<String>> resHeader = httpConn.getHeaderFields();
				
				resMap.put(STATUS_KEY, nStatus);
				resMap.put(BODY_KEY, sResponse);
				resMap.put(HEADERS_KEY, resHeader);
				
			} catch (IOException e) {
				logger.error("", e);
			} finally {
				if (httpConn != null) {
					if (isSSL) {
						((HttpsURLConnection) httpConn).disconnect();
					} else {
						((HttpURLConnection) httpConn).disconnect();
					}
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
		private PostRequest() {
			super();
		}
		
		private static String convertParam(Map<String, Object> param) {
			List<String> listKey = new ArrayList<>();
			Iterator<String> it = param.keySet().iterator();
			while (it.hasNext()) {
				listKey.add(it.next());
			}

			StringBuilder sb = new StringBuilder();
			String key = "";

			for (int i=0; i < listKey.size(); i++) {
				key = listKey.get(i);

				if (i == 0) {
					sb.append(key).append("=").append(param.get(key));
				} else {
					sb.append("&").append(key).append("=").append(param.get(key));
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
		 * @return
		 */
		public static Map<String, Object> postMap(boolean isSSL, String sUrl, Map<String, Object> header, Map<String, Object> param, Charset charset) {
			Map<String, Object> resMap = new HashMap<>();
			String sResponse = "";
			
			URL url = null;
			try {
				url = new URL(sUrl);
				
			} catch (MalformedURLException e) {
				logger.error("", e);
			}
			
			if (url == null) {
				return resMap;
			}
			
			URLConnection httpConn = getURLConnection(url);
			try {
				if (httpConn == null) {
					return resMap;
				}
				
				if (isSSL) {
					((HttpsURLConnection) httpConn).setRequestMethod(POST_METHOD);					
				} else {
					((HttpURLConnection) httpConn).setRequestMethod(POST_METHOD);
				}
				httpConn.setRequestProperty(CONTENT_TYPE, "application/x-www-form-urlencoded");
				
				// Default : Get - true, Post - false
				httpConn.setDoOutput(true);
				
				if (header != null) {
			        Iterator<String> it = header.keySet().iterator();
			        String key = "";
			        
			        while(it.hasNext()) {
			        	 key = it.next();
			        	 httpConn.setRequestProperty(key, String.valueOf(header.get(key)));
			        }
				}
				
				if (param != null) {
					String sParam = convertParam(param);
					
					OutputStream os = httpConn.getOutputStream();
					
					if (charset != null) {
						os.write(sParam.getBytes(charset));
					} else {
						os.write(sParam.getBytes(DEFAULT_CHARSET));
					}
					
					os.flush();
					os.close();
				}
				
				int nStatus = 0;
				if (isSSL) {
					nStatus = ((HttpsURLConnection) httpConn).getResponseCode();
				} else {
					nStatus = ((HttpURLConnection) httpConn).getResponseCode();
				}
				logger.info("Post Status : {}", nStatus);
				
				InputStream is = new BufferedInputStream(httpConn.getInputStream());
				int nRead = 0;
				byte[] buffer = new byte[BUFFER_SIZE];
				
				while ( (nRead = is.read(buffer)) != -1) {
					sResponse = new String(buffer, 0, nRead);
				}
				
				is.close();
				
				Map<String, List<String>> resHeader = httpConn.getHeaderFields();
				
				resMap.put(STATUS_KEY, nStatus);
				resMap.put(BODY_KEY, sResponse);
				resMap.put(HEADERS_KEY, resHeader);
				
			} catch (IOException e) {
				logger.error("", e);
			} finally {
				if (httpConn != null) {
					if (isSSL) {
						((HttpsURLConnection) httpConn).disconnect();
					} else {
						((HttpURLConnection) httpConn).disconnect();
					}
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
		 *  - 키 : HttpConnectionUtil.STATUS_KEY, HttpConnectionUtil.BODY_KEY, HttpConnectionUtil.HEADERS_KEY
		 * </pre>
		 * @param isJson
		 * @param isSSL
		 * @param sUrl
		 * @param header
		 * @param payload
		 * @return
		 */
		public static Map<String, Object> rawMap(boolean isJson, boolean isSSL, String sUrl, Map<String, Object> header, String payload) {
			Map<String, Object> resMap = new HashMap<>();
			String sResponse = "";
			
			URL url = null;
			try {
				url = new URL(sUrl);
				
			} catch (MalformedURLException e) {
				logger.error("", e);
			}
			
			if (url == null) {
				return resMap;
			}
			
			URLConnection httpConn = getURLConnection(url);
			try {
				if (httpConn == null) {
					return resMap;
				}
				
				if (isSSL) {
					((HttpsURLConnection) httpConn).setRequestMethod(POST_METHOD);					
				} else {
					((HttpURLConnection) httpConn).setRequestMethod(POST_METHOD);
				}
				
				if (isJson) {
					httpConn.setRequestProperty(CONTENT_TYPE, "application/json");
				} else {
					httpConn.setRequestProperty(CONTENT_TYPE, "application/xml");
				}
				
				// Default : Get - true, Post - false
				httpConn.setDoOutput(true);
				
				if (header != null) {
			        Iterator<String> it = header.keySet().iterator();
			        String key = "";
			        
			        while(it.hasNext()) {
			        	 key = it.next();
			        	 httpConn.setRequestProperty(key, String.valueOf(header.get(key)));
			        }
				}
				
				OutputStream os = httpConn.getOutputStream();
				os.write(payload.getBytes(DEFAULT_CHARSET));
				os.flush();
				os.close();
				
				int nStatus = 0;
				if (isSSL) {
					nStatus = ((HttpsURLConnection) httpConn).getResponseCode();
				} else {
					nStatus = ((HttpURLConnection) httpConn).getResponseCode();
				}
				logger.info("Post Raw Status : {}", nStatus);
				
				InputStream is = new BufferedInputStream(httpConn.getInputStream());
				int nRead = 0;
				byte[] buffer = new byte[BUFFER_SIZE];
				
				while ( (nRead = is.read(buffer)) != -1) {
					sResponse = new String(buffer, 0, nRead);
				}
				
				is.close();
				
				Map<String, List<String>> resHeader = httpConn.getHeaderFields();
				
				resMap.put(STATUS_KEY, nStatus);
				resMap.put(BODY_KEY, sResponse);
				resMap.put(HEADERS_KEY, resHeader);
				
			} catch (IOException e) {
				logger.error("", e);
			} finally {
				if (httpConn != null) {
					if (isSSL) {
						((HttpsURLConnection) httpConn).disconnect();
					} else {
						((HttpURLConnection) httpConn).disconnect();
					}
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
		
		// XXX - https://blog.morizyun.com/blog/android-httpurlconnection-post-multipart/
	}
	
}
