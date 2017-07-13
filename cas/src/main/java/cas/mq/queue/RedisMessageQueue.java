package cas.mq.queue;

import cas.mq.message.Message;
import cas.utils.JsonUtils;
import cas.utils.RedisUtil;

/**
 * 基于Redis的list数据结构实现的消息队列
 * 
 * @author ChengPan
 */
public class RedisMessageQueue extends MessageQueue {

	private String queueName;
	
	public RedisMessageQueue(String queueName) {
		this.queueName = queueName;
	}
	
	@Override
	public void push(Message message) {
		RedisUtil.getJedis().lpush(queueName, JsonUtils.toJSONString(message));
	}

	@Override
	public Message pop() {
		return JsonUtils.toBean(RedisUtil.getJedis().rpop(queueName), Message.class);
	}
}
