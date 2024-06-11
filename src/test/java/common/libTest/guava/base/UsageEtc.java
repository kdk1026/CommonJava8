package common.libTest.guava.base;

import com.google.common.base.StandardSystemProperty;
import com.google.common.base.Stopwatch;

public class UsageEtc {

	public static void getProperty() {
		StringBuilder sb = null;
		
		sb = new StringBuilder();
		sb.append("[Java] : ").append(StandardSystemProperty.JAVA_VERSION);
		sb.append(", ").append(StandardSystemProperty.JAVA_VENDOR);
		sb.append(", ").append(StandardSystemProperty.JAVA_HOME);
		System.out.println(sb.toString());
		
		sb = new StringBuilder();
		sb.append("[OS] : ").append(StandardSystemProperty.OS_NAME);
		sb.append(", ").append(StandardSystemProperty.OS_ARCH);
		sb.append(", ").append(StandardSystemProperty.OS_VERSION);
		System.out.println(sb.toString());
		
		sb = new StringBuilder();
		sb.append("[User] : ").append(StandardSystemProperty.USER_NAME);
		sb.append(", ").append(StandardSystemProperty.USER_HOME);
		sb.append(", ").append(StandardSystemProperty.USER_DIR);
		System.out.println(sb.toString());
	}
	
	public static void stopWatch() {
		Stopwatch stopWatch = Stopwatch.createUnstarted();
		stopWatch.reset();
		stopWatch.start();
		
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		stopWatch.stop();
		System.out.println(stopWatch.toString());
	}
	
}
