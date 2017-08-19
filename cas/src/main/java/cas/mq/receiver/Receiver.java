package cas.mq.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cas.mq.message.Message;

/**
 * 消息接收者
 * @author ChengPan
 */
public abstract class Receiver {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	public abstract void handleMessage(Message message);
}
