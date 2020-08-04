package wizard.common.messages;

import java.io.Serializable;

public abstract class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private MessageType type;
    private Object content;

    /**
     * Create a new {@code Message} with given type and content.
     *
     * @param type The type of this new message
     * @param content The content of this new message
     */
    public Message(final MessageType type, final Object content) {
        this.type = type;
        this.content = content;
    }

    /**
     * Returns the type of this message.
     *
     * @return The type of this message
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Returns the content of this message.
     *
     * @return The content of this message
     */
    public Object getContent() {
        return content;
    }
}
