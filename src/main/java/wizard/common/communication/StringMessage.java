package wizard.common.communication;

public class StringMessage extends Message {

    private static final long serialVersionUID = 1L;

    public StringMessage(final MessageType type, final String content) {
        super(type, content);
    }

    @Override
    public String getContent() {
        return (String)super.getContent();
    }
}
