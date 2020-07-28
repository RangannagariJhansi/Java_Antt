package wizard.client;

import java.util.List;
import java.util.Scanner;

import wizard.common.cards.Card;
import wizard.common.cards.Cards;
import wizard.common.communication.GameStatus;

public class Player {

    private List<Card> hand;
    private List<Card> trick;
    private GameStatus gameStatus;
    private String gameError;

    /**
     * Create new @{code Player} with empty hand.
     */
    public Player() {
        hand = null;
        trick = null;
        gameStatus = null;
        gameError = null;
    }

    private void printStatus() {
        // Clear the screen
        for (int i = 0; i < 10; i++) {
            System.out.print("\n\n\n\n\n\n\n\n\n\n");
        }

        // TODO: Print trump color
        System.out.printf("Hand:   %s\n", Cards.toString(hand));
        System.out.printf("Trick:  %s\n", Cards.toString(trick));
        System.out.printf("Status: %s\n", gameStatus);
        System.out.println();
        if (gameError != null) {
            System.out.printf("Game error: %s\n", gameError);
            System.out.println();
            gameError = null;
        }
    }

    public void updateGameStatus(final GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        printStatus();
    }

    /**
     * Update the hand of the player.
     * Old hand will be discarded.
     *
     * @param hand The new hand to apply to this player
     */
    public void updateHand(final List<Card> hand) {
        this.hand = hand;
        printStatus();
    }

    /**
     * Update the currently active trick.
     * The old trick will be completely replaced.
     *
     * @param trick The new trick or updated trick
     */
    public void updateTrick(final List<Card> trick) {
        this.trick = trick;
        printStatus();
    }

    public void showGameError(final String error) {
        this.gameError = error;
        printStatus();
    }

    /**
     * Asks the player for a prediction how many trick he will take this
     * round.
     * Will block until the player has chosen a prediction.
     *
     * @return The number of tricks the player predicted.
     */
    public int askPrediction() {
        System.out.print("What is your prediction? ");

        int input = -1;
        Scanner in = new Scanner(System.in);
        boolean done = false;
        while (!done) {
            input = in.nextInt();

            if (input < 0) {
                System.out.println("You cannot take less than 0 tricks!");
                continue;
            }

            done = true;
        }

        return input;
    }

    /**
     * Asks the player for a card to play.
     * Will block until player has chosen a card.
     *
     * @return The card the player wants to play
     */
    public Card askTrickCard() {
        System.out.println("Select card to play. Cards:");
        for (int i = 0; i < hand.size(); i++) {
            System.out.printf("(%d)  %s\n", i, hand.get(i));
        }

        int input = -1;
        Scanner in = new Scanner(System.in);
        boolean done = false;
        while (!done) {
            System.out.print("CardID: ");
            input = in.nextInt();

            if (input < 0) {
                System.out.println("Invalid CardID");
                continue;
            }

            if (input > hand.size() - 1) {
                System.out.println("Invalid CardID");
                continue;
            }

            // TODO: 'Farbe zugeben' enforcen!

            done = true;
        }

        return hand.get(input);
    }
}
