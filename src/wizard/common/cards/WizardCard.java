package wizard.common.cards;

import wizard.common.game.Color;

public class WizardCard extends Card {

    private static final long serialVersionUID = 1L;

    public WizardCard() {
        this.value = 14;
        this.color = Color.CLEAR;
    }

    public boolean isWizard() {
        return true;
    }

    public String toString() {
        return "[W   ]";
    }
}
