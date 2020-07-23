package wizard.common.communication;

public class StringMessage extends Message {

    private static final long serialVersionUID = 1L;

    public StringMessage(MessageType type, String content) {
        super(type, content);
    }

    public String getContent() {
        return (String)super.getContent();
    }
}
