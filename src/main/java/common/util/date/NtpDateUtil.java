package common.util.date;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 13. 김대광	 SonarLint 지시에 따른 주저리 주저리, 기타 주저리 주저리
 * 			흠... java 명령어로 실행 가능한 경우에는 JVM 옵션 timezone 걸 것
 * </pre>
 *
 *
 * @author 김대광
 * @Description ICU4J 필요 - 부득이하게 서버 시간이 일치하지 않는 경우 사용 (이건 서버 담당자랑 시간 동기화 꺼진거 같다고 협의를... 굳이 이딴걸 사용할 필요가 있을료나...)
 */
public class NtpDateUtil {

	private static final Logger logger = LoggerFactory.getLogger(NtpDateUtil.class);

	private static final String TIME_SERVER = "time.windows.com";

	private NtpDateUtil() {
		super();
	}

	public static Date getTodayDate() {
		long nRetTime = 0;

		NTPUDPClient timeClient = new NTPUDPClient();
		InetAddress inetAddress;

		try {
			inetAddress = InetAddress.getByName(TIME_SERVER);
			TimeInfo timeInfo = timeClient.getTime(inetAddress);

			nRetTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();

		} catch (IOException e) {
			logger.error("", e);
		}

		return new Date(nRetTime);
	}

}
