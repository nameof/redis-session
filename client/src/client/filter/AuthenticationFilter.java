package client.filter;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import client.cas.component.LogedSessionManager;
import client.model.User;
import client.utils.HttpRequest;
import client.utils.JsonUtils;
import client.utils.UrlBuilder;

/**
 * 这个过滤器的职责：
 *     登录过滤
 *     对cas的登录票据发送http请求验证
 * @author ChengPan
 */
public class AuthenticationFilter implements Filter{

	/** 全局登录站点地址 */
	private static String CAS_LOGIN_URL = "http://localhost:8080/cas/login";

	/** 全局会话注销地址 */
	private static String GLOBAL_LOGOUT_URL = "http://localhost:8080/cas/logout";
	
	/** 票据Filter方式的验证地址，二选一即可*/
	private static String CAS_VALIDATE_IN_FILTER = "http://localhost:8080/cas/authentication/validatetoken";
	
	/** 票据Controller验证地址，二选一即可 */
	private static String CAS_VALIDATE_IN_CONTROLLER = "http://localhost:8080/cas/validatetoken";
	
	/** 当前站点注销地址，提交到cas保存，在注销时cas向此地址发送消息注销客户端局部会话 */
	private static String CLIENT_LOGOUT_URL = "http://localhost:8080/client/logout";
	
	private static String URL_ENCODING_CHARSET = "UTF-8";
	
	/** 票据传递参数名 */
	private static  final String TICKET_KEY = "token";
	
	/** 返回地址参数名 */
	private static  final String RETURN_URL_KEY = "returnUrl";
	
	/** 注销地址参数名 */
	private static  final String LOGOUT_URL_KEY = "logoutUrl";
	
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
				String params = null;
				//①:http参数传递token，使用cas的AuthenticationFilter验证
				//params = TICKET_KEY + "=" + token;
				
				//②:cookie方式传递，可在CAS的controller层校验
				Map<String, String> cookies = new HashMap<>();
				cookies.put(TICKET_KEY, token);
				
				String responseStr = HttpRequest.sendPost(CAS_VALIDATE_IN_CONTROLLER, params, cookies);
				
				if (StringUtils.isNotBlank(responseStr)) {
					User user = JsonUtils.toBean(responseStr, User.class);
					request.getSession().setAttribute("user", user);
					//存储有效票据到session，以备注销
					request.getSession().setAttribute("token", token);
					//存储全局注销地址，以便页面输出，注销
					request.getSession().setAttribute("GLOBAL_LOGOUT_URL", GLOBAL_LOGOUT_URL);
					
					//添加到已登录session管理器
					LogedSessionManager.attach(token, request.getSession());
					
					UrlBuilder builder = UrlBuilder.parse(url);
					builder.removeParameter(TICKET_KEY);
					response.sendRedirect(builder.toString());
					return;
				}
			}
			//redirect to cas login
			UrlBuilder builder = UrlBuilder.parse(CAS_LOGIN_URL);
			builder.addParameter(RETURN_URL_KEY, URLEncoder.encode(url, URL_ENCODING_CHARSET));
			builder.addParameter(LOGOUT_URL_KEY, URLEncoder.encode(CLIENT_LOGOUT_URL, URL_ENCODING_CHARSET));
			response.sendRedirect(builder.toString());
		}
		else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		String cas_login_url = config.getInitParameter("CAS_LOGIN_URL");
		if (StringUtils.isNotBlank(cas_login_url)) {
			CAS_LOGIN_URL = cas_login_url;
		}
		
		String cas_validate_in_controller = config.getInitParameter("CAS_VALIDATE_IN_CONTROLLER");
		if (StringUtils.isNotBlank(cas_validate_in_controller)) {
			CAS_VALIDATE_IN_CONTROLLER = cas_validate_in_controller;
		}
		
		String cas_validate_in_filter = config.getInitParameter("CAS_VALIDATE_IN_FILTER");
		if (StringUtils.isNotBlank(cas_validate_in_filter)) {
			CAS_VALIDATE_IN_FILTER = cas_validate_in_filter;
		}
		
		String client_logout_url = config.getInitParameter("CLIENT_LOGOUT_URL");
		if (StringUtils.isNotBlank(cas_validate_in_filter)) {
			CLIENT_LOGOUT_URL = client_logout_url;
		}
		
		String global_logout_url = config.getInitParameter("GLOBAL_LOGOUT_URL");
		if (StringUtils.isNotBlank(cas_validate_in_filter)) {
			GLOBAL_LOGOUT_URL = global_logout_url;
		}
	}

}
