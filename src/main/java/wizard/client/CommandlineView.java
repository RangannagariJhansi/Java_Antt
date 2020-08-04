package wizard.client;

import java.util.Scanner;

import wizard.common.GameStatus;
import wizard.common.cards.Card;
import wizard.common.game.Color;
import wizard.common.game.Hand;
import wizard.common.game.Trick;

/**
 * Class handling display of game status information to the user via a
 * simple command line interface.
 */
public class CommandlineView implements UserView {

    private Hand hand;
    private Card trumpCard;
    private Color trumpColor;
    private Trick trick;
    private GameStatus gameStatus;
    private String gameError;

    private final Scanner stdin;

    /**
     * Create new {@code CommandlineView} object.
     */
    public CommandlineView() {
        hand = null;
        trumpCard = null;
        trumpColor = null;
        trick = null;
        gameStatus = null;
        gameError = null;

        stdin = new Scanner(System.in);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh(
            final Hand hand,
            final Card trumpCard,
            final Color trumpColor,
            final Trick trick,
            final GameStatus gameStatus,
            final String gameError) {

        this.hand = hand;
        this.trumpCard = trumpCard;
        this.trumpColor = trumpColor;
        this.trick = trick;
        this.gameStatus = gameStatus;
        this.gameError = gameError;

        refresh();
    }

    private void refresh() {
        // Clear the screen
        for (int i = 0; i < 10; i++) {
            System.out.print("\n\n\n\n\n\n\n\n\n\n");
        }

        // Print hand
        System.out.printf("Hand:   %s\n", hand);

        // Print trump card (+ trump color if not obvious)
        if (trumpCard != null && trumpColor != null) {
            if (trumpCard.getColor() == Color.CLEAR) {
                if (trumpCard.isWizard()) {
                    // Trump card is a wizard so trump color has been chosen by player
                    System.out.printf("Trump: %s (%s)\n", trumpCard, trumpColor);
                } else {
                    // Trump card is a jester so there is no trump color
                    System.out.printf("Trump: %s (no color)\n", trumpCard);
                }

            } else {
                System.out.printf("Trump: %s\n", trumpCard);
            }
        }

        // Print trick
        System.out.printf("Trick:  %s\n", trick);

        // Print status
        System.out.printf("Status: %s\n", gameStatus);
        System.out.println();

        // Print error only one time if error is set
        if (gameError != null) {
            System.out.printf("Game error: %s\n", gameError);
            System.out.println();
            gameError = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int askPrediction() {
        System.out.print("What is your prediction? ");

        int input = -1;
        boolean done = false;
        while (!done) {
            input = stdin.nextInt();

            if (input < 0) {
                System.out.println("You cannot take less than 0 tricks!");
                continue;
            }

            done = true;
        }

        return input;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Card askTrickCard() {
        System.out.println("Select card to play. Cards:");
        for (int i = 0; i < hand.count(); i++) {
            System.out.printf("(%d)  %s\n", i, hand.get(i));
        }

        int input = -1;
        boolean done = false;
        while (!done) {
            System.out.print("CardID: ");
            input = stdin.nextInt();

            if (input < 0) {
                System.out.println("Invalid CardID");
                continue;
            }

            if (input > hand.count() - 1) {
                System.out.println("Invalid CardID");
                continue;
            }

            // TODO: 'Farbe zugeben' enforcen!

            done = true;
        }

        return hand.get(input);
    }
}
