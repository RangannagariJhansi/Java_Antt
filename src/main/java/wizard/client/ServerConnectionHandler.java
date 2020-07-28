package wizard.client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Arrays;

import wizard.common.cards.Card;
import wizard.common.communication.CardsMessage;
import wizard.common.communication.ConnectionHandler;
import wizard.common.communication.GameStatusMessage;
import wizard.common.communication.Message;
import wizard.common.communication.MessageType;
import wizard.common.communication.StringMessage;
import wizard.common.communication.VoidMessage;
import wizard.common.game.Hand;
import wizard.common.game.Trick;

public class ServerConnectionHandler extends ConnectionHandler {

    private final Player player;

    /**
     * Create new {@code ServerConnectionHandler} object with given connection.
     *
     * @param socket Socket handling the connection to the server
     */
    public ServerConnectionHandler(final Socket socket) {
        super(socket);

        this.player = new Player();
    }

    /**
     * Listen for messages from server. Messages will be handled by receive().
     * Will block indefinitely. Meant to be run in its own thread.
     */
    @Override
    public void run() {
        Thread.currentThread().setName("ServerConnection Thread");

        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(socket.getInputStream()))) {
            while (true) {
                Object object = in.readObject();
                if (object instanceof Message) {
                    receive((Message)object);
                } else {
                    System.err.println("Received malformed message (wrong object type)");
                }
            }
        } catch (IOException e) {
            System.err.println("IOException - Error when receiving message with readObject()!");
            e.printStackTrace();
            try {
                out.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("ClassNotFoundException - Error when receiving message with readObject()!");
            e.printStackTrace();
            try {
                out.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Handle message received from server.
     * Decide what type of message is received and call helper functions.
     *
     * @param message The message which was received
     */
    private void receive(final Message message) {
        switch (message.getType()) {
            case GAME_ERROR:
                if (!(message instanceof StringMessage)) {
                    System.err.println("Received message object from client is instance of unexpected class");
                    break;
                }
                receiveGameError(((StringMessage)message));
                break;
            case GAME_STATUS:
                if (!(message instanceof GameStatusMessage)) {
                    System.err.println("Received message object from client is instance of unexpected class");
                    break;
                }
                receiveGameStatus(((GameStatusMessage)message));
                break;
            case UPDATE_HAND:
                if (!(message instanceof CardsMessage)) {
                    System.err.println("Received message object from client is instance of unexpected class");
                    break;
                }
                receiveUpdateHand((CardsMessage)message);
                break;
            case UPDATE_TRICK:
                if (!(message instanceof CardsMessage)) {
                    System.err.println("Received message object from client is instance of unexpected class");
                    break;
                }
                receiveUpdateTrick((CardsMessage)message);
                break;
            case ASK_PREDICTION:
                if (!(message instanceof VoidMessage)) {
                    System.err.println("Received message object from client is instance of unexpected class");
                    break;
                }
                receiveAskPrediction();
                break;
            case ASK_TRICK_CARD:
                if (!(message instanceof VoidMessage)) {
                    System.err.println("Received message object from client is instance of unexpected class");
                    break;
                }
                receiveAskTrickCard();
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
    private void receiveGameError(final StringMessage message) {
        player.showGameError(message.getContent());
    }

    /**
     * Handle received game status message.
     *
     * @param message The message which was received
     */
    private void receiveGameStatus(final GameStatusMessage message) {
        player.updateGameStatus(message.getContent());
    }

    /**
     * Handle received update-hand message.
     *
     * @param message The message which was received
     */
    private void receiveUpdateHand(final CardsMessage message) {
        player.updateHand(new Hand(Arrays.asList(message.getContent())));
    }

    /**
     * Handle received update-trick message.
     *
     * @param message The message which was received
     */
    private void receiveUpdateTrick(final CardsMessage message) {
        player.updateTrick(new Trick(Arrays.asList(message.getContent())));
    }

    /**
     * Handle received ask-prediction message.
     */
    private void receiveAskPrediction() {
        int prediction = player.askPrediction();
        answerPrediction(prediction);
    }

    /**
     * Handle received ask-trick-card message.
     */
    private void receiveAskTrickCard() {
        Card card = player.askTrickCard();
        answerTrickCard(card);
    }

    /**
     * Sends a prediction-answer to the connected server.
     *
     * @param prediction The prediction to be sent to the server
     */
    private void answerPrediction(int prediction) {
        try {
            send(MessageType.ANSWER_PREDICTION, prediction);
        } catch (IOException e) {
            System.err.printf("IOException - Could not send prediction '%d' to server !\n", prediction);
            e.printStackTrace();
        }
    }

    /**
     * Sends a trick-card answer to the connected server.
     *
     * @param card The card to play
     */
    private void answerTrickCard(final Card card) {
        try {
            send(MessageType.ANSWER_TRICK_CARD, card);
        } catch (IOException e) {
            System.err.printf("IOException - Could not send trick card '%s' to server!\n", card);
            e.printStackTrace();
        }
    }
}

