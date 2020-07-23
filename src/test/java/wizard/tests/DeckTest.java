package wizard.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import        org.junit.jupiter.api.Test;

import wizard.common.game.Deck;

class DeckTest {

    @Test
    void testtakeRandom() {
        Deck d = new Deck();
        for (int i = 0; i < 60; i++) {
            assertNotNull(d.takeRandom());
        }

        assertNull(d.takeRandom());
    }

}
