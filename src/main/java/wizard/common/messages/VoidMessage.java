package wizard.common.messages;

public class VoidMessage extends Message {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new {@code VoidMessage} with given type.
     *
     * @param type The type of this new message
     */
    public VoidMessage(final MessageType type) {
        super(type, null);
    }
}
