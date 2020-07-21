package wizard.communication;

public class IntMessage extends Message {
	
	private static final long serialVersionUID = -1979498544138188427L;

	public IntMessage(MessageType type, Integer content) {
		super(type, content);
	}
	
	public Integer getContent() {
		return (Integer)super.getContent();
	}
}
