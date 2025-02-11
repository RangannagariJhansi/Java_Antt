package wizard.common.cards;

import wizard.common.game.Color;

public class JesterCard extends Card {

    private static final long serialVersionUID = 1L;

    /**
     * Create new jester card.
     */
    public JesterCard() {
        super(0, Color.CLEAR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String toSimpleString() {
        return "J";
    }
}
