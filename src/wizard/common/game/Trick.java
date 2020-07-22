package wizard.common.game;

import java.util.ArrayList;
import java.util.List;

import wizard.common.cards.Card;

public class Trick {

    private final Color trump;
    private List<Card> cards;

    public Trick(Color trump) {
        this.trump = trump;
        cards = new ArrayList<Card>();
    }

    public String toString() {
        String str = "Trick: ";

        for (int i = 0; i < cards.size(); i++) {
            str += cards.get(i);

            if (i < cards.size() - 1) {
                str += " -> ";
            }
        }

        return str;
    }

    public void add(Card card) {
        cards.add(card);
    }

    // TODO: Remove !
    public int cardId(Card card) {
        return cards.indexOf(card);
    }

    public Color firstColor() {
        // Return first color in trick
        return cards.stream()
                    .filter(x -> x.getColor() != Color.CLEAR)
                    .findFirst()
                    .map(x -> x.getColor())
                    .orElse(Color.CLEAR);
    }

    public Card takenBy() {
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
                      .max((a, b) -> { return a.compareTo(b); })
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
                    .max((a, b) -> { return a.compareTo(b); })
                    .orElse(null);
    }

}
