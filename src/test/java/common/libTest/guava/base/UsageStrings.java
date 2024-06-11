package common.libTest.guava.base;

import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

public class UsageStrings {

	public static void isNullOrEmpty() {
		String str1 = "";
		String str2 = " ";
		
		System.out.println("isNullOrEmpty : " + Strings.isNullOrEmpty(str1) );
		System.out.println("isNullOrEmpty : " + Strings.isNullOrEmpty(str2) );
	}
	
	public static void defaultStr() {
		String str = null;
		System.out.println("nullToEmpty : " + Strings.nullToEmpty(str) );
		System.out.println("firstNonNull : " + MoreObjects.firstNonNull(str, "Hello") );
	}
	
	public static void deleteWhitespace() {
		String str = " a b c";
		System.out.println( CharMatcher.is(' ').removeFrom(str) );
	}
	
	public static void removeCarriageReturn() {
		String str = "abc \r\n";
		System.out.println( CharMatcher.breakingWhitespace().removeFrom(str) );
	}
	
	public static void leftPad_rightPad() {
		String str = "1";
		System.out.println("leftPad : " + Strings.padStart(str, 3, '0') );
		System.out.println("rightPad : " +  Strings.padEnd(str, 3, '0') );
	}
	
	public static void trim() {
		String str = " abc ";
		System.out.println( CharMatcher.whitespace().trimFrom(str) );
	}
	
	public static void join() {
		String[] arrStr = {"A", "B", "C"};
		System.out.println( Joiner.on("-").join(arrStr) );
	}

	public static void capitalize() {
		String str1 = "apple";
		String str2 = "Apple";
		System.out.println( CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, str1) );
		System.out.println( CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, str2) );
	}
	
}
