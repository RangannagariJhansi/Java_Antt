package wizard.server;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;

import wizard.common.Settings;
import wizard.common.cards.Card;
import wizard.common.communication.CardMessage;
import wizard.common.communication.ConnectionHandler;
import wizard.common.communication.GameStatus;
import wizard.common.communication.IntMessage;
import wizard.common.communication.Message;
import wizard.common.communication.MessageType;
import wizard.common.game.Hand;

/**
 * Class handling receiving of message from client on server side and putting
 * them into a buffer for consumption.
 * Also provides methods for sending messages to client.
 */
public class ClientConnectionHandler extends ConnectionHandler {

    /**
     * Create new {@code PlayerConnectionHandler} object with given connection.
     *
     * @param socket Socket handling the connection to the client
     */
    public ClientConnectionHandler(final Socket socket) {
        super(socket);
    }

    /**
     * Listen for messages from client. Messages will be handled by receive().
     * Will block indefinitely. Meant to be run in its own thread.
     */
    @Override
    public void run() {
        Thread.currentThread().setName(String.format("Player connection thread '%s'", this));

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
        } catch (EOFException e) {
            System.err.println("EOFException - Error when receiving message with readObject()!");
            e.printStackTrace();
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
     * Send update-hand message to connected client.
     *
     * @param hand The hand to update the client to
     */
    public void updateHand(final Hand hand) {
        if (Settings.DEBUG_NETWORK_COMMUNICATION) {
            System.out.printf("Sending updated hand to player '%s'...\n", this);
        }

        try {
            send(MessageType.UPDATE_HAND, hand.asList());
        } catch (IOException e) {
            System.err.printf("IOException - Could not update hand of player '%s'!\n", this);
            e.printStackTrace();
        }
    }

    public void updateTrick(final List<Card> trick) {
        if (Settings.DEBUG_NETWORK_COMMUNICATION) {
            System.out.printf("Sending updated trick to player '%s'...\n", this);
        }

        try {
            send(MessageType.UPDATE_TRICK, trick);
        } catch (IOException e) {
            System.err.printf("IOException - Could not send updated trick to player '%s'!\n", this);
            e.printStackTrace();
        }
    }

    public void updateGameStatus(final GameStatus gameStatus) {
        if (Settings.DEBUG_NETWORK_COMMUNICATION) {
            System.out.printf("Sending new game status zu player '%s'...\n", this);
        }

        try {
            send(MessageType.GAME_STATUS, gameStatus);
        } catch (IOException e) {
            System.err.printf("IOException - Could not send game status zu player '%s'!\n", this);
            e.printStackTrace();
        }
    }

    public int askPrediction(int upperBorder, int notAllowed) {
        if (Settings.DEBUG_NETWORK_COMMUNICATION) {
            System.out.printf("Asking client '%s' for prediction...\n", this);
        }

        // Ask client for prediction
        try {
            send(MessageType.ASK_PREDICTION);
        } catch (IOException e) {
            System.err.printf("IOException - Could not ask player '%s' for prediction!\n", this);
            e.printStackTrace();
        }

        while (true) {
            synchronized(this) {
                // Wait for message from client
                while (bufferIsEmpty()) {
                    if (Settings.DEBUG_NETWORK_COMMUNICATION) {
                        System.out.printf("Waiting for answer of client '%s'...\n", this);
                    }

                    try {
                        wait();
                    } catch (InterruptedException e) {
                        System.err.println("Thread interrupted!");
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }

                // Check if received message is expected message
                Message peek = buffer.peek();
                if (!(peek instanceof IntMessage)
                        || peek.getType() != MessageType.ANSWER_PREDICTION) {

                    // Received message is unexpected
                    // -> wake up other thread and go back to sleep
                    System.err.println("Received message object from client is instance of unexpected class");
                    notify();
                } else {
                    // Received message is expected message
                    // -> return prediction
                    IntMessage msg = (IntMessage)buffer.pop();
                    return msg.getContent();
                }
            }
        }
    }

    /**
     * Sends a message to client prompting player to select a card.
     * Will block until player has chosen a card.
     *
     * @return The card the player wants to play
     */
    public Card askTrickCard() {
        if (Settings.DEBUG_NETWORK_COMMUNICATION) {
            System.out.printf("Asking client '%s' for trick card...\n", this);
        }

        // Ask client for card
        try {
            send(MessageType.ASK_TRICK_CARD);
        } catch (IOException e) {
            System.err.printf("IOException - Could not ask player '%s' for trick card!\n", this);
            e.printStackTrace();
        }

        while (true) {
            synchronized(this) {
                // Wait for message from client
                while (bufferIsEmpty()) {
                    if (Settings.DEBUG_NETWORK_COMMUNICATION) {
                        System.out.printf("Waiting for answer of client '%s'...\n", this);
                    }

                    try {
                        wait();
                    } catch (InterruptedException e) {
                        System.err.println("Thread interrupted!");
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }

                // Check if received message is expected message
                Message peek = buffer.peek();
                if (!(peek instanceof CardMessage)
                        || peek.getType() != MessageType.ANSWER_TRICK_CARD) {

                    // Received message is unexpected
                    // -> wake up other thread and go back to sleep
                    System.err.println("Received message object from client is instance of unexpected class");
                    notify();
                } else {
                    // Received message is expected message
                    // -> return prediction
                    CardMessage msg = (CardMessage)buffer.pop();
                    return msg.getContent();
                }
            }
        }
    }

    /**
     * Send game error to connected client.
     *
     * @param message The game error message to send
     */
    public void sendGameError(final String message) {
        try {
            send(MessageType.GAME_ERROR, message);
        } catch (IOException e) {
            System.err.printf("IOException - Could not send game error to player '%s'!\n", this);
            e.printStackTrace();
        }
    }
}
