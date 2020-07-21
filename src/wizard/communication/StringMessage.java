package wizard.communication;

public class StringMessage extends Message {

	private static final long serialVersionUID = 2882718774936302073L;

	public StringMessage(MessageType type, String content) {
		super(type, content);
	}
	
	public String getContent() {
		return (String)super.getContent();
	}
}
