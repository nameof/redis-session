package cas.mq.receiver;

import cas.mq.message.LogoutMessage;
import cas.mq.message.Message;
import cas.mq.queue.RedisMessageQueue;
import cas.utils.HttpRequest;

/**
 * 注销消息接收者
 * 
 * @author ChengPan
 */
public class LogoutMessageReceiver extends Receiver{

	private static final String LOGOUT_QUEUE_NAME = "logoutQueue";
	
	private static RedisMessageQueue queue = new RedisMessageQueue(LOGOUT_QUEUE_NAME);
	
	@Override
	public void handleMessage() {
		Message message = queue.pop();
		if (message != null) {
			LogoutMessage logoutMsg = new LogoutMessage(message);
			if (logoutMsg != null && logoutMsg.getLogoutUrls() != null) {
				for (String logoutUrl : logoutMsg.getLogoutUrls()) {
					HttpRequest.sendPost(logoutUrl, "token=" + logoutMsg.getToken(), null);
				}
			}
		}
	}

}
