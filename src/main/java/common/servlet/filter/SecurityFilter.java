package common.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Spring Security 사용 시, 불필요
 */
public class SecurityFilter implements Filter {

	@Override
	public void destroy() {
		// Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletResponse response = (HttpServletResponse) res;
		
		/** Cache Control */
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");

		/** Content Type Options */
		response.setHeader("X-Content-Type-Options", "nosniff");

		/** HTTP Strict Transport Security (HSTS)
		 * 		- HTTPS 에서만 동작 (HTTP 에서는 무시)
		 * */
		response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

		/**	X-Frame-Options
		 * 		- DENY, SAMEORIGIN, ALLO-FROM origin
		 * */
		response.addHeader("X-Frame-Options", "SAMEORIGIN");

		/** X-XSS-Protection */
		response.setHeader("X-XSS-Protection", "1; mode=block");

		chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// Auto-generated method stub
	}

}
