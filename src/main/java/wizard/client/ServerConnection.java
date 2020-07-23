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
     * Listen for messages from server
     */
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
     * Handle message received by server
     *
     * @param message The message which was received
     * @throws IOException
     */
    private void receive(final Message message) throws IOException {
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

    private void receiveGameError(final StringMessage message) {
        player.showGameError(message.getContent());
    }

    private void receiveGameStatus(final StringMessage message) {
        player.updateGameStatus(message.getContent());
    }

    private void receiveUpdateHand(final CardsMessage message) {
        player.updateHand(message.getContent());
    }

    private void receiveAskPrediction() {
        int prediction = player.askPrediction();
        answerPrediction(prediction);
    }

    private void receiveAskTrickStart() {
        Card card = player.askTrickStart();
        answerTrickStart(card);
    }

    private void receiveAskTrickCard() {
        Card card = player.askTrickCard();
        answerTrickCard(card);
    }

    private void send(final Message message) throws IOException {
        out.writeObject(message);
        out.flush();
        out.reset();
    }

    private void send(final MessageType type, final Card content) throws IOException {
        if (content == null) {
            System.err.println("Card is null - Sending as VoidMessage instead");
            send(new VoidMessage(type));
        } else {
            send(new CardMessage(type, content));
        }
    }

    private void send(final MessageType type, final Integer content) throws IOException {
        if (content == null) {
            System.err.println("Integer is null - Sending as VoidMessage instead");
            send(new VoidMessage(type));
        } else {
            send(new IntMessage(type, content));
        }
    }

    private void answerPrediction(int prediction) {
        try {
            send(MessageType.ANSWER_PREDICTION, prediction);
        } catch (IOException e) {
            System.err.printf("IOException - Could not send prediction '%d' to server !\n", prediction);
            e.printStackTrace();
        }
    }

    private void answerTrickStart(final Card card) {
        try {
            send(MessageType.ANSWER_TRICK_START, card);
        } catch (IOException e) {
            System.err.printf("IOException - Could not send trick start card '%s' to server!\n", card);
            e.printStackTrace();
        }
    }

    private void answerTrickCard(final Card card) {
        try {
            send(MessageType.ANSWER_TRICK_CARD, card);
        } catch (IOException e) {
            System.err.printf("IOException - Could not send trick card '%s' to server!\n", card);
            e.printStackTrace();
        }
    }
}
