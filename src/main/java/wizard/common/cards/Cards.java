package wizard.common.cards;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code Cards} is an abstraction for multiple cards that may or may not
 * be ordered.
 */
public abstract class Cards {

    /**
     * Return these cards as a {@code List<Card>}.
     *
     * @return these cards as a {@code List<Card>}
     */
    public abstract List<Card> asList();

    /**
     * Returns a string representation.
     * String will fit on one line and will not end with newline character.
     *
     * @return String representation of given cards
     */
    public abstract String toString();

    /**
     * Returns a string representation with given delimiter.
     * String will fit on one line and will not end with newline character.
     * Elements of {@code this} will be separated by given delimiter.
     *
     * @param delimiter String to put between elements
     * @return String representation of given cards
     */
    protected String toStringHelper(final String delimiter) {
        String delim = delimiter;
        if (delim == null) {
            delim = ", ";
        }

        List<Card> cards = asList();
        if (cards == null || cards.size() == 0) {
            return "[  ]";
        }

        String str = cards.stream()
                .map(Card::toString)
                .collect(Collectors.joining(delim));

        return "[ " +str +" ]";
    }
}
