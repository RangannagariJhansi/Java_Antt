package wizard.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wizard.cards.Card;
import wizard.game.Color;
import wizard.game.Deck;
import wizard.game.Trick;

public class Round {

	private final Deck deck;
	private final int round;
	private final List<Player> players;
	private final Card trumpCard;
	private final Color trumpColor;
	
	private int currentPlayer = 0;
	
	public Round(int round, final List<Player> players) {
		this.round = round;
		this.players = players;
		
		System.out.println(this);
		
		deck = new Deck();
    	
    	// Give out cards to players
		for (Player p : players) {
			List<Card> hand = new ArrayList<Card>(round);
			for (int i = 0; i < round; i++) {
    			hand.add(deck.takeRandom());
    		}
			p.giveHand(hand);
    	}
    	
		// Determine trump color for this round
    	trumpCard = deck.takeRandom();
    	trumpColor = trumpCard.getColor();
	}
	
	public String toString() {
		return "Round " +round;
	}
	
	private String status() {
		String status = "";
		
		if (trumpColor != Color.CLEAR) {
			status += trumpColor + " is trump.\n";
		}
		
    	for (Player p : players) {
    		status += p;
    	}
    	
    	return status;
	}
	
	private Player currentPlayer() {
		return players.get(currentPlayer);
	}
	
	private Player nextPlayer() {
		currentPlayer += 1;
		currentPlayer %= players.size();
		return currentPlayer();
	}
	
	public void play() {
		//Ask players for their predictions
		int predictionSum = 0;

		// Ask the first player for prediction
		predictionSum += currentPlayer().askPrediction(round);

		// Ask all but the last player for prediction
		for (int i = 0; i < players.size() - 2; i++) {
			predictionSum += nextPlayer().askPrediction(round);
		}

		// Predictions must not all come true so prediction of last player is restricted
		if (predictionSum <= round) {
			nextPlayer().askPrediction(round, round - predictionSum);    			
		} else {
			nextPlayer().askPrediction(round);
		}

		nextPlayer();
    	
    	System.out.println(status());
    	
    	// Play trick
    	try {
    		for (int trickId = 0; trickId < round; trickId++) {
    			Trick trick = new Trick(trumpColor);

    			// Ask all players for their cards
    			trick.add(currentPlayer().askTrickStart());
    			System.out.println();
    			for (int i = 0; i < players.size() - 1; i++) {
    				trick.add(nextPlayer().askTrickCard(trick));
    				System.out.println();
    			}
    			System.out.println(trick);
    			System.out.println();

    			// Determine who took the trick
    			Card winnerCard = trick.takenBy();
    			int winnerId = (currentPlayer + 1 + trick.cardId(winnerCard)) % players.size();
    			Player winner = players.get(winnerId);

    			System.out.printf("Trick gets taken by card %s by %s\n", winnerCard, winner.getName());

    			winner.giveTrick(trick);

    			// Player wo took the trick gets to start the next trick
    			currentPlayer = winnerId;
    		}
    	}
    	catch (IOException e) {
    		System.err.println("IOException - Could not play tricks!");
    		e.printStackTrace();
    	}
    	
    	
    	
    	
    	
    	
    	// End of Round
    	for (Player p : players) {
    		p.predictionsToScore();
    	}
    	
    	System.out.println("End of round\n");
	}
}
