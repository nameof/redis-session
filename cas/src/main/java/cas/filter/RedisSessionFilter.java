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

import cas.custom.component.CasHttpServletRequest;
import cas.custom.component.CustomSessionProcessor;
import cas.utils.RedisUtil;

public class RedisSessionFilter implements Filter{

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;  
        HttpServletResponse resp = (HttpServletResponse)response;  
        CasHttpServletRequest wrapper = new CasHttpServletRequest(req, resp);  
        chain.doFilter(wrapper, response);
        
        if (wrapper.getSession() instanceof CustomSessionProcessor) {
        	((CustomSessionProcessor) wrapper.getSession()).commit();
        }
        RedisUtil.returnResource();//release redis
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

}
