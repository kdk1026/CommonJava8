package common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 6. 9. kdk	최초작성
 * </pre>
 *
 * <pre>
 * Gzip 압축 후 Base64 인코딩
 *  - 데이터의 크기를 줄여 효율적으로 전송하거나 저장해야 할 때
 *  - 바이너리 데이터를 텍스트 기반 시스템에서 다루어야 할 때
 * </pre>
 *
 * @author kdk
 */
public class GzipCompressorUtil {

	private static final String UTF_8 = StandardCharsets.UTF_8.name();

	private GzipCompressorUtil() {
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

    /**
     * 문자열을 Gzip으로 압축 후 Base64 인코딩
     * @param data
     * @return
     * @throws IOException
     */
    public static String compress(String data) throws IOException {
    	if ( StringUtils.isBlank(data) ) {
    		throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("data"));
    	}

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream)) {
            gzipStream.write(data.getBytes(UTF_8));
        }
        return Base64.getEncoder().encodeToString(byteStream.toByteArray());
    }

    /**
     * Base64 디코딩 후 Gzip 압축 해제
     * @param compressedData
     * @return
     * @throws IOException
     */
    public static String decompress(String compressedData) throws IOException {
    	if ( StringUtils.isBlank(compressedData) ) {
    		throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("compressedData"));
    	}

        byte[] compressedBytes = Base64.getDecoder().decode(compressedData);
        ByteArrayInputStream byteStream = new ByteArrayInputStream(compressedBytes);
        try (GZIPInputStream gzipStream = new GZIPInputStream(byteStream)) {
            return new String(readAllBytes(gzipStream), UTF_8);
        }
    }

    /**
     * InputStream.readAllBytes Java 8 대응
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
    	Objects.requireNonNull(inputStream, ExceptionMessage.isNull("inputStream"));

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] temp = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(temp)) != -1) {
            buffer.write(temp, 0, bytesRead);
        }
        return buffer.toByteArray();
    }

}
