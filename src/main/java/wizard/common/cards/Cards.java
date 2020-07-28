package wizard.common.cards;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        return Cards.toStringHelper(this.cards, delimiter);
    }

    /**
     * Returns a string representation of a list of cards.
     * String will fit on one line and will not end with newline character.
     *
     * @param cards Cards to return a representation of
     * @return String representation of given cards
     */
    public static String toString(final List<Card> cards) {
        return Cards.toStringHelper(cards, null);
    }

    public static String toString(final Cards cards) {
        return Cards.toStringHelper(cards.asList(), null);
    }

    protected static String toStringHelper(final List<Card> cards, String delimiter) {
        if (cards == null || cards.size() == 0) {
            return "[  ]";
        }

        if (delimiter == null) {
            delimiter = ", ";
        }

        String str = cards.stream()
                .map(Card::toString)
                .collect(Collectors.joining(delimiter));

        return "[ " +str +" ]";
    }

}
