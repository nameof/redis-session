package client.cas.component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpSession;

/**
 * 持有已登录的会话session,以便于在session过期，或{@link client.filter.LogoutFilter}收到注销请求时
 * 根据session中存储的token，或注销请求传递过来的token，将集合中的session取出，进行销毁
 * 
 * @author ChengPan
 */
public class LogedSessionManager {
	
	private LogedSessionManager() {}
	
	private static final Map<String, HttpSession> logedSessions = new ConcurrentHashMap<>();

	public static void attach(String token, HttpSession session) {
		logedSessions.put(token, session);
	}
	
	public static HttpSession detach(String token) {
		return logedSessions.remove(token);
	}
	
	public static HttpSession get(String token) {
		if (token == null)
			return null;
		return logedSessions.get(token);
	}
}
