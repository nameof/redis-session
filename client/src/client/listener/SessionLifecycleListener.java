package client.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import client.cas.component.LogedSessionManager;
import client.model.User;

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
