package cas.mq.support;

import cas.mq.receiver.LogoutMessageReceiver;
import cas.mq.receiver.Receiver;
import cas.utils.RedisUtil;

public class LogoutReceiverDispatcher implements Runnable{
	
	private Receiver messageReceiver = new LogoutMessageReceiver();
	
	private static volatile Thread thread;
	
	private static volatile boolean handleMsg = true;
	
	private LogoutReceiverDispatcher() {}
	
	@Override
	public void run() {
		//TODO 处理异常，健壮执行
		while (handleMsg) {
			messageReceiver.handleMessage();
		}
		//释放Jedis资源
		RedisUtil.returnResource();
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
