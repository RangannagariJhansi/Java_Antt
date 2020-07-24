package wizard.client;

import java.util.Scanner;

import wizard.common.cards.Card;
import wizard.common.cards.Cards;

public class Player {

    private Card[] hand;

    /**
     * Create new @{code Player} with empty hand.
     */
    public Player() {
        hand = null;
    }

    public void updateGameStatus(final String status) {
        System.out.printf("Game status: %s\n", status);
    }

    public void showGameError(final String error) {
        System.out.printf("Game error: %s\n", error);
    }

    /**
     * Update the hand of the player.
     * Old hand will be discarded.
     *
     * @param hand The new hand to apply to this player
     */
    public void updateHand(final Card[] hand) {
        this.hand = hand;

        System.out.printf("Hand: %s\n", Cards.toString(hand));
    }

    /**
     * Asks the player for a prediction how many trick he will take this
     * round.
     * Will block until the player has chosen a prediction.
     *
     * @return The number of tricks the player predicted.
     */
    public int askPrediction() {
        // TODO: Say trump color
        System.out.printf("Hand: %s\n", Cards.toString(hand));
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
     * Helper function. Generalizes askTrickStart() and askTrickCard().
     * Will block until player has chosen a card.
     *
     * @param start Whether required card is the first card in a trick
     *        (askTrickStart()) or a trick already exists (askTrickCard()).
     *
     * @return The card the player wants to play
     */
    private Card askTrick(boolean start) {
        if (start) {
            System.out.println("You have to start a trick. Cards:");
        } else {
            System.out.println("Select card to play to trick. Cards:");
        }
        for (int i = 0; i < hand.length; i++) {
            System.out.printf("(%d)  %s\n", i, hand[i]);
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
            
            if (input > hand.length - 1) {
                System.out.println("Invalid CardID");
                continue;
            }

            done = true;
        }

        return hand[input];
    }

    /**
     * Asks the player for a card to start a new trick.
     * Will block until player has chosen a card.
     *
     * @return The card the player wants to play
     */
    public Card askTrickStart() {
        return askTrick(true);
    }

    /**
     * Asks the player for a card to play to existing trick.
     * Will block until player has chosen a card.
     *
     * @return The card the player wants to play
     */
    public Card askTrickCard() {
        // TODO: 'Farbe zugeben' enforcen!
        return askTrick(false);
    }
}
