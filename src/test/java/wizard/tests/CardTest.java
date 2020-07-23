package wizard.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import wizard.common.cards.Card;
import wizard.common.cards.JesterCard;
import wizard.common.cards.NumberCard;
import wizard.common.cards.WizardCard;
import wizard.common.game.Color;

class CardTest {

    @Test
    void testEquals() {
        // Equal (self)
        Card c1 = new NumberCard(10, Color.RED);
        assertTrue(c1.equals(c1));

        // Equal
        Card c2 = new NumberCard(10, Color.RED);
        assertTrue(c1.equals(c2));

        // Normal not equal
        Card c3 = new NumberCard(10, Color.RED);
        Card c4 = new NumberCard(12, Color.RED);
        Card c5 = new NumberCard(12, Color.BLUE);
        assertFalse(c3.equals(c4));
        assertFalse(c4.equals(c5));

        // Not equal with null
        assertFalse(c5.equals(null));

        // Not equal with object of different type
        assertFalse(c5.equals(new Object()));
    }

    @Test
    void testIsWizard() {
        // Number cards are no wizards
        Card c1 = new NumberCard(1, Color.BLUE);
        Card c2 = new NumberCard(2, Color.GREEN);
        Card c3 = new NumberCard(3, Color.RED);
        Card c4 = new NumberCard(4, Color.YELLOW);

        assertFalse(c1.isWizard());
        assertFalse(c2.isWizard());
        assertFalse(c3.isWizard());
        assertFalse(c4.isWizard());

        // A jester is not a wizard
        Card c5 = new JesterCard();
        assertFalse(c5.isWizard());

        // Wizard is wizard
        Card c6 = new WizardCard();
        assertTrue(c6.isWizard());
    }

}
