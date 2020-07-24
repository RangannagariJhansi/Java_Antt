package wizard.common.cards;

import java.util.List;

public abstract class Cards {

    /**
     * Returns a string representation of an array of cards.
     * String will fit on one line and will not end with newline character.
     *
     * @param cards Cards to return a representation of
     * @return String representation of given cards
     */
    public static String toString(final Card[] cards) {
        if (cards == null) {
            return "[ ]";
        }

        String str = "[ ";
        for (int i = 0; i < cards.length; i++) {
            str += cards[i];

            if (i < cards.length - 1) {
                str += ", ";
            }
        }
        str += " ]";

        return str;
    }

    /**
     * Returns a string representation of an array of cards.
     * String will fit on one line and will not end with newline character.
     *
     * @param cards Cards to return a representation of
     * @return String representation of given cards
     */
    public static String toString(final List<Card> cards) {
        return toString(cards.toArray(new Card[cards.size()]));
    }

}
