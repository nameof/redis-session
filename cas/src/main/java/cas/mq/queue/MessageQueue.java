package cas.mq.queue;

import cas.mq.message.Message;

public abstract class MessageQueue {
	
	public abstract void push(Message message);
	
	public abstract Message pop();
}
