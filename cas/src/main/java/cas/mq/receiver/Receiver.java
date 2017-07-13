package cas.mq.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息接收者
 * @author ChengPan
 */
public abstract class Receiver {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	public abstract void handleMessage();
}
