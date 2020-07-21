package wizard.communication;

import wizard.cards.Card;

public class CardMessage extends Message {

	private static final long serialVersionUID = 3093097297969341680L;

	public CardMessage(MessageType type, Card content) {
		super(type, content);
	}
	
	public Card getContent() {
		return (Card)super.getContent();
	}
}
