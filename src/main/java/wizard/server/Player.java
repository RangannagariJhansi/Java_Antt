package wizard.server;

import java.util.List;

import wizard.common.GameStatus;
import wizard.common.cards.Card;
import wizard.common.game.Color;
import wizard.common.game.Hand;

public class Player {
    private final String name;
    private final ClientConnectionHandler connection;

    private Hand hand;

    /**
     * Create a new {@code Player} with given name and connection object.
     *
     * @param name The name of this player
     * @param connection The connection object for this player
     */
    public Player(final String name, final ClientConnectionHandler connection) {
        this.name = name;
        this.connection = connection;

        hand = new Hand();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s: %s", name, hand);
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
    public void giveHand(final Hand hand) {
        this.hand = hand;
        connection.updateHand(hand);
    }

    /**
     * Send current trump color (and corresponding card) to player.
     *
     * @param trumpCard The card dictating the current trump color
     * @param trumpColor The current trump color
     */
    public void updateTrump(final Card trumpCard, final Color trumpColor) {
        connection.updateTrump(trumpCard, trumpColor);
    }

    public void updateTrick(final List<Card> trick) {
        connection.updateTrick(trick);
    }

    public void updateGameStatus(final GameStatus gameStatus) {
        connection.updateGameStatus(gameStatus);
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
        return hand.containsColor(color);
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

        return input;
    }

    /**
     * Asks the player for a card to play.
     * Will block until player has chosen a card.
     *
     * @return The card the player wants to play
     */
    public Card askTrickCard() {
        Card selectedCard = null;

        boolean done = false;
        while (!done) {
            selectedCard = connection.askTrickCard();

            if (!hasCard(selectedCard)) {
                connection.updateHand(hand);
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
        connection.updateHand(hand);

        return selectedCard;
    }
}
