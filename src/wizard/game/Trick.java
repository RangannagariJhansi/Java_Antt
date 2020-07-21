package wizard.game;

import java.util.ArrayList;
import java.util.List;

import wizard.cards.Card;

public class Trick {

	private final Color trump;
    private List<Card> cards;

    public Trick(Color trump) {
    	this.trump = trump;
        cards = new ArrayList<Card>();
    }
    
    public String toString() {
    	String str = "Trick: ";
    	
    	for (int i = 0; i < cards.size(); i++) {
    		str += cards.get(i);
    		
    		if (i < cards.size() - 1) {
    			str += " -> ";    			
    		}
    	}
    	
    	return str;
    }
    
    public void add(Card card) {
        cards.add(card);
    }
    
    public int cardId(Card card) {
    	return cards.indexOf(card);
    }

    public Card takenBy() {
    	// Trick without cards cannot be taken by anyone
    	if (cards.size() == 0) {
    		return null;
    		// TODO: Error
    	}
    	
    	// The trick gets taken by the first player to play a wizard
    	for (Card card : cards) {
    		if (card.isWizard()) {
    			return card;
    		}
    	}
    	
    	// The trick gets taken by the highest trump-colored card
    	{
    		Card winner = null;
    		
    		for (Card card : cards) {
    			if (card.getColor() != trump) {
    				continue;
    			}
    			
    			if (winner == null || card.getValue() > winner.getValue()) {
    				winner = card;
    			}
    		}
    		
    		if (winner != null) {
    			return winner;
    		}
    	}
    	
    	// The trick gets taken by the highest card colored the same as the first color.
    	// If there is never any color (all players play jesters) the trick is taken by the
    	// last player to play a Jester.
    	{
    		Color firstColor = Color.CLEAR;
    		Card winner = null;
    		
    		for (Card card : cards) {
    			Color color = card.getColor();
    			
    			// No color is set
    			if (firstColor == Color.CLEAR) {
    				firstColor = color;
    				winner = card;
    				continue;
    			}
    			
    			// Cards with color different from first color (and trump) don't win
    			if (card.getColor() != firstColor) {
    				continue;
    			}
    			
    			if (card.getValue() > winner.getValue()) {
    				winner = card;
    			}
    		}
    		
    		return winner;
    	}
    }

}
