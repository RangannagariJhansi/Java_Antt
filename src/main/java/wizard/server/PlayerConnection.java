package wizard.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import wizard.common.Settings;
import wizard.common.cards.Card;
import wizard.common.communication.CardMessage;
import wizard.common.communication.CardsMessage;
import wizard.common.communication.GameStatus;
import wizard.common.communication.GameStatusMessage;
import wizard.common.communication.IntMessage;
import wizard.common.communication.Message;
import wizard.common.communication.MessageType;
import wizard.common.communication.StringMessage;
import wizard.common.communication.VoidMessage;
import wizard.common.game.Hand;

public class PlayerConnection extends Thread {

    private final Socket client;
    private ObjectOutputStream out;

    private Object lastAnswerContent = null;
    private MessageType lastAnswerType = null;

    /**
     * Create a new {@code PlayerConnection} with given client socket.
     *
     * @param client The client socket of this {@code PlayerConnection}
     */
    public PlayerConnection(final Socket client) {
        this.client = client;
        try {
            out = new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));
        } catch (IOException e) {
            System.err.printf("IOException - Error when opening output stream to client '%s'!\n", this);
            e.printStackTrace();
            try {
                out.close();
            } catch (IOException e1) {
                System.err.printf("IOException - Error when trying to close output stream to client '%s'!\n", this);
                e1.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String str = client.getInetAddress().getHostAddress();
        str += ":";
        str += client.getPort();

        return str;
    }


    /**
     * Listen for messages from client. Messages will be handled by receive().
     * Will block indefinitely. Meant to be run in its own thread.
     */
    @Override
    public void run() {
        this.setName(String.format("PlayerConnection Thread '%s'", this));

        try (ObjectInputStream in = new ObjectInputStream(
            new BufferedInputStream(
                client.getInputStream()))) {

            while (true) {
                Object object = in.readObject();
                if (object instanceof Message) {
                    receive((Message)object);
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
     * Handle message received from client.
     * Decide what type of message is received and call helper functions.
     *
     * @param message The message which was received
     */
    private void receive(final Message message) {
        if (Settings.DEBUG_NETWORK_COMMUNICATION) {
            System.out.println("Received a message");
        }

        switch (message.getType()) {
            case ANSWER_PREDICTION:
                if (!(message instanceof IntMessage)) {
                    System.err.println("Received message object from client is instance of unexpected class");
                    break;
                }
                synchronized(this) {
                    lastAnswerType = message.getType();
                    lastAnswerContent = message.getContent();
                    notify();
                }
                break;
            case ANSWER_TRICK_CARD:
                if (!(message instanceof CardMessage)) {
                    System.err.println("Received message object from client is instance of unexpected class");
                    break;
                }
                synchronized(this) {
                    lastAnswerType = message.getType();
                    lastAnswerContent = message.getContent();
                    notify();
                }
                break;
            default:
                System.err.println("Received message from client has unknown type");
                break;
        }
    }

    /**
     * Send message to connected client.
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
     * Send void-message to connected client.
     *
     * @param type The type of message to send
     * @throws IOException If sending to server fails
     */
    private void send(final MessageType type) throws IOException {
        send(new VoidMessage(type));
    }

    /**
     * Send string-message to connected client.
     *
     * @param type The type of message to send
     * @param content The string content of the message to send
     * @throws IOException If sending to server fails
     */
    private void send(final MessageType type, final String content) throws IOException {
        if (content == null) {
            send(new VoidMessage(type));
        } else {
            send(new StringMessage(type, content));
        }
    }

    /**
     * Send cards-message to connected client.
     *
     * @param type The type of message to send
     * @param content The cards content of the message to send
     * @throws IOException If sending to server fails
     */
    private void send(final MessageType type, final List<Card> content) throws IOException {
        if (content == null) {
            send(new VoidMessage(type));
        } else {
            send(new CardsMessage(type, content.toArray(new Card[content.size()])));
        }
    }

    /**
     * Send game-status-message to connected client.
     *
     * @param type The type of message to send
     * @param content The game-status content of the message to send
     * @throws IOException If sending to server fails
     */
    private void send(final MessageType type, final GameStatus content) throws IOException {
        if (content == null) {
            send(new VoidMessage(type));
        } else {
            send(new GameStatusMessage(type, content));
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

        try {
            send(MessageType.ASK_PREDICTION);
        } catch (IOException e) {
            System.err.printf("IOException - Could not ask player '%s' for prediction!\n", this);
            e.printStackTrace();
        }

        synchronized(this) {
            while (lastAnswerType != MessageType.ANSWER_PREDICTION) {
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
        }

        int prediction = (Integer)(lastAnswerContent);
        lastAnswerContent = null;
        lastAnswerType = null;
        return prediction;
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

        try {
            send(MessageType.ASK_TRICK_CARD);
        } catch (IOException e) {
            System.err.printf("IOException - Could not ask player '%s' for trick card!\n", this);
            e.printStackTrace();
        }

        synchronized(this) {
            while (lastAnswerType != MessageType.ANSWER_TRICK_CARD) {
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
        }

        Card card = (Card)(lastAnswerContent);
        lastAnswerContent = null;
        lastAnswerType = null;
        return card;
    }
}
