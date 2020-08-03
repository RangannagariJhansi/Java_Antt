package wizard.client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import wizard.common.cards.Card;
import wizard.common.communication.ConnectionHandler;
import wizard.common.communication.Message;
import wizard.common.communication.MessageType;

/**
 * Class handling receiving of message from server on client side and putting
 * them into a buffer for consumption.
 * Also provides methods for sending messages to server.
 */
public class ServerConnectionHandler extends ConnectionHandler {

    /**
     * Create new {@code ServerConnectionHandler} object with given connection.
     *
     * @param socket Socket handling the connection to the server
     */
    public ServerConnectionHandler(final Socket socket) {
        super(socket);
    }

    /**
     * Listen for messages from server. Messages will be added to buffer.
     * Will block indefinitely. Meant to be run in its own thread.
     */
    @Override
    public void run() {
        Thread.currentThread().setName("Server connection thread");

        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(socket.getInputStream()))) {
            while (true) {
                // Receive object
                Object object = in.readObject();

                // Check if received object is of type message,
                // add object to buffer and notify waiting threads
                if (object instanceof Message) {
                    bufferPut((Message)object);
                    synchronized(this) {
                        notify();
                    }
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
     * Sends a prediction-answer to the connected server.
     *
     * @param prediction The prediction to be sent to the server
     */
    public void answerPrediction(int prediction) {
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
    public void answerTrickCard(final Card card) {
        try {
            send(MessageType.ANSWER_TRICK_CARD, card);
        } catch (IOException e) {
            System.err.printf("IOException - Could not send trick card '%s' to server!\n", card);
            e.printStackTrace();
        }
    }
}

