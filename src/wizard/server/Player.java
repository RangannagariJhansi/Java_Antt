package wizard.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wizard.cards.Card;
import wizard.game.Trick;

public class Player {
	private final String name;
	private final PlayerConnection connection;
	
	private List<Card> hand;
	private int tricks;
	private int prediction; 
	private int score;
	
	public Player(final String name, PlayerConnection connection) {
		this.name = name;
		this.connection = connection;
		
		hand = new ArrayList<Card>();
		prediction = -1;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		String handStr = "[ ";
		for (int i = 0; i < hand.size(); i++) {
			handStr += hand.get(i);
			
			if (i < hand.size() - 1) {
				handStr += ", ";
			}
		}
		handStr += " ]";
		
		if (prediction == -1 && hand.size() == 0) {
			return String.format("%s\n", name);
		} else if (prediction == -1) {
			return String.format("%s: %s\n", name, handStr);
		} else {
			return String.format("%s: %s [Predicted: %2d] [Taken: %2d] [Score: %3d]\n", name, handStr, prediction, tricks, score);
		}
	}
	
	public void giveTrick(final Trick trick) {
		tricks++;
	}
	
	public void giveHand(final List<Card> hand) {
		connection.updateHand(hand.toArray(new Card[hand.size()]));
	}
	
	public boolean isOnHand(final Card card) {
		return hand.contains(card);
	}
	
	public int askPrediction(int upperBorder) {
		return askPrediction(upperBorder, -1);
	}
	
	public int askPrediction(int upperBorder, int notAllowed) {
		int input = -1;
		
		boolean done = false;
		while (!done) {
			input = connection.askPrediction(upperBorder, notAllowed);
			
			if (input < 0) {
				connection.sendGameError("You cannot take less than 0 tricks!");
				continue;
			}
			
			if (input > upperBorder) {
				connection.sendGameError(String.format("You cannot take %d tricks in this round!\n", input));
				continue;
			}
			
			if (input == notAllowed) {
				connection.sendGameError(String.format("You must not predict taking %d tricks", notAllowed));
				continue;
			}
			
			done = true;
		}
		
		this.prediction = input;
		return input;
		
		
		/**
		System.out.print(name + ", what is your prediction? ");
		
		int input = -1;
		Scanner in = new Scanner(System.in);
		boolean done = false;
		while (!done) {
			input = in.nextInt();
			
			if (input < 0) {
				System.out.println("You cannot take less than 0 tricks!");
				continue;
			}
			
			if (input > upperBorder) {
				System.out.printf("You cannot take %d tricks in this round!\n", input);
				continue;
			}
			
			if (input == notAllowed) {
				System.out.printf("You must not predict taking %d tricks", notAllowed);
				continue;
			}
			
			done = true;
		}
		
		this.prediction = input;
		return input;
		**/
	}
	
	public Card askTrickStart() throws IOException {
		return askTrickCard(null);
	}
	
	public Card askTrickCard(final Trick trick) throws IOException {
		int input = -1;
		
		boolean done = false;
		while (!done) {
			if (trick == null) {
				input = connection.askTrickStart();
			} else {
				input = connection.askTrickCard(trick);
			}
			
			//TODO: Farbe zugeben enforcen!!
			
			if (input > hand.size()) {
				System.out.println("Invalid Card ID. Please select a card");
				continue;
			}
			
			if (input < 1) {
				System.out.println("Invalid Card ID. Please select a card");
				continue;
			}
			
			done = true;
		}
		
		return hand.get(input);
		
		/**
		// If there is no trick start a new one
		if (trick == null) {
			System.out.println(name + ", you may start a trick. Cards:");
			connection.write(MessageType.ASK_TRICK_START +"\n");
		} else {
			System.out.println(trick);
			System.out.println(name + ", Cards:");
			connection.write(MessageType.ASK_TRICK_CARD +"\n");
		}
		
		for (int i = 1; i < hand.size() + 1; i++) {
			System.out.printf(" (%d) %s\n", i, hand.get(i - 1));
		}
		System.out.print("Select card: ");
		
		int input;
		Scanner in = new Scanner(System.in);
		boolean done = false;
		while (!done) {
			input = in.nextInt();
			
			//TODO: Farbe zugeben enforcen!!
			
			if (input > hand.size()) {
				System.out.println("Invalid Card ID. Please select a card");
				continue;
			}
			
			if (input < 1) {
				System.out.println("Invalid Card ID. Please select a card");
				continue;
			}
			
			done = true;
			
			return hand.get(input - 1);
		}
		
		// Should never happen
		return null;
		**/
	}
	
	public void predictionsToScore() {
		if (tricks == prediction) {
			// Correct predictions score 20 points
			// plus 10 points for every taken trick.
			
			System.out.printf("%s predicted correctly and earned 20 + %2d points\n", name, 10 * tricks);
			
			score += 20;
			score += (10 * tricks);
		} else {
			// Every wrong prediction awards -10 points.
			
			System.out.printf("%s made a false prediction and lost %2d points\n", name, 10 * Math.abs(tricks - prediction));
			
			score -= 10 * Math.abs(tricks - prediction);
		}
		
		// Rest counters for next round
		tricks = 0;
		prediction = -1;
	}
}
