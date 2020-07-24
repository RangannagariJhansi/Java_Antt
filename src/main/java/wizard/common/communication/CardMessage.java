package wizard.common.communication;

import wizard.common.cards.Card;

public class CardMessage extends Message {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new @{code CardMessage} with given type and content.
     *
     * @param type The type of this new message
     * @param content The content of this new message
     */
    public CardMessage(final MessageType type, final Card content) {
        super(type, content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Card getContent() {
        return (Card)super.getContent();
    }
}
