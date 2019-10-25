package common.util.date;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 김대광
 * @Description ICU4J 필요
 * 	- 부득이하게 서버 시간이 일치하지 않는 경우 사용
 * <pre>
 * -----------------------------------
 * 개정이력
 * </pre>
 */
public class NtpDateUtil {

	private NtpDateUtil() {
		super();
	}

	private static final Logger logger = LoggerFactory.getLogger(NtpDateUtil.class);

	private static final String TIME_SERVER = "time.windows.com";

	public static Date getTodayDate() {
		long nRetTime = 0;

		NTPUDPClient timeClient = new NTPUDPClient();
		InetAddress inetAddress;

		try {
			inetAddress = InetAddress.getByName(TIME_SERVER);
			TimeInfo timeInfo = timeClient.getTime(inetAddress);

			nRetTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();

		} catch (UnknownHostException e) {
			logger.error("", e);
		} catch (IOException e) {
			logger.error("", e);
		}

		return new Date(nRetTime);
	}

}
