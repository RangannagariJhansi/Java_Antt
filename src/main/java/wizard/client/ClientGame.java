package wizard.client;

import java.util.Arrays;

import wizard.common.cards.Card;
import wizard.common.communication.CardMessage;
import wizard.common.communication.CardsMessage;
import wizard.common.communication.ColorMessage;
import wizard.common.communication.GameStatus;
import wizard.common.communication.GameStatusMessage;
import wizard.common.communication.Message;
import wizard.common.communication.StringMessage;
import wizard.common.communication.VoidMessage;
import wizard.common.game.Color;
import wizard.common.game.Hand;
import wizard.common.game.Trick;

/**
 * Class handling the game flow on the client side.
 */
public class ClientGame implements Runnable {

    private final UserView view;

    private final ServerConnectionHandler connection;

    private Hand hand;
    private Card trumpCard;
    private Color trumpColor;
    private Trick trick;
    private GameStatus gameStatus;
    private String gameError;

    /**
     * Create new {@code ClientGame} object with given connection to server.
     *
     * @param connection The connection to server
     */
    public ClientGame(final ServerConnectionHandler connection) {
        this.view = new CommandlineView();
        this.connection = connection;

        hand = new Hand();
        trumpCard = null;
        trumpColor = null;
        trick = new Trick();
        gameStatus = GameStatus.UNKNOWN;
        gameError = null;
    }

    public void run() {
        Thread.currentThread().setName("Game logic thread");

        while (true) {
            // Wait for message from server
            synchronized(connection) {
                while (connection.bufferIsEmpty()) {
                    try {
                        connection.wait();
                    } catch (InterruptedException e) {
                        System.err.println("Thread interrupted!");
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }
            }

            // Receive message
            Message message = connection.bufferPop();
            handleMessage(message);
        }
    }

    /**
     * Handle message received from server.
     * Decide what type of message is received and call helper functions.
     *
     * @param message The message which was received
     */
    private void handleMessage(final Message message) {
        switch (message.getType()) {
            case GAME_ERROR:
                if (!(message instanceof StringMessage)) {
                    System.err.println("Received message object from client is instance of unexpected class");
                    break;
                }
                handleGameErrorMessage(((StringMessage)message));
                break;
            case GAME_STATUS:
                if (!(message instanceof GameStatusMessage)) {
                    System.err.println("Received message object from client is instance of unexpected class");
                    break;
                }
                handleGameStatusMessage(((GameStatusMessage)message));
                break;
            case UPDATE_HAND:
                if (!(message instanceof CardsMessage)) {
                    System.err.println("Received message object from client is instance of unexpected class");
                    break;
                }
                handleUpdateHandMessage((CardsMessage)message);
                break;
            case UPDATE_TRUMP_CARD:
                if (!(message instanceof CardMessage)) {
                    System.err.println("Received message object from client is instance of unexpected class");
                    break;
                }
                handleUpdateTrumpCardMessage((CardMessage)message);
                break;
            case UPDATE_TRUMP_COLOR:
                if (!(message instanceof ColorMessage)) {
                    System.err.println("Received message object from client is instance of unexpected class");
                    break;
                }
                handleUpdateTrumpColorMessage((ColorMessage)message);
                break;
            case UPDATE_TRICK:
                if (!(message instanceof CardsMessage)) {
                    System.err.println("Received message object from client is instance of unexpected class");
                    break;
                }
                handleUpdateTrickMessage((CardsMessage)message);
                break;
            case ASK_PREDICTION:
                if (!(message instanceof VoidMessage)) {
                    System.err.println("Received message object from client is instance of unexpected class");
                    break;
                }
                handleAskPredictionMessage();
                break;
            case ASK_TRICK_CARD:
                if (!(message instanceof VoidMessage)) {
                    System.err.println("Received message object from client is instance of unexpected class");
                    break;
                }
                handleAskTrickCardMessage();
                break;
            default:
                System.err.println("Received message from server has unknown type");
                break;
        }
    }

    /**
     * Handle received game error message.
     *
     * @param message The message which was received
     */
    private void handleGameErrorMessage(final StringMessage message) {
        showGameError(message.getContent());
    }

    /**
     * Handle received game status message.
     *
     * @param message The message which was received
     */
    private void handleGameStatusMessage(final GameStatusMessage message) {
        updateGameStatus(message.getContent());
    }

    /**
     * Handle received update-hand message.
     *
     * @param message The message which was received
     */
    private void handleUpdateHandMessage(final CardsMessage message) {
        updateHand(new Hand(Arrays.asList(message.getContent())));
    }

    /**
     * Handle received update-trump-card message.
     *
     * @param message The message which was received
     */
    private void handleUpdateTrumpCardMessage(final CardMessage message) {
        updateTrumpCard(message.getContent());
    }

    /**
     * Handle received update-trump-color message.
     *
     * @param message The message which was received
     */
    private void handleUpdateTrumpColorMessage(final ColorMessage message) {
        updateTrumpColor(message.getContent());
    }

    /**
     * Handle received update-trick message.
     *
     * @param message The message which was received
     */
    private void handleUpdateTrickMessage(final CardsMessage message) {
        updateTrick(new Trick(Arrays.asList(message.getContent())));
    }

    /**
     * Handle received ask-prediction message.
     */
    private void handleAskPredictionMessage() {
        int prediction = view.askPrediction();
        connection.answerPrediction(prediction);
    }

    /**
     * Handle received ask-trick-card message.
     */
    private void handleAskTrickCardMessage() {
        Card card = view.askTrickCard();
        connection.answerTrickCard(card);
    }

    /**
     * Refresh the information displayed to the user.
     */
    private void refreshView() {
        view.refresh(hand, trumpCard, trumpColor, trick, gameStatus, gameError);
    }

    /**
     * Update the current game status.
     *
     * @param gameStatus The new game status
     */
    private void updateGameStatus(final GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        refreshView();
    }

    /**
     * Update the hand of the player.
     * Old hand will be discarded.
     *
     * @param hand The new hand to apply to this player
     */
    private void updateHand(final Hand hand) {
        this.hand = hand;
        refreshView();
    }

    /**
     * Update the current trump card.
     *
     * @param trump The new trump card
     */
    private void updateTrumpCard(final Card trump) {
        this.trumpCard = trump;
        refreshView();
    }

    /**
     * Update the current trump color.
     *
     * @param trump The new trump color
     */
    private void updateTrumpColor(final Color trump) {
        this.trumpColor = trump;
        refreshView();
    }

    /**
     * Update the currently active trick.
     * The old trick will be completely replaced.
     *
     * @param trick The new trick or updated trick
     */
    private void updateTrick(final Trick trick) {
        this.trick = trick;
        refreshView();
    }

    private void showGameError(final String error) {
        this.gameError = error;
        refreshView();
    }
}
