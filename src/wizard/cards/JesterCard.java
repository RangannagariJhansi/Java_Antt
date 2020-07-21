package wizard.cards;

import wizard.game.Color;

public class JesterCard extends Card {

	private static final long serialVersionUID = 82397007714002062L;

	public JesterCard() {
        this.value = 0;
        this.color = Color.CLEAR;
	}
	
	public String toString() {
		return "[J   ]";
	}
}
