package wizard.communication;

import java.io.Serializable;

public abstract class Message implements Serializable {
	
	private static final long serialVersionUID = 2490381780822554144L;
	
	private MessageType type;
	private Object content;
	
	public Message(MessageType type, Object content) {
		this.type = type;
		this.content = content;
	}
	
	public MessageType getType() {
		return type;
	}
	
	public Object getContent() {
		return content;
	}
}
