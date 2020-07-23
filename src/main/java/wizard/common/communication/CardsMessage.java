package wizard.common.communication;

import wizard.common.cards.Card;

public class CardsMessage extends Message {

    private static final long serialVersionUID = 1L;

    public CardsMessage(final MessageType type, final Card[] content) {
        super(type, content);
    }

    @Override
    public Card[] getContent() {
        return (Card[])super.getContent();
    }
}
