package common.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 9. 김대광	JavaDoc 작성
 * </pre>
 * 
 * <pre>
 * Servlet 2.5 시절 방식
 * Servlet 3.0 이후는
 * 	/WEB-INF/web.xml
 * 		<session-config>
 * 			<cookie-config>
 * 				<http-only>true</http-only>
 * 			</cookie-config>
 * 		</session-config>
 * </pre>
 *
 * @author 김대광
 */
public class SessionCookieFilter implements Filter {

	@Override
	public void destroy() {
		// Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		/** Cookie Hijacking 방지 */
		String sessionid = request.getSession().getId();
		response.setHeader("Set-Cookie", "JSESSIONID=" + sessionid + "; Path=/; HttpOnly");
		
		chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// Auto-generated method stub
	}

}
