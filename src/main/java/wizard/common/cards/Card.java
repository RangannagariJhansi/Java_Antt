package wizard.common.cards;

import java.io.Serializable;

import wizard.common.game.Color;

public abstract class Card implements Serializable, Comparable<Card> {

    private static final long serialVersionUID = 1L;

    protected int value;
    protected Color color;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }

        Card other = (Card)object;

        if (getColor() != other.getColor()) {
            return false;
        }
        if (getValue() != other.getValue()) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final Card other) {
        return getValue() - other.getValue();
    }

    /**
     * Returns the color of the card.
     *
     * @return The color of the card
     */
    public Color getColor() {
        return color;
    }

    /**
     * Returns the value of the card.
     *
     * @return The value of the card
     */
    public int getValue() {
        return value;
    }

    /**
     * Indicates whether the card is a wizard.
     *
     * @return True if the card is a wizard, false otherwise
     */
    public boolean isWizard() {
        return false;
    }
}
