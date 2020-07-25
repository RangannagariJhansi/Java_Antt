package wizard.common.cards;

import java.io.Serializable;

import wizard.common.game.Color;

public abstract class Card implements Serializable, Comparable<Card> {

    private static final long serialVersionUID = 1L;

    protected int value;
    protected Color color;

    /**
     * Create new card of given value and color.
     *
     * @param value The value of the created card
     * @param color The color of the created card
     */
    public Card(int value, Color color) {
        this.value = value;
        this.color = color;
    }

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
     * Returns a pretty string representation of this card.
     * String will be exactly 6 characters wide. The first will be '[' and the
     * last will be ']'.
     *
     * @return String representation of this card
     */
    @Override
    public String toString() {
        String str = toSimpleString();

        if (str.length() > 4) {
            return "[????]";
        }

        // Pad to exactly length 4
        while (str.length() < 4) {
            str += " ";
        }

        return "[" +str +"]";
    }

    /**
     * Returns a string representation of this card.
     *
     * @return String representation of this card
     */
    protected abstract String toSimpleString();

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
