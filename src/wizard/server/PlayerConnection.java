package wizard.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import wizard.common.Settings;
import wizard.common.cards.Card;
import wizard.common.communication.CardMessage;
import wizard.common.communication.CardsMessage;
import wizard.common.communication.IntMessage;
import wizard.common.communication.Message;
import wizard.common.communication.MessageType;
import wizard.common.communication.StringMessage;
import wizard.common.communication.VoidMessage;
import wizard.common.game.Trick;

public class PlayerConnection extends Thread {

    private final Socket client;
    private ObjectOutputStream out;

    private Object lastAnswerContent = null;
    private MessageType lastAnswerType = null;

    public PlayerConnection(Socket client) {
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

    public String toString() {
        String str = client.getInetAddress().getHostAddress();
        str += ":";
        str += client.getPort();

        return str;
    }


    /**
     * Read messages from client.
     */
    public void run() {
        this.setName(String.format("PlayerConnection Thread '%s'", this));

        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(client.getInputStream()))) {
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

    private void receive(Message message) {
        if (Settings.DEBUG_NETWORK_COMMUNICATION) {
            System.out.println("Received a message");
        }

        switch(message.getType()) {
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
        case ANSWER_TRICK_START:
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

    private void send(Message message) throws IOException {
        out.writeObject(message);
        out.flush();
        out.reset();
    }

    private void send(MessageType type) throws IOException {
        send(new VoidMessage(type));
    }

    private void send(MessageType type, String content) throws IOException {
        if (content == null) {
            send(new VoidMessage(type));
        } else {
            send(new StringMessage(type, content));
        }
    }

    private void send(MessageType type, Card[] content) throws IOException {
        if (content == null) {
            send(new VoidMessage(type));
        } else {
            send(new CardsMessage(type, content));
        }
    }

    public void sendGameError(String message) {
        try {
            send(MessageType.GAME_ERROR, message);
        } catch (IOException e) {
            System.err.printf("IOException - Could not send game error to player '%s'!\n", this);
            e.printStackTrace();
        }
    }

    public void updateHand(final Card[] hand) {
        if (Settings.DEBUG_NETWORK_COMMUNICATION) {
            System.out.printf("Sending updated hand to player '%s'...\n", this);
        }

        try {
            send(MessageType.UPDATE_HAND, hand);
        } catch (IOException e) {
            System.err.printf("IOException - Could not update hand of player '%s'!\n", this);
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

    public Card askTrickStart() {
        if (Settings.DEBUG_NETWORK_COMMUNICATION) {
            System.out.printf("Asking client '%s' for trick start...\n", this);
        }

        try {
            send(MessageType.ASK_TRICK_START);
        } catch (IOException e) {
            System.err.printf("IOException - Could not ask player '%s' for trick start!\n", this);
            e.printStackTrace();
        }

        synchronized(this) {
            while (lastAnswerType != MessageType.ANSWER_TRICK_START) {
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

    public Card askTrickCard(final Trick trick) {
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
