package cas.mq.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cas.mq.receiver.LogoutMessageReceiver;
import cas.mq.receiver.Receiver;
import cas.utils.RedisUtil;

public class LogoutReceiverDispatcher implements Runnable{
	
	private Receiver messageReceiver = new LogoutMessageReceiver();
	
	private static volatile Thread thread;
	
	private static volatile boolean handleMsg = true;
	
	private static final Logger logger = LoggerFactory.getLogger(LogoutReceiverDispatcher.class);
	
	private LogoutReceiverDispatcher() {}
	
	@Override
	public void run() {
		
		logger.debug("handle message thread start");
		
		//TODO 处理异常，健壮执行
		while (handleMsg) {
			messageReceiver.handleMessage();
		}
		
		
		//释放Jedis资源
		RedisUtil.returnResource();
		
		logger.debug("handle message thread quit");
	}

	public static void dispatch() {
		if (thread == null) {
			synchronized (LogoutReceiverDispatcher.class) {
				if (thread == null) {
					handleMsg = true;
					thread = new Thread(new LogoutReceiverDispatcher());
					thread.start();
				}
			}
		}
	}
	
	public static synchronized void stop() {
		handleMsg = false;
		thread = null;
	}

}
