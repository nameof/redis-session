package cas.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import cas.custom.component.CacheHttpSessionFactory;
import cas.custom.component.DefaultCacheHttpSession;
import cas.models.User;
import cas.utils.JsonUtils;
/**
 * 接收CAS客户端站点的token验证请求，成功返回用户信息
 * @author ChengPan
 */
public class AuthenticationFilter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		 HttpServletRequest req = (HttpServletRequest)request;  
	     HttpServletResponse resp = (HttpServletResponse)response;
	     String token = req.getParameter("token");
	     if (StringUtils.isNotBlank(token)) {
	    	 HttpSession session = CacheHttpSessionFactory.newSessionInstance(req.getSession(), token);
	    	 User user = (User) session.getAttribute("user");
	    	 if (user != null) {
	    		 resp.getWriter().write(JsonUtils.toJSONString(user));
	    	 }
	     }
	}

	@Override
	public void destroy() {
		
	}

}
