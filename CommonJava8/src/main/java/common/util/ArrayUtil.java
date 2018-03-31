package common.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArrayUtil {
	
	private ArrayUtil() {
		super();
	}

	/**
	 * Array를 List 객체로 변환
	 * @param arry
	 * @return
	 */
	public static <T> List<T> arrayToList(T[] arry) {
		return Arrays.asList(arry);
    }

	/**
	 * List 객체를 Array로 변환
	 * @param list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] listtoArray(List<T> list) {
		return (T[]) list.toArray();
	}

	/**
	 * 배열 정렬
	 * @param arry
	 * @return
	 */
	public static <T> T[] arraySort(T[] arry) {
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
		Arrays.sort(arry, Collections.reverseOrder());
		return arry;
	}

}
