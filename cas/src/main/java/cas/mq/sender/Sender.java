package cas.mq.sender;

import cas.mq.message.Message;

public abstract class Sender {
	
	public abstract void sendMessage(Message message);
}
