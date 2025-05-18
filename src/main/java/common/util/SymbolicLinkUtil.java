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
 * 2021. 8. 13. kdk	SonarLint 지시에 따른 수정 (symLinkPath.toFile().delete(); -> Files.delete(symLinkPath);)
 * </pre>
 *
 * @Description	: 1.7 기반
 * @author kdk
 */
public class SymbolicLinkUtil {

	private static final Logger logger = LoggerFactory.getLogger(SymbolicLinkUtil.class);

	/**
	 * 외부에서 객체 인스턴스화 불가
	 */
	private SymbolicLinkUtil() {
		super();
	}

	private static class LazyHolder {
		private static final SymbolicLinkUtil INSTANCE = new SymbolicLinkUtil();
	}

	public static SymbolicLinkUtil getInstance() {
		return LazyHolder.INSTANCE;
	}

	/**
	 * 윈도우의 경우, 관리작 권한으로 실행해야 함
	 * @param srcPathStr
	 * @param symLinkPathStr
	 * @return
	 */
	public boolean makeSymbolicLink(String srcPathStr, String symLinkPathStr) {
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
