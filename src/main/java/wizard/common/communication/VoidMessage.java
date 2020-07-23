package wizard.common.communication;

public class VoidMessage extends Message {

    private static final long serialVersionUID = 1L;

    public VoidMessage(MessageType type) {
        super(type, null);
    }
}
