package cas.mq.message;

import java.io.Serializable;

public class Message implements Serializable{

	private static final long serialVersionUID = 8073211549142445560L;
	
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
