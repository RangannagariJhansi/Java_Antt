package wizard.common.messages;

import wizard.common.GameStatus;

public class GameStatusMessage extends Message {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new {@code IntMessage} with given type and content.
     *
     * @param type The type of this new message
     * @param content The content of this new message
     */
    public GameStatusMessage(final MessageType type, GameStatus content) {
        super(type, content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameStatus getContent() {
        return (GameStatus)super.getContent();
    }
}
