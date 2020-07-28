package wizard.common.communication;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import wizard.common.cards.Card;

public abstract class ConnectionHandler extends Thread {

    protected final Socket socket;
    protected ObjectOutputStream out;

    /**
     * Create new {@code ConnectionHandler} object with given connection.
     *
     * @param socket Socket handling the connection
     */
    public ConnectionHandler(final Socket socket) {
        this.socket = socket;

        try {
            out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
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
        String str = socket.getInetAddress().getHostAddress();
        str += ":";
        str += socket.getPort();

        return str;
    }

    /**
     * Send message to connection.
     *
     * @param message The message to send
     * @throws IOException If sending to connection fails
     */
    protected void send(final Message message) throws IOException {
        out.writeObject(message);
        out.flush();
        out.reset();
    }

    /**
     * Send void-message to connection.
     *
     * @param type The type of message to send
     * @throws IOException If sending to connection fails
     */
    protected void send(final MessageType type) throws IOException {
        send(new VoidMessage(type));
    }

    /**
     * Send string-message to connection.
     *
     * @param type The type of message to send
     * @param content The string content of the message to send
     * @throws IOException If sending to connection fails
     */
    protected void send(final MessageType type, final String content) throws IOException {
        if (content == null) {
            send(new VoidMessage(type));
        } else {
            send(new StringMessage(type, content));
        }
    }

    /**
     * Send cards-message to connection.
     *
     * @param type The type of message to send
     * @param content The cards content of the message to send
     * @throws IOException If sending to connection fails
     */
    protected void send(final MessageType type, final List<Card> content) throws IOException {
        if (content == null) {
            send(new VoidMessage(type));
        } else {
            send(new CardsMessage(type, content.toArray(new Card[content.size()])));
        }
    }

    /**
     * Send game-status-message to connection.
     *
     * @param type The type of message to send
     * @param content The game-status content of the message to send
     * @throws IOException If sending to connection fails
     */
    protected void send(final MessageType type, final GameStatus content) throws IOException {
        if (content == null) {
            send(new VoidMessage(type));
        } else {
            send(new GameStatusMessage(type, content));
        }
    }

    /**
     * Send card-message to connection.
     *
     * @param type The type of message to send
     * @param content The card content of the message to send
     * @throws IOException If sending to connection fails
     */
    protected void send(final MessageType type, final Card content) throws IOException {
        if (content == null) {
            System.err.println("Card is null - Sending as VoidMessage instead");
            send(new VoidMessage(type));
        } else {
            send(new CardMessage(type, content));
        }
    }

    /**
     * Send integer-message to connection.
     *
     * @param type The type of message to send
     * @param content The integer content of the message to send
     * @throws IOException If sending to connection fails
     */
    protected void send(final MessageType type, final Integer content) throws IOException {
        if (content == null) {
            System.err.println("Integer is null - Sending as VoidMessage instead");
            send(new VoidMessage(type));
        } else {
            send(new IntMessage(type, content));
        }
    }
}
