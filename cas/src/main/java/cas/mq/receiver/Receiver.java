package cas.mq.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Receiver {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	public abstract void handleMessage();
}
