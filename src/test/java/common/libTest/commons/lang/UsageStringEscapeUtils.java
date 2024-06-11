package common.libTest.commons.lang;

import org.apache.commons.text.StringEscapeUtils;

public class UsageStringEscapeUtils {

	public static void main(String[] args) {
		String sHtml = "<script>";
		
		// XXX : Decompiler 통해서 확인 
		String sEscape = StringEscapeUtils.escapeHtml4(sHtml);
		System.out.println( String.format("[Escape] %s", sEscape)  );
		System.out.println( String.format("[UnEscape] %s", StringEscapeUtils.unescapeHtml4(sEscape))  );
		
	}

}
