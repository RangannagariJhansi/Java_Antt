package wizard.common.cards;

import wizard.common.game.Color;

public class JesterCard extends Card {

    private static final long serialVersionUID = 1L;

    public JesterCard() {
        this.value = 0;
        this.color = Color.CLEAR;
    }

    @Override
    public String toString() {
        return "[J   ]";
    }
}
