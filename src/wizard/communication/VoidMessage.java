package wizard.communication;

public class VoidMessage extends Message {

	private static final long serialVersionUID = 3528318596206798168L;

	public VoidMessage(MessageType type) {
		super(type, null);
	}
	
	public Object getContent() {
		return null;
	}
}
