package wizard.common.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import wizard.common.cards.Card;
import wizard.common.cards.JesterCard;
import wizard.common.cards.NumberCard;
import wizard.common.cards.WizardCard;

public class Deck {
    private final List<Card> deck;

    public Deck() {
        deck = new ArrayList<Card>();

        // Add normal cards
        for (int i = 1; i < 14; i++) {
            deck.add(new NumberCard(i,  Color.BLUE));
            deck.add(new NumberCard(i,  Color.GREEN));
            deck.add(new NumberCard(i,  Color.RED));
            deck.add(new NumberCard(i,  Color.YELLOW));
        }

        // Add special cards
        for (int i = 0; i < 4; i++) {
            deck.add(new WizardCard());
            deck.add(new JesterCard());
        }
    }

    public Card takeRandom() {
        if (deck.size() < 1) {
            // TODO: Error
            System.err.println("Error! Could not take a card from deck!");
            return null;
        }

        int n = new Random().nextInt(deck.size());
        return deck.remove(n);
    }
}
