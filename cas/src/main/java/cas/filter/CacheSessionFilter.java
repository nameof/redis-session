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

import cas.custom.component.request.CustomHttpServletRequest;
import cas.custom.component.session.CustomSessionProcessor;
import cas.utils.RedisUtil;

/**
 * 处理请求之前，对HttpServletRequest实现包装
 * 请求完成之后，提交自定义Session数据到缓存中，并释放缓存连接资源
 * @author ChengPan
 */
public class CacheSessionFilter implements Filter{

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;  
        HttpServletResponse resp = (HttpServletResponse)response;  
        CustomHttpServletRequest wrapper = new CustomHttpServletRequest(req, resp);
        chain.doFilter(wrapper, response);
        
        if (wrapper.getSession(false) instanceof CustomSessionProcessor) {
        	((CustomSessionProcessor) wrapper.getSession()).commit();
        }
        RedisUtil.returnResource();//release redis
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

}
