package common.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2021. 7. 8. kdk	최초작성
 * </pre>
 * 
 *
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
				symLinkPath.toFile().delete();
				Files.createSymbolicLink(symLinkPath, srcPath);
			}
			
			isSuccess = true;
			
		} catch (IOException e) {
			logger.error("", e);
		}
		
		return isSuccess;
	}
	
}
