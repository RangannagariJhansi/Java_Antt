package wizard.common.game;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import wizard.common.cards.Card;
import wizard.common.cards.Cards;

/**
 * {@code Hand} object represents the current cards on the hand of a player.
 */
public class Hand extends Cards {

    private final List<Card> cards;

    /**
     * Creates new {@code Hand} object containing no cards.
     */
    public Hand() {
        this.cards = new ArrayList<Card>();
    }

    /**
     * Creates new {@code Hand} object containing the given cards.
     *
     * @param cards The cards on this hand
     */
    public Hand(final List<Card> cards) {
        this.cards = cards;
        sort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toStringHelper(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Card> asList() {
        return cards;
    }

    /**
     * Sort hand cards by color and then by value.
     */
    private void sort() {
        // Sort cards by color
        BiFunction<Card, Card, Integer> colorComparison = (x, y) -> {
            return x.getColor().compareTo(y.getColor());
        };

        // Sort cards by value
        BiFunction<Card, Card, Integer> valueComparison = (x, y) -> {
            return x.getValue() - y.getValue();
        };

        // Sort first by color, then by value
        cards.sort((a, b) -> {
            int v = colorComparison.apply(a, b);

            if (v != 0) {
                return v;
            }

            return valueComparison.apply(a, b);
        });
    }

    /**
     * Returns the number of cards on this hand.
     *
     * @return The number of cards on this hand
     */
    public int count() {
        return cards.size();
    }

    /**
     * Checks if a given {@code Card} is on this hand.
     *
     * @param card The card to check if it is on this hand
     * @return True if the given card is on this hand, false otherwise
     */
    public boolean contains(final Card card) {
        return cards.contains(card);
    }

    /**
     * Checks if there is at least one card with the given color on this hand.
     *
     * @param color The color to check for
     * @return True if there is at least one card on this hand that matches
     *         the given color.
     */
    public boolean containsColor(final Color color) {
        return cards.parallelStream().anyMatch(x -> x.getColor() == color);
    }

    /**
     * Removes a given card from this hand.
     *
     * @param card The card to remove from this hand
     */
    public void remove(final Card card) {
        cards.remove(card);
    }
}
