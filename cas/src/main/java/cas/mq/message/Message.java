package cas.mq.message;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息实体，存放消息数据
 * 
 * @author ChengPan
 */
public class Message implements Serializable{

	private static final long serialVersionUID = 8073211549142445560L;
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private String content;
	
	public Message() {}

	public Message(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
