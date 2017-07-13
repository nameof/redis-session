package cas.mq.sender;

import cas.mq.message.Message;
import cas.mq.queue.RedisMessageQueue;

/**
 * 注销消息发送者
 * 
 * @author ChengPan
 */
public class LogoutMessageSender extends Sender{
	
	private static final String LOGOUT_QUEUE_NAME = "logoutQueue";

	private static RedisMessageQueue queue = new RedisMessageQueue(LOGOUT_QUEUE_NAME);
	
	@Override
	public void sendMessage(Message message) {
		queue.push(message);
	}

}
