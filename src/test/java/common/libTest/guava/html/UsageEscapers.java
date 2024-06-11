package common.libTest.guava.html;

import com.google.common.html.HtmlEscapers;

public class UsageEscapers {

	public static void escapeHtml() {
		String str = "<script>alert(1);</script>";
		System.out.println( HtmlEscapers.htmlEscaper().escape(str) );
	}
	
}
