package wizard.common.communication;

public class IntMessage extends Message {

    private static final long serialVersionUID = 1L;

    public IntMessage(final MessageType type, int content) {
        super(type, content);
    }

    @Override
    public Integer getContent() {
        return (Integer)super.getContent();
    }
}
