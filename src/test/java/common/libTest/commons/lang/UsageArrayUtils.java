package common.libTest.commons.lang;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

public class UsageArrayUtils {
	
	public static void add_test() {
		String[] arrStr = {"a01", "b01", "c01"};
		String[] arrStrNew = ArrayUtils.add(arrStr, "d01");

		System.out.println("add_end : " +  ArrayUtils.toString(arrStrNew) );

		arrStrNew = ArrayUtils.insert(1, arrStr, "d01");
		System.out.println("add_index : " +  ArrayUtils.toString(arrStrNew) );
	}

	public static void addAll_test() {
		String[] arrStr = {"a01", "b01", "c01"};
		String[] arrStrNew = ArrayUtils.addAll(arrStr, "d01", "e01");

		System.out.println( ArrayUtils.toString(arrStrNew) );
	}

	public static void remove_test() {
		String[] arrStr = {"a01", "b01", "c01"};

		String[] arrStrNew = ArrayUtils.remove(arrStr, 0);
		System.out.println("remove : " +  ArrayUtils.toString(arrStrNew) );

		arrStrNew = ArrayUtils.removeElement(arrStr, "b01");
		System.out.println("removeElement : " +  ArrayUtils.toString(arrStrNew) );

		arrStrNew = ArrayUtils.removeElements(arrStr, "b01", "c01");
		System.out.println("removeElements : " +  ArrayUtils.toString(arrStrNew) );
	}

	public static void contains_indexOf_test() {
		String[] arrStr = {"a01", "b01", "c01"};

		System.out.println("contains : " + ArrayUtils.contains(arrStr, "b01") );
		System.out.println("indexOf : " + ArrayUtils.indexOf(arrStr, "b01") );
	}

	public static void isEmpty_isNotEmpty_test() {
		String[] arrStr = new String[0];

		System.out.println("isEmpty : " + ArrayUtils.isEmpty(arrStr) );
		System.out.println("isNotEmpty : " + ArrayUtils.isNotEmpty(arrStr) );
	}

	@SuppressWarnings("null")
	public static void nullToEmpty_test() {
		String[] arrStr = null;
		String[] arrStrNew = ArrayUtils.nullToEmpty(arrStr);

		try {
			System.out.println("arrStr length : " + arrStr.length);
		} catch (Exception e) {
			System.out.println("arrStr length is null ");
		}

		System.out.println("arrStrNew length : " + arrStrNew.length);
	}

	public static void reverse_test() {
		String[] arrStr = {"a01", "b01", "c01"};

		ArrayUtils.reverse(arrStr);
		System.out.println( ArrayUtils.toString(arrStr) );
	}

	public static void toMap_test() {
		String[][] arrStr = {{"a01","aaa"}, {"b01","bbb"}, {"c01","ccc"}};
		Map<Object, Object> map = ArrayUtils.toMap(arrStr);

		System.out.println(map);
	}

}
