package cas.mq.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cas.mq.message.Message;

/**
 * 消息队列
 * 
 * @author ChengPan
 */
public abstract class MessageQueue {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	public abstract void push(Message message);
	
	public abstract Message pop();
}
