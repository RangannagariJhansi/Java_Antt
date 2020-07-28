package wizard.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Main {

    private static final int PORT = 2000;

    /**
     * Main function for game client.
     *
     * @param args Command line arguments for application
     */
    public static void main(String[] args) {
        Thread.currentThread().setName("Main Thread");

        // Connect to server
        Socket socket;
        try {
            socket = new Socket("localhost", PORT);
            new ServerConnectionHandler(socket).start();
        } catch (IOException e) {
            System.err.println("IOException - Error when opening socket!");
            e.printStackTrace();
        }
    }
}
