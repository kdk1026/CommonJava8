package common.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArrayUtil {

	protected ArrayUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	/**
	 * Array를 List 객체로 변환
	 * @param arry
	 * @return
	 */
	public static <T> List<T> arrayToList(T[] arry) {
		if ( arry == null || arry.length == 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("arry"));
		}

		return Arrays.asList(arry);
    }

	/**
	 * List 객체를 Array로 변환
	 * @param list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] listtoArray(List<T> list) {
		if ( list == null || list.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("list"));
		}

		return (T[]) list.toArray();
	}

	/**
	 * 배열 정렬
	 * @param arry
	 * @return
	 */
	public static <T> T[] arraySort(T[] arry) {
		if ( arry == null || arry.length == 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("arry"));
		}

		Arrays.sort(arry);
		return arry;
	}

	/**
	 * <pre>
	 * 배열 내림차순 정렬
	 *   - 요소 중 큰건을 위로 정렬
	 * </pre>
	 * @param arry
	 * @return
	 */
	public static <T> T[] arrayReverseSort(T[] arry) {
		if ( arry == null || arry.length == 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("arry"));
		}

		Arrays.sort(arry, Collections.reverseOrder());
		return arry;
	}

}
