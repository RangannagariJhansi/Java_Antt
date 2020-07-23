package wizard.common.cards;

import wizard.common.game.Color;

public class NumberCard extends Card {

    private static final long serialVersionUID = 1L;

    public NumberCard(final int value, final Color color) {
        this.value = value;
        this.color = color;
    }

    @Override
    public String toString() {
        // TODO: Make this prettier
        String v;
        if (value < 10) {
            v = +value + " ";
        } else {
            v = Integer.toString(value);
        }

        switch (color) {
        case BLUE:
            return "[B " +v +"]";
        case GREEN:
            return "[G " +v +"]";
        case RED:
            return "[R " +v +"]";
        case YELLOW:
            return "[Y " +v +"]";
        default:
            // Should never happen
            return "[  " +v +"]";
        }
    }
}
