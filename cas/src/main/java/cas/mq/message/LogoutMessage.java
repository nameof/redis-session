package cas.mq.message;

import java.util.List;

import cas.utils.JsonUtils;

/**
 * 注销消息
 * @author ChengPan
 */
public class LogoutMessage extends Message{

	private static final long serialVersionUID = 1923709448488814904L;
	
	private InnerMessage logoutMessage;
	
	public LogoutMessage() {}
	
	public LogoutMessage(Message message) {
		this(message.getContent());
	}
	
	public LogoutMessage(String content) {
		super(content);
		logoutMessage = JsonUtils.toBean(content, InnerMessage.class);
	}
	
	public LogoutMessage(String token, List<String> logoutUrls) {
		logoutMessage = new InnerMessage(token, logoutUrls);
		setContent(JsonUtils.toJSONString(logoutMessage));
	}
	
	public String getToken() {
		return logoutMessage.getToken();
	}

	public List<String> getLogoutUrls() {
		return logoutMessage.getLogoutUrls();
	}
	
	public static class InnerMessage {

		private String token;
		
		private List<String> logoutUrls;
		
		public InnerMessage() {}
		
		public InnerMessage(String token, List<String> logoutUrls) {
			setToken(token);
			setLogoutUrls(logoutUrls);
		}
		
		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public List<String> getLogoutUrls() {
			return logoutUrls;
		}

		public void setLogoutUrls(List<String> logoutUrls) {
			this.logoutUrls = logoutUrls;
		}
	}
}
