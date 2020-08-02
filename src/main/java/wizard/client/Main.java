package wizard.client;

import java.io.IOException;
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
        try {
            Socket socket = new Socket("localhost", PORT);
            ServerConnectionHandler con = new ServerConnectionHandler(socket);
            con.start();

            ClientGame game = new ClientGame(con);
            new Thread(game).start();

        } catch (IOException e) {
            System.err.println("IOException - Error when opening socket!");
            e.printStackTrace();
        }
    }
}
