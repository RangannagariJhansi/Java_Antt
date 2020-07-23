package wizard.common.cards;

import wizard.common.game.Color;

public class WizardCard extends Card {

    private static final long serialVersionUID = 1L;

    public WizardCard() {
        this.value = 14;
        this.color = Color.CLEAR;
    }

    @Override
    public String toString() {
        return "[W   ]";
    }

    @Override
    public boolean isWizard() {
        return true;
    }
}
