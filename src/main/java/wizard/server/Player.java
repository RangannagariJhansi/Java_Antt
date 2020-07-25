package wizard.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wizard.common.cards.Card;
import wizard.common.game.Color;
import wizard.common.game.Trick;

public class Player {
    private final String name;
    private final PlayerConnection connection;

    private List<Card> hand;
    private int tricks;
    private int prediction;
    private int score;

    /**
     * Create a new {@code Player} with given name and connection object.
     *
     * @param name The name of this player
     * @param connection The connection object for this player
     */
    public Player(final String name, final PlayerConnection connection) {
        this.name = name;
        this.connection = connection;

        hand = new ArrayList<Card>();
        prediction = -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
            return String.format("%s", name);
        } else if (prediction == -1) {
            return String.format("%s: %s", name, handStr);
        } else {
            return String.format("%s: %s"
                    + " [Predicted: %2d]"
                    + " [Taken: %2d]"
                    + " [Score: %3d]",
                    name, handStr, prediction, tricks, score);
        }
    }

    /**
     * Add a trick which has been taken by this player to its trick count.
     *
     * @param trick The trick which has been taken by this player
     */
    public void giveTrick(final Trick trick) {
        tricks++;
    }

    /**
     * Returns the name of this player.
     *
     * @return The name of this player
     */
    public String getName() {
        return name;
    }

    /**
     * Give a hand of cards to this player.
     * The old hand will be overwritten.
     *
     * @param hand The hand to give to this player
     */
    public void giveHand(final List<Card> hand) {
        this.hand = hand;
        connection.updateHand(hand.toArray(new Card[hand.size()]));
    }

    /**
     * Checks whether this player has a given card on his hand.
     *
     * @param card The card to check for
     * @return True if the given card is on this players hand, false otherwise
     */
    public boolean hasCard(final Card card) {
        return hand.contains(card);
    }

    /**
     * Checks whether this player has any card of given color on his hand.
     *
     * @param color The color of cards to check for
     * @return True if the player has at least one card of the given color on his hand.
     */
    public boolean hasColor(final Color color) {
        return hand.parallelStream().anyMatch(x -> x.getColor() == color);
    }

    /**
     * Asks the player for a prediction of how many tricks he will take this
     * round.
     * Will block until the player has chosen a prediction.
     *
     * @param upperBorder The maximum of tricks the player is allowed to answer
     * @return The prediction the player made ranging from 0 to upperBorder
     *         (both included)
     */
    public int askPrediction(int upperBorder) {
        return askPrediction(upperBorder, -1);
    }

    /**
     * Asks the player for a prediction of how many tricks he will take this
     * round.
     * Will block until the player has chosen a prediction.
     *
     * @param upperBorder The maximum of tricks the player is allowed to answer
     * @param notAllowed Which value the player is not allowed to answer
     * @return The prediction the player made ranging from 0 to upperBorder
     *         (both included) excluding notAllowed
     */
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
                String error = String.format("You cannot take %d tricks in this round!\n", input);
                connection.sendGameError(error);
                continue;
            }

            if (input == notAllowed) {
                String error = String.format("You must not predict taking %d tricks", notAllowed);
                connection.sendGameError(error);
                continue;
            }

            done = true;
        }

        this.prediction = input;
        return input;
    }

    /**
     * Asks the player for a card to start a new trick.
     * Will block until player has chosen a card.
     *
     * @return The card the player wants to play
     */
    public Card askTrickStart() throws IOException {
        return askTrickCard(null);
    }

    /**
     * Asks the player for a card to play to existing trick.
     * Will block until player has chosen a card.
     *
     * @param trick The existing trick to play a card to
     * @return The card the player wants to play
     */
    public Card askTrickCard(final Trick trick) throws IOException {
        Card selectedCard = null;

        boolean done = false;
        while (!done) {
            // If there is no trick start a new one
            if (trick == null) {
                selectedCard = connection.askTrickStart();
            } else {
                selectedCard = connection.askTrickCard(trick);
            }

            if (!hasCard(selectedCard)) {
                connection.updateHand(hand.toArray(new Card[hand.size()]));
                connection.sendGameError(String.format(
                        "You have selected card '%s', which is not on your hand!\n",
                        selectedCard));
                continue;
            }

            //TODO: Farbe zugeben enforcen!!
            //if (hasColor(color))

            done = true;
        }

        // Remove selected card from hand
        hand.remove(selectedCard);
        connection.updateHand(hand.toArray(new Card[hand.size()]));

        return selectedCard;
    }

    /**
     * Convert tricks and predictions of this player to score.
     * Call this when round ends.
     */
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
