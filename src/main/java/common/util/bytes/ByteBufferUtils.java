package common.util.bytes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.util.ExceptionMessage;

public class ByteBufferUtils {

	private ByteBufferUtils() {
		super();
	}

	private static final Logger logger = LoggerFactory.getLogger(ByteBufferUtils.class);

	public static ByteBuffer toByteBufferString(String str, String sEncoding) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("str"));
		}

		if ( StringUtils.isBlank(sEncoding) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("sEncoding"));
		}

		byte[] b = null;

		try {
			b = str.getBytes(sEncoding);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}

		if (b == null) {
			return null;
		}

		return ByteBuffer.wrap(b);
	}

	public static ByteBuffer toByteBufferString(List<String> list, String sEncoding) {
		if ( list == null || list.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("list"));
		}

		if ( StringUtils.isBlank(sEncoding) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("sEncoding"));
		}

		int nByteLen = 0;

		for (String s : list) {
			try {
				nByteLen += s.getBytes(sEncoding).length;
			} catch (UnsupportedEncodingException e) {
				logger.error("", e);
			}
		}

		ByteBuffer buffer = ByteBuffer.allocate(nByteLen);

		for (String s : list) {
			try {
				buffer.put(s.getBytes(sEncoding));
			} catch (UnsupportedEncodingException e) {
				logger.error("", e);
			}
		}

		buffer.flip();
		return buffer;
	}

	public static ByteBuffer toByteBufferObject(Object obj, String sEncoding) {
		if ( obj == null ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("obj"));
		}

		if ( StringUtils.isBlank(sEncoding) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("sEncoding"));
		}

		int nByteLen = 0;
		Field[] fields = obj.getClass().getDeclaredFields();

		try {
			for (Field f : fields) {
				f.setAccessible(true);
				nByteLen += f.get(obj).toString().getBytes(sEncoding).length;
			}

		} catch (UnsupportedEncodingException | IllegalArgumentException | IllegalAccessException e) {
			logger.error("", e);
		}

		ByteBuffer buffer = ByteBuffer.allocate(nByteLen);

		try {
			for (Field f : fields) {
				f.setAccessible(true);
				buffer.put(f.get(obj).toString().getBytes(sEncoding));
			}

		} catch (UnsupportedEncodingException | IllegalArgumentException | IllegalAccessException e) {
			logger.error("", e);
		}

		buffer.flip();
		return buffer;
	}

	public static ByteBuffer toByteBufferObject(List<Object> list, String sEncoding) {
		if ( list == null || list.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("list"));
		}

		if ( StringUtils.isBlank(sEncoding) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("sEncoding"));
		}

		int nByteLen = 0;
		Field[] fields = null;

		for (Object obj : list) {
			try {
				fields = obj.getClass().getDeclaredFields();
				for (Field f : fields) {
					f.setAccessible(true);
					nByteLen += f.get(obj).toString().getBytes(sEncoding).length;
				}
			} catch (UnsupportedEncodingException | IllegalArgumentException | IllegalAccessException e) {
				logger.error("", e);
			}
		}

		ByteBuffer buffer = ByteBuffer.allocate(nByteLen);

		for (Object obj : list) {
			try {
				fields = obj.getClass().getDeclaredFields();
				for (Field f : fields) {
					f.setAccessible(true);
					buffer.put(f.get(obj).toString().getBytes(sEncoding));
				}
			} catch (IOException | IllegalArgumentException | IllegalAccessException e) {
				logger.error("", e);
			}
		}

		buffer.flip();
		return buffer;
	}

	public static ByteBuffer toByteBufferMap(Map<String, Object> map, String sEncoding) {
		if ( map == null || map.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("map"));
		}

		if ( StringUtils.isBlank(sEncoding) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("sEncoding"));
		}

		int nByteLen = 0;
		String sKey = "";
		Iterator<String> it = map.keySet().iterator();

		while (it.hasNext()) {
			sKey = it.next();
			nByteLen += String.valueOf(map.get(sKey)).getBytes().length;
		}

		ByteBuffer buffer = ByteBuffer.allocate(nByteLen);

		while (it.hasNext()) {
			sKey = it.next();
			try {
				buffer.put(String.valueOf(map.get(sKey)).getBytes(sEncoding));
			} catch (UnsupportedEncodingException e) {
				logger.error("", e);
			}
		}

		buffer.flip();
		return buffer;
	}

	public static ByteBuffer toByteBufferMap(List<Map<String, Object>> list, String sEncoding) {
		if ( list == null || list.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("list"));
		}

		if ( StringUtils.isBlank(sEncoding) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("sEncoding"));
		}

		int nByteLen = 0;
		String sKey = "";
		Iterator<String> it = null;

		for (Map<String, Object> map : list) {
			it = map.keySet().iterator();
			while (it.hasNext()) {
				sKey = it.next();
				nByteLen += String.valueOf(map.get(sKey)).getBytes().length;
			}
		}

		ByteBuffer buffer = ByteBuffer.allocate(nByteLen);

		for (Map<String, Object> map : list) {
			it = map.keySet().iterator();
			while (it.hasNext()) {
				sKey = it.next();
				try {
					buffer.put(String.valueOf(map.get(sKey)).getBytes(sEncoding));
				} catch (UnsupportedEncodingException e) {
					logger.error("", e);
				}
			}
		}

		buffer.flip();
		return buffer;
	}

	public static ByteBuffer getByteBufferFromByteArray(byte[] bytesArray) {
		return ByteBuffer.wrap(bytesArray);
	}

	public static byte[] getByteArrayFromByteBuffer(ByteBuffer byteBuffer) {
		byte[] bytesArray = new byte[byteBuffer.remaining()];
		byteBuffer.get(bytesArray, 0, bytesArray.length);
		return bytesArray;
	}

	public static byte[] getByteArrayFromByteBufferLimit(ByteBuffer byteBuffer, int newPosition, int limit) {
		byteBuffer.position(newPosition);
		byteBuffer.limit(limit);

		byte[] bytesArray = new byte[byteBuffer.remaining()];
		byteBuffer.get(bytesArray);

		return bytesArray;
	}

	public static byte[] getByteArrayFromByteBufferLength(ByteBuffer byteBuffer, int newPosition, int length) {
		byteBuffer.position(newPosition);
		byteBuffer.limit(newPosition + length);

		byte[] bytesArray = new byte[byteBuffer.remaining()];
		byteBuffer.get(bytesArray);

		return bytesArray;
	}

}
