package client.filter;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import client.model.User;
import client.utils.HttpRequest;
import client.utils.JsonUtils;
import client.utils.UrlBuilder;

public class AuthenticationFilter implements Filter{

	/** 登录站点地址 */
	private static String CAS_LOGIN_URL = "http://localhost:8080/cas/login";
	
	/** 票据验证地址 */
	private static String CAS_VALIDATE_TOKEN_URL = "http://localhost:8080/cas/authentication/validatetoken";
	
	private static String URL_ENCODING_CHARSET = "UTF-8";
	
	/** 票据传递参数名 */
	private static  final String TICKET_KEY = "token";
	
	/** 返回地址参数名 */
	private static  final String RETURN_URL_KEY = "returnUrl";
	
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		if (request.getSession().getAttribute("user") == null) {
			
			String token = request.getParameter(TICKET_KEY);
			String url = request.getRequestURL().toString();
			//HTTP请求验证票据合法性
			if (StringUtils.isNotBlank(token)) {
				//TODO 此处的token参数可重构为 通过http header发送cookie到cas而不是请求参数
				//通过cookie到达cas，可以进行正常的controller层验证，而不是在cas层提供额外的AuthenticationFilter验证
				String responseStr = HttpRequest.sendPost(CAS_VALIDATE_TOKEN_URL, TICKET_KEY + "=" + token);
				if (StringUtils.isNotBlank(responseStr)) {
					User user = JsonUtils.toBean(responseStr, User.class);
					request.getSession().setAttribute("user", user);
					UrlBuilder builder = UrlBuilder.parse(url);
					builder.removeParameter(TICKET_KEY);
					response.sendRedirect(builder.toString());
					return;
				}
			}
			//redirect to cas login
			UrlBuilder builder = UrlBuilder.parse(CAS_LOGIN_URL);
			builder.addParameter(RETURN_URL_KEY, URLEncoder.encode(url, URL_ENCODING_CHARSET));
			response.sendRedirect(builder.toString());
		}
		else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

}
