package cas.mq.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cas.mq.receiver.LogoutMessageReceiver;
import cas.mq.receiver.Receiver;
import cas.utils.RedisUtil;

/**
 * 注销消息调度器，默认情况下开启一个线程，使用{@link cas.mq.receiver.LogoutMessageReceiver}实例对消息队列进行
 * 轮询处理
 * 
 * @author ChengPan
 */
public class LogoutReceiverDispatcher implements Runnable{
	
	private Receiver logoutMessageReceiver = new LogoutMessageReceiver();
	
	private static volatile Thread thread;
	
	private static volatile boolean handleMsg = true;
	
	private static final Logger logger = LoggerFactory.getLogger(LogoutReceiverDispatcher.class);
	
	private LogoutReceiverDispatcher() {}
	
	@Override
	public void run() {
		
		logger.debug("handle message thread start");
		
		while (handleMsg) {
			//处理异常，健壮执行
			try {
				logoutMessageReceiver.handleMessage();
			}
			catch (Exception e) {
				logger.error("注销消息处理异常", e);
			}
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
