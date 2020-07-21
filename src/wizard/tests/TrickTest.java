package wizard.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import wizard.cards.Card;
import wizard.cards.JesterCard;
import wizard.cards.NumberCard;
import wizard.cards.WizardCard;
import wizard.game.Color;
import wizard.game.Trick;

class TrickTest {

	@Test
	void testTakenBy() {
		// Test if empty trick doesn't crash
		{
			Trick t = new Trick(Color.RED);
			assertTrue(t.takenBy() == null);
		}
		
		// Test if trick gets taken by wizard
		{
			Trick t = new Trick(Color.RED);
			Card[] cards = {
					new NumberCard(10, Color.BLUE),
					new NumberCard(13, Color.YELLOW),
					new WizardCard(),
					new NumberCard(15, Color.RED)
			};
			for (Card c : cards) {
				t.add(c);
			}
			assertTrue(t.takenBy() == cards[2]);
		}
		
		// Test if trick gets taken by highest trump color
		{
			Trick t = new Trick(Color.BLUE);
			Card[] cards = {
					new NumberCard(6, Color.RED),
					new NumberCard(7, Color.YELLOW),
					new JesterCard(),
					new NumberCard(2, Color.BLUE),
					new JesterCard(),
					new NumberCard(1, Color.BLUE),
					new NumberCard(8, Color.RED),
					new NumberCard(3, Color.BLUE),
					new NumberCard(9, Color.GREEN)
			};
			for (Card c : cards) {
				t.add(c);
			}
			assertTrue(t.takenBy() == cards[7]);
		}
		
		// Test if trick gets taken by highest correct colored card
		{
			Trick t = new Trick(Color.BLUE);
			Card[] cards = {
					new JesterCard(),
					new NumberCard(1, Color.RED),
					new NumberCard(5, Color.YELLOW),
					new JesterCard(),
					new NumberCard(7, Color.GREEN),
					new NumberCard(2, Color.RED),
					new NumberCard(10, Color.YELLOW),
					new NumberCard(1, Color.RED),
					new NumberCard(12, Color.GREEN),
			};
			for (Card c : cards) {
				t.add(c);
			}
			assertTrue(t.takenBy() == cards[5]);
		}
	}

}
