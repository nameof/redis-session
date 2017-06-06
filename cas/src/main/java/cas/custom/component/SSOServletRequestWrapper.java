package cas.custom.component;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import cas.utils.CookieUtil;

public class SSOServletRequestWrapper extends HttpServletRequestWrapper {  
  
    private HttpSession session;  
    private HttpServletResponse response;  
    public SSOServletRequestWrapper(HttpServletRequest request, HttpServletResponse response) {  
        super(request);
        this.response = response;
    }  
  
    @Override  
    public HttpSession getSession(boolean create) {  
        if(session != null) {  
            return session;
        }
        String token = CookieUtil.getCookie(this, "token");
        if(StringUtils.isBlank(CookieUtil.getCookie(this, "token"))){
        	token = UUID.randomUUID().toString();
        	CookieUtil.addCookie(response, "token", token);
        }
        session = new RedisHttpSession(super.getSession(),token);  
        return session;
    }
  
    @Override  
    public HttpSession getSession() {  
        return getSession(false);
    }
}  