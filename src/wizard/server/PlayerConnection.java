package wizard.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import wizard.cards.Card;
import wizard.communication.CardMessage;
import wizard.communication.CardsMessage;
import wizard.communication.IntMessage;
import wizard.communication.Message;
import wizard.communication.MessageType;
import wizard.communication.StringMessage;
import wizard.communication.VoidMessage;
import wizard.game.Trick;

public class PlayerConnection extends Thread {

    private final Socket client;
    private ObjectOutputStream out;
    
    private Object lastAnswerContent = null;
    private MessageType lastAnswerType = null;
	
    public PlayerConnection(Socket client) {
        this.client = client;
        try {
            out = new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));
            this.start();
        } catch (IOException e) {
            try {
            	out.close();
            } catch (IOException e1) {
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
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(client.getInputStream()))) {
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
    
    private void receive(Message message) {
		System.out.println("Received a message");
		
		switch(message.getType()) {
		case ANSWER_PREDICTION:
			if (!(message instanceof IntMessage)) {
				System.err.println("Received message object from client is instance of unexpected class");
				break;
			}
			lastAnswerType = message.getType();
			lastAnswerContent = message.getContent();
			synchronized(this) {
				notify();
			}
			break;
		case ANSWER_TRICK_START:
		case ANSWER_TRICK_CARD:
			if (!(message instanceof CardMessage)) {
				System.err.println("Received message object from client is instance of unexpected class");
				break;
			}
			lastAnswerType = message.getType();
			lastAnswerContent = message.getContent();
			synchronized(this) {
				notify();
			}
			break;
		default:
			System.err.println("Received message from client has unknown type");
			break;
		}

		/**String[] message = str.split("\n");
		
		if (message.length < 2) {
			System.err.println("Received message is malformed!");
			return;
		}
		
		String messageType = message[0];
		String[] messageContent = Arrays.copyOfRange(message, 1, message.length);
		
		if (messageType.equals(MessageType.ANSWER_PREDICTION.toString())) {
			lastAnswerType = MessageType.ANSWER_PREDICTION;
			lastAnswerContent = messageContent;
			synchronized(this) {
				notify();
			}
		} else if (messageType.equals(MessageType.ANSWER_TRICK_START.toString())) {
			lastAnswerType = MessageType.ANSWER_TRICK_START;
			lastAnswerContent = messageContent;
			synchronized(this) {
				notify();
			}
		} else if (messageType.equals(MessageType.ANSWER_TRICK_CARD.toString())) {
			lastAnswerType = MessageType.ANSWER_TRICK_CARD;
			lastAnswerContent = messageContent;
			synchronized(this) {
				notify();
			}
		}**/
    }
    
    private void send(Message message) throws IOException {
    	out.writeObject(message);
    	out.flush();
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
    	try {
    		send(MessageType.UPDATE_HAND, hand);
    	} catch (IOException e) {
    		System.err.printf("IOException - Could not update hand of player '%s'!\n", this);
    		e.printStackTrace();
    	}
    }
    
    public int askPrediction(int upperBorder) {
    	return askPrediction(upperBorder, -1);
    }
    
    public int askPrediction(int upperBorder, int notAllowed) {
    	System.out.println("Asking client for prediction...");
    	try {
    		send(MessageType.ASK_PREDICTION);
    	} catch (IOException e) {
    		System.err.printf("IOException - Could not ask player '%s' for prediction!\n", this);
    		e.printStackTrace();
    	}
    	
    	while (lastAnswerType != MessageType.ANSWER_PREDICTION) {
    		try {
    			System.out.println("Waiting for client answer...");
    			synchronized(this) {
    				wait();
    			}
    		} catch (InterruptedException e) {
    			System.err.println("Thread interrupted!");
    			e.printStackTrace();
    			Thread.currentThread().interrupt();
    		}
    	}
    	
    	int prediction = (Integer)(lastAnswerContent);
    	lastAnswerContent = null;
    	lastAnswerType = null;
    	return prediction;
    }
    
	public int askTrickStart() {
		System.out.println("Asking client for trick start...");
		try {
			send(MessageType.ASK_TRICK_START);
		} catch (IOException e) {
			System.err.printf("IOException - Could not ask player '%s' for trick start!\n", this);
			e.printStackTrace();
		}
		
    	while (lastAnswerType != MessageType.ANSWER_TRICK_START) {
    		try {
    			System.out.println("Waiting for client answer...");
    			synchronized(this) {
    				wait();
    			}
    		} catch (InterruptedException e) {
    			System.err.println("Thread interrupted!");
    			e.printStackTrace();
    			Thread.currentThread().interrupt();
    		}
    	}
    	
    	int cardId = (Integer)(lastAnswerContent);
    	lastAnswerContent = null;
    	lastAnswerType = null;
    	return cardId;
	}
	
	public int askTrickCard(final Trick trick) {
		System.out.println("Asking client for trick card...");
		try {
			send(MessageType.ASK_TRICK_CARD);
		} catch (IOException e) {
			System.err.printf("IOException - Could not ask player '%s' for trick card!\n", this);
			e.printStackTrace();
		}
		
    	while (lastAnswerType != MessageType.ANSWER_TRICK_CARD) {
    		try {
    			System.out.println("Waiting for client answer...");
    			synchronized(this) {
    				wait();
    			}
    		} catch (InterruptedException e) {
    			System.err.println("Thread interrupted!");
    			e.printStackTrace();
    			Thread.currentThread().interrupt();
    		}
    	}
    	
    	int cardId = (Integer)(lastAnswerContent);
    	lastAnswerContent = null;
    	lastAnswerType = null;
    	return cardId;
	}
}
