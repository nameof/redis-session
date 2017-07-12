package cas.mq.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cas.mq.message.Message;

public abstract class Sender {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	public abstract void sendMessage(Message message);
}
