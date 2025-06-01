package common.libTest;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.i18n.phonenumbers.PhoneNumberUtil;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 6. 1. kdk	최초작성
 * </pre>
 *
 *
 * @author kdk
 */
public class TestLibphonenumber {

	@Test
	public void test() {
		assertTrue( PhoneNumberUtil.getInstance().isPossibleNumber("010-1234-5678", "KR") );
		assertTrue( PhoneNumberUtil.getInstance().isPossibleNumber("01012345678", "KR") );

		assertTrue( PhoneNumberUtil.getInstance().isPossibleNumber("02-1234-5678", "KR") );
		assertTrue( PhoneNumberUtil.getInstance().isPossibleNumber("02-123-5678", "KR") );

		assertTrue( PhoneNumberUtil.getInstance().isPossibleNumber("031-1234-5678", "KR") );

		// XXX 형식만 체크하므로 국내 번호는 정규식으로 체크하고, 해외 번호 필요 시 사용하면 될 듯
		assertTrue( PhoneNumberUtil.getInstance().isPossibleNumber("039-1234-5678", "KR") );
	}

}
