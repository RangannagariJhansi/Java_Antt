package wizard.common.messages;

public class IntMessage extends Message {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new {@code IntMessage} with given type and content.
     *
     * @param type The type of this new message
     * @param content The content of this new message
     */
    public IntMessage(final MessageType type, int content) {
        super(type, content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getContent() {
        return (Integer)super.getContent();
    }
}
