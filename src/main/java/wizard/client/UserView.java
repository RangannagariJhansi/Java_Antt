package wizard.client;

import wizard.common.GameStatus;
import wizard.common.cards.Card;
import wizard.common.game.Color;
import wizard.common.game.Hand;
import wizard.common.game.ScoreBoard;
import wizard.common.game.Trick;

/**
 * Class handling display of game status information to the user.
 */
public interface UserView {

    /**
     * Display the current game status information to the user.
     * Optionally update game status information.
     *
     * @param hand The current player hand
     * @param trumpCard The current trump card
     * @param trumpColor The current trump color
     * @param trick The current trick
     * @param scoreBoard The current scores
     * @param gameStatus The current game status
     * @param gameError If and what error exists
     */
    public void refresh(
            final Hand hand,
            final Card trumpCard,
            final Color trumpColor,
            final Trick trick,
            final ScoreBoard scoreBoard,
            final GameStatus gameStatus,
            final String gameError);

    /**
     * Asks the player for a prediction how many trick he will take this
     * round.
     * Will block until the player has chosen a prediction.
     *
     * @return The number of tricks the player predicted.
     */
    public int askPrediction();

    /**
     * Asks the player for a card to play.
     * Will block until player has chosen a card.
     *
     * @return The card the player wants to play
     */
    public Card askTrickCard();
}
