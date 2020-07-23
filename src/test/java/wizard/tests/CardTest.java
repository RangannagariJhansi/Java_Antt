package wizard.tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import        org.junit.jupiter.api.Test;

import wizard.common.cards.Card;
import wizard.common.cards.JesterCard;
import wizard.common.cards.NumberCard;
import wizard.common.cards.WizardCard;
import wizard.common.game.Color;

class CardTest {

    @Test
    void testEquals() {
        // Equal (self)
        final Card c1 = new NumberCard(10, Color.RED);
        assertTrue(c1.equals(c1));

        // Equal
        final Card c2 = new NumberCard(10, Color.RED);
        assertTrue(c1.equals(c2));

        // Normal not equal
        final Card c3 = new NumberCard(10, Color.RED);
        final Card c4 = new NumberCard(12, Color.RED);
        final Card c5 = new NumberCard(12, Color.BLUE);
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
        final Card c1 = new NumberCard(1, Color.BLUE);
        final Card c2 = new NumberCard(2, Color.GREEN);
        final Card c3 = new NumberCard(3, Color.RED);
        final Card c4 = new NumberCard(4, Color.YELLOW);

        assertFalse(c1.isWizard());
        assertFalse(c2.isWizard());
        assertFalse(c3.isWizard());
        assertFalse(c4.isWizard());

        // A jester is not a wizard
        final Card c5 = new JesterCard();
        assertFalse(c5.isWizard());

        // Wizard is wizard
        final Card c6 = new WizardCard();
        assertTrue(c6.isWizard());
    }

}
