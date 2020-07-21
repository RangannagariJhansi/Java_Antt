package wizard.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import wizard.cards.Card;
import wizard.cards.JesterCard;
import wizard.cards.NumberCard;
import wizard.cards.WizardCard;

public class Deck {
	private Random rnd;
	
	private List<Card> deck;
	
	public Deck() {
		rnd = new Random();
		
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
			System.out.println("Error! Could not take a card from deck!");
			return null;
		}
		
		int n = rnd.nextInt(deck.size());
		return deck.remove(n);
	}
}
