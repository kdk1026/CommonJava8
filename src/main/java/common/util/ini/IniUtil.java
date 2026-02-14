package common.util.ini;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2026. 1. 11. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public class IniUtil {

	private IniUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	/**
	 * ini 파일 읽기
	 * @param filePath
	 * @param sectionName
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public static String getIni(String filePath, String sectionName, String key) throws IOException {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("filePath"));
		}

		if ( StringUtils.isBlank(sectionName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sectionName"));
		}

		if ( StringUtils.isBlank(key) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("key"));
		}

		File file = new File(filePath);
		Ini ini = new Ini(file);

		return ini.get(sectionName, key);
	}

	/**
	 * ini 파일 읽기
	 * @param filePath
	 * @param sectionName
	 * @return
	 * @throws IOException
	 */
	public static List<String> getIniSection(String filePath, String sectionName) throws IOException {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("filePath"));
		}

		if ( StringUtils.isBlank(sectionName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sectionName"));
		}

		List<String> valueList = null;

		File file = new File(filePath);
		Ini ini = new Ini(file);

		Section section = ini.get(sectionName);

		if (section != null) {
			Collection<String> values = section.values();
			valueList = new ArrayList<>(values);
		}

		return valueList;
	}

	/**
	 * ini 파일 읽기
	 * @param filePath
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public static String getIniKey(String filePath, String key) throws IOException {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("filePath"));
		}

		if ( StringUtils.isBlank(key) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("key"));
		}

		String value = null;

		File file = new File(filePath);
		Ini ini = new Ini(file);

		for ( Map.Entry<String, Section> entry : ini.entrySet() ) {
			String sectionName = entry.getKey();
			Section section = ini.get(sectionName);

			if ( section.containsKey(key) ) {
				value = section.get(key);
			}
		}

		return value;
	}

	/**
	 * ini 파일에 추가
	 * @param filePath
	 * @param sectionName
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public static void addIni(String filePath, String sectionName, String key, String value) throws IOException {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("filePath"));
		}

		if ( StringUtils.isBlank(sectionName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sectionName"));
		}

		if ( StringUtils.isBlank(key) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("key"));
		}

		if ( StringUtils.isBlank(value) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("value"));
		}

		File file = new File(filePath);
		Ini ini = new Ini(file);

		ini.add(sectionName, key, value);

		ini.store(file);
	}

	/**
	 * ini 파일에서 기존 섹션, 키의 값 변경
	 * @param filePath
	 * @param sectionName
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public static void setIni(String filePath, String sectionName, String key, String value) throws IOException {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("filePath"));
		}

		if ( StringUtils.isBlank(sectionName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sectionName"));
		}

		if ( StringUtils.isBlank(key) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("key"));
		}

		if ( StringUtils.isBlank(value) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("value"));
		}

		File file = new File(filePath);
		Ini ini = new Ini(file);

		ini.put(sectionName, key, value);

		ini.store(file);
	}

	/**
	 * ini 파일에서 섹션 전체 삭제
	 * @param filePath
	 * @param sectionName
	 * @throws IOException
	 */
	public static void clearIni(String filePath, String sectionName) throws IOException {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("filePath"));
		}

		if ( StringUtils.isBlank(sectionName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sectionName"));
		}

		File file = new File(filePath);
		Ini ini = new Ini(file);

		if ( ini.get(sectionName) != null ) {
			ini.remove(sectionName);

			ini.store(file);
		}
	}

	/**
	 * ini 파일에서 특정 키 삭제
	 * @param filePath
	 * @param sectionName
	 * @param key
	 * @throws IOException
	 */
	public static void clearIni(String filePath, String sectionName, String key) throws IOException {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("filePath"));
		}

		if ( StringUtils.isBlank(sectionName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sectionName"));
		}

		if ( StringUtils.isBlank(key) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("key"));
		}

		File file = new File(filePath);
		Ini ini = new Ini(file);

		if ( ini.get(sectionName, key) != null ) {
			ini.get(sectionName).remove(key);

			ini.store(file);
		}
	}

}
