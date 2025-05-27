package common.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2021. 7. 8. kdk	최초작성
 * 2021. 8. 13. kdk		SonarLint 지시에 따른 수정 (symLinkPath.toFile().delete(); -> Files.delete(symLinkPath);)
 * 2025. 5. 27. 김대광	유틸은 Singleton 패턴을 사용하지 않는 것이 좋다는 의견 반영
 * </pre>
 *
 * @Description	: 1.7 기반
 * @author kdk
 */
public class SymbolicLinkUtil {

	private static final Logger logger = LoggerFactory.getLogger(SymbolicLinkUtil.class);

	private SymbolicLinkUtil() {
		super();
	}

	/**
	 * 윈도우의 경우, 관리작 권한으로 실행해야 함
	 * @param srcPathStr
	 * @param symLinkPathStr
	 * @return
	 */
	public static boolean makeSymbolicLink(String srcPathStr, String symLinkPathStr) {
		if ( StringUtils.isBlank(srcPathStr) ) {
			throw new IllegalArgumentException("srcPathStr");
		}

		if ( StringUtils.isBlank(symLinkPathStr) ) {
			throw new IllegalArgumentException("symLinkPathStr");
		}

		boolean isSuccess = false;

		Path srcPath = Paths.get(srcPathStr);
		Path symLinkPath = Paths.get(symLinkPathStr);


		try {
			boolean isExist = srcPath.toFile().exists();
			if ( !isExist ) {
				Files.createSymbolicLink(symLinkPath, srcPath);
			}

			boolean isSymLink = Files.isSymbolicLink(symLinkPath);
			if ( !isSymLink ) {
				Files.delete(symLinkPath);
				Files.createSymbolicLink(symLinkPath, srcPath);
			}

			isSuccess = true;

		} catch (IOException e) {
			logger.error("", e);
		}

		return isSuccess;
	}

}
