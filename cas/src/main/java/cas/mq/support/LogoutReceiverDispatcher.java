package cas.mq.support;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cas.mq.message.Message;
import cas.mq.queue.MessageQueue;
import cas.mq.queue.RedisMessageQueue;
import cas.mq.receiver.LogoutMessageReceiver;
import cas.mq.receiver.Receiver;
import cas.utils.RedisUtil;

/**
 * 注销消息调度器，开启一个{@link cas.mq.support.LogoutReceiverDispatcher.LogoutMessageHandler}线程
 * 处理注销消息
 * 
 * @author ChengPan
 */
//FIXME 此处存在过度设计嫌疑
public class LogoutReceiverDispatcher{
	
	/** 处理注销消息的线程实例 */
	private static volatile LogoutMessageHandler thread;
	
	private static final Logger logger = LoggerFactory.getLogger(LogoutReceiverDispatcher.class);
	
	private LogoutReceiverDispatcher() {}

	public static void dispatch() {
		if (thread == null) {
			synchronized (LogoutReceiverDispatcher.class) {
				if (thread == null) {
					thread = new LogoutMessageHandler();
					thread.start();
				}
			}
		}
	}
	
	public static synchronized void stop() {
		thread.setHandleMsg(false);
		thread = null;
	}
	
	/**
	 * 默认情况下{@link cas.mq.support.LogoutReceiverDispatcher.LogoutMessageHandler}会持续
	 * 从缓存中获取注销消息，并存入msgBuffer缓冲队列。同时开启{@link cas.mq.support.LogoutReceiverDispatcher.LogoutMessageHandler#INIT_POOL_SIZE}
	 * 个线程对缓存队列中的注销消息进行处理。
	 * @author ChengPan
	 */
	private static class LogoutMessageHandler extends Thread{

		/** 处理注销消息的线程数量 */
		private static final int INIT_POOL_SIZE = 2;
		
		private ExecutorService executor = Executors.newFixedThreadPool(INIT_POOL_SIZE);
		
		/** 线程运行标志 */
		private volatile boolean handleMsg = true;

		/** redis远程队列名 */
		private static final String LOGOUT_QUEUE_NAME = "logoutQueue";
		
		/** redis远程队列 */
		private static final MessageQueue queue = new RedisMessageQueue(LOGOUT_QUEUE_NAME);
		
		/** 消息缓冲队列 */
		private static final BlockingQueue<Message> msgBuffer = new LinkedBlockingQueue<>();
		
		private static final Logger logger = LoggerFactory.getLogger(LogoutMessageHandler.class);
		
		@Override
		public void run() {
			
			logger.debug("handle message thread start");
			
			for(int i = 0; i < INIT_POOL_SIZE; i++) {
				executor.execute(new DispatchRuner(msgBuffer));
			}
			
			while (handleMsg) {
				try {
					Message message = queue.pop();
					if (message != null) {
						msgBuffer.put(message);
					}
				} catch (InterruptedException e) {
					logger.error("注销消息获取异常", e);
				}
			}
			
			//释放Jedis资源
			RedisUtil.returnResource();
			
			//TODO 可以使用信号量机制，使msgBuffer中的消息被处理完之后，再执行shutdown调用
			executor.shutdown();
			
			logger.debug("handle message thread quit");
		}

		public boolean isHandleMsg() {
			return this.handleMsg;
		}

		/**
		 * 如果设置线程标志为false，则消息线程将退出
		 * @param handleMsg 线程运行标志
		 */
		public void setHandleMsg(boolean handleMsg) {
			this.handleMsg = handleMsg;
		}
		
	}

	/**
	 * {@link cas.mq.support.LogoutReceiverDispatcher.DispatchRuner} 的任务是从缓冲队列中获取注销消息
	 * 并使用{@link cas.mq.receiver.LogoutMessageReceiver}实例对消息进行处理
	 * @author ChengPan
	 */
	private static class DispatchRuner implements Runnable{

		/** 消息缓冲队列 */
		private BlockingQueue<Message> msgBuffer;
		
		/** 注销消息接收者 */
		private Receiver logoutMessageReceiver = new LogoutMessageReceiver();
		
		private static final Logger logger = LoggerFactory.getLogger(DispatchRuner.class);

		public DispatchRuner(BlockingQueue<Message> msgBuffer) {
			this.msgBuffer = msgBuffer;
		}
		
		@Override
		public void run() {
			logger.debug("线程 {} 开始处理消息", Thread.currentThread().getId());
			while (true) {
				//处理异常，健壮执行
				try {
					Message msg = msgBuffer.take();
					logoutMessageReceiver.handleMessage(msg);
				} catch (InterruptedException e) {
					logger.error("注销消息处理异常", e);
				}
			}
		}
		
	}
	
}
