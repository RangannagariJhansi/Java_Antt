package wizard.client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import wizard.cards.Card;
import wizard.cards.Cards;
import wizard.communication.CardMessage;
import wizard.communication.CardsMessage;
import wizard.communication.IntMessage;
import wizard.communication.Message;
import wizard.communication.MessageType;
import wizard.communication.StringMessage;
import wizard.communication.VoidMessage;

public class WizardClient implements Runnable {
	
	private static final int PORT = 2000;
	private Socket socket;
	private ObjectOutputStream out;
	
	private Card[] hand;
	
	public static void main(String[] args) {
		new Thread(new WizardClient()).start();
	}
	
	public WizardClient() {
		hand = null;
		
		// Connect to server
		try {
			socket = new Socket("localhost", PORT);
			out = new ObjectOutputStream(socket.getOutputStream());
			new Thread(this).start();
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
		try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()))) {
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
	private void receive(Message message) throws IOException {
		switch(message.getType()) {
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
	
	private void receiveGameError(StringMessage message) {
		System.out.printf("Game error: %s\n", message.getContent());
	}
	
	private void receiveGameStatus(StringMessage message) {
		System.out.printf("Game status: %s\n", message.getContent());
	}
	
	private void receiveUpdateHand(CardsMessage message) {
		hand = message.getContent();
		System.out.printf("Hand: %s\n", Cards.toString(hand));
	}
	
	private void receiveAskPrediction() throws IOException {
		int prediction = askPrediction();
		answerPrediction(prediction);
	}
	
	private void receiveAskTrickStart() throws IOException {
		Card card = askTrickStart();
		answerTrickStart(card);
	}
	
	private void receiveAskTrickCard() throws IOException {
		Card card = askTrickCard();
		answerTrickCard(card);
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
    		System.err.println("String is null - Sending as VoidMessage instead");
    		send(new VoidMessage(type));
    	} else {
    		send(new StringMessage(type, content));
    	}
    }
    
    private void send(MessageType type, Card content) throws IOException {
    	if (content == null) {
    		System.err.println("Card is null - Sending as VoidMessage instead");
    		send(new VoidMessage(type));
    	} else {
    		send(new CardMessage(type, content));
    	}
    }
    
    private void send(MessageType type, Integer content) throws IOException {
    	if (content == null) {
    		System.err.println("Integer is null - Sending as VoidMessage instead");
    		send(new VoidMessage(type));
    	} else {
    		send(new IntMessage(type, content));
    	}
    }
    
	private void answerPrediction(int prediction) throws IOException {
		send(MessageType.ANSWER_PREDICTION, prediction);
	}
	
	private void answerTrickStart(final Card card) throws IOException {
		send(MessageType.ANSWER_TRICK_START, card);
	}
	
	private void answerTrickCard(final Card card) throws IOException {
		send(MessageType.ANSWER_TRICK_CARD, card);
	}
	
	private int askPrediction() throws IOException {
		// TODO: Say trump color
		System.out.printf("Hand: %s\n", Cards.toString(hand));
		System.out.print("What is your prediction? ");
		
		int input = -1;
		Scanner in = new Scanner(System.in);
		boolean done = false;
		while (!done) {
			input = in.nextInt();
			
			if (input < 0) {
				System.out.println("You cannot take less than 0 tricks!");
				continue;
			}
			
			done = true;
		}
		
		return input;
	}
	
	private Card askTrick(boolean start) {
		if (start) {
			System.out.println("You have to start a trick. Cards:");
		} else {
			System.out.println("Select card to play to trick. Cards:");
		}
		for (int i = 0; i < hand.length; i++) {
			System.out.printf("(%d)  %s\n", i, hand[i]);
		}
		
		int input = -1;
		Scanner in = new Scanner(System.in);
		boolean done = false;
		while (!done) {
			System.out.print("CardID: ");
			input = in.nextInt();
			
			if (input < 0) {
				System.out.println("Invalid CardID");
				continue;
			}
			
			if (input > hand.length) {
				System.out.println("Invalid CardID");
				continue;
			}
			
			done = true;
		}
		
		return hand[input];
	}
	
	private Card askTrickStart() {
		return askTrick(true);
	}
	
	private Card askTrickCard() {
		// TODO: 'Farbe zugeben' enforcen!
		return askTrick(false);
	}
}
