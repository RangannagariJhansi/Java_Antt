package wizard.common.communication;

public class StringMessage extends Message {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new {@code StringMessage} with given type and content.
     *
     * @param type The type of this new message
     * @param content The content of this new message
     */
    public StringMessage(final MessageType type, final String content) {
        super(type, content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContent() {
        return (String)super.getContent();
    }
}
