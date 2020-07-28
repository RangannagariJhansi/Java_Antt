package wizard.common.cards;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code Cards} is an abstraction for multiple cards that may or may not
 * be ordered.
 */
public abstract class Cards {

    protected final List<Card> cards;

    public Cards() {
        this.cards = new ArrayList<Card>();
    }

    @Override
    public String toString() {
        return toStringHelper(null);
    }

    protected String toStringHelper(String delimiter) {
        if (delimiter == null) {
            delimiter = ", ";
        }

        String str = "";

        for (int i = 0; i < cards.size(); i++) {
            str += cards.get(i);

            if (i < cards.size() - 1) {
                str += delimiter;
            }
        }

        return str;
    }

    /**
     * Returns a string representation of a list of cards.
     * String will fit on one line and will not end with newline character.
     *
     * @param cards Cards to return a representation of
     * @return String representation of given cards
     */
    public static String toString(final List<Card> cards) {
        if (cards == null) {
            return "[  ]";
        }

        String str = "[ ";
        for (int i = 0; i < cards.size(); i++) {
            str += cards.get(i);

            if (i < cards.size() - 1) {
                str += ", ";
            }
        }
        str += " ]";

        return str;
    }

}
