package wizard.common.communication;

import wizard.common.game.Color;

public class ColorMessage extends Message {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new {@code ColorMessage} with given type and content.
     *
     * @param type The type of this new message
     * @param content The content of this new message
     */
    public ColorMessage(final MessageType type, final Color content) {
        super(type, content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Color getContent() {
        return (Color)super.getContent();
    }
}
