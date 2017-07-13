package cas.custom.component.request;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import cas.custom.component.factory.CacheHttpSessionFactory;
import cas.utils.CookieUtil;

/**
 * HttpServletRequest的包装类，用于包装获取和创建HttpSession操作
 * 
 * @author ChengPan
 */
public class CustomHttpServletRequest extends HttpServletRequestWrapper {  
  
    private HttpSession session;
    private HttpServletResponse response;
    public static String COOKIE_SESSION_KEY = "token";
    public CustomHttpServletRequest(HttpServletRequest request, HttpServletResponse response) {  
        super(request);
        this.response = response;
    }  
  
    @Override
    public HttpSession getSession(boolean create) {  
        if (session != null) {  
            return session;
        }
        if (!create) {
        	return null;
        }
        String token = CookieUtil.getCookieValue(this, COOKIE_SESSION_KEY);
        if (StringUtils.isBlank(token)) {
        	token = UUID.randomUUID().toString();
        	CookieUtil.addCookie(response, COOKIE_SESSION_KEY, token);
        }
        session = CacheHttpSessionFactory.newSessionInstance(super.getSession(), token);  
        return session;
    }
    
    @Override
    public HttpSession getSession() {  
        return this.getSession(true);
    }
}  