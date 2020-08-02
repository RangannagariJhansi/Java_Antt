package wizard.client;

import java.util.Scanner;

import wizard.common.cards.Card;
import wizard.common.communication.GameStatus;
import wizard.common.game.Hand;
import wizard.common.game.Trick;

/**
 * Class handling display of game status information to the user via a
 * simple command line interface.
 */
public class CommandlineView implements UserView {

    private Hand hand;
    private Trick trick;
    private GameStatus gameStatus;
    private String gameError;

    private final Scanner stdin;

    /**
     * Create new {@code CommandlineView} object.
     */
    public CommandlineView() {
        hand = null;
        trick = null;
        gameStatus = null;
        gameError = null;

        stdin = new Scanner(System.in);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh(final Hand hand, final Trick trick,
            final GameStatus gameStatus, final String gameError) {
        this.hand = hand;
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

        // TODO: Print trump color
        System.out.printf("Hand:   %s\n", hand);
        System.out.printf("Trick:  %s\n", trick);
        System.out.printf("Status: %s\n", gameStatus);
        System.out.println();
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
