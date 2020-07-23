package wizard.tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import        org.junit.jupiter.api.Test;

import wizard.common.cards.Card;
import wizard.common.cards.JesterCard;
import wizard.common.cards.NumberCard;
import wizard.common.cards.WizardCard;
import wizard.common.game.Color;
import wizard.common.game.Trick;

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
                new NumberCard(12, Color.GREEN)
            };
            for (Card c : cards) {
                t.add(c);
            }
            assertTrue(t.takenBy() == cards[5]);
        }

        // Test if trick gets taken by last jester if all players play jesters
        {
            Trick t = new Trick(Color.BLUE);
            Card[] cards = {
                new JesterCard(),
                new JesterCard(),
                new JesterCard(),
            };
            for (Card c : cards) {
                t.add(c);
            }

            assertFalse(t.takenBy() == cards[0]);
            assertFalse(t.takenBy() == cards[1]);
            assertTrue(t.takenBy() == cards[2]);
        }
    }

}
