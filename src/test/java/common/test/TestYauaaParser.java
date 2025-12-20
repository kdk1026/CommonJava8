package common.test;

import java.util.Map;

import org.junit.Test;

import common.util.useragent.YauaaParserUtil;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 12. 20. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public class TestYauaaParser {

	@Test
	public void test() {
		String uaString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36";

		Map<String, String> map = YauaaParserUtil.parse(uaString);

		System.out.println(map);
	}

}
