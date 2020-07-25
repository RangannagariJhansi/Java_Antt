package wizard.common.cards;

import wizard.common.game.Color;

public class NumberCard extends Card {

    private static final long serialVersionUID = 1L;

    /**
     * Create new number card of given value and color.
     *
     * @param value The value of the created card
     * @param color The color of the created card
     */
    public NumberCard(int value, final Color color) {
        super(value, color);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String toSimpleString() {
        switch (color) {
            case BLUE:
                return "B " +value;
            case GREEN:
                return "G " +value;
            case RED:
                return "R " +value;
            case YELLOW:
                return "Y " +value;
            default:
            case CLEAR:
                // Should never happen
                return "? " +value;
        }
    }
}
