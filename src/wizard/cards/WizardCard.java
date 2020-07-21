package wizard.cards;

import wizard.game.Color;

public class WizardCard extends Card {
	
	private static final long serialVersionUID = -5775325488906792881L;

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
