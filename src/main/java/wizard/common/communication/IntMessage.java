package wizard.common.communication;

public class IntMessage extends Message {

    private static final long serialVersionUID = 1L;

    public IntMessage(MessageType type, Integer content) {
        super(type, content);
    }

    public Integer getContent() {
        return (Integer)super.getContent();
    }
}
