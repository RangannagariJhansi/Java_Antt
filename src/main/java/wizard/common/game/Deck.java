package wizard.common.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import wizard.common.cards.Card;
import wizard.common.cards.Cards;
import wizard.common.cards.JesterCard;
import wizard.common.cards.NumberCard;
import wizard.common.cards.WizardCard;

/**
 * {@code Deck} object representing a deck of cards.
 */
public class Deck extends Cards {

    private final List<Card> cards;

    /**
     * Create a new {@code Deck} with all cards.
     */
    public Deck() {
        cards = new ArrayList<Card>();

        // Add normal cards
        for (int i = 1; i < 14; i++) {
            cards.add(new NumberCard(i,  Color.BLUE));
            cards.add(new NumberCard(i,  Color.GREEN));
            cards.add(new NumberCard(i,  Color.RED));
            cards.add(new NumberCard(i,  Color.YELLOW));
        }

        // Add special cards
        for (int i = 0; i < 4; i++) {
            cards.add(new WizardCard());
            cards.add(new JesterCard());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Card> asList() {
        return cards;
    }

    /**
     * Removes a random card from the deck and returns it.
     *
     * @return Random card
     */
    public Card takeRandom() {
        if (cards.size() < 1) {
            // TODO: Error
            System.err.println("Error! Could not take a card from deck!");
            return null;
        }

        int n = new Random().nextInt(cards.size());
        return cards.remove(n);
    }

    @Override
    public String toString() {
        return String.format("Deck, %d cards", cards.size());
    }
}
