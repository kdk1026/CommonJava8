package common.libTest.commons.lang;

import org.apache.commons.lang3.StringUtils;

public class UsageStringUtils {

	public static void isEmpty_isBlank() {
		String str1 = "";
		String str2 = " ";
		System.out.println("isEmpty : " + StringUtils.isEmpty(str1) );
		System.out.println("isNotEmpty : " +  StringUtils.isNotEmpty(str2) );

		System.out.println("isBlank : " +  StringUtils.isBlank(str2) );
		System.out.println("isNotBlank : " +  StringUtils.isBlank(str1) );
	}

	public static void defaultStr() {
		String str1 = null;
		String str2 = "";
		System.out.println("defaultString : " + StringUtils.defaultString(str1, "Hello") );
		System.out.println("defaultIfEmpty : " + StringUtils.defaultIfEmpty(str2, "Hello") );
	}

	public static void containsOnly() {
		String str = "abcd";
		System.out.println("containsOnly : " + StringUtils.containsOnly(str, "abcd") );
	}

	public static void deleteWhitespace() {
		String str = " a b c";
		System.out.println( StringUtils.deleteWhitespace(str) );
	}

	public static void removeCarriageReturn() {
		String str = "abc \r\n";
		System.out.println( StringUtils.chomp(str) );
	}
	
	public static void isNumeric() {
		String str = "1234";
		System.out.println( StringUtils.isNumeric(str) );
	}

	public static void leftPad_rightPad() {
		String str = "1";
		System.out.println("leftPad : " + StringUtils.leftPad(str, 3, "0") );
		System.out.println("rightPad : " +  StringUtils.rightPad(str, 3, "0") );
	}

	public static void trim() {
		String str = " abc ";
		System.out.println( StringUtils.trim(str) );
	}

	public static void join() {
		String[] arrStr = {"A", "B", "C"};
		System.out.println( StringUtils.join(arrStr, "-") );
	}

	public static void capitalize() {
		String str1 = "apple";
		String str2 = "Apple";
		System.out.println( "capitalize : " + StringUtils.capitalize(str1) );
		System.out.println( "uncapitalize : " + StringUtils.uncapitalize(str2) );
	}

}
