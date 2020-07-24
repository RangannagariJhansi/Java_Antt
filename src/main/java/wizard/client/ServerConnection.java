package wizard.client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import wizard.common.cards.Card;
import wizard.common.communication.CardMessage;
import wizard.common.communication.CardsMessage;
import wizard.common.communication.IntMessage;
import wizard.common.communication.Message;
import wizard.common.communication.MessageType;
import wizard.common.communication.StringMessage;
import wizard.common.communication.VoidMessage;

public class ServerConnection implements Runnable {

    private static final int PORT = 2000;

    private Socket socket;
    private ObjectOutputStream out;

    private final Player player;

    /**
     * Create new {@code ServerConnection} object.
     */
    public ServerConnection() {
        player = new Player();

        // Connect to server
        try {
            socket = new Socket("localhost", PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            try {
                socket.close();
                out.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
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
                if (!(message instanceof StringMessage)) {
                    System.err.println("Received message object from client is instance of unexpected class");
                    break;
                }
                receiveGameStatus(((StringMessage)message));
                break;
            case UPDATE_HAND:
                if (!(message instanceof CardsMessage)) {
                    System.err.println("Received message object from client is instance of unexpected class");
                    break;
                }
                receiveUpdateHand((CardsMessage)message);
                break;
            case ASK_PREDICTION:
                if (!(message instanceof VoidMessage)) {
                    System.err.println("Received message object from client is instance of unexpected class");
                    break;
                }
                receiveAskPrediction();
                break;
            case ASK_TRICK_START:
                if (!(message instanceof VoidMessage)) {
                    System.err.println("Received message object from client is instance of unexpected class");
                    break;
                }
                receiveAskTrickStart();
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
    private void receiveGameStatus(final StringMessage message) {
        player.updateGameStatus(message.getContent());
    }

    /**
     * Handle received update-hand message.
     *
     * @param message The message which was received
     */
    private void receiveUpdateHand(final CardsMessage message) {
        player.updateHand(message.getContent());
    }

    /**
     * Handle received ask-prediction message.
     */
    private void receiveAskPrediction() {
        int prediction = player.askPrediction();
        answerPrediction(prediction);
    }

    /**
     * Handle received ask-trick-start message.
     */
    private void receiveAskTrickStart() {
        Card card = player.askTrickStart();
        answerTrickStart(card);
    }

    /**
     * Handle received ask-trick-card message.
     */
    private void receiveAskTrickCard() {
        Card card = player.askTrickCard();
        answerTrickCard(card);
    }

    /**
     * Send message to connected server.
     *
     * @param message The message to send
     * @throws IOException If sending to server fails
     */
    private void send(final Message message) throws IOException {
        out.writeObject(message);
        out.flush();
        out.reset();
    }

    /**
     * Send card-message to connected server.
     *
     * @param type The type of message to send
     * @param content The card content of the message to send
     * @throws IOException If sending to server fails
     */
    private void send(final MessageType type, final Card content) throws IOException {
        if (content == null) {
            System.err.println("Card is null - Sending as VoidMessage instead");
            send(new VoidMessage(type));
        } else {
            send(new CardMessage(type, content));
        }
    }

    /**
     * Send integer-message to connected server.
     *
     * @param type The type of message to send
     * @param content The integer content of the message to send
     * @throws IOException If sending to server fails
     */
    private void send(final MessageType type, final Integer content) throws IOException {
        if (content == null) {
            System.err.println("Integer is null - Sending as VoidMessage instead");
            send(new VoidMessage(type));
        } else {
            send(new IntMessage(type, content));
        }
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
     * Sends a trick-start answer to the connected server.
     *
     * @param card The card to start the trick with
     */
    private void answerTrickStart(final Card card) {
        try {
            send(MessageType.ANSWER_TRICK_START, card);
        } catch (IOException e) {
            System.err.printf("IOException - Could not send trick start card '%s' to server!\n", card);
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
