package wizard.communication;

import wizard.cards.Card;

public class CardsMessage extends Message {

	private static final long serialVersionUID = 6944077247609575016L;

	public CardsMessage(MessageType type, Card[] content) {
		super(type, content);
	}
	
	public Card[] getContent() {
		return (Card[])super.getContent();
	}
}
