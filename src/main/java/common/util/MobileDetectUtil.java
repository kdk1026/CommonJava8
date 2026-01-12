package common.util;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import common.BaseObject;

/**
 *
 * <pre>
 * common.util.useragent.YauaaParserUtil 사용
 * </pre>
 *
 *
 * @author 김대광
 */
public class MobileDetectUtil {

	private static final String ANDROID = "android";
	private static final String IOS = "ios";

	private MobileDetectUtil() {
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

	public static class DeviceType extends BaseObject {
		private static final long serialVersionUID = 1L;

		private boolean isTablet;
		private boolean isMobile;
		/**
		 * <pre>
		 * android
		 * ios
		 * </pre>
		 */
		private String devicePlatform;

		private DeviceType(boolean isTablet, boolean isMobile, String devicePlatform) {
			super();
			this.isTablet = isTablet;
			this.isMobile = isMobile;
			this.devicePlatform = devicePlatform;
		}

		public boolean isTablet() {
			return isTablet;
		}

		public boolean isMobile() {
			return isMobile;
		}

		public String getDevicePlatform() {
			return devicePlatform;
		}

		@Override
		public boolean equals(Object obj) {
			return super.equals(obj);
		}

		@Override
		public String toString() {
			return super.toString();
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}
	}

	public static DeviceType detecteDevice(HttpServletRequest request) {
		Objects.requireNonNull(request, ExceptionMessage.isNull("request"));

		DeviceType deviceType = null;
		String userAgent = request.getHeader("User-Agent");

		if (userAgent != null) {
			userAgent = userAgent.toLowerCase();

			deviceType = isTablet(userAgent);

			if (deviceType == null) {
				deviceType = isMobile(userAgent);
			}
		}

		return deviceType;
	}

	private static DeviceType isTablet(String userAgent) {
		if ( StringUtils.isBlank(userAgent) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("userAgent"));
		}

		DeviceType deviceType = null;

		if ( (userAgent.contains(ANDROID)) && (!userAgent.contains("mobile")) ) {
			deviceType = new DeviceType(true, false, ANDROID);
		} else if ( userAgent.contains("ipad") ) {
			deviceType = new DeviceType(true, false, IOS);
		}

		return deviceType;
	}

	private static DeviceType isMobile(String userAgent) {
		if ( StringUtils.isBlank(userAgent) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("userAgent"));
		}

		DeviceType deviceType = null;

		if ( userAgent.contains(ANDROID) ) {
			deviceType = new DeviceType(false, true, ANDROID);
		} else if ( userAgent.contains("iphone") || userAgent.contains("ipod") ) {
			deviceType = new DeviceType(false, true, IOS);
		}

		return deviceType;
	}

}
