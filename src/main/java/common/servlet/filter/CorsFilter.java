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
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 9. 김대광	JavaDoc 작성
 * </pre>
 * 
 * <pre>
 * Spring / SpringSeucirty 사용 시, 자체 설정 이용할 것
 * </pre>
 *
 * @author 김대광
 */
public class CorsFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, x-requested-with, Authorization");
		
		chain.doFilter(req, res);
	}

	@Override
	public void destroy() {
		// Auto-generated method stub
	}
	
}
