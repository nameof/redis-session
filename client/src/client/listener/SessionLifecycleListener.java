package client.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import client.cas.component.LogedSessionManager;

/**
 * 监听session的过期或销毁，从{@link client.cas.component.LogedSessionManager}中移除session
 * 
 * @author ChengPan
 */
public class SessionLifecycleListener implements HttpSessionListener{

	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		String token = (String) session.getAttribute("token");
		if (token != null) {
			LogedSessionManager.detach(token.toString());
		}
	}

}
