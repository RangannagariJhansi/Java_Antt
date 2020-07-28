package wizard.common.game;

import java.util.ArrayList;
import java.util.List;

import wizard.common.cards.Card;
import wizard.common.cards.Cards;

/**
 * {@code Trick} object representing a trick consisting of multiple cards
 * played by different players.
 */
public class Trick extends Cards {

    private final List<Card> cards;

    /**
     * Create a new {@code Trick}.
     */
    public Trick() {
        this.cards = new ArrayList<Card>();
    }

    /**
     * Create a new {@code Trick} containing the given cards.
     *
     * @param cards The cards making up this {@code Trick}
     */
    public Trick(final List<Card> cards) {
        this.cards = cards;
    }

    /**
     * Returns a string representation of this trick.
     * String will fit on one line and will not end with newline character.
     *
     * @return String representation of this trick
     */
    @Override
    public String toString() {
        return toStringHelper(" -> ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Card> asList() {
        return cards;
    }

    /**
     * Adds a new card to this trick.
     *
     * @param card The card to add to this trick
     */
    public void add(final Card card) {
        cards.add(card);
    }

    // TODO: Remove !
    public int cardId(final Card card) {
        return cards.indexOf(card);
    }

    /**
     * Returns the first color of this trick.
     *
     * @return The first color of this trick.
     */
    public Color firstColor() {
        // Return first color in trick
        return cards.stream()
                    .filter(x -> x.getColor() != Color.CLEAR)
                    .findFirst()
                    .map(x -> x.getColor())
                    .orElse(Color.CLEAR);
    }

    /**
     * Returns which card takes this trick.
     *
     * @param trump The trump color for this trick
     * @return The winner card which takes this trick
     */
    public Card takenBy(Color trump) {
        // Trick without cards cannot be taken by anyone
        if (cards.size() == 0) {
            return null;
        }

        Card winner = null;


        // The trick gets taken by the first player to play a wizard
        winner = cards.stream()
                      .filter(x -> x.isWizard())
                      .findFirst()
                      .orElse(null);

        if (winner != null) {
            return winner;
        }


        // The trick gets taken by the highest trump-colored card
        winner = cards.stream()
                      .filter(x -> x.getColor() == trump)
                      .max((a, b) -> {
                          return a.compareTo(b);
                      })
                      .orElse(null);

        if (winner != null) {
            return winner;
        }


        Color firstColor = firstColor();


        // If there is never any color (all players play jesters) the trick is taken by the
        // last player to play a jester.
        if (firstColor == Color.CLEAR) {
            return cards.get(cards.size() - 1);
        }

        // The trick gets taken by the highest card colored the same as the first color.
        return cards.stream()
                    .filter(x -> x.getColor() == firstColor)
                    .max((a, b) -> {
                        return a.compareTo(b);
                    })
                    .orElse(null);
    }

}
