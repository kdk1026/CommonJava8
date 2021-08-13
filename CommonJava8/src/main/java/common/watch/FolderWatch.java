package common.watch;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 7.  8. 김대광	최초작성
 * 2021. 8. 13. 김대광	SonarLint 지시에 따른 수정 (Complexity 는 언제나 그렇듯 어쩔 수가 없단다....) 
 * </pre>
 * 
 * 실행 가능한 JAR 프로젝트에 생성하여 nohup java -jar JAR파일 형태로 백그라운드로 실행
 * @author 김대광
 */
public class FolderWatch {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 외부에서 객체 인스턴스화 불가
	 */
	private FolderWatch() {
		super();
	}
	
	public static FolderWatch getInstance() {
		return LazyHolder.INSTANCE;
	}

	private static class LazyHolder {
		private static final FolderWatch INSTANCE = new FolderWatch();
	}
	
	public void startWatch(String destPath, boolean isAdditionalWork) {
		WatchService service;
		try {
			service = FileSystems.getDefault().newWatchService();

			Path path = Paths.get(destPath);

			// XXX : StandardWatchEventKinds.ENTRY_MODIFY 추가 시, 너무 많은 이벤트 감지됨
			path.register(service
					, StandardWatchEventKinds.ENTRY_CREATE
					, StandardWatchEventKinds.ENTRY_DELETE);

			while (true) {
				WatchKey key = service.take();
				List<WatchEvent<?>> list = key.pollEvents();

				for (WatchEvent<?> event : list) {
					Kind<?> kind = event.kind();
					Path pth = (Path) event.context();

					if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
						logger.debug("생성 : {}", pth.getFileName());

						if ( isAdditionalWork ) {
							// 추가 작업
						}
					}
					else if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
						logger.debug("삭제 : {}", pth.getFileName());

						if ( isAdditionalWork ) {
							// 추가 작업
						}
					}
					else if (kind.equals(StandardWatchEventKinds.OVERFLOW)) {
						logger.debug("OS에서 이벤트가 소실되었거나 버려진 경우");
					}
				}

				if ( !key.reset() ) break;
			}

			service.close();

		} catch (IOException | InterruptedException e) {
			logger.error("", e);
			
			// Restore interrupted state...
		    Thread.currentThread().interrupt();

		}
	}
}
