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

import cas.custom.component.SSOServletRequestWrapper;
import cas.utils.RedisUtil;

public class RedisSessionFilter implements Filter{

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
        HttpServletRequest re = (HttpServletRequest)request;  
        HttpServletResponse res = (HttpServletResponse)response;  
        SSOServletRequestWrapper wrapper = new SSOServletRequestWrapper(re,res);  
        chain.doFilter(wrapper, response);
        RedisUtil.returnResource();//release redis
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
