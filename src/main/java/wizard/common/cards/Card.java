package wizard.common.cards;

import java.io.Serializable;

import wizard.common.game.Color;

public abstract class Card implements Serializable, Comparable<Card> {

    private static final long serialVersionUID = 1L;

    protected int value;
    protected Color color;

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

    @Override
    public int compareTo(Card other) {
        return getValue() - other.getValue();
    }

    public Color getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }

    public boolean isWizard() {
        return false;
    }
}
