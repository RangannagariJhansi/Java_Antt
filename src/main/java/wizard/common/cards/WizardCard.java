package wizard.common.cards;

import wizard.common.game.Color;

public class WizardCard extends Card {

    private static final long serialVersionUID = 1L;

    /**
     * Create new wizard card.
     */
    public WizardCard() {
        super(14, Color.CLEAR);
    }

    @Override
    public String toString() {
        return "[W   ]";
    }

    /**
     * Returns true to indicate the card is a wizard.
     * @see Card#isWizard()
     *
     * @return True
     */
    @Override
    public boolean isWizard() {
        return true;
    }
}
